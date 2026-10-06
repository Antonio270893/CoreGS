package mx.com.gs.demogs.backend.core.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import mx.com.gs.demogs.backend.core.model.Dispositivo;
import mx.com.gs.demogs.backend.core.response.ApiResponse;

public interface IDispositivoService {

	ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorUsuarioYDeviceId(Long usuarioId, String deviceId);

	boolean estaAutorizado(Long usuarioId, String deviceId);

	ResponseEntity<ApiResponse<Map<String, Object>>> registrar(Dispositivo dispositivo);

	ResponseEntity<ApiResponse<Map<String, Object>>> actualizarUltimoAcceso(Long usuarioId, String deviceId);

	Dispositivo obtenerPorUsuarioYDeviceId(Long usuarioId, String deviceId);

	ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorUsuario(Long usuarioId);
	
	ResponseEntity<ApiResponse<Map<String, Object>>> eliminarDispositivo(Long usuarioId, String deviceId);
}