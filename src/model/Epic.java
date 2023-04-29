package model;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Эпик - класс для масштабных задач, которые лучше разбить на подзадачи
 * Каждый эпик знает, какие подзадачи в него входят.
 * Завершение всех подзадач эпика считается завершением эпика.
 */
public class Epic extends Issue {

    /**
     * Содержит список элементов класса {@link SubTask}, содержащихся в экземпляре {@code Epic}
     */
    private final List<SubTask> childrenList = new ArrayList<>();
    private ZonedDateTime endTime; //Время завершения

    public Epic(int id, String title, String description) {
        super(id, title, description);
        //Продолжительность нулевая у нового эпика бех детей
        this.setDuration(Duration.ZERO);
        //Время старта текущая дата
        this.setStartTime(ZonedDateTime.now());
        this.countEndTime();
    }

    public Epic(Epic other) {
        super(other);
        this.childrenList.addAll(other.getChildren());
        this.countDuration();
        this.countStartTime();
        this.countEndTime();
    }

    /**
     * Получить всех детей экземпляра класса {@code Epic}
     * @return - список элементов класса {@link SubTask}, содержащихся в экземпляре {@code Epic}
     */
    public List<SubTask> getChildren() {
        return childrenList;
    }

    public void setEndTime(ZonedDateTime endTime) {
        this.endTime = endTime;
    }

    public void countEndTime() {
        ZonedDateTime endTime = ZonedDateTime.now();
        for (SubTask child : getChildren()) {
            if (endTime.isBefore(child.getEndTime())) {
                endTime = child.getStartTime();
            }
        }
        this.setEndTime(endTime);
    }

    public void countDuration() {
        Duration durationEpic = Duration.ZERO;
        for (SubTask child : getChildren()) {
            durationEpic = durationEpic.plus(child.getDuration());
        }
        this.setDuration(durationEpic);
    }

    public void countStartTime() {
        ZonedDateTime startTime = ZonedDateTime.now();
        for (SubTask child : getChildren()) {
            if (startTime.isAfter(child.getStartTime())) {
                startTime = child.getStartTime();
            }
        }
        this.setStartTime(startTime);
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
        StringBuilder hashChildren = new StringBuilder();
        StringBuilder result = new StringBuilder();

        for (SubTask child : childrenList) {
            if (idChildren.length() == 0) {
                idChildren.append(child.getId());
                hashChildren.append(child.hashCode());
            } else {
                idChildren.append(",").append(child.getId());
                hashChildren.append(",").append(child.hashCode());
            }
        }
        result.append("Epic{").append("id=").append(getId()).append(", status=").append(getStatus());
        result.append(", title='").append(getTitle() ).append('\'').append(", description='").append(getDescription());
        result.append('\'').append(", hash='").append(hashCode()).append('\'');
        result.append(", children.size='").append(childrenList.size()).append('\'');
        result.append(", children.id='").append(idChildren).append('\'');
        result.append(", children.hash='").append(hashChildren).append('\'').append("}");

        for (SubTask subTask : getChildren()) {
            result.append("\n").append("\t\t").append(subTask);
        }
        return result.toString();
    }
}
