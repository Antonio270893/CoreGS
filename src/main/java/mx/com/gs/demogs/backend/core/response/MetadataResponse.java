package mx.com.gs.demogs.backend.core.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MetadataResponse {

	private String status;
	private String code;
	private String message;
}