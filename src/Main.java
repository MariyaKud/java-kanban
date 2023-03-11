
import model.IssueStatus;
import model.IssueType;
import service.Managers;
import service.TestTaskManager;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        //Запускаем тест
        //InMemoryTestManager test = new InMemoryTestManager(new InMemoryTaskManager());
        TestTaskManager test = new TestTaskManager(new Managers().getDefault());

        Scanner scanner = new Scanner(System.in);
        String command;

        test.printLine();
        System.out.println("Поехали!");
        System.out.println("Запуск авто ТЕСТА для менеджера задач...");

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
        test.testGetListOfAllIssueForType();

        //Удалить задачи
        test.testDelAllIssueForType(IssueType.TASK);
        test.testDelAllIssueForType(IssueType.EPIC);
        test.testDelAllIssueForType(IssueType.SUBTASK);
        test.testGetListOfAllIssueForType();

        //Очистить списки
        test.testDelListOfAllIssueForType(IssueType.TASK);
        test.testGetListOfAllIssueForType();
        test.testDelListOfAllIssueForType(IssueType.SUBTASK);
        test.testGetListOfAllIssueForType();
        test.testCreateForEpic(1);
        test.testGetListOfAllIssueForType();
        test.testDelListOfAllIssueForType(IssueType.EPIC);
        test.testGetListOfAllIssueForType();

        test.printLine();
        test.testGetHistory();

        //Выводим итоги авто теста
        test.printLine();
        System.out.print("Авто ТЕСТ завершен ");
        test.viewResult(test.isCommonGoodResultAllTest());
        test.restartTest(); //Перезапускаем тестер, для ручного тестирования

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
                    test.testGetListOfAllIssueForType();
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
