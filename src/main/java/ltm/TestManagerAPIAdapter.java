package ltm;

import io.cucumber.gherkin.GherkinParser;
import io.cucumber.messages.types.Envelope;
import io.cucumber.messages.types.GherkinDocument;
import io.cucumber.plugin.ConcurrentEventListener;
import io.cucumber.plugin.event.EventPublisher;
import io.cucumber.plugin.event.Status;
import io.cucumber.plugin.event.TestCaseEvent;
import io.cucumber.plugin.event.TestCaseFinished;
import io.cucumber.plugin.event.TestSourceRead;
import io.cucumber.plugin.event.TestStepFinished;

import ltm.screenshots.SSConfig;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public abstract class TestManagerAPIAdapter implements ConcurrentEventListener {
    private final List<GherkinDocument> testSources = new LinkedList<>();
    private final ThreadLocal<String> currentFeatureFile = new ThreadLocal<>();
    //private static final ThreadLocal<List<StepDTO>> steps = new ThreadLocal<>();

    //private static final RunDTO runResponseDTO;
    private static final SSConfig screenshotConfig;

    static {
        screenshotConfig = SSConfig.load();
        //runResponseDTO = TestManagerAPIClient.createRun();
    }

    @Override
    public void setEventPublisher(EventPublisher eventPublisher) {
        eventPublisher.registerHandlerFor(TestSourceRead.class, this::readTestSource);
        //eventPublisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
        //eventPublisher.registerHandlerFor(TestCaseStarted.class, this::handleTestCaseStarted);
        //eventPublisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
    }

    protected void readTestSource(TestSourceRead event) {
        try {
            Iterator<Envelope> envelopesIterator = GherkinParser.builder()
                    .build()
                    .parse(Paths.get(event.getUri()))
                    .iterator();

            saveTestSourceIntoMap(envelopesIterator);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void saveTestSourceIntoMap(Iterator<Envelope> envelopeIterator) {
        while (envelopeIterator.hasNext()) {
            Envelope envelope = envelopeIterator.next();
            Optional<GherkinDocument> optionalGherkinDocument = envelope.getGherkinDocument();

            optionalGherkinDocument.ifPresent(testSources::add);
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


    /* private synchronized void handleTestStepFinished(TestStepFinished event) {
        addFinishedStep(event);
    }

    private synchronized void handleTestCaseStarted(TestCaseStarted event) {
        this.handleStartOfFeature(event);
    }

    private synchronized void handleTestCaseFinished(TestCaseFinished event) {
        this.handleEndOfFeature(event);
    } */

    /*   private void cleanSteps() {
           if (steps.get() != null) {
               steps.remove();
           }
       }

       private synchronized void handleStartOfFeature(TestCaseStarted testCase) {
           String uri = currentFeatureFile.get();
           if(uri == null || !uri.equals(testCase.getTestCase().getUri())) {
               currentFeatureFile.set(testCase.getTestCase().getUri());
           }
       }

       private synchronized void handleEndOfFeature(TestCaseFinished testCase) {
           createFeature(testCase);
           cleanSteps();
       }

       protected void createFeature(TestCaseFinished testCase) {
           Feature feature = testSources.getFeature(testCase.getTestCase().getUri());
           if (feature != null) {
               TestDTO test = createTestDTO(testCase, feature.getName());
               TestManagerAPIClient.createTest(test);
           }
       }

       private TestDTO createTestDTO(TestCaseFinished testCase, String featureName) {
           String title        = testCase.getTestCase().getName();
           String runId        = runResponseDTO.getId();
           String status       = getStatusAsString(testCase);
           String type         = "SCENARIO";
           List<String> tags   = testCase.getTestCase().getTags().stream().map(PickleTag::getName).collect(Collectors.toList());

           return new TestDTO(title, runId, status, featureName, type, tags, steps.get());
       }

       private String getStatusAsString(TestCaseEvent test) {
           Result.Type status = Result.Type.PENDING;
           if (test instanceof TestStepFinished) {
               status = ((TestStepFinished) test).result.getStatus();
           } else if (test instanceof TestCaseFinished) {
               status = ((TestCaseFinished) test).result.getStatus();
           }

           return status.toString().toUpperCase().substring(0, status.toString().length() - 2);
       }

       protected synchronized void addFinishedStep(TestStepFinished event) {
           if (steps.get() == null) {
               steps.set(new LinkedList<>());
           }

           if (event.testStep instanceof PickleStepTestStep) {
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

                   stackTrace = truncate(event.result.getErrorMessage(), 5);
               }

               steps.get().add(new StepDTO(getStepText(event), stackTrace, base64Image, status));
           }

       }

       protected synchronized String getStepText(TestStepFinished event) {
           String stepText = null;
           PickleStepTestStep pickle = ((PickleStepTestStep) event.testStep);
           AstNode astNode = testSources.getAstNode(currentFeatureFile.get(), pickle.getStepLine());
           if (astNode != null) {
               Step step = (Step) astNode.node;
               stepText = step.getKeyword() + pickle.getStepText();
           }

           return stepText;
       }

       public static synchronized String truncate(String str, int length) {
           if (str.length() <= length) {
               return str.substring(0, length);
           }

           return str;
       }
   */
    public abstract String getBase64Image();

}