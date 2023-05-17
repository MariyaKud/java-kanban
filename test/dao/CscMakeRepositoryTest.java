package dao;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.CsvMakeRepository;
import repository.IssueRepository;
import service.FileBackedTasksManager;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.Managers;
import service.TaskManager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@DisplayName("Тест работы менеджера с файлом csv..")
class CscMakeRepositoryTest {

    final String dirHome     = "data";
    final String nameFileCSV = "taskManagerTest.csv";
    private IssueRepository issueRepository;
    private File file;
    private TaskManager fileBackedTasksManager;

    @BeforeEach
    void beforeEach() {
        final HistoryManager historyManager = new InMemoryHistoryManager();

        file = new File(dirHome,nameFileCSV);
        //Очищаем файл
        try {
            PrintWriter writer = new PrintWriter(file);
            writer.print("");
            writer.close();
        }  catch (FileNotFoundException e) {
            System.out.println("Проблемы с созданием файла CSV - " + e.getMessage());
            System.out.println("Тесты не состоялись!");
            assumeFalse(true, "Проблемы с доступом к файлу CSV");
        }
        //Класс обмена
        issueRepository = new CsvMakeRepository();
        //Класс менеджера
        fileBackedTasksManager = new FileBackedTasksManager(historyManager, file);
    }

    @DisplayName("Пустой список задач, при загрузке пустого файла.")
    @Test
    void shouldCreateEmptyTasksInManagerTest() {
        issueRepository.load(fileBackedTasksManager, file);
        final List<Task> tasks = fileBackedTasksManager.getAllTasks();

        assertNotNull(tasks, "Список задач null.");
        assertEquals(0, tasks.size(), "Список задач не пуст.");
    }

    @DisplayName("Пустой список подзадач, при загрузке пустого файла.")
    @Test
    void shouldCreateEmptySubTasksInManagerTest() {
        issueRepository.load(fileBackedTasksManager, file);
        final List<SubTask> subTasks = fileBackedTasksManager.getAllSubTasks();

        assertNotNull(subTasks, "Список подзадач null.");
        assertEquals(0, subTasks.size(), "Список подзадач не пуст.");
    }

    @DisplayName("Пустой список эпиков, при загрузке пустого файла.")
    @Test
    void shouldCreateEmptyEpicsInManagerTest() {
        issueRepository.load(fileBackedTasksManager, file);
        final List<Epic> epics = fileBackedTasksManager.getAllEpics();

        assertNotNull(epics, "Список эпиков null.");
        assertEquals(0, epics.size(), "Список эпиков не пуст.");
    }

    @DisplayName("Пустая история, при загрузке пустого файла.")
    @Test
    void shouldCreateEmptyHistoryInManagerTest() {
        issueRepository.load(fileBackedTasksManager, file);
        final List<Issue> history = fileBackedTasksManager.getHistory();

        assertNotNull(history, "Список истории null.");
        assertEquals(0, history.size(), "Список истории не пуст.");
    }

    @DisplayName("Новый менеджер сохраненный в файл восстанавливается с пустыми хранилищами.")
    @Test
    void shouldSaveNothingForNewManagerTest() {
        //Сохраняем новый менеджер в файл
        issueRepository.save(fileBackedTasksManager, file);
        //Восстанавливаем данные по новому менеджеру
        FileBackedTasksManager loadTasksManager = new FileBackedTasksManager(new InMemoryHistoryManager(),file);
        issueRepository.load(loadTasksManager, file);

        final List<Task> tasks = fileBackedTasksManager.getAllTasks();
        final List<SubTask> subTasks = fileBackedTasksManager.getAllSubTasks();
        final List<Epic> epics = loadTasksManager.getAllEpics();
        final List<Issue> history = loadTasksManager.getHistory();

        assertNotNull(tasks, "Список задач null.");
        assertEquals(0, tasks.size(), "Список задач не пуст.");

        assertNotNull(subTasks, "Список подзадач null.");
        assertEquals(0, subTasks.size(), "Список подзадач не пуст.");

        assertNotNull(epics, "Список эпиков null.");
        assertEquals(0, epics.size(), "Список эпиков не пуст.");

        assertNotNull(history, "Список истории null.");
        assertEquals(0, history.size(), "Список истории не пуст.");
    }

    @DisplayName("Сохранить/загрузить задачу.")
    @Test
    void shouldSaveTaskTest() {
        Task task = Managers.getSimpleTaskForTest(fileBackedTasksManager,10 , Instant.now());

        FileBackedTasksManager loadTasksManager = new FileBackedTasksManager(new InMemoryHistoryManager(),file);
        issueRepository.load(loadTasksManager, file);

        Task loadTask = loadTasksManager.getTaskById(task.getId());

        assertEquals(fileBackedTasksManager.getAllTasks(), loadTasksManager.getAllTasks(),
                "Задача загрузилась не корректно.");
        assertEquals(task,loadTask, "Задача загрузилась не корректно.");
    }

    @DisplayName("Сохранить/загрузить эпик без детей.")
    @Test
    void shouldSaveEpicWithoutChildrenTest() {
        Epic epic = Managers.getSimpleEpicForTest(fileBackedTasksManager);

        FileBackedTasksManager loadTasksManager = new FileBackedTasksManager(new InMemoryHistoryManager(),file);
        issueRepository.load(loadTasksManager, file);

        Epic loadEpic = loadTasksManager.getEpicById(epic.getId());

        assertEquals(fileBackedTasksManager.getAllEpics(), loadTasksManager.getAllEpics(),
                "Эпик загрузился не корректно.");
        assertEquals(epic,loadEpic, "Эпик загрузился не корректно.");
        assertEquals(0, epic.getChildren().size(), "Эпик для теста не подходит.");
        assertEquals(0, loadEpic.getChildren().size(), "Эпик загрузился не корректно.");
    }

    @DisplayName("Сохранить/загрузить эпик с ребенком.")
    @Test
    void shouldSaveEpicWithTwoChildrenTest() {
        Epic epic = Managers.getSimpleEpicForTest(fileBackedTasksManager);
        SubTask subTask = Managers.getSimpleSubTaskForTest(fileBackedTasksManager, epic.getId(),
                                              IssueStatus.NEW, 10, Instant.now());

        FileBackedTasksManager loadTasksManager = new FileBackedTasksManager(new InMemoryHistoryManager(),file);
        issueRepository.load(loadTasksManager, file);

        Epic loadEpic = loadTasksManager.getEpicById(epic.getId());
        SubTask loadSubTask =loadTasksManager.getSubTaskById(subTask.getId());

        assertEquals(fileBackedTasksManager.getAllEpics(), loadTasksManager.getAllEpics(),
                "Эпик с детьми загрузился не корректно.");
        assertEquals(fileBackedTasksManager.getAllSubTasks(), loadTasksManager.getAllSubTasks(),
                "Эпик с детьми загрузился не корректно.");
        assertEquals(epic,loadEpic, "Эпик загрузился не корректно.");
        assertEquals(subTask,loadSubTask, "Подзадача загрузилась не корректно.");
    }

    @DisplayName("Сохранить/Загрузить эпик и он же в истории.")
    @Test
    void shouldSaveEpicWithHistoryTest() {
        final Epic epic = Managers.getSimpleEpicForTest(fileBackedTasksManager);
        //Сохраняем в историю
        fileBackedTasksManager.getEpicById(epic.getId());

        FileBackedTasksManager loadTasksManager = new FileBackedTasksManager(new InMemoryHistoryManager(),file);
        issueRepository.load(loadTasksManager, file);

        Epic loadEpic = loadTasksManager.getEpicById(epic.getId());
        final List<Issue> history = loadTasksManager.getHistory();

        assertEquals(fileBackedTasksManager.getHistory(), history, "История не восстановилась.");
        assertEquals(1, history.size(), "История не восстановилась.");
        assertEquals(epic, history.get(0), "История не восстановилась.");
        assertEquals(epic, loadEpic, "Эпик не восстановился.");
    }

    @DisplayName("Загрузить задачу, эпик, подзадачу с историей. Отсортированные списки должны совпасть.")
    @Test
    void shouldLoadTest() {
        //Подготовить файл
        Managers.getSimpleTestForTaskManager(fileBackedTasksManager);
        FileBackedTasksManager loadTasksManager = new FileBackedTasksManager(new InMemoryHistoryManager(),file);
        issueRepository.load(loadTasksManager, file);

        assertEquals(fileBackedTasksManager.getAllTasks(), loadTasksManager.getAllTasks(),
                 "Задачи загрузились не корректны.");
        assertEquals(fileBackedTasksManager.getAllSubTasks(),loadTasksManager.getAllSubTasks(),
                "Подзадачи загрузились не корректны.");
        assertEquals(fileBackedTasksManager.getAllEpics(),loadTasksManager.getAllEpics(),
                "Эпики загрузились не корректны.");
        assertEquals(fileBackedTasksManager.getHistory(),loadTasksManager.getHistory(),
                "История загрузилась не корректно.");
        assertEquals(fileBackedTasksManager.getPrioritizedTasks(),loadTasksManager.getPrioritizedTasks(),
                "Сортированные списке различаются.");
    }

    @DisplayName("Очистить все хранилища.")
    @Test
    void shouldDeleteAll() {
        //Подготовить файл
        Managers.getSimpleTestForTaskManager(fileBackedTasksManager);

        //задачи
        fileBackedTasksManager.deleteAllTasks();
        assertEquals(0,fileBackedTasksManager.getAllTasks().size(),"Список задач не пуст");

        //Подзадачи
        fileBackedTasksManager.deleteAllSubTasks();
        assertEquals(0,fileBackedTasksManager.getAllSubTasks().size(),"Список подзадач не пуст");
        for (Epic epic : fileBackedTasksManager.getAllEpics()) {
            assertEquals(0, fileBackedTasksManager.getChildrenOfEpicById(epic.getId()).size(),
                     "Есть дети у эпика, хотя все подзадачи удалены");
        }

        fileBackedTasksManager.deleteAllEpics();
        assertEquals(0,fileBackedTasksManager.getAllEpics().size(),"Список эпиков не пуст");

        assertEquals(0, fileBackedTasksManager.getPrioritizedTasks().size(),
                "Есть данные в отсортированном хранилище");
        assertEquals(0, fileBackedTasksManager.getHistory().size(),
                "Есть данные в истории просмотров");

    }
}