package model;

public abstract class Issue {
    protected int id;             // Идентификатор
    protected String title;      // Название
    protected String description; // Описание
    protected IssueStatus status;  // Статус

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

}
