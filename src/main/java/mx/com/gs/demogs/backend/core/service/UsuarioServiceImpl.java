package mx.com.gs.demogs.backend.core.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.gs.demogs.backend.core.dao.IUsuarioDao;
import mx.com.gs.demogs.backend.core.model.Usuario;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.response.MetadataResponse;
import mx.com.gs.demogs.backend.exception.ResourceNotFoundException;
import mx.com.gs.demogs.backend.util.MensajeUtil;
import mx.com.gs.demogs.backend.util.PasswordUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServiceImpl implements IUsuarioService {

	private final IUsuarioDao usuarioDao;
	private final PasswordUtil passwordUtil;

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ApiResponse<Map<String, Object>>> buscarUsuario() {

		log.info("Consultando usuarios");

		List<Usuario> usuarios = new ArrayList<>();

		usuarioDao.findAll().forEach(usuarios::add);

		return ResponseEntity.ok(crearResponse(HttpStatus.OK, MensajeUtil.CONSULTA_EXITOSA, usuarios));
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorId(Long id) {

		log.info("Consultando usuario con id: {}", id);

		return usuarioDao.findById(id).map(usuario -> {

			List<Usuario> usuarios = new ArrayList<>();

			usuarios.add(usuario);

			return ResponseEntity.ok(crearResponse(HttpStatus.OK, MensajeUtil.CONSULTA_EXITOSA, usuarios));
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(crearResponse(HttpStatus.NOT_FOUND, MensajeUtil.REGISTRO_NO_ENCONTRADO, null)));
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> crear(Usuario usuario) {

		log.info("Creando usuario con numero de empleado: {}", usuario.getNumeroEmpleado());

		validarUsuario(usuario);

		if (usuarioDao.existsByNumeroEmpleado(usuario.getNumeroEmpleado())) {

			throw new IllegalArgumentException("El numero de empleado ya existe");
		}

		if (usuarioDao.existsByCorreo(usuario.getCorreo())) {

			throw new IllegalArgumentException("El correo ya existe");
		}

		if (usuario.getContrasenia() == null || usuario.getContrasenia().isBlank()) {

			throw new IllegalArgumentException("La contraseña es obligatoria");
		}

		usuario.setContrasenia(passwordUtil.encriptar(usuario.getContrasenia()));

		Usuario usuarioGuardado = usuarioDao.save(usuario);

		List<Usuario> usuarios = new ArrayList<>();

		usuarios.add(usuarioGuardado);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(crearResponse(HttpStatus.CREATED, MensajeUtil.REGISTRO_CREADO, usuarios));
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> actualizar(Usuario usuario, Long id) {

		log.info("Actualizando usuario con id: {}", id);

		if (id == null || id <= 0) {
			throw new IllegalArgumentException("El id del usuario es obligatorio");
		}

		validarUsuario(usuario);

		Usuario usuarioExistente = usuarioDao.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(MensajeUtil.REGISTRO_NO_ENCONTRADO));

		if (usuarioDao.existsByNumeroEmpleadoAndIdNot(usuario.getNumeroEmpleado(), id)) {

			throw new IllegalArgumentException("El numero de empleado ya existe");
		}

		if (usuarioDao.existsByCorreoAndIdNot(usuario.getCorreo(), id)) {

			throw new IllegalArgumentException("El correo ya existe");
		}

		usuarioExistente.setNombre(usuario.getNombre());

		usuarioExistente.setNumeroEmpleado(usuario.getNumeroEmpleado());

		usuarioExistente.setCorreo(usuario.getCorreo());

		usuarioExistente.setCeco(usuario.getCeco());

		usuarioExistente.setActivo(usuario.getActivo());

		if (usuario.getContrasenia() != null && !usuario.getContrasenia().isBlank()) {

			usuarioExistente.setContrasenia(passwordUtil.encriptar(usuario.getContrasenia()));
		}

		Usuario usuarioActualizado = usuarioDao.save(usuarioExistente);

		List<Usuario> usuarios = new ArrayList<>();

		usuarios.add(usuarioActualizado);

		return ResponseEntity.ok(crearResponse(HttpStatus.OK, MensajeUtil.REGISTRO_ACTUALIZADO, usuarios));
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> eliminar(Long id) {

		log.info("Eliminando usuario con id: {}", id);

		if (id == null || id <= 0) {
			throw new IllegalArgumentException("El id del usuario es obligatorio");
		}

		Usuario usuario = usuarioDao.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(MensajeUtil.REGISTRO_NO_ENCONTRADO));

		usuarioDao.delete(usuario);

		return ResponseEntity.ok(crearResponse(HttpStatus.OK, MensajeUtil.REGISTRO_ELIMINADO, null));
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerPorNumeroEmpleado(Long numeroEmpleado) {

		log.info("Consultando usuario con numero de empleado: {}", numeroEmpleado);

		if (numeroEmpleado == null || numeroEmpleado <= 0) {

			throw new IllegalArgumentException("El numero de empleado es obligatorio");
		}

		return usuarioDao.findByNumeroEmpleado(numeroEmpleado).map(usuario -> {

			List<Usuario> usuarios = new ArrayList<>();

			usuarios.add(usuario);

			return ResponseEntity.ok(crearResponse(HttpStatus.OK, MensajeUtil.CONSULTA_EXITOSA, usuarios));
		}).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(crearResponse(HttpStatus.NOT_FOUND, MensajeUtil.REGISTRO_NO_ENCONTRADO, null)));
	}

	private void validarUsuario(Usuario usuario) {

		if (usuario == null) {
			throw new IllegalArgumentException("Los datos del usuario son obligatorios");
		}

		if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {

			throw new IllegalArgumentException("El nombre es obligatorio");
		}

		if (usuario.getNumeroEmpleado() == null || usuario.getNumeroEmpleado() <= 0) {

			throw new IllegalArgumentException("El numero de empleado es obligatorio");
		}

		if (usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {

			throw new IllegalArgumentException("El correo es obligatorio");
		}

		if (usuario.getCeco() == null || usuario.getCeco().isBlank()) {

			throw new IllegalArgumentException("El CECO es obligatorio");
		}
	}

	private ApiResponse<Map<String, Object>> crearResponse(HttpStatus status, String mensaje, List<Usuario> usuarios) {

		MetadataResponse metadata = new MetadataResponse(status.is2xxSuccessful() ? "SUCCESS" : "ERROR",
				String.valueOf(status.value()), mensaje);

		Map<String, Object> data = new LinkedHashMap<>();

		if (usuarios != null) {
			data.put("usuarios", usuarios);
		}

		return new ApiResponse<>(metadata, data);
	}
}