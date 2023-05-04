package model;

import service.Managers;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/**
 * Эпик - класс для масштабных задач, которые лучше разбить на подзадачи экземпляры {@link SubTask}.
 * Каждый эпик знает, какие подзадачи в него входят {@code children}
 * Завершение всех подзадач эпика считается завершением эпика.
 */
public class Epic extends Issue {

    //Содержит список элементов класса SubTask
    private final List<SubTask> children = new ArrayList<>();

    //Расчетное поле, время окончания самой поздней из задач
    private Instant endTime;

    public Epic(int id, String title, String description) {
        super(id, title, description, Duration.ZERO);
        //Время старта/завершения текущая дата
        this.setStartTime(Instant.MIN);
        this.setEndTime(Instant.MIN);
    }

    public Epic( String title, String description) {
        this(0, title, description);
    }

    public void setEndTime(Instant endTime) {
        this.endTime = endTime;
    }

    /**
     * Получить всех детей экземпляра класса {@code Epic}
     * @return - список элементов класса {@link SubTask}, содержащихся в экземпляре {@code Epic}
     */
    public List<SubTask> getChildren() {
        return children;
    }

    @Override
    public IssueType getType() {
        return IssueType.EPIC;
    }

    @Override
    public boolean equals(Object o) {
        Epic epic = (Epic) o;
        return super.equals(o) && getChildren().equals(epic.getChildren());
    }

    @Override
    public String toString() {
        //Идентификаторы детей эпика через ","
        StringBuilder idChildren = new StringBuilder();
        StringBuilder result = new StringBuilder();

        for (SubTask child : children) {
            if (idChildren.length() == 0) {
                idChildren.append(child.getId());
            } else {
                idChildren.append(",").append(child.getId());
            }
        }
        result.append("Epic{").append("id=").append(getId()).append(", status=").append(getStatus());
        result.append(", title='").append(getTitle() ).append('\'').append(", description='").append(getDescription());
        if (getStartTime() == Instant.MIN) {
            result.append('\'').append(", startTime='0");
            result.append('\'').append(", endTime='0'");
        } else {
            result.append('\'').append(", startTime='").append(Managers.getFormatter().format(getStartTime()));
            result.append('\'').append(", endTime='").append(Managers.getFormatter().format(getEndTime())).append('\'');
        }
        result.append(", duration='").append(getDuration().toMinutes()).append("мин.").append('\'');
        result.append(", children.size='").append(children.size()).append('\'');
        result.append(", children.id='").append(idChildren).append('\'').append("}");

        for (SubTask subTask : getChildren()) {
            result.append("\n").append("\t\t").append(subTask);
        }
        return result.toString();
    }
}
