package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;

import java.util.List;

/**
 * Контракт для объекта-менеджера
 */
public interface TaskManager {

    //Текст сообщений об ошибках
    String MSG_ERROR_TYPE_UN_KNOW = "Для выбранного типа задач не создан обработчик в методе.";
    String MSG_ERROR_ID_NOT_FOUND = "Не найдена задача с указанным id.";
    String MSG_ERROR_WRONG_EPIC = "При обновлении эпика дети остаются неизменными.";
    String MSG_ERROR_NOT_FOUND_EPIC = "Не найден эпик для подзадачи.";
    String MSG_ERROR_TYPE_NULL = "Для метода не указан тип задачи.";
    String MSG_ERROR_TASK_EMPTY = "Список задач пуст.";

    //private final static String MSG_ERROR_PARENT_NULL = "Родитель подзадачи не может быть null.";
    //private final static String MSG_ERROR_TYPE_UN_KNOW = "Для выбранного типа задач не создан обработчик в методе.";
    //private final static String MSG_ERROR_ID_NULL = "Не найдена задача с указанным id.";

    Task addTask(String title, String description, IssueStatus status);

    Task addTask(String title, String description);

    SubTask addSubTask(String title, String description, Epic parent, IssueStatus status);

    Epic addEpic(String title, String description);

    void addIssue(Issue issue);

    void updIssue(Issue issue);

    void delIssueById(IssueType issueType, int idIssue);

    Issue getIssueById(IssueType issueType, int idIssue);

    void delAllIssues(IssueType issueType);

    List<Issue> getListAllIssues(IssueType issueType);

    List<SubTask> getListSubTaskOfEpic(Epic epic);

    List<Issue> getHistory();
}
