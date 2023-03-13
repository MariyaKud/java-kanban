
import model.IssueStatus;
import model.IssueType;
import service.TestTaskManager;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        TestTaskManager test = new TestTaskManager();

        Scanner scanner = new Scanner(System.in);
        String command;

        //Запускаем авто тест
        test.printLine();
        System.out.println("Поехали!");
        System.out.println("Запуск авто ТЕСТА для менеджера задач...");

        //1. Тест методов создания всех видов задач, еще не внесенного в систему учета менеджера
        test.addTask(null,null,null);
        test.addTask(null,null);
        test.addSubTask(null,null,null,null);
        test.addEpic(null,null);

        //2. Добавляем задачи через универсальный метод
        test.addIssueTask();
        test.getListAllIssues(IssueType.TASK);    //Тест getListAllIssues

        test.addIssueEpic(1);
        test.getListAllIssues(IssueType.SUBTASK); //Тест getListAllIssues
        test.getListAllIssues(IssueType.EPIC);    //Тест getListAllIssues

        //Очистить списки универсальным методом
        test.delAllIssues(IssueType.TASK);
        test.delAllIssues(IssueType.SUBTASK);
        test.delAllIssues(IssueType.EPIC);
        test.printTaskManager();

        //3. Добавляем задачи через специализированные методы
        test.addTask();
        test.getListAllTasks();

        test.addEpic(2);
        test.getListAllEpics();
        test.getListAllSubTasks();

        test.delAllTasks();
        test.delAllSubTasks();
        test.delAllEpics();

        //Вывести общий результат в хранилищах
        test.printTaskManager();

        //Тест истории просмотров
        test.getHistory();

        //Тест удалить по id универсальный
        test.delLastIssue(IssueType.TASK);
        test.addEpic(2);
        test.delLastIssue(IssueType.SUBTASK);
        test.delLastIssue(IssueType.EPIC);

        //Тест удаления по id
        test.delLastTask();
        test.delLastSubTask();
        test.addEpic(1);
        test.delLastSubTask();
        test.delLastEpic();
        test.printTaskManager();

        //Тест обновления универсального
        test.testUpdStatusForLastTask(IssueStatus.IN_PROGRESS);
        test.testUpdStatusForLastTask(IssueStatus.DONE);
        test.addEpic(1);
        test.testUpdStatusForLastSubTask(IssueStatus.IN_PROGRESS);
        test.testUpdStatusForLastSubTask(IssueStatus.DONE);
        test.addEpic(1);
        test.addEpic(1);
        test.testUpdParentForLastSubTask();

        //Тест индивидуальных update
        test.addTask();
        test.testUpdTaskForLastTask(IssueStatus.IN_PROGRESS);
        test.testUpdTaskForLastTask(IssueStatus.DONE);
        test.addEpic(1);
        test.testUpdSubTaskForLastSubTask();
        test.testUpdStatusForLastEpic();

        //Выводим итоги авто теста
        test.printLine();
        System.out.print("Авто ТЕСТ завершен ");
        test.viewResult(test.isCommonGoodResultAllTest());
        //Перезапускаем тестер, для ручного тестирования
        test.restartTest();

        //Выводим меню для ручного тестирования
        while (true) {

            printMenu();
            command = scanner.nextLine().trim();

            switch (command) {
                case "0":// Закончить тест
                    System.out.print("Ваш тест завершен ");
                    test.viewResult(test.isCommonGoodResultAllTest());
                    scanner.close();
                    return;
                case "1":// Начать тест заново
                    test.restartTest();
                    test.printLine();
                    System.out.print("ТЕСТ перезапущен.");
                    break;
                case "9":// Вывести списки задач
                    test.printTaskManager();
                    break;
                case "2":// Создать задачу
                    test.addTask();
                    break;
                case "3":// Создать эпик с 2 подзадачами
                    test.addEpic(2);
                    break;
                case "11":// Создать эпик с 1 подзадачей
                    test.addEpic(1);
                    break;
                case "10":// Обновить последнюю задачу
                    test.testUpdTaskForLastTask(IssueStatus.DONE);
                    break;
                case "4":// Обновить последний эпик
                    test.testUpdStatusForLastEpic();
                    break;
                case "12":// Обновить последнюю подзадачу
                    test.testUpdSubTaskForLastSubTask();
                    break;
                case "5":// Удалить последнюю задачу
                    test.delLastTask();
                    break;
                case "6":// Удалить последний эпик
                    test.delLastEpic();
                    break;
                case "7":// Удалить последнюю подзадачу
                    test.delLastSubTask();
                    break;
                case "13":// Очистить список задач
                    test.delAllTasks();
                    break;
                case "14":// Очистить список эпиков
                    test.delAllEpics();
                    break;
                case "15":// Очистить список подзадач
                    test.delAllSubTasks();
                    break;
                case "8": // показать результат тестов
                    System.out.print("Общий результат всех тестов ");
                    test.viewResult(test.isCommonGoodResultAllTest());
                    break;
                case "16": // показать историю просмотров
                    test.getHistory();
                    break;
                default:
                    System.out.println("Извините, но такого теста пока нет.");
            }
        }
    }

    static void printMenu() {
        System.out.println("-------------------------------------------");
        System.out.println("1 - Начать тест заново.           |  9  - Вывести списки задач.");
        System.out.println("2 - Создать задачу.               |  10 - Обновить задачу.");
        System.out.println("3 - Создать эпик с 2 подзадачами. |  11 - Создать эпик с 1 подзадачей.");
        System.out.println("4 - Обновить эпик.                |  12 - Обновить подзадачу.");
        System.out.println("5 - Удалить задачу.               |  13 - Очистить задачи.");
        System.out.println("6 - Удалить эпик.                 |  14 - Очистить эпики.");
        System.out.println("7 - Удалить подзадачу.            |  15 - Очистить подзадачи.");
        System.out.println("8 - Получить результат теста.     |  16 - Получить историю просмотров задач.");
        System.out.println("0 - Закончить тест. ");
        System.out.println("-------------------------------------------");
    }

}
