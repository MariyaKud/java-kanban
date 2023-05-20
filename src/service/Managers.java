package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import dao.CsvMakeRepository;
import dao.IssueRepository;

import model.Epic;
import model.IssueStatus;
import model.SubTask;
import model.Task;

import java.lang.reflect.Type;

import java.time.Instant;
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

    public final static String PATH_SERVER = "http://localhost:";
    public final static int PORT_KV_SERVER = 8078;
    public final static int PORT_HTTP_SERVER = 8080;
    private static final IssueRepository issueRepository = new CsvMakeRepository();
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
     *
     * @return объект-менеджер
     */
    public static TaskManager getDefault() {
        return new HttpTaskManager(PORT_KV_SERVER);
    }

    /**
     * Получить дефолтный объект-история просмотров
     *
     * @return объект-история просмотров
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    /**
     * Получить дефолтный объект-обмена менеджера с файлом csv
     *
     * @return объект-экземпляр поддерживающий контрактом {@code IssueRepository} для записи и чтения данных в файл csv
     */
    public static IssueRepository getDefaultIssueRepository() {
        return issueRepository;
    }

    /**
     * Получить дефолтный формат дат, для менеджера задач
     *
     * @return формат хранения и представления дат
     */
    public static DateTimeFormatter getFormatter() {
        return formatter;
    }

    /**
     * Выполняет набор базовых операций интерфейса, с выводом информации о результате на консоль
     *
     * @param taskManager наследник контракта {@code TaskManager}
     */
    public static void getSimpleTestForTaskManager(TaskManager taskManager) {

        System.out.println("ЗАПУЩЕН АВТО ТЕСТ менеджера: " + taskManager.getClass());

        Task newTask1 = new Task("Test", "Description", 10, Instant.now());
        if (taskManager.addTask(newTask1) != null) {
            System.out.println("✅" + "Добавлена задача: " + newTask1);
        } else {
            System.out.println("❌" + "Добавление задачи не состоялось");
        }

        Task newTask2 = new Task("Task2", "Description", 2000);
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

        SubTask newSubTask1 = new SubTask(0, "SubTask1", "Description", 15, newEpic.getId());
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

        SubTask newSubTask2 = new SubTask(0, "SubTask2", "Description", 59, newEpic.getId());
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

        System.out.println("\nИстория просмотров");
        taskManager.getHistory().forEach(System.out::println);
    }

    /**
     * Добавить новую задачу в менеджер со статусом NEW
     *
     * @param taskManager менеджер задач, в него будем добавлять задачу
     * @return добавленная задача, экземпляр класса {@link Task}
     */
    public static Task getSimpleTaskForTest(TaskManager taskManager, int duration, Instant start) {

        final Task task = new Task("Test", "Description", duration, start);
        return taskManager.addTask(task);
    }

    /**
     * Добавить новый эпик без детей в менеджере задач
     *
     * @param taskManager менеджер задач, в него будем добавлять эпик
     * @return добавленный эпик, экземпляр класса {@link Epic}
     */
    public static Epic getSimpleEpicForTest(TaskManager taskManager) {
        final Epic epic = new Epic("Epic", "Description");
        return taskManager.addEpic(epic);
    }

    /**
     * Добавить новую подзадачу в менеджер со статусом NEW
     *
     * @param taskManager менеджер задач, в него будем добавлять подзадачу
     * @param epicID      родительский ID
     * @param issueStatus статус добавляемой подзадачи {@link IssueStatus}
     * @return добавленная подзадача, экземпляр класса {@link SubTask}
     */
    public static SubTask getSimpleSubTaskForTest(TaskManager taskManager, int epicID, IssueStatus issueStatus,
                                                  int duration, Instant start) {
        final SubTask subTask = new SubTask("SubTask", "Description",
                epicID, duration, start, issueStatus);
        return taskManager.addSubTask(subTask);
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.registerTypeAdapter(Instant.class, new InstantTypeConverter());
        return gsonBuilder.create();
    }

    private static class InstantTypeConverter
            implements JsonSerializer<Instant>, JsonDeserializer<Instant> {

        @Override
        public JsonElement serialize(Instant src, Type srcType, JsonSerializationContext context) {
            if (src == Instant.MAX) {
                return new JsonPrimitive(0);
            } else {
                return new JsonPrimitive(src.toEpochMilli());
            }
        }

        @Override
        public Instant deserialize(JsonElement json, Type type, JsonDeserializationContext context)
                throws JsonParseException {
            if ("0".equals(json.getAsString())) {
                return Instant.MAX;
            } else {
                return Instant.ofEpochMilli(json.getAsLong());
            }
        }
    }
}
