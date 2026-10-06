package mx.com.gs.demogs.backend.core.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private String refreshToken;
}