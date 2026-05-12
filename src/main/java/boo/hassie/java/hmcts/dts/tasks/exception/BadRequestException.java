package boo.hassie.java.hmcts.dts.tasks.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException() {
    }

    public BadRequestException(final String message) {
        super(message);
    }
}
