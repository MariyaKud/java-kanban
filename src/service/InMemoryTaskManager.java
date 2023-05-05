package service;

import exception.ValidateException;
import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    //Задачи и подзадачи отсортированные по startTime
    protected final TreeSet<Issue> issuesByPriority = new TreeSet<>(Comparator.comparing(Issue::getStartTime));

    //Момент запуска программы, планируем в будущее
    private static final LocalDateTime START_MOMENT = LocalDateTime.now();

    //Интервала сетки в минутах. Ограничение - час должен быть кратен ITEM_GRID.
    private static final long ITEM_GRID = 15;

    //Глубина сетки в днях (год)
    private static final Duration SIZE_GRID = Duration.ofDays(365);

    //Заполняем сетку с начала текущего часа
    private static final LocalDateTime START_GRID_LOCAL = LocalDateTime.of(START_MOMENT.toLocalDate(), LocalTime.of(START_MOMENT.getHour(), 0));
    //Первый интервал в сетке
    private static final Instant START_GRID = START_GRID_LOCAL.toInstant(ZoneOffset.UTC);

    //Временная сетка, разбитая на интервалы с признаком занято/свободно
    protected final Map<Instant, Boolean> grid = initGrid();

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
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
     *
     * @param issue - задача, добавляемая в хранилище менеджера
     */
    private void synchronizeIdIssueAndManager(Issue issue) {
        if (issue != null && issue.getId() >= id) {
            id = issue.getId();
            getId();
        }
    }

    /**
     * Добавить задачу менеджеру, принудительно назначив ему следующий свободный id менеджера.
     * Сам объект передается в качестве параметра.
     *
     * @param task экземпляр класса {@link Task}
     * @return Новая задача типа {@link Task}. Если задача не прошла валидацию, то null
     */
    @Override
    public Task addTask(Task task) {
        if (task != null) {
            task.setId(getId());
            return addTaskWithId(task);
        }
        return null;
    }

    /**
     * Добавить задачу менеджеру, без изменения id.
     * Сам объект передается в качестве параметра.
     *
     * @param task экземпляр класса {@link Task}
     * @return Новая задача типа {@link Task}. Если задача не прошла валидацию, то null
     * Валидация - задача не должна пересекать по времени, с другими задачами.
     */
    protected Task addTaskWithId(Task task) {
        try {
            List<Instant> itemsValid = validatePeriodIssue(task);

            tasks.put(task.getId(), task);
            issuesByPriority.add(task);
            synchronizeIdIssueAndManager(task);
            occupyItemsInGrid(itemsValid);

        } catch (ValidateException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return task;
    }

    /**
     * Добавить подзадачу менеджеру, принудительно назначив ему следующий свободный id менеджера.
     * Сам объект передается в качестве параметра.
     *
     * @param subTask экземпляр класса {@link SubTask}
     * @return Новая подзадача типа {@link SubTask}. Если подзадача не прошла валидацию, то null
     * Валидация - задача не должна пересекать по времени, с другими задачами.
     */
    @Override
    public SubTask addSubTask(SubTask subTask) {
        if (subTask != null) {
            subTask.setId(getId());
            return addSubTaskWithId(subTask);
        }
        return null;
    }

    /**
     * Добавить подзадачу менеджеру, без изменения id.
     * Сам объект передается в качестве параметра.
     *
     * @param subTask экземпляр класса {@link SubTask}
     * @return Новая задача типа {@link SubTask}. Если подзадача не прошла валидацию, то null
     */
    protected SubTask addSubTaskWithId(SubTask subTask) {
        try {
            List<Instant> itemsValid = validatePeriodIssue(subTask);
            Epic parent = epics.get(subTask.getParentID());
            if (parent != null) {
                List<SubTask> children = parent.getChildren();

                //Помещаем подзадачу с корректным родителем в хранилище менеджера
                subTasks.put(subTask.getId(), subTask);
                synchronizeIdIssueAndManager(subTask);

                //Добавляем родителю ребенка, если нужно
                if (!children.contains(subTask)) {
                    children.add(subTask);
                }
                //Обновляем статус родителя
                updateStatusEpic(parent);
                //Занимаем отрезки на сетке
                occupyItemsInGrid(itemsValid);
                issuesByPriority.add(subTask);
            }
        } catch (ValidateException e) {
            System.out.println(e.getMessage());
            return null;
        }
        return subTask;
    }

    /**
     * Добавить эпик менеджеру, принудительно назначив ему следующий свободный id менеджера
     * Сам объект передается в качестве параметра.
     *
     * @param epic экземпляр класса {@link Epic}
     * @return Новый эпик типа {@link Epic}. Вернет Null, если на вход передать Null
     */
    @Override
    public Epic addEpic(Epic epic) {
        if (epic != null) {
            epic.setId(getId());
            return addEpicWithId(epic);
        }
        return null;
    }

    /**
     * Добавить эпик менеджеру как есть, без изменения id.
     * Сам объект передается в качестве параметра.
     *
     * @param epic экземпляр класса {@link Epic}
     * @return Новый эпик типа {@link Epic}. Вернет Null, если на вход передать Null
     */
    protected Epic addEpicWithId(Epic epic) {
        List<SubTask> children = epic.getChildren();
        if (children.size() == 0) {
            epics.put(epic.getId(), epic);
            synchronizeIdIssueAndManager(epic);
            //новый эпик не содержит детей
            return epic;
        } else {
            return null;
        }
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
        List<Instant> itemsValid = null;
        final Task oldTask = tasks.get(task.getId());
        //Можно обновить только существующий объект
        if (oldTask != null) {
            //Проверка валидации нужна только если есть изменения в периоде
            if (!oldTask.getStartTime().equals(task.getStartTime()) || oldTask.getDuration() != task.getDuration()) {
                freeItemsInGrid(oldTask);
                try {
                    itemsValid = validatePeriodIssue(task);
                } catch (ValidateException e) {
                    itemsValid = validatePeriodIssue(oldTask);
                    occupyItemsInGrid(itemsValid);
                    System.out.println(e.getMessage());
                    return null;
                }
            }
            tasks.put(oldTask.getId(), task);
            issuesByPriority.add(task);
            if (itemsValid != null) {
                occupyItemsInGrid(itemsValid);
            }
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
        List<Instant> itemsValid = null;
        final SubTask oldSubTask = subTasks.get(subTask.getId());
        if (oldSubTask != null) {
            Epic newParent = epics.get(subTask.getParentID());
            Epic oldParent = epics.get(oldSubTask.getParentID());
            if (newParent != null) {
                //Проверка валидации нужна только если есть изменения в периоде
                if (!oldSubTask.getStartTime().equals(subTask.getStartTime()) || oldSubTask.getDuration() != subTask.getDuration()) {
                    freeItemsInGrid(oldSubTask);
                    try {
                        itemsValid = validatePeriodIssue(subTask);
                    } catch (ValidateException e) {
                        itemsValid = validatePeriodIssue(oldSubTask);
                        occupyItemsInGrid(itemsValid);
                        System.out.println(e.getMessage());
                        return null;
                    }
                }
                // обновляем подзадачу
                subTasks.put(subTask.getId(), subTask);
                issuesByPriority.add(subTask);

                if (itemsValid != null) {
                    occupyItemsInGrid(itemsValid);
                }

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
            freeItemsInGrid(tasks.get(id));
            historyManager.remove(id);
            issuesByPriority.remove(tasks.get(id));
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
            freeItemsInGrid(subTasks.get(id));
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
            issuesByPriority.remove(delSubTask);
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
            Epic delEpic = epics.remove(id);
            //Удалить подзадачи эпика в хранилище менеджера
            for (SubTask child : delEpic.getChildren()) {
                SubTask delSubTask = subTasks.remove(child.getId());
                historyManager.remove(child.getId());
                issuesByPriority.remove(delSubTask);
                freeItemsInGrid(delSubTask);
            }
            //Удалить эпик из истории просмотров
            historyManager.remove(id);
            issuesByPriority.remove(delEpic);
            //Удалить эпик из хранилища менеджера
            return delEpic;
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
            issuesByPriority.remove(tasks.get(id));
            freeItemsInGrid(tasks.get(id));
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
            issuesByPriority.remove(subTasks.get(id));
            freeItemsInGrid(subTasks.get(id));
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
            issuesByPriority.remove(subTasks.get(id));
            freeItemsInGrid(subTasks.get(id));
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
     *
     * @param id идентификатор эпика, по которому нужно получить список детей
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
     *
     * @param epic - эпик, для которого выполняется расчет временных характеристик
     */
    private void updateDurationAndDateEpic(Epic epic) {
        int durationEpic = 0;
        final Instant[] dateTime = {Instant.MAX, Instant.MIN};

        if (!epic.getChildren().isEmpty()) {
            for (SubTask child : epic.getChildren()) {
                durationEpic = durationEpic + child.getDuration();
                if (dateTime[0].isAfter(child.getStartTime())) {
                    dateTime[0] = child.getStartTime();
                }
                if (dateTime[1].isBefore(child.getEndTime())) {
                    dateTime[1] = child.getEndTime();
                }
            }
            epic.setDuration(durationEpic);
            epic.setStartTime(dateTime[0]);
            epic.setEndTime(dateTime[1]);
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

    /**
     * Возвращает отсортированный список задач по приоритету
     *
     * @return отсортированный список задач и подзадачи по дате старта
     */
    @Override
    public List<Issue> getPrioritizedTasks() {
        return new ArrayList<>(issuesByPriority);
    }

    /**
     * Проверяет валидность временных характеристик задачи.
     * Весь интервал задачи разбивается на отрезки, для каждого отрезка проверяется занят он на временной сетке или нет.
     * Если занят хотя бы один отрезок, то валидация не пройдена и будет возвращен пустой список
     *
     * @param issue задача любого типа
     * @return интервал задачи разбитый на интервалы длинной минимального отрезка сетки
     */
    private List<Instant> validatePeriodIssue(Issue issue) throws ValidateException {
        List<Instant> itemsValid = new ArrayList<>();

        if (issue == null) {
            throw new ValidateException("На проверку валидации передан null");
        }

        //Устанавливаем дату старта для задачи/подзадачи, если она пустая
        setStartTimeIfEmpty(issue);

        //Проверяем, что в сетке есть место на заданный интервал
        if (issue.getType() != IssueType.EPIC) {
            Instant startIssue = findNearestBorderOfGrid(issue.getStartTime(), false);
            Instant endIssue = findNearestBorderOfGrid(issue.getStartTime()
                    .plusSeconds(issue.getDuration() * 60L), true);
            while (startIssue.isBefore(endIssue)) {
                if (grid.containsKey(startIssue) && !grid.get(startIssue)) {
                    itemsValid.add(startIssue);
                    startIssue = startIssue.plus(Duration.ofMinutes(ITEM_GRID));
                } else {
                    //Если хотя юы один отрезок занят, то весь интервал считаем не валидным
                    throw new ValidateException("Не пройдена валидация по задаче: " + issue);
                }
            }
        }
        return itemsValid;
    }

    /**
     * Если дата старта не задана, определяет задачу в конец списка задач, подзадач, отсортированных по startTime
     *
     * @param issue - задача/подзадача для которой необходимо установить startTime
     */
    private void setStartTimeIfEmpty(Issue issue) {
        if ((issue.getType() != IssueType.EPIC)) {
            if (issue.getStartTime().equals(Instant.MIN)) {
                if (issuesByPriority.isEmpty()) {
                    issue.setStartTime(findNearestBorderOfGrid(START_GRID.plus(Duration.ofMinutes(ITEM_GRID)), true));
                } else {
                    Issue lastIssue = issuesByPriority.last();
                    issue.setStartTime(findNearestBorderOfGrid(lastIssue.getStartTime().plusSeconds(lastIssue.getDuration() * 60L), true));
                }
            }
        }
    }

    /**
     * Находит ближайшую границу сетки в прошлое или в будущее к переданному моменту времени
     *
     * @param instant  момент времени
     * @param inFuture в будущее
     * @return момент времени кратный интервалу сетки доступа на временной оси
     */
    private Instant findNearestBorderOfGrid(Instant instant, boolean inFuture) {
        OffsetDateTime localDateTime = Objects.requireNonNull(instant).atOffset(ZoneOffset.UTC);
        int minutes = localDateTime.toLocalTime().getMinute();

        if (minutes % ITEM_GRID == 0) {
            return instant;
        } else {
            LocalDate localDate = localDateTime.toLocalDate();
            int hours = localDateTime.toLocalTime().getHour();
            int minutesNearest;

            if (inFuture) {
                minutesNearest = (int) ((minutes / ITEM_GRID + 1) * ITEM_GRID);
            } else {
                minutesNearest = (int) (minutes / ITEM_GRID * ITEM_GRID);
            }
            if (minutesNearest == 60) {
                hours = hours + 1;
                minutesNearest = 0;
            }
            if (hours == 24) {
                hours = 0;
                localDate = localDate.plusDays(1);
            }

            return LocalDateTime.of(localDate, LocalTime.of(hours, minutesNearest)).toInstant(ZoneOffset.UTC);
        }
    }

    /**
     * Занять на временной сетке переданный список отрезков
     *
     * @param instants список отрезков на временной оси
     */
    private void occupyItemsInGrid(List<Instant> instants) {
        for (Instant instant : instants) {
            grid.put(instant, true);
        }
    }

    /**
     * Освободить на сетке занятые задачей отрезки времени
     *
     * @param issue задача или подзадача
     */
    private void freeItemsInGrid(Issue issue) {
        Instant startIssue = findNearestBorderOfGrid(issue.getStartTime(), false);
        Instant endIssue = findNearestBorderOfGrid(issue.getStartTime().plusSeconds(issue.getDuration() * 60L), true);
        while (startIssue.isBefore(endIssue)) {
            grid.put(startIssue, false);
            startIssue = startIssue.plus(Duration.ofMinutes(ITEM_GRID));
        }
    }

    /**
     * Инициализируем сетку занятости, разбитую на интервалы размером ITEM_GRID.
     * При первой валидации заполняем сетку значением - false, т.е. все интервалы свободны.
     * Первый интервал в сетке - начало часа запуска программы.
     * Сетка заполняется на год вперед.
     */
    private Map<Instant, Boolean> initGrid() {
        Map<Instant, Boolean> itemsGrid = new HashMap<>();
        Instant endGrid = START_GRID.plus(SIZE_GRID);
        Instant itemGrid = START_GRID;

        while (itemGrid.isBefore(endGrid)) {
            itemsGrid.put(itemGrid, false);
            itemGrid = itemGrid.plus(Duration.ofMinutes(ITEM_GRID));
        }
        return itemsGrid;
    }
}
