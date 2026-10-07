package mx.com.gs.demogs.backend.core.service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.gs.demogs.backend.core.dao.IActivacionDispositivoDao;
import mx.com.gs.demogs.backend.core.dao.IDispositivoDao;
import mx.com.gs.demogs.backend.core.dao.IUsuarioDao;
import mx.com.gs.demogs.backend.core.dto.DispositivoDto;
import mx.com.gs.demogs.backend.core.model.Dispositivo;
import mx.com.gs.demogs.backend.core.model.Usuario;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.response.MetadataResponse;
import mx.com.gs.demogs.backend.exception.ResourceNotFoundException;
import mx.com.gs.demogs.backend.util.FechaUtil;
import mx.com.gs.demogs.backend.util.MensajeUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class DispositivoServiceImpl implements IDispositivoService {

    private final IActivacionDispositivoDao activacionDispositivoDao;
    private final IDispositivoDao dispositivoDao;
    private final IUsuarioDao usuarioDao;

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorUsuarioYDeviceId(
            Long usuarioId,
            String deviceId) {

        validarUsuarioId(usuarioId);
        validarDeviceId(deviceId);

        return dispositivoDao
                .findByUsuarioIdAndDeviceId(usuarioId, deviceId)
                .map(dispositivo -> response(
                        HttpStatus.OK,
                        MensajeUtil.CONSULTA_EXITOSA,
                        List.of(dispositivo)))
                .orElseGet(() -> response(
                        HttpStatus.NOT_FOUND,
                        MensajeUtil.REGISTRO_NO_ENCONTRADO,
                        null));
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaAutorizado(
            Long usuarioId,
            String deviceId) {

        validarUsuarioId(usuarioId);
        validarDeviceId(deviceId);

        return dispositivoDao
                .findByUsuarioIdAndDeviceIdAndActivoTrue(
                        usuarioId,
                        deviceId)
                .isPresent();
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> registrar(
            DispositivoDto dispositivoDto,
            boolean activo) {

        validarDispositivo(dispositivoDto);

        Dispositivo existente = dispositivoDao
                .findByUsuarioIdAndDeviceId(
                        dispositivoDto.usuarioId(),
                        dispositivoDto.deviceId())
                .orElse(null);

        if (existente != null) {
            throw new IllegalArgumentException(
                    "El dispositivo ya se encuentra registrado");
        }

        Usuario usuario = usuarioDao
                .findById(dispositivoDto.usuarioId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Usuario no encontrado"));

        Dispositivo dispositivo = new Dispositivo();

        dispositivo.setUsuario(usuario);
        dispositivo.setDeviceId(dispositivoDto.deviceId());
        dispositivo.setDeviceFingerprint(
                dispositivoDto.deviceFingerprint());
        dispositivo.setTipoDispositivo(
                dispositivoDto.tipoDispositivo());
        dispositivo.setSistemaOperativo(
                dispositivoDto.sistemaOperativo());
        dispositivo.setNavegador(
                dispositivoDto.navegador());
        dispositivo.setActivo(activo);
        dispositivo.setFechaActivacion(
                activo ? FechaUtil.ahora() : null);

        Dispositivo dispositivoGuardado =
                dispositivoDao.save(dispositivo);

        return response(
                HttpStatus.CREATED,
                MensajeUtil.REGISTRO_CREADO,
                List.of(dispositivoGuardado));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> actualizarUltimoAcceso(
            Long usuarioId,
            String deviceId) {

        validarUsuarioId(usuarioId);
        validarDeviceId(deviceId);

        Dispositivo dispositivo =
                obtenerDispositivo(usuarioId, deviceId);

        if (!Boolean.TRUE.equals(dispositivo.getActivo())) {
            throw new IllegalArgumentException(
                    "El dispositivo no se encuentra autorizado");
        }

        dispositivo.setUltimoAcceso(
                FechaUtil.ahora());

        Dispositivo dispositivoActualizado =
                dispositivoDao.save(dispositivo);

        return response(
                HttpStatus.OK,
                MensajeUtil.REGISTRO_ACTUALIZADO,
                List.of(dispositivoActualizado));
    }

    @Override
    @Transactional(readOnly = true)
    public Dispositivo obtenerPorUsuarioYDeviceId(
            Long usuarioId,
            String deviceId) {

        validarUsuarioId(usuarioId);
        validarDeviceId(deviceId);

        return dispositivoDao
                .findByUsuarioIdAndDeviceId(
                        usuarioId,
                        deviceId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean fingerprintCoincide(
            Long usuarioId,
            String deviceId,
            String deviceFingerprint) {

        validarUsuarioId(usuarioId);
        validarDeviceId(deviceId);

        if (deviceFingerprint == null
                || deviceFingerprint.isBlank()) {
            return false;
        }

        Dispositivo dispositivo = dispositivoDao
                .findByUsuarioIdAndDeviceId(
                        usuarioId,
                        deviceId)
                .orElse(null);

        if (dispositivo == null) {
            return false;
        }

        if (dispositivo.getDeviceFingerprint() == null
                || dispositivo.getDeviceFingerprint().isBlank()) {
            return false;
        }

        return dispositivo.getDeviceFingerprint()
                .equals(deviceFingerprint);
    }

    private Dispositivo obtenerDispositivo(
            Long usuarioId,
            String deviceId) {

        return dispositivoDao
                .findByUsuarioIdAndDeviceId(
                        usuarioId,
                        deviceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MensajeUtil.REGISTRO_NO_ENCONTRADO));
    }

    private void validarUsuarioId(Long usuarioId) {

        if (usuarioId == null || usuarioId <= 0) {
            throw new IllegalArgumentException(
                    "El id del usuario es obligatorio");
        }
    }

    private void validarDeviceId(String deviceId) {

        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException(
                    "El deviceId es obligatorio");
        }
    }

    private void validarDispositivo(
            DispositivoDto dispositivoDto) {

        if (dispositivoDto == null) {
            throw new IllegalArgumentException(
                    "Los datos del dispositivo son obligatorios");
        }

        validarUsuarioId(dispositivoDto.usuarioId());
        validarDeviceId(dispositivoDto.deviceId());

        if (dispositivoDto.deviceFingerprint() == null
                || dispositivoDto.deviceFingerprint().isBlank()) {
            throw new IllegalArgumentException(
                    "La huella del dispositivo es obligatoria");
        }

        if (dispositivoDto.tipoDispositivo() == null
                || dispositivoDto.tipoDispositivo().isBlank()) {
            throw new IllegalArgumentException(
                    "El tipo de dispositivo es obligatorio");
        }

        if (dispositivoDto.sistemaOperativo() == null
                || dispositivoDto.sistemaOperativo().isBlank()) {
            throw new IllegalArgumentException(
                    "El sistema operativo es obligatorio");
        }

        if (dispositivoDto.navegador() == null
                || dispositivoDto.navegador().isBlank()) {
            throw new IllegalArgumentException(
                    "El navegador es obligatorio");
        }
    }

    private ResponseEntity<ApiResponse<Map<String, Object>>> response(
            HttpStatus status,
            String mensaje,
            List<Dispositivo> dispositivos) {

        return ResponseEntity
                .status(status)
                .body(crearResponse(
                        status,
                        mensaje,
                        dispositivos));
    }

    private ApiResponse<Map<String, Object>> crearResponse(
            HttpStatus status,
            String mensaje,
            List<Dispositivo> dispositivos) {

        MetadataResponse metadata =
                new MetadataResponse(
                        status.is2xxSuccessful()
                                ? "SUCCESS"
                                : "ERROR",
                        String.valueOf(status.value()),
                        mensaje);

        Map<String, Object> data =
                new LinkedHashMap<>();

        if (dispositivos != null) {
            data.put("dispositivos", dispositivos);
        }

        return new ApiResponse<>(
                metadata,
                data);
    }

    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<ApiResponse<Map<String, Object>>> buscarPorUsuario(
            Long usuarioId) {

        validarUsuarioId(usuarioId);

        List<Dispositivo> dispositivos =
                dispositivoDao
                        .findByUsuarioIdOrderByUltimoAccesoDesc(
                                usuarioId);

        return response(
                HttpStatus.OK,
                MensajeUtil.CONSULTA_EXITOSA,
                dispositivos);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> eliminarDispositivo(
            Long usuarioId,
            String deviceId) {

        validarUsuarioId(usuarioId);
        validarDeviceId(deviceId);

        Dispositivo dispositivo =
                obtenerDispositivo(
                        usuarioId,
                        deviceId);

        activacionDispositivoDao
                .deleteByDispositivoId(
                        dispositivo.getId());

        dispositivoDao.delete(dispositivo);

        return response(
                HttpStatus.OK,
                "Dispositivo eliminado correctamente",
                List.of(dispositivo));
    }
}