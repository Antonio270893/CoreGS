package mx.com.gs.demogs.backend.core.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import mx.com.gs.demogs.backend.core.model.Modulo;

public interface IModuloDao extends CrudRepository<Modulo, Long> {

	List<Modulo> findByActivoTrueOrderByOrdenAsc();

	List<Modulo> findByModuloPadreIsNullAndActivoTrueOrderByOrdenAsc();

	List<Modulo> findByModuloPadreIdAndActivoTrueOrderByOrdenAsc(Long moduloPadreId);

	List<Modulo> findByIdInAndActivoTrueOrderByOrdenAsc(List<Long> ids);
}