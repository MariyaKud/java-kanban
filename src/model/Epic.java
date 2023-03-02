package model;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Issue {

    /**
     * Содержит список элементов класса {@link SubTask}, содержащихся в экземпляре {@link Epic}
     */
    private final List<SubTask> children = new ArrayList<>();

    public Epic(int id, String title, String description) {
        super(id, title, description);
    }

    public List<SubTask> getListChildren() {
        return children;
    }

    /**
     * <b>Метод расчета статуса эпика</b>
     * <p>Правило установки статуса эпика:
     * Если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
     * Если все подзадачи имеют статус DONE, то и эпик считается завершённым со статусом DONE.
     * Во всех остальных случаях статус должен быть IN_PROGRESS.
     */
    public void updateStatus() {

        if (children.size() == 0) {
            setStatus(IssueStatus.NEW);
        } else {

            boolean allNew = true;
            boolean allDone = true;

            for (SubTask child : children) {
                if (child.getStatus() != IssueStatus.NEW) {
                    allNew = false;
                }
                if (child.getStatus() != IssueStatus.DONE) {
                    allDone = false;
                }
                //Прерываем цикл, ничего нового мы дальше не узнаем
                if (!allNew && !allDone) {
                    break;
                }
            }

            if (allNew) {
                setStatus(IssueStatus.NEW);
            } else if (allDone) {
                setStatus(IssueStatus.DONE);
            } else {
                setStatus(IssueStatus.IN_PROGRESS);
            }

        }
    }

    @Override
    public String toString() {
        //Идентификаторы детей эпика через ","
        StringBuilder idChildren = new StringBuilder();
        StringBuilder hashChildren = new StringBuilder();

        for (SubTask child : this.children) {
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
                ", children.size='" + children.size() + '\'' +
                ", children.id='" + idChildren + '\'' +
                ", title='" + getTitle() + '\'' +
                ", hash='" + hashCode() + '\'' +
                ", children.hash='" + hashChildren + '\'' +
                '}';
    }
}
