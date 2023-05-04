package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DisplayName("Тест менеджера задач.")
class InMemoryTaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void beforeEach() {
        //Для каждого теста создаем новый менеджер, чтобы тесты не зависели друг от друга
        final HistoryManager historyManager = new InMemoryHistoryManager();
        taskManager = new InMemoryTaskManager(historyManager);
    }


    @DisplayName("Добавляем задачу с параметром null.")
    @Test
    void addTaskNullTest() {
        final Task task = taskManager.addTask(null);

        assertNull(task, "Добавленная задача null.");

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
        final Task task = new Task(99, "Test", "Description", Duration.ofMinutes(1000));
        taskManager.addTask(task);
        final Task savedTask = taskManager.getTaskById(task.getId());
        System.out.println(task.getStartTime());
        System.out.println(task.getEndTime());

        assertNotNull(task, "Добавленная задача Null.");
        //не совпадут только id, он будет переназначен менеджером
        assertEquals(1, savedTask.getId(), "Не корректный id.");
        //Проверка остальных реквизитов задачи
        assertEquals(IssueType.TASK, savedTask.getType(), "Не корректный Type.");
        assertEquals(IssueStatus.NEW, savedTask.getStatus(), "Не корректный Type.");
        assertEquals("Test", savedTask.getTitle(), "Не корректный Title.");
        assertEquals("Description", savedTask.getDescription(), "Не корректный Description.");
        assertEquals(1000, savedTask.getDuration().toMinutes(), "Не корректный Duration.");
        assertNotEquals(Instant.MIN, savedTask.getStartTime(), "Не корректный StartTime.");

        assertEquals(1,taskManager.getAllTasks().size(),"Не корректный список задач.");
    }

    @DisplayName("Добавляем эпик с собственным id")
    @Test
    void addEpicWithIdTest() {
        final Epic epic = new Epic(99, "Test", "Description");
        taskManager.addEpic(epic);
        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(epic, "Добавлен эпик Null.");
        //не совпадут только id, он будет переназначен менеджером
        assertEquals(1, savedEpic.getId(), "Не корректный id.");
        //Проверка остальных реквизитов задачи
        assertEquals(IssueType.EPIC, savedEpic.getType(), "Не корректный Type.");
        assertEquals(IssueStatus.NEW, savedEpic.getStatus(), "Не корректный Type.");
        assertEquals("Test", savedEpic.getTitle(), "Не корректный Title.");
        assertEquals("Description", savedEpic.getDescription(), "Не корректный Description.");
        assertEquals(0, savedEpic.getDuration().toMinutes(), "Не корректный Duration.");
        assertEquals(Instant.MIN, savedEpic.getStartTime(), "Не корректный StartTime.");
        assertEquals(Instant.MIN, savedEpic.getEndTime(), "Не корректный EndTime.");

        assertEquals(1,taskManager.getAllEpics().size(),"Не корректный список эпиков.");
    }

    @DisplayName("Добавляем подзадачу с собственным id")
    @Test
    void addSubTaskWithIdTest() {
        final Epic parent = Managers.addEpic(taskManager);
        final SubTask subTask = new SubTask(99, "Test", "Description", Duration.ofMinutes(1500),
                                        parent.getId());
        taskManager.addSubTask(subTask);

        System.out.println(parent.getStartTime());
        System.out.println(parent.getEndTime());

        final SubTask savedSubTask = taskManager.getSubTaskById(subTask.getId());
        final Epic saveParent = taskManager.getEpicById(parent.getId());

        assertNotNull(subTask, "Добавленная задача Null.");
        assertEquals(1, saveParent.getId(), "Не корректный id родителя.");
        assertEquals(2, savedSubTask.getId(), "Не корректный id.");
        assertEquals(parent.getId(), savedSubTask.getParentID(), "Не корректный id родителя.");

        assertEquals(1500, savedSubTask.getDuration().toMinutes(), "Не корректный Duration.");
        assertEquals(1500, saveParent.getDuration().toMinutes(), "Не корректный Duration родителя.");

        assertEquals(IssueType.SUBTASK, savedSubTask.getType(), "Не корректный Type.");
        assertEquals(IssueType.EPIC, saveParent.getType(), "Не корректный Type.");

        assertEquals(IssueStatus.NEW, savedSubTask.getStatus(), "Не корректный Type.");
        assertEquals(IssueStatus.NEW, saveParent.getStatus(), "Не корректный Type.");

        assertEquals("Test", savedSubTask.getTitle(), "Не корректный Title.");
        assertEquals("Description", savedSubTask.getDescription(), "Не корректный Description.");

        assertNotEquals(Instant.MIN, savedSubTask.getStartTime(), "Не корректный StartTime.");
        assertNotEquals(Instant.MIN, saveParent.getStartTime(), "Не корректный StartTime.");
        assertNotEquals(Instant.MIN, saveParent.getEndTime(), "Не корректный EndTime.");

        assertEquals(1,taskManager.getAllEpics().size(),"Не корректный список эпиков.");
        assertEquals(1,taskManager.getAllSubTasks().size(),"Не корректный список эпиков.");
    }

    @DisplayName("Добавляем задачу.")
    @Test
    void addTaskTest() {

        final Task task = Managers.addTask(taskManager);
        final Task savedTask = taskManager.getTaskById(task.getId());

        //Анализируем задачу
        assertNotNull(savedTask, "Добавленная задача Null.");
        assertEquals(task, savedTask, "Задачи не совпадают.");
        assertEquals(IssueStatus.NEW, savedTask.getStatus(), "Статус задачи.");
        assertEquals(savedTask,taskManager.getTaskById(1), "Не та задача");

        //Анализируем список задач
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Хранилище задач не инициализировано.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(1,taskManager.getAllTasks().size(), "Не верное количество задач");
        assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @DisplayName("Добавляем эпик с пустым списком подзадач.")
    @Test
    void addEpicTest() {

        final Epic epic = Managers.addEpic(taskManager);
        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Добавленный эпик Null.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(0, savedEpic.getChildren().size(), "Есть дети.");
        assertEquals(Duration.ZERO, savedEpic.getDuration(), "Не корректный Duration у эпика.");
        assertEquals(Instant.MIN, savedEpic.getStartTime(), "Не корректный StartTime у эпика.");
        assertEquals(IssueStatus.NEW, savedEpic.getStatus(), "Не корректный статус.");
        assertEquals(epic,taskManager.getEpicById(1), "Не тот эпик");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Хранилище эпиков не инициализировано.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @DisplayName("Добавляем эпик с подзадачей в статусе NEW.")
    @Test
    void addSubTaskTest() {

        final Epic parent = Managers.addEpic(taskManager);
        final SubTask subTask = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.NEW);

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

        assertEquals(parent,taskManager.getEpicById(1), "Не тот эпик");
        assertEquals(subTask,taskManager.getSubTaskById(2), "Не та подзадача");

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

        final Epic parent = Managers.addEpic(taskManager);
        final SubTask subTask1 = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.IN_PROGRESS);
        final SubTask subTask2 = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.IN_PROGRESS);

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

        final Epic parent = Managers.addEpic(taskManager);
        final SubTask subTask1 = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.DONE);
        final SubTask subTask2 = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.DONE);
        final SubTask subTask3 = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.DONE);

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
        assertEquals(3, taskManager.getChildrenOfEpicById(savedParent.getId()).size(),
                "Не верное количество детей.");

        assertEquals(3, taskManager.getAllSubTasks().size(), "Неверное количество подзадач.");
        assertEquals(1, taskManager.getAllEpics().size(), "Неверное количество эпиков.");

        assertEquals(1,taskManager.getAllEpics().size(), "Не верное количество эпиков");
        assertEquals(3,taskManager.getAllSubTasks().size(), "Не верное количество подзадач");
    }

    @DisplayName("Добавляем эпик с подзадачами в статусе DONE и NEW.")
    @Test
    void addEpicWithTwoChildrenNewDoneTest() {

        final Epic parent = Managers.addEpic(taskManager);
        final SubTask subTask1 = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.NEW);
        final SubTask subTask2 = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.DONE);

        final SubTask savedSubTask1 = taskManager.getSubTaskById(subTask1.getId());
        final SubTask savedSubTask2 = taskManager.getSubTaskById(subTask2.getId());
        final Epic savedParent = taskManager.getEpicById(parent.getId());

        assertEquals(IssueStatus.NEW, savedSubTask1.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.DONE, savedSubTask2.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.IN_PROGRESS, savedParent.getStatus(), "Статус эпика.");
    }

    @DisplayName("Пересечение интервалов задач при добавлении.")
    @Test
    void addTaskWithCrossTest() {
        Task task1 = Managers.addTask(taskManager);
        Task task2 = new Task(0,"Test", "Description",task1.getDuration(),
                task1.getStartTime(),IssueStatus.DONE);

        Task crossTask = taskManager.addTask(task2);

        assertNull(crossTask, "Ошибка валидации");
        assertEquals(1, taskManager.getAllTasks().size(), "Ошибка валидации. В списке задач ошибка.");
    }

    @DisplayName("Изменить интервал задачи на доступный.")
    @Test
    void addTaskWithoutCrossTest() {
        final Task task1 = Managers.addTask(taskManager);
        final Task task2 = Managers.addTask(taskManager);

        final Task taskToUpdate = new Task(task1);
        taskToUpdate.setStartTime(task2.getEndTime().plusSeconds(960));
        final Task updateTask = taskManager.updateTask(taskToUpdate);

        assertNotNull(updateTask, "Ошибка валидации");
        assertEquals(2, taskManager.getAllTasks().size(), "Ошибка валидации. В списке задач ошибка.");
        assertEquals(taskToUpdate.getStartTime(),updateTask.getStartTime(),"Стартовое время " +
                                                                                  "обновлено не корректно");
    }

    @DisplayName("Пересечение интервалов задач при обновлении")
    @Test
    void updateTaskWithCrossTest() {
        final Task task1 = Managers.addTask(taskManager);
        final Task task2 = Managers.addTask(taskManager);

        final Task updateTask = new Task(task2);
        updateTask.setStartTime(task1.getStartTime());
        updateTask.setDuration(task1.getDuration());

        final Task crossTask = taskManager.updateTask(updateTask);

        final List<Task> tasks = taskManager.getAllTasks();

        assertNull(crossTask, "Ошибка валидации");
        assertEquals(2, tasks.size(), "Ошибка валидации. В списке задач ошибка.");
        assertEquals(task2, taskManager.getTaskById(task2.getId()), "Задача обновлена.");
        assertNotEquals(updateTask, taskManager.getTaskById(task2.getId()), "Задача обновлена.");
    }

    @DisplayName("Обновить статус задачи с NEW в DONE.")
    @Test
    void updateStatusTaskTest() {
        final Task task = Managers.addTask(taskManager);
        final Task updateForTask = new Task(task);

        updateForTask.setStatus(IssueStatus.DONE);
        taskManager.updateTask(updateForTask);

        final Task updateTask = taskManager.getTaskById(task.getId());

        assertNotNull(updateTask, "Задачи не возвращаются.");
        assertEquals(updateForTask, updateTask, "Задачи не совпадают.");
        assertEquals(IssueStatus.DONE, updateTask.getStatus(), "Статус задачи не обновлен.");
    }

    @DisplayName("Обновить статус единственной подзадачи с NEW в DONE.")
    @Test
    void updateStatusSubTaskTest() {
        final Epic parent = Managers.addEpic(taskManager);
        final SubTask subTask = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.NEW);

        final SubTask subTaskToUpdate = new SubTask(subTask);
        subTaskToUpdate.setStatus(IssueStatus.DONE);
        taskManager.updateSubTask(subTaskToUpdate);

        final SubTask updateSubTask = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(updateSubTask, "Обновленная подзадача не найдена.");
        assertEquals(subTaskToUpdate, updateSubTask, "Подзадачи не совпадают.");
        assertEquals(IssueStatus.DONE, updateSubTask.getStatus(), "Статус подзадачи не обновлен.");
    }

    @DisplayName("Обновить дату старта подзадачи на свободную.")
    @Test
    void updateStartTimeSubTaskTest() {
        final Epic parent = Managers.addEpic(taskManager);
        final SubTask subTask1 = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.NEW);
        final SubTask subTask2 = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.NEW);

        final SubTask subTaskToUpdate = new SubTask(subTask1);
        subTaskToUpdate.setStartTime(subTask2.getEndTime().plusSeconds(960));
        taskManager.updateSubTask(subTaskToUpdate);
        final SubTask updateSubTask = taskManager.getSubTaskById(subTask1.getId());

        assertNotNull(updateSubTask, "Обновленная подзадача не найдена.");
        assertEquals(subTaskToUpdate.getStartTime(), updateSubTask.getStartTime(), "Ошибка обновления.");
    }

    @DisplayName("Пересечение интервалов задач при обновлении.")
    @Test
    void updateStartTimeWithCrossSubTaskTest() {
        final Epic parent = Managers.addEpic(taskManager);
        final SubTask subTask1 = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.NEW);
        final SubTask subTask2 = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.NEW);

        final SubTask subTaskToUpdate = new SubTask(subTask1);
        subTaskToUpdate.setStartTime(subTask2.getStartTime());
        final SubTask updateSubTask = taskManager.updateSubTask(subTaskToUpdate);

        taskManager.getSubTaskById(subTask1.getId());
        assertNull(updateSubTask, "Ошибка обновления подзадачи.");

        assertEquals(subTask1.getStartTime(), taskManager.getSubTaskById(subTask1.getId()).getStartTime(),
                "Ошибка обновления подзадачи.");
    }

    @DisplayName("Обновить статус эпика без подзадач в DONE.")
    @Test
    void updateStatusEpicTest() {
        //Статус эпика расчетная величина, его нельзя установить
        final Epic epic = Managers.addEpic(taskManager);
        final Epic epicToUpdate = new Epic(epic);

        epicToUpdate.setStatus(IssueStatus.DONE);
        taskManager.updateEpic(epicToUpdate);

        final Epic updateEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(updateEpic, "Эпики не возвращаются.");
        assertNotEquals(IssueStatus.DONE, updateEpic.getStatus(), "Статус эпика обновлен.");
    }

    @DisplayName("Обновить статус подзадач для эпика с 2 детьми")
    @Test
    void updateStatusEpicWithTwoChildrenTest() {

        final Epic parent = Managers.addEpic(taskManager);
        final SubTask subTask1 = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.NEW);
        final SubTask subTask2 = Managers.addSubTask(taskManager,parent.getId(),IssueStatus.NEW);

        SubTask subTask1ToUpdate = new SubTask(subTask1);
        subTask1ToUpdate.setStatus(IssueStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask1ToUpdate);

        SubTask savedSubTask1 = taskManager.getSubTaskById(subTask1.getId());
        SubTask savedSubTask2 = taskManager.getSubTaskById(subTask2.getId());
        Epic savedParent = taskManager.getEpicById(parent.getId());

        assertEquals(IssueStatus.IN_PROGRESS, savedSubTask1.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.NEW, savedSubTask2.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.IN_PROGRESS, savedParent.getStatus(), "Статус эпика.");

        subTask1ToUpdate.setStatus(IssueStatus.DONE);
        taskManager.updateSubTask(subTask1ToUpdate);
        savedSubTask1 = taskManager.getSubTaskById(subTask1.getId());
        savedParent = taskManager.getEpicById(parent.getId());

        assertEquals(IssueStatus.DONE, savedSubTask1.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.NEW, savedSubTask2.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.IN_PROGRESS, savedParent.getStatus(), "Статус эпика.");

        SubTask subTask2ToUpdate = new SubTask(subTask2);
        subTask2ToUpdate.setStatus(IssueStatus.IN_PROGRESS);
        taskManager.updateSubTask(subTask2ToUpdate);
        savedSubTask2 = taskManager.getSubTaskById(subTask2.getId());
        savedParent = taskManager.getEpicById(parent.getId());

        assertEquals(IssueStatus.DONE, savedSubTask1.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.IN_PROGRESS, savedSubTask2.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.IN_PROGRESS, savedParent.getStatus(), "Статус эпика.");

        subTask2ToUpdate.setStatus(IssueStatus.DONE);
        taskManager.updateSubTask(subTask2ToUpdate);
        savedSubTask2 = taskManager.getSubTaskById(subTask2.getId());
        savedParent = taskManager.getEpicById(parent.getId());

        assertEquals(IssueStatus.DONE, savedSubTask1.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.DONE, savedSubTask2.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.DONE, savedParent.getStatus(), "Статус эпика.");

        assertEquals(2, taskManager.getChildrenOfEpicById(parent.getId()).size(),
                                 "Не верное количество детей.");

        assertEquals(1,taskManager.getAllEpics().size(), "Не верное количество эпиков");
        assertEquals(2,taskManager.getAllSubTasks().size(), "Не верное количество подзадач");
    }

    @DisplayName("Удаление существующей задачи по id.")
    @Test
    void deleteTaskByIdTest() {
        //Стандартный вариант - когда есть задача
        final Task task = Managers.addTask(taskManager);
        taskManager.deleteTaskById(task.getId());
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

        final Task task = Managers.addTask(taskManager);
        final Task delTask = taskManager.deleteTaskById(100);
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Возвращает null.");
        assertNotEquals(task,delTask, "Удаляем существующую задачу");
        assertNull(delTask, "Найдена не существующая задача.");
        assertEquals(1, tasks.size(), "Список задач не пуст.");
        assertNull(taskManager.getTaskById(100), "Задача найдена.");
    }

    @DisplayName("Удаление существующей подзадачи по id.")
    @Test
    void deleteSubTaskByIdTest() {
        //Стандартный вариант - когда есть подзадача
        final Epic newEpic = Managers.addEpic(taskManager);
        final SubTask subTask = Managers.addSubTask(taskManager,newEpic.getId(),IssueStatus.DONE);

        final SubTask delSubTask = taskManager.deleteSubTaskById(subTask.getId());
        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(delSubTask, "Возвращает null.");
        assertEquals(0, subTasks.size(), "Список подзадач не пуст.");
        assertNull(taskManager.getSubTaskById(subTask.getId()), "Подзадача не удалена.");
    }

    @DisplayName("Удаление существующей эпика по id.")
    @Test
    void deleteEpicById() {
        //Стандартный вариант - когда есть эпик
        final Epic newEpic = Managers.addEpic(taskManager);
        taskManager.deleteEpicById(newEpic.getId());

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Возвращает null.");
        assertEquals(0, epics.size(), "Список эпиков не пуст.");
        assertNull(taskManager.getEpicById(newEpic.getId()), "Эпик не удален.");
    }

    @DisplayName("Удаление существующей эпика с детьми по id.")
    @Test
    void deleteEpicWithChildrenById() {
        //Стандартный вариант - когда есть эпик
        final Epic epic = Managers.addEpic(taskManager);
        final SubTask subTask = Managers.addSubTask(taskManager,epic.getId(),IssueStatus.IN_PROGRESS);

        taskManager.deleteEpicById(epic.getId());

        final List<Epic> epics = taskManager.getAllEpics();
        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(epics, "Возвращает null.");

        assertEquals(0, epics.size(), "Список эпиков не пуст.");
        assertEquals(0, subTasks.size(), "Список подзадач не пуст.");
        assertEquals(0, taskManager.getChildrenOfEpicById(1).size(), "Список эпиков не пуст.");

        assertNull(taskManager.getEpicById(epic.getId()), "Эпик не удален.");
        assertNull(taskManager.getSubTaskById(subTask.getId()), "Ребенок не удален.");
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
        Managers.addEpic(taskManager);
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

    @DisplayName("Удалить все задачи, при наличии задач.")
    @Test
    void deleteAllTasksTest(){
        Managers.addTask(taskManager);

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

    @DisplayName("Удалить все подзадачи, при наличии подзадач.")
    @Test
    void deleteAllSubTasksTest(){
        Epic epic = Managers.addEpic(taskManager);
        Managers.addSubTask(taskManager, epic.getId(), IssueStatus.NEW);

        taskManager.deleteAllSubTasks();
        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Возвращает null.");
        assertEquals(0, subTasks.size(), "Список подзадач не пуст.");
        assertEquals(0, epic.getChildren().size(), "Список подзадач не пуст.");
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

    @DisplayName("Удалить все эпики при наличии эпика с детьми.")
    @Test
    void deleteAllEpicWithChildren() {
        Epic epic = Managers.addEpic(taskManager);
        Managers.addSubTask(taskManager, epic.getId(), IssueStatus.NEW);

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков не пуст.");
        assertEquals(0, taskManager.getAllSubTasks().size(), "Список подзадач не пуст.");
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
        final Epic parent = Managers.addEpic(taskManager);
        Managers.addSubTask(taskManager,parent.getId(),IssueStatus.NEW);

        final SubTask savedSubTask = taskManager.getSubTaskById(parent.getId());

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

    @DisplayName("Получить историю просмотров для нового менеджера.")
    @Test
    void getHistoryForNewManagerTest() {
        //Для нового менеджера история задач пустой список и не null
        final List<Issue> history = taskManager.getHistory();

        assertNotNull(history, "История - null.");
        assertEquals(0, history.size(), "История не пустая для нового менеджера.");
    }

    @DisplayName("Получить историю просмотров для менеджера в работе.")
    @Test
    void getHistoryTest() {
        Managers.simpleTestForTaskManager(taskManager);

        final List<Issue> history = taskManager.getHistory();

        assertNotNull(history, "История - null.");
        assertEquals(3, history.size(), "Не корректное количество задач в истории.");
    }
}