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
 * <p>Описание сущности {@link Issue}:
 * <p>Название, кратко описывающее суть {@code Issue}.
 * <p>Описание, в котором раскрываются детали.
 * <p>Уникальный идентификационный номер сущности, по которому её можно будет найти.
 * <p>Статус, отображающий её прогресс.
 * <p>Мы будем выделять следующие этапы жизни сущности: {@code NEW},{@code IN_PROGRESS},{@code DONE}.
 */
public class InMemoryTaskManager implements TaskManager {

    // Переменные класса
    private int id = 1; // идентификатор менеджера
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, SubTask> subTasks = new HashMap<>();
    private final List<Issue> historyListOfViewIssue = new ArrayList<>();
    private final static byte SIZE_HISTORY_LIST_OF_VIEW_ISSUE = 10;

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
                        tasks.put(issue.getId(), newTask);
                    } else {
                        System.out.println(MSG_ERROR_ID_NOT_FOUND);
                    }
                    break;

                case EPIC:
                    Epic newEpic = (Epic) issue;
                    Epic oldEpic = (Epic) getIssueById(IssueType.EPIC, issue.getId());

                    if (oldEpic != null) {
                        //Удаляем все подзадачи старого эпика
                        for (SubTask child : oldEpic.getChildren()) {
                            subTasks.remove(child.getId());
                        }
                        //Обновляем эпик
                        epics.put(issue.getId(), (Epic) issue);
                        //Устанавливаем статус нового эпика
                        newEpic.updStatus();
                        //Добавляем подзадачи нового эпика
                        for (SubTask child : newEpic.getChildren()) {
                            if (subTasks.containsKey(child.getId())) {
                                updIssue(IssueType.SUBTASK, child);
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
                    SubTask oldSubTask  = (SubTask) getIssueById(IssueType.SUBTASK, issue.getId());

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
                if (tasks.containsKey(idIssue)) {
                    tasks.remove(idIssue);
                } else {
                    System.out.println(MSG_ERROR_ID_NOT_FOUND);
                }
                break;

            case SUBTASK:
                if (subTasks.containsKey(idIssue)) {
                    SubTask subTask = (SubTask) getIssueById(issueType, idIssue);
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
                issue = tasks.get(idIssue);
                break;
            case EPIC:
                issue = epics.get(idIssue);
                break;
            case SUBTASK:
                issue = subTasks.get(idIssue);
                break;
            default:
                System.out.println(MSG_ERROR_TYPE_UN_KNOW);
                return null;
        }

        //обновляем стек с историей просмотров
        if (historyListOfViewIssue.size() == SIZE_HISTORY_LIST_OF_VIEW_ISSUE) {
            historyListOfViewIssue.remove(1);
        }
        historyListOfViewIssue.add(issue);

        return issue;
    }

    /**
     * Получить список всех задач заданного типа.
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     * @return возвращает список задач менеджера по заданному типу
     */
    @Override
    public List<Issue> getAllIssues(IssueType issueType) {
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
        return epic.getChildren();
    }

    /**
     * Получить историю просмотров задач.
     *
     * @return список просмотренных задач = {Task, SubTask, Epic}
     */
    public List<Issue> getHistory() {
        return historyListOfViewIssue;
    }
}
