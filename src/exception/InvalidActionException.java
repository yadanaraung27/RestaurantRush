package src.exception;

public class InvalidActionException extends Exception {
    private static final long serialVersionUID = 1L;

    public InvalidActionException(String message) {
        super(message);
    }
}
