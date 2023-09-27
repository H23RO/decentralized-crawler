package ro.h23.dars.retrievalcore.config.exception;

public class ServerListReaderException extends Exception {
    public ServerListReaderException() {
        super();
    }

    public ServerListReaderException(String message) {
        super(message);
    }

    public ServerListReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ServerListReaderException(Throwable cause) {
        super(cause);
    }

    protected ServerListReaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
