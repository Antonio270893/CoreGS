package mx.com.gs.demogs.backend.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "usuario_rol")
@IdClass(UsuarioRolId.class)
@Data
public class UsuarioRol {

	@Id
	@Column(name = "usuario_id")
	private Long usuarioId;

	@Id
	@Column(name = "rol_id")
	private Long rolId;
}