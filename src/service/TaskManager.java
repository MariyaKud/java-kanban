package service;

import model.Issue;
import model.IssueType;

import java.util.List;

public interface TaskManager {

    void addIssue(IssueType issueType, Issue issue);

    void updIssue(IssueType issueType, Issue issue);

    void delIssueById(IssueType issueType, int idIssue);

    Issue getIssueById(IssueType issueType, int idIssue);

    void delAllIssues(IssueType issueType);

    List<Issue> getListAllIssues(IssueType issueType);
}
