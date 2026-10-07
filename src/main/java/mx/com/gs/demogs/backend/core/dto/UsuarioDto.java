package mx.com.gs.demogs.backend.core.dto;

public record UsuarioDto(
        String nombre,
        Long numeroEmpleado,
        String ceco,
        String correo,
        String contrasenia,
        Boolean activo) {
}