package server.utility.exceptions;

public class CannotConfirmEmailException extends RuntimeException {
    public CannotConfirmEmailException() {
    }

    public CannotConfirmEmailException(String message) {
        super(message);
    }


    public CannotConfirmEmailException(String exMessage, Exception exception) {
        super(exMessage, exception);
    }

}
