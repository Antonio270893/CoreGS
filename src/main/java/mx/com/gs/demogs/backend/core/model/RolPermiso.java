package mx.com.gs.demogs.backend.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "rol_permiso")
@IdClass(RolPermisoId.class)
@Data
public class RolPermiso {

	@Id
	@Column(name = "rol_id")
	private Long rolId;

	@Id
	@Column(name = "permiso_id")
	private Long permisoId;
}