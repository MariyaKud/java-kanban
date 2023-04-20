package dao;

import service.TaskManager;

/**
 * Интерфейс для сохранения/загрузки данных менеджера в/из файла
 * Заголовок файла: id,type,name,status,description,epic
 */
public interface IssueRepository {

    /**
     * Загрузить задачи и историю просмотров из файла в менеджер
     * @param tracker - экземпляр менеджер задач, поддерживающий контракт {@link TaskManager} для загрузки данных
     */
    void load(TaskManager tracker);

    /**
     * Сохранить задачи и историю просмотров задач в файл
     * @param tracker - менеджер задач, поддерживающий контракт {@link TaskManager}
     */
    void save(TaskManager tracker);
}
