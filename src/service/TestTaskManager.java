package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * СЛУЖЕБНЫЙ КЛАСС для отладки менеджеров задач и истории:
 * <p> {@link InMemoryTaskManager}
 * <p> {@link InMemoryHistoryManager}
 */
public class TestTaskManager {

    //Тексты сообщений об ошибках
    final static String MSG_ERROR_TYPE_UN_KNOW = "Для выбранного типа задач не создан обработчик в методе.";
    final static String MSG_ERROR_TASK_EMPTY = "Список в менеджере пуст.";
    final static String MSG_ERROR_ID_NOT_FOUND = "Не найдена сущность с указанным id.";
    final static String MSG_ERROR_NOT_FOUND_EPIC = "Эпик не найден.";
    final static String MSG_ERROR_FOR_METHOD = "При тестировании метода обнаружена ошибка.";
    final static String MSG_ERROR_TEST = "ТЕСТ метода не выполнен, входные данные заданы не корректно.";
    final static String MSG_ERROR_NOT_TEST = "Сценарий тестирования запустить не вышло:";

    /**
     * tracker тестируемый менеджер {@link TaskManager}
     */
    private final InMemoryTaskManager tracker;

    /**
     * idTest идентификатор проводимого теста
     */
    private int idTest;

    /**
     * Множество выполненных тестов с результатом (без повторений)
     */
    private final Set<Test> tests = new HashSet<>();

    public TestTaskManager() {
        this.idTest = 1;
        this.tracker = (InMemoryTaskManager) Managers.getDefault();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Тестируем метод создания задачи в менеджере
     */
    public void testAddTask(Task task) {
        StringBuilder resultMessage = new StringBuilder();
        boolean goalAchieved = true;
        Test test = new Test(
                "Task addTask(Task task)",
                "Создать новую задачу.",
                "Найти задачу в менеджере..",
                "Task getTaskById(int id)",
                "новая задача не найдена.");
        //Выполнить тест
        if (task != null) {
            //Вызов тестируемого метода
            Task newTask = tracker.addTask(task);
            resultMessage.append("Создана задача с id = ").append(task.getId()).append("\n");

            //Проверка достижения цели - задача найдена в HashMap менеджера
            Task taskMustToBe = tracker.getTaskById(task.getId());
            resultMessage.append(taskMustToBe.toString());
            if (!newTask.equals(taskMustToBe)) {
                goalAchieved = false;
            }
        } else {
            resultMessage.append(MSG_ERROR_TEST).append("\n");
            goalAchieved = false;
        }
        registerResultTest(test, goalAchieved, resultMessage.toString());
    }

    /**
     * Тестируем метод создания подзадачи в менеджере
     */
    public void testAddSubTask(SubTask subTask) {
        StringBuilder resultMessage = new StringBuilder();
        boolean goalAchieved = true;
        Test test = new Test(
                "SubTask addSubTask(SubTask subTask)",
                "Создать новую подзадачу.",
                "Найти подзадачу в менеджере..",
                "SubTask getSubTaskById(int id)",
                "новая подзадача не найдена.");
        //Выполнить тест
        if (subTask != null) {
            //Вызов тестируемого метода
            SubTask newSubTask = tracker.addSubTask(subTask);
            resultMessage.append("Создана подзадача с id = ").append(subTask.getId()).append(" для эпика с id = ");
            resultMessage.append(subTask.getParent().getId()).append("\n");

            //Проверка достижения цели - задача найдена в HashMap менеджера
            SubTask subTaskMustToBe = tracker.getSubTaskById(subTask.getId());
            resultMessage.append(subTaskMustToBe.toString());
            if (!newSubTask.equals(subTaskMustToBe)) {
                goalAchieved = false;
            }
        } else {
            resultMessage.append(MSG_ERROR_TEST).append("\n");
            goalAchieved = false;
        }
        //Результат теста
        registerResultTest(test, goalAchieved, resultMessage.toString());
    }

    /**
     * Тестируем метод создания эпика в менеджере
     */
    public void testAddEpic(Epic epic) {
        StringBuilder resultMessage = new StringBuilder();
        boolean goalAchieved = true;
        Test test = new Test(
                "Epic addEpic(Epic epic)",
                "Создать новую эпик.",
                "Найти эпик в менеджере..",
                "Epic getEpicById(int id)",
                "новый эпик не найден.");
        //Выполнить тест
        if (epic != null) {
            Epic newEpic = tracker.addEpic(epic);
            resultMessage.append("Создана эпик с id = ").append(epic.getId()).append("\n");

            //Проверка достижения цели - задача найдена в HashMap менеджера
            Issue epicMustToBe = tracker.getEpicById(epic.getId());
            resultMessage.append(epicMustToBe.toString()).append("\n");
            if (!newEpic.equals(epicMustToBe)) {
                goalAchieved = false;
            }
        } else {
            resultMessage.append(MSG_ERROR_TEST).append("\n");
            goalAchieved = false;
        }
        //Результат теста
        registerResultTest(test, goalAchieved, resultMessage.toString());
    }

    /**
     * Тестируем метод обновления задачи
     */
    public void testUpdateTask(Task task) {
        StringBuilder resultMessage = new StringBuilder();
        boolean goalAchieved = true;
        Test test = new Test(
                "Task UpdateTask(Task task)",
                "Обновить задачу",
                "по входному id находим задачу в менеджере, сравниваем поля (title,description,status)..",
                "Task getTaskById(int id)",
                "если не найдена задача по входному id или не корректно обновлены поля класса.");
        //Выполнить тест
        if (task != null) {
            Task taskToUpdate = tracker.getTaskById(task.getId());
            if (taskToUpdate != null) {
                //Выводим данные по старой задаче
                resultMessage.append("Найдена задача по id = ").append(taskToUpdate.getId()).append(" : ");
                resultMessage.append(taskToUpdate).append("\n");

                //Обновляем задачу
                task = tracker.updateTask(task);

                //Выводим данные по новой задаче
                resultMessage.append("Обновленная задача с id = ").append(task.getId()).append(" : ");
                resultMessage.append(task);

                //Проверка достижения цели
                Task updateTask = tracker.getTaskById(task.getId());
                if (!task.getTitle().equals(updateTask.getTitle()) ||
                        !task.getStatus().equals(updateTask.getStatus()) ||
                        !task.getDescription().equals(updateTask.getDescription())) {
                    goalAchieved = false;
                }
            } else {
                resultMessage.append(MSG_ERROR_ID_NOT_FOUND).append("\n");
                goalAchieved = false;
            }
        } else {
            resultMessage.append(MSG_ERROR_TEST).append("\n");
            goalAchieved = false;
        }
        registerResultTest(test, goalAchieved, resultMessage.toString());
    }

    /**
     * Тестируем метод обновления подзадач
     */
    public void testUpdateSubTask(SubTask subTask) {
        StringBuilder resultMessage = new StringBuilder();
        boolean goalAchieved = true;
        Test test = new Test(
                "SubTask UpdateSubTask(SubTask subTask)",
                "Обновить подзадачу",
                "по входному id находим подзадачу в менеджере, сравниваем поля " +
                        "(title,description,status, parent).",
                "SubTask getSubTaskById(int id)",
                "если не найдена подзадача по входному id или не корректно обновлены поля класса.");
        //Выполнить тест
        if (subTask != null) {
            SubTask subTaskToUpdate = tracker.getSubTaskById(subTask.getId());
            if (subTaskToUpdate != null) {
                //Выводим данные по старой подзадаче
                resultMessage.append("Найдена подзадача по id = ").append(subTaskToUpdate.getId()).append(" : ");
                resultMessage.append(subTaskToUpdate).append("\n");
                // Дополнительная информация
                Epic parent = subTaskToUpdate.getParent();
                resultMessage.append("Родитель подзадачи эпик с id = ").append(parent.getId()).append(" : ");
                resultMessage.append(parent).append("\n");

                //Обновляем подзадачу
                subTask = tracker.updateSubTask(subTask);

                //Выводим данные по новой подзадаче
                resultMessage.append("\nОбновленная подзадача с id = ").append(subTask.getId()).append(" : ").append(subTask);
                // Дополнительная информация
                Epic newParent = subTask.getParent();
                resultMessage.append("\nРодитель подзадачи эпик с id = ").append(newParent.getId()).append(" : ");
                resultMessage.append(newParent);

                //Проверка достижения цели
                SubTask updateSubTask = tracker.getSubTaskById(subTask.getId());
                if (!subTask.getTitle().equals(updateSubTask.getTitle()) ||
                        !subTask.getStatus().equals(updateSubTask.getStatus()) ||
                        !subTask.getDescription().equals(updateSubTask.getDescription()) ||
                        !subTask.getParent().equals(updateSubTask.getParent())) {
                    goalAchieved = false;
                }
            } else {
                resultMessage.append(MSG_ERROR_ID_NOT_FOUND).append("\n");
                goalAchieved = false;
            }
        } else {
            resultMessage.append(MSG_ERROR_TEST).append("\n");
            goalAchieved = false;
        }
        registerResultTest(test, goalAchieved, resultMessage.toString());
    }

    /**
     * Тестируем метод обновления эпика
     */
    public void testUpdateEpic(Epic epic) {
        StringBuilder resultMessage = new StringBuilder();
        boolean goalAchieved = true;
        Test test = new Test(
                "Epic updEpic(Epic epic)",
                "Обновить эпик",
                "по входному id находим эпик в менеджере, сравниваем поля (title,description). " +
                        "Статус как и дети при обновлении меняться не должен.",
                "Epic getEpicById(int id);",
                "если не найден эпик по входному id, не совпали дети, не корректно обновлены поля.");
        //Выполнить тест
        if (epic != null) {
            Epic epicToUpdate = tracker.getEpicById(epic.getId());
            if (epicToUpdate != null) {
                //Выводим данные по старому эпику
                resultMessage.append("Найден эпик по id = ").append(epic.getId()).append(" : ").append(epicToUpdate);

                //Обновляем эпик
                epic = tracker.updateEpic(epic);

                //Выводим данные по новому эпику
                resultMessage.append("Входной эпик с id = ").append(epic.getId()).append(" : ").append(epic);

                Epic updateEpic = tracker.getEpicById(epic.getId());
                if (!epic.getTitle().equals(updateEpic.getTitle()) ||
                        !epic.getStatus().equals(updateEpic.getStatus()) ||
                        !epic.getDescription().equals(updateEpic.getDescription())) {
                    goalAchieved = false;
                }
                if (!epicToUpdate.getChildren().equals((updateEpic.getChildren()))) {
                    goalAchieved = false;
                }
            } else {
                resultMessage.append(MSG_ERROR_ID_NOT_FOUND).append("\n");
                goalAchieved = false;
            }
        } else {
            resultMessage.append(MSG_ERROR_TEST).append("\n");
            goalAchieved = false;
        }
        registerResultTest(test, goalAchieved, resultMessage.toString());
    }

    /**
     * Сценарий тестирования для проверки сформированной истории просмотров.
     * <p>Выводим на экран очередь с историей просмотров
     */
    public void testGetHistory() {
        StringBuilder resultMessage = new StringBuilder();
        Test test = new Test(
                "List<Issue> getHistory()",
                "Просмотреть историю просмотров.",
                "Визуализация.",
                "System.out.println",
                "тест всегда считаем успешным.");
        //Выполнить тест
        resultMessage.append("История просмотров:").append("\n");
        for (Issue issue : tracker.getHistory()) {
            resultMessage.append("\t").append(issue).append("\n");
        }
        //Результат теста
        registerResultTest(test,true, resultMessage.toString());
    }

    /**
     * Тестируем метод удаления задачи по id
     */
    public void testDeleteTaskById(int id) {
        StringBuilder resultMessage = new StringBuilder();
        boolean goalAchieved;
        Test test = new Test(
                "void delTaskById(int id)",
                "Удалить задачу по id",
                "проверить данные в хранилище на наличие удаленной задачи",
                "Task getTaskById(int id)",
                "тест ошибочный, если удаляемая задача будет найдена в менеджере.");
        //Выполнить тест
        resultMessage.append("\t").append("Попытка удаления задачи с Id = ").append(id).append("\n");

        goalAchieved = tracker.deleteTaskById(id) != null;
        resultMessage.append("\t").append("Поиск задачи с Id = ").append(id);
        registerResultTest(test,goalAchieved, resultMessage.toString());

        if (tracker.getTaskById(id) != null) {
            test.setResultTest(false);
        }
    }

    /**
     * Тестируем метод удаления подзадачи по id
     */
    public void testDeleteSubTaskById(int id) {
        StringBuilder resultMessage = new StringBuilder();
        boolean goalAchieved;
        Test test = new Test(
                "void delSubTaskById(int id)",
                "Удалить подзадачу по id",
                "проверить данные в хранилище на наличие удаленной подзадачи",
                "SubTask getSubTaskById(int id)",
                "тест ошибочный, если удаляемая подзадача будет найдена в менеджере.");
        //Выполнить тест
        resultMessage.append("\t").append("Попытка удаления подзадачи с Id = ").append(id).append("\n");

        goalAchieved = tracker.deleteSubTaskById(id) != null;
        resultMessage.append("\t").append("Поиск подзадачи с Id = ").append(id);
        registerResultTest(test,goalAchieved, resultMessage.toString());

        if (tracker.getSubTaskById(id) != null) {
            test.setResultTest(false);
        }
        //Дополнительно для подзадачи - необходимо проверить, что подзадачи нет в детях эпика
        for (Epic epic : tracker.getAllEpics()) {
            for (SubTask subTask : tracker.getChildrenOfEpicById(epic.getId())) {
                if (subTask.getId() == id) {
                    test.setResultTest(false);
                    break;
                }
            }
        }
    }

    /**
     * Тестируем метод удаления эпика по id
     */
    public void testDeleteEpicById(int id) {
        StringBuilder resultMessage = new StringBuilder();
        boolean goalAchieved;
        Test test = new Test(
                "void delEpicById(int id)",
                "Удалить эпик по id",
                "проверить данные в хранилище на наличие удаляемого эпика",
                "Epic getEpicById(int id)",
                "тест ошибочный, если удаляемый эпик будет найден в менеджере.");
        //Выполнить тест
        resultMessage.append("\t").append("Попытка удаления эпика с Id = ").append(id).append("\n");

        goalAchieved = tracker.deleteEpicById(id) != null;
        resultMessage.append("\t").append("Поиск эпика с Id = ").append(id);
        registerResultTest(test,goalAchieved, resultMessage.toString());

        if (tracker.getEpicById(id) != null) {
            test.setResultTest(false);
        }
        //Для эпика необходимо проверить, что нет подзадач с таким родителем
        for (SubTask subTask : tracker.getAllSubTasks()) {
            if (subTask.getParent().getId() == id) {
                test.setResultTest(false);
                break;
            }
        }
    }

    /**
     * Тестируем метод удаления списка задач
     */
    public void testDeleteAllTasks() {
        boolean goalAchieved = true;
        Test test = new Test(
                "void delAllTasks()",
                "Удалить все задачи",
                "проверить хранилище с задачами в менеджере",
                "List<Task> getListAllTasks()",
                "список задач в менеджере не пуст.");
        //Выполнить тест
        tracker.deleteAllTasks();
        //Проверка
        List<Task> listForCheck = tracker.getAllTasks();
        if (!listForCheck.isEmpty()) {
            goalAchieved = false;
        }
        registerResultTest(test, goalAchieved, "Задачи в хранилище..");
        printListAllTasks();
    }

    /**
     * Тестируем метод удаления списка подзадач
     */
    public void testDeleteAllSubTasks() {
        boolean goalAchieved = true;
        Test test = new Test(
                "void delAllSubTasks()",
                "Удалить все подзадачи",
                "проверить хранилище с подзадачами в менеджере. Проверить списки детей эпиков.",
                "List<Task> getListAllSubTasks()",
                "список подзадач в менеджере не пуст или остались дети у эпиков.");
        //Выполнить тест
        tracker.deleteAllSubTasks();
        //Проверка
        List<SubTask> listForCheck = tracker.getAllSubTasks();
        if (!listForCheck.isEmpty()) {
            goalAchieved = false;
        }
        List<Epic> epicListForCheck = tracker.getAllEpics();
        for (Epic epic : epicListForCheck) {
            if (!epic.getChildren().isEmpty()) {
                goalAchieved = false;
                break;
            }
        }
        registerResultTest(test,goalAchieved, "Подзадачи в хранилище ..");
        printListAllSubTasks();
    }

    /**
     * Тестируем метод удаления списка эпиков
     */
    public void testDeleteAllEpics() {
        boolean goalAchieved = true;
        Test test = new Test(
                "void delAllEpics()",
                "Удалить все эпики",
                "проверить хранилище эпиков в менеджере.",
                "List<Task> getListAllEpics()",
                "тест ошибочный, если остались эпики или подзадачи в менеджере.");
        //Выполнить тест
        tracker.deleteAllEpics();
        //Проверка
        List<Epic> listForCheck = tracker.getAllEpics();
        if (!listForCheck.isEmpty()) {
            goalAchieved = false;
        }
        List<SubTask> subTaskListForCheck = tracker.getAllSubTasks();
        if (!subTaskListForCheck.isEmpty()) {
            goalAchieved = false;
        }
        registerResultTest(test, goalAchieved, "Эпики в хранилище ..");
        printListAllEpic();
    }

    public void testGetIssueById(int id) {

        Test test = new Test(
                "Task getTaskById(int id),SubTask getSubTaskById(int id),Epic getEpicById(int id)",
                "получить сущность по id.",
                "вывести на экран найденную сущность.",
                "System.out.print",
                "тест всегда считаем успешным.");
        //Выполнить тест
        Issue issue = tracker.getTaskById(id);
        if (issue == null) {
            issue = tracker.getSubTaskById(id);
        }
        if (issue == null) {
            issue = tracker.getEpicById(id);
        }
        String mes;
        if (issue == null) {
            mes = "Сущность с указанным id не найдена.";
        } else {
            mes = issue.toString();
        }
        registerResultTest(test, issue != null, mes);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Описание проведенного теста
     */
    private static class Test {
        /**
         * Тестируемый метод
         */
        private final String method;
        /**
         * Цель теста
         */
        private final String goal;
        /**
         * Способ проверки
         */
        private final String way;
        /**
         * Дополнительно используются методы
         */
        private final String use;
        /**
         * Условия ошибки в тесте
         */
        private final String errorCondition;
        /**
         * Результат теста
         */
        private boolean resultTest = false;

        public Test(String method, String goal, String way, String use, String errorCondition) {
            this.method = method;
            this.goal = goal;
            this.way = way;
            this.use = use;
            this.errorCondition = errorCondition;
        }

        public void setResultTest(boolean resultTest) {
            this.resultTest = resultTest;
        }

        @Override
        public String toString() {
            StringBuilder resultOut = new StringBuilder();
            //Обязательные поля
            resultOut.append("Тестируемый метод: ").append(method).append("\n");
            resultOut.append("Цель теста: ").append(goal).append("\n");
            resultOut.append("Способ проверки: ").append(way).append("\n");
            //Не обязательное поле, заполняется не для всех тестов
            if (!"".equals(use)) {
                resultOut.append("Дополнительно используются методы: ").append(use).append("\n");
            }
            //Условия ошибки
            resultOut.append("Условия ошибки в тесте: ").append(errorCondition).append("\n");
            resultOut.append("Результат теста ");
            if (resultTest) {
                resultOut.append("✅");
            } else {
                resultOut.append("❌");
            }
            return resultOut.toString();
        }

        @Override
        public int hashCode() {
            return  Objects.hash(method);
        }

        @Override
        public boolean equals(Object o) {
            // 1
            if (this == o) {
                return true;
            }

            // 2
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            // 3
            Test test = (Test) o;
            return method.equals(test.method);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Описание проведенного теста
     */
    public void printInfoAboutAllTests() {
        int positive = 0;
        int negative = 0;

        printLine();
        System.out.println("РЕЗУЛЬТАТ ТЕСТИРОВАНИЯ:");
        System.out.println("Количество проведенных тестов  = " + (idTest-1));
        System.out.println("Количество проверенных методов = " + tests.size());
        printLine();

        for (Test test : tests) {
            System.out.println(test + "\n");
            if (test.resultTest) {
                ++positive;
            } else {
                ++negative;
            }
        }

        System.out.print("Положительный результат = " + positive);
        System.out.println(" ✅");
        System.out.print("Отрицательный результат = " + negative);
        System.out.println(" ❌");
    }

    /**
     * Обработать результат тестирования.
     *<p> Вывести результат проведенного теста на экран
     *<p> Добавить проведенный тест в множество
     */
    private void registerResultTest(Test test, boolean result, String message) {
        test.setResultTest(result);
        printHeadOfTest();
        System.out.println(test);
        System.out.println(message);
        tests.add(test);
        if (!result) {
            System.out.println(MSG_ERROR_FOR_METHOD);
        }
    }

    /**
     *Вывести заголовок проводимого теста.
     *<p>Подготовить номер для следующего теста (++idTest)
     */
    private void printHeadOfTest() {
        printLine();
        System.out.println("ТECT №"+idTest);
        ++idTest;
    }

    /**
     * Печать разделительную линию
     */
    public void printLine() {
        System.out.println("-------------------------------------------");
    }

    /**
     * Печать всех типов задач в удобочитаемом формате
     */
    public void printTaskManager() {
        printLine();
        System.out.println("Данные в памяти менеджера:");
        printListAllTasks();
        printListAllSubTasks();
        printListAllEpic();
    }

    /**
     * Печать списка задач
     */
    private void printListAllTasks() {
        System.out.println("TASKS:");
        for (Task task : tracker.getAllTasks()) {
            System.out.println("\t" + task);
        }
    }

    /**
     * Печать списка подзадач
     */
    private void printListAllSubTasks() {
        System.out.println("SUBTASKS:");
        for (SubTask subTask : tracker.getAllSubTasks()) {
            System.out.println("\t" + subTask);
        }
    }

    /**
     * Печать списка эпиков
     */
    private void printListAllEpic() {
        System.out.println("EPICS:");
        for (Epic epic : tracker.getAllEpics()) {
            System.out.println("\t" + epic);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Получить последнюю задачу менеджера заданного типа, может вернуть NULL.
     * Используется в сценариях тестирования для минимизации диалога с тестировщиком.
     *
     * @param issueType - тип задачи IssueType = {Task, SubTask, Epic}
     * @return - идентификатор последней созданной задачи. Если нет задач выбранного типа, то вернет NULL
     */
    private Integer getIdForLastIssue(IssueType issueType) {
        List<Issue> issues = new ArrayList<>();

        switch(issueType){
            case TASK:
                List<Task> tasksList = tracker.getAllTasks();
                issues.addAll(tasksList);
                break;

            case EPIC:
                List<Epic> epicsList = tracker.getAllEpics();
                issues.addAll(epicsList);
                break;
            case SUBTASK:
                List<SubTask> subTaskList = tracker.getAllSubTasks();
                issues.addAll(subTaskList);
                break;
            default:
                System.out.println(MSG_ERROR_TYPE_UN_KNOW);
                return null;
        }
        if (!issues.isEmpty()) {
            return issues.get(issues.size() - 1).getId();
        } else {
            System.out.println(MSG_ERROR_TASK_EMPTY);
        }
        return null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Создать экземпляр класса {@link Task}
     * @param title заголовок
     * @param description описание
     * @return новый экземпляр класса {@link Task} со статусом NEW
     */
    private Task addTask(String title, String description) {
        return new Task(0, title, description);
    }

    /**
     * Создать экземпляр класса {@link SubTask}
     * @param title заголовок
     * @param description описание
     * @param parent - владелец подзадачи, экземпляр класса {@link Epic}
     * @return новый экземпляр класса {@link Epic}, без детей со статусом NEW
     */
    private SubTask addSubTask(String title, String description, Epic parent) {
        if (parent == null) {
            parent = addEpic("Родитель для подзадачи: " + title, "");
        }
        SubTask newSubTask = new SubTask(0, title, description, parent, IssueStatus.NEW);
        if (!parent.getChildren().contains(newSubTask)) {
            parent.getChildren().add(newSubTask);
        }
        return newSubTask;
    }

    /**
     * Создать экземпляр класса {@link Epic}
     * @param title заголовок
     * @param description описание
     * @return новый экземпляр класса Epic, без детей со статусом NEW
     */
    private Epic addEpic(String title, String description) {
        return new Epic(0, title, description);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * Сценарий создания задачи, для проверки метода - создать задачу
     * <p>Создаем экземпляр класса задача {@link Task}.
     * <p>Применяем тест метода создания задачи в менеджере.
     */
    public void testScriptAddTaskOneMore() {
        int numberTask=tracker.getAllTasks().size() + 1;
        testAddTask(addTask("Задача" + numberTask,"Описание задачи" + numberTask));
    }

    /**
     * Сценарий создания эпика с детьми, для проверки методов создания эпика {@link Epic} и подзадачи {@link SubTask}
     * @param quantitySubTask количество подзадач эпика
     */
    public void testScriptAddEpicWithChildren(int quantitySubTask) {
        int numberEpic    = tracker.getAllEpics().size() + 1;
        int numberSubTask = tracker.getAllSubTasks().size() + 1;
        Epic newEpic = addEpic("Эпик " + numberEpic, "Описание эпика " + numberEpic);
        testAddEpic(newEpic);
        for (int i = 0; i < quantitySubTask; i++) {
            testAddSubTask(addSubTask("Подзадача " + numberSubTask, "Описание подзадачи "
                    + numberSubTask++, newEpic));
        }
    }

    /**
     * Сценарий тестирования метода обновить задачу (статус):
     * <p>Находим последнюю созданную задачу, если нет, то тест не произойдет - MSG_ERROR_TASK_EMPTY.
     * <p>Клонируем последнюю задачу.
     * <p>Устанавливаем новый статус клону.
     * <p>Вызываем тестируемый метод с передачей клона в качестве обновляемого объекта.
     * @param status новый статус задачи
     */
    public void testScriptUpdateStatusForLastTask(IssueStatus status) {
        Integer id = getIdForLastIssue(IssueType.TASK);
        if (id != null) {
            Task lastTask = tracker.getTaskById(id);
            if (lastTask != null) {
                Task cloneTask = new Task(lastTask);
                cloneTask.setStatus(status);
                cloneTask.setDescription(lastTask.getDescription() + "(Обновлена)");
                testUpdateTask(cloneTask);
            } else {
                printLine();
                System.out.println(MSG_ERROR_NOT_TEST);
                System.out.println(MSG_ERROR_ID_NOT_FOUND);
            }
        } else {
            printLine();
            System.out.println(MSG_ERROR_NOT_TEST);
            System.out.println(MSG_ERROR_TASK_EMPTY);
        }
    }

    /**
     * Сценарий тестирования метода обновить подзадачу (статус):
     * <p>Находим последнюю созданную подзадачу, если нет, то тест не произойдет - MSG_ERROR_TASK_EMPTY.
     * <p>Клонируем последнюю подзадачу.
     * <p>Устанавливаем новый статус клону.
     * <p>Вызываем тестируемый метод с передачей клона в качестве обновляемого объекта.
     * @param status новый статус подзадачи
     */
    public void testScriptUpdateStatusForLastSubTask(IssueStatus status) {
        Integer id = getIdForLastIssue(IssueType.SUBTASK);
        if (id != null) {
            SubTask lastSubTask = tracker.getSubTaskById(id);
            if (lastSubTask != null) {
                SubTask cloneSubTask = new SubTask(lastSubTask);
                cloneSubTask.setStatus(status);
                testUpdateSubTask(cloneSubTask);
            } else {
                printLine();
                System.out.println(MSG_ERROR_NOT_TEST);
                System.out.println(MSG_ERROR_ID_NOT_FOUND);
            }
        } else {
            printLine();
            System.out.println(MSG_ERROR_NOT_TEST);
            System.out.println(MSG_ERROR_TASK_EMPTY);
        }
    }

    /**
     * Сценарий тестирования метода обновить подзадачу (родителя)
     * <p>Для выполнения сценария необходимо минимум два эпика с детьми, иначе сценарий не будет запущен
     * <p>Находим первый эпик.
     * <p>Находим последнюю подзадачу.
     * <p>Убеждаемся, что первый эпик не является родителем последней подзадачи, иначе сценарий не будет запущен.
     * <p>Создаем клона последней подзадачи.
     * <p>Устанавливаем первый эпик родителем клона.
     * <p>Передаем клона в качестве объекта обновления.
     */
    public void testUpdateParentForLastSubTask() {
        Integer id = getIdForLastIssue(IssueType.SUBTASK);
        List<Epic> listEpics= tracker.getAllEpics();

        if (listEpics.size() > 1) {
            int idNewParent = listEpics.get(0).getId();
            if (id != null) {
                SubTask lastSubTask = tracker.getSubTaskById(id);
                Epic firstEpic = tracker.getEpicById(idNewParent);
                if (lastSubTask != null && firstEpic != null) {
                    if (lastSubTask.getParent() != firstEpic) {
                        SubTask newSubTask = new SubTask(lastSubTask);
                        newSubTask.setParent(firstEpic);
                        testUpdateSubTask(newSubTask);
                    } else {
                        printLine();
                        System.out.println(MSG_ERROR_NOT_TEST);
                        System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
                    }
                } else {
                    printLine();
                    System.out.println(MSG_ERROR_NOT_TEST);
                    System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
                }
            } else {
                printLine();
                System.out.println(MSG_ERROR_NOT_TEST);
                System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
            }
        } else {
            printLine();
            System.out.println(MSG_ERROR_NOT_TEST);
            System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
        }
    }

    /**
     * Сценарий тестирования метода обновить эпик (статус рассчитывается по состоянию подзадач)
     * <p> Создаем клон последнего эпика
     * <p> Устанавливаем статус
     * <p> Применяем тестируемый метод к клону
     * @param status новый статус подзадачи
     */
    public void testUpdateStatusForLastEpic(IssueStatus status) {

        Integer id = getIdForLastIssue(IssueType.EPIC);
        if (id != null) {
            Epic lastEpic  = tracker.getEpicById(id);
            if (lastEpic != null) {
                Epic cloneEpic = new Epic(lastEpic);
                cloneEpic.setStatus(status);
                testUpdateEpic(cloneEpic);
            } else {
                printLine();
                System.out.println(MSG_ERROR_NOT_TEST);
                System.out.println(MSG_ERROR_ID_NOT_FOUND);
            }
        } else {
            printLine();
            System.out.println(MSG_ERROR_NOT_TEST);
            System.out.println(MSG_ERROR_TASK_EMPTY);
        }
    }

    /**
     * Сценарий тестирования метода удаления задачи:
     * <p>Находим последнюю созданную задачу.
     * <p>Пытаемся ее удалить.
     */
    public void testScriptDeleteLastTask() {
        Integer id = getIdForLastIssue(IssueType.TASK);
        if (id != null) {
            testDeleteTaskById(id);
        } else {
            printLine();
            System.out.println(MSG_ERROR_NOT_TEST);
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
        }
    }

    /**
     * Сценарий тестирования метода удаления подзадачи
     * <p>Находим последнюю созданную подзадачу.
     * <p>Пытаемся ее удалить.
     */
    public void testScriptDeleteLastSubTask() {
        Integer id = getIdForLastIssue(IssueType.SUBTASK);
        if (id != null) {
            testDeleteSubTaskById(id);
        }
    }

    /**
     * Сценарий тестирования метода удаления эпика
     * <p>Находим последний эпик.
     * <p>Пытаемся ее удалить.
     */
    public void testScriptDeleteLastEpic() {
        Integer id = getIdForLastIssue(IssueType.EPIC);
        if (id != null) {
            testDeleteEpicById(id);
        }
    }
}
