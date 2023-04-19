package DAO;

/**
 * Исключение вида IOException
 */
public class ManagerSaveException extends Exception {
    public ManagerSaveException(String message) {
        super(message);
    }
}
