package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Менеджер задач управления сущностями: {@code Task}, {@code SubTask}, {@code Epic} наследники класса {@code Issue}
 * Хранит свои задачи в оперативной памяти, наследник класса {@code InMemoryTasksManager}
 * Поддерживает контракт {@code TasksManager}
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

    protected int id = 1;                                            //Идентификатор менеджера
    protected final Map<Integer, Task> tasks = new HashMap<>();      //Задачи
    protected final Map<Integer, Epic> epics = new HashMap<>();      //Эпики
    protected final Map<Integer, SubTask> subTasks = new HashMap<>();//Подзадачи
    protected final HistoryManager historyManager;                   //История просмотров
    protected final TreeSet<Issue> issuesByPriority;                 //Задачи и подзадачи отсортированные по startTime

    //Временная сетка, разбитая на интервалы с признаком занято/свободно
    protected final Map<Instant,Boolean> grid = new HashMap<>();

    //Момент запуска программы, планируем в будущее
    private static final LocalDateTime START_MOMENT = LocalDateTime.now();

    //Интервала сетки в минутах. Ограничение - час должен быть кратен ITEM_GRID.
    private static final long ITEM_GRID = 15;

    //Глубина сетки в днях (год)
    private static final Duration SIZE_GRID = Duration.ofDays(365);

    //Заполняем сетку с начала текущего часа
    private static final LocalDateTime START_GRID_LOCAL = LocalDateTime.of(START_MOMENT.toLocalDate(),
                                                          LocalTime.of(START_MOMENT.getHour(), 0));
    //Первый интервал в сетке
    private static final Instant START_GRID = START_GRID_LOCAL.toInstant(ZoneOffset.UTC);

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager   = historyManager;
        this.issuesByPriority = new TreeSet<>((o1, o2) -> {
            if (o1.getStartTime().isBefore(o2.getStartTime())) {
                return 1;
            } else if (o1.getStartTime().isEqual(o2.getStartTime())) {
                return 0;
            } else {
                return -1;
            }
        });
    }

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
     * @param issue - задача, добавляемая в хранилище менеджера
     */
    private void synchronizeIDIssueANDManager(Issue issue) {
        if (issue != null && issue.getId()>=id) {
            id = issue.getId();
            getId();
        }
    }

    /**
     * Инициализируем сетку занятости, разбитую на интервалы размером ITEM_GRID.
     * При первой валидации заполняем сетку значением - false, т.е. все интервалы свободны.
     * Первый интервал в сетке - начало часа запуска программы.
     * Сетка заполняется на год вперед.
     */
    private void initGrid() {
        Instant endGrid  = START_GRID.plus(SIZE_GRID);
        Instant itemGrid = START_GRID;

        while (itemGrid.isBefore(endGrid)) {
            grid.put(itemGrid,false);
            itemGrid = itemGrid.plus(Duration.ofMinutes(ITEM_GRID));
        }
    }

    private Instant findNearestBorderOfGrid(LocalDateTime localDateTime, boolean inFuture) {
        int minutes = localDateTime.toLocalTime().getMinute();

        if (minutes % ITEM_GRID == 0) {
            return localDateTime.toInstant(ZoneOffset.UTC);
        } else {
            int minutesNearest;
            LocalTime localTime;
            if (inFuture) {
                minutesNearest =  (int) ((minutes / ITEM_GRID + 1) * ITEM_GRID);
            } else {
                minutesNearest = (int) (minutes / ITEM_GRID * ITEM_GRID);
            }
            if (minutesNearest == 60) {
                localTime =  LocalTime.of(localDateTime.getHour()+1, 0);
            } else {
                localTime = LocalTime.of(localDateTime.getHour() , minutesNearest);
            }
            return LocalDateTime.of(localDateTime.toLocalDate(),localTime).toInstant(ZoneOffset.UTC);
        }
    }

    /**
     * Если дата старта не задана, определяет задачу в конец списка задач, подзадач, отсортированных по startTime
     * @param issue - задача/подзадача для которой необходимо установить startTime
     */
    private void setStartTimeIFEmpty(Issue issue) {
        if ((issue.getType() == IssueType.TASK || issue.getType() == IssueType.SUBTASK)) {
            if (issue.getStartTime().isEqual(LocalDateTime.MIN)) {
                if (issuesByPriority.isEmpty()) {
                    Instant nearestFreeTime = findNearestBorderOfGrid(LocalDateTime.now(), true);
                    issue.setStartTime( LocalDateTime.ofInstant(nearestFreeTime, ZoneId.systemDefault()));
                } else {
                    Issue lastIssue = issuesByPriority.last();
                    issue.setStartTime(shiftTheTimer(lastIssue.getStartTime(),lastIssue.getDuration()));
                }
            }
        }
    }

    private Issue validatePeriodIssue(Issue issue) {
        boolean issueNotValid = false;

        //Инициализируем сетку при первой проверке валидности отрезка
        if (grid.isEmpty()) {
            initGrid();
        };

        //Устанавливаем дату старта для задачи/подзадачи, если она пустая
        setStartTimeIFEmpty(issue);

        //Проверяем, что в сетке есть место на заданный интервал
        if (issue.getType() != IssueType.EPIC) {

            List<Instant> itemToValidate = new ArrayList<>();
            Instant startIssue = findNearestBorderOfGrid(issue.getStartTime(), false);
            Instant endIssue   = findNearestBorderOfGrid(issue.getStartTime().plus(issue.getDuration()), true);
            while (startIssue.isBefore(endIssue)) {
                itemToValidate.add(startIssue);
                startIssue = startIssue.plus(Duration.ofMinutes(ITEM_GRID));
            }

            for (Instant instant : itemToValidate) {
                issueNotValid = grid.get(instant);
                if (issueNotValid) {
                    break;
                }
            }
        }

        return issueNotValid ? null : issue;
    }

    /**
     * Возвращает следующую свободную дату для планирования,
     * под каждую задачу закладываем отрезки кратные отрезку в сетке
     * @param startTime дата начала последней запланированной задачи
     * @param duration продолжительность последней задачи
     * @return подходящая дата для начала новой задачи
     */
    protected static LocalDateTime shiftTheTimer(LocalDateTime startTime,Duration duration) {
        //смещаем всегда на интервал, кратный минимальному отрезку
        if (duration.toMinutes() % ITEM_GRID == 0) {
            return startTime.plusMinutes(duration.toMinutes());
        } else {
            return startTime.plusMinutes((duration.dividedBy(Duration.ofMinutes(ITEM_GRID)) + 1) * ITEM_GRID);
        }
    }

    /**
     * Добавить задачу менеджеру, принудительно назначив ему следующий свободный id менеджера.
     * Сам объект передается в качестве параметра.
     * @param task экземпляр класса {@link Task}
     * @return Новая задача типа {@link Task}. Если задача не прошла валидацию, то null
     */
    @Override
    public Task addTask(Task task) {
        task.setId(getId());
        return addTaskWithID(task);
    }

    /**
     * Добавить задачу менеджеру, без изменения id.
     * Сам объект передается в качестве параметра.
     * @param task экземпляр класса {@link Task}
     * @return Новая задача типа {@link Task}. Если задача не прошла валидацию, то null
     * Валидация - задача не должна пересекать по времени, с другими задачами.
     */
    protected Task addTaskWithID(Task task) {
        task = (Task) validatePeriodIssue(task);
        if (task != null) {
            tasks.put(task.getId(), task);
            synchronizeIDIssueANDManager(task);
        } else {
            System.out.println(MSG_ERROR_NULL);
        }
        return task;
    }

    /**
     * Добавить подзадачу менеджеру, принудительно назначив ему следующий свободный id менеджера.
     * Сам объект передается в качестве параметра.
     * @param subTask экземпляр класса {@link SubTask}
     * @return Новая подзадача типа {@link SubTask}. Если подзадача не прошла валидацию, то null
     * Валидация - задача не должна пересекать по времени, с другими задачами.
     */
    @Override
    public SubTask addSubTask(SubTask subTask) {
        subTask.setId(getId());
        return addSubTaskWithID(subTask);
    }

    /**
     * Добавить подзадачу менеджеру, без изменения id.
     * Сам объект передается в качестве параметра.
     * @param subTask экземпляр класса {@link SubTask}
     * @return Новая задача типа {@link SubTask}. Если подзадача не прошла валидацию, то null
     */
    protected SubTask addSubTaskWithID(SubTask subTask) {
        subTask = (SubTask) validatePeriodIssue(subTask);
        if (subTask != null) {
            Epic parent = epics.get(subTask.getParentID());
            if (parent != null) {
                List<SubTask> children = parent.getChildren();

                //Помещаем подзадачу с корректным родителем в хранилище менеджера
                subTasks.put(subTask.getId(), subTask);
                synchronizeIDIssueANDManager(subTask);

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
     * Добавить эпик менеджеру, принудительно назначив ему следующий свободный id менеджера
     * Сам объект передается в качестве параметра.
     * @param epic экземпляр класса {@link Epic}
     * @return Новый эпик типа {@link Epic}. Вернет Null, если на вход передать Null
     */
    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(getId());
        return addEpicWithID(epic);
    }

    /**
     * Добавить эпик менеджеру как есть, без изменения id.
     * Сам объект передается в качестве параметра.
     * @param epic экземпляр класса {@link Epic}
     * @return Новый эпик типа {@link Epic}. Вернет Null, если на вход передать Null
     */
    protected Epic addEpicWithID(Epic epic) {
        if (epic != null) {
            List<SubTask> children = epic.getChildren();

            epics.put(epic.getId(), epic);
            synchronizeIDIssueANDManager(epic);

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
     * @return Обновленная задача типа {@link Task}. Если задача с заданным id не найдена, то null
     */
    @Override
    public Task updateTask(Task task) {
        Task oldTask = tasks.get(task.getId());

        //Мы можем обновить только существующий объект
        if (oldTask != null && validatePeriodIssue(task) != null) {
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
            if (newParent != null && validatePeriodIssue(subTask) != null) {
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

        updateDurationAndDateEpic(epic);

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
     * Рассчитать продолжительность эпика — сумма продолжительности всех его подзадач.
     * Рассчитать Время начала — дата старта самой ранней подзадачи.
     * Рассчитать время завершения — время окончания самой поздней из задач.
     * @param epic - эпик, для которого выполняется расчет временных характеристик
     */
    private void updateDurationAndDateEpic(Epic epic) {
        Duration durationEpic = Duration.ZERO;
        final LocalDateTime[] dateTime = {LocalDateTime.MAX,LocalDateTime.MIN};

        if (!epic.getChildren().isEmpty()) {
            for (SubTask child : epic.getChildren()) {
                durationEpic = durationEpic.plus(child.getDuration());
                if (dateTime[0].isAfter(child.getStartTime())) {
                    dateTime[0] = child.getStartTime();
                }
                if (dateTime[1].isBefore(child.getStartTime())) {
                    dateTime[1] = child.getStartTime();
                }
            }
            epic.setDuration(durationEpic);
            epic.setStartTime(dateTime[0]);
            epic.setEndTime(dateTime[1]);
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

    @Override
    public List<Issue> getPrioritizedTasks() {
        return new ArrayList<>(issuesByPriority);
    }
}
