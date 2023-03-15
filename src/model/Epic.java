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
     * В следующей версии тип хранимых данных в списке будет изменен на id детей
     * Если другие менеджеры будут иметь другие правила формирования статуса эпика, то метод будет перенесен в менеджер,
     * по текущим условиям задачи наличие этого метода в этом классе кажется более симпатичным и стабильным решением.
     */
    private final List<SubTask> childrenList = new ArrayList<>();

    public Epic(int id, String title, String description) {
        super(id, title, description);
    }

    /**
     * Получить всех детей экземпляра класса {@code Epic}
     * @return - список элементов класса {@link SubTask}, содержащихся в экземпляре {@code Epic}
     */
    public List<SubTask> getChildrenList() {
        return childrenList;
    }

    /**
     * <b>Рассчитать статус эпика</b>
     * <p>Правило установки статуса эпика:
     * Если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
     * Если все подзадачи имеют статус DONE, то и эпик считается завершённым со статусом DONE.
     * Во всех остальных случаях статус должен быть IN_PROGRESS.
     */
    public void updateStatus() {

        if (childrenList.size() == 0) {
            setStatus(IssueStatus.NEW);
        } else {
            boolean allNew = true;
            boolean allDone = true;

            for (SubTask child : childrenList) {
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
