package service;

import model.Epic;
import model.Issue;
import model.SubTask;
import model.Task;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Основной класс управления сущностями: {@code Task}, {@code SubTask}, {@code Epic} наследники класса {@code Issue}
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
    @Override
    public int getId() {
        return id++;
    }

    /**
     * Добавить задачу менеджеру. Сам объект передается в качестве параметра.
     *
     * @param task экземпляр класса {@link Task}
     */
    @Override
    public void addTask(Task task) {
        if (!tasksMap.containsValue(task) && task != null) {
            tasksMap.put(task.getId(), task);
        }
    }

    /**
     * Добавить подзадачу менеджеру. Сам объект передается в качестве параметра.
     *
     * @param subTask экземпляр класса {@link SubTask}
     */
    @Override
    public void addSubTask(SubTask subTask) {
        if (!subTasksMap.containsValue(subTask) && subTask != null) {
            Epic parent = subTask.getParent();
            List<SubTask> children = parent.getChildrenList();

            subTasksMap.put(subTask.getId(), subTask);

            //Добавляем родителя, если его нет в хранилище
            if (!epicsMap.containsValue(parent)) {
                addEpic(parent);
            }

            //Добавляем родителю ребенка, если нужно
            if (!children.contains(subTask)) {
                children.add(subTask);
            }

            //Обновляем статус родителя
            parent.updateStatus();
        }
    }

    /**
     * Добавить эпик менеджеру. Сам объект передается в качестве параметра.
     *
     * @param epic экземпляр класса {@link Epic}
     */
    @Override
    public void addEpic(Epic epic) {
        if (!epicsMap.containsValue(epic) && epic != null) {
            List<SubTask> children = epic.getChildrenList();

            epicsMap.put(epic.getId(), epic);

            //Проверяем наличие подзадач в хранилище менеджера, если не находим, то добавляем
            for (SubTask child : children) {
                addSubTask(child);
            }
            //Состав подзадач не меняем, значит статус пересчитывать не нужно
        }
    }

     /**
     * Обновить задачу. Новая версия объекта передается в качестве параметра.
     * @param task новая версия задачи с верным идентификатором, включая обновленный статус
     */
    @Override
    public void updateTask(Task task) {

        Task oldTask = getTaskById(task.getId());

        //Мы можем обновить только существующий объект
        if (oldTask != null) {
            // обновляем задачу в менеджере
            tasksMap.put(oldTask.getId(), task);
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
        }
    }

    /**
     * Обновить подзадачу. Новая версия объекта передается в качестве параметра.
     * @param subTask новая версия объекта с верным идентификатором, включая обновленный статус
     */
    @Override
    public void updateSubTask(SubTask subTask) {
        SubTask oldSubTask  = getSubTaskById(subTask.getId());

        if (oldSubTask != null) {
            if (epicsMap.containsValue(subTask.getParent())) {
                // обновляем подзадачу
                subTasksMap.put(subTask.getId(), subTask);

                //Удаляем старую подзадачу у эпика родителя
                oldSubTask.getParent().getChildrenList().remove(oldSubTask);

                //Обновляем статус старого родителя
                oldSubTask.getParent().updateStatus();

                //Добавляем обновленную подзадачу в эпик
                if (!subTask.getParent().getChildrenList().contains(subTask)) {
                    subTask.getParent().getChildrenList().add(subTask);
                }
                //Обновляем статус родителя
                subTask.getParent().updateStatus();

            } else {
                System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
            }
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
        }
    }

    /**
     * Обновить эпик. Новая версия объекта передается в качестве параметра.
     * @param epic новая версия объекта с верным идентификатором
     */
    @Override
    public void updateEpic(Epic epic) {
        Epic oldEpic = getEpicById(epic.getId());

        if (oldEpic != null) {
            //при обновлении эпика его дети остаются "старыми"
            if (oldEpic.getChildrenList().equals((epic.getChildrenList()))) {
                // обновляем эпик
                epicsMap.put(oldEpic.getId(), epic);

                //меняем родителя у детей
                for (SubTask subTask : oldEpic.getChildrenList()) {
                    subTask.setParent(epic);
                }
                //у обновляемого эпика состав детей не меняется, значит не меняется и статус
            } else {
                System.out.println(MSG_ERROR_WRONG_EPIC);
            }
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
        }
    }

    /**
     * Удалить задачу {@link Task} по id
     * @param id идентификатор задачи
     */
    @Override
    public void deleteTaskById(int id) {
        if (tasksMap.containsKey(id)) {
            tasksMap.remove(id);
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
        }
    }

    /**
     * Удалить подзадачу {@link SubTask} по id
     * @param id - идентификатор задачи
     */
    @Override
    public void deleteSubTaskById(int id) {
        if (subTasksMap.containsKey(id)) {
            SubTask subTask = getSubTaskById(id);
            if (subTask != null) {
                //Удаляем эту подзадачу в эпике
                subTask.getParent().getChildrenList().remove(subTask);
                //Обновляем статус родителя
                subTask.getParent().updateStatus();
                //Удаляем из менеджера подзадачу
                subTasksMap.remove(id);
            }
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
        }
    }

    /**
     * Удалить эпик {@link SubTask} по id
     * @param id - идентификатор задачи
     */
    @Override
    public void deleteEpicById(int id) {
        if (epicsMap.containsKey(id)) {
            //удаляем подзадачи эпика в менеджере
            for (SubTask child : epicsMap.get(id).getChildrenList()) {
                if (subTasksMap.containsValue(child)) {
                    subTasksMap.remove(child.getId());
                }
            }
            epicsMap.remove(id);
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
        }
    }

    /**
     * Получить задачу {@link Task} по id. Может вернуть null.
     * @param id - идентификатор задачи
     * @return задача типа {@link Task}. Если задача не найдена, то null
     */
    @Override
    public Task getTaskById(int id) {
        Task task = tasksMap.get(id);
        historyManager.add(task);
        return task;
    }

    /**
     * Получить подзадачу {@link SubTask} по id. Может вернуть null.
     * @param id - идентификатор задачи
     * @return задача типа {@link SubTask}. Если задача не найдена, то null
     */
    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasksMap.get(id);
        historyManager.add(subTask);
        return subTask;
    }

    /**
     * Получить эпик {@link Epic} по id. Может вернуть null.
     * @param id - идентификатор задачи
     * @return задача типа {@link Epic}. Если задача не найдена, то null
     */
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicsMap.get(id);
        historyManager.add(epic);
        return epic;
    }

    /**
     * Получить список всех задач менеджера.
     * @return список задач {@link Task}
     */
    @Override
    public List<Task> getListAllTasks() {
        List<Task> tasksList = new ArrayList<>();
        tasksList.addAll(tasksMap.values());

        return tasksList;
    }

    /**
     * Получить список всех подзадач менеджера.
     * @return список подзадач {@link SubTask}
     */
    @Override
    public List<SubTask> getListAllSubTasks() {
        List<SubTask> subTasksList = new ArrayList<>();
        subTasksList.addAll(subTasksMap.values());

        return subTasksList;
    }

    /**
     * Получить список всех эпиков менеджера.
     * @return список эпиков {@link Epic}
     */
    @Override
    public List<Epic> getListAllEpics() {
        List<Epic> epicList = new ArrayList<>();
        epicList.addAll(epicsMap.values());

        return epicList;
    }

    ///////////////////////////////////////////////
    /**
     * Удалить все задачи {@link Task}
     */
    @Override
    public void deleteAllTasks() {
        tasksMap.clear();
    }

    /**
     * Удалить все подзадачи {@link SubTask}
     */
    @Override
    public void deleteAllSubTasks() {
        subTasksMap.clear();
        epicsMap.clear();
    }

    /**
     * Удалить все эпики {@link Epic}
     */
    @Override
    public void deleteAllEpics() {
        subTasksMap.clear();
    }

    ///////////////////////////////////////////////
    /**
     * Получить список всех подзадач для эпика.
     *
     * @param epic эпик, по которому нужно получить список подзадач
     * @return список подзадач эпика
     */
    @Override
    public List<SubTask> getListSubTasksOfEpic(Epic epic) {
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
}
