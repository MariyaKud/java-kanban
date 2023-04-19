package DAO;

import service.TaskManager;

/**
 * Интерфейс для сохранения/загрузки данных менеджера в/из файла
 */
public interface IssueRepository {

    /**
     * Загрузить задачи и историю просмотров из файла в менеджер
     * @param tracker - новый экземпляр менеджер задач, поддерживающий контракт {@link TaskManager}
     * @return менеджер задач, полученный на входе, с загруженными данными из файла
     */
    TaskManager load(TaskManager tracker);

    /**
     * Сохранить задачи и историю просмотров задач в файл
     * @param tracker - менеджер задач, поддерживающий контракт {@link TaskManager}
     * @throws ManagerSaveException возникает, если не получилось записать данных менеджера в файл
     */
    void save(TaskManager tracker) throws ManagerSaveException;
}
