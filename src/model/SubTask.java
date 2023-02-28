package model;

public class SubTask extends Issue {
    private final Epic parent; // родитель эпик

    public SubTask(int id, String title, String description, Epic parent, IssueStatus status) {
        super(id,title,description);
        this.parent = parent;
        this.status = status;
    }

    public Epic getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "SubTask{"  +
                "id=" + id +
                ", parentId=" + parent.getId() +
                ", status="   + getStatus() +
                ", title='"   + getTitle() + '\'' +
                ", hash='"    + hashCode() + '\'' +
                ", parent.hash='" + parent.hashCode() + '\'' +
                '}';
    }
}
