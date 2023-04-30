package service;

import model.Issue;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void getHistoryNewTest() {
        //Для нового экземпляра класса HistoryManager - история это пустой список
        //Проверяем, что getHistory() вернет не null и размер списка = 0
        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(0, history.size(), "История не пустая.");
    }

    @Test
    void addOneTaskTest() {
        //Проверяем наличие задачи в истории, после применения метода add()
        Task task = new Task(0, "Test addTask", "Description", Duration.ofMinutes(10));

        historyManager.add(task);

        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(1, history.size(), "Длина очереди не соответствует ожидаемой.");
    }

    @Test
    void addOneTaskTwiceCheckDoubleTest() {
        //Проверяем наличие задачи в истории, после применения метода add()
        Task task = new Task(0, "Test addTask", "Description", Duration.ofMinutes(10));

        historyManager.add(task);
        historyManager.add(task);

        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(1, history.size(), "Длина очереди не соответствует ожидаемой.");
    }

    @Test
    void removeSingleTaskTest() {
        //Удаляем единственную задачу, проверяем чтобы очередь не превращалась в null и была пустой
        Task task = new Task(0, "Test remove", "Description", Duration.ofMinutes(10));

        historyManager.add(task);
        historyManager.remove(0);

        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(0, history.size(), "Длина очереди не соответствует ожидаемой.");
    }

    @Test
    void removeFromBeginOfLineTest() {
        //Удаляем задачу из начала очереди
        LocalDateTime startTime = LocalDateTime.now();

        Task task1 = new Task(0, "Test", "Description", Duration.ofMinutes(10), startTime);
        historyManager.add(task1);

        Task task2 = new Task(1, "Test", "Description",
                               Duration.ofMinutes(10), startTime.plusMinutes(15));
        historyManager.add(task2);

        historyManager.remove(0);

        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(1, history.size(), "Длина очереди не соответствует ожидаемой.");
        assertEquals(task2, history.get(0), "Не корректная задача в очереди.");
    }

    @Test
    void removeFromEndOfLineTest() {
        //Удаляем задачу из конца очереди
        LocalDateTime startTime = LocalDateTime.now();

        Task task1 = new Task(0, "Test", "Description", Duration.ofMinutes(10), startTime);
        historyManager.add(task1);

        Task task2 = new Task(1, "Test", "Description",
                Duration.ofMinutes(10), startTime.plusMinutes(15));
        historyManager.add(task2);

        historyManager.remove(1);

        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(1, history.size(), "Длина очереди не соответствует ожидаемой.");
        assertEquals(task1, history.get(0), "Не корректная задача в очереди.");
    }

    @Test
    void removeFromMiddleOfLineTest() {
        //Удаляем задачу из середины очереди
        LocalDateTime startTime = LocalDateTime.now();

        Task task1 = new Task(0, "Test", "Description", Duration.ofMinutes(10), startTime);
        historyManager.add(task1);

        Task task2 = new Task(1, "Test", "Description",
                Duration.ofMinutes(10), startTime.plusMinutes(15));
        historyManager.add(task2);

        Task task3 = new Task(2, "Test", "Description",
                Duration.ofMinutes(10), startTime.plusMinutes(30));
        historyManager.add(task3);

        historyManager.remove(1);

        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(2, history.size(), "Длина очереди не соответствует ожидаемой.");
        assertEquals(task1, history.get(0), "Не корректная задача в начале очереди.");
        assertEquals(task3, history.get(1), "Не корректная задача в конце очереди.");
    }
}