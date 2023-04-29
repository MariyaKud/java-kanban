package model;

import java.time.Duration;
import java.time.ZonedDateTime;

/**
 * Универсальный класс задач, родитель любой сущности, управляемой менеджером.
 *
 */
public abstract class Issue {

    private int id;                   // Идентификатор
    private String title;             // Название
    private String description;       // Описание
    private Duration duration;        //Продолжительность в минутах
    private  ZonedDateTime startTime; //Время запуска
    private IssueStatus status;       // Статус

    public Issue(int id, String title, String description, Duration duration, ZonedDateTime startTime) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.startTime = startTime;
        this.status = IssueStatus.NEW;
    }

    public Issue(int id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.duration = Duration.ZERO;
        this.startTime = ZonedDateTime.now();
        this.status = IssueStatus.NEW;
    }

    public Issue(Issue other) {
        this(other.getId(), other.getTitle(), other.getDescription(), other.getDuration(), other.getStartTime());
        this.status = other.getStatus();
    }

    public IssueType getType() {
        return IssueType.TASK;
    }

    public int getId() {
        return id;
    }

    public IssueStatus getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Duration getDuration() {
        return duration;
    }

    public ZonedDateTime getStartTime() {
        return startTime;
    }

    public ZonedDateTime getEndTime() {
        return startTime.plusMinutes(duration.toMinutes());
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(ZonedDateTime startTime) {
        this.startTime = startTime;
    }

    @Override
    public int hashCode() {
        return getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Issue task = (Issue) o;
        return getId() == task.getId() &&
                getStatus() == task.getStatus() &&
                getDuration().equals(task.getDuration()) &&
                getStartTime().equals(task.getStartTime()) &&
                getTitle().equals(task.getTitle()) &&
                getDescription().equals(task.getDescription());
    }
}
