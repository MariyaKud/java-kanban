package exception;

public class StatusResponseMistake extends RuntimeException {
    public StatusResponseMistake(String message) {
        super(message);
    }
}