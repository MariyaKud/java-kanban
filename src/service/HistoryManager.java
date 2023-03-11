package service;

import model.Issue;

import java.util.List;

/**
 * Контракт для объекта-история просмотров
 */
public interface HistoryManager {

    void add(Issue issue);

    List<Issue> getHistory();

    void clearHistory();
}
