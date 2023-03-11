package service;

/**
 * Утилитарный класс <b>{@code Managers}</b> ответственный за получение дефолтных значений
 *
 * <p>Должен подобрать нужную реализацию:
 * <p> - объекта-менеджера {@code TaskManager}
 * <p> - объекта-история просмотров {@code HistoryManager}
 */
public class Managers {

    /**
     * Получить дефолтный объект-менеджера
     * @return  объект-менеджер
     */
    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    /**
     * Получить дефолтный объект-история просмотров
     * @return  объект-история просмотров
     */
    public static InMemoryHistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
