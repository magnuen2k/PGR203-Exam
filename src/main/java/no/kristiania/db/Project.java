package no.kristiania.db;

public class Project {

    private long id;
    private String projectName;
    private String desc;
    private boolean projectStatus;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public void setProjectStatus(boolean projectStatus) {
        this.projectStatus = projectStatus;
    }

    public boolean getProjectStatus() {
        return projectStatus;
    }
}
