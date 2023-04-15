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
     * Добавить задачу в конец очереди
     * @param issue - добавляемая задача
     */
    void add(Issue issue);

    /**
     * Удалить задачу в очереди по id
     * @param id - идентификатор удаляемой задачи
     */
    void remove(int id);

    /**
     * Получить историю просмотров задач
     * @return - список просмотренных задач
     */
    List<Issue> getHistory();
}
