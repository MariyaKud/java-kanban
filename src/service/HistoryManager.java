package service;

import model.Issue;

import java.util.List;

/**
 * Контракт для объекта-история просмотров
 */
public interface HistoryManager {

    byte SIZE_HISTORY_OF_VIEW_ISSUE_LIST = 10;

    void add(Issue issue);

    List<Issue> getHistory();

    void clearHistory();
}
