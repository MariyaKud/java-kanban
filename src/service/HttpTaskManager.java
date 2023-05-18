package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dao.KVClientBuilder;
import dao.KVServer;
import dao.SerializerIssue;
import dao.KVClient;

import model.Epic;
import model.SubTask;
import model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {

    private final KVClient client;

    private static final Gson gson = Managers.getGson();

    public HttpTaskManager(HistoryManager historyManager, int port) {
        super(historyManager, null);
        this.client = new KVClientBuilder()
                           .port(port)
                           .create();
    }

    public static void main(String[] args) {

        final KVServer kvServer;
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            System.out.println("Возникли проблемы с доступом к северу");
            return;
        }

        final TaskManager httpTasksManager = Managers.getDefault();

        Managers.getSimpleTestForTaskManager(httpTasksManager);

        System.out.println("\n Загружаем данные с сервера HTTP..");
        TaskManager loadFromHTTPServer = loadFromHTTPServer(Managers.PORT_KV_SERVER);

        System.out.println("\nСверим данные менеджера, с восстановленными данными с сервера HTTP:");

        System.out.println("Результат сравнения задач менеджера и задач загруженных с сервера HTTP: " +
                ((loadFromHTTPServer.getAllTasks().equals(httpTasksManager.getAllTasks())) ? "✅" : "❌"));
        System.out.println("Результат сравнения подзадач менеджера и подзадач загруженных с сервера HTTP: " +
                ((loadFromHTTPServer.getAllSubTasks().equals(httpTasksManager.getAllSubTasks())) ? "✅" : "❌"));
        System.out.println("Результат сравнения эпиков менеджера и эпиков загруженных с сервера HTTP: " +
                ((loadFromHTTPServer.getAllEpics().equals(httpTasksManager.getAllEpics())) ? "✅" : "❌"));
        System.out.println("Результат сравнения истории просмотров менеджера и истории восстановленной с сервера HTTP: "
                + (loadFromHTTPServer.getHistory().equals(httpTasksManager.getHistory()) ? "✅" : "❌"));
        System.out.println("Результат сравнения отсортированного списка задач менеджера и восстановленного: " +
                (loadFromHTTPServer.getPrioritizedTasks().equals(httpTasksManager.getPrioritizedTasks())
                        ? "✅" : "❌"));

        System.out.println("\nАВТО ТЕСТ HttpTaskManager завершен");

        kvServer.stop();
    }

    static HttpTaskManager loadFromHTTPServer(int port) {

        HttpTaskManager httpTasksManager = new HttpTaskManager(Managers.getDefaultHistory(), port);

        System.out.println("Выполняется загрузка данных с сервера ..");
        //TASKS
        String json = httpTasksManager.client.load("tasks");
        final List<Task> loadTasks = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {}.getType());
        loadTasks.forEach(httpTasksManager::addTaskWithId);
        //EPIC
        json = httpTasksManager.client.load("epics");
        final List<Epic> loadEpics = gson.fromJson(json, new TypeToken<ArrayList<Epic>>() {}.getType());
        loadEpics.forEach(httpTasksManager::addEpicWithId);
        //SUBTASKS
        json = httpTasksManager.client.load("subTasks");
        final List<SubTask> loadSubTasks = gson.fromJson(json, new TypeToken<ArrayList<SubTask>>() {}.getType());
        loadSubTasks.forEach(httpTasksManager::addSubTaskWithId);
        //HISTORY
        json = httpTasksManager.client.load("history");
        for (Integer id : SerializerIssue.stringToHistory(gson.fromJson(json, String.class))) {
            if (httpTasksManager.getTaskById(id) == null) {
                if (httpTasksManager.getSubTaskById(id) == null) {
                    httpTasksManager.getEpicById(id);
                }
            }
        }

        return httpTasksManager;
    }

    @Override
    void save() {
        client.put("tasks", gson.toJson(getAllTasks()));
        client.put("epics", gson.toJson(getAllEpics()));
        client.put("subTasks", gson.toJson(getAllSubTasks()));
        client.put("history", gson.toJson(SerializerIssue.historyToString(getHistory())));
    }
}
