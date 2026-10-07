package mx.com.gs.demogs.backend.core.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.gs.demogs.backend.core.dao.IUsuarioDao;
import mx.com.gs.demogs.backend.core.dto.UsuarioDto;
import mx.com.gs.demogs.backend.core.model.Usuario;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.response.MetadataResponse;
import mx.com.gs.demogs.backend.exception.ResourceNotFoundException;
import mx.com.gs.demogs.backend.util.MensajeUtil;
import mx.com.gs.demogs.backend.util.PasswordUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsuarioServiceImpl implements IUsuarioService {

    private final IUsuarioDao usuarioDao;
    private final PasswordUtil passwordUtil;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> buscarUsuario() {

        log.info("Consultando usuarios");

        List<Usuario> usuarios = new ArrayList<>();

        usuarioDao.findAll().forEach(usuarios::add);

        return ResponseEntity.ok(
                crearResponse(
                        HttpStatus.OK,
                        MensajeUtil.CONSULTA_EXITOSA,
                        usuarios));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorId(
            Long id) {

        log.info("Consultando usuario con id: {}", id);

        return usuarioDao.findById(id)
                .map(usuario -> {

                    List<Usuario> usuarios = new ArrayList<>();
                    usuarios.add(usuario);

                    return ResponseEntity.ok(
                            crearResponse(
                                    HttpStatus.OK,
                                    MensajeUtil.CONSULTA_EXITOSA,
                                    usuarios));
                })
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(
                                crearResponse(
                                        HttpStatus.NOT_FOUND,
                                        MensajeUtil.REGISTRO_NO_ENCONTRADO,
                                        null)));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> crear(
            UsuarioDto usuarioDto) {

        log.info(
                "Creando usuario con numero de empleado: {}",
                usuarioDto.numeroEmpleado());

        validarUsuario(usuarioDto);

        if (usuarioDao.existsByNumeroEmpleado(
                usuarioDto.numeroEmpleado())) {

            throw new IllegalArgumentException(
                    "El numero de empleado ya existe");
        }

        if (usuarioDao.existsByCorreo(
                usuarioDto.correo())) {

            throw new IllegalArgumentException(
                    "El correo ya existe");
        }

        if (usuarioDto.contrasenia() == null
                || usuarioDto.contrasenia().isBlank()) {

            throw new IllegalArgumentException(
                    "La contraseña es obligatoria");
        }

        Usuario usuario = convertirEntidad(usuarioDto);

        usuario.setContrasenia(
                passwordUtil.encriptar(
                        usuarioDto.contrasenia()));

        Usuario usuarioGuardado =
                usuarioDao.save(usuario);

        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuarioGuardado);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        crearResponse(
                                HttpStatus.CREATED,
                                MensajeUtil.REGISTRO_CREADO,
                                usuarios));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> actualizar(
            UsuarioDto usuarioDto,
            Long id) {

        log.info(
                "Actualizando usuario con id: {}",
                id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El id del usuario es obligatorio");
        }

        validarUsuario(usuarioDto);

        Usuario usuarioExistente =
                usuarioDao.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MensajeUtil.REGISTRO_NO_ENCONTRADO));

        if (usuarioDao.existsByNumeroEmpleadoAndIdNot(
                usuarioDto.numeroEmpleado(),
                id)) {

            throw new IllegalArgumentException(
                    "El numero de empleado ya existe");
        }

        if (usuarioDao.existsByCorreoAndIdNot(
                usuarioDto.correo(),
                id)) {

            throw new IllegalArgumentException(
                    "El correo ya existe");
        }

        usuarioExistente.setNombre(
                usuarioDto.nombre());

        usuarioExistente.setNumeroEmpleado(
                usuarioDto.numeroEmpleado());

        usuarioExistente.setCorreo(
                usuarioDto.correo());

        usuarioExistente.setCeco(
                usuarioDto.ceco());

        if (usuarioDto.activo() != null) {
            usuarioExistente.setActivo(
                    usuarioDto.activo());
        }

        if (usuarioDto.contrasenia() != null
                && !usuarioDto.contrasenia().isBlank()) {

            usuarioExistente.setContrasenia(
                    passwordUtil.encriptar(
                            usuarioDto.contrasenia()));
        }

        Usuario usuarioActualizado =
                usuarioDao.save(usuarioExistente);

        List<Usuario> usuarios = new ArrayList<>();
        usuarios.add(usuarioActualizado);

        return ResponseEntity.ok(
                crearResponse(
                        HttpStatus.OK,
                        MensajeUtil.REGISTRO_ACTUALIZADO,
                        usuarios));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> eliminar(
            Long id) {

        log.info(
                "Eliminando usuario con id: {}",
                id);

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El id del usuario es obligatorio");
        }

        Usuario usuario =
                usuarioDao.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        MensajeUtil.REGISTRO_NO_ENCONTRADO));

        usuarioDao.delete(usuario);

        return ResponseEntity.ok(
                crearResponse(
                        HttpStatus.OK,
                        MensajeUtil.REGISTRO_ELIMINADO,
                        null));
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> obtenerPorNumeroEmpleado(
            Long numeroEmpleado) {

        log.info(
                "Consultando usuario con numero de empleado: {}",
                numeroEmpleado);

        if (numeroEmpleado == null || numeroEmpleado <= 0) {
            throw new IllegalArgumentException(
                    "El numero de empleado es obligatorio");
        }

        return usuarioDao.findByNumeroEmpleado(
                numeroEmpleado)
                .map(usuario -> {

                    List<Usuario> usuarios = new ArrayList<>();
                    usuarios.add(usuario);

                    return ResponseEntity.ok(
                            crearResponse(
                                    HttpStatus.OK,
                                    MensajeUtil.CONSULTA_EXITOSA,
                                    usuarios));
                })
                .orElseGet(() -> ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(
                                crearResponse(
                                        HttpStatus.NOT_FOUND,
                                        MensajeUtil.REGISTRO_NO_ENCONTRADO,
                                        null)));
    }

    private Usuario convertirEntidad(
            UsuarioDto usuarioDto) {

        Usuario usuario = new Usuario();

        usuario.setNombre(
                usuarioDto.nombre());

        usuario.setNumeroEmpleado(
                usuarioDto.numeroEmpleado());

        usuario.setCeco(
                usuarioDto.ceco());

        usuario.setCorreo(
                usuarioDto.correo());

        usuario.setActivo(
                usuarioDto.activo());

        return usuario;
    }

    private void validarUsuario(
            UsuarioDto usuarioDto) {

        if (usuarioDto == null) {
            throw new IllegalArgumentException(
                    "Los datos del usuario son obligatorios");
        }

        if (usuarioDto.nombre() == null
                || usuarioDto.nombre().isBlank()) {

            throw new IllegalArgumentException(
                    "El nombre es obligatorio");
        }

        if (usuarioDto.numeroEmpleado() == null
                || usuarioDto.numeroEmpleado() <= 0) {

            throw new IllegalArgumentException(
                    "El numero de empleado es obligatorio");
        }

        if (usuarioDto.correo() == null
                || usuarioDto.correo().isBlank()) {

            throw new IllegalArgumentException(
                    "El correo es obligatorio");
        }

        if (usuarioDto.ceco() == null
                || usuarioDto.ceco().isBlank()) {

            throw new IllegalArgumentException(
                    "El CECO es obligatorio");
        }
    }

    private ApiResponse<Map<String, Object>> crearResponse(
            HttpStatus status,
            String mensaje,
            List<Usuario> usuarios) {

        MetadataResponse metadata =
                new MetadataResponse(
                        status.is2xxSuccessful()
                                ? "SUCCESS"
                                : "ERROR",
                        String.valueOf(status.value()),
                        mensaje);

        Map<String, Object> data =
                new LinkedHashMap<>();

        if (usuarios != null) {
            data.put("usuarios", usuarios);
        }

        return new ApiResponse<>(
                metadata,
                data);
    }
}