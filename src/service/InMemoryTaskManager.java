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
    private int getId() {
        return id++;
    }

    /**
     * Добавить задачу менеджеру. Сам объект передается в качестве параметра.
     *
     * @param task экземпляр класса {@link Task}
     */
    @Override
    public void addTask(Task task) {
        if (task != null) {
            //Добавляем только новые задачи с 0 идентификатором
            if (task.getId() == 0) {
                task.setId(getId());
                tasksMap.put(task.getId(), task);
            } else {
                System.out.println(MSG_ERROR_NOT_NEW);
            }
        } else {
            System.out.println(MSG_ERROR_NULL);
        }
    }

    /**
     * Добавить подзадачу менеджеру. Сам объект передается в качестве параметра.
     *
     * @param subTask экземпляр класса {@link SubTask}
     */
    @Override
    public void addSubTask(SubTask subTask) {
        if (subTask != null) {
            Epic parent = epicsMap.get(subTask.getParent().getId());
            if (subTask.getId() == 0 && parent != null) {
                List<SubTask> children = parent.getChildrenList();

                //Ставим подзадаче родителя из менеджера
                subTask.setParent(parent);

                //Устанавливаем новый свободный id
                subTask.setId(getId());

                //Помещаем подзадачу с корректным родителем
                subTasksMap.put(subTask.getId(), subTask);

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
    }

    /**
     * Добавить эпик менеджеру. Сам объект передается в качестве параметра.
     *
     * @param epic экземпляр класса {@link Epic}
     */
    @Override
    public void addEpic(Epic epic) {
        if (epic != null) {
            if (epic.getId() == 0) {
                List<SubTask> children = epic.getChildrenList();

                epic.setId(getId());
                epicsMap.put(epic.getId(), epic);

                //Проверяем наличие подзадач в хранилище менеджера, если не находим, то добавляем
                for (SubTask child : children) {
                    addSubTask(child);
                }
                //Состав подзадач не меняем, значит статус пересчитывать не нужно
            } else {
                System.out.println(MSG_ERROR_NOT_NEW);
            }
        } else {
            System.out.println(MSG_ERROR_NULL);
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
        SubTask oldSubTask = subTasksMap.get(subTask.getId());
        Epic newParent = epicsMap.get(subTask.getParent().getId());

        if (oldSubTask != null && newParent != null) {
            //берем корректного родителя их менеджера по id
            subTask.setParent(newParent);

            // обновляем подзадачу
            subTasksMap.put(subTask.getId(), subTask);

            //Удаляем старую подзадачу у старого эпика родителя
            oldSubTask.getParent().getChildrenList().remove(oldSubTask);

            //Обновляем статус старого родителя
            updateStatusEpic(oldSubTask.getParent());

            //Добавляем обновленную подзадачу в эпик
            if (!newParent.getChildrenList().contains(subTask)) {
                newParent.getChildrenList().add(subTask);
            }
            //Обновляем статус родителя
            updateStatusEpic(newParent);

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
        Epic oldEpic = epicsMap.get(epic.getId());

        if (oldEpic != null) {
            //при обновлении эпика его дети остаются "старыми"
            if (oldEpic.getChildrenList().equals((epic.getChildrenList()))) {
                // обновляем эпик
                epicsMap.put(oldEpic.getId(), epic);

                //меняем родителя у детей
                for (SubTask subTask : oldEpic.getChildrenList()) {
                    subTask.setParent(epic);
                }

                //Контроль статуса
                updateStatusEpic(epic);
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
            SubTask subTask = subTasksMap.get(id);
            if (subTask != null) {
                //Удаляем эту подзадачу в эпике
                subTask.getParent().getChildrenList().remove(subTask);
                //Обновляем статус родителя
                updateStatusEpic(subTask.getParent());
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
    ///////////////////////////////////////////////
    /**
     * Удалить все задачи {@link Task}
     */
    @Override
    public void deleteAllTasks() {
        tasksMap.clear();
    }

    /**
     * Удалить все подзадачи {@link SubTask} в менеджере, а также всех детей у эпиков
     */
    @Override
    public void deleteAllSubTasks() {
        subTasksMap.clear();
        for (Epic value : epicsMap.values()) {
            value.getChildrenList().clear();
        }
    }

    /**
     * Удалить все эпики {@link Epic}, а также все подзадачи, т.к. все родители удалены
     */
    @Override
    public void deleteAllEpics() {
        subTasksMap.clear();
        epicsMap.clear();
    }

    ///////////////////////////////////////////////
    /**
     * Получить задачу {@link Task} по id. Может вернуть null.
     * @param id - идентификатор задачи
     * @return задача типа {@link Task}. Если задача не найдена, то null
     */
    @Override
    public Task getTaskById(int id) {
        Task task = tasksMap.get(id);
        if (task != null) {
            historyManager.add(task);
            return task;
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
            return null;
        }
    }

    /**
     * Получить подзадачу {@link SubTask} по id. Может вернуть null.
     * @param id - идентификатор задачи
     * @return задача типа {@link SubTask}. Если задача не найдена, то null
     */
    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = subTasksMap.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
            return subTask;
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
            return null;
        }
    }

    /**
     * Получить эпик {@link Epic} по id. Может вернуть null.
     * @param id - идентификатор задачи
     * @return задача типа {@link Epic}. Если задача не найдена, то null
     */
    @Override
    public Epic getEpicById(int id) {
        Epic epic = epicsMap.get(id);
        if (epic != null) {
            historyManager.add(epic);
            return epic;
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
            return null;
        }
    }

    /**
     * Получить список всех задач менеджера.
     * @return список задач {@link Task}
     */
    @Override
    public List<Task> getListAllTasks() {
        return new ArrayList<>(tasksMap.values());
    }

    /**
     * Получить список всех подзадач менеджера.
     * @return список подзадач {@link SubTask}
     */
    @Override
    public List<SubTask> getListAllSubTasks() {
        return new ArrayList<>(subTasksMap.values());
    }

    /**
     * Получить список всех эпиков менеджера.
     * @return список эпиков {@link Epic}
     */
    @Override
    public List<Epic> getListAllEpics() {
        return new ArrayList<>(epicsMap.values());
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
        return new ArrayList<>(epic.getChildrenList());
    }

    /**
     * <b>Рассчитать статус эпика</b>
     * <p>Правило установки статуса эпика:
     * Если у эпика нет подзадач или все они имеют статус NEW, то статус должен быть NEW.
     * Если все подзадачи имеют статус DONE, то и эпик считается завершённым со статусом DONE.
     * Во всех остальных случаях статус должен быть IN_PROGRESS.
     */
    @Override
    public void updateStatusEpic(Epic epic) {

        if (epic.getChildrenList().size() == 0) {
            epic.setStatus(IssueStatus.NEW);
        } else {
            boolean allNew = true;
            boolean allDone = true;

            for (SubTask child : epic.getChildrenList()) {
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
     *
     * @return список просмотренных задач = {Task, SubTask, Epic}
     */
    @Override
    public List<Issue> getHistory() {
        return historyManager.getHistory();
    }

}
