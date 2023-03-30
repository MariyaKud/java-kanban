
import model.IssueStatus;
import service.TestTaskManager;

import java.util.InputMismatchException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        TestTaskManager test = new TestTaskManager();

        Scanner scanner = new Scanner(System.in);
        String command;

        /* Запускаем авто тест
        test.printLine();
        System.out.println("Поехали!");
        System.out.println("Запуск авто ТЕСТА для менеджера задач...");

        //Создать задачу/подзадачу/эпик
        test.testScriptAddTaskOneMore();                   //Тест №1
        test.testScriptAddEpicWithChildren(2);//Тест №2-4

        //Получить списки
        test.testGetListAllTasks();                        //Тест №5
        test.testGetListAllEpics();                        //Тест №6
        test.testGetListAllSubTasks();                     //Тест №7

        //Тест истории просмотров
        test.testScriptGetHistory();                       //Тест №8

        //тест удаления списков
        test.testDeleteAllTasks();                         //Тест №9
        test.testDeleteAllSubTasks();                      //Тест №10
        test.testDeleteAllEpics();                         //Тест №11

        //Вывести общий результат в хранилищах
        test.printTaskManager(); // должно быть пусто

        test.testScriptGetHistory();

        //Тест удаления по id
        test.testScriptAddTaskOneMore();                   //Тест №12
        test.testScriptDeleteLastTask();                   //Тест №13
        test.testScriptAddEpicWithChildren(2);//Тест №14-16
        test.testScriptDeleteLastSubTask();                //Тест №17
        test.testScriptDeleteLastEpic();                   //Тест №18

        //Вывести общий результат в хранилищах
        test.printTaskManager();// должно быть пусто

        //Тест обновлений:
        //Обновляем статус задачи
        test.testScriptAddTaskOneMore();                                   //Тест №19
        test.testScriptUpdateStatusForLastTask(IssueStatus.IN_PROGRESS);   //Тест №20
        test.testScriptUpdateStatusForLastTask(IssueStatus.DONE);          //Тест №21
        //Обновляем статус у подзадачи
        test.testScriptAddEpicWithChildren(1);                //Тест №22,23
        test.testScriptUpdateStatusForLastSubTask(IssueStatus.IN_PROGRESS);//Тест №24
        test.testScriptUpdateStatusForLastSubTask(IssueStatus.DONE);       //Тест №25
        //Обновляем родителя у подзадачи
        test.testScriptAddEpicWithChildren(1);                //Тест №26,27
        // для проверки теста смены родителя выведем стартовое состояние
        test.printTaskManager();
        test.testUpdateParentForLastSubTask();                             //Тест №28
        //Проверим статус эпика, для нескольких подзадач, должен стать Done
        test.testScriptUpdateStatusForLastSubTask(IssueStatus.DONE);       //Тест №29
        //Обновляем эпик, попытаемся поставить статус DONE
        //Менеджер не должен этого допустить
        test.testScriptAddEpicWithChildren(1);                //Тест №30,31
        test.testUpdateStatusForLastEpic(IssueStatus.DONE);                //Тест №32

        //Выводим итоги авто теста
        test.printLine();
        System.out.print("Авто ТЕСТ завершен ");
        test.viewResult(test.isCommonGoodResultAllTest());
         */

        //Создаем нового тестировщика для ручного тестирования
        //test = new TestTaskManager();

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
                    int userInput = scanner.nextInt();
                    test.testGetIssueById(userInput);
                    break;
                case "11":// Получить историю просмотров задач.
                    test.testScriptGetHistory();
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
