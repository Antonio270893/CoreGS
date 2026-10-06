
package mx.com.gs.demogs.backend.core.service;

public interface IAuditoriaService {

	void registrar(String accion, String tabla, Long registroId, String datosAnteriores, String datosNuevos);

}
