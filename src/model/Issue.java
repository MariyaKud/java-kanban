package model;

/**
 * Универсальный класс задач, родитель любой сущности, управляемой менеджером.
 *
 */
public abstract class Issue {

    private int id;             // Идентификатор
    private final String title;       // Название
    private final String description; // Описание
    private IssueStatus status;       // Статус

    public Issue(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = IssueStatus.NEW;
    }

    // конструктор копии
    public Issue(Issue other) {
        this(other.getId(), other.getTitle(), other.getDescription());
        this.status = other.getStatus();
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

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public void setId(int id) {
        this.id = id;
    }
}
