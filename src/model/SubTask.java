package model;

/**
 * Подзадача - класс для разбиения масштабных задач
 * Для каждой подзадачи известно, в рамках какого эпика она выполняется.
 */
public class SubTask extends Issue {

    /**
     * Родитель экземпляр класса {@link Epic}, владелец текущего экземпляра класса {@code SubTask}
     */
    private Epic parent;

    public SubTask(int id, String title, String description, Epic parent, IssueStatus status) {
        super(id,title,description);
        this.parent = parent;
        this.setStatus(status);
    }

    public SubTask(SubTask other) {
        super(other);
        this.parent = other.getParent();
    }

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
