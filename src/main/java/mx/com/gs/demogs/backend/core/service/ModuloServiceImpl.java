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

import mx.com.gs.demogs.backend.core.dao.IModuloDao;
import mx.com.gs.demogs.backend.core.model.Modulo;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.response.MetadataResponse;
import mx.com.gs.demogs.backend.exception.ResourceNotFoundException;
import mx.com.gs.demogs.backend.util.MensajeUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuloServiceImpl implements IModuloService {

	private static final String ENTIDAD_MODULO = "modulo";

	private final IModuloDao moduloDao;
	private final IAuditoriaService auditoriaService;

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ApiResponse<Map<String, Object>>> buscarModulos() {

		log.info("Consultando modulos");

		List<Modulo> modulos = new ArrayList<>();
		moduloDao.findAll().forEach(modulos::add);

		return response(HttpStatus.OK, MensajeUtil.CONSULTA_EXITOSA, modulos);
	}

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorId(Long id) {

		validarId(id);

		return moduloDao.findById(id)
				.map(modulo -> response(HttpStatus.OK, MensajeUtil.CONSULTA_EXITOSA, List.of(modulo)))
				.orElseGet(() -> response(HttpStatus.NOT_FOUND, MensajeUtil.REGISTRO_NO_ENCONTRADO, null));
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> crear(Modulo modulo) {

		validarModulo(modulo);

		prepararModuloPadre(modulo);

		Modulo moduloGuardado = moduloDao.save(modulo);

		auditoriaService.registrar(
				"CREATE",
				ENTIDAD_MODULO,
				moduloGuardado.getId(),
				null,
				moduloGuardado.toString());

		return response(HttpStatus.CREATED, MensajeUtil.REGISTRO_CREADO, List.of(moduloGuardado));
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> actualizar(Modulo modulo, Long id) {

		validarId(id);
		validarModulo(modulo);

		Modulo moduloExistente = obtenerModulo(id);

		String datosAnteriores = moduloExistente.toString();

		actualizarModuloPadre(moduloExistente, modulo, id);

		moduloExistente.setNombre(modulo.getNombre());
		moduloExistente.setRuta(modulo.getRuta());
		moduloExistente.setIcono(modulo.getIcono());
		moduloExistente.setOrden(modulo.getOrden());
		moduloExistente.setActivo(modulo.getActivo());

		Modulo moduloActualizado = moduloDao.save(moduloExistente);

		auditoriaService.registrar(
				"UPDATE",
				ENTIDAD_MODULO,
				id,
				datosAnteriores,
				moduloActualizado.toString());

		return response(HttpStatus.OK, MensajeUtil.REGISTRO_ACTUALIZADO, List.of(moduloActualizado));
	}

	@Override
	@Transactional
	public ResponseEntity<ApiResponse<Map<String, Object>>> eliminar(Long id) {

		validarId(id);

		Modulo modulo = obtenerModulo(id);

		String datosAnteriores = modulo.toString();

		modulo.setActivo(false);

		Modulo moduloActualizado = moduloDao.save(modulo);

		auditoriaService.registrar(
				"DEACTIVATE",
				ENTIDAD_MODULO,
				id,
				datosAnteriores,
				moduloActualizado.toString());

		return response(HttpStatus.OK, MensajeUtil.REGISTRO_ELIMINADO, null);
	}

	private void prepararModuloPadre(Modulo modulo) {

		if (modulo.getModuloPadre() == null) {
			return;
		}

		Long padreId = modulo.getModuloPadre().getId();

		validarIdPadre(padreId);

		Modulo moduloPadre = obtenerModulo(padreId);

		modulo.setModuloPadre(moduloPadre);
	}

	private void actualizarModuloPadre(Modulo moduloExistente, Modulo modulo, Long id) {

		if (modulo.getModuloPadre() == null) {
			moduloExistente.setModuloPadre(null);
			return;
		}

		Long padreId = modulo.getModuloPadre().getId();

		validarIdPadre(padreId);

		if (padreId.equals(id)) {
			throw new IllegalArgumentException("Un modulo no puede ser padre de si mismo");
		}

		moduloExistente.setModuloPadre(obtenerModulo(padreId));
	}

	private Modulo obtenerModulo(Long id) {

		return moduloDao.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException(MensajeUtil.REGISTRO_NO_ENCONTRADO));
	}

	private void validarId(Long id) {

		if (id == null || id <= 0) {
			throw new IllegalArgumentException("El id del modulo es obligatorio");
		}
	}

	private void validarIdPadre(Long id) {

		if (id == null || id <= 0) {
			throw new IllegalArgumentException("El id del modulo padre es obligatorio");
		}
	}

	private void validarModulo(Modulo modulo) {

		if (modulo == null) {
			throw new IllegalArgumentException("Los datos del modulo son obligatorios");
		}

		if (modulo.getNombre() == null || modulo.getNombre().isBlank()) {
			throw new IllegalArgumentException("El nombre del modulo es obligatorio");
		}

		if (modulo.getOrden() == null || modulo.getOrden() < 0) {
			throw new IllegalArgumentException("El orden del modulo es obligatorio");
		}

		if (modulo.getActivo() == null) {
			modulo.setActivo(true);
		}
	}

	private ResponseEntity<ApiResponse<Map<String, Object>>> response(
			HttpStatus status,
			String mensaje,
			List<Modulo> modulos) {

		return ResponseEntity.status(status).body(crearResponse(status, mensaje, modulos));
	}

	private ApiResponse<Map<String, Object>> crearResponse(
			HttpStatus status,
			String mensaje,
			List<Modulo> modulos) {

		MetadataResponse metadata = new MetadataResponse(
				status.is2xxSuccessful() ? "SUCCESS" : "ERROR",
				String.valueOf(status.value()),
				mensaje);

		Map<String, Object> data = new LinkedHashMap<>();

		if (modulos != null) {
			data.put("modulos", modulos);
		}

		return new ApiResponse<>(metadata, data);
	}
}