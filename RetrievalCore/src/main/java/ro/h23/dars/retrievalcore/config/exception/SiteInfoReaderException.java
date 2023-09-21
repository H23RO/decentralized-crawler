package ro.h23.dars.retrievalcore.config.exception;

public class SiteInfoReaderException extends Exception {
    public SiteInfoReaderException() {
        super();
    }

    public SiteInfoReaderException(String message) {
        super(message);
    }

    public SiteInfoReaderException(String message, Throwable cause) {
        super(message, cause);
    }

    public SiteInfoReaderException(Throwable cause) {
        super(cause);
    }

    protected SiteInfoReaderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
