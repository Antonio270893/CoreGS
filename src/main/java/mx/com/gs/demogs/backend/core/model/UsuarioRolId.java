package mx.com.gs.demogs.backend.core.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class UsuarioRolId implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = -4021249179821817070L;
	private Long usuarioId;
    private Long rolId;
}