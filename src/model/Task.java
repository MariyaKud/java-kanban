package model;

import service.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Задача - класс для простых задач
 */
public class Task extends Issue {

    public Task(int id, String title, String description, Duration duration) {
        super(id, title, description, duration);
    }

    public Task(int id, String title, String description, Duration duration, LocalDateTime startTime) {
        super(id, title, description, duration, startTime);
    }

    public Task(int id, String title, String description, Duration duration, LocalDateTime startTime,
                IssueStatus status) {
        super(id, title, description, duration, startTime);
        this.setStatus(status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", title='" + getTitle() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", startTime='" + getStartTime().format(Managers.getFormatter()) + '\'' +
                ", endTime='" + getEndTime().format(Managers.getFormatter()) + '\'' +
                ", duration='" + getDuration().toMinutes() + "мин." + '\'' +
                '}';
    }
}
