package mx.com.gs.demogs.backend.core.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class RolModuloId implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long rolId;

    private Long moduloId;
}