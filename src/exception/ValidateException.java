package exception;

/**
 * Исключение валидации задачи, регистрирует факт пересечения задач на оси времени
 */
public class ValidateException extends RuntimeException {
    public ValidateException(String message) {
        super(message);
    }
}
