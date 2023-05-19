package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import dao.KVClientBuilder;
import dao.KVClient;
import dao.SerializerIssue;

import model.Epic;
import model.SubTask;
import model.Task;

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

    public static HttpTaskManager loadFromHTTPServer(int port) {

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
