package mx.com.gs.demogs.backend.core.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import mx.com.gs.demogs.backend.core.model.Usuario;

public interface IUsuarioDao extends CrudRepository<Usuario, Long> {

	Optional<Usuario> findByNumeroEmpleado(Long numeroEmpleado);

	boolean existsByNumeroEmpleado(Long numeroEmpleado);

	boolean existsByCorreo(String correo);

	boolean existsByNumeroEmpleadoAndIdNot(Long numeroEmpleado, Long id);

	boolean existsByCorreoAndIdNot(String correo, Long id);
}