package mx.com.gs.demogs.backend.core.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import mx.com.gs.demogs.backend.core.request.CorreoRequest;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.response.MetadataResponse;
import mx.com.gs.demogs.backend.core.service.ICorreoService;
import mx.com.gs.demogs.backend.util.MensajeUtil;

@RestController
@RequestMapping("/v1/core/correo")
@RequiredArgsConstructor
@Slf4j
public class CorreoController {

    private final ICorreoService correoService;

	@PostMapping("/enviar")
	public ResponseEntity<ApiResponse<Void>> enviar(
            @RequestBody CorreoRequest request) { 

        log.info("Solicitando envío de correo");

        correoService.enviar(request);

        MetadataResponse metadata = new MetadataResponse(
                "SUCCESS",
                String.valueOf(HttpStatus.OK.value()),
                MensajeUtil.CONSULTA_EXITOSA
        );

        return ResponseEntity.ok(
                new ApiResponse<>(metadata, null)
        );
    }
}