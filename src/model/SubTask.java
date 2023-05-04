package model;

import service.Managers;

import java.time.Instant;

/**
 * Подзадача - класс для разбиения масштабных задач
 * Для каждой подзадачи известно, в рамках какого эпика она выполняется.
 */
public class SubTask extends Issue {

    /**
     * Идентификатор родителя экземпляр класса {@link Epic}, владелец текущего экземпляра класса {@code SubTask}
     */
    private final int parentID;

    public SubTask(int id, String title, String description, int duration, Instant startTime,
                   int parentID, IssueStatus status) {
        super(id, title, description, duration, startTime);
        this.parentID = parentID;
        this.setStatus(status);
    }

    public SubTask(int id, String title, String description, int parentID, int duration, Instant startTime) {
        this(id, title, description, duration, startTime, parentID, IssueStatus.NEW);
    }

    public SubTask(int id, String title, String description, int duration, int parentID) {
        this(id, title, description, duration, Instant.MIN, parentID, IssueStatus.NEW);
    }

    public SubTask(String title, String description, int duration, int parentID) {
        this(0, title, description, duration, Instant.MIN, parentID, IssueStatus.NEW);
    }

    public SubTask(String title, String description, int duration, int parentID, IssueStatus status) {
        this(0, title, description, parentID, duration, Instant.MIN);
        this.setStatus(status);
    }

    public SubTask (SubTask other) {
        this(other.getId(), other.getTitle(), other.getDescription(), other.getParentID() , other.getDuration(),
                other.getStartTime());
    }

    public int getParentID() {
        return parentID;
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
                ", startTime='" + Managers.getFormatter().format(getStartTime()) + '\'' +
                ", endTime='" + Managers.getFormatter().format(getEndTime()) + '\'' +
                ", duration='" + getDuration() + "мин." + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        SubTask subTask = (SubTask) o;
        return super.equals(o) && getParentID() == subTask.getParentID();
    }
}
