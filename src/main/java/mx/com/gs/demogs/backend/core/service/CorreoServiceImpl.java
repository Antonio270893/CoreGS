package mx.com.gs.demogs.backend.core.service;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import mx.com.gs.demogs.backend.core.dao.IPlantillaCorreoDao;
import mx.com.gs.demogs.backend.core.model.PlantillaCorreo;
import mx.com.gs.demogs.backend.core.request.CorreoRequest;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorreoServiceImpl implements ICorreoService {

    private static final Pattern VARIABLE_PATTERN =
            Pattern.compile("\\{\\{\\s*([^{}]++)\\s*\\}\\}");

    private final JavaMailSender mailSender;
    private final IPlantillaCorreoDao plantillaCorreoDao;

    @Override
    public void enviar(CorreoRequest request) {

        log.info("===== CORREO SERVICE INICIADO =====");

        validarRequest(request);

        log.info(
                "Preparando envío de correo a: {}",
                request.getPara());

        String asunto = request.getAsunto();
        String contenido = request.getContenido();

        if (request.getReferenciaPlantilla() != null
                && !request.getReferenciaPlantilla().isBlank()) {

            log.info(
                    "Buscando plantilla: {}",
                    request.getReferenciaPlantilla());

            PlantillaCorreo plantilla =
                    plantillaCorreoDao
                            .findByReferenciaAndActivoTrue(
                                    request.getReferenciaPlantilla())
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "No se encontró la plantilla de correo: "
                                                    + request.getReferenciaPlantilla()));

            log.info(
                    "Plantilla encontrada: {}",
                    plantilla.getReferencia());

            contenido =
                    reemplazarVariables(
                            plantilla.getContenido(),
                            request.getDatos());

            if (asunto == null || asunto.isBlank()) {

                asunto =
                        reemplazarVariables(
                                plantilla.getAsunto(),
                                request.getDatos());
            }
        }

        if (contenido == null || contenido.isBlank()) {

            throw new IllegalArgumentException(
                    "El contenido del correo es obligatorio");
        }

        if (asunto == null || asunto.isBlank()) {

            throw new IllegalArgumentException(
                    "El asunto del correo es obligatorio");
        }

        log.info("===== DATOS DEL CORREO =====");

        log.info(
                "Para: {}",
                request.getPara());

        log.info(
                "CC: {}",
                request.getCc());

        log.info(
                "CCO: {}",
                request.getCco());

        log.info(
                "Asunto: {}",
                asunto);

        log.info(
                "Referencia plantilla: {}",
                request.getReferenciaPlantilla());

        log.info(
                "Datos: {}",
                request.getDatos());

        log.info(
                "Contenido: {}",
                contenido);

        log.info("============================");

        enviarCorreo(
                request,
                asunto,
                contenido);
    }

    private void enviarCorreo(
            CorreoRequest request,
            String asunto,
            String contenido) {

        try {

            MimeMessage mensaje =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            mensaje,
                            true,
                            "UTF-8");

            helper.setTo(
                    request.getPara()
                            .toArray(new String[0]));

            agregarCc(
                    helper,
                    request.getCc());

            agregarCco(
                    helper,
                    request.getCco());

            helper.setSubject(asunto);

            helper.setText(
                    contenido,
                    true);

            log.info(
                    "Enviando correo mediante SMTP...");

            mailSender.send(mensaje);

            log.info(
                    "Correo enviado correctamente a: {}",
                    request.getPara());

        } catch (MessagingException e) {

            log.error(
                    "Error al preparar el correo para: {}",
                    request.getPara(),
                    e);

            throw new IllegalStateException(
                    "No fue posible preparar el correo",
                    e);

        } catch (Exception e) {

            log.error(
                    "Error al enviar el correo para: {}",
                    request.getPara(),
                    e);

            throw new IllegalStateException(
                    "No fue posible enviar el correo",
                    e);
        }
    }

    private void agregarCc(
            MimeMessageHelper helper,
            List<String> cc)
            throws MessagingException {

        if (cc != null && !cc.isEmpty()) {

            helper.setCc(
                    cc.toArray(new String[0]));
        }
    }

    private void agregarCco(
            MimeMessageHelper helper,
            List<String> cco)
            throws MessagingException {

        if (cco != null && !cco.isEmpty()) {

            helper.setBcc(
                    cco.toArray(new String[0]));
        }
    }

    private String reemplazarVariables(
            String contenido,
            Map<String, Object> datos) {

        if (contenido == null || contenido.isBlank()) {
            return contenido;
        }

        if (datos == null || datos.isEmpty()) {
            return contenido;
        }

        Matcher matcher =
                VARIABLE_PATTERN.matcher(contenido);

        StringBuffer resultado =
                new StringBuffer();

        while (matcher.find()) {

            String variable =
                    matcher.group(1).trim();

            Object valor =
                    obtenerValor(
                            datos,
                            variable);

            String reemplazo =
                    valor != null
                            ? String.valueOf(valor)
                            : matcher.group(0);

            matcher.appendReplacement(
                    resultado,
                    Matcher.quoteReplacement(
                            reemplazo));
        }

        matcher.appendTail(resultado);

        return resultado.toString();
    }

    private Object obtenerValor(
            Map<String, Object> datos,
            String variable) {

        if (!variable.contains(".")) {
            return datos.get(variable);
        }

        String[] niveles =
                variable.split("\\.");

        Object valor = datos;

        for (String nivel : niveles) {

            if (!(valor instanceof Map<?, ?> mapa)) {
                return null;
            }

            valor = mapa.get(nivel);

            if (valor == null) {
                return null;
            }
        }

        return valor;
    }

    private void validarRequest(
            CorreoRequest request) {

        if (request == null) {

            throw new IllegalArgumentException(
                    "La solicitud de correo es obligatoria");
        }

        if (request.getPara() == null
                || request.getPara().isEmpty()) {

            throw new IllegalArgumentException(
                    "Debe indicar al menos un destinatario");
        }

        boolean tienePlantilla =
                request.getReferenciaPlantilla() != null
                        && !request.getReferenciaPlantilla()
                                .isBlank();

        boolean tieneContenido =
                request.getContenido() != null
                        && !request.getContenido().isBlank();

        if (!tienePlantilla && !tieneContenido) {

            throw new IllegalArgumentException(
                    "Debe indicar una plantilla o contenido de correo");
        }
    }
}