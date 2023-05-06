package exception;

public class ParentNotFound extends RuntimeException {
    public ParentNotFound(int message) {
        super(Integer.toString(message));
    }
}