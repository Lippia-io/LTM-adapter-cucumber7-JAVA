package ltm.exceptions;

public class TestManagerException extends RuntimeException {
    public TestManagerException(String message) {
        super(message);
    }

    public TestManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestManagerException(Throwable cause) {
        super(cause);
    }

    public TestManagerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
