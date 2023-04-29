package model;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Подзадача - класс для разбиения масштабных задач
 * Для каждой подзадачи известно, в рамках какого эпика она выполняется.
 */
public class SubTask extends Issue {

    /**
     * Идентификатор родителя экземпляр класса {@link Epic}, владелец текущего экземпляра класса {@code SubTask}
     */
    private int parentID;

    public SubTask(int id, String title, String description, Duration duration, ZonedDateTime startTime,
                   int parentID, IssueStatus status) {
        super(id, title, description, duration, startTime);
        this.parentID = parentID;
        this.setStatus(status);
    }

    public SubTask(int id, String title, String description, Duration duration, ZonedDateTime startTime,
                   int parentID) {
        this(id, title, description, duration, startTime, parentID, IssueStatus.NEW);
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
                ", startTime='" + getStartTime() + '\'' +
                ", duration='" + getDuration() + '\'' +
                ", hash='"    + hashCode() + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        SubTask subTask = (SubTask) o;
        return super.equals(o) && getParentID() == subTask.getParentID();
    }
}
