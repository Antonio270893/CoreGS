package mx.com.gs.demogs.backend.core.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.gs.demogs.backend.core.dao.IUsuarioDao;
import mx.com.gs.demogs.backend.core.dto.DispositivoDto;
import mx.com.gs.demogs.backend.core.model.Dispositivo;
import mx.com.gs.demogs.backend.core.model.RefreshToken;
import mx.com.gs.demogs.backend.core.model.Usuario;
import mx.com.gs.demogs.backend.core.request.AuthRequest;
import mx.com.gs.demogs.backend.core.request.RefreshTokenRequest;
import mx.com.gs.demogs.backend.core.response.TokenResponse;
import mx.com.gs.demogs.backend.core.service.IDispositivoService;
import mx.com.gs.demogs.backend.core.service.JwtService;
import mx.com.gs.demogs.backend.core.service.RefreshTokenService;

@RestController
@RequestMapping("/v1/core")
@RequiredArgsConstructor
@Slf4j
public class TokenController {

	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtService jwtService;
	private final IUsuarioDao usuarioDao;
	private final RefreshTokenService refreshTokenService;
	private final IDispositivoService dispositivoService;

	@PostMapping("/authenticate")
	public ResponseEntity<?> authenticate(@RequestBody AuthRequest request) {

		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(
						request.getNumeroEmpleado(),
						request.getContrasenia()));

		Usuario usuario = usuarioDao
				.findByNumeroEmpleado(request.getNumeroEmpleado())
				.orElseThrow(() -> new IllegalStateException(
						"Usuario no encontrado"));

		if (request.getDeviceId() != null
				&& !request.getDeviceId().isBlank()) {

			Dispositivo dispositivo = dispositivoService
					.obtenerPorUsuarioYDeviceId(
							usuario.getId(),
							request.getDeviceId());

			if (dispositivo == null) {

				DispositivoDto dispositivoDto = new DispositivoDto(
						usuario.getId(),
						request.getDeviceId(),
						request.getDeviceFingerprint(),
						request.getTipoDispositivo(),
						request.getSistemaOperativo(),
						request.getNavegador());

				dispositivoService.registrar(
						dispositivoDto,
						false);

				log.info(
						"Nuevo dispositivo registrado para usuario: {}",
						usuario.getNumeroEmpleado());

				return ResponseEntity.ok(
						new DispositivoLoginResponse(
								false,
								true,
								"El dispositivo requiere activación"));
			}

			if (!Boolean.TRUE.equals(dispositivo.getActivo())) {

				log.warn(
						"Dispositivo no autorizado para usuario: {}",
						usuario.getNumeroEmpleado());

				return ResponseEntity.ok(
						new DispositivoLoginResponse(
								false,
								true,
								"El dispositivo requiere activación"));
			}

			dispositivoService.actualizarUltimoAcceso(
					usuario.getId(),
					request.getDeviceId());
		}

		UserDetails userDetails = userDetailsService
				.loadUserByUsername(
						request.getNumeroEmpleado().toString());

		String jwt = jwtService.generateToken(
				userDetails,
				usuario);

		RefreshToken refreshToken = refreshTokenService
				.crear(usuario.getNumeroEmpleado());

		return ResponseEntity.ok(
				new TokenResponse(
						jwt,
						"Bearer",
						jwtService.getExpirationInSeconds(),
						refreshToken.getToken()));
	}

	@PostMapping("/auth/refresh")
	public ResponseEntity<TokenResponse> refresh(
			@RequestBody RefreshTokenRequest request) {

		RefreshToken refreshToken = refreshTokenService
				.validar(request.getRefreshToken());

		UserDetails userDetails = userDetailsService
				.loadUserByUsername(
						refreshToken.getNumeroEmpleado().toString());

		Usuario usuario = usuarioDao
				.findByNumeroEmpleado(
						refreshToken.getNumeroEmpleado())
				.orElseThrow(() -> new IllegalStateException(
						"Usuario no encontrado"));

		String jwt = jwtService.generateToken(
				userDetails,
				usuario);

		return ResponseEntity.ok(
				new TokenResponse(
						jwt,
						"Bearer",
						jwtService.getExpirationInSeconds(),
						refreshToken.getToken()));
	}

	@PostMapping("/auth/logout")
	public ResponseEntity<Void> logout(
			@RequestBody RefreshTokenRequest request) {

		refreshTokenService.revocar(
				request.getRefreshToken());

		return ResponseEntity.noContent().build();
	}

	private record DispositivoLoginResponse(
			boolean dispositivoAutorizado,
			boolean requiereActivacion,
			String mensaje) {
	}
}