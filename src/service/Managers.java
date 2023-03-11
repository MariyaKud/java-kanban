package service;

/**
 * Утилитарный класс {@code Managers} ответственный за создание менеджера задач
 *
 * <p>Должен подобрать нужную реализацию {@code TaskManager}
 */
public class Managers {

    private final InMemoryTaskManager tracker = new InMemoryTaskManager();


    /**
     * Получить реализацию интерфейса {@link TaskManager}
     * @return  объект-менеджер {@code TaskManager}
     */
    public InMemoryTaskManager getDefault() {
        return tracker;
    }
}
