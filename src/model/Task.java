package model;

public class Task extends Issue {

    public Task(int id, String title, String description, IssueStatus status) {
        super(id,title,description);
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", status=" + status +
                ", title='" + title + '\'' +
                ", hash='"  + hashCode() + '\'' +
                '}';
    }
}
