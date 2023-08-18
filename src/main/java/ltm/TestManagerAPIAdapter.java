package ltm;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.Feature;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.DataTableArgument;
import io.cucumber.plugin.event.DocStringArgument;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.PickleStepTestStep;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.Step;
import io.cucumber.plugin.event.StepArgument;
import io.cucumber.plugin.event.TestCaseEvent;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestCaseStarted;
import io.cucumber.plugin.event.TestSourceRead;
import io.cucumber.plugin.event.TestStepFinished;

import ltm.models.run.request.StepDTO;
import ltm.models.run.request.TestDTO;
import ltm.models.run.response.RunDTO;
import ltm.screenshots.SSConfig;
import ltm.screenshots.Strategy;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TestManagerAPIAdapter implements ConcurrentEventListener {
    private final Map<URI, GherkinDocument> testSources = new ConcurrentHashMap<>();
    private final ThreadLocal<URI> currentFeatureFile = new ThreadLocal<>();
    private static final ThreadLocal<List<StepDTO>> steps = new ThreadLocal<>();
    private static final RunDTO runResponseDTO;
    private static final SSConfig screenshotConfig;

    static {
        screenshotConfig = SSConfig.load();
        runResponseDTO = TestManagerAPIClient.createRun();
    }

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestSourceRead.class, this::handleTestSourceRead);
        eventPublisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
        eventPublisher.registerHandlerFor(TestCaseStarted.class, this::handleTestCaseStarted);
        eventPublisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
    }

    protected void handleTestSourceRead(TestSourceRead event) {
        try {
            Iterator<Envelope> envelopesIterator = GherkinParser.builder()
                    .build()
                    .parse(Paths.get(event.getUri()))
                    .iterator();

            saveTestSourceIntoMap(envelopesIterator, event.getUri());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveTestSourceIntoMap(Iterator<Envelope> envelopeIterator, URI featurePath) {
        while (envelopeIterator.hasNext()) {
            Envelope envelope = envelopeIterator.next();
            Optional<GherkinDocument> optionalGherkinDocument = envelope.getGherkinDocument();

            optionalGherkinDocument.ifPresent(gherkin -> testSources.put(featurePath, gherkin));
        }
    }

    private String getStatusAsString(TestCaseEvent test) {
        Status status = Status.PENDING;
        if (test instanceof TestStepFinished) {
            status = ((TestStepFinished) test).getResult().getStatus();
        } else if (test instanceof TestCaseFinished) {
            status = ((TestCaseFinished) test).getResult().getStatus();
        }

        return status.toString().toUpperCase().substring(0, status.toString().length() - 2);
    }



    private synchronized void handleTestCaseStarted(TestCaseStarted event) {
        this.handleStartOfFeature(event);
    }

    private synchronized void handleStartOfFeature(TestCaseStarted testCase) {
        URI uri = currentFeatureFile.get();
        if(uri == null || !uri.equals(testCase.getTestCase().getUri())) {
            currentFeatureFile.set(testCase.getTestCase().getUri());
        }
    }

    private synchronized void handleTestStepFinished(TestStepFinished event) {
        addFinishedStep(event);
    }

    protected synchronized void addFinishedStep(TestStepFinished event) {
        if (steps.get() == null) {
            steps.set(new LinkedList<>());
        }

        if (event.getTestStep() instanceof PickleStepTestStep) {
            String base64Image = null;
            String stackTrace = null;
            String status = getStatusAsString(event);

            if (screenshotConfig.contains(Strategy.ON_EACH_STEP)) {
                base64Image = getBase64Image();
            }

            if (status.equalsIgnoreCase("FAIL")) {
                if (screenshotConfig.contains(Strategy.ON_FAILURE)) {
                    base64Image = getBase64Image();
                }

                stackTrace = truncate(event.getResult().getError().getMessage(), 5);
            }

            steps.get().add(new StepDTO(getStepText(event), stackTrace, base64Image, status));
        }
    }

    protected synchronized String getStepText(TestStepFinished event) {
        PickleStepTestStep pickle = ((PickleStepTestStep) event.getTestStep());
        Step step = pickle.getStep();

        String text = step.getText();
        StepArgument argument = step.getArgument();

        if (argument instanceof DataTableArgument) {
            StringBuilder dtString = new DataTableFormatter
                    ((DataTableArgument) argument).generateTabularFormat();

            // TODO Auto-generated
        } else if (argument instanceof DocStringArgument) {
            String content = ((DocStringArgument) argument).getContent();
            String mediaType = ((DocStringArgument) argument).getMediaType();

            // TODO Auto-generated
        }

        return step.getKeyword().concat(text);
    }

    private synchronized void handleTestCaseFinished(TestCaseFinished event) {
        this.handleEndOfFeature(event);
    }

    private synchronized void handleEndOfFeature(TestCaseFinished testCase) {
        createFeature(testCase);
        cleanSteps();
    }

    protected void createFeature(TestCaseFinished testCase) {
        GherkinDocument gherkin = testSources.get(testCase.getTestCase().getUri());
        Optional<Feature> optionalFeature = gherkin.getFeature();

        optionalFeature.ifPresent(feature -> {
            TestDTO test = createTestDTO(testCase, feature.getName());
            TestManagerAPIClient.createTest(test);
        });
    }

    private void cleanSteps() {
        if (steps.get() != null) {
            steps.remove();
        }
    }

    private TestDTO createTestDTO(TestCaseFinished testCase, String featureName) {
        String title        = testCase.getTestCase().getName();
        String runId        = runResponseDTO.getId();
        String status       = getStatusAsString(testCase);
        String type         = "SCENARIO";
        List<String> tags   = new ArrayList<>(testCase.getTestCase().getTags());

        return new TestDTO(title, runId, status, featureName, type, tags, steps.get());
    }

    public static synchronized String truncate(String str, int length) {
        if (str.length() <= length) {
            return str.substring(0, length);
        }

        return str;
    }

    public abstract String getBase64Image();
}