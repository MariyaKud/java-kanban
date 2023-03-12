
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

        //2. Добавляем задачи
        test.addTask();

        //Тест getListAllIssues
        test.getListAllIssues(IssueType.TASK);
        test.getListAllIssues(IssueType.SUBTASK);
        test.getListAllIssues(IssueType.EPIC);
        test.printTaskManager();

        //Выводим итоги авто теста
        test.printLine();
        System.out.print("Авто ТЕСТ завершен ");
        test.viewResult(test.isCommonGoodResultAllTest());
        //Перезапускаем тестер, для ручного тестирования
        test.restartTest();

        /*
        //Создать задачи
        test.testCreateForTask();
        test.testCreateForTask();
        test.testCreateForEpic(1);
        test.testCreateForEpic(2);

        //Распечатать списки
        test.testGetListOfAllIssueForType();

        //Изменить статусы
        test.testUpdStatusForLastTask(IssueStatus.IN_PROGRESS);
        test.testUpdStatusForLastTask(IssueStatus.DONE);
        // для наглядности будем тестировать эпик с 2 подзадачами
        test.testUpdStatusForLastSubTask(IssueStatus.IN_PROGRESS);
        test.testUpdStatusForLastSubTask(IssueStatus.DONE);
        // для наглядности сохранения статуса эпика удалим существующие эпики и создадим новый
        test.testDelListOfAllIssueForType(IssueType.EPIC);
        test.testCreateForEpic(1);
        test.testUpdStatusForLastEpic(IssueStatus.DONE);
        test.printTaskManager();

        //Удалить задачи
        test.testDelAllIssueForType(IssueType.TASK);
        test.testDelAllIssueForType(IssueType.EPIC);
        test.testDelAllIssueForType(IssueType.SUBTASK);
        test.printTaskManager();

        //Очистить списки
        test.testDelListOfAllIssueForType(IssueType.TASK);
        test.printTaskManager();
        test.testDelListOfAllIssueForType(IssueType.SUBTASK);
        test.printTaskManager();
        test.testCreateForEpic(1);
        test.printTaskManager();
        test.testDelListOfAllIssueForType(IssueType.EPIC);
        test.printTaskManager();

        test.printLine();
        test.testGetHistory();



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
                case "2":// Вывести списки задач
                    test.printTaskManager();
                    break;
                case "3":// Создать задачу
                    test.testCreateForTask();
                    break;
                case "4":// Создать эпик с 2 подзадачами
                    test.testCreateForEpic(2);
                    break;
                case "5":// Создать эпик с 1 подзадачей
                    test.testCreateForEpic(1);
                    break;
                case "6":// Обновить последнюю задачу
                    test.testUpdStatusForLastTask(IssueStatus.DONE);
                    break;
                case "7":// Обновить последний эпик
                    test.testUpdStatusForLastEpic(IssueStatus.DONE);
                    break;
                case "8":// Обновить последнюю подзадачу
                    test.testUpdStatusForLastSubTask(IssueStatus.DONE);
                    break;
                case "9":// Удалить последнюю задачу
                    test.testDelAllIssueForType(IssueType.TASK);
                    break;
                case "10":// Удалить последний эпик
                    test.testDelAllIssueForType(IssueType.EPIC);
                    break;
                case "11":// Удалить последнюю подзадачу
                    test.testDelAllIssueForType(IssueType.SUBTASK);
                    break;
                case "12":// Очистить список задач
                    test.testDelListOfAllIssueForType(IssueType.TASK);
                    break;
                case "13":// Очистить список эпиков
                    test.testDelListOfAllIssueForType(IssueType.EPIC);
                    break;
                case "14":// Очистить список подзадач
                    test.testDelListOfAllIssueForType(IssueType.SUBTASK);
                    break;
                case "15": // показать результат тестов
                    System.out.print("Общий результат всех тестов ");
                    test.viewResult(test.isCommonGoodResultAllTest());
                    break;
                case "16": // показать историю просмотров
                    test.testGetHistory();
                    break;
                default:
                    System.out.println("Извините, но такого теста пока нет.");
            }
        }
         */
    }

    static void printMenu() {
        System.out.println("-------------------------------------------");
        System.out.println("0 - Закончить тест.  1  - Начать тест заново.          2  - Вывести списки задач.");
        System.out.println("3 - Создать задачу.  4  - Создать эпик с 2 подзадачами.5  - Создать эпик с 1 подзадачей.");
        System.out.println("6 - Обновить задачу. 7  - Обновить эпик.               8  - Обновить подзадачу.");
        System.out.println("9 - Удалить задачу.  10 - Удалить эпик.                11 - Удалить подзадачу.");
        System.out.println("12- Очистить задачи. 13 - Очистить эпики.              14 - Очистить подзадачи.");
        System.out.println("15- Получить результат теста. 16 - Получить историю просмотров задач.");
        System.out.println("-------------------------------------------");
    }

}
