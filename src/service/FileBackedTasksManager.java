package service;

import dao.IssueRepository;

import model.Epic;
import model.SubTask;
import model.Task;
import model.IssueStatus;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Менеджер задач управления сущностями: {@code Task}, {@code SubTask}, {@code Epic} наследники класса {@code Issue}
 * Хранит свои задачи в файле на диске, наследник класса {@code InMemoryTasksManager}
 * Поддерживает контракт {@code TasksManager}
 */
public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final int ITEM_DURATION = 15;
    private final File file;
    private final IssueRepository csvMakeRepository = Managers.getDefaultIssueRepository();

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public static void main(String[] args) {

        //Создаем экземпляр менеджера
        final String dirHome     = System.getProperty("user.home");
        final String nameFileCSV = "taskManager.csv";

        final File file = new File(dirHome,nameFileCSV);
        final FileBackedTasksManager fileBackedManager = new FileBackedTasksManager(file);

        //Считаем, что планирование задач идет в будущее
        LocalDateTime localDateTime = LocalDateTime.now();

        System.out.println("\nЗАПУЩЕН АВТО ТЕСТ.");
        System.out.println("Заполнение объекта менеджера данными....");
        Task newTask1 = new Task(0, "Task1", "Description", Duration.ofMinutes(10),
                                    localDateTime, IssueStatus.NEW);
        localDateTime = shiftTheTimer(localDateTime,newTask1.getDuration());
        fileBackedManager.addTask(newTask1);
        System.out.println("Добавлена задача: " + newTask1);

        Task newTask2 = new Task(0, "Task2", "Description", Duration.ofMinutes(20),
                                   localDateTime, IssueStatus.NEW);
        localDateTime = shiftTheTimer(localDateTime,newTask1.getDuration());
        fileBackedManager.addTask(newTask2);
        System.out.println("Добавлена задача: " + newTask2);

        newTask2.setStatus(IssueStatus.DONE);
        fileBackedManager.updateTask(newTask2);
        System.out.println("Установлен статус 'DONE' для задачи: " + newTask2);
        System.out.println("Добавили в историю задачу: " + newTask2);

        Epic newEpic = new Epic(0, "Epic1", "Description");
        fileBackedManager.addEpic(newEpic);
        System.out.println("Добавлен эпик: " + newEpic);

        SubTask newSubTask1 = new SubTask(0, "SubTask1", "Description", Duration.ofMinutes(15),
                                            localDateTime, newEpic.getId());
        localDateTime = shiftTheTimer(localDateTime,newTask1.getDuration());
        fileBackedManager.addSubTask(newSubTask1);
        System.out.println("Добавлена подзадача: " + newSubTask1);

        newSubTask1.setStatus(IssueStatus.DONE);
        fileBackedManager.updateSubTask(newSubTask1);
        System.out.println("Установлен статус 'DONE' для подзадачи: " + newSubTask1);
        System.out.println("Добавили в историю подзадачу: " + newSubTask1);

        SubTask newSubTask2 = new SubTask(0, "SubTask2", "Description", Duration.ofMinutes(15),
                                           localDateTime, newEpic.getId());
        fileBackedManager.addSubTask(newSubTask2);
        System.out.println("Добавлена подзадача: " + newSubTask2);
        fileBackedManager.getSubTaskById(newSubTask2.getId());
        System.out.println("Добавили в историю подзадачу: " + newSubTask2 + "\n");

        System.out.println("Состояние менеджера задач:");
        fileBackedManager.tasks.forEach((key,value) -> System.out.println(value));
        fileBackedManager.epics.forEach((key,value) -> System.out.println(value));
        System.out.println("\nСверим данные менеджера, с сохраненными данными в csv файле:");

        FileBackedTasksManager loadFromFileTracker = loadFromFile(file);

        System.out.println("Результат сравнения задач менеджера и задач загруженных из csv файла: " +
                loadFromFileTracker.tasks.equals(fileBackedManager.tasks));
        System.out.println("Результат сравнения подзадач менеджера и подзадач загруженных из csv файла: " +
                loadFromFileTracker.subTasks.equals(fileBackedManager.subTasks));
        System.out.println("Результат сравнения эпиков менеджера и эпиков загруженных из csv файла: " +
                loadFromFileTracker.epics.equals(fileBackedManager.epics));
        System.out.println("Результат сравнения истории просмотров менеджера и истории восстановленной из csv файла: " +
                loadFromFileTracker.getHistory().equals(fileBackedManager.getHistory()));

        System.out.println("\nАВТО ТЕСТ завершен.");
    }

    /**
     * Возвращает следующую свободную дату для планирования,
     * под каждую задачу закладываем отрезки кратные отрезку в сетке
     * @param startTime дата начала последней запланированной задачи
     * @param duration продолжительность последней задачи
     * @return подходящая дата для начала новой задачи
     */
    private static LocalDateTime shiftTheTimer(LocalDateTime startTime,Duration duration) {
        //смещаем всегда на интервал, кратный минимальному отрезку
        if (duration.toMinutes() % ITEM_DURATION == 0) {
            return startTime.plusMinutes(duration.toMinutes());
        } else {
            return startTime.plusMinutes((duration.dividedBy(Duration.ofMinutes(ITEM_DURATION)) + 1) * ITEM_DURATION);
        }
    }

    /**
     * Восстанавливает данные менеджера из файла при запуске программы
     * @param file csv-файл для хранения данных менеджера задач
     * @return Экземпляр класса {@link FileBackedTasksManager}. Может вернуть null, если не сможет
     * проинициализировать csv-файл
     */
    static FileBackedTasksManager loadFromFile(File file) {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        System.out.println("Выполняется загрузка данных из файла csv ..");
        fileBackedTasksManager.csvMakeRepository.load(fileBackedTasksManager, fileBackedTasksManager.file);

        return fileBackedTasksManager;
    }

    /**
     * Сохранить текущее состояние менеджера задач в файл
     */
    void save() {
        csvMakeRepository.save(this, file);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public Task addTaskWithID(Task task) {
        return super.addTaskWithID(task);
    }

    @Override
    public SubTask addSubTaskWithID(SubTask subTask) {
        return super.addSubTaskWithID(subTask);
    }

    @Override
    public Epic addEpicWithID(Epic epic) {
        return super.addEpicWithID(epic);
    }

    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) {
        super.addSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
        return subTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
        return epic;
    }

    @Override
    public Task deleteTaskById(int id) {
        Task task = super.deleteTaskById(id);
        save();
        return task;
    }

    @Override
    public SubTask deleteSubTaskById(int id) {
        SubTask subTask = super.deleteSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public Epic deleteEpicById(int id) {
        Epic epic = super.deleteEpicById(id);
        save();
        return epic;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubTasks() {
        super.deleteAllSubTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        SubTask subTask = super.getSubTaskById(id);
        save();
        return subTask;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }
}
