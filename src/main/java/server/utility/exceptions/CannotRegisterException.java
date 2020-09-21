package server.utility.exceptions;

public class CannotRegisterException extends RuntimeException {
    public CannotRegisterException() {
    }

    public CannotRegisterException(String message) {
        super(message);
    }
}
