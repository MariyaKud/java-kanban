package dao;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class SerializerIssue {

    private SerializerIssue() {
    }

    /**
     * Сериализует задачу в строку, для выгрузки в файл.
     * @param issue - задача для сериализации
     * @return строка, созданная по правилу id,type,name,status,description,epic
     */
    static String issueToString(Issue issue) {
        //"id,type,name,status,description,duration,startTime,epic";
        StringBuilder result = new StringBuilder();
        result.append(issue.getId()).append(",").append(issue.getType()).append(",");
        result.append(issue.getTitle()).append(",").append(issue.getStatus()).append(",");
        result.append(issue.getDescription()).append(",");
        result.append(issue.getDuration()).append(",").append(issue.getStartTime()).append(",");

        if (issue.getType() == IssueType.SUBTASK) {
            result.append(((SubTask) issue).getParentID());
        }
        result.append("\n");

        return result.toString();
    }

    /**
     * Создает задачу по строковому представлению задачи.
     * Правило представления задачи:id,type,name,status,description,epic
     * @param value строковое представление задачи
     * @return экземпляр классов {@code Task},{@code SubTask},{@code Epic}, собранная по строке
     */
    static Issue issueFromString(String value) {
        String[] split = value.trim().split(",");

        //Разбираем строку: id,type,name,status,description,epic
        int id;
        //Критично
        try {
            id = Integer.parseInt(split[0]);
        } catch (NumberFormatException e) {
            System.out.println(e.getMessage());
            System.out.println("Задача с id = " + split[0] + " не загружена!");
            return null;
        }

        int idParent = 0;
        if (split.length > 5) {
            try {
                idParent = Integer.parseInt(split[5].trim());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                System.out.println("Задача с id = " + split[0] + "не загружена!");
                System.out.println("Некорректный id родителя = " + split[0]);
                return null;
            }
        }
        //Не критично
        IssueStatus status = IssueStatus.NEW;
        try {
            status = IssueStatus.valueOf(split[3]);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        IssueType type = IssueType.TASK;
        try {
            type = IssueType.valueOf(split[1]);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }

        String name = split[2].trim();
        String description = split[4].trim();

        Duration duration  = Duration.ZERO;
        ZonedDateTime startTime = ZonedDateTime.now();

        switch (type) {
            case TASK:
                return new Task(id, name, description, duration, startTime, status);

            case EPIC:
                return new Epic(id, name, description);

            case SUBTASK:
                return new SubTask(id, name, description, duration, startTime, idParent, status);

            default:
                System.out.println("Переданы не корректные данные. По ним нельзя собрать задачу любого типа.");
                return null;
        }
    }

    /**
     * Сериализация истории просмотров задач
     * @param history - список просмотренных задач, экземпляры классов {@code Task},{@code SubTask},{@code Epic}
     * @return строковое представление списка - идентификаторы задач, разделенные запятой
     */
    static String historyToString(List<Issue> history) {
        //id задач в порядке просмотра
        StringBuilder result = new StringBuilder();
        int counter = 1;

        for (Issue issue : history) {
            result.append(issue.getId());
            if (counter++ < history.size()) {
                result.append(",");
            }
        }

        return result.toString();
    }

    /**
     * Разбор сериализованной истории задач в список идентификаторов просмотренных задач
     * @param value - сериализованная строка истории просмотров, идентификаторы задач, разделенные запятой
     * @return список идентификаторов просмотренных задач
     */
    static List<Integer> historyFromString(String value) {
        List<Integer> history = new ArrayList<>();
        String[] split = value.trim().split(",");
        int id;

        for (String s : split) {
            try {
                id = Integer.parseInt(s);
                history.add(id);
            }  catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                System.out.println("Не получилось восстановить историю по id = " + s);
            }
        }

        return history;
    }
}
