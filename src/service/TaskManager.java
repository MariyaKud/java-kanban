package service;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import model.*;

/**
 * Основной класс работы с сущностями: {@code Task}, {@code SubTask}, {@code Epic}
 *
 */
public class TaskManager {

    // Переменные класса
    private int id = 1; //доступный идентификатор менеджера
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();

    //Текст сообщений об ошибках
    private final static String MSG_ERROR_TYPE_NULL = "Для метода не указан тип задачи.";
    private final static String MSG_ERROR_TYPE_UNKNOW = "Для выбранного типа задач не создан обработчик в методе.";
    private final static String MSG_ERROR_ID_NOT_FOUND = "Не найдена задача с указанным id.";
    private final static String MSG_ERROR_TASK_NULL = "Данные по обновлению задачи пустые.";
    private final static String MSG_ERROR_NOT_FOUND_EPIC = "Менеджер не нашел родителя добавляемой подзадачи.";

    /**
     * Метод выдает очередной идентификатор для новой задачи + готовит идентификатор для следующей задачи
     *
     * @return возвращает очередной свободный идентификатор
     */
    private int newId() {
        return id++;
    }

    /**
     * Метод для перезапуска менеджера:
     * очищает данные: задачи/подзадачи/эпики
     * id = 1
     */
    public void restartTaskManager() {
        deleteAllTask(IssueType.TASK);
        deleteAllTask(IssueType.EPIC);
        id = 1;
    }

    public Task initTask(String title, String description, IssueStatus status) {
        return new Task(newId(), title, description, status);
    }

    public SubTask initSubTask(String title,String description, Epic parent, IssueStatus status) {
        return new SubTask(newId(), title, description, parent, status);
    }

    public Epic initEpic(String title,String description) {
        return new Epic(newId(), title, description);
    }

    /**
     * Метод создает задачу. Сам объект передается в качестве параметра.
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     * @param issue     экземпляр класса Issue
     */
    public void createTask(IssueType issueType, Issue issue) {

        if (issue != null) {
            switch (issueType) {
                case TASK:
                    tasks.put(issue.getId(), (Task) issue);
                    break;

                case EPIC:
                    epics.put(issue.getId(), (Epic) issue);
                    break;

                case SUBTASK:
                    SubTask newSubTask = (SubTask) issue;
                    Epic parent = newSubTask.getParent();
                    //Если родитель известен, то берем и его ребенка
                    if (epics.containsValue(parent)) {
                        subTasks.put(issue.getId(), (SubTask) issue);
                        //добавляем родителю ребенка
                        parent.getChildren().add(newSubTask);
                    } else {
                        System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
                    }
                    break;

                default:
                    System.out.println(MSG_ERROR_TYPE_UNKNOW);
            }
        } else {
            System.out.println(MSG_ERROR_TYPE_NULL);
        }
    }

    /**
     * Метод удаляет задачу по идентификатору.
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     * @param idIssue   идентификатор задачи на удаление
     */
    public void deleteTask(IssueType issueType, int idIssue) {

        switch (issueType) {
            case TASK:
                if (tasks.containsKey(idIssue)) {
                    tasks.remove(idIssue);
                } else {
                    System.out.println(MSG_ERROR_ID_NOT_FOUND);
                }
                break;

            case SUBTASK:
                if (subTasks.containsKey(idIssue)) {
                    SubTask subTask = (SubTask) getTaskForId(issueType, idIssue);
                    if (subTask != null) {
                        //Удаляем эту подзадачу в эпике
                        subTask.getParent().getChildren().remove(subTask);
                        //Удаляем из менеджера подзадачу
                        subTasks.remove(idIssue);
                    }
                } else {
                    System.out.println(MSG_ERROR_ID_NOT_FOUND);
                }
                break;

            case EPIC:
                if (epics.containsKey(idIssue)) {
                    //удаляем подзадачи эпика в менеджере
                    for (SubTask child : epics.get(idIssue).getChildren()) {
                        if (subTasks.containsValue(child)) {
                            subTasks.remove(child.getId());
                        }
                    }
                    epics.remove(idIssue);
                } else {
                    System.out.println(MSG_ERROR_ID_NOT_FOUND);
                }
                break;

            default:
                System.out.println(MSG_ERROR_TYPE_UNKNOW);
        }
    }

    /**
     * Метод для получения списка всех задач заданного типа
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     * @return возвращает список задач менеджера по заданному типу
     */
    public List<Issue> getAllTask(IssueType issueType) {
        ArrayList<Issue> issues = new ArrayList<>();

        switch (issueType) {
            case TASK:
                issues.addAll(tasks.values());
                break;

            case EPIC:
                issues.addAll(epics.values());
                break;

            case SUBTASK:
                issues.addAll(subTasks.values());
                break;

            default:
                System.out.println(MSG_ERROR_TYPE_UNKNOW);
        }
        return issues;
    }

    /**
     * Метод удаляет все задачи по заданному типу
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     */
    public void deleteAllTask(IssueType issueType) {
        switch (issueType) {
            case TASK:
                tasks.clear();
                break;

            case EPIC:
                subTasks.clear(); // нет смысла оставлять подзадачи, если родители удалены
                epics.clear();
                break;

            case SUBTASK:
                for (Epic epic : epics.values()) {
                    epic.getChildren().clear();
                }
                subTasks.clear();
                break;

            default:
                System.out.println(MSG_ERROR_TYPE_UNKNOW);
        }
    }

    /**
     * Метод возвращает задачу по идентификатору или NULL
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     * @param idIssue   идентификатор задачи для поиска
     * @return - задачу по идентификатору, может вернуть NULL
     */
    public Issue getTaskForId(IssueType issueType, int idIssue) {
        switch (issueType) {
            case TASK:
                return tasks.get(idIssue);
            case EPIC:
                return epics.get(idIssue);
            case SUBTASK:
                return subTasks.get(idIssue);
            default:
                System.out.println(MSG_ERROR_TYPE_UNKNOW);
                return null;
        }
    }

    /**
     * Метод возвращает список всех подзадач определённого эпика.
     *
     * @param epic эпик, по которому нужно получить список подзадач
     * @return список подзадач эпика
     */
    public List<SubTask> getSubTaskForEpic(Epic epic) {
        return epic.getChildren();
    }

    /**
     * Метод обновляет данные в хранилище по задаче.
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     * @param issue     новая версия объекта с верным идентификатором, включая обновленный статус
     */
    public void updateTask(IssueType issueType, Issue issue) {

        if (issue != null) {
            switch (issueType) {
                case TASK:
                    Task newTask = (Task) issue;
                    Task oldTask = (Task) getTaskForId(IssueType.TASK, issue.getId());

                    //Мы можем обновить только существующий объект
                    if (oldTask != null) {
                        tasks.put(issue.getId(), newTask);
                    } else {
                        System.out.println(MSG_ERROR_ID_NOT_FOUND);
                    }
                    break;

                case EPIC:
                    Epic newEpic = (Epic) issue;
                    Epic oldEpic = (Epic) getTaskForId(IssueType.EPIC, issue.getId());

                    if (oldEpic != null) {
                        //Удаляем все подзадачи старого эпика
                        for (SubTask child : oldEpic.getChildren()) {
                            subTasks.remove(child.getId());
                        }
                        //Обновляем эпик
                        epics.put(issue.getId(), (Epic) issue);
                        //Устанавливаем статус нового эпика
                        newEpic.updateStatus();
                        //Добавляем подзадачи нового эпика
                        for (SubTask child : newEpic.getChildren()) {
                            if (subTasks.containsKey(child.getId())) {
                                updateTask(IssueType.SUBTASK, child);
                            } else {
                                subTasks.put(child.getId(),child);
                            }
                        }
                    } else {
                        System.out.println(MSG_ERROR_ID_NOT_FOUND);
                    }
                    break;

                case SUBTASK:
                    SubTask newSubTask = (SubTask) issue;
                    SubTask oldSubTask  = (SubTask) getTaskForId(IssueType.SUBTASK, issue.getId());

                    if (oldSubTask != null) {
                        //Родитель подзадачи - эпик
                        Epic parent = oldSubTask.getParent();
                        //Удаляем старую подзадачу у эпика родителя
                        parent.getChildren().remove(oldSubTask);
                        //Обновляем подзадачу в списке подзадач
                        subTasks.put(issue.getId(), newSubTask);
                        //Добавляем обновленную подзадачу в эпик
                        parent.getChildren().add(newSubTask);
                    } else {
                        System.out.println(MSG_ERROR_ID_NOT_FOUND);
                    }
                    break;

                default:
                    System.out.println(MSG_ERROR_TYPE_UNKNOW);
            }
        } else {
            System.out.println(MSG_ERROR_TASK_NULL);
        }
    }
}
