package controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import exception.NotValidate;
import exception.ParentNotFound;

import model.Epic;
import model.SubTask;
import model.Task;

import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    private static final Gson gson = Managers.getGson();
    private final HttpServer server;
    private final TaskManager taskManager;

    public HttpTaskServer() throws IOException {
        taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        server = HttpServer.create(new InetSocketAddress("localhost", Managers.PORT_HTTP_SERVER), 0);
        server.createContext("/tasks", this::tasksHandle);
    }

    public static void main(String[] args) throws IOException {
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        Managers.getSimpleTestForTaskManager(httpTaskServer.taskManager);
        httpTaskServer.start();
    }

    public void start() {
        System.out.println("Запускаем сервер на порту " + Managers.PORT_HTTP_SERVER);
        System.out.println("Открой в браузере http://localhost:" + Managers.PORT_HTTP_SERVER + "/");
        server.start();
    }

    public void stop() {
        System.out.println("Остановлен сервер на порту " + Managers.PORT_HTTP_SERVER);
        server.stop(0);
    }

    private void tasksHandle(HttpExchange httpExchange) throws IOException {
        final String path = httpExchange.getRequestURI().getPath();
        final String method = httpExchange.getRequestMethod();
        final String param = httpExchange.getRequestURI().getQuery();

        try {
            switch (method) {
                case "GET":
                    if (Pattern.matches("^/tasks$", path)) {
                        //Отсортированный список
                        tasksHandler(httpExchange);
                    } else if (Pattern.matches("^/tasks/task$", path)) {
                        //Список всех задач
                        taskHandler(httpExchange);
                    } else if (Pattern.matches("^/tasks/subtask$", path)) {
                        //Список всех подзадач
                        subTaskHandler(httpExchange);
                    } else if (Pattern.matches("^/tasks/epic$", path)) {
                        //Список всех эпиков
                        epicHandler(httpExchange);
                    } else if (Pattern.matches("^/tasks/task/$", path)) {
                        // Поиск задачи по id
                        taskIdHandler(httpExchange, param);
                    } else if (Pattern.matches("^/tasks/subtask/$", path)) {
                        // Поиск задачи по id
                        subTaskIdHandler(httpExchange, param);
                    } else if (Pattern.matches("^/tasks/epic/$", path)) {
                        // Поиск задачи по id
                        epicIdHandler(httpExchange, param);
                    } else if (Pattern.matches("^/tasks/subtask/epic/$", path)) {
                        //Получить детей эпика
                        childrenEpicByIdHandler(httpExchange, param);
                    } else if (Pattern.matches("^/tasks/history$", path)) {
                        //История задач
                        historyHandler(httpExchange);
                    } else {
                        taskErrorHandler(httpExchange, 400, "Не корректны запрос - " + path);
                    }
                    break;

            case "POST":
                if (Pattern.matches("^/tasks/task/$", path)) {
                    taskADDUpdateHandler(httpExchange);
                } else if (Pattern.matches("^/tasks/subtask/$", path)) {
                    subTaskADDUpdateHandler(httpExchange);
                } else if (Pattern.matches("^/tasks/epic/$", path)) {
                    epicADDUpdateHandler(httpExchange);
                } else {
                    taskErrorHandler(httpExchange, 400, "Не корректны запрос - " + path);
                }
                break;

            case "DELETE":
                if (Pattern.matches("^/tasks/task$", path)) {
                    taskDeleteHandler(httpExchange);
                } else if (Pattern.matches("^/tasks/subtask$", path)) {
                    subTaskDeleteHandler(httpExchange);
                } else if (Pattern.matches("^/tasks/epic$", path)) {
                    epicDeleteHandler(httpExchange);
                } else if (Pattern.matches("^/tasks/task/$", path)) {
                    taskIdDeleteHandler(httpExchange, param);
                } else if (Pattern.matches("^/tasks/subtask/$", path)) {
                    subTaskIdDeleteHandler(httpExchange, param);
                } else if (Pattern.matches("^/tasks/epic/$", path)) {
                    epicIdDeleteHandler(httpExchange, param);
                } else {
                    taskErrorHandler(httpExchange, 400, "Не корректны запрос - " + path);
                }
                break;

            default:
                taskErrorHandler(httpExchange, 405,"Ждем GET или POST или DELETE, а получили " + method);
            }

        } finally {
            httpExchange.close();
        }
    }

    private void tasksHandler(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 200, gson.toJson(taskManager.getPrioritizedTasks()));
    }

    private void taskHandler(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 200, gson.toJson(taskManager.getAllTasks()));
    }

    private void subTaskHandler(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 200, gson.toJson(taskManager.getAllSubTasks()));
    }

    private void epicHandler(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 200, gson.toJson(taskManager.getAllEpics()));
    }

    private void taskIdHandler(HttpExchange httpExchange, String param) throws IOException {
        int id = parsePathId(param);
        if (id != -1) {
            final Task task = taskManager.getTaskById(id);
            if (task != null) {
                sendText(httpExchange, 200, gson.toJson(task));
            } else {
                taskErrorHandler(httpExchange, 404, "Задача с id =" + id + " не найдена.");
            }
        } else {
            taskErrorHandler(httpExchange, 404,
                    "Для метода GET получен не корректный id задачи = " + param);
        }
    }

    private void subTaskIdHandler(HttpExchange httpExchange, String param) throws IOException {
        int id = parsePathId(param);
        if (id != -1) {
            final SubTask task = taskManager.getSubTaskById(id);
            if (task != null) {
                sendText(httpExchange, 200, gson.toJson(task));
            } else {
                taskErrorHandler(httpExchange, 404, "Подзадача с id =" + id + " не найдена.");
            }
        } else {
            taskErrorHandler(httpExchange, 404,
                    "Для метода GET получен не корректный id подзадачи = " + param);
        }
    }

    private void epicIdHandler(HttpExchange httpExchange, String param) throws IOException {
        int id = parsePathId(param);
        if (id != -1) {
            final Epic task = taskManager.getEpicById(id);
            if (task != null) {
                sendText(httpExchange, 200, gson.toJson(task));
            } else {
                taskErrorHandler(httpExchange, 404, "Эпик с id =" + id + " не найдена.");
            }
        } else {
            taskErrorHandler(httpExchange, 404,
                    "Для метода GET получен не корректный id задачи = " + param);
        }
    }

    private void taskADDUpdateHandler(HttpExchange httpExchange) throws IOException {
        try {
            String body = readText(httpExchange);
            Task task = gson.fromJson(body, Task.class);
            if (task != null) {
                if (taskManager.getTaskById(task.getId()) == null) {
                    //Добавляем
                    task = taskManager.addTask(task);
                } else {
                    //Обновление
                    task = taskManager.updateTask(task);
                }
                if (task != null) {
                    sendText(httpExchange, 201, gson.toJson(task));
                } else {
                    sendText(httpExchange, 400, "");
                }
            } else {
                //ресурс не найден
                taskErrorHandler(httpExchange,404, "Передан null");
            }
        } catch (JsonSyntaxException e) {
            taskErrorHandler(httpExchange,422, "Не получилось преобразовать в задачу.");
        } catch (NotValidate e) {
            taskErrorHandler(httpExchange,422, "Указанный период задачи занят.");
        } catch (Exception e) {
            taskErrorHandler(httpExchange,422, e.getMessage());
        }
    }

    private void subTaskADDUpdateHandler(HttpExchange httpExchange) throws IOException {
        try {
            String body = readText(httpExchange);
            SubTask task = gson.fromJson(body, SubTask.class);
            if (task != null) {
                if (taskManager.getTaskById(task.getId()) == null) {
                    //Добавляем
                    task = taskManager.addSubTask(task);
                } else {
                    //Обновление
                    task = taskManager.updateSubTask(task);
                }
                if (task != null) {
                    sendText(httpExchange, 201, gson.toJson(task));
                } else {
                    sendText(httpExchange, 400, "");
                }
            } else {
                //ресурс не найден
                taskErrorHandler(httpExchange,404, "Передан null");
            }
        } catch (JsonSyntaxException e) {
            taskErrorHandler(httpExchange,422, "Не получилось преобразовать в задачу.");
        } catch (NotValidate e) {
            taskErrorHandler(httpExchange,422, "Указанный период подзадачи занят.");
        } catch (ParentNotFound e) {
            taskErrorHandler(httpExchange,422, "Не найден родитель подзадачи.");
        } catch (Exception e) {
            taskErrorHandler(httpExchange,422, e.getMessage());
        }
    }

    private void epicADDUpdateHandler(HttpExchange httpExchange) throws IOException {
        try {
            String body = readText(httpExchange);
            Epic epic = gson.fromJson(body, Epic.class);
            if (epic != null) {
                if (taskManager.getTaskById(epic.getId()) == null) {
                    //Добавляем
                    epic = taskManager.addEpic(epic);
                } else {
                    //Обновление
                    epic = taskManager.updateEpic(epic);
                }
                if (epic != null) {
                    sendText(httpExchange, 201, gson.toJson(epic));
                } else {
                    sendText(httpExchange, 400, "");
                }
            } else {
                //ресурс не найден
                taskErrorHandler(httpExchange,404, "Передан null");
            }
        } catch (JsonSyntaxException e) {
            taskErrorHandler(httpExchange,422, "Не получилось преобразовать JSON в эпик.");
        } catch (Exception e) {
            taskErrorHandler(httpExchange,422, e.getMessage());
        }
    }

    private void taskIdDeleteHandler(HttpExchange httpExchange, String param) throws IOException {
        int id = parsePathId(param);
        if (id != -1) {
            final Task task = taskManager.deleteTaskById(id);
            if (task != null) {
                sendText(httpExchange, 200, gson.toJson(task));
            } else {
                taskErrorHandler(httpExchange, 404, "Задача с id =" + id + " не найдена.");
            }
        } else {
            taskErrorHandler(httpExchange, 404,
                    "Для метода DELETE получен не корректный id задачи = " + param);
        }
    }

    private void subTaskIdDeleteHandler(HttpExchange httpExchange, String param) throws IOException {
        int id = parsePathId(param);
        if (id != -1) {
            final SubTask task = taskManager.deleteSubTaskById(id);
            if (task != null) {
                sendText(httpExchange, 200, gson.toJson(task));
            }  else {
                taskErrorHandler(httpExchange, 404, "Подзадача с id =" + id + " не найдена.");
            }
        } else {
            taskErrorHandler(httpExchange, 404,
                    "Для метода DELETE получен не корректный id подзадачи = " + param);
        }
    }

    private void epicIdDeleteHandler(HttpExchange httpExchange, String param) throws IOException {
        int id = parsePathId(param);
        if (id != -1) {
            final Epic task = taskManager.deleteEpicById(id);
            if (task != null) {
                sendText(httpExchange, 200, gson.toJson(task));
            } else {
                taskErrorHandler(httpExchange, 404, "Эпик с id =" + id + " не найдена.");
            }
        } else {
            taskErrorHandler(httpExchange, 404,
                    "Для метода DELETE получен не корректный параметр " + param);
        }
    }

    private void childrenEpicByIdHandler(HttpExchange httpExchange, String param) throws IOException {
        int id = parsePathId(param);
        if (id != -1) {
            final Epic epic = taskManager.getEpicById(id);
            if (epic != null) {
                sendText(httpExchange, 200, gson.toJson(taskManager.getChildrenOfEpicById(id)));
            } else {
                taskErrorHandler(httpExchange, 404, "Эпик с id =" + id + " не найден.");
            }
        } else {
            taskErrorHandler(httpExchange, 404,
                    "Не корректный id эпика = " + param);
        }
    }

    private void taskDeleteHandler(HttpExchange httpExchange) throws IOException {
        taskManager.deleteAllTasks();
        sendText(httpExchange, 204, "");
    }

    private void subTaskDeleteHandler(HttpExchange httpExchange) throws IOException {
        taskManager.deleteAllSubTasks();
        sendText(httpExchange, 204, "");
    }

    private void epicDeleteHandler(HttpExchange httpExchange) throws IOException {
        taskManager.deleteAllEpics();
        sendText(httpExchange, 204, "");
    }

    private void historyHandler(HttpExchange httpExchange) throws IOException {
        sendText(httpExchange, 200, gson.toJson(taskManager.getHistory()));
    }

    private int parsePathId(String param) {
        try {
            String pathId = param.replaceFirst("id=", "");
            try {
                return Integer.parseInt(pathId);
            } catch (NumberFormatException e) {
                return -1;
            }
        } catch (Exception e) {
            return -1;
        }
    }

    protected void sendText(HttpExchange h, int code, String text) throws IOException {
        byte[] resp = text.getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
    }

    private void taskErrorHandler(HttpExchange h, int code, String textError) throws IOException {
        byte[] resp = gson.toJson(textError).getBytes(UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
    }

    protected String readText(HttpExchange h) throws IOException {
        return new String(h.getRequestBody().readAllBytes(), UTF_8);
    }
}
