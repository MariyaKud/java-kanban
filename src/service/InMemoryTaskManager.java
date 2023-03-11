package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Основной класс управления сущностями: {@code Task}, {@code SubTask}, {@code Epic} наследники класса {@code Issue}
 *
 * <p><b>Описание сущности {@link Issue}:</b>
 * <p> - Название, кратко описывающее суть {@code Issue}.
 * <p> - Описание, в котором раскрываются детали.
 * <p> - Уникальный идентификационный номер сущности, по которому её можно будет найти.
 * <p> - Статус, отображающий её прогресс.
 * <p>Мы будем выделять следующие этапы жизни сущности: {@code NEW},{@code IN_PROGRESS},{@code DONE}.
 *
 * <p><b>Функции объекта-менеджера:</b>
 * <p>  - Возможность хранить задачи всех типов.
 * <p>  - Методы для каждого из типа Issue ({@code Task},{@code SubTask},{@code Epic}):
 * <p>  - Получение списка всех задач.
 * <p>  - Получение списка всех задач.
 * <p>  - Удаление всех задач.
 * <p>  - Получение по идентификатору.
 * <p>  - Создание. Сам объект должен передаваться в качестве параметра.
 * <p>  - Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
 * <p>  - Удаление по идентификатору.
 *
 * <p><b>Дополнительные методы:</b>
 * <p>  - Получение списка всех подзадач определённого эпика.
 */
public class InMemoryTaskManager implements TaskManager {

    // Переменные класса
    private int id = 1; // идентификатор менеджера
    private final Map<Integer, Task> tasksMap = new HashMap<>();
    private final Map<Integer, Epic> epicsMap = new HashMap<>();
    private final Map<Integer, SubTask> subTasksMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    /**
     * Метод выдает очередной идентификатор для новой задачи,
     * <p> готовит идентификатор для следующей задачи.
     *
     * @return возвращает очередной свободный идентификатор
     */
    private int getId() {
        return id++;
    }

    /**
     * Перезапустить менеджер задач:
     * <p> - очищает хранилища: задачи/подзадачи/эпики
     * <p> - устанавливает счетчик задач в 1 (id = 1)
     */
    public void restartTaskManager() {
        delAllIssues(IssueType.TASK);
        delAllIssues(IssueType.EPIC);
        historyManager.clearHistory();
        id = 1;
    }

    /**
     * Создать новую задачу.
     */
    @Override
    public Task addTask(String title, String description, IssueStatus status) {
        return new Task(getId(), title, description, status);
    }

    /**
     * Создать новую подзадачу.
     */
    @Override
    public SubTask addSubTask(String title, String description, Epic parent, IssueStatus status) {
        return new SubTask(getId(), title, description, parent, status);
    }

    /**
     * Создать новый эпик.
     */
    @Override
    public Epic addEpic(String title, String description) {
        return new Epic(getId(), title, description);
    }

    /**
     * Создать задачу. Сам объект передается в качестве параметра.
     *
     * @param issue     экземпляр класса Issue
     */
    @Override
    public void addIssue(Issue issue) {

        if (issue instanceof Task) {
            tasksMap.put(issue.getId(), (Task) issue);

        } else if (issue instanceof Epic) {
            List<SubTask> children = ((Epic) issue).getChildrenList();

            epicsMap.put(issue.getId(), (Epic) issue);

            //Проверяем наличие подзадач в хранилище менеджера, если не находим, то добавляем
            for (SubTask child : children) {
                if (!subTasksMap.containsValue(child)){
                    addIssue(child);
                }
            }
            //у нового эпика состав детей не меняется, значит не меняется и статус

        } else if (issue instanceof SubTask) {
            Epic parent = ((SubTask) issue).getParent();
            List<SubTask> children = parent.getChildrenList();

            subTasksMap.put(issue.getId(), (SubTask) issue);

            //Добавляем родителя, если его нет в хранилище
            if (!epicsMap.containsValue(parent)) {
                addIssue(parent);
            }

            //Добавляем родителю ребенка, если нужно
            if (!children.contains(issue)) {
                children.add((SubTask) issue);
            }

            //Обновляем статус родителя
            parent.updStatus();

        } else {
            System.out.println(MSG_ERROR_TYPE_UN_KNOW);
        }
    }

    /**
     * Обновить задачу. Новая версия объекта передается в качестве параметра.
     *
     * @param issue новая версия объекта с верным идентификатором, включая обновленный статус
     */
    @Override
    public void updIssue(Issue issue) {

        if (issue instanceof Task) {
            Task oldTask = (Task) getIssueById(IssueType.TASK, issue.getId());

            //Мы можем обновить только существующий объект
            if (oldTask != null) {
                tasksMap.put(oldTask.getId(), (Task) issue);        // обновляем задачу
            } else {
                System.out.println(MSG_ERROR_ID_NOT_FOUND);
            }

        } else if (issue instanceof Epic) {
            Epic oldEpic = (Epic) getIssueById(IssueType.EPIC, issue.getId());

            if (oldEpic != null) {
                //при обновлении эпика его дети остаются "старыми"
                if (oldEpic.getChildrenList().equals(((Epic) issue).getChildrenList())) {
                    epicsMap.put(oldEpic.getId(), (Epic) issue);    // обновляем эпик

                    //меняем родителя у детей
                    for (SubTask subTask : oldEpic.getChildrenList()) {
                        subTask.setParent((Epic) issue);
                    }
                    //у обновляемого эпика состав детей не меняется, значит не меняется и статус

                } else {
                    System.out.println(MSG_ERROR_WRONG_EPIC);
                }
            } else {
                System.out.println(MSG_ERROR_ID_NOT_FOUND);
            }

        } else if (issue instanceof SubTask) {
            SubTask oldSubTask  = (SubTask) getIssueById(IssueType.SUBTASK, issue.getId());

            if (oldSubTask != null) {
                Epic parent = ((SubTask) issue).getParent();
                if (epicsMap.containsValue(parent)) {
                    subTasksMap.put(issue.getId(), (SubTask) issue); // обновляем подзадачу

                    //Удаляем старую подзадачу у эпика родителя
                    oldSubTask.getParent().getChildrenList().remove(oldSubTask);
                    //Обновляем статус старого родителя
                    oldSubTask.getParent().updStatus();

                    //Добавляем обновленную подзадачу в эпик
                    if (!parent.getChildrenList().contains((SubTask) issue)) {
                        parent.getChildrenList().add((SubTask) issue);
                    }
                    //Обновляем статус нового родителя
                    parent.updStatus();

                } else {
                    System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
                }
            } else {
                System.out.println(MSG_ERROR_ID_NOT_FOUND);
            }
        } else {
            System.out.println(MSG_ERROR_TYPE_UN_KNOW);
        }
    }

    /**
     * Удалить задачу по идентификатору.
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     * @param idIssue   идентификатор задачи к удалению
     */
    @Override
    public void delIssueById(IssueType issueType, int idIssue) {

        switch (issueType) {
            case TASK:
                if (tasksMap.containsKey(idIssue)) {
                    tasksMap.remove(idIssue);
                } else {
                    System.out.println(MSG_ERROR_ID_NOT_FOUND);
                }
                break;

            case SUBTASK:
                if (subTasksMap.containsKey(idIssue)) {
                    SubTask subTask = (SubTask) getIssueById(issueType, idIssue);
                    if (subTask != null) {
                        //Удаляем эту подзадачу в эпике
                        subTask.getParent().getChildrenList().remove(subTask);
                        //Обновляем статус родителя
                        subTask.getParent().updStatus();
                        //Удаляем из менеджера подзадачу
                        subTasksMap.remove(idIssue);
                    }
                } else {
                    System.out.println(MSG_ERROR_ID_NOT_FOUND);
                }
                break;

            case EPIC:
                if (epicsMap.containsKey(idIssue)) {
                    //удаляем подзадачи эпика в менеджере
                    for (SubTask child : epicsMap.get(idIssue).getChildrenList()) {
                        if (subTasksMap.containsValue(child)) {
                            subTasksMap.remove(child.getId());
                        }
                    }
                    epicsMap.remove(idIssue);
                } else {
                    System.out.println(MSG_ERROR_ID_NOT_FOUND);
                }
                break;

            default:
                System.out.println(MSG_ERROR_TYPE_UN_KNOW);
        }
    }

    /**
     * Получить задачу по идентификатору, если задача не найдена, то метод вернет NULL.
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     * @param idIssue   идентификатор задачи для поиска
     * @return - задачу по идентификатору или NULL
     */
    @Override
    public Issue getIssueById(IssueType issueType, int idIssue) {
        Issue issue;

        switch (issueType) {
            case TASK:
                issue = tasksMap.get(idIssue);
                break;
            case EPIC:
                issue = epicsMap.get(idIssue);
                break;
            case SUBTASK:
                issue = subTasksMap.get(idIssue);
                break;
            default:
                System.out.println(MSG_ERROR_TYPE_UN_KNOW);
                return null;
        }

        historyManager.add(issue);
        return issue;
    }

    /**
     * Получить список всех задач заданного типа.
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     * @return возвращает список задач менеджера по заданному типу
     */
    @Override
    public List<Issue> getListAllIssues(IssueType issueType) {
        ArrayList<Issue> issues = new ArrayList<>();

        switch (issueType) {
            case TASK:
                issues.addAll(tasksMap.values());
                break;

            case EPIC:
                issues.addAll(epicsMap.values());
                break;

            case SUBTASK:
                issues.addAll(subTasksMap.values());
                break;

            default:
                System.out.println(MSG_ERROR_TYPE_UN_KNOW);
        }
        return issues;
    }

    /**
     * Удалить все задачи по заданному типу.
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     */
    @Override
    public void delAllIssues(IssueType issueType) {
        switch (issueType) {
            case TASK:
                tasksMap.clear();
                break;

            case EPIC:
                subTasksMap.clear(); // нет смысла оставлять подзадачи, если родители удалены
                epicsMap.clear();
                break;

            case SUBTASK:
                for (Epic epic : epicsMap.values()) {
                    epic.getChildrenList().clear();
                }
                subTasksMap.clear();
                break;

            default:
                System.out.println(MSG_ERROR_TYPE_UN_KNOW);
        }
    }

    /**
     * Получить список всех подзадач для эпика.
     *
     * @param epic эпик, по которому нужно получить список подзадач
     * @return список подзадач эпика
     */
    @Override
    public List<SubTask> getListSubTaskOfEpic(Epic epic) {
        return epic.getChildrenList();
    }

    /**
     * Получить историю просмотров задач.
     *
     * @return список просмотренных задач = {Task, SubTask, Epic}
     */
    @Override
    public List<Issue> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Manager{" + "\n");
        if (tasksMap.size() != 0) {
            result.append("TASK:\n");
            for (Task tsk : tasksMap.values()) {
                result.append("\t").append(tsk).append("\n");
            }
        }
        if (epicsMap.size() != 0) {
            result.append("EPIC:\n");
            for (Epic epc : epicsMap.values()) {
                result.append("\t").append(epc).append("\n");
                if (epc.getChildrenList().size() != 0) {
                    for (SubTask subtsk : epc.getChildrenList()) {
                        result.append("\t\t").append(subtsk).append("\n");
                    }
                }
            }
        }
        result.append("}").append("\n");

        return result.toString();
    }
}
