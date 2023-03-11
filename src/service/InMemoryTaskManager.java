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
 * <p>Название, кратко описывающее суть {@code Issue}.
 * <p>Описание, в котором раскрываются детали.
 * <p>Уникальный идентификационный номер сущности, по которому её можно будет найти.
 * <p>Статус, отображающий её прогресс.
 * <p>Мы будем выделять следующие этапы жизни сущности: {@code NEW},{@code IN_PROGRESS},{@code DONE}.
 *
 * <p><b>Функции объекта-менеджера:</b>
 * <p> Возможность хранить задачи всех типов.
 * <p> Методы для каждого из типа Issue ({@code Task},{@code SubTask},{@code Epic}):
 * <p> Получение списка всех задач.
 * <p> Получение списка всех задач.
 * <p> Удаление всех задач.
 * <p> Получение по идентификатору.
 * <p> Создание. Сам объект должен передаваться в качестве параметра.
 * <p> Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
 * <p> Удаление по идентификатору.
 *
 * <p><b>Дополнительные методы:</b>
 * <p> Получение списка всех подзадач определённого эпика.
 */
public class InMemoryTaskManager implements TaskManager {

    // Переменные класса
    private int id = 1; // идентификатор менеджера
    private final Map<Integer, Task> tasksMap = new HashMap<>();
    private final Map<Integer, Epic> epicsMap = new HashMap<>();
    private final Map<Integer, SubTask> subTasksMap = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    //Текст сообщений об ошибках
    private final static String MSG_ERROR_TYPE_NULL = "Для метода не указан тип задачи.";
    private final static String MSG_ERROR_TYPE_UN_KNOW = "Для выбранного типа задач не создан обработчик в методе.";
    private final static String MSG_ERROR_ID_NOT_FOUND = "Не найдена задача с указанным id.";
    private final static String MSG_ERROR_TASK_NULL = "Данные по обновлению задачи пустые.";
    private final static String MSG_ERROR_NOT_FOUND_EPIC = "Менеджер не нашел родителя добавляемой подзадачи.";

    /**
     * Метод выдает очередной идентификатор для новой задачи,
     * <p> готовит идентификатор для следующей задачи.
     *
     * @return возвращает очередной свободный идентификатор
     */
    private int getId() {
        return id++;
    }

    public Task addTask(String title, String description, IssueStatus status) {
        return new Task(getId(), title, description, status);
    }

    public SubTask addSubTask(String title, String description, Epic parent, IssueStatus status) {
        return new SubTask(getId(), title, description, parent, status);
    }

    public Epic addEpic(String title, String description) {
        return new Epic(getId(), title, description);
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
     * Создать задачу. Сам объект передается в качестве параметра.
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     * @param issue     экземпляр класса Issue
     */
    @Override
    public void addIssue(IssueType issueType, Issue issue) {

        if (issue != null) {
            switch (issueType) {
                case TASK:
                    tasksMap.put(issue.getId(), (Task) issue);
                    break;

                case EPIC:
                    epicsMap.put(issue.getId(), (Epic) issue);
                    break;

                case SUBTASK:
                    SubTask newSubTask = (SubTask) issue;
                    Epic parent = newSubTask.getParent();
                    //Если родитель известен, то берем и его ребенка
                    if (epicsMap.containsValue(parent)) {
                        subTasksMap.put(issue.getId(), (SubTask) issue);
                        //добавляем родителю ребенка
                        parent.getChildrenList().add(newSubTask);
                    } else {
                        System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
                    }
                    break;

                default:
                    System.out.println(MSG_ERROR_TYPE_UN_KNOW);
            }
        } else {
            System.out.println(MSG_ERROR_TYPE_NULL);
        }
    }

    /**
     * Обновить задачу. Новая версия объекта передается в качестве параметра.
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     * @param issue     новая версия объекта с верным идентификатором, включая обновленный статус
     */
    @Override
    public void updIssue(IssueType issueType, Issue issue) {

        if (issue != null) {
            switch (issueType) {
                case TASK:
                    Task newTask = (Task) issue;
                    Task oldTask = (Task) getIssueById(IssueType.TASK, issue.getId());

                    //Мы можем обновить только существующий объект
                    if (oldTask != null) {
                        tasksMap.put(issue.getId(), newTask);
                    } else {
                        System.out.println(MSG_ERROR_ID_NOT_FOUND);
                    }
                    break;

                case EPIC:
                    Epic newEpic = (Epic) issue;
                    Epic oldEpic = (Epic) getIssueById(IssueType.EPIC, issue.getId());

                    if (oldEpic != null) {
                        //Удаляем все подзадачи старого эпика
                        for (SubTask child : oldEpic.getChildrenList()) {
                            subTasksMap.remove(child.getId());
                        }
                        //Обновляем эпик
                        epicsMap.put(issue.getId(), (Epic) issue);
                        //Устанавливаем статус нового эпика
                        newEpic.updStatus();
                        //Добавляем подзадачи нового эпика
                        for (SubTask child : newEpic.getChildrenList()) {
                            if (subTasksMap.containsKey(child.getId())) {
                                updIssue(IssueType.SUBTASK, child);
                            } else {
                                subTasksMap.put(child.getId(),child);
                            }
                        }
                    } else {
                        System.out.println(MSG_ERROR_ID_NOT_FOUND);
                    }
                    break;

                case SUBTASK:
                    SubTask newSubTask = (SubTask) issue;
                    SubTask oldSubTask  = (SubTask) getIssueById(IssueType.SUBTASK, issue.getId());

                    if (oldSubTask != null) {
                        //Родитель подзадачи - эпик
                        Epic parent = oldSubTask.getParent();
                        //Удаляем старую подзадачу у эпика родителя
                        parent.getChildrenList().remove(oldSubTask);
                        //Обновляем подзадачу в списке подзадач
                        subTasksMap.put(issue.getId(), newSubTask);
                        //Добавляем обновленную подзадачу в эпик
                        parent.getChildrenList().add(newSubTask);
                    } else {
                        System.out.println(MSG_ERROR_ID_NOT_FOUND);
                    }
                    break;

                default:
                    System.out.println(MSG_ERROR_TYPE_UN_KNOW);
            }
        } else {
            System.out.println(MSG_ERROR_TASK_NULL);
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
    public List<SubTask> getListSubTaskOfEpic(Epic epic) {
        return epic.getChildrenList();
    }

    /**
     * Получить историю просмотров задач.
     *
     * @return список просмотренных задач = {Task, SubTask, Epic}
     */
    public List<Issue> getHistory() {
        return historyManager.getHistory();
    }
}
