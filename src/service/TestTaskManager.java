package service;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * СЛУЖЕБНЫЙ КЛАСС для отладки менеджера задач класса {@link InMemoryTaskManager}
 */
public class TestTaskManager {

    //Текст сообщений об ошибках
    String MSG_ERROR_TYPE_UN_KNOW = "Для выбранного типа задач не создан обработчик в методе.";
    String MSG_ERROR_ID_NOT_FOUND = "Не найдена сущность с указанным id.";
    String MSG_ERROR_TASK_EMPTY = "Список в менеджере пуст.";
    String MSG_ERROR_NOT_FOUND_EPIC = "Эпик не найден.";
    String MSG_ERROR_FOR_METHOD = "При тестировании метода обнаружена ошибка.";
    String MSG_ERROR_TEST = "ТЕСТ метода не выполнен, входные данные заданы не корректно.";

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

    ////////////////////////////МЕТОДЫ_ТЕСТЕРОВЩИКИ//////////////////////////++

    /**
     * Тестируем метод создания задачи в менеджере
     */
    public void testAddTask(Task task) {

        boolean goalAchieved = true;// тест пройден

        printHeadOfTest("Task addTask(Task task)",
                "создать новую задачу",
                "найти задачу в менеджере.",
                "Task getTaskById(int id)",
                "новая задача не найдена.");

        if (task != null) {
            //Вызов тестируемого метода
            tracker.addTask(task);
            System.out.println("Создана задача с id = " + task.getId());

            //Проверка достижения цели - задача найдена в HashMap менеджера
            Task taskMustToBe = tracker.getTaskById(task.getId());
            if (taskMustToBe == null) {
                goalAchieved = false;
            }
        } else {
            System.out.println(MSG_ERROR_TEST);
        }

        viewResult(goalAchieved);
        if (goalAchieved) {
            System.out.println(task);
        } else {
            System.out.println(MSG_ERROR_FOR_METHOD);
        }
    }

    /**
     * Тестируем метод создания подзадачи в менеджере
     */
    public void testAddSubTask(SubTask subTask) {
        // тест пройден
        boolean goalAchieved = true;

        printHeadOfTest("SubTask addSubTask(SubTask subTask)",
                "создать новую подзадачу",
                "найти подзадачу в менеджере.",
                "SubTask getSubTaskById(int id)",
                "новая подзадача не найдена.");

        if (subTask != null) {
            //Вызов тестируемого метода
            tracker.addSubTask(subTask);
            System.out.println("Создана подзадача с id = " + subTask.getId() + " для эпика с id = "
                    + subTask.getParent().getId());

            //Проверка достижения цели - задача найдена в HashMap менеджера
            SubTask subTaskMustToBe = tracker.getSubTaskById(subTask.getId());
            if (subTaskMustToBe == null) {
                goalAchieved = false;
            }
        } else {
            System.out.println(MSG_ERROR_TEST);
        }

        viewResult(goalAchieved);
        if (goalAchieved) {
            System.out.println(subTask);
        } else {
            System.out.println(MSG_ERROR_FOR_METHOD);
        }
    }

    /**
     * Тестируем метод создания эпика в менеджере
     */
    public void testAddEpic(Epic epic) {
        boolean goalAchieved = true;// тест пройден

        printHeadOfTest("Epic addEpic(Epic epic)",
                "создать новый эпик",
                "найти эпик в менеджере.",
                "Epic getEpicById(int id)",
                "новый эпик не найден.");

        //Обращение к методу
        if (epic != null) {
            tracker.addEpic(epic);
            System.out.println("Создана эпик с id = " + epic.getId());

            //Проверка достижения цели - задача найдена в HashMap менеджера
            Issue epicMustToBe = tracker.getEpicById(epic.getId());
            if (epicMustToBe == null) {
                goalAchieved = false;
            }
        } else {
            System.out.println(MSG_ERROR_TEST);
        }

        viewResult(goalAchieved);
        if (goalAchieved) {
            System.out.println(epic);
        } else {
            System.out.println(MSG_ERROR_FOR_METHOD);
        }
    }

    /**
     * Тестируем метод обновления задачи
     */
    public void testUpdateTask(Task task) {

        boolean goalAchieved = true;

        printHeadOfTest("Task UpdateTask(Task task)",
                "обновить задачу",
                "по входному id находим задачу в менеджере, сравниваем поля (title,description,status).",
                "Task getTaskById(int id)",
                "если не найдена задача по входному id или не корректно обновлены поля класса.");

        if (task != null) {
            Task taskToUpdate = tracker.getTaskById(task.getId());
            if (taskToUpdate != null) {

                //Выводим данные по старой задаче
                System.out.println("Найдена задача по id = " + taskToUpdate.getId() + " : " + taskToUpdate);

                //Выводим данные по новой задаче
                System.out.println("\nОбновлена задача с id = " + task.getId() + " : " + task + "\n");

                //Обновляем задачу
                tracker.updateTask(task);

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
        } else {
            System.out.println(MSG_ERROR_TEST);
        }

        viewResult(goalAchieved);
        printListAllTasks();
    }

    /**
     * Тестируем метод обновления подзадач
     */
    public void testUpdateSubTask(SubTask subTask) {
        boolean goalAchieved = true;

        printHeadOfTest("SubTask UpdateSubTask(SubTask subTask)",
                "обновить подзадачу",
                "по входному id находим подзадачу в менеджере, сравниваем поля " +
                         "(title,description,status, parent).",
                "SubTask getSubTaskById(int id);",
                "если не найдена подзадача по входному id или не корректно обновлены поля класса.");

        if (subTask != null) {
            SubTask subTaskToUpdate = tracker.getSubTaskById(subTask.getId());
            if (subTaskToUpdate != null) {

                //Выводим данные по старой подзадаче
                System.out.println("Найдена подзадача по id = " + subTaskToUpdate.getId() + " : " + subTaskToUpdate);
                // Дополнительная информация
                Epic parent = subTaskToUpdate.getParent();
                System.out.println("Родитель подзадачи эпик с id = " + parent.getId() + " : " + parent);
                System.out.println("Все подзадачи эпика с id = " + parent.getId() + " : ");
                for (SubTask subTaskChild : parent.getChildrenList()) {
                    System.out.println(subTaskChild);
                }

                //Выводим данные по новой подзадаче
                System.out.println("\nВходная подзадача с id = " + subTask.getId() + " : " + subTask);
                // Дополнительная информация
                Epic newParent = subTask.getParent();
                System.out.println("Родитель подзадачи эпик с id = " + newParent.getId() + " : " + newParent);
                System.out.println("Все подзадачи эпика с id = " + newParent.getId() + " : ");
                for (SubTask subTaskChild : newParent.getChildrenList()) {
                    System.out.println(subTaskChild);
                }

                //Обновляем подзадачу
                tracker.updateSubTask(subTask);

                SubTask updateSubTask = tracker.getSubTaskById(subTask.getId());
                if (!subTask.getTitle().equals(updateSubTask.getTitle()) ||
                        !subTask.getStatus().equals(updateSubTask.getStatus()) ||
                        !subTask.getDescription().equals(updateSubTask.getDescription()) ||
                        !subTask.getParent().equals(updateSubTask.getParent())) {
                    goalAchieved = false;
                }

            } else {
                System.out.println(MSG_ERROR_ID_NOT_FOUND);
                goalAchieved = false;
            }
        } else {
            System.out.println(MSG_ERROR_TEST);
        }

        viewResult(goalAchieved);
        printListAllSubTasks();
        printListAllEpic();
    }

    /**
     * Тестируем метод обновления эпика
     */
    public void testUpdateEpic(Epic epic) {
        boolean goalAchieved = true;

        printHeadOfTest("Epic updEpic(Epic epic)",
                "обновить эпик",
                "по входному id находим эпик в менеджере, сравниваем поля (title,description). " +
                        "Статус как и дети при обновлении меняться не должен.",
                "Epic getEpicById(int id);",
                "если не найден эпик по входному id, не совпали дети, не корректно обновлены поля.");

        if (epic != null) {
            Epic epicToUpdate = tracker.getEpicById(epic.getId());
            if (epicToUpdate != null) {

                //Выводим данные по старому эпику
                System.out.println("Найден эпик по id = " + epic.getId() + " : " + epicToUpdate);
                // Дополнительная информация
                System.out.println("Все подзадачи эпика с id = " + epicToUpdate.getId() + " : "
                        + epicToUpdate.getChildrenList());

                //Выводим данные по новому эпику
                System.out.println("\nВходной эпик с id = " + epic.getId() + " : " + epic);
                // Дополнительная информация
                System.out.println("Все подзадачи эпика с id = " + epic.getId() + " : "
                        + epic.getChildrenList() + "\n");

                //Обновляем эпик
                tracker.updateEpic(epic);

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
        } else {
            System.out.println(MSG_ERROR_TEST);
        }

        viewResult(goalAchieved);
        printListAllEpic();
        printListAllSubTasks();
    }

    /**
     * Тестируем метод удаления задачи по id
     */
    public void testDeleteTaskById(int id) {
        boolean goalAchieved = true;

        printHeadOfTest("void delTaskById(int id)",
                "удалить задачу",
                "проверить данные в хранилище на наличие удаленной задачи",
                "Task getTaskById(int id)",
                "тест ошибочный, если удаляемая задача будет найдена в менеджере.");

        System.out.println("Попытка удаления задачи с Id = " + id);
        tracker.deleteTaskById(id);

        System.out.println("Поиск задачи с Id = " + id);
        if (tracker.getTaskById(id) != null) {
            goalAchieved = false;
        }

        viewResult(goalAchieved);
        printListAllTasks();
    }

    /**
     * Тестируем метод удаления подзадачи по id
     */
    public void testDeleteSubTaskById(int id) {
        boolean goalAchieved = true;

        printHeadOfTest("void delSubTaskById(int id)",
                "удалить подзадачу",
                "проверить данные в хранилище на наличие удаленной подзадачу",
                "SubTask getSubTaskById(int id)",
                "тест ошибочный, если удаляемая подзадача будет найдена в менеджере.");

        System.out.println("Попытка удаления подзадачи с Id = " + id);
        tracker.deleteSubTaskById(id);

        System.out.println("Поиск подзадачи с Id = " + id);
        if (tracker.getSubTaskById(id) != null) {
            goalAchieved = false;
        }

        //Дополнительно для подзадачи - необходимо проверить, что подзадачи нет в детях эпика
        for (Epic epic : tracker.getListAllEpics()) {
            for (SubTask subTask : tracker.getListSubTasksOfEpic(epic)) {
                if (subTask.getId() == id) {
                    goalAchieved = false;
                    break;
                }
            }
        }

        viewResult(goalAchieved);
        printListAllEpic();
        printListAllSubTasks();
    }

    /**
     * Тестируем метод удаления эпика по id
     */
    public void testDeleteEpicById(int id) {
        boolean goalAchieved = true;

        printHeadOfTest("void delEpicById(int id)",
                "удалить эпик по id",
                "проверить данные в хранилище на наличие удаляемого эпика",
                "Epic getEpicById(int id)",
                "тест ошибочный, если удаляемый эпик будет найден в менеджере.");

        System.out.println("Попытка удаления эпика с Id = " + id);
        tracker.deleteEpicById(id);

        System.out.println("Поиск эпика с Id = " + id);
        if (tracker.getEpicById(id) != null) {
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
        printListAllEpic();
        printListAllSubTasks();
    }

    /**
     * Тестируем метод удаления списка задач
     */
    public void testDeleteAllTasks() {
        boolean goalAchieved = true;

        printHeadOfTest("void delAllTasks()",
                "удалить все задачи.",
                "проверить хранилище с задачами в менеджере.",
                "List<Task> getListAllTasks()",
                "список задач в менеджере не пуст.");

        System.out.println("Выполняется удаление задач ..");
        tracker.deleteAllTasks();

        //Проверка
        List<Task> listForCheck = tracker.getListAllTasks();
        if (!listForCheck.isEmpty()) {
            goalAchieved = false;
        }

        viewResult(goalAchieved);
        System.out.println("TASKS: " + listForCheck);
    }

    /**
     * Тестируем метод удаления списка подзадач
     */
    public void testDeleteAllSubTasks() {
        boolean goalAchieved = true;

        printHeadOfTest("void delAllSubTasks()",
                "удалить все подзадачи.",
                "проверить хранилище с подзадачами в менеджере. Проверить списки детей эпиков.",
                "List<Task> getListAllSubTasks()",
                "список подзадач в менеджере не пуст или остались дети у эпиков.");

        System.out.println("Выполняется удаление подзадач ..");
        tracker.deleteAllSubTasks();

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
        System.out.println("SUBTASKS: " + listForCheck);
        System.out.println("EPICS: " + epicListForCheck);
    }

    /**
     * Тестируем метод удаления списка эпиков
     */
    public void testDeleteAllEpics() {
        boolean goalAchieved = true;

        printHeadOfTest("void delAllEpics()",
                "удалить все эпики.",
                "проверить хранилище эпиков в менеджере.",
                "List<Task> getListAllEpics()",
                "тест ошибочный, если остались эпики или подзадачи в менеджере.");

        System.out.println("Выполняется удаление эпиков ..");
        tracker.deleteAllEpics();

        //Проверка
        List<Epic> listForCheck = tracker.getListAllEpics();
        if (!listForCheck.isEmpty()) {
            goalAchieved = false;
        }
        List<SubTask> subTaskListForCheck = tracker.getListAllSubTasks();
        if (subTaskListForCheck.size() > 0) {
            goalAchieved = false;
        }

        viewResult(goalAchieved);
        System.out.println("EPICS: " + listForCheck);
        System.out.println("SUBTASKS: " + subTaskListForCheck);
    }

    /**
     * Тестируем метод получения списка задач
     */
    public void testGetListAllTasks() {

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
    }

    /**
     * Тестируем метод получения списка подзадач
     */
    public void testGetListAllSubTasks() {

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
    }

    /**
     * Тестируем метод получения списка эпиков
     */
    public void testGetListAllEpics() {

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
    }

    public void testGetIssueById(int id) {

        Issue issue = tracker.getTaskById(id);
        if (issue == null) {
            issue = tracker.getSubTaskById(id);
        }
        if (issue == null) {
            issue = tracker.getEpicById(id);
        }

        printHeadOfTest("Task getTaskById(int id),SubTask getSubTaskById(int id),Epic getEpicById(int id)",
                "получить задачу по id.",
                "визуализируем найденную задачу.",
                "System.out.print",
                "тест всегда считаем успешным.");

        viewResult(true);

        System.out.println(issue);
        if (issue == null) {
            System.out.println("Сущность с указанным id не найдена.");
        }
    }
    ////////////////////////////МЕТОДЫ_ТЕСТЕРОВЩИКИ//////////////////////////--

    ////////////////////////////МЕТОДЫ_СЦЕНАРИИ_ТЕСТА////////////////////////++
    /**
     * Сценарий создания задачи, для проверки метода - создать задачу
     * <p>Создаем экземпляр класса задача {@link Task}.
     * <p>Применяем тест метода создания задачи в менеджере.
     */
    public void testScriptAddTaskOneMore(){
        int numberTask=tracker.getListAllTasks().size() + 1;
        testAddTask(addTask("Задача" + numberTask,"Описание задачи" + numberTask));
    }

    /**
     * Сценарий создания эпика с детьми, для проверки методов создания эпика {@link Epic} и подзадачи {@link SubTask}
     * @param quantitySubTask количество подзадач эпика
     */
    public void testScriptAddEpicWithChildren(int quantitySubTask) {
        int numberEpic    = tracker.getListAllEpics().size() + 1;
        int numberSubTask = tracker.getListAllSubTasks().size() + 1;
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
                testUpdateTask(cloneTask);
            } else {
                System.out.println(MSG_ERROR_ID_NOT_FOUND);
            }
        } else {
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
            System.out.println(MSG_ERROR_ID_NOT_FOUND);
            }
        } else {
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
        List<Epic> listEpics= tracker.getListAllEpics();

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
                System.out.println(MSG_ERROR_ID_NOT_FOUND);
            }
        } else {
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

    /**
     * Сценарий тестирования для проверки сформированной истории просмотров:
     * <p>Добавляем и просматриваем задачи в количестве по размеру пула истории просмотров.
     * <p>Выводим на экран пул с историей просмотров, задачи должны выводиться с ID по возрастанию
     */
    public void testScriptGetHistory() {
        int numberTask = tracker.getListAllTasks().size() + 1;

        printHeadOfTest("List<Issue> getHistory()",
                "проверить историю просмотров.",
                "Выводим историю просмотров на экран",
                "System.out.println",
                "тест всегда считаем успешным. Историю просмотров оцениваем на экране.");

        viewResult(true);

        //Выводим список просмотров
        System.out.println("История просмотров:");
        for (Issue issue : tracker.getHistory()) {
            System.out.println("\t" + issue);
        }
    }

    ////////////////////////////МЕТОДЫ_СЦЕНАРИИ_ТЕСТА////////////////////////

    ////////////////////////////МЕТОДЫ_ПЕЧАТИ/////////////////////////////////

    public void printLine() {
        System.out.println("-------------------------------------------");
    }

    /**
     * Печать всех типов задач в удобочитаемом формате
     */
    public void printTaskManager() {
        printLine();
        System.out.println("Списки задач в памяти тестируемого менеджера:");
        printListAllTasks();
        printListAllSubTasks();
        printListAllEpic();
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
     * Печать списка задач
     */
    private void printListAllTasks() {
        List<Task> listTask = tracker.getListAllTasks();
        System.out.println("TASKS:");
        for (Task task : listTask) {
            System.out.println("\t" + task);
        }
    }

    /**
     * Печать списка подзадач
     */
    private void printListAllSubTasks() {
        List<SubTask> listSubTask = tracker.getListAllSubTasks();
        System.out.println("SUBTASKS:");
        for (SubTask subTask : listSubTask) {
            System.out.println("\t" + subTask);
        }
    }

    /**
     * Печать списка эпиков
     */
    private void printListAllEpic() {
        List<Epic> Epic = tracker.getListAllEpics();
        System.out.println("EPICS:");
        for (Epic epic : Epic) {
            System.out.println("\t" + epic);
        }
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

    ////////////////////////////МЕТОДЫ_ПЕЧАТИ/////////////////////////////////

    ////////////////////////////СЛУЖЕБНЫЕ/////////////////////////////////////
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
                List<Task> tasksList = tracker.getListAllTasks();
                issues.addAll(tasksList);
                break;

            case EPIC:
                List<Epic> epicsList = tracker.getListAllEpics();
                issues.addAll(epicsList);
                break;
            case SUBTASK:
                List<SubTask> subTaskList = tracker.getListAllSubTasks();
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

    ////////////////////////////МЕТОДЫ_КОНСТРУКТОРЫ//////////////////////////
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
        if (!parent.getChildrenList().contains(newSubTask)) {
            parent.getChildrenList().add(newSubTask);
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

    ////////////////////////////СЛУЖЕБНЫЕ/////////////////////////////////////
}
