package dao;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.FileBackedTasksManager;
import service.HistoryManager;
import service.InMemoryHistoryManager;
import service.Managers;
import service.TaskManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.io.FileReader;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

@DisplayName("Тест работы менеджера с файлом CSV..")
class CSVMakeRepositoryTest {

    final String dirHome     = System.getProperty("user.home");
    final String nameFileCSV = "taskManager.csv";
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
        issueRepository = new CSVMakeRepository();
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

    @DisplayName("Новый менеджер задач сохранит файл с одной строкой (заголовок).")
    @Test
    void shouldSaveOnlyTittleForNewManagerTest() {
        issueRepository.save(fileBackedTasksManager, file);

        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            assertEquals(issueRepository.FILE_HEAD, br.readLine()+"\n", "Не верный заголовок.");
            assertNotNull(br.readLine(), "В файле есть данные, кроме заголовка.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("Сохранить в файл задачу.")
    @Test
    void shouldSaveTaskWithoutChildrenTest() {
        Managers.addTask(fileBackedTasksManager);

        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            assertEquals(issueRepository.FILE_HEAD, br.readLine()+"\n", "Не верный заголовок.");
            //проверяем не изменяемую часть
            String lineToCheck = "1,TASK,Test,NEW,Description";
            assertEquals(lineToCheck, br.readLine().substring(0,lineToCheck.length()),
                     "Данные в файле не корректны");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("Сохранить в файл эпик без детей.")
    @Test
    void shouldSaveEpicWithoutChildrenTest() {
        Managers.addEpic(fileBackedTasksManager);

        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            assertEquals(issueRepository.FILE_HEAD, br.readLine()+"\n", "Не верный заголовок.");
            assertEquals("1,EPIC,Epic,NEW,Description,0,0",
                          br.readLine(), "Данные в файле не корректны");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("Сохранить в файл эпик с ребенком.")
    @Test
    void shouldSaveEpicWithTwoChildrenTest() {
        Epic epic = Managers.addEpic(fileBackedTasksManager);
        Managers.addSubTask(fileBackedTasksManager, epic.getId(), IssueStatus.NEW);

        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            assertEquals(issueRepository.FILE_HEAD, br.readLine()+"\n", "Не верный заголовок.");
            //проверяем не изменяемую часть для эпика
            String lineToCheck = "1,EPIC,Epic,NEW,Description";
            assertEquals(lineToCheck, br.readLine().substring(0,lineToCheck.length()),
                         "Данные в файле не корректны");

            lineToCheck = "2,SUBTASK,SubTask,NEW,Description";
            String Line = br.readLine();
            assertEquals(lineToCheck, Line.substring(0,lineToCheck.length()),
                    "Данные в файле не корректны");
            assertEquals(Integer.toString(epic.getId()),Line.substring(Line.length()-1),
                     "Не верный id у родителя.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("Сохранить эпик и он же в истории.")
    @Test
    void shouldSaveEpicWithHistoryTest() {
        final Epic epic = Managers.addEpic(fileBackedTasksManager);
        fileBackedTasksManager.getEpicById(epic.getId());

        try (BufferedReader br = new BufferedReader(new FileReader(file)))
        {
            assertEquals(issueRepository.FILE_HEAD, br.readLine()+"\n", "Не верный заголовок.");
            assertEquals("1,EPIC,Epic,NEW,Description,0,0",
                    br.readLine(), "Данные по эпику в файле не корректны");
            assertEquals("",br.readLine(),"Нет разделительной строки перед историей");
            assertEquals("1", br.readLine(),"Не корректная история");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @DisplayName("Загрузить задачу, эпик, подзадачу с историей.")
    @Test
    void shouldLoadTest() {
        //Подготовить файл
        Managers.simpleTestForTaskManager(fileBackedTasksManager);
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

    }
}