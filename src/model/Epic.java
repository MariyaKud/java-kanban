package model;

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

    public Epic(int id, String title, String description) {
        super(id, title, description);
    }

    public Epic(Epic other) {
        super(other);
        this.childrenList.addAll(other.getChildrenList());
    }

    /**
     * Получить всех детей экземпляра класса {@code Epic}
     * @return - список элементов класса {@link SubTask}, содержащихся в экземпляре {@code Epic}
     */
    public List<SubTask> getChildrenList() {
        return childrenList;
    }


    @Override
    public String toString() {
        //Идентификаторы детей эпика через ","
        StringBuilder idChildren = new StringBuilder();
        StringBuilder hashChildren = new StringBuilder();

        for (SubTask child : this.childrenList) {
            if (idChildren.length() == 0) {
                idChildren.append(child.getId());
                hashChildren.append(child.hashCode());
            } else {
                idChildren.append(",").append(child.getId());
                hashChildren.append(",").append(child.hashCode());
            }
        }

        return "Epic{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", children.size='" + childrenList.size() + '\'' +
                ", children.id='" + idChildren + '\'' +
                ", title='" + getTitle() + '\'' +
                ", hash='" + hashCode() + '\'' +
                ", children.hash='" + hashChildren + '\'' +
                '}';
    }
}
