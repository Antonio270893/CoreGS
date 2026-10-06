package mx.com.gs.demogs.backend.core.request;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class CorreoRequest {

	private List<String> para;

	private List<String> cc;

	private List<String> cco;

	private String asunto;

	private String referenciaPlantilla;

	private Map<String, Object> datos;

	private String contenido;
}