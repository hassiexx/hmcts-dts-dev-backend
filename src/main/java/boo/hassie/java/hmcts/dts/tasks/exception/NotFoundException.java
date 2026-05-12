package boo.hassie.java.hmcts.dts.tasks.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
        super("resource not found");
    }

    public NotFoundException(final String message) {
        super(message);
    }
}
