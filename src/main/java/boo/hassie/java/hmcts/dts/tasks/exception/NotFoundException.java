package boo.hassie.java.hmcts.dts.tasks.exception;

public class NotFoundException extends RuntimeException {
    public NotFoundException() {
    }

    public NotFoundException(final String message) {
        super(message);
    }
}
