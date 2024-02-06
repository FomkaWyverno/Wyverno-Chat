package ua.wyverno.files.exceptions;

public class CloudFileTryCalculationException extends RuntimeException {
    public CloudFileTryCalculationException() {
        super();
    }

    public CloudFileTryCalculationException(String message) {
        super(message);
    }

    public CloudFileTryCalculationException(String message, Throwable cause) {
        super(message, cause);
    }

    public CloudFileTryCalculationException(Throwable cause) {
        super(cause);
    }

    protected CloudFileTryCalculationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
