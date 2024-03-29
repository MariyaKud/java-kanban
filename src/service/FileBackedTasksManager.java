package service;

import dao.IssueRepository;

import exception.NotValidate;
import exception.ParentNotFound;

import model.Epic;
import model.SubTask;
import model.Task;

import java.io.File;

/**
 * Менеджер задач управления сущностями: {@code Task}, {@code SubTask}, {@code Epic} наследники класса {@code Issue}
 * Хранит свои данные в файле на диске, наследник класса {@code InMemoryTasksManager}
 * Поддерживает контракт {@code TasksManager}
 */
public class FileBackedTasksManager extends InMemoryTaskManager {

    private final File file;
    private final IssueRepository csvMakeRepository = Managers.getDefaultIssueRepository();

    public FileBackedTasksManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    /**
     * Восстанавливает данные менеджера из файла при запуске программы
     * @param file csv-файл для хранения данных менеджера задач
     * @return Экземпляр класса {@link FileBackedTasksManager}. Может вернуть null, если не сможет
     * проинициализировать csv-файл
     */
    public static FileBackedTasksManager loadFromFile(File file) {

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
    public Task addTaskWithId(Task task) {
        return super.addTaskWithId(task);
    }

    @Override
    public SubTask addSubTaskWithId(SubTask subTask) throws NotValidate {
        return super.addSubTaskWithId(subTask);
    }

    @Override
    public Epic addEpicWithId(Epic epic) {
        return super.addEpicWithId(epic);
    }

    @Override
    public Task addTask(Task task) throws NotValidate {
        Task newTask = super.addTask(task);
        save();
        return newTask;
    }

    @Override
    public SubTask addSubTask(SubTask subTask) throws NotValidate, ParentNotFound {
        SubTask newSubTask = super.addSubTask(subTask);
        save();
        return newSubTask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic newEpic = super.addEpic(epic);
        save();
        return newEpic;
    }

    @Override
    public Task updateTask(Task task) throws NotValidate {
        Task updateTask = super.updateTask(task);
        save();
        return updateTask;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) throws NotValidate, ParentNotFound {
        SubTask updateSubTask = super.updateSubTask(subTask);
        save();
        return updateSubTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Epic updateEpic = super.updateEpic(epic);
        save();
        return updateEpic;
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
