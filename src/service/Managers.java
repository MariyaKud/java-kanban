package service;

/**
 * Утилитарный класс {@code Managers} ответственный за создание менеджера задач
 *
 * <p>Должен подобрать нужную реализацию {@code TaskManager}
 */
public class Managers<T extends TaskManager> {

    private T tracker;

    /**
     * Получить реализацию интерфейса {@link TaskManager}
     * @return  объект-менеджер {@code TaskManager}
     */
    public T getDefault() {
        return tracker;
    }
}
