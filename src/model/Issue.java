package model;

import java.time.Duration;
import java.time.Instant;

/**
 * Универсальный класс задач, родитель любой сущности, управляемой менеджером.
 * <p>Минимально-обязательный набор полей любой задачи менеджера:
 * <p>id - идентификатор
 * <p>title - имя задачи
 * <p>description - описание задачи
 * <p>duration - продолжительность задачи, оценка того, сколько времени она займёт в минутах (число);
 * <p>startTime - дата/время, когда предполагается приступить к выполнению задачи.
 * <p>status - статус задачи.
 */

public abstract class Issue {

    private int id;                   // Идентификатор
    private String title;             // Название
    private String description;       // Описание
    private Duration duration;        //Продолжительность в минутах
    private Instant startTime;        //Время запуска
    private IssueStatus status;       // Статус

    public Issue(int id, String title, String description, Duration duration) {
        this.id          = id;
        this.title       = title;
        this.description = description;
        this.duration    = duration;
        this.startTime   = Instant.MIN;
        this.status      = IssueStatus.NEW;
    }

    public Issue(int id, String title, String description, Duration duration, Instant startTime) {
        this(id, title, description, duration);
        this.setStartTime(startTime);
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

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return startTime.plusSeconds(duration.toSeconds());
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(Instant startTime) {
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
