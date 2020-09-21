package server.utility.exceptions;

public class CannotConfirmEmailException extends RuntimeException {
    public CannotConfirmEmailException() {
    }

    public CannotConfirmEmailException(String message) {
        super(message);
    }
}
