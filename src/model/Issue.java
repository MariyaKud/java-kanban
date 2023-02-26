package model;

public abstract class Issue {
    protected int id;             // Идентификатор
    protected String tittle;      // Название
    protected String description; // Описание
    protected IssueStatus status;  // Статус

    public Issue(int id, String tittle, String description) {
        this.id = id;
        this.tittle = tittle;
        this.description = description;
        this.status = IssueStatus.NEW;
    }

    public int getId() {
        return id;
    }

    public String getTittle() {
        return tittle;
    }

    public String getDescription() {
        return description;
    }

    public IssueStatus getStatus() {
        return status;
    }

}
