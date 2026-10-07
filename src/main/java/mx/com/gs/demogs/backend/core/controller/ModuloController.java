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
import mx.com.gs.demogs.backend.core.dto.ModuloDto;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.service.IModuloService;

@RestController
@RequestMapping("/v1/core/modulo")
@RequiredArgsConstructor
public class ModuloController {

    private final IModuloService moduloService;

    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> buscarModulos() {
        return moduloService.buscarModulos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorId(
            @PathVariable Long id) {

        return moduloService.buscarPorId(id);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> crear(
            @RequestBody ModuloDto moduloDto) {

        return moduloService.crear(moduloDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> actualizar(
            @PathVariable Long id,
            @RequestBody ModuloDto moduloDto) {

        return moduloService.actualizar(moduloDto, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, Object>>> eliminar(
            @PathVariable Long id) {

        return moduloService.eliminar(id);
    }
}