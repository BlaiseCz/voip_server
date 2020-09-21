package server.utility.exceptions;

public class CannotLogInException extends RuntimeException {
    public CannotLogInException(String message) {
        super(message);
    }
}
