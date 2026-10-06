package mx.com.gs.demogs.backend.core.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.service.IMenuService;
import mx.com.gs.demogs.backend.core.service.JwtService;

@RestController
@RequestMapping("/v1/core/menu")
@RequiredArgsConstructor
public class MenuController {

	private final IMenuService menuService;
	private final JwtService jwtService;

	@GetMapping
	public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerMenu(
			@RequestHeader("Authorization") String authorization) {

		String token = authorization.substring(7);

		Long usuarioId = jwtService.extractUsuarioId(token);

		return menuService.obtenerMenu(usuarioId);
	}
}