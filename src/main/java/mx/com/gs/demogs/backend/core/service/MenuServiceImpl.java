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
import mx.com.gs.demogs.backend.core.dao.IRolModuloDao;
import mx.com.gs.demogs.backend.core.dao.IUsuarioRolDao;
import mx.com.gs.demogs.backend.core.model.Modulo;
import mx.com.gs.demogs.backend.core.model.RolModulo;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.response.MetadataResponse;
import mx.com.gs.demogs.backend.dto.MenuDto;
import mx.com.gs.demogs.backend.util.MensajeUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements IMenuService {

	private final IUsuarioRolDao usuarioRolDao;
	private final IRolModuloDao rolModuloDao;
	private final IModuloDao moduloDao;

	@Override
	@Transactional(readOnly = true)
	public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerMenu(Long usuarioId) {

		log.info("Consultando menu para el usuario con id: {}", usuarioId);

		if (usuarioId == null || usuarioId <= 0) {
			throw new IllegalArgumentException("El id del usuario es obligatorio");
		}

		List<Long> roles = usuarioRolDao.obtenerRolesPorUsuario(usuarioId);

		if (roles.isEmpty()) {
			return ResponseEntity.ok(crearResponse(HttpStatus.OK, "El usuario no tiene roles asignados", List.of()));
		}

		List<RolModulo> relaciones = rolModuloDao.obtenerModulosPorRoles(roles);

		if (relaciones.isEmpty()) {
			return ResponseEntity.ok(crearResponse(HttpStatus.OK, "El usuario no tiene modulos asignados", List.of()));
		}

		List<Long> moduloIds = relaciones.stream().map(RolModulo::getModuloId).distinct().toList();

		List<Modulo> modulos = moduloDao.findByIdInAndActivoTrueOrderByOrdenAsc(moduloIds);

		List<MenuDto> menu = construirMenu(modulos);

		return ResponseEntity.ok(crearResponse(HttpStatus.OK, MensajeUtil.CONSULTA_EXITOSA, menu));
	}

	private List<MenuDto> construirMenu(List<Modulo> modulos) {

		Map<Long, MenuDto> menuMap = new LinkedHashMap<>();

		for (Modulo modulo : modulos) {

			if (modulo.getModuloPadre() == null) {

				MenuDto menuDto = MenuDto.builder().id(modulo.getId()).nombre(modulo.getNombre()).ruta(modulo.getRuta())
						.icono(modulo.getIcono()).orden(modulo.getOrden()).submodulos(new ArrayList<>()).build();

				menuMap.put(modulo.getId(), menuDto);
			}
		}

		for (Modulo modulo : modulos) {

			if (modulo.getModuloPadre() != null) {

				Long padreId = modulo.getModuloPadre().getId();

				MenuDto padre = menuMap.get(padreId);

				if (padre != null) {

					MenuDto submodulo = MenuDto.builder().id(modulo.getId()).nombre(modulo.getNombre())
							.ruta(modulo.getRuta()).icono(modulo.getIcono()).orden(modulo.getOrden())
							.submodulos(new ArrayList<>()).build();

					padre.getSubmodulos().add(submodulo);
				}
			}
		}

		menuMap.values()
				.forEach(menu -> menu.getSubmodulos().sort((a, b) -> Integer.compare(a.getOrden(), b.getOrden())));

		return new ArrayList<>(menuMap.values());
	}

	private ApiResponse<Map<String, Object>> crearResponse(HttpStatus status, String mensaje, List<MenuDto> modulos) {

		MetadataResponse metadata = new MetadataResponse(status.is2xxSuccessful() ? "SUCCESS" : "ERROR",
				String.valueOf(status.value()), mensaje);

		Map<String, Object> data = new LinkedHashMap<>();

		data.put("modulos", modulos);

		return new ApiResponse<>(metadata, data);
	}
}