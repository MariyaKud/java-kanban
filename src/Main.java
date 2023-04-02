
import model.IssueStatus;
import service.TestTaskManager;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        //Создаем экземпляр тестировщика
        TestTaskManager test = new TestTaskManager();
        //Создаем объект сканера
        Scanner scanner = new Scanner(System.in);
        //Переменная для введенной команды
        String command;
        //Переменная для ввода id
        int inputId;

        System.out.println("Запущен авто тест...");
        test.testScriptAddTaskOneMore();
        test.testScriptAddTaskOneMore();
        test.testScriptAddEpicWithChildren(2);
        test.printTaskManager();
        test.testScriptUpdateStatusForLastTask(IssueStatus.IN_PROGRESS);
        test.testScriptUpdateStatusForLastTask(IssueStatus.DONE);
        test.testScriptUpdateStatusForLastSubTask(IssueStatus.IN_PROGRESS);
        test.testScriptUpdateStatusForLastSubTask(IssueStatus.DONE);
        test.testScriptAddEpicWithChildren(1);
        test.testUpdateParentForLastSubTask();
        test.printTaskManager();
        test.testUpdateStatusForLastEpic(IssueStatus.DONE);
        test.testGetHistory();
        test.testScriptDeleteLastTask();
        test.testScriptDeleteLastSubTask();
        test.testScriptDeleteLastEpic();
        test.testGetHistory();
        test.testDeleteAllTasks();
        test.testGetHistory();
        test.testDeleteAllSubTasks();
        test.testGetHistory();
        test.testDeleteAllEpics();
        test.testGetHistory();
        test.testScriptAddTaskOneMore();
        test.testScriptAddTaskOneMore();
        test.printTaskManager();
        test.testGetIssueById(8);
        test.testGetHistory();

        test.printInfoAboutAllTests();
        System.out.println("Авто тест завершен!");
        System.out.println("-------------------------------------------");
        System.out.println("Запущено ручное тестирование..");
        //Выводим меню для ручного тестирования
        test = new TestTaskManager();

        while (true) {

            printMenu();
            command = scanner.nextLine().trim();

            switch (command) {
                case "0":// Закончить тест
                    System.out.println("Ваш тест завершен.");
                    test.printInfoAboutAllTests();
                    scanner.close();
                    return;
                case "1":// Вывести списки задач
                    test.printTaskManager();
                    break;
                case "2":// Создать задачу
                    test.testScriptAddTaskOneMore();
                    break;
                case "3":// Создать эпик
                    test.testScriptAddEpicWithChildren(0);
                    break;
                case "4":// Создать эпик с 1 подзадачей
                    test.testScriptAddEpicWithChildren(1);
                    break;
                case "5":// Создать эпик с 2 подзадачами.
                    test.testScriptAddEpicWithChildren(2);
                    break;
                case "6":// Создать эпик с 3 подзадачами.
                    test.testScriptAddEpicWithChildren(3);
                    break;
                case "7":// Очистить задачи.
                    test.testDeleteAllTasks();
                    break;
                case "8": // Очистить подзадачи.
                    test.testDeleteAllSubTasks();
                    break;
                case "9":// Очистить эпики.
                    test.testDeleteAllEpics();
                    break;
                case "10":// Найти задачу
                    System.out.println("Укажите id для поиска:");
                    if (scanner.hasNextInt()) {
                        inputId = scanner.nextInt();
                        test.testGetIssueById(inputId);
                        scanner.nextLine();
                    } else {
                        System.out.println("Тест не выполнился. Для выполнения этого теста необходимо внести число.");
                    }
                    break;
                case "11":// Получить историю просмотров задач.
                    test.testGetHistory();
                    break;
                case "12":// Обновить последнюю подзадачу
                    test.testScriptDeleteLastTask();
                    break;
                case "13":// Удалить подзадачу.
                    test.testScriptDeleteLastSubTask();
                    break;
                case "14":// Удалить эпик.
                    test.testScriptDeleteLastEpic();
                    break;
                case "15":// Обновить статус задачи.
                    test.testScriptUpdateStatusForLastTask(IssueStatus.DONE);
                    break;
                case "16": // Обновить родителя подзадачи.
                    test.testUpdateParentForLastSubTask();
                    break;
                case "17": // Обновить статус подзадачи.
                    test.testScriptUpdateStatusForLastSubTask(IssueStatus.DONE);
                    break;
                case "18": // Обновить эпик.
                    test.testUpdateStatusForLastEpic(IssueStatus.DONE);
                    break;
                case "19": // Получить результат теста.
                    test.printInfoAboutAllTests();
                    break;
                default:
                    System.out.println("Такой функционал не предусмотрен.");
            }
        }
    }

    private static void  printMenu() {
        System.out.println("-------------------------------------------");
        System.out.println("1  - Вывести все списки менеджера. | 11 - Получить историю просмотров задач.");
        System.out.println("2  - Создать задачу.               | 12 - Удалить задачу.");
        System.out.println("3  - Создать эпик.                 | 13 - Удалить подзадачу.");
        System.out.println("4  - Создать эпик с 1 подзадачей.  | 14 - Удалить эпик.");
        System.out.println("5  - Создать эпик с 2 подзадачами. | 15 - Обновить статус задачи.");
        System.out.println("6  - Создать эпик с 3 подзадачами. | 16 - Обновить родителя подзадачи.");
        System.out.println("7  - Очистить задачи.              | 17 - Обновить статус подзадачи.");
        System.out.println("8  - Очистить подзадачи.           | 18 - Обновить эпик.");
        System.out.println("9  - Очистить эпики.               | 19 - Получить результат теста.");
        System.out.println("10 - Найти задачу по id.           | 0  - Закончить тест.");
        System.out.println("-------------------------------------------");
    }
}


