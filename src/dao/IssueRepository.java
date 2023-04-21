package dao;

import service.TaskManager;

import java.io.File;

/**
 * Интерфейс для сохранения/загрузки данных менеджера в/из файла
 * Заголовок файла: id,type,name,status,description,epic
 */
public interface IssueRepository {

    /**
     * Загрузить задачи и историю просмотров из файла в менеджер
     * @param tracker - экземпляр менеджер задач, поддерживающий контракт {@link TaskManager} для загрузки данных
     * @param file файл, из которого загружаем данные
     */
    void load(TaskManager tracker, File file);

    /**
     * Сохранить задачи и историю просмотров менеджера в файл
     * @param tracker менеджер задач, поддерживающий контракт {@link TaskManager}
     * @param file файл, в который сохраняем данные менеджера задач
     */
    void save(TaskManager tracker, File file);
}
