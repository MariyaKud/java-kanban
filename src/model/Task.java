package model;

/**
 * Задача - класс для простых задач
 */
public class Task extends Issue {

    public Task(int id, String title, String description, IssueStatus status) {
        super(id,title,description);
        this.setStatus(status);
    }

    public Task(int id, String title, String description) {
        this(id,title,description,IssueStatus.NEW);
    }

    public Task(Task other) {
        super(other);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", hash='"  + hashCode() + '\'' +
                '}';
    }
}
