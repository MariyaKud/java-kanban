package dao;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import exception.ManagerSaveException;
import model.Epic;
import model.Issue;
import model.SubTask;
import model.Task;

import service.FileBackedTasksManager;
import service.TaskManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Универсальный класс сохранения/загрузки данных менеджера задач, поддерживающих контракт {@link TaskManager}
 * в CSV файл
 *
 * <p> В файл записываются задачи, эпики, подзадачи:
 * id,type,name,status,description,duration,startTime,epic
 * <p> Пустая строка
 * <p> Идентификаторы просмотренных задач через ","
 */
public final class CsvMakeRepository implements IssueRepository {

    /**
     * Загрузить задачи и историю просмотров из файла в менеджер
     * @param tracker - менеджер, работающий с файлами, в который нужно загрузить данные из файла
     * @param file файл, из которого загружаем данные
     */
    @Override
    public void load(TaskManager tracker, File file) {

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            //Пропускаем заголовок
            fileReader.readLine();

            //Читаем задачи
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if  (!line.isEmpty()) {
                    Issue issue = SerializerIssue.stringToIssue(line);
                    if (issue != null) {
                        switch(issue.getType()){
                            case TASK:
                                ((FileBackedTasksManager) tracker).addTaskWithId((Task) issue);
                                break;

                            case EPIC:
                                ((FileBackedTasksManager) tracker).addEpicWithId((Epic) issue);
                                break;

                            case SUBTASK:
                                ((FileBackedTasksManager) tracker).addSubTaskWithId((SubTask) issue);
                                break;

                            default:
                                System.out.println(MSG_ENUM);
                        }
                    }
                } else {
                    // пустая строка означает, что задачи закончились
                    break;
                }
            }

            //Читаем историю
            if (fileReader.ready()) {
                String line = fileReader.readLine();
                if  (!line.isEmpty()) {
                    List<Integer> historyID = SerializerIssue.stringToHistory(line.trim());

                    for (Integer id : historyID) {
                        if (tracker.getTaskById(id) == null) {
                            if (tracker.getSubTaskById(id) == null) {
                                tracker.getEpicById(id);
                            }
                        }
                    }

                }
            }

        } catch (IOException  e) {
            System.out.println("Произошла ошибка во время чтения файла:");
            System.out.println(e.getMessage());
            System.out.println("Запущен новый менеджер без истории.");
        }
    }

    /**
     * Сохранить задачи историю просмотров задач в файл
     * @param tracker - менеджер задач, поддерживающий контракт {@link TaskManager}
     * @param file файл, в который сохраняем данные менеджера задач
     * @throws ManagerSaveException при ошибке записи данных в csv-файл
     */
    @Override
    public void save(TaskManager tracker, File file) throws ManagerSaveException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {

            writer.write(FILE_HEAD);

            for (Task value : tracker.getAllTasks()) {
                writer.write(SerializerIssue.issueToString(value));
            }
            for (Epic value : tracker.getAllEpics()) {
                writer.write(SerializerIssue.issueToString(value));
            }
            for (SubTask value : tracker.getAllSubTasks()) {
                writer.write(SerializerIssue.issueToString(value));
            }

            writer.newLine();
            writer.write(SerializerIssue.historyToString(tracker.getHistory()));

        } catch (IOException e) {
            throw new ManagerSaveException(e.getMessage());
        }
    }

    /**
     * Постман: https://www.getpostman.com/collections/a83b61d9e1c81c10575c
     */
    public static class KVServer {
        public static final int PORT = 8078;
        private final String apiToken;
        private final HttpServer server;
        private final Map<String, String> data = new HashMap<>();
        private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

        public KVServer() throws IOException {
            apiToken = generateApiToken();
            server = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
            server.createContext("/register", this::register);
            server.createContext("/save", this::save);
            server.createContext("/load", this::load);
        }

        public static void main(String[] args) throws IOException {
            KVServer kvServer = new KVServer();
            kvServer.start();
        }

        private void load(HttpExchange h) throws IOException {
            try {
                System.out.println("\n/load");
                if (!hasAuth(h)) {
                    System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                    h.sendResponseHeaders(403, 0);
                    return;
                }
                if ("GET".equals(h.getRequestMethod())) {
                    String key = h.getRequestURI().getPath().substring("/load/".length());
                    if (key.isEmpty()) {
                        System.out.println("Key для восстановления пустой. key указывается в пути: /load/{key}");
                        h.sendResponseHeaders(400, 0);
                        return;
                    }
                    if (data.containsKey(key)) {
                        String responseString = data.get(key);
                        System.out.println("Значение для ключа " + key + " получено!");

                        byte[] bytes = responseString.getBytes(DEFAULT_CHARSET);
                        h.sendResponseHeaders(200, bytes.length);
                        try (OutputStream os = h.getResponseBody()) {
                            os.write(bytes);
                        }
                    } else {
                        h.sendResponseHeaders(200, 0);
                        System.out.println("Данных по ключу " + key + " нет!");
                    }
                } else {
                    System.out.println("/load ждёт GET-запрос, а получил: " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        }

        private void save(HttpExchange h) throws IOException {
            try {
                System.out.println("\n/save");
                if (!hasAuth(h)) {
                    System.out.println("Запрос неавторизован, нужен параметр в query API_TOKEN со значением апи-ключа");
                    h.sendResponseHeaders(403, 0);
                    return;
                }
                if ("POST".equals(h.getRequestMethod())) {
                    String key = h.getRequestURI().getPath().substring("/save/".length());
                    if (key.isEmpty()) {
                        System.out.println("Key для сохранения пустой. key указывается в пути: /save/{key}");
                        h.sendResponseHeaders(400, 0);
                        return;
                    }
                    String value = readText(h);
                    if (value.isEmpty()) {
                        System.out.println("Value для сохранения пустой. value указывается в теле запроса");
                        h.sendResponseHeaders(400, 0);
                        return;
                    }
                    data.put(key, value);
                    System.out.println("Значение для ключа " + key + " успешно обновлено!");
                    h.sendResponseHeaders(200, 0);
                } else {
                    System.out.println("/save ждёт POST-запрос, а получил: " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        }

        private void register(HttpExchange h) throws IOException {
            try {
                System.out.println("\n/register");
                if ("GET".equals(h.getRequestMethod())) {
                    sendText(h, apiToken);
                } else {
                    System.out.println("/register ждёт GET-запрос, а получил " + h.getRequestMethod());
                    h.sendResponseHeaders(405, 0);
                }
            } finally {
                h.close();
            }
        }

        public void start() {
            System.out.println("Запускаем сервер на порту " + PORT);
            System.out.println("Открой в браузере http://localhost:" + PORT + "/");
            System.out.println("API_TOKEN: " + apiToken);
            server.start();
        }

        public void stop() {
            System.out.println("Остановлен сервер на порту " + PORT);
            System.out.println("API_TOKEN: " + apiToken);
            server.stop(0);
        }

        private String generateApiToken() {
            return "" + System.currentTimeMillis();
        }

        protected boolean hasAuth(HttpExchange h) {
            String rawQuery = h.getRequestURI().getRawQuery();
            return rawQuery != null && (rawQuery.contains("API_TOKEN=" + apiToken) || rawQuery.contains("API_TOKEN=DEBUG"));
        }

        protected String readText(HttpExchange h) throws IOException {
            return new String(h.getRequestBody().readAllBytes(), UTF_8);
        }

        protected void sendText(HttpExchange h, String text) throws IOException {
            byte[] resp = text.getBytes(UTF_8);
            h.getResponseHeaders().add("Content-Type", "application/json");
            h.sendResponseHeaders(200, resp.length);
            h.getResponseBody().write(resp);
        }

        public static class HttpTaskServer {
        }
    }
}
