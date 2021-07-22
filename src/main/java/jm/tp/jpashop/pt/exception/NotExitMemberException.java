package jm.tp.jpashop.pt.exception;

public class NotExitMemberException extends NotExit {
    public NotExitMemberException() {
        super();
    }

    public NotExitMemberException(String message) {
        super(message);
    }

    public NotExitMemberException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotExitMemberException(Throwable cause) {
        super(cause);
    }
}
