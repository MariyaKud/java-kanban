package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Запущено тестирование класса InMemoryHistoryManager..")
class TaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        //Для каждого теста создаем новый менеджер, чтобы тесты не зависели друг от друга
        final HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }

    private Task addTask() {
        final Task task = new Task("Test", "Description", Duration.ofMinutes(10));
        taskManager.addTask(task);
        return task;
    }

    private Epic addEpic() {
        final Epic epic = new Epic("Epic", "Description");
        taskManager.addEpic(epic);
        return epic;
    }

    private SubTask addSubTask(Epic epic, IssueStatus issueStatus) {
        final SubTask subTask = new SubTask("SubTask", "Description", Duration.ofMinutes(15),
                                                  epic.getId(), issueStatus);
        taskManager.addSubTask(subTask);
        return subTask;
    }

    @DisplayName("Добавляем задачу с параметром NULL.")
    @Test
    void addTaskNullTest() {
        final Task task = taskManager.addTask(null);

        assertNull(task, "Добавленная задача Null.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Хранилище задач не инициализировано.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @DisplayName("Добавляем подзадачу с параметром NULL.")
    @Test
    void addSubTaskNullTest() {
        final SubTask subTask = taskManager.addSubTask(null);

        assertNull(subTask, "Добавленная задача Null.");

        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Хранилище подзадач не инициализировано.");
        assertEquals(0, subTasks.size(), "Неверное количество подзадач.");
    }

    @DisplayName("Добавляем эпик с параметром NULL.")
    @Test
    void addEpicNullTest() {
        final Epic epic = taskManager.addEpic(null);

        assertNull(epic, "Добавлен эпик Null.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Хранилище эпиков не инициализировано.");
        assertEquals(0, epics.size(), "Неверное количество эпиков.");
    }

    @DisplayName("Добавляем задачу с собственным id")
    @Test
    void addTaskWithIdTest() {
        final Task task = new Task(99, "Test", "Description", Duration.ofMinutes(100));
        taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(task, "Добавленная задача Null.");
        assertEquals(1, savedTask.getId(), "Не корректный id.");
        assertEquals(100, savedTask.getDuration().toMinutes(), "Интервал.");
    }

    @DisplayName("Добавляем эпик с собственным id")
    @Test
    void addEpicWithIdTest() {
        final Epic epic = new Epic(99, "Epic", "Description");
        taskManager.addEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(epic, "Добавлен эпик Null.");
        assertEquals(1, savedEpic.getId(), "Не корректный id.");
        assertEquals(0, savedEpic.getDuration().toMinutes(), "Интервал.");
    }

    @DisplayName("Добавляем подзадачу с собственным id")
    @Test
    void addSubTaskWithIdTest() {
        final Epic parent = new Epic(99, "Epic", "Description");
        taskManager.addEpic(parent);
        final SubTask subTask = new SubTask(99, "SubTask", "Description", Duration.ofMinutes(500),
                                        parent.getId());
        taskManager.addSubTask(subTask);
        final SubTask savedSubTask = taskManager.getSubTaskById(subTask.getId());
        final Epic saveParent = taskManager.getEpicById(parent.getId());

        assertNotNull(subTask, "Добавленная задача Null.");
        assertEquals(1, saveParent.getId(), "Не корректный id.");
        assertEquals(2, savedSubTask.getId(), "Не корректный id.");
        assertEquals(500, savedSubTask.getDuration().toMinutes(), "Интервал.");
    }

    @DisplayName("Добавляем задачу.")
    @Test
    void addTaskTest() {

        final Task task = addTask();
        final Task savedTask = taskManager.getTaskById(task.getId());

        //Анализируем задачу
        assertNotNull(savedTask, "Добавленная задача Null.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(IssueStatus.NEW, savedTask.getStatus(), "Статус задачи.");

        //Анализируем список задач
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Хранилище задач не инициализировано.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @DisplayName("Добавляем эпик с пустым списком подзадач.")
    @Test
    void addEpicTest() {

        final Epic epic = addEpic();
        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Добавленный эпик Null.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(0, savedEpic.getChildren().size(), "Новый эпик с детьми.");
        assertEquals(savedEpic.getDuration(), Duration.ZERO, "У эпика не пустой интервал.");
        assertEquals(savedEpic.getStartTime(), LocalDateTime.MIN, "У эпика не пустой дата старта.");
        assertEquals(IssueStatus.NEW, savedEpic.getStatus(), "Статус эпика.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Хранилище эпиков не инициализировано.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @DisplayName("Добавляем эпик с подзадачей в статусе NEW.")
    @Test
    void addSubTaskTest() {

        final Epic parent = addEpic();
        final SubTask subTask = addSubTask(parent,IssueStatus.NEW);

        final SubTask savedSubTask = taskManager.getSubTaskById(subTask.getId());
        final Epic savedParent = taskManager.getEpicById(subTask.getParentID());

        assertNotNull(savedSubTask, "Добавленная подзадача Null.");
        assertEquals(subTask, savedSubTask, "Подзадачи не совпадают.");
        assertNotNull(savedParent, "Родитель не найден.");

        assertEquals(savedSubTask.getStartTime(), savedParent.getStartTime(), "Дата старта эпика.");
        assertEquals(savedSubTask.getEndTime(), savedParent.getEndTime(), "Дата завершения эпика.");

        assertEquals(IssueStatus.NEW, savedParent.getStatus(), "Статус эпика.");
        assertEquals(IssueStatus.NEW, savedSubTask.getStatus(), "Статус подзадачи.");

        assertEquals(1,savedParent.getChildren().size(),"Не верное количество детей.");

        final List<SubTask> subTasks = taskManager.getAllSubTasks();
        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(subTasks, "Хранилище подзадач не инициализировано.");
        assertEquals(1, subTasks.size(), "Неверное количество подзадач.");
        assertEquals(subTask, subTasks.get(0), "Подзадачи не совпадают.");

        assertNotNull(epics, "Хранилище эпиков не инициализировано.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
    }

    @DisplayName("Добавляем эпик с подзадачами в статусе IN_PROGRESS.")
    @Test
    void addEpicWithTwoChildrenInProgressTest() {

        final Epic parent = addEpic();
        final SubTask subTask1 = addSubTask(parent,IssueStatus.IN_PROGRESS);
        final SubTask subTask2 = addSubTask(parent,IssueStatus.IN_PROGRESS);

        final SubTask savedSubTask1 = taskManager.getSubTaskById(subTask1.getId());
        final SubTask savedSubTask2 = taskManager.getSubTaskById(subTask2.getId());
        final Epic savedParent = taskManager.getEpicById(parent.getId());

        assertEquals(IssueStatus.IN_PROGRESS, savedSubTask1.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.IN_PROGRESS, savedSubTask2.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.IN_PROGRESS, savedParent.getStatus(), "Статус эпика.");

        assertEquals(savedSubTask1.getStartTime(), savedParent.getStartTime(), "Дата старта эпика.");
        assertEquals(savedSubTask2.getEndTime(), savedParent.getEndTime(), "Дата завершения эпика.");

        assertEquals(2,savedParent.getChildren().size(),"Не верное количество детей.");

        final List<SubTask> subTasks = taskManager.getAllSubTasks();
        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(subTasks, "Хранилище подзадач не инициализировано.");
        assertEquals(2, subTasks.size(), "Неверное количество подзадач.");

        assertNotNull(epics, "Хранилище эпиков не инициализировано.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
    }

    @DisplayName("Добавляем эпик с подзадачами в статусе DONE.")
    @Test
    void addEpicWithTreeChildrenDoneTest() {

        final Epic parent = addEpic();
        final SubTask subTask1 = addSubTask(parent,IssueStatus.DONE);
        final SubTask subTask2 = addSubTask(parent,IssueStatus.DONE);
        final SubTask subTask3 = addSubTask(parent,IssueStatus.DONE);

        final SubTask savedSubTask1 = taskManager.getSubTaskById(subTask1.getId());
        final SubTask savedSubTask2 = taskManager.getSubTaskById(subTask2.getId());
        final SubTask savedSubTask3 = taskManager.getSubTaskById(subTask3.getId());
        final Epic savedParent = taskManager.getEpicById(parent.getId());

        assertEquals(IssueStatus.DONE, savedSubTask1.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.DONE, savedSubTask2.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.DONE, savedParent.getStatus(), "Статус эпика.");

        assertEquals(savedSubTask1.getStartTime(), savedParent.getStartTime(), "Дата старта эпика.");
        assertEquals(savedSubTask3.getEndTime(), savedParent.getEndTime(), "Дата завершения эпика.");
        //Добавить проверку интервала

        assertEquals(3,savedParent.getChildren().size(),"Не верное количество детей.");

        assertEquals(3, taskManager.getAllSubTasks().size(), "Неверное количество подзадач.");
        assertEquals(1, taskManager.getAllEpics().size(), "Неверное количество эпиков.");
    }

    @DisplayName("Добавляем эпик с подзадачами в статусе DONE и NEW.")
    @Test
    void addEpicWithTwoChildrenNewDoneTest() {

        final Epic parent = addEpic();
        final SubTask subTask1 = addSubTask(parent,IssueStatus.NEW);
        final SubTask subTask2 = addSubTask(parent,IssueStatus.DONE);

        final SubTask savedSubTask1 = taskManager.getSubTaskById(subTask1.getId());
        final SubTask savedSubTask2 = taskManager.getSubTaskById(subTask2.getId());
        final Epic savedParent = taskManager.getEpicById(parent.getId());

        assertEquals(IssueStatus.NEW, savedSubTask1.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.DONE, savedSubTask2.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.IN_PROGRESS, savedParent.getStatus(), "Статус эпика.");
    }

    @DisplayName("Обновить статус задачи с NEW в DONE.")
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

    @DisplayName("Обновить статус единственной подзадачи с NEW в DONE.")
    @Test
    void updateStatusSubTaskTest() {
        final Epic epic = addEpic();
        final SubTask subTask = addSubTask(epic,IssueStatus.NEW);
        subTask.setStatus(IssueStatus.DONE);
        final SubTask updateSubTask = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(updateSubTask, "Обновленная подзадача не найдена.");
        assertEquals(subTask, updateSubTask, "Подзадачи не совпадают.");
        assertEquals(IssueStatus.DONE, updateSubTask.getStatus(), "Статус подзадачи не обновлен.");
    }

    @DisplayName("Обновить статус эпика без подзадач в DONE.")
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

    @DisplayName("Удаление существующей задачи по id.")
    @Test
    void deleteTaskByIdTest() {
        //Стандартный вариант - когда есть задача
        final Task task = new Task(0, "Test addTask", "Test addTask description", Duration.ofMinutes(10),
                             LocalDateTime.now(), IssueStatus.IN_PROGRESS);

        final Task newTask = taskManager.addTask(task);
        taskManager.deleteTaskById(newTask.getId());
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Возвращает null.");
        assertEquals(0, tasks.size(), "Список задач не пуст.");
        assertNull(taskManager.getTaskById(0), "Задача найдена.");
    }

    @DisplayName("Не корректный id. Удаление задачи по id в пустом списке задач.")
    @Test
    void deleteTaskByIdWhenTasksEmptyTest() {

        final Task task = taskManager.deleteTaskById(100);
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Возвращает null.");
        assertEquals(0, tasks.size(), "Список задач не пуст.");
        assertNull(taskManager.getTaskById(100), "Задача найдена.");
        assertNull(task,"Возвращает не null.");
    }

    @DisplayName("Не корректный id. Удаление не существующей задачи по id.")
    @Test
    void deleteTaskByNotHaveIdTest() {

        final Task task = addTask();
        final Task newTask = taskManager.addTask(task);
        taskManager.deleteTaskById(100);
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Возвращает null.");
        assertNull(newTask, "Найдена не существующая задача.");
        assertEquals(1, tasks.size(), "Список задач не пуст.");
        assertNull(taskManager.getTaskById(100), "Задача найдена.");
    }

    @DisplayName("Удаление существующей подзадачи по id.")
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

    @DisplayName("Удаление существующей эпика по id.")
    @Test
    void deleteEpicById() {
        //Стандартный вариант - когда есть эпик
        final Epic newEpic = addEpic();
        taskManager.deleteEpicById(newEpic.getId());

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Возвращает null.");
        assertEquals(0, epics.size(), "Список эпиков не пуст.");
        assertNull(taskManager.getEpicById(newEpic.getId()), "Эпик не удален.");
    }

    @DisplayName("Не корректный id. Удаление эпика по id в пустом списке.")
    @Test
    void deleteEpicByIdWhenTasksEmptyTest() {
        final Epic epic = taskManager.deleteEpicById(100);
        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Возвращает null.");
        assertNull(epic, "Не получили null.");
        assertEquals(0, epics.size(), "Список эпиков не пуст.");
        assertNull(taskManager.getEpicById(100), "Эпик найден.");
    }

    @DisplayName("Не корректный id. Удаление не существующего эпика по id.")
    @Test
    void deleteEpicByNotHaveIdTest() {
        addEpic();
        final Epic delEpic = taskManager.deleteEpicById(100);
        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Возвращает null.");
        assertNull(delEpic, "Не получили null.");
        assertEquals(1, epics.size(), "Список эпиков не пуст.");
        assertNull(taskManager.getEpicById(100), "Эпик не удален.");
    }

    @DisplayName("Удалить все задачи, при пустом списке.")
    @Test
    void deleteAllTasksForNewManagerTest() {
        //Для нового менеджера, после удаления подзадач - пустой список и не null
        taskManager.deleteAllTasks();
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Возвращает null.");
        assertEquals(0, tasks.size(), "Список задач не пуст.");
    }

    @DisplayName("Удалить все подзадачи, при пустом списке.")
    @Test
    void deleteAllSubTasksForNewManagerTest() {
        //Для нового менеджера, после удаления подзадач - пустой список и не null
        taskManager.deleteAllSubTasks();
        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Возвращает null.");
        assertEquals(0, subTasks.size(), "Список подзадач не пуст.");
    }

    @DisplayName("Удалить все эпики, при пустом списке.")
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

    @DisplayName("Не корректный id. Найти задачу по не существующему id.")
    @Test
    void getEpicByIdWithNotCorrectIdTest() {
        //Ищем эпик с неверным id
        final Epic epic = new Epic(0, "Epic", "Description");

        final Epic newEpic  = taskManager.addEpic(epic);
        final Epic findEpic = taskManager.getEpicById(newEpic.getId()+1);

        assertNull(findEpic, "Не возвращает null.");
    }

    @DisplayName("Не корректный id. Найти подзадачу по не существующему id.")
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

    @DisplayName("Проверить список задач для нового менеджера.")
    @Test
    void getAllTasksForNewManagerTest() {
        //Для нового менеджера подзадачи пустой список и не null
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "История - null.");
        assertEquals(0, tasks.size(), "Хранилище задач не пустое для нового менеджера.");
    }

    @DisplayName("Проверить список подзадач для нового менеджера.")
    @Test
    void getAllSubTasksForNewManagerTest() {
        //Для нового менеджера подзадачи пустой список и не null
        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "История - null.");
        assertEquals(0, subTasks.size(), "Хранилище подзадач не пустое для нового менеджера.");
    }

    @DisplayName("Проверить список эпиков для нового менеджера.")
    @Test
    void getAllEpicsForNewManagerTest() {
        //Для нового менеджера эпики пустой список и не null
        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "История - null.");
        assertEquals(0, epics.size(), "Хранилище эпика не пустое для нового менеджера.");
    }

    @DisplayName("Проверить список детей для нового эпика.")
    @Test
    void getChildrenOfEpicByIdForNewEpicTest() {
        //Для нового эпика список подзадач пустой и не null
        Epic epic = new Epic(0, "Epic", "Description");
        final Epic newEpic = taskManager.addEpic(epic);

        final List<SubTask> children = taskManager.getChildrenOfEpicById(newEpic.getId());

        assertNotNull(children, "Список детей эпика - null.");
        assertEquals(0, children.size(), "Список детей эпика не пустая.");
    }

    @DisplayName("Получить историю списка для нового менеджера.")
    @Test
    void getHistoryForNewManagerTest() {
        //Для нового менеджера история задач пустой список и не null
        final List<Issue> history = taskManager.getHistory();

        assertNotNull(history, "История - null.");
        assertEquals(0, history.size(), "История не пустая для нового менеджера.");
    }
}