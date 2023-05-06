package exception;

public class NotValidate extends RuntimeException {
    public NotValidate(String message) {
        super(message);
    }
}