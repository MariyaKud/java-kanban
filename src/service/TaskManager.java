package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;

import java.util.List;

/**
 * Контракт для классов объект-менеджер
 */
public interface TaskManager {

    //Текст сообщений об ошибках
    String MSG_ERROR_TYPE_UN_KNOW = "Для выбранного типа задач не создан обработчик в методе.";
    String MSG_ERROR_ID_NOT_FOUND = "Не найдена задача с указанным id.";
    String MSG_ERROR_TASK_EMPTY = "Список задач пуст.";
    String MSG_ERROR_WRONG_EPIC = "При обновлении эпика дети остаются неизменными.";
    String MSG_ERROR_NOT_FOUND_EPIC = "Не найден эпик для подзадачи.";
    String MSG_ERROR_FOR_METHOD = "Возникла проблема при проверке метода.";

    Task addTask(String title, String description, IssueStatus status);

    Task addTask(String title, String description);

    void addTask(Task task);

    SubTask addSubTask(String title, String description, Epic parent, IssueStatus status);

    void addSubTask(SubTask subTask);

    Epic addEpic(String title, String description);

    void addEpic(Epic epic);

    void addIssue(Issue issue);

    void updIssue(Issue issue);

    void delIssueById(IssueType issueType, int idIssue);

    Issue getIssueById(IssueType issueType, int idIssue);

    void delAllIssues(IssueType issueType);

    void delAllTasks();

    void delAllSubTasks();

    void delAllEpics();

    List<Issue> getListAllIssues(IssueType issueType);

    List<Task> getListAllTasks();

    List<SubTask> getListAllSubTasks();

    List<Epic> getListAllEpics();

    List<SubTask> getListSubTaskOfEpic(Epic epic);

    List<Issue> getHistory();
}
