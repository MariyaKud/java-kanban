package dao;

import dao.IssueRepository;
import dao.SerializerIssue;
import exception.ManagerSaveException;
import model.Epic;
import model.Issue;
import model.SubTask;
import model.Task;

import service.FileBackedTasksManager;
import service.TaskManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Универсальный класс сохранения/загрузки данных менеджера задач, поддерживающих контракт {@link TaskManager}
 * в CSV файл
 *
 * <p> В файл записываются задачи, эпики, подзадачи:
 * id, type, name, status, description, duration, startTime, epic
 * <p> Пустая строка
 * <p> Идентификаторы просмотренных задач через ","
 */
public final class CsvMakeRepository implements IssueRepository {

    /**
     * Загрузить задачи и историю просмотров из файла в менеджер
     * @param tracker - менеджер, работающий с файлами, в который нужно загрузить данные из файла
     * @param file файл, из которого загружаем данные
     */
    @Override
    public void load(TaskManager tracker, File file) {

        try (BufferedReader fileReader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            //Пропускаем заголовок
            fileReader.readLine();

            //Читаем задачи
            while (fileReader.ready()) {
                String line = fileReader.readLine();
                if  (!line.isEmpty()) {
                    Issue issue = SerializerIssue.stringToIssue(line);
                    if (issue != null) {
                        switch(issue.getType()){
                            case TASK:
                                ((FileBackedTasksManager) tracker).addTaskWithId((Task) issue);
                                break;

                            case EPIC:
                                ((FileBackedTasksManager) tracker).addEpicWithId((Epic) issue);
                                break;

                            case SUBTASK:
                                ((FileBackedTasksManager) tracker).addSubTaskWithId((SubTask) issue);
                                break;

                            default:
                                System.out.println(MSG_ENUM);
                        }
                    }
                } else {
                    // пустая строка означает, что задачи закончились
                    break;
                }
            }

            //Читаем историю
            if (fileReader.ready()) {
                String line = fileReader.readLine();
                if  (!line.isEmpty()) {
                    List<Integer> historyID = SerializerIssue.stringToHistory(line.trim());

                    for (Integer id : historyID) {
                        if (tracker.getTaskById(id) == null) {
                            if (tracker.getSubTaskById(id) == null) {
                                tracker.getEpicById(id);
                            }
                        }
                    }

                }
            }

        } catch (IOException  e) {
            System.out.println("Произошла ошибка во время чтения файла:");
            System.out.println(e.getMessage());
            System.out.println("Запущен новый менеджер без истории.");
        }
    }

    /**
     * Сохранить задачи историю просмотров задач в файл
     * @param tracker - менеджер задач, поддерживающий контракт {@link TaskManager}
     * @param file файл, в который сохраняем данные менеджера задач
     * @throws ManagerSaveException при ошибке записи данных в csv-файл
     */
    @Override
    public void save(TaskManager tracker, File file) throws ManagerSaveException {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8))) {

            writer.write(FILE_HEAD);

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
            throw new ManagerSaveException(e.getMessage());
        }
    }
}
