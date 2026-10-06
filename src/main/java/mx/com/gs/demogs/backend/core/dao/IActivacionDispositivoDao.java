package mx.com.gs.demogs.backend.core.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import mx.com.gs.demogs.backend.core.model.ActivacionDispositivo;

public interface IActivacionDispositivoDao
        extends CrudRepository<ActivacionDispositivo, Long> {

    Optional<ActivacionDispositivo> findByTokenAndUtilizadoFalse(
            String token);

    Optional<ActivacionDispositivo> findByCodigoAndUtilizadoFalse(
            String codigo);
    
    void deleteByDispositivoId(Long dispositivoId);
}