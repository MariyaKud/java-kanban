
import service.FileBackedTasksManager;
import service.Managers;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("-----------------------------------------");
        //Авто тест InMemoryTaskManager
        TaskManager taskManager = Managers.getDefault();
        Managers.simpleTestForTaskManager(taskManager);
        System.out.println("\nАВТО ТЕСТ InMemoryTaskManager завершен");
        System.out.println("-----------------------------------------");

        //Авто тест FileBackedTasksManager
        FileBackedTasksManager.main(args);
        System.out.println("-----------------------------------------");
    }
}


