
import model.IssueStatus;
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

        //Создать задачу/подзадачу/эпик
        test.testScriptAddTaskOneMore();                   //Тест №1
        test.testScriptAddEpicWithChildren(2);//Тест №2-4

        //Получить списки
        test.testGetListAllTasks();                        //Тест №5
        test.testGetListAllEpics();                        //Тест №6
        test.testGetListAllSubTasks();                     //Тест №7

        //Тест истории просмотров
        test.testGetHistory();                             //Тест №8

        //тест удаления списков
        test.testDeleteAllTasks();                         //Тест №9
        test.testDeleteAllSubTasks();                      //Тест №10
        test.testDeleteAllEpics();                         //Тест №11

        //Вывести общий результат в хранилищах
        test.printTaskManager(); // должно быть пусто

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
        test.testScriptAddTaskOneMore();                        //Тест №19
        test.testUpdateTaskForLastTask(IssueStatus.IN_PROGRESS);//Тест №20
        test.testUpdateTaskForLastTask(IssueStatus.DONE);       //Тест №21
        //Обновляем статус у подзадачи
        test.testScriptAddEpicWithChildren(1);          //Тест №22,23
        test.testUpdateStatusForLastSubTask(IssueStatus.IN_PROGRESS);//Тест №24
        test.testUpdateStatusForLastSubTask(IssueStatus.DONE);       //Тест №25
        //Обновляем родителя у подзадачи
        test.testScriptAddEpicWithChildren(1);   //Тест №26,27
        test.testUpdateSubTaskForLastSubTask();               //Тест №28
        //Проверим статус эпика, для нескольких подзадач, должен стать Done
        test.testUpdateStatusForLastSubTask(IssueStatus.DONE);//Тест №29
        //Обновляем эпик, попытаемся поставить статус DONE
        //Менеджер не должен этого допустить
        test.testScriptAddEpicWithChildren(1);   //Тест №30,31
        test.testUpdateStatusForLastEpic(IssueStatus.DONE);   //Тест №32

        //Выводим итоги авто теста
        test.printLine();
        System.out.print("Авто ТЕСТ завершен ");
        test.viewResult(test.isCommonGoodResultAllTest());

        //Создаем нового тестировщика для ручного тестирования
        test = new TestTaskManager();
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
                case "3":// Создать эпик с 2 подзадачами
                    test.testScriptAddEpicWithChildren(2);
                    break;
                case "4":// Создать эпик с 1 подзадачей
                    test.testScriptAddEpicWithChildren(1);
                    break;
                case "5":// Удалить последнюю задачу
                    test.testScriptDeleteLastTask();
                    break;
                case "6":// Удалить последний эпик
                    test.testScriptDeleteLastEpic();
                    break;
                case "7":// Удалить последнюю подзадачу
                    test.testScriptDeleteLastSubTask();
                    break;
                case "8": // показать результат тестов
                    System.out.print("Общий результат всех тестов ");
                    test.viewResult(test.isCommonGoodResultAllTest());
                    break;
                case "9":// Обновить последнюю задачу
                    test.testUpdateTaskForLastTask(IssueStatus.DONE);
                    break;
                case "10":// Обновить последний эпик
                    test.testUpdateStatusForLastEpic(IssueStatus.DONE);
                    break;
                case "11":// Обновить последнюю подзадачу
                    test.testUpdateSubTaskForLastSubTask();
                    break;
                case "12":// Очистить список задач
                    test.testDeleteAllTasks();
                    break;
                case "13":// Очистить список эпиков
                    test.testDeleteAllEpics();
                    break;
                case "14":// Очистить список подзадач
                    test.testDeleteAllSubTasks();
                    break;
                case "15": // показать историю просмотров
                    test.testGetHistory();
                    break;
                default:
                    System.out.println("Извините, но такого теста пока нет.");
            }
        }
    }

    static void printMenu() {
        System.out.println("-------------------------------------------");
        System.out.println("1 - Вывести списки задач.         |  0  - Закончить тест.");
        System.out.println("2 - Создать задачу.               |  9  - Обновить задачу.");
        System.out.println("3 - Создать эпик с 2 подзадачами. |  10 - Обновить эпик.");
        System.out.println("4 - Создать эпик с 1 подзадачей.  |  11 - Обновить подзадачу.");
        System.out.println("5 - Удалить задачу.               |  12 - Очистить задачи.");
        System.out.println("6 - Удалить эпик.                 |  13 - Очистить эпики.");
        System.out.println("7 - Удалить подзадачу.            |  14 - Очистить подзадачи.");
        System.out.println("8 - Получить результат теста.     |  15 - Получить историю просмотров задач.");
        System.out.println("-------------------------------------------");
    }

}
