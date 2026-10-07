
package mx.com.gs.demogs.backend.core.service;

import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import mx.com.gs.demogs.backend.core.dao.IAuditoriaDao;
import mx.com.gs.demogs.backend.core.model.Auditoria;
import mx.com.gs.demogs.backend.core.model.Usuario;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditoriaServiceImpl implements IAuditoriaService {

	private final IAuditoriaDao auditoriaDao;

	@Override
	@Transactional
	public void registrar(String accion, String tabla, Long registroId, String datosAnteriores, String datosNuevos) {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || !authentication.isAuthenticated()) {

			throw new IllegalStateException("No existe un usuario autenticado");
		}

		Object credentials = authentication.getCredentials();

		if (!(credentials instanceof Long usuarioId)) {

			throw new IllegalStateException("No fue posible obtener el id del usuario autenticado");
		}

		if (usuarioId <= 0) {
			throw new IllegalStateException("El id del usuario autenticado no es válido");
		}

		if (accion == null || accion.isBlank()) {
			throw new IllegalArgumentException("La accion es obligatoria");
		}

		if (tabla == null || tabla.isBlank()) {
			throw new IllegalArgumentException("La tabla es obligatoria");
		}

		Usuario usuario = new Usuario();
		usuario.setId(usuarioId);

		Auditoria auditoria = new Auditoria();

		auditoria.setUsuario(usuario);
		auditoria.setFecha(LocalDateTime.now(ZoneId.of("America/Mexico_City")));
		auditoria.setAccion(accion);
		auditoria.setTabla(tabla);
		auditoria.setRegistroId(registroId);
		auditoria.setDatosAnteriores(datosAnteriores);
		auditoria.setDatosNuevos(datosNuevos);

		auditoriaDao.save(auditoria);

		log.info("Auditoria registrada. Usuario: {}, Accion: {}, Tabla: {}, Registro: {}", usuarioId, accion, tabla,
				registroId);
	}

}
