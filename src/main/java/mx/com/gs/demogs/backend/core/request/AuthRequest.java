package mx.com.gs.demogs.backend.core.request;

import lombok.Data;

@Data
public class AuthRequest {

	private Long numeroEmpleado;

	private String contrasenia;

	private String deviceId;

	private String deviceFingerprint;

	private String tipoDispositivo;

	private String sistemaOperativo;

	private String navegador;
}
