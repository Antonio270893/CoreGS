package mx.com.gs.demogs.backend.core.service;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import mx.com.gs.demogs.backend.core.response.ApiResponse;

public interface IMenuService {

	ResponseEntity<ApiResponse<Map<String, Object>>> obtenerMenu(Long usuarioId);

}