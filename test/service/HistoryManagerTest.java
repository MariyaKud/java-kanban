package service;

import model.Issue;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тест менеджера истории просмотров.")
class HistoryManagerTest {

    private static HistoryManager historyManager;

    @BeforeEach
    void beforeEach() {
        //Перед каждым тестом создаем новый экземпляр, чтобы тесты не зависели друг от друга
        historyManager = new InMemoryHistoryManager();
    }

    @DisplayName("Поверяем метод getHistory() для пустого списка.")
    @Test
    void shouldGiveEmptyHistoryForNewManager() {
        //Для нового экземпляра класса HistoryManager - история это пустой список
        //Проверяем, что getHistory() вернет не null и размер списка = 0
        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(0, history.size(), "История не пустая.");
    }

    @DisplayName("Поверяем метод getHistory() для НЕ пустого списка.")
    @Test
    void shouldGiveOneTaskInHistory() {
        //Проверяем наличие задачи в истории, после применения метода add()
        Task task = new Task("Test addTask", "Description", 10);

        historyManager.add(task);

        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(1, history.size(), "Длина списка не соответствует ожидаемой.");
    }

    @DisplayName("Проверка дублирования, добавляем в истории одну и ту же задачу.")
    @Test
    void shouldGiveOneTaskIfAddTwiceSameTasks() {
        //Проверяем наличие задачи в истории, после добавления одной и той же задачи в очереди
        //Она не должна двоиться
        Task task = new Task("Test addTask", "Description", 10);

        historyManager.add(task);
        historyManager.add(task);

        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(1, history.size(), "Длина очереди не соответствует ожидаемой.");
    }

    @DisplayName("Удаляем единственную задачу из истории.")
    @Test
    void shouldGiveEmptyHistoryIfRemoveSingleTask() {
        //Удаляем единственную задачу, проверяем чтобы очередь не превращалась в null и была пустой
        Task task = new Task("Test remove", "Description", 10);

        historyManager.add(task);
        historyManager.remove(0);

        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(0, history.size(), "Длина очереди не соответствует ожидаемой.");
    }

    @DisplayName("Удаляем задачу из начала очереди.")
    @Test
    void shouldGiveLastTaskIfRemoveFromBeginOfHistory() {
        Instant startTime = Instant.now();

        Task task1 = new Task(0, "Test", "Description", 10, Instant.now());
        historyManager.add(task1);

        Task task2 = new Task(1, "Test", "Description",
                               10, startTime.plusSeconds(10 * 60L));
        historyManager.add(task2);

        historyManager.remove(0);

        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(1, history.size(), "Длина очереди не соответствует ожидаемой.");
        assertEquals(task2, history.get(0), "Не корректная задача в очереди.");
    }

    @DisplayName("Удаляем задачу из конца очереди.")
    @Test
    void shouldGiveFirstTaskIfRemoveFromEndOfHistory() {
        //Удаляем задачу из конца очереди
        Instant startTime = Instant.now();

        Task task1 = new Task(0, "Test", "Description", 10, startTime);
        historyManager.add(task1);

        Task task2 = new Task(1, "Test", "Description",
                          10, startTime.plusSeconds(10 * 60L));
        historyManager.add(task2);

        historyManager.remove(1);

        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(1, history.size(), "Длина очереди не соответствует ожидаемой.");
        assertEquals(task1, history.get(0), "Не корректная задача в очереди.");
    }

    @DisplayName("Удаляем задачу из середины очереди.")
    @Test
    void shoudSaveFirstAndLastIfRemoveFromMiddleOfHistory() {
        //Удаляем задачу из середины очереди
        Instant startTime = Instant.now();

        Task task1 = new Task(0, "Test", "Description", 10, startTime);
        historyManager.add(task1);

        Task task2 = new Task(1, "Test", "Description",
                           10, startTime.plusSeconds(20 * 60L));
        historyManager.add(task2);

        Task task3 = new Task(2, "Test", "Description",
                         10, startTime.plusSeconds(30 * 60L));
        historyManager.add(task3);

        historyManager.remove(1);

        final List<Issue> history = historyManager.getHistory();

        assertNotNull(history, "История null.");
        assertEquals(2, history.size(), "Длина очереди не соответствует ожидаемой.");
        assertEquals(task1, history.get(0), "Не корректная задача в начале очереди.");
        assertEquals(task3, history.get(1), "Не корректная задача в конце очереди.");
    }
}