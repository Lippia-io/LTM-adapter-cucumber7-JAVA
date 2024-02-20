package ltm.models.run.request;

public final class RunDTO {
    public RunDTO() {
    }

    public RunDTO(String runName, String projectCode, String repositoryUrl, String branchName) {
        this.runName = runName;
        this.projectCode = projectCode;
        this.repositoryUrl = repositoryUrl;
        this.branchName = branchName;
    }

    public void setRunName(String runName) {
        this.runName = runName;
    }

    public String getRunName() {
        return runName;
    }

    public void setProject(String projectCode) {
        this.projectCode = projectCode;
    }

    public String getProjectCode() {
        return projectCode;
    }

    public String getRepositoryUrl() {
        return repositoryUrl;
    }

    public void setRepositoryUrl(String repositoryUrl) {
        this.repositoryUrl = repositoryUrl;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    private String runName;
    private String projectCode;
    private String repositoryUrl;
    private String branchName;
}
