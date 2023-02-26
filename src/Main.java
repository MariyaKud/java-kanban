import model.*;
import service.TaskManager;
import service.TestTaskManager;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        //Запускаем менеджера, нумерация задач начинается с 1
        TaskManager tracker = new TaskManager();
        //Запускаем тест
        TestTaskManager test = new TestTaskManager(tracker);
        //экземпляр Scanner
        Scanner scanner = new Scanner(System.in);
        String command;

        test.printLine();
        System.out.println("Поехали!");
        System.out.println("Запуск авто ТЕСТА для менеджера задач...");

        //Создать задачи
        test.createTask();
        test.createTask();
        test.createEpic(1);
        test.createEpic(2);

        //Распечатать списки
        test.printTask();

        //Изменить статусы
        test.lastTaskUpdateStatus(IssueStatus.IN_PROGRESS);
        test.lastTaskUpdateStatus(IssueStatus.DONE);
        // для наглядности будем тестировать эпик с 2 подзадачами
        test.lastSubTaskUpdateStatus(IssueStatus.IN_PROGRESS);
        test.lastSubTaskUpdateStatus(IssueStatus.DONE);
        // для наглядности сохранения статуса эпика удалим существующие эпики и создадим новый
        test.deleteAllTask(IssueType.EPIC);
        test.createEpic(1);
        test.lastEpicUpdateStatus(IssueStatus.DONE);
        test.printTask();

        //Удалить задачи
        test.lastTaskToDelete(IssueType.TASK);
        test.lastTaskToDelete(IssueType.EPIC);
        test.lastTaskToDelete(IssueType.SUBTASK);
        test.printTask();

        //Очистить списки
        test.deleteAllTask(IssueType.TASK);
        test.printTask();
        test.deleteAllTask(IssueType.SUBTASK);
        test.printTask();
        test.createEpic(1);
        test.printTask();
        test.deleteAllTask(IssueType.EPIC);
        test.printTask();

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
                case "2": test.printTask(); break;                              // Вывести списки задач
                case "3": test.createTask(); break;                             // Создать задачу
                case "4": test.createEpic(2); break;               // Создать эпик с 2 подзадачами
                case "5": test.createEpic(1); break;               // Создать эпик с 1 подзадачей
                case "6": test.lastTaskUpdateStatus(IssueStatus.DONE); break;   // Обновить последнюю задачу
                case "7": test.lastEpicUpdateStatus(IssueStatus.DONE); break;   // Обновить последний эпик
                case "8": test.lastSubTaskUpdateStatus(IssueStatus.DONE); break;// Обновить последнюю подзадачу
                case "9": test.lastTaskToDelete(IssueType.TASK); break;         // Удалить последнюю задачу
                case "10":test.lastTaskToDelete(IssueType.EPIC); break;         // Удалить последний эпик
                case "11":test.lastTaskToDelete(IssueType.SUBTASK); break;      // Удалить последнюю подзадачу
                case "12":test.deleteAllTask(IssueType.TASK); break;            // Очистить список задач
                case "13":test.deleteAllTask(IssueType.SUBTASK); break;         // Очистить список подзадач
                case "14":test.deleteAllTask(IssueType.EPIC); break;            // Очистить список эпиков
                case "15": // показать результат тестов
                    System.out.print("Общий результат всех тестов ");
                    test.viewResult(test.isCommonGoodResultAllTest());
                    break;
                default: System.out.println("Извините, но такого теста пока нет.");
            }
        }

    }

    static void printMenu() {
        System.out.println("-------------------------------------------");
        System.out.println("1  - Начать тест заново.");
        System.out.println("2  - Вывести списки задач.");
        System.out.println("3  - Создать задачу.");
        System.out.println("4  - Создать эпик с 2 подзадачами.");
        System.out.println("5  - Создать эпик с 1 подзадачей.");
        System.out.println("6  - Обновить последнюю задачу.");
        System.out.println("7  - Обновить последний эпик.");
        System.out.println("8  - Обновить последнюю подзадачу.");
        System.out.println("9  - Удалить последнюю задачу.");
        System.out.println("10 - Удалить последний эпик.");
        System.out.println("11 - Удалить последнюю подзадачу.");
        System.out.println("12 - Очистить список задач.");
        System.out.println("13 - Очистить список подзадач.");
        System.out.println("14 - Очистить список эпиков.");
        System.out.println("15 - Получить общий результат теста.");
        System.out.println("0  - Закончить тест.");
        System.out.println("-------------------------------------------");
    }

}
