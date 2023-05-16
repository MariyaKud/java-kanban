package controller;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    public static final int PORT = 8080;
    private static final Gson gson = Managers.getGson();
    private final HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        server.createContext("/tasks", this::tasks);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        Managers.getSimpleTestForTaskManager(httpTaskServer.taskManager);
        httpTaskServer.start();
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + PORT);
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        server.start();
    }

    public void stop() {
        System.out.println("Остановлен сервер на порту " + PORT);
        server.stop(0);
    }

    private void tasks(HttpExchange httpExchange) throws IOException {
        final String path = httpExchange.getRequestURI().getPath();
        final String method = httpExchange.getRequestMethod();
        final String param = httpExchange.getRequestURI().getQuery();

        try {
            switch (method) {
                case "GET":
                    if (Pattern.matches("^/tasks$", path)) {
                        //Отсортированный список
                        tasksHandler(httpExchange);
                    } else if (Pattern.matches("^/tasks/history$", path)) {
                        //История задач
                        historyHandler(httpExchange);
                    } else if (Pattern.matches("^/tasks/task$", path)) {
                        //Список всех задач
                        taskHandler(httpExchange);
                    } else if (Pattern.matches("^/tasks/task/$", path)) {
                        // Поиск задачи по id
                        taskIdHandler(httpExchange,param);
                    } else {
                        taskErrorHandler(httpExchange, "Не корректная строка запроса = " + path);
                    }
                    break;

            case "POST":
                if (Pattern.matches("^/tasks/task/$", path)) {
                    taskIdADDHandler(httpExchange, param);
                } else {
                    taskErrorHandler(httpExchange, "Не корректная строка запроса = " + path);
                }
                break;

            case "DELETE":
                if (Pattern.matches("^/tasks/task$", path)) {
                    taskDeleteHandler(httpExchange);
                } else if (Pattern.matches("^/tasks/task/$", path)) {
                    taskIdDeleteHandler(httpExchange, param);
                } else {
                    taskErrorHandler(httpExchange, "Не корректная строка запроса = " + path);
                }
                break;

            default:
                taskErrorHandler(httpExchange, "Ждем GET или POST или DELETE, а получили " + method);

            }
        } finally {
            httpExchange.close();
        }
    }

    private void taskErrorHandler(HttpExchange httpExchange, String textError) throws IOException {
        System.out.println(textError);
        sendText(httpExchange, 405, "");
    }

    private void taskHandler(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 201, gson.toJson(taskManager.getAllTasks()));
    }

    private void tasksHandler(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 201, gson.toJson(taskManager.getPrioritizedTasks()));
    }

    private void taskIdHandler(HttpExchange httpExchange, String param) throws IOException {
        int id = parsePathId(param);
        if (id != -1) {
            System.out.println("Получена задача id = " + id);
            sendText(httpExchange, 201, gson.toJson(taskManager.getTaskById(id)));
        } else {
            taskErrorHandler(httpExchange, "Получен не корректный параметр " + param);
        }
    }

    private void taskIdDeleteHandler(HttpExchange httpExchange, String param) throws IOException {
        int id = parsePathId(param);
        if (id != -1) {
            sendText(httpExchange, 201, gson.toJson(taskManager.deleteTaskById(id)));
        } else {
            taskErrorHandler(httpExchange,
                    "Для метода DELETE получен не корректный параметр " + param);
        }
    }

    private void taskIdADDHandler(HttpExchange httpExchange, String param) throws IOException {
        int id = parsePathId(param);
        if (id != -1) {
            //TODO берем тело задачи
        } else {
            taskErrorHandler(httpExchange,
                    "Для метода POST получен не корректный параметр " + param);
        }
    }

    private void taskDeleteHandler(HttpExchange httpExchange) throws IOException {
        taskManager.deleteAllTasks();
        sendText(httpExchange, 200, "");
    }

    private void historyHandler(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 201, gson.toJson(taskManager.getHistory()));
    }

    private int parsePathId(String param) {
        String pathId = param.replaceFirst("id=", "");
        try {
            return Integer.parseInt(pathId);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    protected void sendText(HttpExchange h, int code, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
    }
}
