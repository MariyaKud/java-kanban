package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        //Для каждого теста создаем новый менеджер, чтобы тесты не зависели друг от друга
        final HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    private Task addTask() {
        Task task = new Task(0, "Test", "Description", Duration.ofMinutes(10));
        taskManager.addTask(task);
        return task;
    }

    private SubTask addSubTask() {
        Epic epic = new Epic(0, "Epic", "Description");
        Epic newEpic = taskManager.addEpic(epic);

        SubTask subTask = new SubTask(1, "SubTask", "Description",
                Duration.ofMinutes(15), LocalDateTime.now(), newEpic.getId());

        taskManager.addSubTask(subTask);
        return subTask;
    }

    private Epic addEpic() {
        Epic epic = new Epic(0, "Epic", "Description");
        taskManager.addEpic(epic);
        return epic;
    }

    @Test
    void addTaskTest() {
        //addTask - cтандартное поведение
        final Task task = addTask();
        final Task savedTask = taskManager.getTaskById(task.getId());

        //Анализируем задачу
        assertNotNull(savedTask, "Добавленная задача не найдена.");
        assertEquals(task, savedTask, "Добавляемая задача не совпадают с задачей из менеджера с тем же id.");

        //Анализируем список задач
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Задачи в менеджере NULL.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void addSubTaskTest() {
        //addSubTask - cтандартное поведение
        final SubTask subTask = addSubTask();
        final SubTask savedSubTask = taskManager.getSubTaskById(subTask.getId());
        final Epic parent = taskManager.getEpicById(subTask.getParentID());

        assertNotNull(savedSubTask, "Добавленная подзадача не найдена.");
        assertEquals(subTask, savedSubTask, "Добавляемая подзадача не совпадают с " +
                                                    "подзадачей из менеджера с тем же id.");
        assertNotNull(parent, "Родитель не найден.");

        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Подзадачи на возвращаются.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");
    }

    @Test
    void addEpicTest() {
        //addEpic - cтандартное поведением
        final Epic epic = addEpic();
        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Добавленный эпик не найден.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @Test
    void updateStatusTaskTest() {
        final Task task = addTask();
        task.setStatus(IssueStatus.DONE);
        taskManager.updateTask(task);
        final Task updateTask = taskManager.getTaskById(task.getId());

        assertNotNull(updateTask, "Задачи не возвращаются.");
        assertEquals(task, updateTask, "Задачи не совпадают.");
        assertEquals(IssueStatus.DONE, updateTask.getStatus(), "Статус задачи не обновлен.");
    }

    @Test
    void updateStatusSubTaskTest() {
        final SubTask subTask = addSubTask();
        subTask.setStatus(IssueStatus.DONE);
        final SubTask updateSubTask = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(updateSubTask, "Обновленная подзадача не найдена.");
        assertEquals(subTask, updateSubTask, "Подзадачи не совпадают.");
        assertEquals(IssueStatus.DONE, updateSubTask.getStatus(), "Статус подзадачи не обновлен.");
    }

    @Test
    void updateStatusEpicTest() {
        //Статус эпика расчетная величина, его нельзя установить
        final Epic epic = addEpic();

        epic.setStatus(IssueStatus.DONE);
        taskManager.updateEpic(epic);

        final Epic updateEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(updateEpic, "Эпики не возвращаются.");
        assertNotEquals(IssueStatus.DONE, updateEpic.getStatus(), "Статус эпика обновлен.");
    }

    @Test
    void deleteTaskByIdTest() {
        //Стандартный вариант - когда есть задача
        Task task = new Task(0, "Test addTask", "Test addTask description", Duration.ofMinutes(10),
                             LocalDateTime.now(), IssueStatus.IN_PROGRESS);

        final Task newTask = taskManager.addTask(task);
        taskManager.deleteTaskById(newTask.getId());
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Возвращает null.");
        assertEquals(0, tasks.size(), "Список задач не пуст.");
        assertNull(taskManager.getTaskById(0), "Задача не удалена.");
    }

    @Test
    void deleteSubTaskByIdTest() {
        //Стандартный вариант - когда есть подзадача
        final Epic epic = new Epic(0, "Epic", "Description");
        final Epic newEpic = taskManager.addEpic(epic);

        final SubTask subTask = new SubTask(1, "SubTask", "Description",
                Duration.ofMinutes(15), LocalDateTime.now(), newEpic.getId());

        taskManager.deleteTaskById(subTask.getId());

        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Возвращает null.");
        assertEquals(0, subTasks.size(), "Список подзадач не пуст.");
        assertNull(taskManager.getSubTaskById(subTask.getId()), "Подзадача не удалена.");
    }

    @Test
    void deleteEpicById() {
        //Стандартный вариант - когда есть эпик
        final Epic epic = new Epic(0, "Epic", "Description");
        final Epic newEpic = taskManager.addEpic(epic);

        taskManager.deleteEpicById(newEpic.getId());

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Возвращает null.");
        assertEquals(0, epics.size(), "Список эпиков не пуст.");
        assertNull(taskManager.getEpicById(newEpic.getId()), "Эпик не удален.");
    }

    @Test
    void deleteAllTasksForNewManagerTest() {
        //Для нового менеджера, после удаления подзадач - пустой список и не null
        taskManager.deleteAllTasks();
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Возвращает null.");
        assertEquals(0, tasks.size(), "Список задач не пуст.");
    }

    @Test
    void deleteAllSubTasksForNewManagerTest() {
        //Для нового менеджера, после удаления подзадач - пустой список и не null
        taskManager.deleteAllSubTasks();
        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Возвращает null.");
        assertEquals(0, subTasks.size(), "Список подзадач не пуст.");
    }

    @Test
    void deleteAllEpicsForNewManagerTest() {
        //Для нового менеджера, после удаления эпики - пустой список и не null
        taskManager.deleteAllEpics();
        final List<Epic> epics = taskManager.getAllEpics();
        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(epics, "Возвращает null.");
        assertEquals(0, epics.size(), "Список эпиков не пуст.");

        assertNotNull(subTasks, "Возвращает null.");
        assertEquals(0, subTasks.size(), "Список подзадач не пуст.");
    }

    @Test
    void getTaskByIdWithNotCorrectIdTest() {
        //Ищем задачу с неверным id
        final Task task = new Task(0, "Task", "Description", Duration.ofMinutes(10));

        final Task newTask  = taskManager.addTask(task);
        final Task findTask = taskManager.getTaskById(newTask.getId()+1);

        assertNull(findTask, "Не возвращает null.");
    }

    @Test
    void getSubTaskByIdWithNotCorrectIdTest() {
        //Ищем подзадачу с неверным id
        final Epic epic = new Epic(0, "Epic", "Description");
        final Epic newEpic = taskManager.addEpic(epic);

        final SubTask subTask = new SubTask(1, "SubTask", "Description",
                                             Duration.ofMinutes(15), LocalDateTime.now(), newEpic.getId());

        final SubTask newSubTask = taskManager.addSubTask(subTask);
        final SubTask savedSubTask = taskManager.getSubTaskById(newSubTask.getId()+1);

        assertNull(savedSubTask, "Не возвращает null.");
    }

    @Test
    void getEpicByIdWithNotCorrectIdTest() {
        //Ищем эпик с неверным id
        final Epic epic = new Epic(0, "Epic", "Description");

        final Epic newEpic  = taskManager.addEpic(epic);
        final Epic findEpic = taskManager.getEpicById(newEpic.getId()+1);

        assertNull(findEpic, "Не возвращает null.");
    }

    @Test
    void getAllTasksForNewManagerTest() {
        //Для нового менеджера подзадачи пустой список и не null
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "История - null.");
        assertEquals(0, tasks.size(), "Хранилище задач не пустое для нового менеджера.");
    }

    @Test
    void getAllSubTasksForNewManagerTest() {
        //Для нового менеджера подзадачи пустой список и не null
        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "История - null.");
        assertEquals(0, subTasks.size(), "Хранилище подзадач не пустое для нового менеджера.");
    }

    @Test
    void getAllEpicsForNewManagerTest() {
        //Для нового менеджера эпики пустой список и не null
        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "История - null.");
        assertEquals(0, epics.size(), "Хранилище эпика не пустое для нового менеджера.");
    }

    @Test
    void getChildrenOfEpicByIdForNewEpicTest() {
        //Для нового эпика список подзадач пустой и не null
        Epic epic = new Epic(0, "Epic", "Description");
        final Epic newEpic = taskManager.addEpic(epic);

        final List<SubTask> children = taskManager.getChildrenOfEpicById(epic.getId());

        assertNotNull(children, "Список детей эпика - null.");
        assertEquals(0, children.size(), "Список детей эпика не пустая.");
    }

    @Test
    void getHistoryForNewManagerTest() {
        //Для нового менеджера история задач пустой список и не null
        final List<Issue> history = taskManager.getHistory();

        assertNotNull(history, "История - null.");
        assertEquals(0, history.size(), "История не пустая для нового менеджера.");
    }
}