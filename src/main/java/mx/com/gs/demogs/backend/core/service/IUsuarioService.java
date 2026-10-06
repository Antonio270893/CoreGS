package mx.com.gs.demogs.backend.core.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import mx.com.gs.demogs.backend.core.model.Usuario;
import mx.com.gs.demogs.backend.core.response.ApiResponse;

public interface IUsuarioService {

	ResponseEntity<ApiResponse<Map<String, Object>>> buscarUsuario();

	ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorId(Long id);

	ResponseEntity<ApiResponse<Map<String, Object>>> crear(Usuario usuario);

	ResponseEntity<ApiResponse<Map<String, Object>>> actualizar(Usuario usuario, Long id);

	ResponseEntity<ApiResponse<Map<String, Object>>> eliminar(Long id);

	ResponseEntity<ApiResponse<Map<String, Object>>> obtenerPorNumeroEmpleado(Long numeroEmpleado);
}