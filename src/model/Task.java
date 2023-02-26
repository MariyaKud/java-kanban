package model;

public class Task extends Issue {

    public Task(int id, String tittle, String description) {
        super(id, tittle, description);
    }

    public Task(int id, String tittle, String description, IssueStatus status) {
        super(id, tittle, description);
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + this.id +
                ", status=" + this.status +
                ", tittle='" + this.tittle + '\'' +
                ", hash='" + this.hashCode() + '\'' +
                '}';
    }

}
