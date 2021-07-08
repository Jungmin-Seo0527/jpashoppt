package jm.tp.jpashop.pt.exception;

public class NotExit extends RuntimeException{
    public NotExit() {
        super();
    }

    public NotExit(String message) {
        super(message);
    }

    public NotExit(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExit(Throwable cause) {
        super(cause);
    }

    protected NotExit(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
