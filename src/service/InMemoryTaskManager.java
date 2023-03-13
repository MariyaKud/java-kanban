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
 * <p> - Название, кратко описывающее суть {@code Issue}.
 * <p> - Описание, в котором раскрываются детали.
 * <p> - Уникальный идентификационный номер сущности, по которому её можно будет найти.
 * <p> - Статус, отображающий её прогресс.
 * <p>Мы будем выделять следующие этапы жизни сущности: {@code NEW},{@code IN_PROGRESS},{@code DONE}.
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
    private int getId() {
        return id++;
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
     * Создать экземпляр класса {@link Task}
     * @param title заголовок
     * @param description описание
     * @param status - статус подзадачи {NEW, IN_PROGRESS,DONE}
     * @return новый экземпляр класса {@link Task}
     */
    @Override
    public Task addTask(String title, String description, IssueStatus status) {
        return new Task(getId(), title, description, status);
    }

    /**
     * Создать экземпляр класса {@link Task}
     * @param title заголовок
     * @param description описание
     * @return новый экземпляр класса {@link Task} со статусом NEW
     */
    @Override
    public Task addTask(String title, String description) {
        return new Task(getId(), title, description);
    }

    /**
     * Создать экземпляр класса {@link SubTask}
     * @param title заголовок
     * @param description описание
     * @param parent - владелец подзадачи, экземпляр класса {@link Epic}
     * @param status - статус подзадачи {NEW, IN_PROGRESS,DONE}
     * @return новый экземпляр класса {@link Epic}, без детей со статусом NEW
     */
    @Override
    public SubTask addSubTask(String title, String description, Epic parent, IssueStatus status) {
        if (parent == null) {
            parent = addEpic("Родитель для подзадачи: " + title, "");

        }
        SubTask newSubTask = new SubTask(getId(), title, description, parent, status);
        if (!parent.getChildrenList().contains(newSubTask)) {
            parent.getChildrenList().add(newSubTask);
        }
        return newSubTask;
    }

    /**
     * Создать экземпляр класса {@link Epic}
     * @param title заголовок
     * @param description описание
     * @return новый экземпляр класса Epic, без детей со статусом NEW
     */
    @Override
    public Epic addEpic(String title, String description) {
        return new Epic(getId(), title, description);
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
            parent.updStatus();
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
     * Добавить задачу менеджеру одного из типов: {@link Task},{@link SubTask},{@link Epic}
     * наследники класса {@link Issue}. Сам объект передается в качестве параметра.
     *
     * @param issue экземпляр класса Issue
     */
    @Override
    public void addIssue(Issue issue) {

        if (issue instanceof Task) {
            addTask((Task) issue);

        } else if (issue instanceof Epic) {
            addEpic((Epic) issue);

        } else if (issue instanceof SubTask) {
            addSubTask((SubTask) issue);

        } else {
            System.out.println(MSG_ERROR_TYPE_UN_KNOW);
        }
    }

    ///////////////////////////////////////////////
    /**
     * Обновить задачу для любого типа: {@link Task},{@link SubTask},{@link Epic} .
     * Новая версия объекта передается в качестве параметра.
     *
     * @param issue новая версия объекта с верным идентификатором, включая обновленный статус
     */
    @Override
    public void updIssue(Issue issue) {

        if (issue instanceof Task) {
            Task oldTask = (Task) getIssueById(IssueType.TASK, issue.getId());

            //Мы можем обновить только существующий объект
            if (oldTask != null) {
                tasksMap.put(oldTask.getId(), (Task) issue);        // обновляем задачу
            } else {
                System.out.println(MSG_ERROR_ID_NOT_FOUND);
            }

        } else if (issue instanceof Epic) {
            Epic oldEpic = (Epic) getIssueById(IssueType.EPIC, issue.getId());

            if (oldEpic != null) {
                //при обновлении эпика его дети остаются "старыми"
                if (oldEpic.getChildrenList().equals(((Epic) issue).getChildrenList())) {
                    epicsMap.put(oldEpic.getId(), (Epic) issue);    // обновляем эпик

                    //меняем родителя у детей
                    for (SubTask subTask : oldEpic.getChildrenList()) {
                        subTask.setParent((Epic) issue);
                    }
                    //у обновляемого эпика состав детей не меняется, значит не меняется и статус

                } else {
                    System.out.println(MSG_ERROR_WRONG_EPIC);
                }
            } else {
                System.out.println(MSG_ERROR_ID_NOT_FOUND);
            }

        } else if (issue instanceof SubTask) {
            SubTask oldSubTask  = (SubTask) getIssueById(IssueType.SUBTASK, issue.getId());

            if (oldSubTask != null) {
                Epic parent = ((SubTask) issue).getParent();
                if (epicsMap.containsValue(parent)) {
                    subTasksMap.put(issue.getId(), (SubTask) issue); // обновляем подзадачу

                    //Удаляем старую подзадачу у эпика родителя
                    oldSubTask.getParent().getChildrenList().remove(oldSubTask);
                    //Обновляем статус старого родителя
                    oldSubTask.getParent().updStatus();

                    //Добавляем обновленную подзадачу в эпик
                    if (!parent.getChildrenList().contains((SubTask) issue)) {
                        parent.getChildrenList().add((SubTask) issue);
                    }
                    //Обновляем статус нового родителя
                    parent.updStatus();

                } else {
                    System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
                }
            } else {
                System.out.println(MSG_ERROR_ID_NOT_FOUND);
            }
        } else {
            System.out.println(MSG_ERROR_TYPE_UN_KNOW);
        }
    }

    /**
     * Обновить задачу. Новая версия объекта передается в качестве параметра.
     * @param task новая версия задачи с верным идентификатором, включая обновленный статус
     */
    @Override
    public void updTask(Task task) {

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
    public void updSubTask(SubTask subTask) {
        SubTask oldSubTask  = getSubTaskById(subTask.getId());

        if (oldSubTask != null) {
            if (epicsMap.containsValue(subTask.getParent())) {
                // обновляем подзадачу
                subTasksMap.put(subTask.getId(), subTask);

                //Удаляем старую подзадачу у эпика родителя
                oldSubTask.getParent().getChildrenList().remove(oldSubTask);

                //Обновляем статус старого родителя
                oldSubTask.getParent().updStatus();

                //Добавляем обновленную подзадачу в эпик
                if (!subTask.getParent().getChildrenList().contains(subTask)) {
                    subTask.getParent().getChildrenList().add(subTask);
                }
                //Обновляем статус родителя
                subTask.getParent().updStatus();

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
    public void updEpic(Epic epic) {
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
     * Удалить задачу определенного типа {@link Task},{@link SubTask},{@link Epic} по id
     * @param issueType  тип задачи IssueType = {TASK, SUBTASK, EPIC}
     * @param idIssue    идентификатор задачи к удалению
     */
    @Override
    public void delIssueById(IssueType issueType, int idIssue) {

        switch (issueType) {
            case TASK:
                delTaskById(idIssue);
                break;

            case SUBTASK:
                delSubTaskById(idIssue);
                break;

            case EPIC:
                delEpicById(idIssue);
                break;

            default:
                System.out.println(MSG_ERROR_TYPE_UN_KNOW);
        }
    }

    /**
     * Удалить задачу {@link Task} по id
     * @param id идентификатор задачи
     */
    @Override
    public void delTaskById(int id) {
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
    public void delSubTaskById(int id) {
        if (subTasksMap.containsKey(id)) {
            SubTask subTask = (SubTask) getIssueById(IssueType.SUBTASK, id);
            if (subTask != null) {
                //Удаляем эту подзадачу в эпике
                subTask.getParent().getChildrenList().remove(subTask);
                //Обновляем статус родителя
                subTask.getParent().updStatus();
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
    public void delEpicById(int id) {
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
     * Получить задачу определенного типа {@link Task},{@link SubTask},{@link Epic} по id. Может вернуть null.
     * @param issueType  тип задачи IssueType = {TASK, SUBTASK, EPIC}
     * @param idIssue    идентификатор задачи
     * @return Задача запрошенного типа с указанным id. Если задача не найдена, то null
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

    ///////////////////////////////////////////////
    /**
     * Получить список всех задач заданного типа {@link Task},{@link SubTask},{@link Epic}
     *
     * @param issueType тип задачи IssueType = {Task, SubTask, Epic}
     * @return возвращает список задач менеджера по заданному типу
     */
    @Override
    public List<Issue> getListAllIssues(IssueType issueType) {
        List<Issue> issues = new ArrayList<>();

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
     * Удалить все задачи по заданному типу {@link Task},{@link SubTask},{@link Epic}
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
     * Удалить все задачи {@link Task}
     */
    @Override
    public void delAllTasks() {
        tasksMap.clear();
    }

    /**
     * Удалить все подзадачи {@link SubTask}
     */
    @Override
    public void delAllSubTasks() {
        subTasksMap.clear();
        epicsMap.clear();
    }

    /**
     * Удалить все эпики {@link Epic}
     */
    @Override
    public void delAllEpics() {
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
    public List<SubTask> getListSubTaskOfEpic(Epic epic) {
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

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("Manager{" + "\n");
        if (tasksMap.size() != 0) {
            result.append("TASK:\n");
            for (Task task : tasksMap.values()) {
                result.append("\t").append(task).append("\n");
            }
        }
        if (subTasksMap.size() != 0) {
            result.append("SUBTASK:\n");
            for (SubTask subtask: subTasksMap.values()) {
                result.append("\t").append(subtask).append("\n");
            }
        }
        if (epicsMap.size() != 0) {
            result.append("EPIC:\n");
            for (Epic epic : epicsMap.values()) {
                result.append("\t").append(epic).append("\n");
                if (epic.getChildrenList().size() != 0) {
                    for (SubTask subTask : epic.getChildrenList()) {
                        result.append("\t\t").append(subTask).append("\n");
                    }
                }
            }
        }
        result.append("}");

        return result.toString();
    }
}
