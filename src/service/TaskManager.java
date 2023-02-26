package service;

import java.util.HashMap;
import java.util.ArrayList;

import model.*;

public class TaskManager {

    // Переменные класса
    private int id; //доступный идентификатор менеджера
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();

    //Текст сообщений об ошибках
    private final static String msgErrorTypeNull = "Для метода не указан тип задачи.";
    private final static String msgErrorTypeUnKnow = "Для выбранного типа задач не создан обработчик в методе.";
    private final static String msgErrorIdNotFound = "Не найдена задача с указанным id.";
    private final static String msgErrorTaskNUll = "Данные по обновлению задачи пустые.";
    private final static String msgErrorNotFoundEpic = "Менеджер не нашел родителя добавляемой подзадачи.";

    public TaskManager() {
        this.id = 1;
    }

    /**
     * Метод выдает очередной идентификатор для новой задачи + готовит идентификатор для следующей задачи
     *
     * @return возвращает очередной свободный идентификатор
     */
    public int newId() {
        return id++;
    }

    /**
     * Метод для перезапуска менеджера:
     * - очищает все мапы: задачи/подзадачи/эпики
     * - id = 1
     */
    public void restartTaskManager() {
        deleteAllTask(IssueType.TASK);
        deleteAllTask(IssueType.EPIC);
        this.id = 1;
    }

    /**
     * Метод создает задачу. Сам объект передается в качестве параметра.
     *
     * @param issueType - тип задачи IssueType = {Task, SubTask, Epic}
     * @param issue     - экземпляр класса Issue
     */
    public void createTask(IssueType issueType, Issue issue) {

        if (issueType == null) {
            System.out.println(msgErrorTypeNull);
        } else if (issue == null) {
            System.out.println(msgErrorTaskNUll);
        } else {
            switch (issueType) {
                case TASK:
                    tasks.put(issue.getId(), (Task) issue);
                    break;
                case EPIC:
                    epics.put(issue.getId(), (Epic) issue);
                    //эпик создается без подзадач, поэтому проверка для детей не нужна
                    break;
                case SUBTASK:
                    Epic parent = ((SubTask) issue).getParent();
                    if (epics.containsValue(parent)) {
                        subTasks.put(issue.getId(), (SubTask) issue);
                    } else {
                        System.out.println(msgErrorNotFoundEpic);
                    }
                    break;
                default:
                    System.out.println(msgErrorTypeUnKnow);
            }
        }
    }

    /**
     * Метод удаляет задачу по идентификатору.
     *
     * @param issueType - тип задачи IssueType = {Task, SubTask, Epic}
     * @param idIssue   - идентификатор задачи на удаление
     */
    public void deleteTask(IssueType issueType, int idIssue) {

        switch (issueType) {
            case TASK:
                if (tasks.containsKey(idIssue)) {
                    tasks.remove(idIssue);
                } else {
                    System.out.println(msgErrorIdNotFound);
                }
                break;
            case SUBTASK:
                if (subTasks.containsKey(idIssue)) {
                    SubTask subTask = (SubTask) getTaskForId(issueType, idIssue);
                    //Удаляем ребенка у эпика
                    subTask.getParent().deleteChild(subTask);
                    //Удаляем из мапы подзадачу
                    subTasks.remove(idIssue);
                } else {
                    System.out.println(msgErrorIdNotFound);
                }
                break;
            case EPIC:
                if (epics.containsKey(idIssue)) {
                    //удаляем подзадачи эпика
                    for (SubTask child : getSubTaskForEpic(epics.get(idIssue))) {
                        if (subTasks.containsValue(child)) {
                            subTasks.remove(child.getId());
                        }
                    }
                    epics.remove(idIssue);
                } else {
                    System.out.println(msgErrorIdNotFound);
                }
                break;
            default:
                System.out.println(msgErrorTypeUnKnow);
        }
    }

    /**
     * Метод для получения списка всех задач заданного типа
     *
     * @param issueType - тип задачи IssueType = {Task, SubTask, Epic}
     * @return возвращает список задач менеджера по заданному типу
     */
    public ArrayList<Issue> getAllTask(IssueType issueType) {
        ArrayList<Issue> issues = new ArrayList<>();

        if (issueType == null) {
            System.out.println(msgErrorTypeNull);
        } else if (issueType == IssueType.TASK) {
            issues.addAll(tasks.values());
        } else if (issueType == IssueType.EPIC) {
            issues.addAll(epics.values());
        } else if (issueType == IssueType.SUBTASK) {
            issues.addAll(subTasks.values());
        } else {
            System.out.println(msgErrorTypeUnKnow);
        }

        return issues;
    }

    /**
     * Метод удаляет все задачи по заданному типу
     *
     * @param issueType - тип задачи IssueType = {Task, SubTask, Epic}
     */
    public void deleteAllTask(IssueType issueType) {
        switch (issueType) {
            case TASK:
                this.tasks.clear();
                break;
            case EPIC:
                this.subTasks.clear(); // нет смысла оставлять подзадачи, если родители удалены
                this.epics.clear();
                break;
            case SUBTASK:
                for (Epic epic : epics.values()) {
                    epic.deleteChildren();
                }
                this.subTasks.clear();
                break;
            default:
                System.out.println(msgErrorTypeUnKnow);
        }
    }

    /**
     * Метод возвращает задачу по идентификатору.
     *
     * @param issueType - тип задачи IssueType = {Task, SubTask, Epic}
     * @param idIssue   - идентификатор задачи для поиска
     * @return - задачу по идентификатору, может вернуть null
     */
    public Issue getTaskForId(IssueType issueType, int idIssue) {

        if (idIssue < 0 || idIssue > this.id) {
            System.out.println(msgErrorIdNotFound);
        } else if (issueType == null) {
            System.out.println(msgErrorTypeNull);
        } else {
            switch (issueType) {
                case TASK:
                    return tasks.get(idIssue);
                case EPIC:
                    return epics.get(idIssue);
                case SUBTASK:
                    return subTasks.get(idIssue);
                default:
                    System.out.println(msgErrorTypeUnKnow);
            }
        }
        return null;
    }

    /**
     * Метод возвращает список всех подзадач определённого эпика.
     *
     * @param epic - эпик, по которому нужно получить список подзадач
     * @return - список подзадач эпика
     */
    public ArrayList<SubTask> getSubTaskForEpic(Epic epic) {
        return epic.getChildren();
    }

    /**
     * Метод обновляет данные в хранилище по задаче.
     *
     * @param issueType - тип задачи IssueType = {Task, SubTask, Epic}
     * @param issue     - новая версия объекта с верным идентификатором, включая обновленный статус
     */
    public void updateTask(IssueType issueType, Issue issue) {
        if (issue != null) {
            switch (issueType) {
                case TASK:
                    tasks.put(issue.getId(), (Task) issue);
                    break;
                case EPIC:
                    ((Epic) issue).setStatus();
                    epics.put(issue.getId(), (Epic) issue);
                    break;
                case SUBTASK:
                    //Родитель подзадачи - эпик
                    Epic parent = ((SubTask) issue).getParent();
                    //Удаляем из эпика ссылку на старую подзадачу
                    parent.deleteChild((SubTask) getTaskForId(IssueType.SUBTASK, issue.getId()));
                    //Обновляем подзадачу в списке подзадач
                    subTasks.put(issue.getId(), (SubTask) issue);
                    //Добавляем обновленную подзадачу в эпик
                    parent.addChild((SubTask) issue);
                    break;
                default:
                    System.out.println(msgErrorTypeUnKnow);
            }
        } else {
            System.out.println(msgErrorTaskNUll);
        }
    }

}
