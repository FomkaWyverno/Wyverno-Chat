package ua.wyverno.dropbox.exceptions;

public class UnknownMetadataTypeException extends RuntimeException {
    public UnknownMetadataTypeException() {
        super();
    }

    public UnknownMetadataTypeException(String message) {
        super(message);
    }

    public UnknownMetadataTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownMetadataTypeException(Throwable cause) {
        super(cause);
    }

    protected UnknownMetadataTypeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
