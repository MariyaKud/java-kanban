package model;

import service.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Подзадача - класс для разбиения масштабных задач
 * Для каждой подзадачи известно, в рамках какого эпика она выполняется.
 */
public class SubTask extends Issue {

    /**
     * Идентификатор родителя экземпляр класса {@link Epic}, владелец текущего экземпляра класса {@code SubTask}
     */
    private final int parentID;

    public SubTask(int id, String title, String description, Duration duration, LocalDateTime startTime,
                   int parentID, IssueStatus status) {
        super(id, title, description, duration, startTime);
        this.parentID = parentID;
        this.setStatus(status);
    }

    public SubTask(int id, String title, String description, Duration duration, LocalDateTime startTime, int parentID) {
        this(id, title, description, duration, startTime, parentID, IssueStatus.NEW);
    }

    public SubTask(int id, String title, String description, Duration duration, int parentID) {
        this(id, title, description, duration, LocalDateTime.MIN, parentID, IssueStatus.NEW);
    }

    public SubTask(String title, String description, Duration duration, int parentID) {
        this(0, title, description, duration, LocalDateTime.MIN, parentID, IssueStatus.NEW);
    }

    public SubTask(String title, String description, Duration duration, int parentID, IssueStatus status) {
        this(0, title, description, duration, LocalDateTime.MIN, parentID);
        this.setStatus(status);
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
                ", startTime='" + getStartTime().format(Managers.getFormatter()) + '\'' +
                ", endTime='" + getEndTime().format(Managers.getFormatter()) + '\'' +
                ", duration='" + getDuration().toMinutes() + "мин." + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        SubTask subTask = (SubTask) o;
        return super.equals(o) && getParentID() == subTask.getParentID();
    }
}
