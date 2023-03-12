package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;

import java.util.List;

/**
 * СЛУЖЕБНЫЙ КЛАСС для тестирования сервиса экземпляра класса, реализации интерфейса {@link TaskManager}
 */
public class TestTaskManager implements TaskManager{

    /**
     tracker тестируемый менеджер {@link TaskManager}
     */
    private final InMemoryTaskManager tracker;

    /**
     * idTest идентификатор проводимого теста
     */
    private int idTest;

    /**
     *  Если при выполнении очередного теста случилась ошибка, то
     *  необходимо установить commonGoodResultAllTest = false
     */
    private boolean commonGoodResultAllTest;

    public TestTaskManager() {
        this.idTest = 1;
        this.commonGoodResultAllTest = true;
        this.tracker = Managers.getDefault();
    }

    public boolean isCommonGoodResultAllTest() {
        return commonGoodResultAllTest;
    }

    @Override
    public Task addTask(String title, String description, IssueStatus status) {

        Task newTask = tracker.addTask(null,null,null);

        printHeadOfTest("Task addTask(String title, String description, IssueStatus status)",
                        "создать новую задачу",
                        "проверка на null - все входные параметры подменяем на null.",
                        "",
                        "метод вернет null");

        viewResult(newTask!= null);
        System.out.println(newTask);

        return newTask;
    }

    @Override
    public Task addTask(String title, String description) {

        Task newTask = tracker.addTask(null,null);

        printHeadOfTest("Task addTask(String title, String description)",
                "создать новую задачу",
                "проверка на null - все входные параметры подменяем на null.",
                "",
                "метод вернет null");

        viewResult(newTask!= null);
        System.out.println(newTask);

        return newTask;
    }

    public void addTask() {
        int numberTask = tracker.getListAllIssues(IssueType.TASK).size() + 1;

        addIssue(tracker.addTask("Задача " + numberTask,
                "Описание задачи " + numberTask));

    }

    @Override
    public SubTask addSubTask(String title, String description, Epic parent, IssueStatus status) {

        SubTask newSubTask = tracker.addSubTask(null,null,null,null);

        printHeadOfTest("SubTask addSubTask(String title, String description, Epic parent, " +
                        "IssueStatus status)",
                       "создать новую подзадачу",
                       "проверка на null - все входные параметры подменяем на null.",
                       "getParent()",
                       "метод вернет null");

        viewResult(newSubTask!= null);
        System.out.println(newSubTask);
        if (newSubTask != null) {
            System.out.println(newSubTask.getParent());
        }

        return newSubTask;
    }

    @Override
    public Epic addEpic(String title, String description) {

        Epic newEpic = tracker.addEpic(null,null);

       printHeadOfTest("addEpic(String title, String description)",
                        "создать новый эпик.",
                        "проверка на null - все входные параметры подменяем на null.",
                        "",
                        "метод вернет null");

        viewResult(newEpic!= null);
        System.out.println(newEpic);

        return newEpic;
    }

    @Override
    public void addIssue(Issue issue) {

        boolean goalAchieved = true;                // тест пройден
        IssueType issueType  = getIssueType(issue); // тип входной задачи

        printHeadOfTest("void addIssue(Issue issue)",
                       "создать новую задачу с типом " + issueType,
                       "выполняем поиск новой задачи в хранилище менеджера.",
                       "addTask(String title, String description), getListAllIssues(issueType),getIssueById",
                       "новая задача не найдена или тип задачи не известен менеджеру.");

        //Обращение к методу
        if (issueType != null) {
            tracker.addIssue(issue);
            System.out.println("Создана задача типа " + issueType + " с id = " + issue.getId());

            //Проверка достижения цели - задача найдена в HashMap менеджера
            Issue issueMustToBe = tracker.getIssueById(issueType, issue.getId());
            if (issueMustToBe == null) {
                goalAchieved = false;
            }
        } else {
            goalAchieved = false;
        }

        viewResult(goalAchieved);

        //Визуализация результата
        if (issueType != null) {
            System.out.println(tracker.getListAllIssues(issueType));
        } else {
            System.out.println(MSG_ERROR_TYPE_UN_KNOW);
        }
    }

    @Override
    public void updIssue(Issue issue) {

    }

    @Override
    public void delIssueById(IssueType issueType, int idIssue) {

    }

    @Override
    public Issue getIssueById(IssueType issueType, int idIssue) {
        return null;
    }

    @Override
    public void delAllIssues(IssueType issueType) {

    }

    @Override
    public List<Issue> getListAllIssues(IssueType issueType) {

        List<Issue> listIssue = tracker.getListAllIssues(issueType);

        printHeadOfTest("List<Issue> getListAllIssues(IssueType issueType)",
                "получить список задач типа " + issueType,
                "визуализация полученного списка задач.",
                "System.out.print",
                "тест всегда считаем успешным.");

        viewResult(true);
        System.out.println(listIssue);

        return null;
    }

    @Override
    public List<SubTask> getListSubTaskOfEpic(Epic epic) {
        return null;
    }

    @Override
    public List<Issue> getHistory() {
        return null;
    }

    public void printLine() {
        System.out.println("-------------------------------------------");
    }

    /**
     * Печать всех типов задач в удобочитаемом формате
     */
    public void printTaskManager() {
        printLine();
        System.out.println("Списки задач в памяти тестируемого менеджера:");
        System.out.println(tracker);
    }

    /**
     * Начать тест с начала:
     *<p> - очищает все HashMap менеджера: задачи/подзадачи/эпики
     *<p> - сбрасывает счетчик проводимых тестов (idTest = 1)
     *<p> - сбрасывает результат получения ошибок, для нового теста их нет (commonGoodResultAllTest = true)
     */
    public void restartTest() {
        tracker.restartTaskManager();
        idTest = 1;
        commonGoodResultAllTest = true;
    }

    /**
     * Вывести заголовок проводимого теста:
     * <p> - выводим номер проводимого теста
     * <p> - выводит описание проводимого теста
     * <p> - готовит номер для следующего теста (++idTest)
     *
     * @param methodForTest  какой метод тестируем
     * @param purposeMethod назначение метода
     * @param wayForTest описание способа проверки метода
     * @param usingMethod перечисление других методов менеджера, используемых для проверки
     * @param mistakeMethod описание ошибки метода
     */
    private void printHeadOfTest(String methodForTest, String purposeMethod, String wayForTest,
                                 String usingMethod, String mistakeMethod) {
        printLine();
        System.out.println("ТECT №" + idTest);
        System.out.println("Тестируемый метод: " + methodForTest);
        System.out.println("Цель теста: " + purposeMethod);
        System.out.println("Способ проверки: " + wayForTest);
        if (!"".equals(usingMethod)) {
            System.out.println("Дополнительно используются методы: " + usingMethod);
        }
        System.out.println("Условия ошибки в тесте: " + mistakeMethod+"\n");
        ++idTest;
    }

    /**
     * Визуализирует результат теста:
     *
     * @param resultGood - результат True (достигнут) / False (что-то пошло не так)
     */
    public void viewResult(boolean resultGood) {
        System.out.print("Результат теста ");
        if (resultGood) {
            System.out.println(" ✅");
        } else {
            System.out.println(" ❌");
        }
        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!resultGood) {
            commonGoodResultAllTest = false;
        }
    }

    /**
     * Получить последнюю задачу менеджера заданного типа, может вернуть NULL.
     * Используется в ряде тестов для упрощения кода, т.к. эти тесты могут проводиться над любой задачей и допущение,
     * что тест выполняется для последней не искажает проводимый тест.
     *
     * @param issueType - тип задачи IssueType = {Task, SubTask, Epic}
     * @return - идентификатор последней созданной задачи. Если нет задач выбранного типа, то вернет NULL
     */
    private Integer getIdForLastIssue(IssueType issueType) {

        if (issueType != null) {
            List<Issue> issues = tracker.getListAllIssues(issueType);
            if (!issues.isEmpty()) {
                return issues.get(issues.size() - 1).getId();
            } else {
                System.out.println(MSG_ERROR_TASK_EMPTY);
            }
        } else {
            System.out.println(MSG_ERROR_TYPE_NULL);
        }
        return null;
    }

    private IssueType getIssueType(Issue issue) {
        if (issue instanceof Task) {
            return IssueType.TASK;
        } else if (issue instanceof SubTask) {
            return IssueType.SUBTASK;
        } else if (issue instanceof Epic) {
            return IssueType.EPIC;
        } else {
            return null;
        }
    }
}
