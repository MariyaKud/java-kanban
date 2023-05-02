package model;

import service.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
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
    private LocalDateTime endTime;

    public Epic(int id, String title, String description) {
        super(id, title, description, Duration.ZERO);
        //Продолжительность нулевая у нового эпика бех детей
        this.setDuration(Duration.ZERO);
        //Время старта/завершения текущая дата
        this.setStartTime(LocalDateTime.MIN);
        this.setEndTime(LocalDateTime.MIN);
    }

    public Epic( String title, String description) {
        this(0, title, description);
    }

    public void setEndTime(LocalDateTime endTime) {
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
        result.append('\'').append(", startTime='").append(getStartTime().format(Managers.getFormatter()));
        result.append('\'').append(", endTime='").append(getEndTime().format(Managers.getFormatter())).append('\'');
        result.append(", duration='").append(getDuration().toMinutes()).append("мин.").append('\'');
        result.append(", children.size='").append(children.size()).append('\'');
        result.append(", children.id='").append(idChildren).append('\'').append("}");

        for (SubTask subTask : getChildren()) {
            result.append("\n").append("\t\t").append(subTask);
        }
        return result.toString();
    }
}
