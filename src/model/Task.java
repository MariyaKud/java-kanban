package model;

import service.Managers;

import java.time.Duration;
import java.time.Instant;


/**
 * Задача - класс для простых задач
 */
public class Task extends Issue {

    //Максимальный набор полей
    public Task(int id, String title, String description, Duration duration, Instant startTime,
                IssueStatus status) {
        super(id, title, description, duration, startTime);
        this.setStatus(status);
    }

    public Task(int id, String title, String description, Duration duration, Instant startTime) {
        super(id, title, description, duration, startTime);
    }

    public Task(int id, String title, String description, Duration duration) {
        super(id, title, description, duration);
    }

    //Минимальный набор полей
    public Task(String title, String description, Duration duration) {
        super(0, title, description, duration);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", startTime='" + Managers.getFormatter().format(getStartTime()) + '\'' +
                ", endTime='" + Managers.getFormatter().format(getEndTime()) + '\'' +
                ", duration='" + getDuration().toMinutes() + "мин." + '\'' +
                '}';
    }
}
