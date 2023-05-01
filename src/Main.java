
import model.IssueStatus;
import model.Task;
import service.FileBackedTasksManager;
import service.InMemoryTaskManager;
import service.Managers;
import service.TaskManager;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {
        //FileBackedTasksManager.main(args);

        TaskManager taskManager = new InMemoryTaskManager(Managers.getDefaultHistory());
        LocalDateTime localDateTime = LocalDateTime.now();

        System.out.println("\nЗАПУЩЕН АВТО ТЕСТ.");

        Task newTask1 = new Task(0, "Test", "Description", Duration.ofMinutes(10));
        if (taskManager.addTask(newTask1) != null) {
            System.out.println("Добавлена задача: " + newTask1);
        }
    }
}


