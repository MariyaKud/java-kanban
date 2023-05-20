package model;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

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

    private Integer id;               // Идентификатор
    private String title;             // Название
    private String description;       // Описание
    private int duration;             //Продолжительность в минутах
    private Instant startTime;        //Время запуска
    private IssueStatus status;       // Статус

    public Issue(Integer id, String title, String description, int duration) {
        this.id          = id;
        this.title       = title;
        this.description = description;
        this.duration    = duration;
        this.startTime   = Instant.MAX;
        this.status      = IssueStatus.NEW;
    }

    public Issue(Integer id, String title, String description, int duration, Instant startTime) {
        this(id, title, description, duration);
        this.setStartTime(startTime);
    }

    public IssueType getType() {
        return IssueType.TASK;
    }

    public Integer getId() {
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

    public int getDuration() {
        return duration;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        if (Instant.MAX.equals(getStartTime())) {
            return Instant.MAX;
        } else {
            return startTime.plusSeconds(duration * 60L);
        }
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setStatus(IssueStatus status) {
        this.status = status;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setStartTime(Instant startTime) {
        if (startTime == Instant.MAX) {
            this.startTime = startTime;
        } else {
            this.startTime = startTime.truncatedTo(ChronoUnit.MINUTES);
        }
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
                getDuration() == task.getDuration() &&
                getStartTime().equals(task.getStartTime()) &&
                getTitle().equals(task.getTitle()) &&
                getDescription().equals(task.getDescription());
    }
}
