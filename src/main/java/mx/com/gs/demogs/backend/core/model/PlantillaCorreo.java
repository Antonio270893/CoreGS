package mx.com.gs.demogs.backend.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "plantilla_correo")
@Data
public class PlantillaCorreo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String referencia;

	@Column(nullable = false, length = 150)
	private String nombre;

	@Column(nullable = false, length = 255)
	private String asunto;

	@Column(nullable = false, columnDefinition = "LONGTEXT")
	private String contenido;

	@Column(nullable = false)
	private Boolean activo;
}