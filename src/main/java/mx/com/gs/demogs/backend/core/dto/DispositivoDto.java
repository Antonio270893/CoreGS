package mx.com.gs.demogs.backend.core.dto;

public record DispositivoDto(
        Long usuarioId,
        String deviceId,
        String deviceFingerprint,
        String tipoDispositivo,
        String sistemaOperativo,
        String navegador
) {
}