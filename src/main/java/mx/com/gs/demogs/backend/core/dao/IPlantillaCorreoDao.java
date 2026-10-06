package mx.com.gs.demogs.backend.core.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import mx.com.gs.demogs.backend.core.model.PlantillaCorreo;

public interface IPlantillaCorreoDao extends CrudRepository<PlantillaCorreo, Long> {

	Optional<PlantillaCorreo> findByReferenciaAndActivoTrue(String referencia);
}