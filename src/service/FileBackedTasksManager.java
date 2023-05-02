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

    private final File file;
    private final IssueRepository csvMakeRepository = Managers.getDefaultIssueRepository();

    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static void main(String[] args) {

        //Создаем экземпляр менеджера
        final String dirHome     = System.getProperty("user.home");
        final String nameFileCSV = "taskManager.csv";

        final File file = new File(dirHome,nameFileCSV);
        final FileBackedTasksManager fileBackedManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);


        System.out.println("ЗАПУЩЕН АВТО ТЕСТ FileBackedTasksManager.");
        System.out.println("Заполнение объекта менеджера данными....");
        Task newTask1 = new Task(0, "Task1", "Description", Duration.ofMinutes(10));
        if (fileBackedManager.addTask(newTask1) != null) {
            System.out.println("Добавлена задача: " + newTask1);
        }

        Task newTask2 = new Task(0, "Task2", "Description", Duration.ofMinutes(20));
        if (fileBackedManager.addTask(newTask2) != null) {
            System.out.println("Добавлена задача: " + newTask2);
        }

        newTask2.setStatus(IssueStatus.DONE);
        if (fileBackedManager.updateTask(newTask2) != null) {
            System.out.println("Установлен статус 'DONE' для задачи: " + newTask2);
            System.out.println("Добавили в историю задачу: " + newTask2);
        }

        Epic newEpic = new Epic(0, "Epic1", "Description");
        fileBackedManager.addEpic(newEpic);
        System.out.println("Добавлен эпик: " + newEpic);

        SubTask newSubTask1 = new SubTask(0, "SubTask1", "Description", Duration.ofMinutes(15),
                                             newEpic.getId());
        if (fileBackedManager.addSubTask(newSubTask1) != null) {
            System.out.println("Добавлена подзадача: " + newSubTask1);
        }

        newSubTask1.setStatus(IssueStatus.DONE);
        if (fileBackedManager.updateSubTask(newSubTask1) != null) {
            System.out.println("Установлен статус 'DONE' для подзадачи: " + newSubTask1);
            System.out.println("Добавили в историю подзадачу: " + newSubTask1);
        }

        SubTask newSubTask2 = new SubTask(0, "SubTask2", "Description", Duration.ofMinutes(15),
                                           newEpic.getId());
        if (fileBackedManager.addSubTask(newSubTask2) != null) {
            System.out.println("Добавлена подзадача: " + newSubTask2);
            fileBackedManager.getSubTaskById(newSubTask2.getId());
            System.out.println("Добавили в историю подзадачу: " + newSubTask2 + "\n");
        }

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

        System.out.println("\nАВТО ТЕСТ завершен FileBackedTasksManager.");
    }

    /**
     * Восстанавливает данные менеджера из файла при запуске программы
     * @param file csv-файл для хранения данных менеджера задач
     * @return Экземпляр класса {@link FileBackedTasksManager}. Может вернуть null, если не сможет
     * проинициализировать csv-файл
     */
    static FileBackedTasksManager loadFromFile(File file) {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(Managers.getDefaultHistory(), file);
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
