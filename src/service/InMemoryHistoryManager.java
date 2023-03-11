package service;

import model.Issue;

import java.util.ArrayList;
import java.util.List;

/**
 * Объекта-история просмотров задач {Task, SubTask, Epic}
 */
public class InMemoryHistoryManager implements HistoryManager{

    private final List<Issue> historyOfViewIssueList = new ArrayList<>();
    private final static byte SIZE_HISTORY_OF_VIEW_ISSUE_LIST = 10;

    @Override
    public void add(Issue issue) {

        if (issue != null) {
            if (historyOfViewIssueList.size() == SIZE_HISTORY_OF_VIEW_ISSUE_LIST) {
                historyOfViewIssueList.remove(0);
            }
            historyOfViewIssueList.add(issue);
        }
    }

    @Override
    public List<Issue> getHistory() {
        return historyOfViewIssueList;
    }

    @Override
    public void clearHistory() {
        historyOfViewIssueList.clear();
    }
}
