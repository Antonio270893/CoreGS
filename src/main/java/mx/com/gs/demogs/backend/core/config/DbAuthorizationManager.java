package mx.com.gs.demogs.backend.core.config;

import java.util.function.Supplier;

import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.gs.demogs.backend.core.dao.IUsuarioDao;
import mx.com.gs.demogs.backend.core.service.AuthorizationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class DbAuthorizationManager
        implements AuthorizationManager<RequestAuthorizationContext> {

    private final AuthorizationService authorizationService;
    private final IUsuarioDao usuarioDao;

    @Override
    public AuthorizationDecision authorize(
            Supplier<? extends Authentication> authentication,
            RequestAuthorizationContext context) {

        Authentication auth = authentication.get();

        if (auth == null || !auth.isAuthenticated()) {

            log.warn("Usuario no autenticado");

            return new AuthorizationDecision(false);
        }

        if (!(auth.getPrincipal()
                instanceof UserDetails userDetails)) {

            log.warn("El principal no es UserDetails");

            return new AuthorizationDecision(false);
        }

        Long numeroEmpleado =
                Long.valueOf(userDetails.getUsername());

        var usuario =
                usuarioDao.findByNumeroEmpleado(numeroEmpleado);

        if (usuario.isEmpty()) {

            log.warn(
                "Usuario no encontrado: {}",
                numeroEmpleado
            );

            return new AuthorizationDecision(false);
        }

        String url =
                context.getRequest().getServletPath();

        String metodo =
                context.getRequest().getMethod();

        Long usuarioId =
                usuario.get().getId();

        log.info("Autorizando solicitud");
        log.info("URL: {}", url);
        log.info("Método: {}", metodo);
        log.info("Número empleado: {}", numeroEmpleado);
        log.info("Usuario ID: {}", usuarioId);

        boolean permitido =
                authorizationService.tienePermiso(
                    url,
                    metodo,
                    usuarioId
                );

        log.info("Permiso concedido: {}", permitido);

        return new AuthorizationDecision(permitido);
    }
}