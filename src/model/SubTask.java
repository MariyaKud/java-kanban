package model;

public class SubTask extends Issue {
    private final Epic parent; // родитель эпик

    public SubTask(int id, String tittle, String description, Epic parent) {
        super(id, tittle, description);
        this.parent = parent;
        parent.addChild(this);
    }

    public SubTask(int id, String tittle, String description, Epic parent, IssueStatus status) {
        super(id, tittle, description);
        this.parent = parent;
        this.status = status;
    }

    public Epic getParent() {
        return parent;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + this.id +
                ", parentId=" + this.parent.getId() +
                ", status=" + this.getStatus() +
                ", tittle='" + this.getTittle() + '\'' +
                ", hash='" + this.hashCode() + '\'' +
                ", parent.hash='" + this.parent.hashCode() + '\'' +
                '}';
    }

}
