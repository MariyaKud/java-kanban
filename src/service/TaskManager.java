package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;

import java.util.List;

/**
 * Контракт для классов объект-менеджер
 */
public interface TaskManager {

    //Текст сообщений об ошибках
    String MSG_ERROR_TYPE_UN_KNOW = "Для выбранного типа задач не создан обработчик в методе.";
    String MSG_ERROR_ID_NOT_FOUND = "Не найдена задача с указанным id.";
    String MSG_ERROR_TASK_EMPTY = "Список задач пуст.";
    String MSG_ERROR_WRONG_EPIC = "При обновлении эпика дети остаются неизменными.";
    String MSG_ERROR_NOT_FOUND_EPIC = "Эпик не найден.";
    String MSG_ERROR_FOR_METHOD = "Возникла проблема при проверке метода.";

    ///////////////////////////////////////////////
    /**
     * Создать экземпляр класса {@link Task}
     * @param title заголовок
     * @param description описание
     * @param status - статус подзадачи {NEW, IN_PROGRESS,DONE}
     * @return новый экземпляр класса {@link Task}
     */
    Task addTask(String title, String description, IssueStatus status);

    /**
     * Создать экземпляр класса {@link Task}
     * @param title заголовок
     * @param description описание
     * @return новый экземпляр класса {@link Task} со статусом NEW
     */
    Task addTask(String title, String description);

    /**
     * Создать экземпляр класса {@link SubTask}
     * @param title заголовок
     * @param description описание
     * @param parent - владелец подзадачи, экземпляр класса {@link Epic}
     * @param status - статус подзадачи {NEW, IN_PROGRESS,DONE}
     * @return новый экземпляр класса {@link Epic}, без детей со статусом NEW
     */
    SubTask addSubTask(String title, String description, Epic parent, IssueStatus status);

    /**
     * Создать экземпляр класса {@link Epic}
     * @param title заголовок
     * @param description описание
     * @return новый экземпляр класса Epic, без детей со статусом NEW
     */
    Epic addEpic(String title, String description);

    /**
     * Добавить задачу менеджеру. Сам объект передается в качестве параметра.
     *
     * @param task экземпляр класса {@link Task}
     */
    void addTask(Task task);

    /**
     * Добавить подзадачу менеджеру. Сам объект передается в качестве параметра.
     *
     * @param subTask экземпляр класса {@link SubTask}
     */
    void addSubTask(SubTask subTask);

    /**
     * Добавить эпик менеджеру. Сам объект передается в качестве параметра.
     *
     * @param epic экземпляр класса {@link Epic}
     */
    void addEpic(Epic epic);

    /**
     * Добавить задачу менеджеру одного из типов: {@link Task},{@link SubTask},{@link Epic}
     * наследники класса {@link Issue}. Сам объект передается в качестве параметра.
     *
     * @param issue экземпляр класса Issue
     */
    void addIssue(Issue issue);

    ///////////////////////////////////////////////
    /**
     * Обновить задачу одного из типов: {@link Task},{@link SubTask},{@link Epic}
     * Новая версия объекта передается в качестве параметра.
     *
     * @param issue новая версия объекта с верным идентификатором, включая обновленный статус
     */
    void updIssue(Issue issue);

    /**
     * Обновить задачу. Новая версия объекта передается в качестве параметра.
     * @param task новая версия объекта с верным идентификатором, включая обновленный статус
     */
    void updTask(Task task);

    /**
     * Обновить подзадачу. Новая версия объекта передается в качестве параметра.
     * @param subTask новая версия объекта с верным идентификатором, включая обновленный статус
     */
    void updSubTask(SubTask subTask);

    /**
     * Обновить эпик. Новая версия объекта передается в качестве параметра.
     * @param epic новая версия объекта с верным идентификатором
     */
    void updEpic(Epic epic);

    ///////////////////////////////////////////////
    /**
     * Удалить задачу определенного типа {@link Task},{@link SubTask},{@link Epic} по id
     * @param issueType  тип задачи IssueType = {TASK, SUBTASK, EPIC}
     * @param idIssue    идентификатор задачи к удалению
     */
    void delIssueById(IssueType issueType, int idIssue);

    /**
     * Удалить задачу {@link Task} по id
     * @param id идентификатор задачи
     */
    void delTaskById(int id);

    /**
     * Удалить подзадачу {@link SubTask} по id
     * @param id - идентификатор задачи
     */
    void delSubTaskById(int id);

    /**
     * Удалить эпик {@link SubTask} по id
     * @param id - идентификатор задачи
     */
    void delEpicById(int id);
    ///////////////////////////////////////////////
    /**
     * Получить задачу определенного типа {@link Task},{@link SubTask},{@link Epic} по id. Может вернуть null.
     * @param issueType  тип задачи IssueType = {TASK, SUBTASK, EPIC}
     * @param idIssue    идентификатор задачи
     * @return Задача запрошенного типа с указанным id. Если задача не найдена, то null
     */
    Issue getIssueById(IssueType issueType, int idIssue);

    /**
     * Получить задачу {@link Task} по id. Может вернуть null.
     * @param id - идентификатор задачи
     * @return задача типа {@link Task}. Если задача не найдена, то null
     */
    Task getTaskById(int id);

    /**
     * Получить подзадачу {@link SubTask} по id. Может вернуть null.
     * @param id - идентификатор задачи
     * @return задача типа {@link SubTask}. Если задача не найдена, то null
     */
    SubTask getSubTaskById(int id);

    /**
     * Получить эпик {@link Epic} по id. Может вернуть null.
     * @param id - идентификатор задачи
     * @return задача типа {@link Epic}. Если задача не найдена, то null
     */
    Epic getEpicById(int id);

    ///////////////////////////////////////////////
    /**
     * Удалить все задачи по заданному типу {@link Task},{@link SubTask},{@link Epic}
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     */
    void delAllIssues(IssueType issueType);

    /**
     * Удалить все задачи {@link Task}
     */
    void delAllTasks();

    /**
     * Удалить все подзадачи {@link SubTask}
     */
    void delAllSubTasks();

    /**
     * Удалить все эпики {@link Epic}
     */
    void delAllEpics();

    ///////////////////////////////////////////////
    /**
     * Получить список всех задач заданного типа {@link Task},{@link SubTask},{@link Epic}
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     * @return возвращает список задач менеджера по заданному типу
     */
    List<Issue> getListAllIssues(IssueType issueType);

    /**
     * Получить список всех задач менеджера.
     * @return список задач {@link Task}
     */
    List<Task> getListAllTasks();

    /**
     * Получить список всех подзадач менеджера.
     * @return список подзадач {@link SubTask}
     */
    List<SubTask> getListAllSubTasks();

    /**
     * Получить список всех эпиков менеджера.
     * @return список эпиков {@link Epic}
     */
    List<Epic> getListAllEpics();

    ///////////////////////////////////////////////
    /**
     * Получить список всех подзадач для эпика.
     *
     * @param epic эпик, по которому нужно получить список подзадач
     * @return список подзадач эпика
     */
    List<SubTask> getListSubTaskOfEpic(Epic epic);

    ///////////////////////////////////////////////
    /**
     * Получить историю просмотров задач.
     *
     * @return список просмотренных задач = {Task, SubTask, Epic}
     */
    List<Issue> getHistory();
}
