package mx.com.gs.demogs.backend.core.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import mx.com.gs.demogs.backend.core.request.ActivacionCodigoRequest;
import mx.com.gs.demogs.backend.core.request.ActivacionDispositivoLoginRequest;
import mx.com.gs.demogs.backend.core.request.ActivacionTokenRequest;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.service.IActivacionDispositivoService;

@RestController
@RequestMapping("/v1/core/dispositivo/activacion")
@RequiredArgsConstructor
@Slf4j
public class ActivacionDispositivoController {

	private final IActivacionDispositivoService activacionDispositivoService;

	@PostMapping("/login")
	public ResponseEntity<ApiResponse<Map<String, Object>>> generarPorLogin(
			@RequestBody ActivacionDispositivoLoginRequest request) {

		log.info("Generando activación desde login para empleado: {}", request.getNumeroEmpleado());

		return activacionDispositivoService.generarPorLogin(request.getNumeroEmpleado(), request.getDeviceId());
	}

	@PostMapping("/{dispositivoId}")
	public ResponseEntity<ApiResponse<Map<String, Object>>> generar(@PathVariable Long dispositivoId) {

		log.info("Generando activación para dispositivo: {}", dispositivoId);

		return activacionDispositivoService.generar(dispositivoId);
	}

	@PostMapping("/token")
	public ResponseEntity<ApiResponse<Map<String, Object>>> activarPorToken(
			@RequestBody ActivacionTokenRequest request) {

		log.info("Activando dispositivo mediante token");

		return activacionDispositivoService.activarPorToken(request.getToken());
	}

	@PostMapping("/codigo")
	public ResponseEntity<ApiResponse<Map<String, Object>>> activarPorCodigo(
			@RequestBody ActivacionCodigoRequest request) {

		log.info("Activando dispositivo mediante código");

		return activacionDispositivoService.activarPorCodigo(request.getCodigo());
	}
}