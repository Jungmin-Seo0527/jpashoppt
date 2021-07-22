package jm.tp.jpashop.pt.exception;

public class NotExitMemberException extends NotExit {

    public static String ERROR_MESSAGE = "400 error Not exit member";

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
