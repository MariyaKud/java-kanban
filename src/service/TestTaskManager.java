package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;

import java.util.List;

public class TestTaskManager {

    /**
     tracker тестируемый менеджер {@link TaskManager}
     */
    private final TaskManager tracker;       // тестируемый менеджер

    /**
     * idTest идентификатор проводимого теста
     */
    private int idTest;                      // номер выполняемого теста

    /**
     *  Если при выполнении очередного теста случилась ошибка, то
     *  необходимо установить commonGoodResultAllTest = false
     */
    private boolean commonGoodResultAllTest; // результат всех тестов, если ВСЕ тесты прошли успешно, то true

    private final static String MSG_ERROR_PARENT_NULL = "Родитель подзадачи не может быть null.";
    private final static String MSG_ERROR_TYPE_NULL = "Для метода не указан тип задачи.";
    private final static String MSG_ERROR_TASK_EMPTY = "Список задач пуст.";
    private final static String MSG_ERROR_TYPE_UNKNOW = "Для выбранного типа задач не создан обработчик в методе.";
    private final static String MSG_ERROR_ID_NULL = "Не найдена задача с указанным id.";

    /**
     * Метод для тестирования сервиса {@link TaskManager}
     */
    public TestTaskManager(TaskManager taskManager) {
        this.tracker = taskManager;
        this.idTest = 1;
        this.commonGoodResultAllTest = true;
    }

    /**
     * ТЕСТ - получить списки задач по типам
     */
    public void testGetAllTaskAndViewResult() {
        printLine();
        printTitleTest();
        System.out.println("Цель ТЕСТА: получить списки задач по всем типам");
        System.out.println("Проверяемый метод: getAllTask, используемый метод System.out.print\n");

        //Визуализируем результаты теста
        System.out.print("ЗАДАЧИ:" + tracker.getAllTask(IssueType.TASK));
        viewResult(true);
        System.out.print("ЗПИКИ:" + tracker.getAllTask(IssueType.EPIC));
        viewResult(true);
        System.out.print("ПОДЗАДАЧИ:" + tracker.getAllTask(IssueType.SUBTASK));
        viewResult(true);
    }

    /**
     * ТЕСТ - очистить список задач по типу
     */
    public void testDeleteAllTaskForType(IssueType type) {

        boolean goalAchieved = true;

        printLine();
        printTitleTest();
        System.out.println("Цель ТЕСТА: удалить все задачи с типом = " + type);
        System.out.println("Проверяемый метод: deleteAllTask, используется метод для проверки - getAllTask\n");

        System.out.println("Выполняется удаление задач с типом = " + type + " ..");
        tracker.deleteAllTask(type);

        List<Issue> tasksForCheck = tracker.getAllTask(type);

        //Проверка результата
        if (!tasksForCheck.isEmpty()) {
            //Что-то осталось
            goalAchieved = false;
        } else if (type == IssueType.EPIC) {
            //Дополнительная проверка для эпиков. Если все эпики удалены, то и подзадач быть не должно
            List<Issue> issues = tracker.getAllTask(IssueType.SUBTASK);
            if (issues.size() > 0) {
                goalAchieved = false;
            }
        } else if (type == IssueType.SUBTASK) {
            //Дополнительная проверка для подзадач. Если удалены все подзадачи, то эпики должны остаться без детей
            List<Issue> epics = tracker.getAllTask(IssueType.EPIC);
            for (Issue epic : epics) {
                if (((Epic) epic).getChildren().size() > 0) {
                    goalAchieved = false;
                    break;
                }
            }
        }

        //Вывод результата теста
        System.out.print("Результат теста ");
        viewResult(goalAchieved);
        System.out.println("ВСЕ задачи типа " + type + " : " + tracker.getAllTask(type));
        if (type == IssueType.EPIC) {
            System.out.println("ВСЕ задачи типа " + IssueType.SUBTASK + " : " + tracker.getAllTask(IssueType.SUBTASK));
        } else if (type == IssueType.SUBTASK) {
            System.out.println("ВСЕ задачи типа " + IssueType.EPIC + " : " + tracker.getAllTask(IssueType.EPIC));
        }

        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!goalAchieved) {
            this.commonGoodResultAllTest = false;
        }
    }

    /**
     * ТЕСТ - создать задачу
     */
    public void testCreateForTask() {

        int numberTask = tracker.getAllTask(IssueType.TASK).size() + 1;
        boolean goalAchieved = true;
        String titleTask = "Задача " + numberTask;
        String descriptionTask = "Описание задачи " + numberTask;

        //Выводим цель теста
        printLine();
        printTitleTest();
        System.out.println("Цель ТЕСТА: создать задачу");
        System.out.println("Проверяемый метод: createTask для задач, " +
                "используемый метод для проверки - getTaskForId\n");

        //Тестируем метод
        Task newTask = tracker.initTask(titleTask, descriptionTask, IssueStatus.NEW);
        tracker.createTask(IssueType.TASK, newTask);
        System.out.println("Создана задача с id = " + newTask.getId());

        //Проверка достижения цели - задача найдена в HashMap менеджера
        Issue issue = tracker.getTaskForId(IssueType.TASK, newTask.getId());
        if (issue == null) {
            goalAchieved = false;
        }

        //Вывод результата теста
        System.out.print("Результат теста ");
        viewResult(goalAchieved);
        System.out.println("ЗАДАЧИ: " + tracker.getAllTask(IssueType.TASK));

        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!goalAchieved) {
            this.commonGoodResultAllTest = false;
        }
    }

    /**
     * ТЕСТ - создать эпик с заданным количеством подзадач
     */
    public void testCreateForEpic(int quantitySubTask) {

        int numberTask = tracker.getAllTask(IssueType.EPIC).size() + 1;
        String titleTask = "Эпик " + numberTask;
        String descriptionTask = "Описание эпика " + numberTask;
        Epic newTask = tracker.initEpic(titleTask, descriptionTask);

        //Выводим цель теста
        printLine();
        printTitleTest();
        System.out.println("Цель ТЕСТА: создать эпик");
        System.out.println("Проверяемый метод: createTask для эпика, " +
                "используемый метод для проверки - getTaskForId\n");

        //Тестируем метод
        tracker.createTask(IssueType.EPIC, newTask);
        System.out.println("Создан эпик с id = " + newTask.getId());

        //Проверка достижения цели - задача найдена в HashMap менеджера
        boolean goalAchieved = true;
        Issue issue = tracker.getTaskForId(IssueType.EPIC, newTask.getId());
        if (issue == null) {
            goalAchieved = false;
        }

        //Вывод результата теста
        System.out.print("Результат теста ");
        viewResult(goalAchieved);
        System.out.println("ЭПИКИ: " + tracker.getAllTask(IssueType.EPIC));

        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!goalAchieved) {
            this.commonGoodResultAllTest = false;
        }

        for (int i = 0; i < quantitySubTask; i++) {
            testCreateForSubTask(newTask);
        }
    }

    /**
     * ТЕСТ - создать подзадачу
     *
     * @param parent эпик (родитель подзадачи)
     */
    private void testCreateForSubTask(Epic parent) {

        boolean goalAchieved = true;

        //Выводим цель теста
        printLine();
        printTitleTest();
        System.out.println("Цель ТЕСТА: создать подзадачу для эпика");
        System.out.println("Проверяемый метод: createTask для эпика, " +
                "используемый метод для проверки - getTaskForId\n");

        //Тестируем метод
        if (parent != null) {
            int numberTask = tracker.getAllTask(IssueType.SUBTASK).size() + 1;
            String titleTask = "Подзадача " + numberTask;
            String descriptionTask = "Описание подзадачи " + numberTask;
            String printTask = "Создана подзадача с id = ";

            SubTask newTask = tracker.initSubTask(titleTask, descriptionTask, parent, IssueStatus.NEW);
            tracker.createTask(IssueType.SUBTASK, newTask);
            System.out.println(printTask + newTask.getId() + " для эпика с id = " + parent.getId());

            //Проверка достижения цели - задача найдена в HashMap менеджера
            if (tracker.getTaskForId(IssueType.SUBTASK, newTask.getId()) == null) {
                goalAchieved = false;
            }
        } else {
            System.out.println(MSG_ERROR_PARENT_NULL);
            goalAchieved = false;
        }

        //Вывод результата теста
        System.out.print("Результат теста ");
        viewResult(goalAchieved);
        System.out.println("ПОДЗАДАЧИ: " + tracker.getAllTask(IssueType.SUBTASK));
        System.out.println("ЭПИКИ: " + tracker.getAllTask(IssueType.EPIC));

        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!goalAchieved) {
            this.commonGoodResultAllTest = false;
        }
    }

    /**
     * Метод СЛУЖЕБНЫЙ - обновить задачу (смена статуса включена)
     * Проверка выполняется в методе updateTask
     */
    public void lastTaskUpdateStatus(IssueStatus status) {

        Integer id = getLastIdTask(IssueType.TASK);
        if (id != null) {
            testUpdateTaskForChangeStatus(IssueType.TASK, status, id);
        }
    }

    /**
     * Метод СЛУЖЕБНЫЙ - обновить эпик (статус рассчитывается по состоянию подзадач)
     * Проверка выполняется в методе updateTask
     */
    public void lastEpicUpdateStatus(IssueStatus status) {
        Integer id = getLastIdTask(IssueType.EPIC);
        if (id != null) {
            testUpdateTaskForChangeStatus(IssueType.EPIC, status, id);
        }
    }

    /**
     * Метод СЛУЖЕБНЫЙ - обновить задачу (смена статуса включена, пересчет статуса родителя)
     * Проверка выполняется в методе updateTask
     */
    public void lastSubTaskUpdateStatus(IssueStatus status) {
        Integer id = getLastIdTask(IssueType.SUBTASK);
        if (id != null) {
            testUpdateTaskForChangeStatus(IssueType.SUBTASK, status, id);
        }
    }

    /**
     * ТЕСТ - обновить задачу/подзадачу/эпик (смена статуса)
     *
     * @param type   - тип задачи IssueType = {Task, SubTask, Epic}
     * @param status - новый статус задачи
     * @param id     - идентификатор обновляемой задачи
     */
    private void testUpdateTaskForChangeStatus(IssueType type, IssueStatus status, Integer id) {

        if (type == null) {
            return;
        }
        boolean goalAchieved = true;

        //Выводим цель теста
        printLine();
        printTitleTest();
        System.out.println("Цель ТЕСТА: обновить задачу тип = " + type + ". Устанавливаем статус = " + status);
        System.out.println("Проверяемый метод: updateTask, " +
                "используемый метод для проверки - getTaskForId\n");

        //Тестируем метод
        if (id != null) {
            Issue taskToUpdate = tracker.getTaskForId(type, id);

            if (taskToUpdate != null) {

                //Выводим данные по старой задаче
                System.out.println("Найдена задача по id = " + taskToUpdate.getId() + " : " + taskToUpdate);
                if (type == IssueType.SUBTASK) {
                    Epic parent = ((SubTask) taskToUpdate).getParent();
                    System.out.println("Родитель подзадачи эпик с id = " + parent.getId() + " : " + parent);
                    System.out.println("Все подзадачи эпика с id = " + parent.getId() + " : " + parent.getChildren());
                } else if (type == IssueType.EPIC) {
                    System.out.println("Все подзадачи эпика с id = " + taskToUpdate.getId() + " : "
                            + ((Epic) taskToUpdate).getChildren());
                }

                //Дополнительным конструктором создаем ее копию, но с другим статусом, также обновим заголовок
                Issue newTaskToUpdate;
                switch (type) {
                    case TASK:
                        newTaskToUpdate = new Task(taskToUpdate.getId(), taskToUpdate.getTitle() + "(обновлена)",
                                taskToUpdate.getDescription(), status);
                        break;
                    case EPIC:
                        newTaskToUpdate = new Epic(taskToUpdate.getId(), taskToUpdate.getTitle() + "(обновлена)",
                                taskToUpdate.getDescription());
                        // Состав подзадач совпадает, поэтому статус обновленного эпика должен сохраниться
                        for (SubTask child : ((Epic) taskToUpdate).getChildren()) {
                            ((Epic) newTaskToUpdate).getChildren().add(child);
                        }
                        break;
                    case SUBTASK:
                        newTaskToUpdate = new SubTask(taskToUpdate.getId(), taskToUpdate.getTitle() + "(обновлена)",
                                taskToUpdate.getDescription(), ((SubTask) taskToUpdate).getParent(), status);

                        break;
                    default:
                        System.out.println(MSG_ERROR_TYPE_UNKNOW);
                        goalAchieved = false;
                        newTaskToUpdate = null;
                        break;

                }

                //Если получилось создать копию задачи, то можно переходить к самому тесту updateTask
                if (newTaskToUpdate != null) {
                    //Обновляем задачу, выводим информацию по обновленной задаче по правильному id
                    tracker.updateTask(type, newTaskToUpdate);
                    Issue newIssue = tracker.getTaskForId(type, taskToUpdate.getId());
                    System.out.println("Обновлена задача с id = " + taskToUpdate.getId() + " : "
                            + newIssue);

                    // Дополнительная информация по подзадаче
                    if (type == IssueType.SUBTASK) {
                        Epic parent = ((SubTask) newIssue).getParent();
                        System.out.println("Родитель подзадачи эпик с id = " + parent.getId() + " : " + parent);
                        System.out.println("Все подзадачи эпика с id = " + parent.getId() + " : " + parent.getChildren());
                    } else if (type == IssueType.EPIC) {
                        // Дополнительная информация по эпику
                        System.out.println("Все подзадачи эпика с id = " + taskToUpdate.getId() + " : "
                                + ((Epic) newIssue).getChildren());
                    }

                    if (type != IssueType.EPIC && newTaskToUpdate.getStatus() == taskToUpdate.getStatus()) {
                        //Статус не изменен, значит обновление не произошло
                        goalAchieved = false;
                    } else if (type == IssueType.EPIC && newTaskToUpdate.getTitle().equals(taskToUpdate.getTitle())) {
                        //Для эпика проверяем, что изменился заголовок
                        goalAchieved = false;
                    }
                }
            } else {
                System.out.println(MSG_ERROR_ID_NULL);
                goalAchieved = false;
            }
        } else {
            System.out.println(MSG_ERROR_ID_NULL);
            goalAchieved = false;
        }

        //Вывод результата теста
        System.out.print("Результат теста ");
        viewResult(goalAchieved);
        System.out.println("Все задачи типа " + type + ": " + tracker.getAllTask(type));
        if (type == IssueType.SUBTASK) {
            System.out.println("ЭПИКИ: " + tracker.getAllTask(IssueType.EPIC));
        } else if (type == IssueType.EPIC) {
            System.out.println("ПОДЗАДАЧИ: " + tracker.getAllTask(IssueType.SUBTASK));
        }

        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!goalAchieved) {
            this.commonGoodResultAllTest = false;
        }
    }

    /**
     * Метод СЛУЖЕБНЫЙ - удалить последнюю созданную задачу, заданного типа
     *
     * @param type - тип задачи IssueType = {Task, SubTask, Epic}
     */
    public void lastTaskToDelete(IssueType type) {

        Integer id = getLastIdTask(type);
        if (id != null) {
            testDeleteTaskForId(type, id);
        }
    }

    /**
     * ТЕСТ - удалить задачу по идентификатору, заданного типа
     *
     * @param type - тип задачи IssueType = {Task, SubTask, Epic}
     * @param id   - идентификатор задачи
     */
    private void testDeleteTaskForId(IssueType type, Integer id) {

        boolean goalAchieved = true;

        //Выводим цель теста
        printLine();
        printTitleTest();
        System.out.println("Цель ТЕСТА: удалить задачу тип = " + type);
        System.out.println("Проверяемый метод: deleteTask, " +
                "используемые методы для проверки - getTaskForId, getAllTask, getSubTaskForEpic\n");

        //Тестируем метод
        if (id != null) {
            System.out.println("Попытка удаления задачи с Id = " + id + " тип задачи = " + type);
            tracker.deleteTask(type, id);

            //Проверка достижения цели - задачи в HashMap менеджера нет
            if (tracker.getTaskForId(type, id) != null) {
                goalAchieved = false;
            } else if (type == IssueType.SUBTASK) {
                //Дополнительно для подзадачи - необходимо проверить, что подзадачи нет в детях эпика
                for (Issue issue : tracker.getAllTask(IssueType.EPIC)) {
                    for (SubTask subTask : tracker.getSubTaskForEpic((Epic) issue)) {
                        if (subTask.getId() == id) {
                            goalAchieved = false;
                            break;
                        }
                    }
                }
            } else if (type == IssueType.EPIC) {
                //Для эпика необходимо проверить, что нет подзадач с таким родителем
                for (Issue issue : tracker.getAllTask(IssueType.SUBTASK)) {
                    if (((SubTask) issue).getParent().getId() == id) {
                        goalAchieved = false;
                        break;
                    }
                }
            }

        } else {
            System.out.println(MSG_ERROR_ID_NULL);
            goalAchieved = false;
        }

        //Вывод результата теста
        System.out.print("Результат теста ");
        viewResult(goalAchieved);
        System.out.println("ЗАДАЧИ " + type + ": " + tracker.getAllTask(type));
        if (type == IssueType.SUBTASK) {
            System.out.println("ЭПИКИ: " + tracker.getAllTask(IssueType.EPIC));
        } else if (type == IssueType.EPIC) {
            System.out.println("ПОДЗАДАЧИ: " + tracker.getAllTask(IssueType.SUBTASK));
        }

        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!goalAchieved) {
            this.commonGoodResultAllTest = false;
        }
    }

    public void printLine() {
        System.out.println("-------------------------------------------");
    }

    public boolean isCommonGoodResultAllTest() {
        return commonGoodResultAllTest;
    }

    /**
     * Метод СЛУЖЕБНЫЙ - для перезапуска теста:
     * - очищает все HashMap менеджера: задачи/подзадачи/эпики
     * - id = 1
     * - commonGoodResultAllTest = true
     */
    public void restartTest() {
        this.tracker.restartTaskManager();
        this.idTest = 1;
        this.commonGoodResultAllTest = true;
    }

    /**
     * Метод СЛУЖЕБНЫЙ - для вывода номера выполняемого теста:
     * - готовит номер для следующего теста
     */
    private void printTitleTest() {
        System.out.println("ТECT №" + this.idTest);
        ++this.idTest;
    }

    /**
     * Метод СЛУЖЕБНЫЙ - для вывода результатов теста:
     *
     * @param resultGood - результат True (достигнут) / False (что-то пошло не так)
     */
    public void viewResult(boolean resultGood) {
        if (resultGood) {
            System.out.println(" ✅");
        } else {
            System.out.println(" ❌");
        }
    }

    /**
     * Метод СЛУЖЕБНЫЙ - для получения последней задачи менеджера заданного типа
     *
     * @param issueType - тип задачи IssueType = {Task, SubTask, Epic}
     * @return - id последней созданной задачи
     */
    private Integer getLastIdTask(IssueType issueType) {

        if (issueType != null) {
            List<Issue> issues = tracker.getAllTask(issueType);
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
}
