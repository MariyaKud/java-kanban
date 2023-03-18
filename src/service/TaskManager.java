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
    String MSG_ERROR_NULL = "Метод не отработал. Вместо сущности в качестве параметра передан NULL";
    String MSG_ERROR_NOT_NEW = "Метод не отработал. В метод передана сущность с не годным id.";
    String MSG_ERROR_ID_NOT_FOUND = "Не найдена сущность с указанным id.";
    String MSG_ERROR_WRONG_EPIC = "Метод не отработал. При обновлении эпика дети не должны меняться.";

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
     *
     * @param task новая версия объекта с верным идентификатором, включая обновленный статус
     */
    void updateTask(Task task);

    /**
     * Обновить подзадачу. Новая версия объекта передается в качестве параметра.
     *
     * @param subTask новая версия объекта с верным идентификатором, включая обновленный статус
     */
    void updateSubTask(SubTask subTask);

    /**
     * Обновить эпик. Новая версия объекта передается в качестве параметра.
     *
     * @param epic новая версия объекта с верным идентификатором
     */
    void updateEpic(Epic epic);

    ///////////////////////////////////////////////
    /**
     * Удалить задачу {@link Task} по id
     *
     * @param id идентификатор задачи
     */
    void deleteTaskById(int id);

    /**
     * Удалить подзадачу {@link SubTask} по id
     *
     * @param id - идентификатор задачи
     */
    void deleteSubTaskById(int id);

    /**
     * Удалить эпик {@link SubTask} по id
     *
     * @param id - идентификатор задачи
     */
    void deleteEpicById(int id);
    ///////////////////////////////////////////////
    /**
     * Получить задачу {@link Task} по id. Может вернуть null.
     *
     * @param id - идентификатор задачи
     * @return задача типа {@link Task}. Если задача не найдена, то null
     */
    Task getTaskById(int id);

    /**
     * Получить подзадачу {@link SubTask} по id. Может вернуть null.
     *
     * @param id - идентификатор задачи
     * @return задача типа {@link SubTask}. Если задача не найдена, то null
     */
    SubTask getSubTaskById(int id);

    /**
     * Получить эпик {@link Epic} по id. Может вернуть null.
     *
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
     *
     * @return список задач {@link Task}
     */
    List<Task> getListAllTasks();

    /**
     * Получить список всех подзадач менеджера.
     *
     * @return список подзадач {@link SubTask}
     */
    List<SubTask> getListAllSubTasks();

    /**
     * Получить список всех эпиков менеджера.
     *
     * @return список эпиков {@link Epic}
     */
    List<Epic> getListAllEpics();

    ///////////////////////////////////////////////
    /**
     * <b>Получить список всех подзадач для эпика.</b>
     *
     * @param epic эпик, по которому нужно получить список подзадач
     * @return список подзадач эпика
     */
    List<SubTask> getListSubTasksOfEpic(Epic epic);

    /**
     * <b>Рассчитать статус эпика</b>
     *
     * <p>Правило установки статуса эпика:
     * Если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
     * Если все подзадачи имеют статус DONE, то и эпик считается завершённым со статусом DONE.
     * Во всех остальных случаях статус должен быть IN_PROGRESS.
     */
    void updateStatusEpic(Epic epic);

    ///////////////////////////////////////////////
    /**
     * Получить историю просмотров задач.
     *
     * @return список просмотренных задач = {Task, SubTask, Epic}
     */
    List<Issue> getHistory();
}
