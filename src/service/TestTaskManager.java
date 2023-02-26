package service;

import model.*;

import java.util.ArrayList;

public class TestTaskManager {
    private final TaskManager tracker;       // тестируемый менеджер
    private int idTest;                      // номер выполняемого теста
    private boolean commonGoodResultAllTest; // результат всех тестов, если ВСЕ тесты прошли успешно, то true

    private final static String msgErrorParentNull = "Родитель подзадачи не может быть null.";
    private final static String msgErrorTypeNull = "Для метода не указан тип задачи.";
    private final static String msgErrorTaskEmpty = "Список задач пуст.";
    private final static String msgErrorTypeUnKnow = "Для выбранного типа задач не создан обработчик в методе.";
    private final static String msgErrorIdNull = "Не найдена задача с указанным id.";

    /**
     * Любой метод данного класса должен запускаться с метода - printTittleTest()
     *   - для ведения хронологии тестов
     * Если при выполнении теста случилась ошибка
     *    - необходимо установить commonGoodResultAllTest = false
     */
    public TestTaskManager(TaskManager taskManager) {
        this.tracker = taskManager;
        this.idTest = 1;
        this.commonGoodResultAllTest = true;
    }

    /**
     * ТЕСТ - получить списки задач по типам
     */
    public void printTask() {
        printLine();
        printTittleTest();
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
    public void deleteAllTask(IssueType type) {
        printLine();
        printTittleTest();
        System.out.println("Цель ТЕСТА: удалить все задачи с типом = " + type);
        System.out.println("Проверяемый метод: deleteAllTask, используется метод для проверки - getAllTask\n");

        System.out.println("Выполняется удаление задач с типом = " + type +" ..");
        tracker.deleteAllTask(type);

        //Простая достижения цели теста
        boolean goalAchieved    = true;
        ArrayList<Issue> tasksForCheck = tracker.getAllTask(type);

        if (!tasksForCheck.isEmpty()) {
            //Что-то осталось
            goalAchieved = false;
        }

        ArrayList<Issue> issues = tracker.getAllTask(IssueType.SUBTASK);
        //Дополнительная проверка для эпиков. Если все эпики удалены, то и подзадач быть не должно
        if (goalAchieved && type == IssueType.EPIC && issues.size() > 0) {
            goalAchieved = false;
        }

        //Дополнительная проверка для подзадач
        if (goalAchieved && type == IssueType.SUBTASK) {
            //Если удалены все подзадачи, то эпики должны остаться без детей
            ArrayList<Issue> epics = tracker.getAllTask(IssueType.EPIC);
            for (Issue epic : epics) {
                if (((Epic) epic).getChildren().size()>0) {
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
        }
        if (type == IssueType.SUBTASK) {
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
    public void createTask() {
        //Выводим цель теста
        printLine();
        printTittleTest();
        System.out.println("Цель ТЕСТА: создать задачу");
        System.out.println("Проверяемый метод: createTask для задач, " +
                            "используемый метод для проверки - getTaskForId\n");

        //Тестируем метод
        int numberTask = tracker.getAllTask(IssueType.TASK).size()+1;
        String tittleTask = "Задача " + numberTask;
        String descriptionTask = "Описание задачи " + numberTask;
        String printTask = "Создана задача с id = ";

        Task newTask = new Task(tracker.newId(),tittleTask,descriptionTask);
        tracker.createTask(IssueType.TASK, newTask);
        System.out.println(printTask + newTask.getId());

        //Проверка достижения цели - задача найдена в HashMap менеджера
        boolean goalAchieved = true;
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
     * ТЕСТ - создать эпик
     */
    public void createEpic(int quantitySubTask) {
        //Выводим цель теста
        printLine();
        printTittleTest();
        System.out.println("Цель ТЕСТА: создать эпик");
        System.out.println("Проверяемый метод: createTask для эпика, " +
                            "используемый метод для проверки - getTaskForId\n");

        //Тестируем метод
        int numberTask = tracker.getAllTask(IssueType.EPIC).size()+1;
        String tittleTask = "Эпик " + numberTask;
        String descriptionTask = "Описание эпика " + numberTask;
        String printTask = "Создан эпик с id = ";

        Epic newTask = new Epic(tracker.newId(),tittleTask,descriptionTask);
        tracker.createTask(IssueType.EPIC, newTask);
        System.out.println(printTask + newTask.getId());

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
            createSubTask(newTask);
        }

    }

    /**
     * ТЕСТ - создать подзадачу
     */
    private void createSubTask(Epic parent) {

        //Выводим цель теста
        printLine();
        printTittleTest();
        System.out.println("Цель ТЕСТА: создать подзадачу для эпика");
        System.out.println("Проверяемый метод: createTask для эпика, " +
                           "используемый метод для проверки - getTaskForId\n");

        //Тестируем метод
        boolean goalAchieved = true;

        if (parent!= null) {
            int numberTask = tracker.getAllTask(IssueType.SUBTASK).size() + 1;
            String tittleTask = "Подзадача " + numberTask;
            String descriptionTask = "Описание подзадачи " + numberTask;
            String printTask = "Создана подзадача с id = ";

            SubTask newTask = new SubTask(tracker.newId(),tittleTask, descriptionTask, parent);
            tracker.createTask(IssueType.SUBTASK, newTask);
            System.out.println(printTask + newTask.getId() + " для эпика с id = " + parent.getId());

            //Проверка достижения цели - задача найдена в HashMap менеджера
            if (tracker.getTaskForId(IssueType.SUBTASK, newTask.getId()) == null) {
                goalAchieved = false;
            }
        } else {
            System.out.println(msgErrorParentNull);
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
            updateTask(IssueType.TASK, status, id);
        }
    }

    /**
     * Метод СЛУЖЕБНЫЙ - обновить эпик (статус рассчитывается по состоянию подзадач)
     * Проверка выполняется в методе updateTask
     */
    public void lastEpicUpdateStatus(IssueStatus status) {
        Integer id = getLastIdTask(IssueType.EPIC);
        if (id != null) {
            updateTask(IssueType.EPIC, status, id);
        }
    }

    /**
     * Метод СЛУЖЕБНЫЙ - обновить задачу (смена статуса включена, пересчет статуса родителя)
     * Проверка выполняется в методе updateTask
     */
    public void lastSubTaskUpdateStatus(IssueStatus status) {
        Integer id = getLastIdTask(IssueType.SUBTASK);
        if (id != null) {
            updateTask(IssueType.SUBTASK, status, id);
        }
    }

    /**
     * ТЕСТ - обновить задачу/подзадачу/эпик (смена статуса)
     * @param type   - тип задачи IssueType = {Task, SubTask, Epic}
     * @param status - новый статус задачи
     * @param id     - идентификатор обновляемой задачи
     */
    private void updateTask(IssueType type, IssueStatus status, Integer id) {

        //Выводим цель теста
        printLine();
        printTittleTest();
        System.out.println("Цель ТЕСТА: обновить задачу тип = " + type + ". Устанавливаем статус = " + status);
        System.out.println("Проверяемый метод: updateTask, " +
                            "используемый метод для проверки - getTaskForId\n");

        boolean goalAchieved = true;

        //Тестируем метод
        if (id != null && type != null) {
            Issue taskToUpdate = tracker.getTaskForId(type,id);
            System.out.println("Найдена задача по id = " + taskToUpdate.getId() + " : " + taskToUpdate);
            if (type == IssueType.SUBTASK) {
                Epic parent = ((SubTask) taskToUpdate).getParent();
                System.out.println("Родитель подзадачи эпик с id = " + parent.getId() + " : " + parent);
                System.out.println("Все подзадачи эпика с id = " + parent.getId() + " : " + parent.getChildren());
            }
            if (type == IssueType.EPIC) {
                System.out.println("Все подзадачи эпика с id = " + taskToUpdate.getId() + " : "
                                   + ((Epic) taskToUpdate).getChildren());
            }

            //Дополнительным конструктором создаем ее копию, но с другим статусом, также обновим заголовок
            Issue newTaskToUpdate;
            switch (type) {
                case TASK:
                    newTaskToUpdate = new Task(taskToUpdate.getId(), taskToUpdate.getTittle() +"(обновлена)",
                                               taskToUpdate.getDescription(), status);
                    break;
                case EPIC:
                    newTaskToUpdate = new Epic(taskToUpdate.getId(), taskToUpdate.getTittle() +"(обновлена)",
                                               taskToUpdate.getDescription());
                    // Состав подзадач совпадает, поэтому статус обновленного эпика должен сохраниться
                    for (SubTask child : ((Epic) taskToUpdate).getChildren()) {
                        ((Epic) newTaskToUpdate).addChild(child);
                    }
                    break;
                case SUBTASK:
                    newTaskToUpdate = new SubTask(taskToUpdate.getId(), taskToUpdate.getTittle() +"(обновлена)",
                                          taskToUpdate.getDescription(), ((SubTask) taskToUpdate).getParent(), status);

                    break;
                default:
                    System.out.println(msgErrorTypeUnKnow);
                    goalAchieved    = false;
                    newTaskToUpdate = null;
                    break;
            }

            //Если получилось создать копию задачи, то можно переходить к самому тесту updateTask
            if (goalAchieved) {
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
                }
                // Дополнительная информация по эпику
                if (type == IssueType.EPIC) {
                    System.out.println("Все подзадачи эпика с id = " + taskToUpdate.getId() + " : "
                            + ((Epic) newIssue).getChildren());
                }
            }

        } else {
            System.out.println(msgErrorIdNull);
            goalAchieved = false;
        }

        //Вывод результата теста
        System.out.print("Результат теста ");
        viewResult(goalAchieved);
        System.out.println("Все задачи типа " + type +": "+ tracker.getAllTask(type));
        if (type == IssueType.SUBTASK) {
            System.out.println("ЭПИКИ: " + tracker.getAllTask(IssueType.EPIC));
        }
        if (type == IssueType.EPIC) {
            System.out.println("ПОДЗАДАЧИ: " + tracker.getAllTask(IssueType.SUBTASK));
        }

        //Если текущий тест дал ошибку, то считаем весь тест испорченным
        if (!goalAchieved) {
            this.commonGoodResultAllTest = false;
        }
    }

    /**
     * Метод СЛУЖЕБНЫЙ - удалить последнюю созданную задачу, заданного типа
     * @param type   - тип задачи IssueType = {Task, SubTask, Epic}
     */
    public void lastTaskToDelete(IssueType type) {

        Integer id = getLastIdTask(type);
        if (id != null) {
            deleteTask(type, id);
        }
    }

    /**
     * ТЕСТ - удалить задачу по идентификатору, заданного типа
     * @param type - тип задачи IssueType = {Task, SubTask, Epic}
     * @param id   - идентификатор задачи
     */
    private void deleteTask(IssueType type, Integer id) {

        //Выводим цель теста
        printLine();
        printTittleTest();
        System.out.println("Цель ТЕСТА: удалить задачу тип = " + type);
        System.out.println("Проверяемый метод: deleteTask, " +
                           "используемые методы для проверки - getTaskForId, getAllTask, getSubTaskForEpic\n");

        boolean goalAchieved = true;

        //Тестируем метод
        if (id != null) {
            System.out.println("Попытка удаления задачи с Id = " + id + " тип задачи = " + type);
            tracker.deleteTask(type, id);

            //Проверка достижения цели - задачи в HashMap менеджера нет
            if (tracker.getTaskForId(type, id) != null) {
                goalAchieved = false;
            }

            //Дополнительно для подзадачи - необходимо проверить, что подзадачи нет в детях эпика
            if (type == IssueType.SUBTASK) {
                for (Issue issue : tracker.getAllTask(IssueType.EPIC)) {
                    for (SubTask subTask : tracker.getSubTaskForEpic((Epic) issue)) {
                        if (subTask.getId() == id) {
                            goalAchieved = false;
                            break;
                        }
                    }
                }
            }

            //Для эпика необходимо проверить, что нет подзадач с таким родителем
            if (type == IssueType.EPIC) {
                for (Issue issue : tracker.getAllTask(IssueType.SUBTASK)) {
                    if (((SubTask)issue).getParent().getId() == id) {
                        goalAchieved = false;
                        break;
                    }
                }
            }

        } else {
            System.out.println(msgErrorIdNull);
            goalAchieved = false;
        }

        //Вывод результата теста
        System.out.print("Результат теста ");
        viewResult(goalAchieved);
        System.out.println("ЗАДАЧИ " + type +": "+ tracker.getAllTask(type));
        if (type == IssueType.SUBTASK) {
            System.out.println("ЭПИКИ: " + tracker.getAllTask(IssueType.EPIC));
        }
        if (type == IssueType.EPIC) {
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
     *   - очищает все HashMap менеджера: задачи/подзадачи/эпики
     *   - id = 1
     *   - commonGoodResultAllTest = true
     */
    public void restartTest() {
        this.tracker.restartTaskManager();
        this.idTest = 1;
        this.commonGoodResultAllTest = true;
    }

    /**
     * Метод СЛУЖЕБНЫЙ - для вывода номера выполняемого теста:
     *   - готовит номер для следующего теста
     */
    private void printTittleTest() {
        System.out.println("ТECT №" + this.idTest);
        ++this.idTest;
    }

    /**
     * Метод СЛУЖЕБНЫЙ - для вывода результатов теста:
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
     * @param issueType - тип задачи IssueType = {Task, SubTask, Epic}
     * @return - id последней созданной задачи
     */
    private Integer getLastIdTask(IssueType issueType) {

        if (issueType != null) {
            ArrayList<Issue> issues = tracker.getAllTask(issueType);
            if (!issues.isEmpty()) {
                return issues.get(issues.size() - 1).getId();
            } else {
                System.out.println(msgErrorTaskEmpty);
            }
        } else {
            System.out.println(msgErrorTypeNull);
        }
        return null;
    }

}
