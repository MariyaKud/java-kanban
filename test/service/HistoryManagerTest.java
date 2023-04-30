package service;

import model.Issue;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeAll
    static void beforeAll() {
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void add() {
        //Проверяем наличие задачи в истории, после применения метода add()
        Task task = new Task(0, "Test addTask", "Test addTask description", Duration.ofMinutes(10));

        historyManager.add(task);

        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История не пустая.");
        assertEquals(1, history.size(), "История Не пустая.");
    }

    @Test
    void remove() {
    }

    @Test
    void getHistory() {
    }
}