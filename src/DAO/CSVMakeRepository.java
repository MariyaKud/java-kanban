package DAO;

import model.Epic;
import model.Issue;
import model.SubTask;
import model.Task;
import service.TaskManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class CSVMakeRepository implements IssueRepository {

    final static String MSG_ENUM = "Не корректное имя для перечисления";

    private final File file;

    public CSVMakeRepository(File file) {
        this.file = file;
    }

    @Override
    public TaskManager load(TaskManager tracker) {

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file.getName(),
                StandardCharsets.UTF_8))) {

            //Читаем задачи
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if  (!line.isEmpty()) {
                    Issue issue = SerializerIssue.issueFromString(line);
                    if (issue != null) {
                        switch(issue.getType()){
                            case TASK:
                                tracker.addTask((Task) issue);
                                break;

                            case EPIC:
                                tracker.addEpic((Epic) issue);
                                break;

                            case SUBTASK:
                                tracker.addSubTask((SubTask) issue);
                                break;

                            default:
                                System.out.println(MSG_ENUM);
                        }
                    }
                } else {
                    break; // пустая строка означает, что задачи закончились
                }
            }

            //Читаем историю
            if (fileReader.ready()) {
                String line = fileReader.readLine();
                if  (!line.isEmpty()) {
                    List<Integer> historyID = SerializerIssue.historyFromString(line.trim());
                    for (Integer id : historyID) {
                        if (tracker.getTaskById(id) == null) {
                            if (tracker.getSubTaskById(id) == null) {
                                tracker.getEpicById(id);
                            }
                        }
                    }
                }
            }

        } catch (FileNotFoundException e) {
            System.out.println("Файл с данными не найден, запущен новый менеджер без истории.");
            return tracker;
        } catch (IOException e) {
            System.out.println("Произошла ошибка во время чтения файла, запущен новый менеджер без истории.");
            return tracker;
        }
        return tracker;
    }

    @Override
    public void save(TaskManager tracker) throws ManagerSaveException {
        //Проверяем наличие файла менеджера задач. Если его нет, то пытаемся создать
        if (!file.exists()) {
            try {
                boolean fileExists = file.createNewFile();
                if (!fileExists) {
                    throw new ManagerSaveException("fileNotExists");
                }
            } catch (IOException exception) {
                throw new ManagerSaveException("IOException");
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file.getName(), StandardCharsets.UTF_8))) {

            for (Task value : tracker.getAllTasks()) {
                writer.write(SerializerIssue.issueToString(value));
            }
            for (Epic value : tracker.getAllEpics()) {
                writer.write(SerializerIssue.issueToString(value));
            }
            for (SubTask value : tracker.getAllSubTasks()) {
                writer.write(SerializerIssue.issueToString(value));
            }
            writer.newLine();
            writer.write(SerializerIssue.historyToString(tracker.getHistory()));

        } catch (IOException e) {
            throw new ManagerSaveException("IOException");
        }
    }
}
