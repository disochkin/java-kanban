package controllers;

public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(final String message) {
        super(message);
    }

    public String getMessages() {
        return getMessage();
    }
}
