package mx.com.gs.demogs.backend.core.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuDto {

	private Long id;
	private String nombre;
	private String ruta;
	private String icono;
	private Integer orden;
	private List<MenuDto> submodulos;
}