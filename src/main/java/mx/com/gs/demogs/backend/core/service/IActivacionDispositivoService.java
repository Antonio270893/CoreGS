package mx.com.gs.demogs.backend.core.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import mx.com.gs.demogs.backend.core.response.ApiResponse;

public interface IActivacionDispositivoService {

	ResponseEntity<ApiResponse<Map<String, Object>>> generar(Long dispositivoId);

	ResponseEntity<ApiResponse<Map<String, Object>>> activarPorToken(String token);

	ResponseEntity<ApiResponse<Map<String, Object>>> activarPorCodigo(String codigo);

	ResponseEntity<ApiResponse<Map<String, Object>>> generarPorLogin(Long numeroEmpleado, String deviceId);
}