package mx.com.gs.demogs.backend.core.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import lombok.RequiredArgsConstructor;
import mx.com.gs.demogs.backend.core.dao.IEndpointDao;
import mx.com.gs.demogs.backend.core.dao.IRolPermisoDao;
import mx.com.gs.demogs.backend.core.dao.IUsuarioRolDao;
import mx.com.gs.demogs.backend.core.model.Endpoint;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final IEndpointDao endpointDao;
    private final IRolPermisoDao rolPermisoDao;
    private final IUsuarioRolDao usuarioRolDao;

    private final AntPathMatcher pathMatcher =
            new AntPathMatcher();

    public boolean tienePermiso(
            String url,
            String metodo,
            Long usuarioId) {

        List<Endpoint> endpoints =
                endpointDao.buscarPorMetodoActivo(metodo);

        return endpoints.stream()
                .filter(endpoint ->
                    pathMatcher.match(
                        endpoint.getUrl(),
                        url
                    )
                )
                .anyMatch(endpoint ->
                    usuarioTienePermiso(
                        usuarioId,
                        endpoint.getPermiso().getId()
                    )
                );
    }

    private boolean usuarioTienePermiso(
            Long usuarioId,
            Long permisoId) {

        List<Long> roles =
                usuarioRolDao.obtenerRolesPorUsuario(
                    usuarioId
                );

        return roles.stream()
                .anyMatch(rolId ->
                    rolPermisoDao
                        .existsByRolIdAndPermisoId(
                            rolId,
                            permisoId
                        )
                );
    }
}