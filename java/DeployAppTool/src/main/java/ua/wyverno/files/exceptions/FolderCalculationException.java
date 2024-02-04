package ua.wyverno.files.exceptions;

public class FolderCalculationException extends RuntimeException {
    public FolderCalculationException() {
        super();
    }

    public FolderCalculationException(String message) {
        super(message);
    }

    public FolderCalculationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FolderCalculationException(Throwable cause) {
        super(cause);
    }

    protected FolderCalculationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
