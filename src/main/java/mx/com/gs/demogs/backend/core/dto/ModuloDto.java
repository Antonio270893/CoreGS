package mx.com.gs.demogs.backend.core.dto;

public record ModuloDto(
        String nombre,
        String ruta,
        String icono,
        Long moduloPadreId,
        Integer orden,
        Boolean activo) {
}