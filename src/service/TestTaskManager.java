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

    private final static String MSG_ERROR_PARENT_NULL = "Родитель подзадачи не может быть null.";
    private final static String MSG_ERROR_TYPE_NULL = "Для метода не указан тип задачи.";
    private final static String MSG_ERROR_TASK_EMPTY = "Список задач пуст.";
    private final static String MSG_ERROR_TYPE_UN_KNOW = "Для выбранного типа задач не создан обработчик в методе.";
    private final static String MSG_ERROR_ID_NULL = "Не найдена задача с указанным id.";

    /**
     * Метод для тестирования сервиса экземпляра класса для реализации интерфейса {@link TaskManager}
     */
    public TestTaskManager() {
        this.idTest = 1;
        this.commonGoodResultAllTest = true;
        this.tracker = Managers.getDefault();
    }

    public void testGetHistory(){
        for (Issue issue : tracker.getHistory()) {
            System.out.println(issue);
        }
    }

    public void printManager() {
        printLine();
        System.out.println("В памяти менеджера:");
        System.out.println(tracker);
    }

    /**
     * ТЕСТ - получить списки задач по типам
     */
    public void testGetListOfAllIssueForType() {
        printLine();
        printTitleTest();
        System.out.println("Цель ТЕСТА: получить списки задач по всем типам");
        System.out.println("Проверяемый метод: getListOfAllIssueForType, используемый метод System.out.print\n");

        //Визуализируем результаты теста
        System.out.print("ЗАДАЧИ:" + tracker.getListAllIssues(IssueType.TASK));
        viewResult(true);
        System.out.print("ЗПИКИ:" + tracker.getListAllIssues(IssueType.EPIC));
        viewResult(true);
        System.out.print("ПОДЗАДАЧИ:" + tracker.getListAllIssues(IssueType.SUBTASK));
        viewResult(true);
    }

    /**
     * ТЕСТ - очистить список задач по типу
     */
    public void testDelListOfAllIssueForType(IssueType type) {

        boolean goalAchieved = true;

        printLine();
        printTitleTest();
        System.out.println("Цель ТЕСТА: удалить все задачи с типом = " + type);
        System.out.println("Проверяемый метод: delAllIssueForType, используется метод для проверки - " +
                "           getListOfAllIssueForType\n");

        System.out.println("Выполняется удаление задач с типом = " + type + " ..");
        tracker.delAllIssues(type);

        List<Issue> issueListForCheck = tracker.getListAllIssues(type);

        //Проверка результата
        if (!issueListForCheck.isEmpty()) {
            //Что-то осталось
            goalAchieved = false;
        } else if (type == IssueType.EPIC) {
            //Дополнительная проверка для эпиков. Если все эпики удалены, то и подзадач быть не должно
            List<Issue> subTaskListForCheck = tracker.getListAllIssues(IssueType.SUBTASK);
            if (subTaskListForCheck.size() > 0) {
                goalAchieved = false;
            }
        } else if (type == IssueType.SUBTASK) {
            //Дополнительная проверка для подзадач. Если удалены все подзадачи, то эпики должны остаться без детей
            List<Issue> epicListForCheck = tracker.getListAllIssues(IssueType.EPIC);
            for (Issue epic : epicListForCheck) {
                if (((Epic) epic).getChildrenList().size() > 0) {
                    goalAchieved = false;
                    break;
                }
            }
        }

        //Вывод результата теста
        System.out.print("Результат теста ");
        viewResult(goalAchieved);
        System.out.println("ВСЕ задачи типа " + type + " : " + tracker.getListAllIssues(type));
        if (type == IssueType.EPIC) {
            System.out.println("ВСЕ задачи типа " + IssueType.SUBTASK + " : "
                    + tracker.getListAllIssues(IssueType.SUBTASK));
        } else if (type == IssueType.SUBTASK) {
            System.out.println("ВСЕ задачи типа " + IssueType.EPIC + " : "
                    + tracker.getListAllIssues(IssueType.EPIC));
        }

        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!goalAchieved) {
            commonGoodResultAllTest = false;
        }
    }

    /**
     * ТЕСТ - создать задачу
     */
    public void testCreateForTask() {

        int numberTask = tracker.getListAllIssues(IssueType.TASK).size() + 1;
        boolean goalAchieved = true;
        String titleTask = "Задача " + numberTask;
        String descriptionTask = "Описание задачи " + numberTask;

        //Выводим цель теста
        printLine();
        printTitleTest();
        System.out.println("Цель ТЕСТА: создать задачу");
        System.out.println("Проверяемый метод: createIssueForType для задач, " +
                "используемый метод для проверки - getIssueByIdForType\n");

        //Тестируем метод
        Task newTask = tracker.addTask(titleTask, descriptionTask, IssueStatus.NEW);
        tracker.addIssue(newTask);
        System.out.println("Создана задача с id = " + newTask.getId());

        //Проверка достижения цели - задача найдена в HashMap менеджера
        Issue issue = tracker.getIssueById(IssueType.TASK, newTask.getId());
        if (issue == null) {
            goalAchieved = false;
        }

        //Вывод результата теста
        System.out.print("Результат теста ");
        viewResult(goalAchieved);
        System.out.println("ЗАДАЧИ: " + tracker.getListAllIssues(IssueType.TASK));

        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!goalAchieved) {
            commonGoodResultAllTest = false;
        }
    }


    /**
     * ТЕСТ - создать эпик с заданным количеством подзадач
     *
     * @param quantitySubTask количество подзадач эпика
     */

    public void testCreateForEpic(int quantitySubTask) {

        int number = tracker.getListAllIssues(IssueType.EPIC).size() + 1;
        String title = "Эпик " + number;
        String description = "Описание эпика " + number;
        Epic newEpic = tracker.addEpic(title, description);

        //Выводим цель теста
        printLine();
        printTitleTest();
        System.out.println("Цель ТЕСТА: создать эпик");
        System.out.println("Проверяемый метод: createIssueForType для эпика, "
                + "используемый метод для проверки - getIssueByIdForType\n");

        //Тестируем метод
        tracker.addIssue(newEpic);
        System.out.println("Создан эпик с id = " + newEpic.getId());

        //Проверка достижения цели - задача найдена в HashMap менеджера
        boolean goalAchieved = true;
        Issue issue = tracker.getIssueById(IssueType.EPIC, newEpic.getId());
        if (issue == null) {
            goalAchieved = false;
        }

        //Вывод результата теста
        System.out.print("Результат теста ");
        viewResult(goalAchieved);
        System.out.println("ЭПИКИ: " + tracker.getListAllIssues(IssueType.EPIC));

        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!goalAchieved) {
            commonGoodResultAllTest = false;
        }

        for (int i = 0; i < quantitySubTask; i++) {
            testCreateForSubTask(newEpic);
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
        System.out.println("Проверяемый метод: createIssueForType для эпика, " +
                "используемый метод для проверки - getIssueByIdForType\n");

        //Тестируем метод
        if (parent != null) {
            int number = tracker.getListAllIssues(IssueType.SUBTASK).size() + 1;
            String title = "Подзадача " + number;
            String description = "Описание подзадачи " + number;

            SubTask newSubTask = tracker.addSubTask(title, description, parent, IssueStatus.NEW);
            tracker.addIssue(newSubTask);
            System.out.println("Создана подзадача с id = " + newSubTask.getId() + " для эпика с id = "
                    + parent.getId());

            //Проверка достижения цели - задача найдена в HashMap менеджера
            if (tracker.getIssueById(IssueType.SUBTASK, newSubTask.getId()) == null) {
                goalAchieved = false;
            }
        } else {
            System.out.println(MSG_ERROR_PARENT_NULL);
            goalAchieved = false;
        }

        //Вывод результата теста
        System.out.print("Результат теста ");
        viewResult(goalAchieved);
        System.out.println("ПОДЗАДАЧИ: " + tracker.getListAllIssues(IssueType.SUBTASK));
        System.out.println("ЭПИКИ: " + tracker.getListAllIssues(IssueType.EPIC));

        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!goalAchieved) {
            commonGoodResultAllTest = false;
        }
    }


    /**
     * Метод СЛУЖЕБНЫЙ - обновить задачу (смена статуса включена)
     * Проверка выполняется в методе updateTask
     */
    public void testUpdStatusForLastTask(IssueStatus status) {

        Integer id = getIdForLastTask(IssueType.TASK);
        if (id != null) {
            testUpdIssueForTypeById(IssueType.TASK, status, id);
        }
    }

    /**
     * Метод СЛУЖЕБНЫЙ - обновить эпик (статус рассчитывается по состоянию подзадач)
     * Проверка выполняется в методе updateTask
     */
    public void testUpdStatusForLastEpic(IssueStatus status) {
        Integer id = getIdForLastTask(IssueType.EPIC);
        if (id != null) {
            testUpdIssueForTypeById(IssueType.EPIC, status, id);
        }
    }

    /**
     * Метод СЛУЖЕБНЫЙ - обновить задачу (смена статуса включена, пересчет статуса родителя)
     * Проверка выполняется в методе updateTask
     */
    public void testUpdStatusForLastSubTask(IssueStatus status) {
        Integer id = getIdForLastTask(IssueType.SUBTASK);
        if (id != null) {
            testUpdIssueForTypeById(IssueType.SUBTASK, status, id);
        }
    }

    /**
     * ТЕСТ - обновить задачу/подзадачу/эпик (смена статуса)
     *
     * @param type   - тип задачи IssueType = {Task, SubTask, Epic}
     * @param status - новый статус задачи
     * @param id     - идентификатор обновляемой задачи
     */
    private void testUpdIssueForTypeById(IssueType type, IssueStatus status, Integer id) {

        if (type == null) {
            return;
        }
        boolean goalAchieved = true;

        //Выводим цель теста
        printLine();
        printTitleTest();
        System.out.println("Цель ТЕСТА: обновить задачу тип = " + type + ". Устанавливаем статус = " + status);
        System.out.println("Проверяемый метод: updIssueForType, " +
                "используемый метод для проверки - getIssueByIdForType\n");

        //Тестируем метод
        if (id != null) {
            Issue issueToUpdate = tracker.getIssueById(type, id);

            if (issueToUpdate != null) {

                //Выводим данные по старой задаче
                System.out.println("Найдена задача по id = " + issueToUpdate.getId() + " : " + issueToUpdate);
                if (type == IssueType.SUBTASK) {
                    Epic parent = ((SubTask) issueToUpdate).getParent();
                    System.out.println("Родитель подзадачи эпик с id = " + parent.getId() + " : " + parent);
                    System.out.println("Все подзадачи эпика с id = " + parent.getId() + " : " + parent.getChildrenList());
                } else if (type == IssueType.EPIC) {
                    System.out.println("Все подзадачи эпика с id = " + issueToUpdate.getId() + " : "
                            + ((Epic) issueToUpdate).getChildrenList());
                }

                //Дополнительным конструктором создаем ее копию, но с другим статусом, также обновим заголовок
                Issue newIssueToUpdate;
                switch (type) {
                    case TASK:
                        newIssueToUpdate = new Task(issueToUpdate.getId(), issueToUpdate.getTitle() + "(обновлена)",
                                issueToUpdate.getDescription(), status);
                        break;
                    case EPIC:
                        newIssueToUpdate = new Epic(issueToUpdate.getId(), issueToUpdate.getTitle() + "(обновлена)",
                                issueToUpdate.getDescription());
                        // Состав подзадач совпадает, поэтому статус обновленного эпика должен сохраниться
                        for (SubTask child : ((Epic) issueToUpdate).getChildrenList()) {
                            ((Epic) newIssueToUpdate).getChildrenList().add(child);
                        }
                        break;
                    case SUBTASK:
                        newIssueToUpdate = new SubTask(issueToUpdate.getId(), issueToUpdate.getTitle() + "(обновлена)",
                                issueToUpdate.getDescription(), ((SubTask) issueToUpdate).getParent(), status);

                        break;
                    default:
                        System.out.println(MSG_ERROR_TYPE_UN_KNOW);
                        goalAchieved = false;
                        newIssueToUpdate = null;
                        break;

                }

                //Если получилось создать копию задачи, то можно переходить к самому тесту updateTask
                if (newIssueToUpdate != null) {
                    //Обновляем задачу, выводим информацию по обновленной задаче по правильному id
                    tracker.updIssue(newIssueToUpdate);
                    Issue newIssue = tracker.getIssueById(type, issueToUpdate.getId());
                    System.out.println("Обновлена задача с id = " + issueToUpdate.getId() + " : "
                            + newIssue);

                    // Дополнительная информация по подзадаче
                    if (type == IssueType.SUBTASK) {
                        Epic parent = ((SubTask) newIssue).getParent();
                        System.out.println("Родитель подзадачи эпик с id = " + parent.getId() + " : " + parent);
                        System.out.println("Все подзадачи эпика с id = " + parent.getId() + " : " + parent.getChildrenList());
                    } else if (type == IssueType.EPIC) {
                        // Дополнительная информация по эпику
                        System.out.println("Все подзадачи эпика с id = " + issueToUpdate.getId() + " : "
                                + ((Epic) newIssue).getChildrenList());
                    }

                    if (type != IssueType.EPIC && newIssueToUpdate.getStatus() == issueToUpdate.getStatus()) {
                        //Статус не изменен, значит обновление не произошло
                        goalAchieved = false;
                    } else if (type == IssueType.EPIC && newIssueToUpdate.getTitle().equals(issueToUpdate.getTitle())) {
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
        System.out.println("Все задачи типа " + type + ": " + tracker.getListAllIssues(type));
        if (type == IssueType.SUBTASK) {
            System.out.println("ЭПИКИ: " + tracker.getListAllIssues(IssueType.EPIC));
        } else if (type == IssueType.EPIC) {
            System.out.println("ПОДЗАДАЧИ: " + tracker.getListAllIssues(IssueType.SUBTASK));
        }

        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!goalAchieved) {
            commonGoodResultAllTest = false;
        }
    }

    /**
     * Метод СЛУЖЕБНЫЙ - удалить последнюю созданную задачу, заданного типа
     *
     * @param type - тип задачи IssueType = {Task, SubTask, Epic}
     */

    public void testDelAllIssueForType(IssueType type) {

        Integer id = getIdForLastTask(type);
        if (id != null) {
            testDelIssueForTypeById(type, id);
        }
    }



    /**
     * ТЕСТ - удалить задачу по идентификатору, заданного типа
     *
     * @param type - тип задачи IssueType = {Task, SubTask, Epic}
     * @param id   - идентификатор задачи
     *
     */

    private void testDelIssueForTypeById(IssueType type, Integer id) {

        boolean goalAchieved = true;

        //Выводим цель теста
        printLine();
        printTitleTest();
        System.out.println("Цель ТЕСТА: удалить задачу тип = " + type);
        System.out.println("Проверяемый метод: delIssueByIdForType, " +
                "используемые методы для проверки - getIssueByIdForType, " +
                "getListOfAllIssueForType, getListSubTaskForEpic\n");

        //Тестируем метод
        if (id != null) {
            System.out.println("Попытка удаления задачи с Id = " + id + " тип задачи = " + type);
            tracker.delIssueById(type, id);

            //Проверка достижения цели - задачи в HashMap менеджера нет
            if (tracker.getIssueById(type, id) != null) {
                goalAchieved = false;
            } else if (type == IssueType.SUBTASK) {
                //Дополнительно для подзадачи - необходимо проверить, что подзадачи нет в детях эпика
                for (Issue issue : tracker.getListAllIssues(IssueType.EPIC)) {
                    for (SubTask subTask : tracker.getListSubTaskOfEpic((Epic) issue)) {
                        if (subTask.getId() == id) {
                            goalAchieved = false;
                            break;
                        }
                    }
                }
            } else if (type == IssueType.EPIC) {
                //Для эпика необходимо проверить, что нет подзадач с таким родителем
                for (Issue issue : tracker.getListAllIssues(IssueType.SUBTASK)) {
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
        System.out.println("ЗАДАЧИ " + type + ": " + tracker.getListAllIssues(type));
        if (type == IssueType.SUBTASK) {
            System.out.println("ЭПИКИ: " + tracker.getListAllIssues(IssueType.EPIC));
        } else if (type == IssueType.EPIC) {
            System.out.println("ПОДЗАДАЧИ: " + tracker.getListAllIssues(IssueType.SUBTASK));
        }

        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!goalAchieved) {
            commonGoodResultAllTest = false;
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
        tracker.restartTaskManager();
        idTest = 1;
        commonGoodResultAllTest = true;
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
    private Integer getIdForLastTask(IssueType issueType) {

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

}
