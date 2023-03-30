package service;

import model.Epic;
import model.Issue;
import model.SubTask;
import model.Task;

import java.util.List;

/**
 * Контракт для очереди с историей просмотров задач типа: {@link Task},{@link SubTask},{@link Epic}
 */
public interface HistoryManager {

    /**
     * Размер очереди просмотренных пользователем задач
     */
    byte SIZE_HISTORY_OF_VIEW_ISSUE_LIST = 10;

    /**
     * Добавить задачу в конец очереди
     */
    void add(Issue issue);

    /**
     * Удалить задачу в очереди по id
     */
    void remove(int id);

    /**
     * Получить список задач в очереди
     */
    List<Issue> getHistory();
}
