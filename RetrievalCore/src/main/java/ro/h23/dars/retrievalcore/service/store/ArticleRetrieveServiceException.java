package ro.h23.dars.retrievalcore.service.store;

public class ArticleRetrieveServiceException extends Exception {

    public ArticleRetrieveServiceException() {
        super();
    }

    public ArticleRetrieveServiceException(String message) {
        super(message);
    }

    public ArticleRetrieveServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArticleRetrieveServiceException(Throwable cause) {
        super(cause);
    }

    protected ArticleRetrieveServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
