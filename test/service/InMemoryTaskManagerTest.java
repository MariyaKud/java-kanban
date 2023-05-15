package service;

import exception.NotValidate;
import exception.ParentNotFound;
import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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


    @DisplayName("Должны получить null при добавлении задачи с параметром null.")
    @Test
    void shouldReturnNullIfAddTaskNull() {
        final Task task = taskManager.addTask(null);

        assertNull(task, "Добавленная задача null.");

        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Хранилище задач не инициализировано.");
        assertEquals(0, tasks.size(), "Неверное количество задач.");
    }

    @DisplayName("Должны получить null при добавлении подзадачи с параметром null.")
    @Test
    void shouldReturnNullIfAddSubTaskNull() {
        final SubTask subTask = taskManager.addSubTask(null);

        assertNull(subTask, "Добавленная задача Null.");

        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Хранилище подзадач не инициализировано.");
        assertEquals(0, subTasks.size(), "Неверное количество подзадач.");
    }

    @DisplayName("Должны получить null при добавлении эпика с параметром null.")
    @Test
    void shouldReturnNullIfAddEpicNull() {
        final Epic epic = taskManager.addEpic(null);

        assertNull(epic, "Добавлен эпик Null.");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Хранилище эпиков не инициализировано.");
        assertEquals(0, epics.size(), "Неверное количество эпиков.");
    }

    @DisplayName("Должны добавить задачу с id = 1 (нумерация менеджера).")
    @Test
    void shouldAddTaskWithCorrectId() {
        final Task task = new Task(99, "Test", "Description", 1000);
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
        assertEquals(1000, savedTask.getDuration(), "Не корректный Duration.");
        assertNotEquals(Instant.MIN, savedTask.getStartTime(), "Не корректный StartTime.");

        assertEquals(1,taskManager.getAllTasks().size(),"Не корректный список задач.");
    }

    @DisplayName("Должны добавить эпик без детей с id = 1 (нумерация менеджера).")
    @Test
    void shouldAddEpicWithCorrectId() {
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
        assertEquals(0, savedEpic.getDuration(), "Не корректный Duration.");
        assertEquals(Instant.MAX, savedEpic.getStartTime(), "Не корректный StartTime.");
        assertEquals(Instant.MAX, savedEpic.getEndTime(), "Не корректный EndTime.");

        assertEquals(1,taskManager.getAllEpics().size(),"Не корректный список эпиков.");
    }

    @DisplayName("Должны добавить эпик с id = 1 и подзадачу с id = 2 (нумерация менеджера).")
    @Test
    void shouldAddSubTaskWithCorrectId() {
        final Epic parent = Managers.getSimpleEpicForTest(taskManager);
        final SubTask subTask = new SubTask(99, "Test", "Description",1500, parent.getId());
        taskManager.addSubTask(subTask);

        System.out.println(parent.getStartTime());
        System.out.println(parent.getEndTime());

        final SubTask savedSubTask = taskManager.getSubTaskById(subTask.getId());
        final Epic saveParent = taskManager.getEpicById(parent.getId());

        assertNotNull(subTask, "Добавленная задача Null.");
        assertEquals(1, saveParent.getId(), "Не корректный id родителя.");
        assertEquals(2, savedSubTask.getId(), "Не корректный id.");
        assertEquals(parent.getId(), savedSubTask.getParentID(), "Не корректный id родителя.");

        assertEquals(1500, savedSubTask.getDuration(), "Не корректный Duration.");
        assertEquals(1500, saveParent.getDuration(), "Не корректный Duration родителя.");

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

    @DisplayName("Должны добавить задачу с id = 1, при добавлении задачи без id (id=0).")
    @Test
    void shouldAddTask() {

        final Task task = Managers.getSimpleTaskForTest(taskManager, 100, Instant.now());
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

    @DisplayName("Должны добавить эпик с id=1 без детей со статусом NEW, при добавлении эпика без id (id=0).")
    @Test
    void shouldAddEpic() {

        final Epic epic = Managers.getSimpleEpicForTest(taskManager);
        final Epic savedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(savedEpic, "Добавленный эпик Null.");
        assertEquals(epic, savedEpic, "Эпики не совпадают.");
        assertEquals(0, savedEpic.getChildren().size(), "Есть дети.");
        assertEquals(0, savedEpic.getDuration(), "Не корректный Duration у эпика.");
        assertEquals(Instant.MAX, savedEpic.getStartTime(), "Не корректный StartTime у эпика.");
        assertEquals(IssueStatus.NEW, savedEpic.getStatus(), "Не корректный статус.");
        assertEquals(epic,taskManager.getEpicById(1), "Не тот эпик");

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Хранилище эпиков не инициализировано.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.get(0), "Эпики не совпадают.");
    }

    @DisplayName("Должны добавить эпик со статусом NEW с подзадачей в статусе NEW.")
    @Test
    void shouldAddSubTaskStatusNew() {

        final Epic parent = Managers.getSimpleEpicForTest(taskManager);
        final SubTask subTask = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.NEW,
                                                     10, Instant.now());

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

    @DisplayName("Должны добавить эпик со статусом IN_PROGRESS с подзадачами в статусе IN_PROGRESS.")
    @Test
    void shouldAddEpicWithTwoChildrenInProgress() {

        final Epic parent = Managers.getSimpleEpicForTest(taskManager);
        final SubTask subTask1 = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.IN_PROGRESS,
                                              10, Instant.now());
        final SubTask subTask2 = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.IN_PROGRESS,
                                                   100, Instant.now().plusSeconds(2000));

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

    @DisplayName("Должны добавить эпик со статусом DONE с подзадачами в статусе DONE.")
    @Test
    void shouldAddEpicWithTreeChildrenDone() {

        final Epic parent = Managers.getSimpleEpicForTest(taskManager);
        final SubTask subTask1 = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.DONE,
                                                     10, Instant.now());
        final SubTask subTask2 = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.DONE,
                                                   10, Instant.now().plusSeconds(2000));
        final SubTask subTask3 = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.DONE,
                                                      10, Instant.now().plusSeconds(6000));

        final SubTask savedSubTask1 = taskManager.getSubTaskById(subTask1.getId());
        final SubTask savedSubTask2 = taskManager.getSubTaskById(subTask2.getId());
        final SubTask savedSubTask3 = taskManager.getSubTaskById(subTask3.getId());
        final Epic savedParent = taskManager.getEpicById(parent.getId());

        assertEquals(IssueStatus.DONE, savedSubTask1.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.DONE, savedSubTask2.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.DONE, savedSubTask3.getStatus(), "Статус подзадачи.");
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

    @DisplayName("Должны добавить эпик со статусом IN_PROGRESS с подзадачами NEW и DONE.")
    @Test
    void shouldAddEpicWithTwoChildrenNewDone() {

        final Epic parent = Managers.getSimpleEpicForTest(taskManager);
        final SubTask subTask1 = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.NEW,
                                                      10, Instant.now());
        final SubTask subTask2 = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.DONE,
                                                         200, Instant.now().plusSeconds(2000));

        final SubTask savedSubTask1 = taskManager.getSubTaskById(subTask1.getId());
        final SubTask savedSubTask2 = taskManager.getSubTaskById(subTask2.getId());
        final Epic savedParent = taskManager.getEpicById(parent.getId());

        assertEquals(IssueStatus.NEW, savedSubTask1.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.DONE, savedSubTask2.getStatus(), "Статус подзадачи.");
        assertEquals(IssueStatus.IN_PROGRESS, savedParent.getStatus(), "Статус эпика.");
    }

    @DisplayName("Должны обновить статус задачи с NEW в DONE.")
    @Test
    void shouldUpdateStatusTaskNewToDone() {
        final Task task = Managers.getSimpleTaskForTest(taskManager, 10, Instant.now());
        final Task updateForTask = new Task(task);

        updateForTask.setStatus(IssueStatus.DONE);
        taskManager.updateTask(updateForTask);

        final Task updateTask = taskManager.getTaskById(task.getId());

        assertNotNull(updateTask, "Задачи не возвращаются.");
        assertEquals(updateForTask, updateTask, "Задачи не совпадают.");
        assertEquals(IssueStatus.NEW, task.getStatus(), "Не подходящий статус задачи для обновления.");
        assertEquals(IssueStatus.DONE, updateTask.getStatus(), "Статус задачи не обновлен.");
    }

    @DisplayName("Должны обновить статус подзадачи и его родителя с NEW в DONE. ")
    @Test
    void shouldUpdateStatusSubTaskNewToDone() {
        final Epic parent = Managers.getSimpleEpicForTest(taskManager);
        final SubTask subTask = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.NEW,
                                                       10, Instant.now());

        final SubTask subTaskToUpdate = new SubTask(subTask);
        subTaskToUpdate.setStatus(IssueStatus.DONE);
        taskManager.updateSubTask(subTaskToUpdate);

        final SubTask updateSubTask = taskManager.getSubTaskById(subTask.getId());

        assertNotNull(updateSubTask, "Обновленная подзадача не найдена.");
        assertEquals(subTaskToUpdate, updateSubTask, "Статус подзадачи не обновлен.");
        assertEquals(IssueStatus.DONE, updateSubTask.getStatus(), "Статус подзадачи не обновлен.");
        assertEquals(IssueStatus.DONE, taskManager.getEpicById(updateSubTask.getParentID()).getStatus(),
                                                     "Статус эпика не обновлен.");
    }

    @DisplayName("Должен оставить статус NEW при попытке обновить статус эпика в DONE")
    @Test
    void shouldUpdateStatusEpic() {
        //Статус эпика расчетная величина, его нельзя установить
        final Epic epic = Managers.getSimpleEpicForTest(taskManager);
        final Epic epicToUpdate = new Epic(epic);

        epicToUpdate.setStatus(IssueStatus.DONE);
        final Epic updateEpic = taskManager.updateEpic(epicToUpdate);

        assertNotNull(updateEpic, "Эпики не возвращаются.");
        assertNotEquals(IssueStatus.DONE, updateEpic.getStatus(), "Статус эпика обновлен.");
        assertEquals(IssueStatus.NEW, updateEpic.getStatus(), "Статус эпика обновлен.");
        assertNotEquals(IssueStatus.DONE, taskManager.getEpicById(epic.getId()).getStatus(),
                "Статус эпика обновлен.");
    }

    @DisplayName("Должны обновить статус эпика с 2 детьми, при изменении статуса подзадач")
    @Test
    void shouldUpdateStatusEpicWithTwoChildren() {

        final Epic parent = Managers.getSimpleEpicForTest(taskManager);
        final SubTask subTask1 = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.NEW,
                10, Instant.now());
        final SubTask subTask2 = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.NEW,
                10, Instant.now().plusSeconds(2000));

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

    @DisplayName("Должны получить ParentNotFound, при добавлении подзадачи с отсутствующим id родителем.")
    @Test
    void shouldReturnParentNotFoundAddSubTaskWithoutParent() {
        final ParentNotFound e = assertThrows(ParentNotFound.class,  () -> Managers.getSimpleSubTaskForTest(
                                               taskManager,10,IssueStatus.NEW,10, Instant.now()));
        assertEquals("10", e.getMessage());
    }

    @DisplayName("Должны получить ParentNotFound, при обновлении родителя подзадачи на отсутствующий id родителем.")
    @Test
    void shouldReturnParentNotFoundUpdateSubTaskExistParent() {
        final Epic parent = Managers.getSimpleEpicForTest(taskManager);
        final SubTask subTask = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.NEW,
                10, Instant.now());
        final SubTask subTaskToUpdate = new SubTask(subTask.getId(), subTask.getTitle(), subTask.getDescription(),
                                                       10,10,subTask.getStartTime());
        final ParentNotFound e = assertThrows(ParentNotFound.class,  () -> taskManager.updateSubTask(subTaskToUpdate));
        assertEquals("10", e.getMessage());
    }

    @DisplayName("Должны получить исключение NotValidate, при добавлении задачи на занятый интервал.")
    @Test
    void shouldReturnNotValidateAddTaskWithCross() {
        final Task task1 = Managers.getSimpleTaskForTest(taskManager, 100, Instant.now());
        final NotValidate e = assertThrows(NotValidate.class,  () -> taskManager.addTask(task1));
        assertEquals(task1.toString(), e.getMessage());
    }

    @DisplayName("Должны получить исключение NotValidate, при добавлении подзадачи на занятый интервал.")
    @Test
    void shouldReturnNotValidateAddSubTaskWithCross() {
        final Epic parent = Managers.getSimpleEpicForTest(taskManager);
        final SubTask subTask = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.NEW,
                                                                     10, Instant.now());
        final NotValidate e = assertThrows(NotValidate.class,  () -> taskManager.addSubTask(subTask));
        assertEquals(subTask.toString(), e.getMessage());
    }

    @DisplayName("Должны обновить интервал задачи на доступный.")
    @Test
    void shouldAddTaskWithoutCross() {
        final Task task1 = Managers.getSimpleTaskForTest(taskManager, 100, Instant.now());
        final Task task2 = Managers.getSimpleTaskForTest(taskManager, 100, Instant.now()
                .plusSeconds(10000));

        final Task taskToUpdate = new Task(task1);
        taskToUpdate.setStartTime(task2.getEndTime().plusSeconds(960));
        final Task updateTask = taskManager.updateTask(taskToUpdate);

        assertNotNull(updateTask, "Ошибка валидации");
        assertEquals(2, taskManager.getAllTasks().size(), "Ошибка валидации. В списке задач ошибка.");
        assertEquals(taskToUpdate.getStartTime(),updateTask.getStartTime(),"Стартовое время " +
                "обновлено не корректно");
    }

    @DisplayName("Должны получить NotValidate, при обновлении задачи на занятый интервал.")
    @Test
    void shouldReturnNotValidateUpdateTaskWithCross() {
        final Task task = Managers.getSimpleTaskForTest(taskManager, 10, Instant.now());
        final Task taskToUpdate = Managers.getSimpleTaskForTest(taskManager, 100,
                Instant.now().plusSeconds(2000));

        final Task updateTask = new Task(taskToUpdate);
        updateTask.setStartTime(task.getStartTime());
        updateTask.setDuration(task.getDuration());

        final NotValidate e = assertThrows(NotValidate.class,  () -> taskManager.updateTask(updateTask));
        assertEquals(updateTask.toString(), e.getMessage());
    }

    @DisplayName("Должны обновить родителя c обновлением статуса старого и нового родителя.")
    @Test
    void shouldUpdateParent() {
        final Epic parent1 = Managers.getSimpleEpicForTest(taskManager);
        final Epic parent2 = Managers.getSimpleEpicForTest(taskManager);

        final Instant now = Instant.now();
        final SubTask subTask1 = Managers.getSimpleSubTaskForTest(taskManager,parent1.getId(),IssueStatus.NEW,
                10, now);
        final SubTask subTask2 = Managers.getSimpleSubTaskForTest(taskManager,parent2.getId(),IssueStatus.DONE,
                10, subTask1.getEndTime().plusSeconds(2000));

        final SubTask subTaskToUpdate = new SubTask(subTask2.getId(), subTask2.getTitle(), subTask2.getDescription(),
                subTask2.getDuration(),subTask2.getStartTime(), parent1.getId(), subTask2.getStatus());

        final SubTask subTaskUpdate = taskManager.updateSubTask(subTaskToUpdate);
        final Epic parent1Update = taskManager.getEpicById(parent1.getId());
        final Epic parent2Update = taskManager.getEpicById(parent2.getId());

        assertEquals(parent1.getId(), subTaskUpdate.getParentID(), "Не корректный id родителя");
        assertEquals(2, parent1Update.getChildren().size(),"Не корректное состояние детей у нового родителя");
        assertEquals(0, parent2Update.getChildren().size(),"Не корректное состояние детей у старого родителя");

        assertEquals(IssueStatus.IN_PROGRESS, parent1Update.getStatus(),"Не обновился статус нового родителя.");
        assertEquals(IssueStatus.NEW, parent2Update.getStatus(),"Не обновился статус старого родителя.");


        assertEquals(subTask2.getEndTime(), parent1Update.getEndTime(),
                    "Не корректно обновилось время завершения нового родителя.");

        assertEquals(subTask1.getDuration() + subTask2.getDuration(), parent1Update.getDuration(),
                "Не корректно обновилось интервал нового родителя.");

        assertEquals(Instant.MAX, parent2Update.getEndTime(),
                "Не корректно обновилось время завершения старого родителя.");
        assertEquals(0, parent2Update.getDuration(),
                "Не корректно обновилось интервал старого родителя.");
    }

    @DisplayName("Должны получить NotValidate, при обновлении подзадачи на занятый интервал.")
    @Test
    void shouldRefuseUpdateStartTimeWithCrossSubTask() {
        final Epic parent = Managers.getSimpleEpicForTest(taskManager);

        final Instant now = Instant.now();
        final SubTask subTask1 = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.NEW,
                10, now);
        final SubTask subTask2 = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.NEW,
                10, now.plusSeconds(3000));

        final SubTask subTaskToUpdate = new SubTask(subTask1);
        subTaskToUpdate.setStartTime(subTask2.getStartTime());

        final NotValidate e = assertThrows(NotValidate.class,  () -> taskManager.updateSubTask(subTaskToUpdate));
        assertEquals(subTaskToUpdate.toString(), e.getMessage());
    }

    @DisplayName("Должны обновить дату старта Подзадачи на свободный отрезок.")
    @Test
    void shouldUpdateStartTimeSubTask() {
        final Epic parent = Managers.getSimpleEpicForTest(taskManager);
        Instant now = Instant.now();
        Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.NEW,10, now);
        final SubTask subTask2 = Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.NEW,
                                                       10,now.plusSeconds(3000));

        final SubTask subTaskToUpdate = new SubTask(subTask2);
        subTaskToUpdate.setStartTime(subTask2.getEndTime().plusSeconds(10000));
        taskManager.updateSubTask(subTaskToUpdate);

        final SubTask updateSubTask = taskManager.getSubTaskById(subTaskToUpdate.getId());

        assertNotNull(updateSubTask, "Обновление вернуло null.");
        assertEquals(subTaskToUpdate.getStartTime(), updateSubTask.getStartTime(), "Ошибка обновления даты.");
    }

    @DisplayName("Должны удалить задачу по существующему id.")
    @Test
    void shouldDeleteTaskById() {
        //Стандартный вариант - когда есть задача
        final Task task = Managers.getSimpleTaskForTest(taskManager, 10, Instant.now());
        taskManager.deleteTaskById(task.getId());
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Возвращает null.");
        assertEquals(0, tasks.size(), "Список задач не пуст.");
        assertNull(taskManager.getTaskById(0), "Задача найдена.");
    }

    @DisplayName("Должны получить null, при удалении по id в пустом списке.")
    @Test
    void shouldReturnNullIfDeleteTaskByIdWhenTasksEmpty() {

        final Task task = taskManager.deleteTaskById(100);
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Возвращает null.");
        assertEquals(0, tasks.size(), "Список задач не пуст.");
        assertNull(taskManager.getTaskById(100), "Задача найдена.");
        assertNull(task,"Возвращает не null.");
    }

    @DisplayName("Должны получить null, при удалении задачи по не существующему id.")
    @Test
    void shouldReturnNullIfDeleteTaskByNotHaveId() {

        final Task task = Managers.getSimpleTaskForTest(taskManager, 10, Instant.now());
        final Task delTask = taskManager.deleteTaskById(100);

        final List<Task> tasks = taskManager.getAllTasks();

        assertNull(delTask, "Найдена не существующая задача.");
        assertNotNull(tasks, "Возвращает null.");

        assertEquals(1, tasks.size(), "Список задач не пуст.");
        assertNull(taskManager.getTaskById(100), "Задача найдена.");
        assertEquals(task, taskManager.getTaskById(task.getId()), "Список задач не корректен.");
    }

    @DisplayName("Должны удалить подзадачу по существующему id.")
    @Test
    void shouldDeleteSubTaskById() {
        //Стандартный вариант - когда есть подзадача
        final Epic newEpic = Managers.getSimpleEpicForTest(taskManager);
        final SubTask subTask = Managers.getSimpleSubTaskForTest(taskManager,newEpic.getId(),IssueStatus.DONE,
                                                     10, Instant.now());

        final SubTask delSubTask = taskManager.deleteSubTaskById(subTask.getId());
        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(delSubTask, "Возвращает null.");
        assertEquals(0, subTasks.size(), "Список подзадач не пуст.");
        assertNull(taskManager.getSubTaskById(subTask.getId()), "Подзадача не удалена.");
    }

    @DisplayName("Должны удалить эпик без детей по существующему id.")
    @Test
    void shouldDeleteEpicById() {
        //Стандартный вариант - когда есть эпик
        final Epic newEpic = Managers.getSimpleEpicForTest(taskManager);
        taskManager.deleteEpicById(newEpic.getId());

        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Возвращает null.");
        assertEquals(0, epics.size(), "Список эпиков не пуст.");
        assertNull(taskManager.getEpicById(newEpic.getId()), "Эпик не удален.");
    }

    @DisplayName("Должны удалить эпик и его детей по существующим id.")
    @Test
    void shouldDeleteEpicWithChildrenById() {
        //Стандартный вариант - когда есть эпик
        final Epic epic = Managers.getSimpleEpicForTest(taskManager);
        final SubTask subTask = Managers.getSimpleSubTaskForTest(taskManager,epic.getId(),IssueStatus.IN_PROGRESS,
                                              10, Instant.now());

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

    @DisplayName("Должны получить null, при удалении эпика в пустом списке.")
    @Test
    void shouldDeleteEpicByIdWhenTasksEmpty() {
        final Epic epic = taskManager.deleteEpicById(100);
        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Возвращает null.");
        assertNull(epic, "Не получили null.");
        assertEquals(0, epics.size(), "Список эпиков не пуст.");
        assertNull(taskManager.getEpicById(100), "Эпик найден.");
    }

    @DisplayName("Должны получить null, при удалении эпика по не существующему id.")
    @Test
    void shouldDeleteEpicByNotHaveId() {
        Managers.getSimpleEpicForTest(taskManager);
        final Epic delEpic = taskManager.deleteEpicById(100);
        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "Возвращает null.");
        assertNull(delEpic, "Не получили null.");
        assertEquals(1, epics.size(), "Список эпиков не пуст.");
        assertNull(taskManager.getEpicById(100), "Эпик не удален.");
    }

    @DisplayName("Должны получить пустой список задач, при пустом списке.")
    @Test
    void shouldTasksNotNullIfDeleteAllTasksForNewManager() {
        //Для нового менеджера, после удаления подзадач - пустой список и не null
        taskManager.deleteAllTasks();
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Возвращает null.");
        assertEquals(0, tasks.size(), "Список задач не пуст.");
    }

    @DisplayName("Должны получить пустой список задач, при удалении всех задач, при наличии задач.")
    @Test
    void shouldDeleteAllTasks(){
        Task task = Managers.getSimpleTaskForTest(taskManager, 10, Instant.now());
        //Добавляем в историю
        taskManager.getTaskById(task.getId());

        taskManager.deleteAllTasks();
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "Возвращает null.");
        assertEquals(0, tasks.size(), "Список задач не пуст.");
        assertEquals(0, taskManager.getHistory().size(), "Список истории не пуст.");
    }

    @DisplayName("Должны получить пустой список подзадач, при пустом списке.")
    @Test
    void shouldSubTasksNotNullIfDeleteAllSubTasksForNewManager() {
        //Для нового менеджера, после удаления подзадач - пустой список и не null
        taskManager.deleteAllSubTasks();
        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Возвращает null.");
        assertEquals(0, subTasks.size(), "Список подзадач не пуст.");
    }

    @DisplayName("Должны получить пустой список подзадачи эпики без детей, при наличии эпиков с детьми, " +
            "после удаления всех подзадач.")
    @Test
    void shouldDeleteAllSubTasksTest(){
        Epic epic = Managers.getSimpleEpicForTest(taskManager);
        Managers.getSimpleSubTaskForTest(taskManager, epic.getId(), IssueStatus.NEW, 10, Instant.now());

        taskManager.deleteAllSubTasks();
        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "Возвращает null.");
        assertEquals(0, subTasks.size(), "Список подзадач не пуст.");
        assertEquals(0, epic.getChildren().size(), "Список подзадач не пуст.");
    }

    @DisplayName("Должны получить пустой список эпиков, при пустом списке.")
    @Test
    void shouldEpicsNotNullIfDeleteAllEpicsForNewManager() {
        //Для нового менеджера, после удаления эпики - пустой список и не null
        taskManager.deleteAllEpics();
        final List<Epic> epics = taskManager.getAllEpics();
        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(epics, "Возвращает null.");
        assertEquals(0, epics.size(), "Список эпиков не пуст.");

        assertNotNull(subTasks, "Возвращает null.");
        assertEquals(0, subTasks.size(), "Список подзадач не пуст.");
    }

    @DisplayName("Должны получить пустые списки эпиков и подзадач, после удаления всех эпиков," +
            " при наличии эпика с детьми.")
    @Test
    void shouldDeleteAllEpicWithChildren() {
        Epic epic = Managers.getSimpleEpicForTest(taskManager);
        Managers.getSimpleSubTaskForTest(taskManager, epic.getId(), IssueStatus.NEW, 10, Instant.now());

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getAllEpics().size(), "Список эпиков не пуст.");
        assertEquals(0, taskManager.getAllSubTasks().size(), "Список подзадач не пуст.");
    }

    @DisplayName("Должны получить null, при поиске эпика по не существующему id.")
    @Test
    void shouldReturnNullGetEpicByIdWithNotCorrectId() {
        //Ищем эпик с неверным id
        final Epic epic = new Epic(0, "Epic", "Description");

        final Epic newEpic  = taskManager.addEpic(epic);
        final Epic findEpic = taskManager.getEpicById(newEpic.getId()+1);

        assertNull(findEpic, "Не возвращает null.");
    }

    @DisplayName("Должны получить null, при поиске подзадачи по не существующему id.")
    @Test
    void shouldReturnNullGetSubTaskByIdWithNotCorrectId() {
        //Ищем подзадачу с неверным id
        final Epic parent = Managers.getSimpleEpicForTest(taskManager);
        Managers.getSimpleSubTaskForTest(taskManager,parent.getId(),IssueStatus.NEW, 10, Instant.now());

        final SubTask savedSubTask = taskManager.getSubTaskById(parent.getId());

        assertNull(savedSubTask, "Не возвращает null.");
    }

    @DisplayName("Должны получить пустой список задач, для нового менеджера")
    @Test
    void shouldReturnEmptyTasksIfGetAllTasksForNewManager() {
        //Для нового менеджера подзадачи пустой список и не null
        final List<Task> tasks = taskManager.getAllTasks();

        assertNotNull(tasks, "История - null.");
        assertEquals(0, tasks.size(), "Хранилище задач не пустое для нового менеджера.");
    }

    @DisplayName("Должны получить пустой список подзадач, для нового менеджера")
    @Test
    void shouldReturnEmptySubTasksIfGetAllSubTasksForNewManager() {
        //Для нового менеджера подзадачи пустой список и не null
        final List<SubTask> subTasks = taskManager.getAllSubTasks();

        assertNotNull(subTasks, "История - null.");
        assertEquals(0, subTasks.size(), "Хранилище подзадач не пустое для нового менеджера.");
    }

    @DisplayName("Должны получить пустой список эпиков, для нового менеджера")
    @Test
    void shouldReturnEmptyEpicsIfGetAllEpicsForNewManager() {
        //Для нового менеджера эпики пустой список и не null
        final List<Epic> epics = taskManager.getAllEpics();

        assertNotNull(epics, "История - null.");
        assertEquals(0, epics.size(), "Хранилище эпика не пустое для нового менеджера.");
    }

    @DisplayName("Должны получить пустой список подзадач, для нового эпика")
    @Test
    void shouldReturnEmptyListIfGetChildrenOfEpicByIdForNewEpic() {
        //Для нового эпика список подзадач пустой и не null
        Epic epic = new Epic(0, "Epic", "Description");
        final Epic newEpic = taskManager.addEpic(epic);

        final List<SubTask> children = taskManager.getChildrenOfEpicById(newEpic.getId());

        assertNotNull(children, "Список детей эпика - null.");
        assertEquals(0, children.size(), "Список детей эпика не пустая.");
    }

    @DisplayName("Должны получить пустой список истории просмотров для нового менеджера.")
    @Test
    void shouldReturnEmptyListIfGetHistoryForNewManager() {
        //Для нового менеджера история задач пустой список и не null
        final List<Issue> history = taskManager.getHistory();

        assertNotNull(history, "История - null.");
        assertEquals(0, history.size(), "История не пустая для нового менеджера.");
    }

    @DisplayName("Должны получить историю просмотров для не нового менеджера.")
    @Test
    void shouldReturnHistoryListForGetHistory() {
        Managers.getSimpleTestForTaskManager(taskManager);

        final List<Issue> history = taskManager.getHistory();

        assertNotNull(history, "История - null.");
        assertEquals(3, history.size(), "Не корректное количество задач в истории.");
    }
}