package mx.com.gs.demogs.backend.core.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import mx.com.gs.demogs.backend.core.dto.UsuarioDto;
import mx.com.gs.demogs.backend.core.response.ApiResponse;

public interface IUsuarioService {

	ResponseEntity<ApiResponse<Map<String, Object>>> buscarUsuario();

	ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorId(Long id);

	ResponseEntity<ApiResponse<Map<String, Object>>> crear(UsuarioDto usuarioDto);

	ResponseEntity<ApiResponse<Map<String, Object>>> actualizar(UsuarioDto usuarioDto, Long id);

	ResponseEntity<ApiResponse<Map<String, Object>>> eliminar(Long id);

	ResponseEntity<ApiResponse<Map<String, Object>>> obtenerPorNumeroEmpleado(Long numeroEmpleado);
}