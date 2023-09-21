package ro.h23.dars.retrievalcore.service.store;

public class ArticleStoreServiceException  extends Exception {

    public ArticleStoreServiceException() {
        super();
    }

    public ArticleStoreServiceException(String message) {
        super(message);
    }

    public ArticleStoreServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArticleStoreServiceException(Throwable cause) {
        super(cause);
    }

    protected ArticleStoreServiceException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
