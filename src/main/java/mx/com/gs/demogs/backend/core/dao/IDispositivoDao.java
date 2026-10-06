package mx.com.gs.demogs.backend.core.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import mx.com.gs.demogs.backend.core.model.Dispositivo;

public interface IDispositivoDao extends CrudRepository<Dispositivo, Long> {

	Optional<Dispositivo> findByUsuarioIdAndDeviceId(Long usuarioId, String deviceId);

	Optional<Dispositivo> findByUsuarioIdAndDeviceIdAndActivoTrue(Long usuarioId, String deviceId);

	Optional<Dispositivo> findByUsuarioNumeroEmpleadoAndDeviceId(Long numeroEmpleado, String deviceId);
	
	List<Dispositivo> findByUsuarioIdOrderByUltimoAccesoDesc(Long usuarioId);
}