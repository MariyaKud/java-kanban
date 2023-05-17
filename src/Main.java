import service.FileBackedTasksManager;
import service.HttpTaskManager;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

public class Main {
    public static void main(String[] args) {

        System.out.println("-----------------------------------------");
        //Авто тест InMemoryTaskManager
        TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        Managers.getSimpleTestForTaskManager(taskManager);
        System.out.println("\nАВТО ТЕСТ InMemoryTaskManager завершен");
        System.out.println("-----------------------------------------");

        //Авто тест FileBackedTasksManager
        FileBackedTasksManager.main(args);
        System.out.println("-----------------------------------------");

        //Авто тест HttpTaskManager
        HttpTaskManager.main(args);
        System.out.println("-----------------------------------------");
    }
}


