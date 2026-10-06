package mx.com.gs.demogs.backend.core.request;

import lombok.Data;

@Data
public class ActivacionDispositivoLoginRequest {

    private Long numeroEmpleado;

    private String deviceId;
}