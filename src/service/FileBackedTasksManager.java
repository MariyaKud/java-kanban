package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {
    private static final String HOME = System.getProperty("user.home");
    private static final String NAME_FILE = "taskManager.csv";
    private final File fileTaskManager;

    public static void main(String[] args) {

        File newTaskManagerFile = new File(HOME,NAME_FILE);
        FileBackedTasksManager tracker = new FileBackedTasksManager(newTaskManagerFile);


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

    }

    public FileBackedTasksManager(File taskManagerFile) {
        this.fileTaskManager = taskManagerFile;
        loadFromFile(taskManagerFile);
    }

    /**
     * Сохранить текущее состояние менеджера задач в файл
     */
    public void save() {
        boolean fileExists = fileTaskManager.exists();

        //Проверяем наличие файла менеджера задач
        //Если его нет, то пытаемся создать
        if (!fileExists) {
            try {
                fileExists = fileTaskManager.createNewFile();
                if (!fileExists) {
                    throw new ManagerSaveException("Произошла ошибка во время создания файла менеджера! " +
                                                     "Корректная работа менеджера невозможна.");
                }
            } catch (ManagerSaveException exception) {
                System.out.println(exception.getMessage());
                return;
            } catch (IOException exception) {
                System.out.println("Произошла ошибка во время записи в файл.");
                return;
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileTaskManager.getName()
                                                                     , StandardCharsets.UTF_8))) {
            for (Task value : tasks.values()) {
                writer.write(SerializationToString.toString(value));
                writer.write("\n");
            }
            for (Epic value : epics.values()) {
                writer.write(SerializationToString.toString(value));
                writer.write("\n");
            }
            for (SubTask value : subTasks.values()) {
                writer.write(SerializationToString.toString(value));
                writer.write("\n");
            }
            writer.write("\n");
            writer.write(SerializationToString.historyToString(historyManager));

        } catch (FileNotFoundException e) {
            System.out.println("Файл для записи данных не найден.");
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время записи в файл.");
        }
    }

    /**
     * Восстанавливает данные менеджера из файла при запуске программы
     * @param file - с данными менеджера
     */
    public void loadFromFile(File file) {

        System.out.println("Выполняется загрузка данных из файла ..");
        if (file.exists()) {
            try (BufferedReader fileReader = new BufferedReader(new FileReader(file.getName(),
                    StandardCharsets.UTF_8))) {

                while (fileReader.ready()) {
                    String line = fileReader.readLine();
                    if (!"".equals(line.trim())) {
                        Issue issue = fromString(line);
                        System.out.println(issue);
                    } else {
                        break;
                    }
                }
                //Историю читаем
                if (fileReader.ready()) {
                    String line = fileReader.readLine();
                    List<Integer> history = historyFromString(line.trim());
                    for (Integer integer : history) {
                        System.out.println(integer);
                    }
                }

            } catch (FileNotFoundException e) {
                System.out.println("Файл с данными не найден, запущен новый менеджер без истории.");
            } catch (IOException e) {
                System.out.println("Произошла ошибка во время чтения файла, запущен новый менеджер без истории.");
            }
        } else {
            System.out.println("Файл с данными не найден, запущен новый менеджер без истории.");
        }
    }

    private class ManagerSaveException extends Exception {
        public ManagerSaveException(String message) {
            super(message);
        }
    }

    private static class SerializationToString {
        private SerializationToString() {
        }

        public static String toString(Issue issue) {
            //id,type,name,status,description,epic
            StringBuilder result = new StringBuilder();
            result.append(issue.getId()).append(",").append(issue.getType()).append(",");
            result.append(issue.getTitle()).append(",").append(issue.getStatus()).append(",");
            result.append(issue.getDescription()).append(",");

            if (issue.getType() == IssueType.SUBTASK) {
                result.append(((SubTask) issue).getParent().getId());
            }
            return result.toString();
        }

        public static String historyToString(HistoryManager manager) {
            //id задач в порядке просмотра
            StringBuilder result = new StringBuilder();

            for (Issue issue : manager.getHistory()) {
                result.append(issue.getId()).append(",");
            }

            return result.toString();
        }
    }

    public Issue fromString(String value) {
        String[] split = value.trim().split(",");
        String type = split[1];
        switch (type) {
            case "TASK":
                Task newTask = new Task(Integer.parseInt(split[0]), split[2], split[4],
                        IssueStatus.valueOf(split[3]));
                super.addTask(newTask);
                return newTask;
            case "EPIC":
                Epic newEpic = new Epic(Integer.parseInt(split[0]), split[2], split[4]);
                super.addEpic(newEpic);
                return newEpic;
            case "SUBTASK":
                SubTask newSubTask = new SubTask(Integer.parseInt(split[0]), split[2], split[4],
                        getEpicById(Integer.parseInt(split[5])),IssueStatus.valueOf(split[3]));
                super.addSubTask(newSubTask);
                return newSubTask;
            default:
                System.out.println("Переданы не корректные данные. По ним нельзя собрать задачу любого типа.");
                return null;
        }
    }

    public static List<Integer> historyFromString(String value) {
        List<Integer> history= new ArrayList<>();
        return history;
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

    @Override
    public List<Task> getAllTasks() {
        return super.getAllTasks();
    }

    @Override
    public List<SubTask> getAllSubTasks() {
        return super.getAllSubTasks();
    }

    @Override
    public List<Epic> getAllEpics() {
        return super.getAllEpics();
    }

    @Override
    public List<SubTask> getChildrenOfEpicById(int id) {
        return super.getChildrenOfEpicById(id);
    }

    @Override
    public List<Issue> getHistory() {
        return super.getHistory();
    }
}
