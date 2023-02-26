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

            if ("1".equals(command)) {         // Начать тест заново
                test.restartTest();
                test.printLine();
                System.out.print("ТЕСТ перезапущен.");
            } else if ("2".equals(command)) {  // Вывести списки задач
                test.printTask();
            } else if ("3".equals(command)) {  // Создать задачу
                test.createTask();
            } else if ("4".equals(command)) {  // Создать эпик с 2 подзадачами
                test.createEpic(2);
            } else if ("5".equals(command)) {  // Создать эпик с 1 подзадачей
                test.createEpic(1);
            } else if ("6".equals(command)) {  // Обновить последнюю задачу
                test.lastTaskUpdateStatus(IssueStatus.DONE);
            } else if ("7".equals(command)) {  // Обновить последний эпик
                test.lastEpicUpdateStatus(IssueStatus.DONE);
            } else if ("8".equals(command)) {  // Обновить последнюю подзадачу
                test.lastSubTaskUpdateStatus(IssueStatus.DONE);
            } else if ("9".equals(command)) {  // Удалить последнюю задачу
                test.lastTaskToDelete(IssueType.TASK);
            } else if ("10".equals(command)) { // Удалить последний эпик
                test.lastTaskToDelete(IssueType.EPIC);
            } else if ("11".equals(command)) { // Удалить последнюю подзадачу
                test.lastTaskToDelete(IssueType.SUBTASK);
            } else if ("12".equals(command)) { // Очистить список задач
                test.deleteAllTask(IssueType.TASK);
            } else if ("13".equals(command)) { // Очистить список подзадач
                test.deleteAllTask(IssueType.SUBTASK);
            } else if ("14".equals(command)) { // Очистить список эпиков
                test.deleteAllTask(IssueType.EPIC);
                test.printTask();
            } else if ("15".equals(command)) { // показать результат тестов
                System.out.print("Общий результат всех тестов ");
                test.viewResult(test.isCommonGoodResultAllTest());
            } else if ("0".equals(command)) {  // Закончить тест
                System.out.print("Ваш тест завершен ");
                test.viewResult(test.isCommonGoodResultAllTest());
                scanner.close();
                return;
            } else {
                System.out.println("Извините, но такого теста пока нет.");
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
