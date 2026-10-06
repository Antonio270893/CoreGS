package mx.com.gs.demogs.backend.core.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import mx.com.gs.demogs.backend.core.model.Modulo;
import mx.com.gs.demogs.backend.core.response.ApiResponse;

public interface IModuloService {

	ResponseEntity<ApiResponse<Map<String, Object>>> buscarModulos();

	ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorId(Long id);

	ResponseEntity<ApiResponse<Map<String, Object>>> crear(Modulo modulo);

	ResponseEntity<ApiResponse<Map<String, Object>>> actualizar(Modulo modulo, Long id);

	ResponseEntity<ApiResponse<Map<String, Object>>> eliminar(Long id);
}