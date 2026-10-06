package mx.com.gs.demogs.backend.core.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mx.com.gs.demogs.backend.core.model.Usuario;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.service.IUsuarioService;

@RestController
@RequestMapping("/v1/core/usuario")
@RequiredArgsConstructor
public class UsuarioController {

	private final IUsuarioService usuarioService;

	@GetMapping
	public ResponseEntity<ApiResponse<Map<String, Object>>> buscarUsuario() {

		return usuarioService.buscarUsuario();
	}

	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorId(@PathVariable Long id) {

		return usuarioService.buscarPorId(id);
	}

	@GetMapping("/empleado/{numeroEmpleado}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerPorNumeroEmpleado(
			@PathVariable Long numeroEmpleado) {

		return usuarioService.obtenerPorNumeroEmpleado(numeroEmpleado);
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Map<String, Object>>> crear(@RequestBody Usuario usuario) {

		return usuarioService.crear(usuario);
	}

	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> actualizar(@PathVariable Long id,
			@RequestBody Usuario usuario) {

		return usuarioService.actualizar(usuario, id);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> eliminar(@PathVariable Long id) {

		return usuarioService.eliminar(id);
	}
}
