package tech.firas.framework.fileimport.processor.db;

public class ValidationException extends Exception {

    public ValidationException(final String message) {
        super(message);
    }

    public ValidationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
