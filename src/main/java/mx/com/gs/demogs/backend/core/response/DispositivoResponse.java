package mx.com.gs.demogs.backend.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DispositivoResponse {

	private boolean dispositivoAutorizado;

	private boolean requiereActivacion;

	private String mensaje;
}