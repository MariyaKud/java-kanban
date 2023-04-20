package model;

/**
 * Универсальный класс задач, родитель любой сущности, управляемой менеджером.
 *
 */
public abstract class Issue {

    private int id;             // Идентификатор
    private String title;       // Название
    private String description; // Описание
    private IssueStatus status; // Статус

    public Issue(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = IssueStatus.NEW;
    }

    public Issue(Issue other) {
        this(other.getId(), other.getTitle(), other.getDescription());
        this.status = other.getStatus();
    }

    public int getId() {
        return id;
    }

    public IssueType getType() {
        return IssueType.TASK;
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

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Issue task = (Issue) o;
        return getId() == task.getId() &&
                getStatus() == task.getStatus() &&
                getTitle().equals(task.getTitle()) &&
                getDescription().equals(task.getDescription());
    }
}
