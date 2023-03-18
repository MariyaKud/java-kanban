package service;

import model.Epic;
import model.Issue;
import model.SubTask;
import model.Task;

import java.util.List;

/**
 * Контракт для стека с историей просмотров задач типа: {@link Task},{@link SubTask},{@link Epic}
 */
public interface HistoryManager {

    /**
     * Размер стека просмотренных пользователем задач
     */
    byte SIZE_HISTORY_OF_VIEW_ISSUE_LIST = 10;

    /**
     * Добавить задачу в конец стека
     */
    void add(Issue issue);

    /**
     * Получить список задач в стеке
     */
    List<Issue> getHistory();
}
