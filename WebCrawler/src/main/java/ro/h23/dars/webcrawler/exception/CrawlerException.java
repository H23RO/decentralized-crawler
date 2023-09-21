package ro.h23.dars.webcrawler.exception;

public class CrawlerException extends Exception {

    public CrawlerException(String message) {
        super(message);
    }

    public CrawlerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CrawlerException(Throwable cause) {
        super(cause);
    }

    public CrawlerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
