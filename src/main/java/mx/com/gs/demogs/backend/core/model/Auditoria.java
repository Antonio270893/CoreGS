
package mx.com.gs.demogs.backend.core.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "auditoria")
@Getter
@Setter
@NoArgsConstructor
public class Auditoria {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@Column(nullable = false)
	private LocalDateTime fecha;

	@Column(nullable = false, length = 30)
	private String accion;

	@Column(nullable = false, length = 100)
	private String tabla;

	@Column(name = "registro_id")
	private Long registroId;

	@Column(name = "datos_anteriores", columnDefinition = "JSON")
	private String datosAnteriores;

	@Column(name = "datos_nuevos", columnDefinition = "JSON")
	private String datosNuevos;
}
