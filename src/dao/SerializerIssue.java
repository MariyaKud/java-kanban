package dao;

import model.Epic;
import model.Issue;
import model.IssueStatus;
import model.IssueType;
import model.SubTask;
import model.Task;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class SerializerIssue {

    private SerializerIssue() {
    }

    /**
     * Преобразует задачу в строку, для выгрузки в файл.
     *
     * @param issue - задача для преобразования
     * @return строка, созданная по правилу id,type,name,status,description,epic
     */
    static String issueToString(Issue issue) {
        //"id,type,name,status,description,duration,startTime,epic";
        StringBuilder result = new StringBuilder();
        result.append(issue.getId()).append(",");
        result.append(issue.getType()).append(",");
        result.append(issue.getTitle()).append(",");
        result.append(issue.getStatus()).append(",");
        result.append(issue.getDescription()).append(",");
        result.append(issue.getDuration()).append(",");
        if (issue.getStartTime() != Instant.MAX) {
            result.append(issue.getStartTime());
        }
        result.append(",");
        //Для подзадачи нужен эпик
        if (issue.getType() == IssueType.SUBTASK) {
            result.append(((SubTask) issue).getParentID());
        }
        result.append("\n");

        return result.toString();
    }

    /**
     * Создает задачу по строковому представлению задачи.
     * Правило представления задачи:id,type,name,status,description,epic
     *
     * @param value строковое представление задачи
     * @return экземпляр классов {@code Task},{@code SubTask},{@code Epic}, собранная по строке
     */
    static Issue stringToIssue(String value) {
        //Разбираем строку: id,type,name,status,description,duration,startTime,epic
        String[] split = value.trim().split(",");

        final int id;
        int idParent = 0;
        IssueType type = IssueType.TASK;
        final String name;
        IssueStatus status = IssueStatus.NEW;
        final String description;
        int duration = 0;
        final String startTimeStr;
        Instant startTime = Instant.MAX;

        //КРИТИЧНО: id задачи
        if (split.length > 0) {
            try {
                id = Integer.parseInt(split[0]);
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                System.out.println("Задача с id = " + split[0] + " не загружена!");
                return null;
            }
        } else {
            return null;
        }

        //КРИТИЧНО: id родителя подзадачи
        if (split.length > 7) {
            type = IssueType.SUBTASK;
            try {
                idParent = Integer.parseInt(split[7].trim());
            } catch (NumberFormatException e) {
                System.out.println(e.getMessage());
                System.out.println("Задача с id = " + split[0] + "не загружена!");
                System.out.println("Некорректный id родителя = " + split[0]);
                return null;
            }
        }

        //Все остальное не критично, можно поставить значения по дефолту
        //Тип задачи
        if (split.length > 1) {
            try {
                type = IssueType.valueOf(split[1]);
            } catch (IllegalArgumentException ignored) {
            }
        }
        //Имя задачи
        if (split.length > 2) {
            name = split[2].trim();
        } else {
            name = "";
        }
        //Статус
        if (split.length > 3) {
            try {
                status = IssueStatus.valueOf(split[3]);
            } catch (IllegalArgumentException ignored) {
            }
        }
        //Описание
        if (split.length > 4) {
            description = split[4].trim();
        } else {
            description = "";
        }
        //Длительность задачи в минутах
        if (split.length > 5) {
            try {
                duration = Integer.parseInt(split[5].trim());
            } catch (NumberFormatException ignored) {
            }
        }
        //Время начала задачи
        if (split.length > 6) {
            startTimeStr = split[6].trim();
        } else {
            startTimeStr = "";
        }

        try {
            startTime = Instant.parse(startTimeStr);
        } catch (DateTimeParseException ignored) {
        }

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
     *
     * @param history - список просмотренных задач, экземпляры классов {@code Task},{@code SubTask},{@code Epic}
     * @return строковое представление списка - идентификаторы задач, разделенные запятой
     */
    static String historyToString(List<Issue> history) {
        //id задач в порядке просмотра
        StringBuilder result = new StringBuilder();
        history.forEach(h -> result.append(h.getId()).append(","));
        if (result.length() > 0) {
            result.deleteCharAt(result.lastIndexOf(","));
        }
        return result.toString();
    }

    /**
     * Разбор строки истории задач в список идентификаторов просмотренных задач
     *
     * @param value строка истории просмотров, идентификаторы задач, разделенные запятой
     * @return список идентификаторов просмотренных задач
     */
    static List<Integer> stringToHistory(String value) {
        List<Integer> history = new ArrayList<>();
        String[] split = value.trim().split(",");
        int id;

        for (String s : split) {
            try {
                id = Integer.parseInt(s);
                history.add(id);
            } catch (NumberFormatException e) {
                if (!"".equals(s.trim())) {
                    System.out.println(e.getMessage());
                    System.out.println("Не получилось восстановить историю по id = " + s);
                }
            }
        }
        return history;
    }
}
