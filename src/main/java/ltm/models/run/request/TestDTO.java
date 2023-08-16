package ltm.models.run.request;

import java.util.LinkedList;
import java.util.List;

public final class TestDTO {
    public TestDTO() {
    }

    public TestDTO(String title, String runId, String status, String feature, String type, List<String> tags, List<StepDTO> steps) {
        this.title = title;
        this.runId = runId;
        this.status = status;
        this.feature = feature;
        this.type = type;
        this.tags = tags;
        this.steps = steps;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setRunId(String runId) {
        this.runId = runId;
    }

    public String getRunId() {
        return runId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setFeature(String feature) {
        this.feature = feature;
    }

    public String getFeature() {
        return feature;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void addTag(String tag) {
        if (tags == null) {
            tags = new LinkedList<>();
        }

        tags.add(tag);
    }

    public List<String> getTags() {
        return tags;
    }

    public void addStep(StepDTO step) {
        if (steps == null) {
            steps = new LinkedList<>();
        }

        steps.add(step);
    }

    public List<StepDTO> getSteps() {
        return steps;
    }

    private String title;
    private String runId;
    private String status;
    private String feature;
    private String type;
    private List<String> tags;
    private List<StepDTO> steps;
}
