package service;

import exception.ManagerSaveException;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import dao.KVClientBuilder;
import dao.KVClient;

import model.Epic;
import model.Issue;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Менеджер задач управления сущностями: {@code Task}, {@code SubTask}, {@code Epic} наследники класса {@code Issue}
 * Хранит свои данные на сервере экземпляра класса {@code KVServer}.
 * Для обращения к данным сервера использует посредника экземпляр класса {@code KVClient}.
 * Наследник класса {@code FileBackedTasksManager}.
 * Поддерживает контракт {@code TasksManager}.
 */
public class HttpTaskManager extends FileBackedTasksManager {
    private final KVClient client;
    private static final Gson gson = Managers.getGson();

    public HttpTaskManager(HistoryManager historyManager, int port, boolean shouldLoad) throws ManagerSaveException {
        super(historyManager, null);

        this.client = new KVClientBuilder()
                .port(port)
                .create();

        if (shouldLoad) {
            loadFromHTTPServer();
        }
    }

    public HttpTaskManager(int port) {
        this(Managers.getDefaultHistory(), port, false);
    }

    /**
     * Загружает данные менеджера с сервера HTTP, используя в качестве посредника экземпляр класса {@link KVClient}
     * Загружаемые данные: задачи, подзадачи, эпики, история просмотров
     * @throws ManagerSaveException -  при проблеме с восстановлением данных
     */
    private void loadFromHTTPServer() throws ManagerSaveException {

        System.out.println("Выполняется загрузка данных с сервера ..");

        //TASKS
        try {
            final List<Task> loadTasks = gson.fromJson(client.load("tasks"), new TypeToken<ArrayList<Task>>() {
            }.getType());
            loadTasks.forEach(this::addTaskWithId);

        } catch (JsonSyntaxException | NullPointerException e) {
            throw new ManagerSaveException("Не получилось восстановить задачи с сервера HTTP");
        }

        //EPIC
        try {
            final List<Epic> loadEpics = gson.fromJson(client.load("epics"), new TypeToken<ArrayList<Epic>>() {
            }.getType());
            loadEpics.forEach(this::addEpicWithId);

        } catch (JsonSyntaxException | NullPointerException e) {
            throw new ManagerSaveException("Не получилось восстановить эпики с сервера HTTP");
        }

        //SUBTASKS
        try {
            final List<SubTask> loadSubTasks = gson.fromJson(client.load("subTasks"),
                                                new TypeToken<ArrayList<SubTask>>() {}.getType());

            loadSubTasks.forEach(this::addSubTaskWithId);

        } catch (JsonSyntaxException | NullPointerException e) {
            throw new ManagerSaveException("Не получилось восстановить подзадачи с сервера HTTP");
        }

        //HISTORY
        try {
            List<Integer> history = gson.fromJson(client.load("history"), new TypeToken<List<Integer>>() {
            }.getType());
            history.forEach(this::addToHistoryById);

        } catch (JsonSyntaxException | NullPointerException e) {
            throw new ManagerSaveException("Не получилось восстановить историю просмотров с сервера HTTP");
        }
    }

    /**
     * Добавляет задачу/подзадачу/эпик в историю по id
     *
     * @param id - идентификатор задачи/подзадачи/эпика
     */
    private void addToHistoryById(int id) {
        Issue issue = tasks.get(id);

        if (issue == null) {
            issue = subTasks.get(id);
        }
        if (issue == null) {
            issue = epics.get(id);
        }
        if (issue != null) {
            historyManager.add(issue);
        }
    }

    @Override
    void save() {
        client.put("tasks", gson.toJson(getAllTasks()));
        client.put("epics", gson.toJson(getAllEpics()));
        client.put("subTasks", gson.toJson(getAllSubTasks()));
        client.put("history", gson.toJson(getHistory().stream().map(Issue::getId).collect(Collectors.toList())));
    }
}
