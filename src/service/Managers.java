package service;

import dao.CSVMakeRepository;
import dao.IssueRepository;
import model.Epic;
import model.IssueStatus;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Утилитарный класс <b>{@code Managers}</b> ответственный за получение дефолтных значений
 *
 * <p>Должен подобрать нужную реализацию:
 * <p> - объекта-менеджера {@code TaskManager}
 * <p> - объекта-история просмотров {@code HistoryManager}
 */
public class Managers {

    private static final IssueRepository issueRepository = new CSVMakeRepository();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").
                                                       withZone(ZoneId.systemDefault());

    /**
     * Приватный конструктор для закрытия возможности создать объект.
     * Дефолтный конструктор класса - публичный, нам нужно его перекрыть,
     * т.к. все методы класса static объект нам не нужен.
     */
    private Managers() {
    }

    /**
     * Получить дефолтный объект-менеджера
     * @return  объект-менеджер
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultHistory());
    }

    /**
     * Получить дефолтный объект-история просмотров
     * @return  объект-история просмотров
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    /**
     * Получить дефолтный объект-обмена менеджера с файлом csv
     * @return  объект-экземпляр поддерживающий контрактом {@code IssueRepository} для записи и чтения данных в файл csv
     */
    public static IssueRepository getDefaultIssueRepository() {
        return issueRepository;
    }

    /**
     * Получить дефолтный формат дат, для менеджера задач
     * @return формат хранения и представления дат
     */
    public static DateTimeFormatter getFormatter() {
        return formatter;
    }

    /**
     * Выполняет набор базовых операций интерфейса, с выводом информации о результате на консоль
     * @param taskManager наследник контракта {@code TaskManager}
     */
    public static void simpleTestForTaskManager(TaskManager taskManager) {

        System.out.println("ЗАПУЩЕН АВТО ТЕСТ менеджера: " + taskManager.getClass());

        Task newTask1 = new Task("Test", "Description", Duration.ofMinutes(10));
        if (taskManager.addTask(newTask1) != null) {
            System.out.println("✅" + "Добавлена задача: " + newTask1);
        } else {
            System.out.println("❌" + "Добавление задачи не состоялось");
        }

        Task newTask2 = new Task("Task2", "Description", Duration.ofMinutes(20));
        if (taskManager.addTask(newTask2) != null) {
            System.out.println("✅" + "Добавлена задача: " + newTask2);
        } else {
            System.out.println("❌" + "Добавление задачи не состоялось");
        }

        newTask2.setStatus(IssueStatus.DONE);
        if (taskManager.updateTask(newTask2) != null) {
            System.out.println("✅" + "Установлен статус 'DONE' для задачи: " + newTask2);
            System.out.println("✅" + "Добавили в историю задачу: " + newTask2);
        } else {
            System.out.println("❌" + "Обновление статуса задачи не состоялось");
        }

        Epic newEpic = new Epic(0, "Epic1", "Description");
        if (taskManager.addEpic(newEpic) != null) {
            System.out.println("✅" + "Добавлен эпик: " + newEpic);
        } else {
            System.out.println("❌" + "Добавление эпика не состоялось");
        }

        SubTask newSubTask1 = new SubTask(0, "SubTask1", "Description", Duration.ofMinutes(15),
                newEpic.getId());
        if (taskManager.addSubTask(newSubTask1) != null) {
            System.out.println("✅" + "Добавлена подзадача: " + newSubTask1);
        }
        newSubTask1.setStatus(IssueStatus.DONE);
        if (taskManager.updateSubTask(newSubTask1) != null) {
            System.out.println("✅" + "Установлен статус 'DONE' для подзадачи: " + newSubTask1);
            System.out.println("✅" + "Добавили в историю подзадачу: " + newSubTask1);
        } else {
            System.out.println("❌" + "Обновление подзадачи не состоялось");
        }

        SubTask newSubTask2 = new SubTask(0, "SubTask2", "Description", Duration.ofMinutes(15),
                                           newEpic.getId());
        if (taskManager.addSubTask(newSubTask2) != null) {
            System.out.println("✅" + "Добавлена подзадача: " + newSubTask2);
        } else {
            System.out.println("❌" + "Обновление подзадачи не состоялось");
        }
        if (taskManager.getSubTaskById(newSubTask2.getId()) != null) {
            System.out.println("✅" + "Добавили в историю подзадачу: " + newSubTask2 + "\n");
        } else {
            System.out.println("❌" + "При поиске по id подзадача не записана в историю");
        }

        System.out.println("Состояние менеджера задач:");
        taskManager.getAllTasks().forEach(System.out::println);
        taskManager.getAllEpics().forEach(System.out::println);

        System.out.println("\nОтсортированный список задач по дате старта:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);
    }

    /**
     * Добавить новую задачу в менеджер со статусом NEW
     * @param taskManager менеджер задач, в него будем добавлять задачу
     * @return добавленная задача, экземпляр класса {@link Task}
     */
    public static Task addTask(TaskManager taskManager) {
        final Task task = new Task("Test", "Description", Duration.ofMinutes(20));
        taskManager.addTask(task);
        return task;
    }

    /**
     * Добавить новый эпик без детей в менеджере задач
     * @param taskManager менеджер задач, в него будем добавлять эпик
     * @return добавленный эпик, экземпляр класса {@link Epic}
     */
    public static Epic addEpic(TaskManager taskManager) {
        final Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);
        return epic;
    }

    /**
     * Добавить новую подзадачу в менеджер со статусом NEW
     * @param taskManager менеджер задач, в него будем добавлять подзадачу
     * @param epicID родительский ID
     * @param issueStatus статус добавляемой подзадачи {@link IssueStatus}
     * @return добавленная подзадача, экземпляр класса {@link SubTask}
     */
    public static SubTask addSubTask(TaskManager taskManager, int epicID, IssueStatus issueStatus) {
        final SubTask subTask = new SubTask("SubTask", "Description", Duration.ofMinutes(15),
                epicID, issueStatus);
        taskManager.addSubTask(subTask);
        return subTask;
    }
}
