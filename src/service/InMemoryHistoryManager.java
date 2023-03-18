package service;

import model.Issue;

import java.util.ArrayList;
import java.util.List;

/**
 * Представитель контракта истории просмотров задач {Task, SubTask, Epic}
 */
public class InMemoryHistoryManager implements HistoryManager{

    private final List<Issue> historyOfViewIssueList = new ArrayList<>();

    /**
     * Добавить задачу в конец стека
     */
    @Override
    public void add(Issue issue) {

        if (issue != null) {
            if (historyOfViewIssueList.size() == SIZE_HISTORY_OF_VIEW_ISSUE_LIST) {
                historyOfViewIssueList.remove(0);
            }
            historyOfViewIssueList.add(issue);
        }
    }

    /**
     * Получить список задач в стеке
     */
    @Override
    public List<Issue> getHistory() {
        return historyOfViewIssueList;
    }
}
