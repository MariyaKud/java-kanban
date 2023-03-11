package model;

public abstract class Issue {

    private final int id;             // Идентификатор
    private final String title;       // Название
    private final String description; // Описание
    private IssueStatus status;       // Статус

    public Issue(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = IssueStatus.NEW;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public IssueStatus getStatus() {
        return status;
    }

    protected void setStatus(IssueStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", hash='"  + hashCode() + '\'' +
                '}';
    }
}
