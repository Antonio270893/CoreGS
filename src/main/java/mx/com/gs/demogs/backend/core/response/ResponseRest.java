package mx.com.gs.demogs.backend.core.response;

import org.springframework.http.HttpStatus;

import lombok.Data;

@Data
public class ResponseRest {

    private MetadataResponse metadata;

    public void setMetadata(
            HttpStatus codigo,
            String mensaje) {

        String status = codigo.is2xxSuccessful()
                ? "SUCCESS"
                : "ERROR";

        this.metadata = new MetadataResponse(
            status,
            String.valueOf(codigo.value()),
            mensaje
        );
    }
}