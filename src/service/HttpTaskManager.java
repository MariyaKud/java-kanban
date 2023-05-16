package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dao.SerializerIssue;
import kv.KVServer;
import kv.KVTaskClient;
import model.Epic;
import model.SubTask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpTaskManager extends FileBackedTasksManager {

    private final String url;

    private static String json;

    private static KVTaskClient client = null;

    private static TaskManager taskManager = Managers.getDefault();

    private static final Gson gson = Managers.getGson();

    public HttpTaskManager(HistoryManager historyManager, String url) {
        super(historyManager, null);
        this.url = url;
        this.client = KVTaskClient.initKVTaskClient(url);
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

        final HttpTaskManager httpTasksManager = new HttpTaskManager(Managers.getDefaultHistory(),
                                                                     Managers.URL_KV_SERVER);

        Managers.getSimpleTestForTaskManager(httpTasksManager);

        System.out.println("\n Загружаем данные с сервера HTTP..");
        FileBackedTasksManager loadFromHTTPServer = loadFromHTTPServer(Managers.URL_KV_SERVER);

        System.out.println("\nСверим данные менеджера, с восстановленными данными с сервера HTTP:");

        System.out.println("Результат сравнения задач менеджера и задач загруженных с сервера HTTP: " +
                ((loadFromHTTPServer.tasks.equals(httpTasksManager.tasks)) ? "✅" : "❌"));
        System.out.println("Результат сравнения подзадач менеджера и подзадач загруженных с сервера HTTP: " +
                ((loadFromHTTPServer.subTasks.equals(httpTasksManager.subTasks)) ? "✅" : "❌"));
        System.out.println("Результат сравнения эпиков менеджера и эпиков загруженных с сервера HTTP: " +
                ((loadFromHTTPServer.epics.equals(httpTasksManager.epics)) ? "✅" : "❌"));
        System.out.println("Результат сравнения истории просмотров менеджера и истории восстановленной с сервера HTTP: "
                + (loadFromHTTPServer.getHistory().equals(httpTasksManager.getHistory()) ? "✅" : "❌"));
        System.out.println("Результат сравнения отсортированного списка задач менеджера и восстановленного: " +
                (loadFromHTTPServer.getPrioritizedTasks().equals(httpTasksManager.getPrioritizedTasks())
                        ? "✅" : "❌"));

        System.out.println("\nАВТО ТЕСТ HttpTaskManager завершен");

        kvServer.stop();
    }

    static HttpTaskManager loadFromHTTPServer(String url) {

        HttpTaskManager httpTasksManager = new HttpTaskManager(Managers.getDefaultHistory(), url);

        System.out.println("Выполняется загрузка данных с сервера ..");

        json = httpTasksManager.client.load("tasks");
        final List<Task> loadTasks = gson.fromJson(json, new TypeToken<ArrayList<Task>>() {
        }.getType());
        for (Task loadTask : loadTasks) {
            httpTasksManager.addTaskWithId(loadTask);
        }

        json = httpTasksManager.client.load("epics");
        final List<Epic> loadEpics = gson.fromJson(json, new TypeToken<ArrayList<Epic>>() {
        }.getType());
        for (Epic loadTask : loadEpics) {
            httpTasksManager.addEpicWithId(loadTask);
        }

        json = httpTasksManager.client.load("subTasks");
        final List<SubTask> loadSubTasks = gson.fromJson(json, new TypeToken<ArrayList<SubTask>>() {
        }.getType());
        for (SubTask loadTask : loadSubTasks) {
            httpTasksManager.addSubTaskWithId(loadTask);
        }

        json = httpTasksManager.client.load("history");
        List<Integer> historyID = SerializerIssue.stringToHistory(gson.fromJson(json, String.class));

        for (Integer id : historyID) {
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

        final String history = SerializerIssue.historyToString(getHistory());
        client.put("history", gson.toJson(history));
    }
}
