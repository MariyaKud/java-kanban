package model;

import java.util.ArrayList;

public class Epic extends Issue {
    private final ArrayList<SubTask> children = new ArrayList<>(); // Список подзадач эпика

    public Epic(int id, String title, String description) {
        super(id, title, description);
    }

    public ArrayList<SubTask> getChildren() {
        return children;
    }

    /**
     * Метод для расчета статуса эпика. Правила для установки статуса эпика.
     * Если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
     * Если все подзадачи имеют статус DONE, то и эпик считается завершённым со статусом DONE.
     * Во всех остальных случаях статус должен быть IN_PROGRESS.
     */
    public void updateStatus() {

        if (children.size() == 0) {
            this.status = IssueStatus.NEW;
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
                this.status = IssueStatus.NEW;
            } else if (allDone) {
                this.status = IssueStatus.DONE;
            } else {
                this.status = IssueStatus.IN_PROGRESS;
            }

        }
    }

    @Override
    public String toString() {
        //Идентификаторы детей эпика через ","
        String idChildren = "";
        String hashChildren = "";

        for (SubTask child : this.children) {
            if (idChildren.equals("")) {
                idChildren = idChildren + child.getId();
                hashChildren = hashChildren + child.hashCode();
            } else {
                idChildren = idChildren + "," + child.getId();
                hashChildren = hashChildren + "," + child.hashCode();
            }
        }

        return "Epic{" +
                "id=" + this.id +
                ", status=" + this.status +
                ", children.size='" + this.children.size() + '\'' +
                ", children.id='" + idChildren + '\'' +
                ", title='" + this.getTitle() + '\'' +
                ", hash='" + this.hashCode() + '\'' +
                ", children.hash='" + hashChildren + '\'' +
                '}';
    }
}
