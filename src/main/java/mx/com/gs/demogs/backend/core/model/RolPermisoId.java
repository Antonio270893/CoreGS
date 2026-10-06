package mx.com.gs.demogs.backend.core.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class RolPermisoId implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4151454624188575170L;
	private Long rolId;
    private Long permisoId;
}