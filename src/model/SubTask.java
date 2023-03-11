package model;

public class SubTask extends Issue {

    /**
     * Владелец текущего экземпляра класса {@code SubTask}
     */
    private Epic parent;

    public SubTask(int id, String title, String description, Epic parent, IssueStatus status) {
        super(id,title,description);
        this.parent = parent;
        this.setStatus(status);
    }

    /**
     * Получить родителя экземпляра класса {@code SubTask}
     * @return - владелец экземпляр класса {@link Epic}
     */
    public Epic getParent() {
        return parent;
    }

    public void setParent(Epic parent) {
        this.parent = parent;
    }

    @Override
    public String toString() {
        return "SubTask{"  +
                "id=" + getId() +
                ", parentId=" + parent.getId() +
                ", status="   + getStatus() +
                ", title='"   + getTitle() + '\'' +
                ", hash='"    + hashCode() + '\'' +
                ", parent.hash='" + parent.hashCode() + '\'' +
                '}';
    }
}
