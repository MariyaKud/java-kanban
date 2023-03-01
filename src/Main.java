import model.IssueStatus;
import model.IssueType;
import service.TaskManager;
import service.TestTaskManager;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        //Запускаем тест
        TestTaskManager test = new TestTaskManager(new TaskManager());
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
        test.testGetAllTaskAndViewResult();

        //Изменить статусы
        test.lastTaskUpdateStatus(IssueStatus.IN_PROGRESS);
        test.lastTaskUpdateStatus(IssueStatus.DONE);
        // для наглядности будем тестировать эпик с 2 подзадачами
        test.lastSubTaskUpdateStatus(IssueStatus.IN_PROGRESS);
        test.lastSubTaskUpdateStatus(IssueStatus.DONE);
        // для наглядности сохранения статуса эпика удалим существующие эпики и создадим новый
        test.testDeleteAllTaskForType(IssueType.EPIC);
        test.testCreateForEpic(1);
        test.lastEpicUpdateStatus(IssueStatus.DONE);
        test.testGetAllTaskAndViewResult();

        //Удалить задачи
        test.lastTaskToDelete(IssueType.TASK);
        test.lastTaskToDelete(IssueType.EPIC);
        test.lastTaskToDelete(IssueType.SUBTASK);
        test.testGetAllTaskAndViewResult();

        //Очистить списки
        test.testDeleteAllTaskForType(IssueType.TASK);
        test.testGetAllTaskAndViewResult();
        test.testDeleteAllTaskForType(IssueType.SUBTASK);
        test.testGetAllTaskAndViewResult();
        test.testCreateForEpic(1);
        test.testGetAllTaskAndViewResult();
        test.testDeleteAllTaskForType(IssueType.EPIC);
        test.testGetAllTaskAndViewResult();

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
                    test.testGetAllTaskAndViewResult();
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
                    test.lastTaskUpdateStatus(IssueStatus.DONE);
                    break;
                case "7":// Обновить последний эпик
                    test.lastEpicUpdateStatus(IssueStatus.DONE);
                    break;
                case "8":// Обновить последнюю подзадачу
                    test.lastSubTaskUpdateStatus(IssueStatus.DONE);
                    break;
                case "9":// Удалить последнюю задачу
                    test.lastTaskToDelete(IssueType.TASK);
                    break;
                case "10":// Удалить последний эпик
                    test.lastTaskToDelete(IssueType.EPIC);
                    break;
                case "11":// Удалить последнюю подзадачу
                    test.lastTaskToDelete(IssueType.SUBTASK);
                    break;
                case "12":// Очистить список задач
                    test.testDeleteAllTaskForType(IssueType.TASK);
                    break;
                case "13":// Очистить список подзадач
                    test.testDeleteAllTaskForType(IssueType.SUBTASK);
                    break;
                case "14":// Очистить список эпиков
                    test.testDeleteAllTaskForType(IssueType.EPIC);
                    break;
                case "15": // показать результат тестов
                    System.out.print("Общий результат всех тестов ");
                    test.viewResult(test.isCommonGoodResultAllTest());
                    break;
                default:
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
