
import model.Epic;
import model.IssueStatus;
import model.SubTask;
import model.Task;
import service.FileBackedTasksManager;
import service.Managers;
import service.TaskManager;

import java.time.Duration;

public class Main {

    public static void main(String[] args) {

        //Авто тест InMemoryTaskManager
        TaskManager taskManager = Managers.getDefault();

        System.out.println("\nЗАПУЩЕН АВТО ТЕСТ InMemoryTaskManager.");

        Task newTask1 = new Task(0, "Test", "Description", Duration.ofMinutes(10));
        if (taskManager.addTask(newTask1) != null) {
            System.out.println("Добавлена задача: " + newTask1);
        }

        Task newTask2 = new Task(0, "Task2", "Description", Duration.ofMinutes(20));
        if (taskManager.addTask(newTask2) != null) {
            System.out.println("Добавлена задача: " + newTask2);
        }

        newTask2.setStatus(IssueStatus.DONE);
        if (taskManager.updateTask(newTask2) != null) {
            System.out.println("Установлен статус 'DONE' для задачи: " + newTask2);
            System.out.println("Добавили в историю задачу: " + newTask2);
        }

        Epic newEpic = new Epic(0, "Epic1", "Description");
        if (taskManager.addEpic(newEpic) != null) {
            System.out.println("Добавлен эпик: " + newEpic);
        }

        SubTask newSubTask1 = new SubTask(0, "SubTask1", "Description", Duration.ofMinutes(15),
                                            newEpic.getId());
        if (taskManager.addSubTask(newSubTask1) != null) {
            System.out.println("Добавлена подзадача: " + newSubTask1);
        }
        newSubTask1.setStatus(IssueStatus.DONE);
        if (taskManager.updateSubTask(newSubTask1)  != null) {
            System.out.println("Установлен статус 'DONE' для подзадачи: " + newSubTask1);
            System.out.println("Добавили в историю подзадачу: " + newSubTask1);
        }

        SubTask newSubTask2 = new SubTask(0, "SubTask2", "Description", Duration.ofMinutes(15),
                                             newEpic.getId());
        if (taskManager.addSubTask(newSubTask2) != null) {
            System.out.println("Добавлена подзадача: " + newSubTask2);
        }
        if (taskManager.getSubTaskById(newSubTask2.getId()) != null) {
            System.out.println("Добавили в историю подзадачу: " + newSubTask2 + "\n");
        }

        System.out.println("Состояние менеджера задач:");
        taskManager.getAllTasks().forEach(System.out::println);
        taskManager.getAllEpics().forEach(System.out::println);
        System.out.println("\nОтсортированный список задач по дате старта:");
        taskManager.getPrioritizedTasks().forEach(System.out::println);
        System.out.println("\nАВТО ТЕСТ InMemoryTaskManager завершен");

        System.out.println("-----------------------------------------");

        //Авто тест FileBackedTasksManager
        FileBackedTasksManager.main(args);
    }
}


