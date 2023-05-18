package controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dao.KVServer;
import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import org.junit.jupiter.api.Test;
import service.Managers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@DisplayName("Тест API менеджера задач..")
class HttpTaskServerTest {
    private final String urlServer = Managers.PATH_SERVER + Managers.PORT_HTTP_SERVER;
    private static final Gson gson = Managers.getGson();
    private KVServer kvServer;
    private HttpTaskServer httpTaskServer;
    private final HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    void beforeEach() {

        //Запускаем сервер хранилища
        try
        {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            System.out.println("Проблемы с подключением к серверу хранилища- " + e.getMessage());
            System.out.println("Тесты не состоялись!");
            assumeFalse(true, "Проблемы с доступом к серверу хранилища");

        }

        //Запускаем сервер API
        try
        {
            httpTaskServer = new HttpTaskServer();
            httpTaskServer.start();
        } catch (IOException e) {
            System.out.println("Проблемы с подключением к серверу API- " + e.getMessage());
            System.out.println("Тесты не состоялись!");
            assumeFalse(true, "Проблемы с доступом к серверу API");
        }
    }

    @AfterEach
    void AfterEach() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    @DisplayName("Должны получить статус 200 и отсортированные по дате задачи. Задача с пустой датой в конце.")
    @Test
    void shouldReturn() {
        Task task = addTask();
        SubTask subTask = addSubTask();
        final HttpResponse<String> response = testGet("/tasks");
        if (response != null && subTask != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");
            final List<Issue> loadTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
            }.getType());
            assertNotNull(loadTasks, "Вместо пустого списка получен null");
            assertNotEquals(0, loadTasks.size(), "Размер списка не верен.");
            assertEquals(2, loadTasks.size(), "Размер списка не верен.");
            assertEquals(subTask.getStartTime(),loadTasks.get(1).getStartTime(),"Не верная сортировка");
        }
    }

    @DisplayName("Должны получить статус 200 и пустой список задач.")
    @Test
    void shouldReturnEmptyListTaskForNewManager() {
        final HttpResponse<String> response = testGet("/tasks/task");
        if (response != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");
            final List<Task> loadTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
            }.getType());
            assertNotNull(loadTasks, "Вместо пустого списка получен null");
            assertEquals(0, loadTasks.size(), "Список подзадач должен быть пустым.");
        }
    }

    @DisplayName("Должны получить статус 200 и пустой список подзадач.")
    @Test
    void shouldReturnEmptyListSubTaskForNewManager() {
        final HttpResponse<String> response = testGet("/tasks/subtask");
        if (response != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");
            final List<SubTask> loadTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
            }.getType());
            assertNotNull(loadTasks, "Вместо пустого списка получен null");
            assertEquals(0, loadTasks.size(), "Список подзадач должен быть пустым.");
        }
    }

    @DisplayName("Должны получить статус 200 и пустой список эпиков.")
    @Test
    void shouldReturnEmptyListEpicForNewManager() {
        final HttpResponse<String> response = testGet("/tasks/epic");
        if (response != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");
            final List<Epic> loadTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {
            }.getType());
            assertNotNull(loadTasks, "Вместо пустого списка получен null");
            assertEquals(0, loadTasks.size(), "Список эпиков должен быть пустым.");
        }
    }

    @DisplayName("Должны получить статус 201, в теле json новой задачи.")
    @Test
    void shouldReturn201JsonAddTask() {
        final Task newTask = new Task(1, "Test", "Description", 1000);
        final HttpResponse<String> response = testPost("/tasks/task/", gson.toJson(newTask));
        if (response != null) {
            assertEquals(201, response.statusCode(), "Ожидали код 201.");

            final Task loadTask = gson.fromJson(response.body(), Task.class);
            assertNotNull(loadTask, "null");
            assertEquals(newTask, loadTask, "Задачи не равны.");
        }
    }

    @DisplayName("Должны получить статус 201, в теле json нового эпика.")
    @Test
    void shouldReturn201JsonAddEpic() {
        final Epic newTask = new Epic(1, "Test", "Description");
        final HttpResponse<String> response = testPost("/tasks/epic/", gson.toJson(newTask));
        if (response != null) {
            assertEquals(201, response.statusCode(), "Ожидали код 201.");

            final Epic loadTask = gson.fromJson(response.body(), Epic.class);
            assertNotNull(loadTask, "null");
            assertEquals(newTask, loadTask, "Эпики не равны.");
        }
    }

    @DisplayName("Должны получить статус 201, в теле json новой подзадачи.")
    @Test
    void shouldReturn201JsonAddSubTask() {
        final Epic epic = addEpic();
        if (epic != null) {
            final SubTask subTask = new SubTask(2, "SubTask", "Description", epic.getId(),
                    20, Instant.MAX);

            final HttpResponse<String> response = testPost("/tasks/subtask/", gson.toJson(subTask));
            if (response != null) {
                assertEquals(201, response.statusCode(), "Ожидали код 201.");

                final SubTask loadsubTask = gson.fromJson(response.body(), SubTask.class);
                assertNotNull(subTask, "null");
                assertEquals(subTask, loadsubTask, "Подзадачи не равны.");
                assertEquals(epic.getId(), loadsubTask.getParentID(), "Не верный id родителя.");
            }
        }
    }

    @DisplayName("Должны получить статус 200 в теле json список задач из одной задачи.")
    @Test
    void shouldReturn200JsonTasksWithOneTask() {
        final Task newTask = addTask();
        final HttpResponse<String> response = testGet("/tasks/task");
        if (newTask != null && response != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");
            final List<Task> loadTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {}.getType());
            assertNotNull(loadTasks, "Получен null");
            assertEquals(1, loadTasks.size(), "Не верный размер списка задач.");
            assertEquals(newTask, loadTasks.get(0), "Задачи получены не верно.");
        }
    }

    @DisplayName("Должны получить статус 200 в теле json не пустой список подзадач.")
    @Test
    void  shouldReturn200JsonSubTaskWithOneSubTask() {
        final SubTask newTask = addSubTask();
        final HttpResponse<String> response = testGet("/tasks/subtask");
        if (newTask != null && response != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");
            final List<SubTask> loadTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
            }.getType());
            assertNotNull(loadTasks, "Получен null");
            assertEquals(1, loadTasks.size(), "Не верный размер списка подзадач.");
            assertEquals(newTask, loadTasks.get(0), "Подзадачи получены не верно.");
        }
    }

    @DisplayName("Должны получить статус 200 в теле json не пустой список эпиков.")
    @Test
    void shouldReturn200JsonEpicsWithOneEpic() {
        final Epic newTask = addEpic();
        final HttpResponse<String> response = testGet("/tasks/epic");
        if (newTask != null && response != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");
            final List<Epic> loadTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Epic>>() {}.getType());
            assertNotNull(loadTasks, "Получен null");
            assertEquals(1, loadTasks.size(), "Не верный размер списка эпиков.");
            assertEquals(newTask, loadTasks.get(0), "Эпики получены не верно.");
        }
    }

    @DisplayName("Должны получить статус 200, в теле json найденная задача.")
    @Test
    void shouldReturn200JsonWithFindTask() {
        final Task newTask = addTask();
        final HttpResponse<String> response = testGet("/tasks/task/?id=1");
        if (response != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");

            final Task loadTask = gson.fromJson(response.body(), Task.class);
            assertNotNull(loadTask, "null");
            assertEquals(newTask, loadTask, "Задачи не равны.");
        }
    }
    @DisplayName("Должны получить статус 404, если нет задачи с id.")
    @Test
    void shouldReturn404NotExistTask() {
        final HttpResponse<String> response = testGet("/tasks/task/?id=1");
        if (response != null) {
            assertEquals(404, response.statusCode(), "Ожидали код 404.");
        }
    }

    @DisplayName("Должны получить статус 200, в теле json найденная подзадача.")
    @Test
    void shouldReturn200JsonWithFindSubTask() {
        final SubTask newTask = addSubTask();
        final HttpResponse<String> response = testGet("/tasks/subtask/?id=2");
        if (response != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");

            final SubTask loadTask = gson.fromJson(response.body(), SubTask.class);
            assertNotNull(loadTask, "null");
            assertEquals(newTask, loadTask, "Задачи не равны.");
        }
    }

    @DisplayName("Должны получить статус 404, если нет подзадачи с id.")
    @Test
    void shouldReturn404NotExistSubTask() {
        final HttpResponse<String> response = testGet("/tasks/subtask/?id=1");
        if (response != null) {
            assertEquals(404, response.statusCode(), "Ожидали код 404.");
        }
    }

    @DisplayName("Должны получить статус 200, в теле json найденный эпик.")
    @Test
    void shouldReturn200JsonWithFindEpic() {
        final Epic newTask = addEpic();
        final HttpResponse<String> response = testGet("/tasks/epic/?id=1");
        if (response != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");

            final Epic loadTask = gson.fromJson(response.body(), Epic.class);
            assertNotNull(loadTask, "null");
            assertEquals(newTask, loadTask, "Эпики не равны.");
        }
    }

    @DisplayName("Должны получить статус 404, если нет эпика с id.")
    @Test
    void shouldReturn404NotExistEpic() {
        final HttpResponse<String> response = testGet("/tasks/epic/?id=1");
        if (response != null) {
            assertEquals(404, response.statusCode(), "Ожидали код 404.");
        }
    }

    @DisplayName("Должны получить ошибку 400 при передачи в POST не корректных данных.")
    @Test
    void shouldReturn400IfPostWrongPath() {
        final Epic newTask = addEpic();
        final HttpResponse<String> response = testPost("/tasks/issue", gson.toJson(newTask));
        if (response != null) {
            assertEquals(400, response.statusCode(), "Ожидали код 400.");
        }
    }

    @DisplayName("Должны получить статус 200 в теле json ребенок эпика с заданным id в параметре.")
    @Test
    void  shouldReturn200JsonChildrenEpic() {
        final SubTask newTask = addSubTask();
        final HttpResponse<String> response = testGet("/tasks/subtask/epic/?id=1");
        if (newTask != null && response != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");
            final List<SubTask> loadTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<SubTask>>() {
            }.getType());
            assertNotNull(loadTasks, "Получен null");
            assertEquals(1, loadTasks.size(), "Не верный размер списка подзадач.");
            assertEquals(newTask, loadTasks.get(0), "Подзадачи получены не верно.");
        }
    }

    @DisplayName("Должны получить статус ошибки 404 при запросе детей по не существующему эпику.")
    @Test
    void  shouldReturn404NotExistEpicForAskChildren() {

        final HttpResponse<String> response = testGet("/tasks/subtask/epic/?id=1");
        if (response != null) {
            assertEquals(404, response.statusCode(), "Ожидали код 404.");
        }
    }

    @DisplayName("Должны получить статус ошибки 200, история из одной задачи с id=1")
    @Test
    void  shouldReturn200JsonListHistory() {
        Task newTask = addTask();
        testGet("/tasks/task/?id=1");//Добавляем задачу в историю просмотров
        final HttpResponse<String> response = testGet("/tasks/history");
        if (response != null) {
            assertEquals(200, response.statusCode(), "Не верный код ответа. Ожидали 200.");
            final List<Task> loadTasks = gson.fromJson(response.body(), new TypeToken<ArrayList<Task>>() {
            }.getType());
            assertNotNull(loadTasks, "Получен null");
            assertEquals(1, loadTasks.size(), "Не верный размер списка.");
            assertEquals(newTask, loadTasks.get(0), "История не верная.");
        }
    }

    @DisplayName("Должны получить статус ошибки 400 обращение не корректному пути запроса метода GET.")
    @Test
    void  shouldReturn400IfPathForGetWrong() {
        final HttpResponse<String> response = testGet("/tasks/thwth");
        if (response != null) {
            assertEquals(400, response.statusCode(), "Ожидали код 400.");
        }
    }

    @DisplayName("Должны получить статус успеха 204 и пустое тело.")
    @Test
    void  shouldReturn204AndEmptyTasks() {
        final HttpResponse<String> response = testDelete("/tasks/task");
        if (response != null) {
            assertEquals(204, response.statusCode(), "Ожидали код 204.");
            assertTrue(response.body().isEmpty(), "Пустой ответ.");
        }
    }

    @DisplayName("Должны получить статус успеха 204 и пустое тело.")
    @Test
    void  shouldReturn204AndEmptySubTasks() {
        final HttpResponse<String> response = testDelete("/tasks/subtask");
        if (response != null) {
            assertEquals(204, response.statusCode(), "Ожидали код 204.");
            assertTrue(response.body().isEmpty(), "Пустой ответ.");
        }
    }

    @DisplayName("Должны получить статус успеха 204 и пустое тело.")
    @Test
    void  shouldReturn204AndEmptyEpics() {
        final HttpResponse<String> response = testDelete("/tasks/epic");
        if (response != null) {
            assertEquals(204, response.statusCode(), "Ожидали код 204.");
            assertTrue(response.body().isEmpty(), "Пустой ответ.");
        }
    }

    @DisplayName("Должны получить статус успеха 200 и JSON удаленной задачи.")
    @Test
    void  shouldReturn200AndEmptyTask() {
        Task task = addTask();
        final HttpResponse<String> response = testDelete("/tasks/task/?id=1");
        if (response != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");

            final Task delTask = gson.fromJson(response.body(), Task.class);
            assertNotNull(delTask, "null");
            assertEquals(task, delTask, "Задачи не равны.");
        }
    }

    @DisplayName("Должны получить статус успеха 200 и JSON удаленной подзадачи.")
    @Test
    void  shouldReturn200AndEmptySubTask() {
        SubTask subTask = addSubTask();
        final HttpResponse<String> response = testDelete("/tasks/subtask/?id=2");
        if (response != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");

            final SubTask delSubTask = gson.fromJson(response.body(), SubTask.class);
            assertNotNull(delSubTask, "null");
            assertEquals(subTask, delSubTask, "Подзадачи не равны.");
        }
    }

    @DisplayName("Должны получить статус успеха 200 и JSON удаленный эпик.")
    @Test
    void  shouldReturn200AndEmptyEpic() {
        Epic task = addEpic();
        final HttpResponse<String> response = testDelete("/tasks/epic/?id=1");
        if (response != null) {
            assertEquals(200, response.statusCode(), "Ожидали код 200.");

            final Epic delTask = gson.fromJson(response.body(), Epic.class);
            assertNotNull(delTask, "null");
            assertEquals(task, delTask, "Эпики не равны.");
        }
    }

    private HttpResponse<String> testDelete(String path) {
        final HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlServer + path)).DELETE().build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("Тест метода DELETE не состоялся");
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    private HttpResponse<String> testGet(String path) {
        final HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlServer + path)).GET().build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("Тест метода GET не состоялся");
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    private HttpResponse<String> testPost(String path, String json) {
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlServer + path)).POST(body).build();
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            System.out.println("Тест метода POST не состоялся");
            System.out.println(Arrays.toString(e.getStackTrace()));
            return null;
        }
    }

    private Task addTask() {
        final URI url = URI.create(urlServer + "/tasks/task/");
        final Task newTask = new Task("Test", "Description", 1000, Instant.now());
        final String json  = gson.toJson(newTask);

        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        final HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();

        try {
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return gson.fromJson(response.body(), Task.class);

        } catch (Exception e) {
            return null;
        }
    }

    private Epic addEpic() {
        final Epic newTask = new Epic("Epic", "Description");
        final HttpResponse<String> response = testPost("/tasks/epic/", gson.toJson(newTask));
        if (response != null) {
            return gson.fromJson(response.body(), Epic.class);
        } else {
            return null;
        }
    }

    private SubTask addSubTask() {
        final Epic epic = addEpic();
        if (epic != null) {
            final SubTask subTask = new SubTask("SubTask", "Description", epic.getId(),
                    20, Instant.MAX, IssueStatus.NEW);

            final HttpResponse<String> response = testPost("/tasks/subtask/", gson.toJson(subTask));
            if (response != null && response.statusCode() == 201) {
                return gson.fromJson(response.body(), SubTask.class);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
}