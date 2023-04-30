package service;

import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static model.IssueStatus.NEW;
import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    private static TaskManager taskManager;

    @BeforeAll
    static void beforeAll() {
        //Для каждого теста создаем новый менеджер, чтобы тесты не зависели друг от друга
        taskManager = Managers.getDefault();
    }

    @Test
    void addTaskTest() {
        Task task = new Task(0, "Test addTask", "Test addTask description", Duration.ofMinutes(10));

        final Task newTask = taskManager.addTask(task);

        final Task savedTask = taskManager.getTaskById(newTask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи на возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addSubTask() {
    }

    @Test
    void addEpic() {
    }

    @Test
    void updateTask() {
    }

    @Test
    void updateSubTask() {
    }

    @Test
    void updateEpic() {
    }

    @Test
    void deleteTaskById() {
    }

    @Test
    void deleteSubTaskById() {
    }

    @Test
    void deleteEpicById() {
    }

    @Test
    void deleteAllTasks() {
    }

    @Test
    void deleteAllSubTasks() {
    }

    @Test
    void deleteAllEpics() {
    }

    @Test
    void getTaskById() {
    }

    @Test
    void getSubTaskById() {
    }

    @Test
    void getEpicById() {
    }

    @Test
    void getAllTasks() {
    }

    @Test
    void getAllSubTasks() {
    }

    @Test
    void getAllEpics() {
    }

    @Test
    void getChildrenOfEpicById() {
    }

    @Test
    void getHistory() {
    }
}