package server.utility.exceptions;

public class CannotRenewTokenException extends RuntimeException {
    public CannotRenewTokenException() {
    }

    public CannotRenewTokenException(String message) {
        super(message);
    }
}
