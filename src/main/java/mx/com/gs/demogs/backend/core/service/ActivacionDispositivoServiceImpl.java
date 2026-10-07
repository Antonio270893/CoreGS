package mx.com.gs.demogs.backend.core.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.gs.demogs.backend.core.dao.IActivacionDispositivoDao;
import mx.com.gs.demogs.backend.core.dao.IDispositivoDao;
import mx.com.gs.demogs.backend.core.model.ActivacionDispositivo;
import mx.com.gs.demogs.backend.core.model.Dispositivo;
import mx.com.gs.demogs.backend.core.request.CorreoRequest;
import mx.com.gs.demogs.backend.core.response.ApiResponse;
import mx.com.gs.demogs.backend.core.response.MetadataResponse;
import mx.com.gs.demogs.backend.exception.ResourceNotFoundException;
import mx.com.gs.demogs.backend.util.FechaUtil;
import mx.com.gs.demogs.backend.util.MensajeUtil;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivacionDispositivoServiceImpl implements IActivacionDispositivoService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter
            .ofPattern(
                    "dd 'de' MMMM 'de' yyyy 'a las' HH:mm",
                    Locale.forLanguageTag("es-MX"));

    private static final String MENSAJE_DISPOSITIVO_ACTIVADO =
            "El dispositivo fue activado correctamente";

    @Value("${dispositivo.activacion.expiracion-minutos}")
    private int minutosExpiracion;

    @Value("${dispositivo.activacion.url}")
    private String urlActivacion;

    private final IActivacionDispositivoDao activacionDispositivoDao;
    private final IDispositivoDao dispositivoDao;
    private final ICorreoService correoService;

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> generar(
            Long dispositivoId) {

        if (dispositivoId == null || dispositivoId <= 0) {
            throw new IllegalArgumentException(
                    "El id del dispositivo es obligatorio");
        }

        Dispositivo dispositivo = dispositivoDao.findById(dispositivoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        MensajeUtil.REGISTRO_NO_ENCONTRADO));

        return generarActivacion(dispositivo);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> generarPorLogin(
            Long numeroEmpleado,
            String deviceId) {

        if (numeroEmpleado == null || numeroEmpleado <= 0) {
            throw new IllegalArgumentException(
                    "El número de empleado es obligatorio");
        }

        if (deviceId == null || deviceId.isBlank()) {
            throw new IllegalArgumentException(
                    "El identificador del dispositivo es obligatorio");
        }

        Dispositivo dispositivo = dispositivoDao
                .findByUsuarioNumeroEmpleadoAndDeviceId(
                        numeroEmpleado,
                        deviceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No se encontró el dispositivo"));

        return generarActivacion(dispositivo);
    }

    private ResponseEntity<ApiResponse<Map<String, Object>>> generarActivacion(
            Dispositivo dispositivo) {

        Long dispositivoId = dispositivo.getId();

        if (Boolean.TRUE.equals(dispositivo.getActivo())) {
            throw new IllegalArgumentException(
                    "El dispositivo ya se encuentra autorizado");
        }

        if (dispositivo.getUsuario() == null
                || dispositivo.getUsuario().getCorreo() == null
                || dispositivo.getUsuario().getCorreo().isBlank()) {
            throw new IllegalArgumentException(
                    "El usuario del dispositivo no tiene un correo registrado");
        }

        LocalDateTime ahora = FechaUtil.ahora();

        Iterable<ActivacionDispositivo> activaciones =
                activacionDispositivoDao.findAll();

        for (ActivacionDispositivo activacion : activaciones) {

            if (activacion.getDispositivo().getId().equals(dispositivoId)
                    && !Boolean.TRUE.equals(activacion.getUtilizado())) {

                activacion.setUtilizado(true);
                activacion.setFechaUtilizacion(ahora);
                activacionDispositivoDao.save(activacion);
            }
        }

        ActivacionDispositivo activacion = new ActivacionDispositivo();

        String token = UUID.randomUUID().toString();
        String codigo = generarCodigo();

        activacion.setDispositivo(dispositivo);
        activacion.setToken(token);
        activacion.setCodigo(codigo);
        activacion.setFechaCreacion(ahora);
        activacion.setFechaExpiracion(
                ahora.plusMinutes(minutosExpiracion));
        activacion.setUtilizado(false);

        ActivacionDispositivo guardada =
                activacionDispositivoDao.save(activacion);

        String url = urlActivacion + "?token=" + guardada.getToken();

        Map<String, Object> datosCorreo = new LinkedHashMap<>();

        datosCorreo.put(
                "nombre",
                dispositivo.getUsuario().getNombre());
        datosCorreo.put(
                "tipoDispositivo",
                dispositivo.getTipoDispositivo());
        datosCorreo.put(
                "sistemaOperativo",
                dispositivo.getSistemaOperativo());
        datosCorreo.put(
                "navegador",
                dispositivo.getNavegador());
        datosCorreo.put(
                "token",
                guardada.getToken());
        datosCorreo.put(
                "codigo",
                guardada.getCodigo());
        datosCorreo.put(
                "url",
                url);
        datosCorreo.put(
                "fechaExpiracion",
                guardada.getFechaExpiracion()
                        .format(FORMATO_FECHA));

        CorreoRequest correoRequest = new CorreoRequest();
        correoRequest.setPara(
                List.of(dispositivo.getUsuario().getCorreo()));
        correoRequest.setReferenciaPlantilla(
                "ACTIVACION_DISPOSITIVO");
        correoRequest.setDatos(datosCorreo);

        correoService.enviar(correoRequest);

        log.info(
                "Activación de dispositivo generada y enviada. Dispositivo ID: {}",
                dispositivoId);

        Map<String, Object> datos = new LinkedHashMap<>();
        datos.put(
                "fechaExpiracion",
                guardada.getFechaExpiracion());

        return response(
                HttpStatus.CREATED,
                MensajeUtil.REGISTRO_CREADO,
                datos);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> activarPorToken(
            String token) {

        validarToken(token);

        ActivacionDispositivo activacion =
                activacionDispositivoDao
                        .findByTokenAndUtilizadoFalse(token)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "El token de activación no es válido"));

        validarExpiracion(activacion);
        activarDispositivo(activacion);

        return response(
                HttpStatus.OK,
                MENSAJE_DISPOSITIVO_ACTIVADO,
                Map.of(
                        "dispositivoAutorizado",
                        true,
                        "mensaje",
                        MENSAJE_DISPOSITIVO_ACTIVADO));
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse<Map<String, Object>>> activarPorCodigo(
            String codigo) {

        validarCodigo(codigo);

        ActivacionDispositivo activacion =
                activacionDispositivoDao
                        .findByCodigoAndUtilizadoFalse(codigo)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "El código de activación no es válido"));

        validarExpiracion(activacion);
        activarDispositivo(activacion);

        return response(
                HttpStatus.OK,
                MENSAJE_DISPOSITIVO_ACTIVADO,
                Map.of(
                        "dispositivoAutorizado",
                        true,
                        "mensaje",
                        MENSAJE_DISPOSITIVO_ACTIVADO));
    }

    private void activarDispositivo(
            ActivacionDispositivo activacion) {

        LocalDateTime ahora = FechaUtil.ahora();

        Dispositivo dispositivo = activacion.getDispositivo();

        dispositivo.setActivo(true);
        dispositivo.setFechaActivacion(ahora);
        dispositivoDao.save(dispositivo);

        activacion.setUtilizado(true);
        activacion.setFechaUtilizacion(ahora);
        activacionDispositivoDao.save(activacion);

        log.info(
                "Dispositivo activado. Dispositivo ID: {}",
                dispositivo.getId());
    }

    private void validarExpiracion(
            ActivacionDispositivo activacion) {

        if (activacion.getFechaExpiracion() == null
                || activacion.getFechaExpiracion()
                        .isBefore(FechaUtil.ahora())) {

            throw new IllegalArgumentException(
                    "El token de activación ha expirado");
        }
    }

    private void validarToken(String token) {

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException(
                    "El token de activación es obligatorio");
        }
    }

    private void validarCodigo(String codigo) {

        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException(
                    "El código de activación es obligatorio");
        }
    }

    private String generarCodigo() {

        return String.format(
                "%06d",
                secureRandom.nextInt(1_000_000));
    }

    private ResponseEntity<ApiResponse<Map<String, Object>>> response(
            HttpStatus status,
            String mensaje,
            Map<String, Object> datos) {

        MetadataResponse metadata = new MetadataResponse(
                status.is2xxSuccessful()
                        ? "SUCCESS"
                        : "ERROR",
                String.valueOf(status.value()),
                mensaje);

        Map<String, Object> data = new LinkedHashMap<>();

        if (datos != null) {
            data.putAll(datos);
        }

        return ResponseEntity
                .status(status)
                .body(new ApiResponse<>(metadata, data));
    }
}