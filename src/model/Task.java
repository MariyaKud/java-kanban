package model;

public class Task extends Issue {

    public Task(int id, String title, String description, IssueStatus status) {
        super(id,title,description);
        this.setStatus(status);
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
