package mx.com.gs.demogs.backend.core.dao;

import org.springframework.data.repository.CrudRepository;

import mx.com.gs.demogs.backend.core.model.Permiso;

public interface IPermisoDao extends CrudRepository<Permiso, Long> {
}