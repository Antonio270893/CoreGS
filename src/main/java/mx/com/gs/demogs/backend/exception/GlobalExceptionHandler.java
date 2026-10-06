package mx.com.gs.demogs.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.response.MetadataResponse;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {

		log.warn("Credenciales inválidas");

		return crearError(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException ex) {

		log.warn(ex.getMessage());

		return crearError(HttpStatus.BAD_REQUEST, ex.getMessage());
	}

	@ExceptionHandler(RefreshTokenRevokedException.class)
	public ResponseEntity<ApiResponse<Void>> handleRefreshTokenRevoked(RefreshTokenRevokedException ex) {

		log.warn(ex.getMessage());

		return crearError(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	@ExceptionHandler(RefreshTokenExpiredException.class)
	public ResponseEntity<ApiResponse<Void>> handleRefreshTokenExpired(RefreshTokenExpiredException ex) {

		log.warn(ex.getMessage());

		return crearError(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	@ExceptionHandler(RefreshTokenNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleRefreshTokenNotFound(RefreshTokenNotFoundException ex) {

		log.warn(ex.getMessage());

		return crearError(HttpStatus.UNAUTHORIZED, ex.getMessage());
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {

		return crearError(HttpStatus.NOT_FOUND, ex.getMessage());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {

		log.error("Error interno no controlado", ex);

		return crearError(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
	}

	private ResponseEntity<ApiResponse<Void>> crearError(HttpStatus status, String mensaje) {

		MetadataResponse metadata = new MetadataResponse("ERROR", String.valueOf(status.value()), mensaje);

		ApiResponse<Void> response = new ApiResponse<>(metadata, null);

		return ResponseEntity.status(status).body(response);
	}
}