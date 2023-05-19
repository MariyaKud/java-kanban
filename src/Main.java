import dao.KVServer;

import service.HttpTaskManager;
import service.Managers;
import service.TaskManager;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        System.out.println("-----------------------------------------");
        final KVServer kvServer;
        try {
            kvServer = new KVServer();
            kvServer.start();
        } catch (IOException e) {
            System.out.println("Возникли проблемы с доступом к северу");
            return;
        }

        final TaskManager taskManager = Managers.getDefault();

        Managers.getSimpleTestForTaskManager(taskManager);

        System.out.println("\n Загружаем данные с сервера HTTP..");
        TaskManager loadFromHTTPServer = HttpTaskManager.loadFromHTTPServer(Managers.PORT_KV_SERVER);

        System.out.println("\nСверим данные менеджера, с восстановленными данными с сервера HTTP:");

        System.out.println("Результат сравнения задач менеджера и задач загруженных с сервера HTTP: " +
                ((loadFromHTTPServer.getAllTasks().equals(taskManager.getAllTasks())) ? "✅" : "❌"));
        System.out.println("Результат сравнения подзадач менеджера и подзадач загруженных с сервера HTTP: " +
                ((loadFromHTTPServer.getAllSubTasks().equals(taskManager.getAllSubTasks())) ? "✅" : "❌"));
        System.out.println("Результат сравнения эпиков менеджера и эпиков загруженных с сервера HTTP: " +
                ((loadFromHTTPServer.getAllEpics().equals(taskManager.getAllEpics())) ? "✅" : "❌"));
        System.out.println("Результат сравнения истории просмотров менеджера и истории восстановленной с сервера HTTP: "
                + (loadFromHTTPServer.getHistory().equals(taskManager.getHistory()) ? "✅" : "❌"));
        System.out.println("Результат сравнения отсортированного списка задач менеджера и восстановленного: " +
                (loadFromHTTPServer.getPrioritizedTasks().equals(taskManager.getPrioritizedTasks())
                        ? "✅" : "❌"));

        System.out.println("\nАВТО ТЕСТ HttpTaskManager завершен");

        kvServer.stop();

        System.out.println("-----------------------------------------");
    }
}


