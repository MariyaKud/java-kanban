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
        super(id,title,description);
        this.setStatus(IssueStatus.NEW);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
