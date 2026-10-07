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
import mx.com.gs.demogs.backend.core.dao.IModuloDao;
import mx.com.gs.demogs.backend.core.dto.ModuloDto;
import mx.com.gs.demogs.backend.core.model.Modulo;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.response.MetadataResponse;
import mx.com.gs.demogs.backend.exception.ResourceNotFoundException;
import mx.com.gs.demogs.backend.util.MensajeUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class ModuloServiceImpl implements IModuloService {

    private static final String ENTIDAD_MODULO = "modulo";

    private final IModuloDao moduloDao;
    private final IAuditoriaService auditoriaService;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> buscarModulos() {

        log.info("Consultando modulos");

        List<Modulo> modulos = new ArrayList<>();
        moduloDao.findAll().forEach(modulos::add);

        return response(
                HttpStatus.OK,
                MensajeUtil.CONSULTA_EXITOSA,
                modulos);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorId(Long id) {

        validarId(id);

        return moduloDao.findById(id)
                .map(modulo -> response(
                        HttpStatus.OK,
                        MensajeUtil.CONSULTA_EXITOSA,
                        List.of(modulo)))
                .orElseGet(() -> response(
                        HttpStatus.NOT_FOUND,
                        MensajeUtil.REGISTRO_NO_ENCONTRADO,
                        null));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> crear(
            ModuloDto moduloDto) {

        validarModulo(moduloDto);

        Modulo modulo = convertirEntidad(moduloDto);

        prepararModuloPadre(
                modulo,
                moduloDto.moduloPadreId());

        Modulo moduloGuardado = moduloDao.save(modulo);

        auditoriaService.registrar(
                "CREATE",
                ENTIDAD_MODULO,
                moduloGuardado.getId(),
                null,
                moduloGuardado.toString());

        return response(
                HttpStatus.CREATED,
                MensajeUtil.REGISTRO_CREADO,
                List.of(moduloGuardado));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> actualizar(
            ModuloDto moduloDto,
            Long id) {

        validarId(id);
        validarModulo(moduloDto);

        Modulo moduloExistente = obtenerModulo(id);

        String datosAnteriores = moduloExistente.toString();

        actualizarModuloPadre(
                moduloExistente,
                moduloDto.moduloPadreId(),
                id);

        moduloExistente.setNombre(moduloDto.nombre());
        moduloExistente.setRuta(moduloDto.ruta());
        moduloExistente.setIcono(moduloDto.icono());
        moduloExistente.setOrden(moduloDto.orden());

        if (moduloDto.activo() != null) {
            moduloExistente.setActivo(moduloDto.activo());
        }

        Modulo moduloActualizado = moduloDao.save(moduloExistente);

        auditoriaService.registrar(
                "UPDATE",
                ENTIDAD_MODULO,
                id,
                datosAnteriores,
                moduloActualizado.toString());

        return response(
                HttpStatus.OK,
                MensajeUtil.REGISTRO_ACTUALIZADO,
                List.of(moduloActualizado));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> eliminar(Long id) {

        validarId(id);

        Modulo modulo = obtenerModulo(id);

        String datosAnteriores = modulo.toString();

        modulo.setActivo(false);

        Modulo moduloActualizado = moduloDao.save(modulo);

        auditoriaService.registrar(
                "DEACTIVATE",
                ENTIDAD_MODULO,
                id,
                datosAnteriores,
                moduloActualizado.toString());

        return response(
                HttpStatus.OK,
                MensajeUtil.REGISTRO_ELIMINADO,
                null);
    }

    private Modulo convertirEntidad(ModuloDto moduloDto) {

        Modulo.ModuloBuilder builder = Modulo.builder()
                .nombre(moduloDto.nombre())
                .ruta(moduloDto.ruta())
                .icono(moduloDto.icono())
                .orden(moduloDto.orden());

        if (moduloDto.activo() != null) {
            builder.activo(moduloDto.activo());
        }

        return builder.build();
    }

    private void prepararModuloPadre(
            Modulo modulo,
            Long moduloPadreId) {

        if (moduloPadreId == null) {
            modulo.setModuloPadre(null);
            return;
        }

        validarIdPadre(moduloPadreId);

        Modulo moduloPadre = obtenerModulo(moduloPadreId);

        modulo.setModuloPadre(moduloPadre);
    }

    private void actualizarModuloPadre(
            Modulo moduloExistente,
            Long moduloPadreId,
            Long id) {

        if (moduloPadreId == null) {
            moduloExistente.setModuloPadre(null);
            return;
        }

        validarIdPadre(moduloPadreId);

        if (moduloPadreId.equals(id)) {
            throw new IllegalArgumentException(
                    "Un modulo no puede ser padre de si mismo");
        }

        moduloExistente.setModuloPadre(
                obtenerModulo(moduloPadreId));
    }

    private Modulo obtenerModulo(Long id) {

        return moduloDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MensajeUtil.REGISTRO_NO_ENCONTRADO));
    }

    private void validarId(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El id del modulo es obligatorio");
        }
    }

    private void validarIdPadre(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "El id del modulo padre es obligatorio");
        }
    }

    private void validarModulo(ModuloDto moduloDto) {

        if (moduloDto == null) {
            throw new IllegalArgumentException(
                    "Los datos del modulo son obligatorios");
        }

        if (moduloDto.nombre() == null
                || moduloDto.nombre().isBlank()) {
            throw new IllegalArgumentException(
                    "El nombre del modulo es obligatorio");
        }

        if (moduloDto.orden() == null
                || moduloDto.orden() < 0) {
            throw new IllegalArgumentException(
                    "El orden del modulo es obligatorio");
        }
    }

    private ResponseEntity<ApiResponse<Map<String, Object>>> response(
            HttpStatus status,
            String mensaje,
            List<Modulo> modulos) {

        return ResponseEntity
                .status(status)
                .body(crearResponse(
                        status,
                        mensaje,
                        modulos));
    }

    private ApiResponse<Map<String, Object>> crearResponse(
            HttpStatus status,
            String mensaje,
            List<Modulo> modulos) {

        MetadataResponse metadata = new MetadataResponse(
                status.is2xxSuccessful()
                        ? "SUCCESS"
                        : "ERROR",
                String.valueOf(status.value()),
                mensaje);

        Map<String, Object> data = new LinkedHashMap<>();

        if (modulos != null) {
            data.put("modulos", modulos);
        }

        return new ApiResponse<>(metadata, data);
    }
}