package jm.tp.jpashop.pt.exception;

public class NotExitOrderException extends NullPointerException {

    public static String ERROR_MESSAGE = "Not exit order error";

    public NotExitOrderException() {
        super();
    }

    public NotExitOrderException(String s) {
        super(s);
    }

    @Override public String getMessage() {
        return super.getMessage();
    }
}
