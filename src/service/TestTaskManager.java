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
public class TestTaskManager implements TaskManager {

    /**
     * tracker тестируемый менеджер {@link TaskManager}
     */
    private final InMemoryTaskManager tracker;

    /**
     * idTest идентификатор проводимого теста
     */
    private int idTest;

    /**
     * Если при выполнении очередного теста случилась ошибка, то
     * необходимо установить commonGoodResultAllTest = false
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

    /**
     * Тестируем метод создание объекта-задача с заданным статусом
     */
    public Task addTask(String title, String description, IssueStatus status) {

        Task newTask = tracker.addTask(null, null, null);

        printHeadOfTest("Task addTask(String title, String description, IssueStatus status)",
                "создать новую задачу",
                "проверка на null - все входные параметры подменяем на null.",
                "",
                "метод вернет null");

        viewResult(newTask != null);
        System.out.println(newTask);

        return null;
    }

    /**
     * Тестируем метод создание объекта-задача с дефолтным статусом NEW
     */
    public Task addTask(String title, String description) {

        Task newTask = tracker.addTask(null, null);

        printHeadOfTest("Task addTask(String title, String description)",
                "создать новую задачу",
                "проверка на null - все входные параметры подменяем на null.",
                "",
                "метод вернет null");

        viewResult(newTask != null);
        System.out.println(newTask);

        return null;
    }

    /**
     * Тестируем метод создания задачи в менеджере, используем метод для типа задач Task
     */
    @Override
    public void addTask(Task task) {

        boolean goalAchieved = true;// тест пройден

        printHeadOfTest("void addTask(Task task)",
                "создать новую задачу",
                "выполняем поиск новой задачи в хранилище менеджера.",
                "addTask(String title, String description), getListAllIssues(issueType)," +
                        "Task getTaskById(int id)",
                "задача не найдена.");

        //Обращение к методу
        tracker.addTask(task);
        System.out.println("Создана задача с id = " + task.getId());

        //Проверка достижения цели - задача найдена в HashMap менеджера
        Issue taskMustToBe = tracker.getTaskById(task.getId());
        if (taskMustToBe == null) {
            goalAchieved = false;
        }

        viewResult(goalAchieved);

        //Визуализация результата
        if (taskMustToBe != null) {
            printListAllIssues(IssueType.TASK);
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
        }
    }

    /**
     * Служебный метод создания задачи в менеджере - тестируем addTask
     */
    public void addTask() {
        int numberTask = tracker.getListAllIssues(IssueType.TASK).size() + 1;
        addTask(tracker.addTask("Задача " + numberTask, "Описание задачи " + numberTask));
    }

    /**
     * Служебный метод создания задачи в менеджере - тестируем addIssue
     */
    public void addIssueTask() {
        int numberTask = tracker.getListAllIssues(IssueType.TASK).size() + 1;
        addIssue(tracker.addTask("Задача " + numberTask, "Описание задачи " + numberTask));
    }

    /**
     * Тестируем метод создание объекта-подзадача
     */
    public SubTask addSubTask(String title, String description, Epic parent, IssueStatus status) {

        SubTask newSubTask = tracker.addSubTask(null, null, null, null);

        printHeadOfTest("SubTask addSubTask(String title, String description, Epic parent, " +
                        "IssueStatus status)",
                "создать новую подзадачу",
                "проверка на null - все входные параметры подменяем на null.",
                "getParent()",
                "метод вернет null");

        viewResult(newSubTask != null);
        System.out.println(newSubTask);
        if (newSubTask != null) {
            System.out.println(newSubTask.getParent());
        }

        return null;
    }

    /**
     * Тестируем метод создания подзадачи в менеджере, используем метод для типа задач SubTask
     */
    @Override
    public void addSubTask(SubTask subTask) {
        // тест пройден
        boolean goalAchieved = true;

        printHeadOfTest("void addSubTask(SubTask subTask)",
                "создать новую подзадачу",
                "выполняем поиск новой подзадачи в хранилище менеджера.",
                "SubTask addSubTask(String title, String description, Epic parent, IssueStatus status) ," +
                        " getListAllIssues(issueType), void addSubTask(SubTask subTask)",
                "новая подзадача не найдена.");

        if (subTask != null) {
            //Обращение к методу
            tracker.addSubTask(subTask);
            System.out.println("Создана подзадача с id = " + subTask.getId() + " для эпика с id = "
                    + subTask.getParent().getId());

            //Проверка достижения цели - задача найдена в HashMap менеджера
            Issue subTaskMustToBe = tracker.getSubTaskById(subTask.getId());
            if (subTaskMustToBe == null) {
                goalAchieved = false;
            }
        } else {
            goalAchieved = false;
        }

        viewResult(goalAchieved);

        //Визуализация результата
        if (goalAchieved) {
            printListAllIssues(IssueType.SUBTASK);
            printListAllIssues(IssueType.EPIC);
        } else {
            System.out.println(MSG_ERROR_FOR_METHOD);
        }
    }

    /**
     * Тестируем метод создание объекта-эпика
     */
    public Epic addEpic(String title, String description) {

        Epic newEpic = tracker.addEpic(null, null);
        printHeadOfTest("addEpic(String title, String description)",
                "создать новый эпик.",
                "проверка на null - все входные параметры подменяем на null.",
                "",
                "метод вернет null");

        viewResult(newEpic != null);
        System.out.println(newEpic);

        return newEpic;
    }

    /**
     * Тестируем метод создания эпика с заданным количеством подзадач универсальным методом для класса Issue
     *
     * @param quantitySubTask количество подзадач эпика
     */
    public void addIssueEpic(int quantitySubTask) {
        int numberEpic = tracker.getListAllIssues(IssueType.EPIC).size() + 1;
        Epic newEpic = tracker.addEpic("Эпик " + numberEpic, "Описание эпика " + numberEpic);
        addIssue(newEpic);
        for (int i = 0; i < quantitySubTask; i++) {
            addIssue(tracker.addSubTask("Эпик " + numberEpic, "Описание эпика " + numberEpic,
                    newEpic, IssueStatus.NEW));
        }
    }

    /**
     * Тестируем метод создания эпика с заданным количеством подзадач используя специальные методы
     * классов Epic и SubTask
     *
     * @param quantitySubTask количество подзадач эпика
     */
    public void addEpic(int quantitySubTask) {
        int numberEpic = tracker.getListAllEpics().size() + 1;
        Epic newEpic = tracker.addEpic("Эпик " + numberEpic, "Описание эпика " + numberEpic);
        addEpic(newEpic);
        for (int i = 0; i < quantitySubTask; i++) {
            addSubTask(tracker.addSubTask("Эпик " + numberEpic, "Описание эпика " + numberEpic,
                    newEpic, IssueStatus.NEW));
        }
    }

    /**
     * Тестируем метод создания эпика в менеджере, используем метод для типа задач Epic
     */
    @Override
    public void addEpic(Epic epic) {
        boolean goalAchieved = true;// тест пройден

        printHeadOfTest("void addEpic(Epic epic)",
                "создать новый эпик",
                "выполняем поиск эпика в хранилище менеджера.",
                "addEpic(String title, String description), getListAllIssues(issueType)," +
                        "Epic getEpicById(int id);",
                "эпик не найден.");

        //Обращение к методу
        tracker.addEpic(epic);
        System.out.println("Создана эпик с id = " + epic.getId());

        //Проверка достижения цели - задача найдена в HashMap менеджера
        Issue epicMustToBe = tracker.getEpicById(epic.getId());
        if (epicMustToBe == null) {
            goalAchieved = false;
        }

        viewResult(goalAchieved);

        //Визуализация результата
        if (epicMustToBe != null) {
            printListAllIssues(IssueType.EPIC);
        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
        }
    }

    public void addIssue(Issue issue) {

        boolean goalAchieved = true;                // тест пройден
        IssueType issueType = getIssueType(issue); // тип входной сущности

        printHeadOfTest("void addIssue(Issue issue)",
                "создать новую сущность в менеджере задач с типом " + issueType,
                "выполняем поиск новой сущности в хранилище менеджера.",
                "addTask(String title, String description), getListAllIssues(issueType)," +
                        "getIssueById(IssueType issueType, int idIssue)",
                "новая сущность не найдена или тип сущности не известен менеджеру.");

        //Обращение к методу
        if (issueType != null) {
            tracker.addIssue(issue);
            System.out.println("Создана сущность типа " + issueType + " с id = " + issue.getId());

            //Проверка достижения цели - сущность найдена в HashMap менеджера
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
            printListAllIssues(issueType);
        } else {
            System.out.println(MSG_ERROR_TYPE_UN_KNOW);
        }
    }

    public void testUpdStatusForLastTask(IssueStatus status) {
        Integer id = getIdForLastIssue(IssueType.TASK);
        //id можно указать в качестве входного параметра и проверять на наличие такой задачи, но для ускорения теста
        //использую последнюю задачу, чтобы не тратить время на дополнительный ввод id при проверке
        if (id != null) {
            Task lastTask = getTaskById(id);
            if (lastTask != null) {
                Task newTask = new Task(lastTask.getId(), lastTask.getTitle() + "(обновлена)",
                        lastTask.getDescription(), status);
                updIssue(newTask);
            } else {
                System.out.println(MSG_ERROR_TASK_EMPTY);
            }
        } else {
            System.out.println(MSG_ERROR_TASK_EMPTY);
        }
    }

    public void testUpdStatusForLastSubTask(IssueStatus status) {
        Integer id = getIdForLastIssue(IssueType.SUBTASK);
        if (id != null) {
            SubTask lastSubTask = getSubTaskById(id);
            if (lastSubTask != null) {
                SubTask newSubTask = new SubTask(lastSubTask.getId(), lastSubTask.getTitle() + "(обновлена)",
                        lastSubTask.getDescription(), lastSubTask.getParent(), status);
                updIssue(newSubTask);
            } else {
                System.out.println(MSG_ERROR_TASK_EMPTY);
            }
        } else {
            System.out.println(MSG_ERROR_TASK_EMPTY);
        }
    }

    public void testUpdParentForLastSubTask() {
        Integer id = getIdForLastIssue(IssueType.SUBTASK);
        //Для простоты берем первый эпик и последнюю подзадачу, нам нужно, чтобы эпиков было минимум 2
        List<Epic> listEpics= tracker.getListAllEpics();

        if (listEpics.size() > 1) {
            Integer idNewParent = listEpics.get(0).getId();

            if (id != null && idNewParent != null) {
                SubTask lastSubTask = tracker.getSubTaskById(id);
                Epic firstEpic = tracker.getEpicById(idNewParent);
                if (lastSubTask != null && firstEpic != null) {
                    if (lastSubTask.getParent() != firstEpic) {
                        SubTask newSubTask = new SubTask(lastSubTask.getId(), lastSubTask.getTitle() +
                                "(обновлена)", lastSubTask.getDescription(), firstEpic, lastSubTask.getStatus());
                        updIssue(newSubTask);
                    } else {
                        System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
                    }
                } else {
                    System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
                }
            } else {
                System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
            }
        } else {
            System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
        }
    }

    public void updIssue(Issue issue) {
        boolean goalAchieved = true;
        IssueType issueType = getIssueType(issue);

        printHeadOfTest("void updIssue(Issue issue)",
                "обновить сущность типа " + issueType,
                "по входному id находим сущность в менеджере, сравниваем поля (title,description,status).",
                "Issue getIssueById(IssueType issueType, int idIssue)",
                "если не найдена сущность по входному id или не корректно обновлены поля класса.");

        Issue issueToUpdate = tracker.getIssueById(issueType, issue.getId());
        if (issueToUpdate != null) {

            //Выводим данные по старой задаче
            System.out.println("Найдена сущность с типом "+ issueType + " по id = " +
                                   issueToUpdate.getId() + " : " + issueToUpdate);
            if (issueType == IssueType.SUBTASK) {
                Epic parent = ((SubTask) issueToUpdate).getParent();
                System.out.println("Родитель подзадачи эпик с id = " + parent.getId() + " : " + parent);
                System.out.println("Все подзадачи эпика с id = " + parent.getId() + " : " + parent.getChildrenList());
            } else if (issueType == IssueType.EPIC) {
                System.out.println("Все подзадачи эпика с id = " + issueToUpdate.getId() + " : "
                        + ((Epic) issueToUpdate).getChildrenList());
            }

            //Обновляем задачу, выводим информацию по обновленной задаче по правильному id
            tracker.updIssue(issue);
            System.out.println("\nОбновлена сущность с id = " + issueToUpdate.getId() + " : " + issue);

            // Дополнительная информация
            if (issueType == IssueType.SUBTASK) {
                Epic parent = ((SubTask) issue).getParent();
                System.out.println("Родитель подзадачи эпик с id = " + parent.getId() + " : " + parent);
                System.out.println("Все подзадачи эпика с id = " + parent.getId() + " : " + parent.getChildrenList());
            } else if (issueType == IssueType.EPIC) {
                // Дополнительная информация по эпику
                System.out.println("Все подзадачи эпика с id = " + issueToUpdate.getId() + " : "
                        + ((Epic) issue).getChildrenList());
            }

            Issue updateIssue = tracker.getIssueById(issueType, issue.getId());
            if (!issue.getTitle().equals(updateIssue.getTitle()) ||
                    !issue.getStatus().equals(updateIssue.getStatus()) ||
                       !issue.getDescription().equals(updateIssue.getDescription())) {
                goalAchieved = false;
            }

        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
            goalAchieved = false;
        }

        viewResult(goalAchieved);
        printListAllIssues(issueType);
        if (issueType == IssueType.SUBTASK) {
            System.out.println("ЭПИКИ: " + tracker.getListAllIssues(IssueType.EPIC));
        } else if (issueType == IssueType.EPIC) {
            System.out.println("ПОДЗАДАЧИ: " + tracker.getListAllIssues(IssueType.SUBTASK));
        }
    }

    @Override
    public void updTask(Task task) {

        boolean goalAchieved = true;

        printHeadOfTest("void updTask(Task task)",
                "обновить задачу",
                "по входному id находим задачу в менеджере, сравниваем поля (title,description,status).",
                "Task getTaskById(int id)",
                "если не найдена задача по входному id или не корректно обновлены поля класса.");

        Task taskToUpdate = tracker.getTaskById(task.getId());
        if (taskToUpdate != null) {

            //Выводим данные по старой задаче
            System.out.println("Найдена задача по id = " + taskToUpdate.getId() + " : " + taskToUpdate);

            //Обновляем задачу, выводим информацию по обновленной задаче по правильному id
            tracker.updTask(task);
            System.out.println("\nОбновлена задача с id = " + task.getId() + " : " + task);

            Task updateTask = tracker.getTaskById(task.getId());
            if (!task.getTitle().equals(updateTask.getTitle()) ||
                    !task.getStatus().equals(updateTask.getStatus()) ||
                    !task.getDescription().equals(updateTask.getDescription())) {
                goalAchieved = false;
            }

        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
            goalAchieved = false;
        }

        viewResult(goalAchieved);
        printListAllIssues(IssueType.TASK);
    }

    public void testUpdTaskForLastTask(IssueStatus status) {
        Integer id = getIdForLastIssue(IssueType.TASK);
        if (id != null) {
            Task lastTask = getTaskById(id);
            if (lastTask != null) {
                Task newTask = new Task(lastTask.getId(), lastTask.getTitle() + "(обновлена)",
                        lastTask.getDescription(), status);
                updTask(newTask);
            } else {
                System.out.println(MSG_ERROR_TASK_EMPTY);
            }
        } else {
            System.out.println(MSG_ERROR_TASK_EMPTY);
        }
    }
    @Override
    public void updSubTask(SubTask subTask) {
        boolean goalAchieved = true;

        printHeadOfTest("void updIssue(Issue issue)",
                "обновить подзадачу",
                "по входному id находим подзадачу в менеджере, сравниваем поля " +
                         "(title,description,status, parent).",
                "SubTask getSubTaskById(int id);",
                "если не найдена подзадача по входному id или не корректно обновлены поля класса.");

        SubTask subTaskToUpdate = tracker.getSubTaskById(subTask.getId());
        if (subTaskToUpdate != null) {

            //Выводим данные по старой задаче
            System.out.println("Найдена подзадача по id = " + subTaskToUpdate.getId() + " : " + subTaskToUpdate);

            Epic parent = subTaskToUpdate.getParent();
            System.out.println("Родитель подзадачи эпик с id = " + parent.getId() + " : " + parent);
            System.out.println("Все подзадачи эпика с id = " + parent.getId() + " : " + parent.getChildrenList());


            //Обновляем задачу, выводим информацию по обновленной задаче по правильному id
            tracker.updSubTask(subTask);
            System.out.println("\nОбновлена подзадачу с id = " + subTask.getId() + " : " + subTask);

            // Дополнительная информация
            Epic newParent = subTask.getParent();
            System.out.println("Родитель подзадачи эпик с id = " + newParent.getId() + " : " + newParent);
            System.out.println("Все подзадачи эпика с id = " + newParent.getId() + " : " + newParent.getChildrenList());


            SubTask updateSubTask = tracker.getSubTaskById(subTask.getId());
            if (!subTask.getTitle().equals(updateSubTask.getTitle()) ||
                    !subTask.getStatus().equals(updateSubTask.getStatus()) ||
                    !subTask.getDescription().equals(updateSubTask.getDescription()) ||
                    !subTask.getParent().equals(updateSubTask.getParent()) ) {
                goalAchieved = false;
            }

        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
            goalAchieved = false;
        }

        viewResult(goalAchieved);
        printListAllIssues(IssueType.SUBTASK);
        printListAllIssues(IssueType.EPIC);
    }

    public void testUpdSubTaskForLastSubTask() {
        Integer id = getIdForLastIssue(IssueType.SUBTASK);
        //Для простоты берем первый эпик и последнюю подзадачу, нам нужно, чтобы эпиков было минимум 2
        List<Epic> listEpics= tracker.getListAllEpics();

        if (listEpics.size() > 1) {
            Integer idNewParent = listEpics.get(0).getId();

            if (id != null && idNewParent != null) {
                SubTask lastSubTask = tracker.getSubTaskById(id);
                Epic firstEpic = tracker.getEpicById(idNewParent);
                if (lastSubTask != null && firstEpic != null) {
                    if (lastSubTask.getParent() != firstEpic) {
                        SubTask newSubTask = new SubTask(lastSubTask.getId(), lastSubTask.getTitle() +
                                "(обновлена)", lastSubTask.getDescription(), firstEpic, lastSubTask.getStatus());
                        updSubTask(newSubTask);
                    } else {
                        System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
                    }
                } else {
                    System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
                }
            } else {
                System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
            }
        } else {
            System.out.println(MSG_ERROR_NOT_FOUND_EPIC);
        }
    }

    @Override
    public void updEpic(Epic epic) {
        boolean goalAchieved = true;

        printHeadOfTest("void updEpic(Epic epic)",
                "обновить эпик",
                "по входному id находим эпик в менеджере, сравниваем поля (title,description). " +
                          "Статус как и дети при обновлении меняться не должен.",
                "Epic getEpicById(int id);",
                "если не найден эпик по входному id, не совпали дети, не корректно обновлены поля или" +
                             "поменялся статус.");

        Epic epicToUpdate = tracker.getEpicById(epic.getId());
        if (epicToUpdate != null) {

            //Выводим данные по старой задаче
            System.out.println("Найдена эпик по id = " + epic.getId() + " : " + epicToUpdate);
            System.out.println("Все подзадачи эпика с id = " + epicToUpdate.getId() + " : "
                                                             + epicToUpdate.getChildrenList());

            //Обновляем задачу, выводим информацию по обновленной задаче по правильному id
            tracker.updEpic(epic);
            System.out.println("\nОбновлен эпик с id = " + epic.getId() + " : " + epic);

            // Дополнительная информация
            System.out.println("Все подзадачи эпика с id = " + epic.getId() + " : "
                                                             + epic.getChildrenList());

            Epic updateEpic = tracker.getEpicById(epic.getId());
            if (!epic.getTitle().equals(updateEpic.getTitle()) ||
                    !epic.getStatus().equals(updateEpic.getStatus()) ||
                    !epic.getDescription().equals(updateEpic.getDescription())) {
                goalAchieved = false;
            }
            if (!epicToUpdate.getChildrenList().equals((updateEpic.getChildrenList()))) {
                goalAchieved = false;
            }

        } else {
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
            goalAchieved = false;
        }

        viewResult(goalAchieved);
        printListAllIssues(IssueType.EPIC);
        printListAllIssues(IssueType.SUBTASK);
    }

    /**
     * Метод СЛУЖЕБНЫЙ - обновить эпик (статус рассчитывается по состоянию подзадач)
     * Проверка выполняется в методе updateTask
     */
    public void testUpdStatusForLastEpic() {
        List<Epic> listEpics= tracker.getListAllEpics();
        //для эксперимента есть нужное количество эпиков
        if (listEpics.size() > 1) {
            Epic epicFirst = tracker.getEpicById(tracker.getListAllEpics().get(0).getId());
            Integer idEpicLast = getIdForLastIssue(IssueType.EPIC);
            if (idEpicLast != null) {
                Epic epicLast = tracker.getEpicById(idEpicLast);
                Epic newEpic = new Epic(epicLast.getId(), epicFirst.getTitle() + "(обновлена)",
                        epicFirst.getDescription());
                for (SubTask subTask : epicLast.getChildrenList()) {
                    newEpic.getChildrenList().add(subTask);
                }
                updEpic(newEpic);
            }
        }
    }

    public void delIssueById(IssueType issueType, int idIssue) {
        boolean goalAchieved = true;

        printHeadOfTest("void delIssueById(IssueType issueType, int idIssue)",
                "удалить сущность типа " + issueType,
                "проверить данные в хранилище на наличие удаленной сущностей",
                "Issue getIssueById(IssueType issueType, int idIssue)",
                "тест ошибочный, если удаляемая сущность типа " + issueType + " будет найдена в менеджере.");

        System.out.println("Попытка удаления сущности с Id = " + idIssue + " тип задачи = " + issueType);
        tracker.delIssueById(issueType, idIssue);

        //Проверка достижения цели - задачи в HashMap менеджера нет
        if (tracker.getIssueById(issueType, idIssue) != null) {
            goalAchieved = false;
        } else if (issueType == IssueType.SUBTASK) {
            //Дополнительно для подзадачи - необходимо проверить, что подзадачи нет в детях эпика
            for (Issue issue : tracker.getListAllIssues(IssueType.EPIC)) {
                for (SubTask subTask : tracker.getListSubTaskOfEpic((Epic) issue)) {
                    if (subTask.getId() == idIssue) {
                        goalAchieved = false;
                        break;
                    }
                }
            }
        } else if (issueType == IssueType.EPIC) {
            //Для эпика необходимо проверить, что нет подзадач с таким родителем
            for (Issue issue : tracker.getListAllIssues(IssueType.SUBTASK)) {
                if (((SubTask) issue).getParent().getId() == idIssue) {
                    goalAchieved = false;
                    break;
                }
            }
        }

        viewResult(goalAchieved);
        printListAllIssues(issueType);
    }

    @Override
    public void delTaskById(int id) {
        boolean goalAchieved = true;

        printHeadOfTest("void delTaskById(int id)",
                "удалить задачу",
                "проверить данные в хранилище на наличие удаленной задачи",
                "Issue getIssueById(IssueType issueType, int idIssue), void delAllTasks()",
                "тест ошибочный, если удаляемая задача будет найдена в менеджере.");

        System.out.println("Попытка удаления задачи с Id = " + id);
        tracker.delTaskById(id);

        if (tracker.getIssueById(IssueType.TASK, id) != null) {
            goalAchieved = false;
        }

        viewResult(goalAchieved);
        for (Task listAllTask : tracker.getListAllTasks()) {
            System.out.println(listAllTask);
        }
    }

    @Override
    public void delSubTaskById(int id) {
        boolean goalAchieved = true;

        printHeadOfTest("void delSubTaskById(int id)",
                "удалить подзадачу",
                "проверить данные в хранилище на наличие удаленной подзадачу",
                "Issue getIssueById(IssueType issueType, int idIssue), void getListAllSubTasks()",
                "тест ошибочный, если удаляемая подзадача будет найдена в менеджере.");

        System.out.println("Попытка удаления подзадачи с Id = " + id);
        tracker.delSubTaskById(id);

        if (tracker.getIssueById(IssueType.TASK, id) != null) {
            goalAchieved = false;
        }

        //Дополнительно для подзадачи - необходимо проверить, что подзадачи нет в детях эпика
        for (Epic epic : tracker.getListAllEpics()) {
            for (SubTask subTask : tracker.getListSubTaskOfEpic(epic)) {
                if (subTask.getId() == id) {
                    goalAchieved = false;
                    break;
                }
            }
        }

        viewResult(goalAchieved);
        for (SubTask listST : tracker.getListAllSubTasks()) {
            System.out.println(listST);
        }
        for (Epic listE : tracker.getListAllEpics()) {
            System.out.println(listE);
        }
    }

    @Override
    public void delEpicById(int id) {
        boolean goalAchieved = true;

        printHeadOfTest("void delEpicById(int id)",
                "удалить эпик по id",
                "проверить данные в хранилище на наличие удаляемого эпика",
                "Issue getIssueById(IssueType issueType, int idIssue), void delAllTasks()",
                "тест ошибочный, если удаляемый эпик будет найден в менеджере.");

        System.out.println("Попытка удаления эпика с Id = " + id);
        tracker.delEpicById(id);

        if (tracker.getIssueById(IssueType.TASK, id) != null) {
            goalAchieved = false;
        }

        //Для эпика необходимо проверить, что нет подзадач с таким родителем
        for (SubTask subTask : tracker.getListAllSubTasks()) {
            if (subTask.getParent().getId() == id) {
                goalAchieved = false;
                break;
            }
        }

        viewResult(goalAchieved);
        for (Epic listE : tracker.getListAllEpics()) {
            System.out.println(listE);
        }
        for (SubTask listST : tracker.getListAllSubTasks()) {
            System.out.println(listST);
        }
    }

    /**
     * Метод СЛУЖЕБНЫЙ - удалить последнюю созданную задачу, заданного типа
     *
     * @param type - тип задачи IssueType = {Task, SubTask, Epic}
     */

    public void delLastIssue(IssueType type) {

        Integer id = getIdForLastIssue(type);
        if (id != null) {
            delIssueById(type, id);
        }
    }

    public void delLastTask() {
        Integer id = getIdForLastIssue(IssueType.TASK);
        if (id != null) {
            delTaskById(id);
        }
    }

    public void delLastSubTask() {
        Integer id = getIdForLastIssue(IssueType.SUBTASK);
        if (id != null) {
            delSubTaskById(id);
        }
    }

    public void delLastEpic() {
        Integer id = getIdForLastIssue(IssueType.EPIC);
        if (id != null) {
            delEpicById(id);
        }
    }

    public void delAllIssues(IssueType issueType) {

        boolean goalAchieved = true;

        printHeadOfTest("void delAllIssues(IssueType issueType)",
                "удалить все сущности типа " + issueType,
                "проверить данные в хранилище на наличие удаляемых сущностей",
                "List<Issue> getListAllIssues(IssueType issueType)",
                "тест ошибочный, если список сущностей типом " + issueType + " не пуст.");

        System.out.println("Выполняется удаление сущностей с типом = " + issueType + " ..");
        tracker.delAllIssues(issueType);

        List<Issue> issueListForCheck = tracker.getListAllIssues(issueType);

        //Проверка результата
        if (!issueListForCheck.isEmpty()) {
            //Что-то осталось
            goalAchieved = false;
        } else if (issueType == IssueType.EPIC) {
            //Дополнительная проверка для эпиков. Если все эпики удалены, то и подзадач быть не должно
            List<Issue> subTaskListForCheck = tracker.getListAllIssues(IssueType.SUBTASK);
            if (subTaskListForCheck.size() > 0) {
                goalAchieved = false;
            }
        } else if (issueType == IssueType.SUBTASK) {
            //Дополнительная проверка для подзадач. Если удалены все подзадачи, то эпики должны остаться без детей
            List<Issue> epicListForCheck = tracker.getListAllIssues(IssueType.EPIC);
            for (Issue epic : epicListForCheck) {
                if (((Epic) epic).getChildrenList().size() > 0) {
                    goalAchieved = false;
                    break;
                }
            }
        }

        viewResult(goalAchieved);
        printListAllIssues(issueType);
        if (issueType == IssueType.EPIC) {
            printListAllIssues(IssueType.SUBTASK);
        } else if (issueType == IssueType.SUBTASK) {
            printListAllIssues(IssueType.EPIC);
        }
    }

    @Override
    public void delAllTasks() {
        boolean goalAchieved = true;

        printHeadOfTest("void delAllTasks()",
                "удалить все задачи.",
                "проверить хранилище с задачами в менеджере.",
                "List<Task> getListAllTasks()",
                "тест ошибочный, если остались задачи в менеджере.");

        System.out.println("Выполняется удаление задач ..");
        tracker.delAllTasks();

        List<Task> listForCheck = tracker.getListAllTasks();
        if (!listForCheck.isEmpty()) {
            goalAchieved = false;
        }

        viewResult(goalAchieved);
        System.out.println(listForCheck);
    }

    @Override
    public void delAllSubTasks() {
        boolean goalAchieved = true;

        printHeadOfTest("void delAllSubTasks()",
                "удалить все подзадачи.",
                "проверить хранилище с подзадачами в менеджере. Проверить списки детей эпиков.",
                "List<Task> getListAllSubTasks()",
                "тест ошибочный, если остались подзадачи в менеджере или дети у эпиков.");

        System.out.println("Выполняется удаление подзадач ..");
        tracker.delAllSubTasks();

        //Проверка
        List<SubTask> listForCheck = tracker.getListAllSubTasks();
        if (!listForCheck.isEmpty()) {
            goalAchieved = false;
        }
        List<Epic> epicListForCheck = tracker.getListAllEpics();
        for (Epic epic : epicListForCheck) {
            if (epic.getChildrenList().size() > 0) {
                goalAchieved = false;
                break;
            }
        }

        viewResult(goalAchieved);
        System.out.println("ПОДЗАДАЧИ: " + listForCheck);
        System.out.println("ЭПИКИ: " + epicListForCheck);
    }

    @Override
    public void delAllEpics() {
        boolean goalAchieved = true;

        printHeadOfTest("void delAllEpics()",
                "удалить все эпики.",
                "проверить хранилище эпиков в менеджере.",
                "List<Task> getListAllEpics()",
                "тест ошибочный, если остались эпики или подзадачи в менеджере.");

        System.out.println("Выполняется удаление эпиков ..");
        tracker.delAllEpics();

        List<Epic> listForCheck = tracker.getListAllEpics();
        if (!listForCheck.isEmpty()) {
            goalAchieved = false;
        }
        List<SubTask> subTaskListForCheck = tracker.getListAllSubTasks();
        if (subTaskListForCheck.size() > 0) {
            goalAchieved = false;
        }

        viewResult(goalAchieved);
        System.out.println("ЭПИКИ: " + listForCheck);
        System.out.println("ПОДЗАДАЧИ: " + subTaskListForCheck);
    }

    public List<Issue> getListAllIssues(IssueType issueType) {

        List<Issue> listIssue = tracker.getListAllIssues(issueType);

        printHeadOfTest("List<Issue> getListAllIssues(IssueType issueType)",
                "получить список сущностей типа " + issueType,
                "визуализация полученного списка сущностей.",
                "System.out.print",
                "тест всегда считаем успешным.");

        viewResult(true);
        System.out.println(issueType + ":");
        for (Issue issue : listIssue) {
            System.out.println("\t" + issue);
        }

        return null;
    }

    @Override
    public List<Task> getListAllTasks() {

        List<Task> lists = tracker.getListAllTasks();

        printHeadOfTest("List<Task> getListAllTasks()",
                "получить список всех задач.",
                "визуализация полученного списка задач.",
                "System.out.print",
                "тест всегда считаем успешным.");

        viewResult(true);
        System.out.println("TASK:");
        for (Issue ls : lists) {
            System.out.println("\t" + ls);
        }

        return null;
    }

    @Override
    public List<SubTask> getListAllSubTasks() {

        List<SubTask> lists = tracker.getListAllSubTasks();

        printHeadOfTest("List<SubTask> getListAllSubTasks()",
                "получить список всех подзадач.",
                "визуализация полученного списка подзадач.",
                "System.out.print",
                "тест всегда считаем успешным.");

        viewResult(true);
        System.out.println("SUBTASK:");
        for (Issue ls : lists) {
            System.out.println("\t" + ls);
        }

        return null;
    }

    @Override
    public List<Epic> getListAllEpics() {

        List<Epic> lists = tracker.getListAllEpics();

        printHeadOfTest("List<Epic> getListAllEpics()",
                "получить список всех эпиков.",
                "визуализация полученного списка эпиков.",
                "System.out.print",
                "тест всегда считаем успешным.");

        viewResult(true);
        System.out.println("EPIC:");
        for (Issue ls : lists) {
            System.out.println("\t" + ls);
        }

        return null;
    }

    public void printListAllIssues(IssueType issueType) {

        List<Issue> listIssue = tracker.getListAllIssues(issueType);

        System.out.println(issueType + ":");
        for (Issue issue : listIssue) {
            System.out.println("\t" + issue);
        }
    }

    @Override
    public List<SubTask> getListSubTaskOfEpic(Epic epic) {
        //Данный метод тестируется косвенно в других тестах
        return null;
    }

    @Override
    public Task getTaskById(int id) {
        //Данный метод тестируется косвенно в других тестах
        return null;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        //Данный метод тестируется косвенно в других тестах
        return null;
    }

    @Override
    public Epic getEpicById(int id) {
        //Данный метод тестируется косвенно в других тестах
        return null;
    }

    @Override
    public List<Issue> getHistory() {
        int numberTask = tracker.getListAllIssues(IssueType.TASK).size() + 1;

        printHeadOfTest("List<Issue> getHistory()",
                "проверить историю просмотров.",
                "создадим подряд задачи c просмотром для заполнения очереди просмотров." +
                        " Порядок задач в истории должен встать по возрастанию",
                "void addTask(Task task), Issue getIssueById(IssueType issueType, int idIssue)",
                "тест всегда считаем успешным. Историю просмотров оцениваем на экране.");

        //Создам задачи в количестве SIZE_HISTORY_OF_VIEW_ISSUE_LIST
        for (int i = 0; i < HistoryManager.SIZE_HISTORY_OF_VIEW_ISSUE_LIST + 1; i++) {
            Task newTask = tracker.addTask("Задача " + numberTask, "Описание задачи " + numberTask);
            tracker.addTask(newTask);
            System.out.println("Создана задача с id = " + newTask.getId());
            tracker.getIssueById(IssueType.TASK, newTask.getId());
            ++numberTask;
        }

        viewResult(true);

        //Выводим список просмотров
        System.out.println("История просмотров:");
        for (Issue issue : tracker.getHistory()) {
            System.out.println("\t" + issue);
        }

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
     * <p> - очищает все HashMap менеджера: задачи/подзадачи/эпики
     * <p> - сбрасывает счетчик проводимых тестов (idTest = 1)
     * <p> - сбрасывает результат получения ошибок, для нового теста их нет (commonGoodResultAllTest = true)
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
     * @param methodForTest какой метод тестируем
     * @param purposeMethod назначение метода
     * @param wayForTest    описание способа проверки метода
     * @param usingMethod   перечисление других методов менеджера, используемых для проверки
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
        System.out.println("Условия ошибки в тесте: " + mistakeMethod + "\n");
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
            System.out.println(MSG_ERROR_TYPE_UN_KNOW);
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
