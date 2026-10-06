package mx.com.gs.demogs.backend.exception;

public class RefreshTokenExpiredException
        extends RuntimeException {

    public RefreshTokenExpiredException(
            String message) {

        super(message);
    }
}