package DAO;

import service.TaskManager;

/**
 * Интерфейс для сохранения/загрузки данных менеджера в/из файла
 */
public interface IssueRepository {

    /**
     * Загрузить задачи и историю просмотров из файла в менеджер
     * @param tracker - менеджер, работающий с файлами, в который нужно загрузить данные из файла
     * @return менеджер с загруженными данными из файла
     */
    TaskManager load(TaskManager tracker);

    /**
     * Сохранить задачи историю просмотров задач в файл
     * @param tracker - менеджер задач, поддерживающий контракт {@link TaskManager}
     * @throws ManagerSaveException исключение вида IOException
     */
    void save(TaskManager tracker) throws ManagerSaveException;
}
