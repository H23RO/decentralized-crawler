package ro.h23.dars.webscraper.exception;

public class ArticleInfoSaverException  extends Exception {

    public ArticleInfoSaverException(String message) {
        super(message);
    }

    public ArticleInfoSaverException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArticleInfoSaverException(Throwable cause) {
        super(cause);
    }

    public ArticleInfoSaverException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
