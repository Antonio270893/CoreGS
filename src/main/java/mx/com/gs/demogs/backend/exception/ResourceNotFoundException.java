package mx.com.gs.demogs.backend.exception;

public class ResourceNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9062197036875050607L;

	public ResourceNotFoundException(String mensaje) {

		super(mensaje);
	}
}