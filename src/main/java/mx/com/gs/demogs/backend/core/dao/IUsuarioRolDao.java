package mx.com.gs.demogs.backend.core.dao;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import mx.com.gs.demogs.backend.core.model.UsuarioRol;
import mx.com.gs.demogs.backend.core.model.UsuarioRolId;

public interface IUsuarioRolDao
        extends CrudRepository<UsuarioRol, UsuarioRolId> {

    @Query("""
        SELECT ur.rolId
        FROM UsuarioRol ur
        WHERE ur.usuarioId = :usuarioId
    """)
    List<Long> obtenerRolesPorUsuario(
            @Param("usuarioId") Long usuarioId);
}