package mx.com.gs.demogs.backend.core.service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import mx.com.gs.demogs.backend.core.model.Usuario;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long expiration;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration}") long expiration) {

        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    @SuppressWarnings("java:S2143")
    public String generateToken(
            UserDetails userDetails,
            Usuario usuario) {

        Map<String, Object> claims = new HashMap<>();

        claims.put("id", usuario.getId());
        claims.put("numeroEmpleado", usuario.getNumeroEmpleado());
        claims.put("correo", usuario.getCorreo());
        claims.put("activo", usuario.getActivo());

        claims.put(
                "roles",
                userDetails.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList());

        Instant ahora = Instant.now();
        Instant expiracion = ahora.plusMillis(expiration);

        return Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(ahora))
                .expiration(Date.from(expiracion))
                .signWith(secretKey)
                .compact();
    }

    public String extractUsername(String token) {

        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(
            String token,
            UserDetails userDetails) {

        String username = extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    @SuppressWarnings("java:S2143")
    private boolean isTokenExpired(String token) {

        Date expirationDate = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        return expirationDate.toInstant()
                .isBefore(Instant.now());
    }

    public long getExpirationInSeconds() {

        return expiration / 1000;
    }

    public Long extractUsuarioId(String token) {

        Number id = (Number) Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("id");

        return id != null
                ? id.longValue()
                : null;
    }
}