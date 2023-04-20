package service;

import dao.CSVMakeRepository;
import dao.IssueRepository;

import java.io.File;

/**
 * Утилитарный класс <b>{@code Managers}</b> ответственный за получение дефолтных значений
 *
 * <p>Должен подобрать нужную реализацию:
 * <p> - объекта-менеджера {@code TaskManager}
 * <p> - объекта-история просмотров {@code HistoryManager}
 */
public class Managers {

    /**
     * Приватный конструктор для закрытия возможности создать объект.
     * Дефолтный конструктор класса - публичный, нам нужно его перекрыть,
     * т.к. все методы класса static объект нам не нужен.
     */
    private Managers() {
    }

    /**
     * Получить дефолтный объект-менеджера
     * @return  объект-менеджер
     */
    public static TaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    /**
     * Получить дефолтный объект-история просмотров
     * @return  объект-история просмотров
     */
    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    /**
     * Получить дефолтный объект-обмена менеджера с файлом csv
     * @return  объект-экземпляр поддерживающий контрактом {@code IssueRepository} для записи и чтения данных в файл csv
     */
    public static IssueRepository getDefaultIssueRepository() {
        final String DIR_HOME = System.getProperty("user.home");
        final String CSV_FILE_REPOSITORY = "taskManager.csv";
        File csvFileRepository = new File(DIR_HOME,CSV_FILE_REPOSITORY);

        return new CSVMakeRepository(csvFileRepository);
    }
}
