package service;

import DAO.CSVMakeRepository;
import DAO.ManagerSaveException;

import model.Epic;
import model.Issue;
import model.SubTask;
import model.Task;
import model.IssueStatus;

import java.io.File;
import java.io.IOException;

/**
 * Менеджер задач управления сущностями: {@code Task}, {@code SubTask}, {@code Epic} наследники класса {@code Issue}
 * Хранит свои задачи в файле на диске, наследник класса {@code InMemoryTasksManager}
 * Поддерживает контракт {@code TasksManager}
 */
public class FileBackedTasksManager extends InMemoryTaskManager {

    private static final String HOME = System.getProperty("user.home");
    private static final String NAME_FILE = "taskManager.csv";
    private final File fileTaskManager;
    private final CSVMakeRepository csvMakeRepository;

    public FileBackedTasksManager(File fileTaskManager) {
        this.fileTaskManager = fileTaskManager;
        this.csvMakeRepository = new CSVMakeRepository(fileTaskManager);
    }

    public static void main(String[] args) {

        File newTaskManagerFile = new File(HOME,NAME_FILE);
        FileBackedTasksManager tracker = loadFromFile(newTaskManagerFile);

        if (tracker == null) {
            System.out.println("Не получилось инициализировать менеджер задач, программа не запущена.");
            return;
        }

        System.out.println("Загружены данные:");
        System.out.println("ТАSK:");
        for (Issue issue : tracker.getAllTasks()) {
            System.out.println(issue);
        }
        System.out.println("SUBТАSK:");
        for (Issue issue : tracker.getAllSubTasks()) {
            System.out.println(issue);
        }
        System.out.println("EPICS:");
        for (Issue issue : tracker.getAllEpics()) {
            System.out.println(issue);
        }
        System.out.println("ИСТОРИЯ:");
        for (Issue issue : tracker.getHistory()) {
            System.out.println(issue);
        }

        System.out.println("\nЗапускаем авто тест менеджера задач, хранящего данные в файле..");
        Task newTask1 = new Task(0, "Task1", "Description");
        tracker.addTask(newTask1);
        System.out.println("Добавлена задача: " + newTask1);
        Task newTask2 = new Task(0, "Task2", "Description");
        tracker.addTask(newTask2);
        System.out.println("Добавлена задача: " + newTask2);
        newTask2.setStatus(IssueStatus.DONE);
        tracker.updateTask(newTask2);
        System.out.println("Установлен статус DONE для задачи: " + newTask2);
        System.out.println("Добавили в историю задачу: " + newTask2);

        Epic newEpic = new Epic(0, "Epic1", "Description");
        tracker.addEpic(newEpic);
        System.out.println("Добавлен эпик: " + newEpic);

        SubTask newSubTask1 = new SubTask(0, "SubTask1", "Description", newEpic.getId());
        tracker.addSubTask(newSubTask1);
        System.out.println("Добавлена подзадача: " + newSubTask1);

        newSubTask1.setStatus(IssueStatus.DONE);
        tracker.updateSubTask(newSubTask1);
        System.out.println("Установлен статус DONE для подзадачи: " + newSubTask1);
        System.out.println("Добавили в историю подзадачу: " + newSubTask1);

        SubTask newSubTask2 = new SubTask(0, "SubTask2", "Description", newEpic.getId());
        tracker.addSubTask(newSubTask2);
        System.out.println("Добавлена подзадача: " + newSubTask2);
        tracker.getSubTaskById(newSubTask2.getId());
        System.out.println("Добавили в историю подзадачу: " + newSubTask2);

        System.out.println("\nАвто тест завершен.");
    }

    /**
     * Восстанавливает данные менеджера из файла при запуске программы
     * @param file csv-файл для хранения данных менеджера задач
     * @return Экземпляр класса {@link FileBackedTasksManager}. Может вернуть null, если не сможет
     * проинициализировать csv-файл
     */
    static FileBackedTasksManager loadFromFile(File file) {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);

        boolean fileExist = file.exists();
        //Проверяем наличие файла менеджера задач. Если его нет, то пытаемся создать
        if (!fileExist) {
            try {
                fileExist = file.createNewFile();
            } catch (IOException | SecurityException  e) {
                System.out.println("Возникли проблемы при создании файла, работа менеджера невозможна!");
                System.out.println(e.getMessage());
                return null;
            }
        }

        if (fileExist) {
            System.out.println("Выполняется загрузка данных из файла ..");
            return (FileBackedTasksManager) fileBackedTasksManager.csvMakeRepository.load(fileBackedTasksManager);
        }

        return fileBackedTasksManager;
    }

    /**
     * Сохранить текущее состояние менеджера задач в файл
     */
    void save() {
        try {
            csvMakeRepository.save(this);
        }
        catch (ManagerSaveException e) {
            System.out.println(e.getMessage());
            for (StackTraceElement stackTraceElement : e.getStackTrace()) {
                System.out.println(stackTraceElement);
            }
        }
    }

 ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
