package mx.com.gs.demogs.backend.core.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mx.com.gs.demogs.backend.core.model.RolModulo;
import mx.com.gs.demogs.backend.core.model.RolModuloId;

public interface IRolModuloDao extends CrudRepository<RolModulo, RolModuloId> {

	List<RolModulo> findByRolId(Long rolId);

	@Query("""
			SELECT rm
			FROM RolModulo rm
			INNER JOIN Modulo m
				ON m.id = rm.moduloId
			WHERE rm.rolId IN :rolIds
			  AND m.activo = TRUE
			""")
	List<RolModulo> obtenerModulosPorRoles(@Param("rolIds") List<Long> rolIds);
}