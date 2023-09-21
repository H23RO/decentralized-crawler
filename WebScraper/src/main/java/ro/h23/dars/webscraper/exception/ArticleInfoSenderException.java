package ro.h23.dars.webscraper.exception;

public class ArticleInfoSenderException extends Exception {

    public ArticleInfoSenderException(String message) {
        super(message);
    }

    public ArticleInfoSenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArticleInfoSenderException(Throwable cause) {
        super(cause);
    }

    public ArticleInfoSenderException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
