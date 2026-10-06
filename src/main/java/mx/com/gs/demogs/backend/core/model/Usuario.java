package mx.com.gs.demogs.backend.core.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "usuario")
@Getter
@Setter
@NoArgsConstructor
@JsonPropertyOrder({ "id", "nombre", "numeroEmpleado", "ceco", "correo", "activo", "contrasenia" })
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "nombre", nullable = false)
	private String nombre;

	@Column(name = "numero_empleado", nullable = false, unique = true)
	private Long numeroEmpleado;

	@Column(name = "correo", nullable = false, unique = true)
	private String correo;

	@Column(name = "ceco", nullable = false, unique = true)
	private String ceco;

	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(name = "contrasenia", nullable = false)
	private String contrasenia;

	@Column(name = "activo", nullable = false)
	private Boolean activo;
}