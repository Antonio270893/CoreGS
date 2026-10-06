package mx.com.gs.demogs.backend.core.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mx.com.gs.demogs.backend.core.model.Endpoint;

public interface IEndpointDao
        extends CrudRepository<Endpoint, Long> {

    @Query("""
        SELECT e
        FROM Endpoint e
        WHERE e.metodo = :metodo
          AND e.activo = true
    """)
    List<Endpoint> buscarPorMetodoActivo(
            @Param("metodo") String metodo);
}