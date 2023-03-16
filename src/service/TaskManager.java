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
    String MSG_ERROR_ID_NOT_FOUND = "Не найдена задача с указанным id.";
    String MSG_ERROR_WRONG_EPIC = "При обновлении эпика дети остаются неизменными.";
    String MSG_ERROR_NOT_FOUND_EPIC = "Эпик не найден.";

    /**
     * Метод выдает очередной идентификатор для новой задачи,
     * <p> готовит идентификатор для следующей задачи.
     *
     * @return возвращает очередной свободный идентификатор
     */
    int getId();

    ///////////////////////////////////////////////
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

    ///////////////////////////////////////////////
    /**
     * Обновить задачу. Новая версия объекта передается в качестве параметра.
     * @param task новая версия объекта с верным идентификатором, включая обновленный статус
     */
    void updateTask(Task task);

    /**
     * Обновить подзадачу. Новая версия объекта передается в качестве параметра.
     * @param subTask новая версия объекта с верным идентификатором, включая обновленный статус
     */
    void updateSubTask(SubTask subTask);

    /**
     * Обновить эпик. Новая версия объекта передается в качестве параметра.
     * @param epic новая версия объекта с верным идентификатором
     */
    void updateEpic(Epic epic);

    ///////////////////////////////////////////////
    /**
     * Удалить задачу {@link Task} по id
     * @param id идентификатор задачи
     */
    void deleteTaskById(int id);

    /**
     * Удалить подзадачу {@link SubTask} по id
     * @param id - идентификатор задачи
     */
    void deleteSubTaskById(int id);

    /**
     * Удалить эпик {@link SubTask} по id
     * @param id - идентификатор задачи
     */
    void deleteEpicById(int id);
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
    List<SubTask> getListSubTasksOfEpic(Epic epic);

    ///////////////////////////////////////////////
    /**
     * Получить историю просмотров задач.
     *
     * @return список просмотренных задач = {Task, SubTask, Epic}
     */
    List<Issue> getHistory();
}
