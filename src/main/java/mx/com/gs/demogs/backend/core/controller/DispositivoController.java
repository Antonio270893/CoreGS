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
import lombok.extern.slf4j.Slf4j;

import mx.com.gs.demogs.backend.core.dto.DispositivoDto;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.service.IDispositivoService;

@RestController
@RequestMapping("/v1/core/dispositivo")
@RequiredArgsConstructor
@Slf4j
public class DispositivoController {

	private final IDispositivoService dispositivoService;

	@GetMapping("/usuario/{usuarioId}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorUsuario(@PathVariable Long usuarioId) {

		log.info("Consultando dispositivos del usuario: {}", usuarioId);

		return dispositivoService.buscarPorUsuario(usuarioId);
	}

	@GetMapping("/{usuarioId}/{deviceId}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorUsuarioYDeviceId(@PathVariable Long usuarioId,
			@PathVariable String deviceId) {

		log.info("Consultando dispositivo del usuario: {}", usuarioId);

		return dispositivoService.buscarPorUsuarioYDeviceId(usuarioId, deviceId);
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Map<String, Object>>> registrar(@RequestBody DispositivoDto dispositivoDto) {

		log.info("Registrando dispositivo para usuario: {}", dispositivoDto.usuarioId());

		return dispositivoService.registrar(dispositivoDto);
	}

	@PutMapping("/{usuarioId}/{deviceId}/acceso")
	public ResponseEntity<ApiResponse<Map<String, Object>>> actualizarUltimoAcceso(@PathVariable Long usuarioId,
			@PathVariable String deviceId) {

		log.info("Actualizando ultimo acceso del dispositivo del usuario: {}", usuarioId);

		return dispositivoService.actualizarUltimoAcceso(usuarioId, deviceId);
	}

	@DeleteMapping("/{usuarioId}/{deviceId}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> eliminarDispositivo(@PathVariable Long usuarioId,
			@PathVariable String deviceId) {

		log.info("Eliminando dispositivo del usuario: {}", usuarioId);

		return dispositivoService.eliminarDispositivo(usuarioId, deviceId);
	}
}