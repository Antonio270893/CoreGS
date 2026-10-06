package mx.com.gs.demogs.backend.exception;

public class RefreshTokenRevokedException extends RuntimeException {

	public RefreshTokenRevokedException(String message) {

		super(message);
	}
}