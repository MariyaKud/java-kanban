package service;

import DAO.CSVMakeRepository;
import DAO.ManagerSaveException;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.File;
import java.io.IOException;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private static final String HOME = System.getProperty("user.home");
    private static final String NAME_FILE = "taskManager.csv";
    private final File fileTaskManager;

    public FileBackedTasksManager(File fileTaskManager) {
        this.fileTaskManager = fileTaskManager;
    }

    public static void main(String[] args) {

        File newTaskManagerFile = new File(HOME,NAME_FILE);
        FileBackedTasksManager tracker = loadFromFile(newTaskManagerFile);

        /*
        Task newTask1 = new Task(0, "Task1", "Description");
        tracker.addTask(newTask1);
        Task newTask2 = new Task(0, "Task2", "Description");
        tracker.addTask(newTask2);
        newTask2.setStatus(IssueStatus.DONE);
        tracker.updateTask(newTask2);

        Epic newEpic = new Epic(0, "Epic1", "Description");
        tracker.addEpic(newEpic);

        SubTask newSubTask1 = new SubTask(0, "SubTask1", "Description", newEpic);
        tracker.addSubTask(newSubTask1);
        newSubTask1.setStatus(IssueStatus.DONE);
        tracker.updateSubTask(newSubTask1);

        SubTask newSubTask2 = new SubTask(0, "SubTask2", "Description", newEpic);
        tracker.addSubTask(newSubTask2);
        tracker.getSubTaskById(newSubTask2.getId());
         */
    }

    /**
     * Восстанавливает данные менеджера из файла при запуске программы
     * @param file - с данными менеджера
     */
    public static FileBackedTasksManager loadFromFile(File file) {

        FileBackedTasksManager fileBackedTasksManager = new FileBackedTasksManager(file);
        System.out.println("Выполняется загрузка данных из файла ..");
        return (FileBackedTasksManager) new CSVMakeRepository(file).load(fileBackedTasksManager);

    }

    /**
     * Сохранить текущее состояние менеджера задач в файл
     */
    public void save() {

        try {
            new CSVMakeRepository(fileTaskManager).save(this);
        }
        catch (ManagerSaveException e) {
            //настроили обработку исключения, сгенерированного в try
            System.out.println("Сохранить данные в файл не вышло!");
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
