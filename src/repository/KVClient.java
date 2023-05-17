package repository;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import model.Epic;
import model.SubTask;
import model.Task;

import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class KVClient {

    private final String url;
    private String apiToken;

    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = Managers.getGson();

    public KVClient(int port) {
        this.url = "http://localhost:" + port;
    }

    public static void main(String[] args) throws IOException {
        //Запускаем сервер
        final KVServer kvServer = new KVServer();
        kvServer.start();

        //Создаем клиента
        final KVClient kvTaskClient = new KVClientBuilder()
                                           .port(Managers.PORT_KV_SERVER)
                                           .create();

        //Создаем контекст менеджера задач для теста
        final TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        Managers.getSimpleTestForTaskManager(taskManager);


        //Проверка чтения/записи задач
        String json = gson.toJson(taskManager.getAllTasks());
        kvTaskClient.put("tasks",json);
        String jsonString =  kvTaskClient.load("tasks");
        final List<Task> loadTasks = gson.fromJson(jsonString, new TypeToken<ArrayList<Task>>() {
        }.getType());

        System.out.println("Результат сравнения задач менеджера и задач загруженных с сервера: " +
                ((taskManager.getAllTasks().equals(loadTasks)) ? "✅" : "❌"));

        //Проверка чтения/записи подзадач
        json = gson.toJson(taskManager.getAllSubTasks());
        kvTaskClient.put("subTasks",json);
        jsonString =  kvTaskClient.load("subTasks");
        final List<SubTask> loadSubTasks = gson.fromJson(jsonString, new TypeToken<ArrayList<SubTask>>() {
        }.getType());

        System.out.println("Результат сравнения подзадач менеджера и подзадач загруженных с сервера: " +
                ((taskManager.getAllSubTasks().equals(loadSubTasks)) ? "✅" : "❌"));

        //Проверка чтения/записи эпиков
        json = gson.toJson(taskManager.getAllEpics());
        kvTaskClient.put("epics",json);
        jsonString =  kvTaskClient.load("epics");
        final List<Epic> loadEpics = gson.fromJson(jsonString, new TypeToken<ArrayList<Epic>>() {
        }.getType());

        System.out.println("Результат сравнения эпиков менеджера и эпики загруженные с сервера: " +
                ((taskManager.getAllEpics().equals(loadEpics)) ? "✅" : "❌"));

        //Список истории (храним только идентификаторы)
        final String history = SerializerIssue.historyToString(taskManager.getHistory());
        json = gson.toJson(history);
        kvTaskClient.put("history",json);
        jsonString =  kvTaskClient.load("history");

        System.out.println("Результат сравнения истории менеджера и загруженный список с сервера: " +
                ((history.equals(gson.fromJson(jsonString, String.class))) ? "✅" : "❌"));

        //останавливаем сервер
        kvServer.stop();
    }

    public void put(String key, String json) {

        final URI uri = URI.create(url + "/save/" + key + "?API_TOKEN=" + apiToken);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .POST(body)
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                System.out.println("Данные по ключу " + key + " сохранены на сервере.");
            } else {
                System.out.println("Что-то пошло не так, при получении токена. Сервер вернул код состояния: "
                        + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }

    public String load(String key) {

        final URI uri = URI.create(url + "/load/" + key + "?API_TOKEN=" + apiToken);
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                JsonElement jsonElement = JsonParser.parseString(response.body());
                return gson.toJson(jsonElement);

            } else {
                System.out.println("Что-то пошло не так, при получении токена. Сервер вернул код состояния: "
                        + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                                 "Проверьте, пожалуйста, адрес и повторите попытку.");
        }

        return "";
    }

    public void register() {

        final URI uri = URI.create(url + "/register");
        final HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .GET()
                .build();
        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                apiToken = JsonParser.parseString(response.body()).getAsString();
            } else {
                System.out.println("Что-то пошло не так, при получении токена. Сервер вернул код состояния: "
                        + response.statusCode());
            }
        } catch (NullPointerException | IOException | InterruptedException e) {
            System.out.println("Во время выполнения запроса возникла ошибка.\n" +
                    "Проверьте, пожалуйста, адрес и повторите попытку.");
        }
    }
}

