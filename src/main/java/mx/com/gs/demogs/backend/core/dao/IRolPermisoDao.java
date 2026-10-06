package mx.com.gs.demogs.backend.core.dao;

import org.springframework.data.repository.CrudRepository;

import mx.com.gs.demogs.backend.core.model.RolPermiso;
import mx.com.gs.demogs.backend.core.model.RolPermisoId;

public interface IRolPermisoDao
        extends CrudRepository<RolPermiso, RolPermisoId> {

    boolean existsByRolIdAndPermisoId(
            Long rolId,
            Long permisoId);
}