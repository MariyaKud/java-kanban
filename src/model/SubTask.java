package model;

/**
 * Подзадача - класс для разбиения масштабных задач
 * Для каждой подзадачи известно, в рамках какого эпика она выполняется.
 */
public class SubTask extends Issue {

    /**
     * Идентификатор родителя экземпляр класса {@link Epic}, владелец текущего экземпляра класса {@code SubTask}
     */
    private int parentID;

    public SubTask(int id, String title, String description, int parentID, IssueStatus status) {
        super(id,title,description);
        this.parentID = parentID;
        this.setStatus(status);
    }

    public SubTask(SubTask other) {
        super(other);
        this.parentID = other.getParentID();
    }

    public int getParentID() {
        return parentID;
    }

    public void setParentID(int parentID) {
        this.parentID = parentID;
    }

    @Override
    public IssueType getType() {
        return IssueType.SUBTASK;
    }

    @Override
    public String toString() {
        return "SubTask{"  +
                "id=" + getId() +
                ", parentId=" + parentID +
                ", status="   + getStatus() +
                ", title='"   + getTitle() + '\'' +
                ", description='"   + getDescription() + '\'' +
                ", hash='"    + hashCode() + '\'' +
                '}';
    }
}
