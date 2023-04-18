package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
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
 * <p>  - Удаление всех задач.
 * <p>  - Получение по идентификатору.
 * <p>  - Создание. Сам объект должен передаваться в качестве параметра.
 * <p>  - Обновление. Новая версия объекта с верным идентификатором передаётся в виде параметра.
 * <p>  - Удаление по идентификатору.
 *
 * <p><b>Дополнительные методы:</b>
 * <p>  - Получение списка всех подзадач определённого эпика.
 * <p>  - Установка статуса эпика.
 */
public class InMemoryTaskManager implements TaskManager {

    protected int id = 1; // идентификатор менеджера
    protected final Map<Integer, Task> tasks = new HashMap<>();
    protected final Map<Integer, Epic> epics = new HashMap<>();
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();
    protected final HistoryManager historyManager = Managers.getDefaultHistory();

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
     * Синхронизировать id менеджера и id задачи
     * <p> Если задача передана с id=0, то берем следующий свободный идентификатор менеджера.
     * В противном случае берем максимальное значение id
     * @param issue - задача, добавляемая в хранилище менеджера
     * @return возвращает текущее значение идентификатора менеджера
     */
    private int setId(Issue issue) {

        if (issue.getId() == 0) {
            issue.setId(getId());
        } else {
            id = Math.max(issue.getId(), id);
            getId();
        }

        return id - 1;
    }

    /**
     * Добавить задачу менеджеру. Сам объект передается в качестве параметра.
     *
     * @param task экземпляр класса {@link Task}
     * @return новая задача типа {@link Task}. Если задача не найдена, то null
     */
    @Override
    public Task addTask(Task task) {

        if (task != null) {
            int idTask = setId(task);
            tasks.put(idTask, task);
        } else {
            System.out.println(MSG_ERROR_NULL);
        }

        return task;
    }

    /**
     * Добавить подзадачу менеджеру. Сам объект передается в качестве параметра.
     *
     * @param subTask экземпляр класса {@link SubTask}
     * @return новая задача типа {@link SubTask}. Если задача не найдена, то null
     */
    @Override
    public SubTask addSubTask(SubTask subTask) {
        if (subTask != null) {
            Epic parent = epics.get(subTask.getParentID());
            if (parent != null) {
                List<SubTask> children = parent.getChildren();

                //Устанавливаем новый свободный id
                int idSubTask= setId(subTask);

                //Помещаем подзадачу с корректным родителем в хранилище менеджера
                subTasks.put(idSubTask, subTask);

                //Добавляем родителю ребенка, если нужно
                if (!children.contains(subTask)) {
                    children.add(subTask);
                }
                //Обновляем статус родителя
                updateStatusEpic(parent);
            } else {
                System.out.println(MSG_ERROR_NOT_NEW);
            }
        } else {
            System.out.println(MSG_ERROR_NULL);
        }
        return subTask;
    }

    /**
     * Добавить эпик менеджеру. Сам объект передается в качестве параметра.
     *
     * @param epic экземпляр класса {@link Epic}
     * @return новая задача типа {@link Epic}. Если задача не найдена, то null
     */
    @Override
    public Epic addEpic(Epic epic) {
        if (epic != null) {
            List<SubTask> children = epic.getChildren();

            //Устанавливаем новый свободный id
            int idEpic = setId(epic);

            epics.put(idEpic, epic);

            //Проверяем наличие подзадач в хранилище менеджера, если не находим, то добавляем
            for (SubTask child : children) {
                addSubTask(child);
            }
            //Актуализируем статус, хоть он и не должен меняться, но вдруг, на входе, нам дали не корректный
            updateStatusEpic(epic);
        } else {
            System.out.println(MSG_ERROR_NULL);
        }
        return epic;
    }

    ///////////////////////////////////////////////
    /**
     * Обновить задачу. Новая версия объекта передается в качестве параметра.
     *
     * @param task новая версия задачи с верным идентификатором, включая обновленный статус
     * @return Обновленная задача типа {@link Task}. Если задача не найдена, то null
     */
    @Override
    public Task updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());

        //Мы можем обновить только существующий объект
        if (oldTask != null) {
            // обновляем задачу в менеджере
            tasks.put(oldTask.getId(), task);
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
        }
        return getTaskById(task.getId());
    }

    /**
     * Обновить подзадачу. Новая версия объекта передается в качестве параметра.
     *
     * @param subTask новая версия объекта с верным идентификатором, включая обновленный статус
     * @return Обновленная задача типа {@link SubTask}. Если задача не найдена, то null
     */
    @Override
    public SubTask updateSubTask(SubTask subTask) {
        SubTask oldSubTask = subTasks.get(subTask.getId());
        if (oldSubTask != null) {
            Epic newParent = epics.get(subTask.getParentID());
            Epic oldParent = epics.get(oldSubTask.getParentID());
            if (newParent != null) {
                // обновляем подзадачу
                subTasks.put(subTask.getId(), subTask);

                if (subTask.getParentID() != oldSubTask.getParentID()) {
                    //Удалить старую подзадачу у старого эпика родителя
                    oldParent.getChildren().remove(oldSubTask);
                    //Обновляем статус старого родителя
                    updateStatusEpic(oldParent);
                } else {
                    //Удаляем ссылку на старую задачу
                    newParent.getChildren().remove(oldSubTask);
                }
                //Добавляем обновленную подзадачу в эпик
                if (!newParent.getChildren().contains(subTask)) {
                    newParent.getChildren().add(subTask);
                }
                //Обновляем статус родителя
                updateStatusEpic(newParent);
            } else {
                System.out.println(MSG_ERROR_ID_NOT_FOUND);
            }
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
        }
        return getSubTaskById(subTask.getId());
    }

    /**
     * Обновить эпик. Новая версия объекта передается в качестве параметра.
     *
     * @param epic новая версия объекта с верным идентификатором
     * @return Обновленная задача типа {@link Epic}. Если задача не найдена, то null
     */
    @Override
    public Epic updateEpic(Epic epic) {
        Epic oldEpic = epics.get(epic.getId());

        if (oldEpic != null) {
            if (oldEpic.getChildren().equals((epic.getChildren()))) {
                // обновляем эпик
                epics.put(oldEpic.getId(), epic);
                //Контроль статуса
                updateStatusEpic(epic);
            } else {
                System.out.println(MSG_ERROR_WRONG_EPIC);
            }
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
        }
        return getEpicById(epic.getId());
    }

    ///////////////////////////////////////////////
    /**
     * Удалить задачу {@link Task} по id
     *
     * @param id идентификатор задачи
     * @return удаленная задача типа {@link Task}. Если задача не найдена, то null
     */
    @Override
    public Task deleteTaskById(int id) {
        if (tasks.containsKey(id)) {
            historyManager.remove(id);
            return tasks.remove(id);
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
            return null;
        }
    }

    /**
     * Удалить подзадачу {@link SubTask} по id
     *
     * @param id - идентификатор задачи
     * @return удаленная задача типа {@link SubTask}. Если задача не найдена, то null
     */
    @Override
    public SubTask deleteSubTaskById(int id) {
        if (subTasks.containsKey(id)) {
            SubTask delSubTask = subTasks.remove(id);
            //Обработать родителя удаляемой подзадачи
            Epic parent = getEpicById(delSubTask.getParentID());
            if (parent != null) {
                //Удаляем эту подзадачу в эпике
                parent.getChildren().remove(delSubTask);
                //Обновляем статус родителя
                updateStatusEpic(parent);
            }
            //Удаляем подзадачу из истории просмотров
            historyManager.remove(id);
            return delSubTask;
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
            return null;
        }
    }

    /**
     * Удалить эпик {@link SubTask} по id
     *
     * @param id - идентификатор задачи
     * @return удаленная задача типа {@link Epic}. Если задача не найдена, то null
     */
    @Override
    public Epic deleteEpicById(int id) {
        if (epics.containsKey(id)) {
            //Удалить подзадачи эпика в хранилище менеджера
            for (SubTask child : epics.get(id).getChildren()) {
               subTasks.remove(child.getId());
               historyManager.remove(child.getId());
            }
            //Удалить эпик из истории просмотров
            historyManager.remove(id);
            //Удалить эпик из хранилища менеджера
            return epics.remove(id);
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
            return null;
        }
    }

    ///////////////////////////////////////////////
    /**
     * Удалить все задачи {@link Task}
     */
    @Override
    public void deleteAllTasks() {
        for (Integer id : tasks.keySet()) {
            historyManager.remove(id);
        }
        tasks.clear();
    }

    /**
     * Удалить все подзадачи {@link SubTask} в менеджере, а также всех детей у эпиков
     */
    @Override
    public void deleteAllSubTasks() {
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();

        for (Epic epic : epics.values()) {
            epic.getChildren().clear();
            updateStatusEpic(epic);
        }
    }

    /**
     * Удалить все эпики {@link Epic}, а также все подзадачи, т.к. все родители удалены
     */
    @Override
    public void deleteAllEpics() {
        for (Integer id : subTasks.keySet()) {
            historyManager.remove(id);
        }
        subTasks.clear();

        for (Integer id : epics.keySet()) {
            historyManager.remove(id);
        }
        epics.clear();
    }

    ///////////////////////////////////////////////
    /**
     * Получить задачу {@link Task} по id. Может вернуть null.
     *
     * @param id - идентификатор задачи
     * @return задача типа {@link Task}. Если задача не найдена, то null
     */
    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return task;
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND + " в хранилище задач.");
            return null;
        }
    }

    /**
     * Получить подзадачу {@link SubTask} по id. Может вернуть null.
     *
     * @param id - идентификатор задачи
     * @return задача типа {@link SubTask}. Если задача не найдена, то null
     */
    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
            return subTask;
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND + " в хранилище подзадач.");
            return null;
        }
    }

    /**
     * Получить эпик {@link Epic} по id. Может вернуть null.
     *
     * @param id - идентификатор задачи
     * @return задача типа {@link Epic}. Если задача не найдена, то null
     */
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND + " в хранилище эпиков.");
            return null;
        }
    }

    ///////////////////////////////////////////////
    /**
     * Получить список всех задач менеджера.
     *
     * @return список задач {@link Task}
     */
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    /**
     * Получить список всех подзадач менеджера.
     *
     * @return список подзадач {@link SubTask}
     */
    @Override
    public List<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    /**
     * Получить список всех эпиков менеджера.
     *
     * @return список эпиков {@link Epic}
     */
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    ///////////////////////////////////////////////

    /**
     * <b>Получить список всех подзадач для эпика.</b>
     * @param id  идентификатор эпика, по которому нужно получить список детей
     * @return список подзадач эпика
     */
    @Override
    public List<SubTask> getChildrenOfEpicById(int id) {
        List<SubTask> children = new ArrayList<>();
        Epic epic = epics.get(id);

        if (epic != null) {
            children.addAll(epic.getChildren());
        }
        return children;
    }

    /**
     * <b>Рассчитать статус эпика</b>
     *
     * <p>Правило установки статуса эпика:
     * Если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
     * Если все подзадачи имеют статус DONE, то и эпик считается завершённым со статусом DONE.
     * Во всех остальных случаях статус должен быть IN_PROGRESS.
     *
     * @param epic - эпик с обновленным статусом
     */
    private void updateStatusEpic(Epic epic) {
        if (epic.getChildren().isEmpty()) {
            epic.setStatus(IssueStatus.NEW);
        } else {
            boolean allNew = true;
            boolean allDone = true;

            for (SubTask child : epic.getChildren()) {
                if (child.getStatus() != IssueStatus.NEW) {
                    allNew = false;
                }
                if (child.getStatus() != IssueStatus.DONE) {
                    allDone = false;
                }
                //Прерываем цикл, ничего нового мы дальше не узнаем
                if (!allNew && !allDone) {
                    break;
                }
            }
            if (allNew) {
                epic.setStatus(IssueStatus.NEW);
            } else if (allDone) {
                epic.setStatus(IssueStatus.DONE);
            } else {
                epic.setStatus(IssueStatus.IN_PROGRESS);
            }
        }
    }

    /**
     * Получить историю просмотров задач.
     * @return список просмотренных задач = {Task, SubTask, Epic}
     */
    @Override
    public List<Issue> getHistory() {
        return historyManager.getHistory();
    }
}
