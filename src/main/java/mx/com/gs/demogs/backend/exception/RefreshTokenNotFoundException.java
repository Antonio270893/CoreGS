package mx.com.gs.demogs.backend.exception;

public class RefreshTokenNotFoundException
        extends RuntimeException {

    public RefreshTokenNotFoundException(
            String message) {

        super(message);
    }
}