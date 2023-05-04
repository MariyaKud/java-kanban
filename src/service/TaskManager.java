package service;

import model.Epic;
import model.Issue;
import model.SubTask;
import model.Task;

import java.util.List;

/**
 * Контракт для классов объект-менеджер
 */
public interface TaskManager {

    //Текст сообщений об ошибках
    String MSG_ERROR_ID_NOT_FOUND = "Не найдена сущность с указанным id";
    String MSG_ERROR_WRONG_EPIC = "Метод не отработал. При обновлении эпика дети не должны меняться";

    ///////////////////////////////////////////////
    /**
     * Добавить задачу менеджеру. Сам объект передается в качестве параметра.
     * @param task экземпляр класса {@link Task}
     * @return - добавленная задача
     */
    Task addTask(Task task);

    /**
     * Добавить подзадачу менеджеру. Сам объект передается в качестве параметра.
     * @param subTask экземпляр класса {@link SubTask}
     * @return добавленная подзадача
     */
    SubTask addSubTask(SubTask subTask);

    /**
     * Добавить эпик менеджеру. Сам объект передается в качестве параметра.
     * @param epic экземпляр класса {@link Epic}
     * @return добавленный эпик
     */
    Epic addEpic(Epic epic);

    ///////////////////////////////////////////////
    /**
     * Обновить задачу. Новая версия объекта передается в качестве параметра.
     * @param task новая версия объекта с верным идентификатором, включая обновленный статус
     * @return - обновленная задача
     */
    Task updateTask(Task task);

    /**
     * Обновить подзадачу. Новая версия объекта передается в качестве параметра.
     * @param subTask новая версия объекта с верным идентификатором, включая обновленный статус
     * @return обновленная подзадача
     */
    SubTask updateSubTask(SubTask subTask);

    /**
     * Обновить эпик. Новая версия объекта передается в качестве параметра.
     * @param epic новая версия объекта с верным идентификатором
     * @return обновленный эпик
     */
    Epic updateEpic(Epic epic);

    ///////////////////////////////////////////////
    /**
     * Удалить задачу {@link Task} по id
     * @param id идентификатор задачи
     * @return - удаленная задача
     */
    Task deleteTaskById(int id);

    /**
     * Удалить подзадачу {@link SubTask} по id
     * @param id - идентификатор задачи
     * @return - удаленная подзадача
     */
    SubTask deleteSubTaskById(int id);

    /**
     * Удалить эпик {@link SubTask} по id
     * @param id - идентификатор задачи
     * @return - удаленный эпик
     */
    Epic deleteEpicById(int id);

    ///////////////////////////////////////////////
    /**
     * Удалить все задачи {@link Task}
     */
    void deleteAllTasks();

    /**
     * Удалить все подзадачи {@link SubTask}
     */
    void deleteAllSubTasks();

    /**
     * Удалить все эпики {@link Epic}
     */
    void deleteAllEpics();

    ///////////////////////////////////////////////
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
     * Получить список всех задач менеджера.
     * @return список задач {@link Task}
     */
    List<Task> getAllTasks();

    /**
     * Получить список всех подзадач менеджера.
     * @return список подзадач {@link SubTask}
     */
    List<SubTask> getAllSubTasks();

    /**
     * Получить список всех эпиков менеджера.
     * @return список эпиков {@link Epic}
     */
    List<Epic> getAllEpics();

    ///////////////////////////////////////////////
    /**
     * <b>Получить список всех подзадач для эпика.</b>
     * @param id  идентификатор эпика, по которому нужно получить список детей
     * @return список подзадач эпика
     */
    List<SubTask> getChildrenOfEpicById(int id);

    ///////////////////////////////////////////////
    /**
     * Получить историю просмотров задач.
     * @return список просмотренных задач = {Task, SubTask, Epic}
     */
    List<Issue> getHistory();

    /**
     * Возвращающий список задач и подзадач отсортированных по приоритету, то есть по startTime.
     * @return список задач и подзадач отсортированных по startTime
     */
    List<Issue> getPrioritizedTasks();
}
