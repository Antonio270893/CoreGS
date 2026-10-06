package mx.com.gs.demogs.backend.core.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import mx.com.gs.demogs.backend.core.dao.IRefreshTokenDao;
import mx.com.gs.demogs.backend.core.model.RefreshToken;
import mx.com.gs.demogs.backend.exception.RefreshTokenExpiredException;
import mx.com.gs.demogs.backend.exception.RefreshTokenNotFoundException;
import mx.com.gs.demogs.backend.exception.RefreshTokenRevokedException;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final IRefreshTokenDao refreshTokenDao;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;

    @Transactional
    public RefreshToken crear(
            Long numeroEmpleado) {

        refreshTokenDao.deleteByNumeroEmpleado(
                numeroEmpleado
        );

        RefreshToken refreshToken =
                new RefreshToken();

        refreshToken.setToken(
                UUID.randomUUID().toString()
        );

        refreshToken.setNumeroEmpleado(
                numeroEmpleado
        );

        refreshToken.setFechaExpiracion(
                Instant.now()
                        .plusMillis(
                                refreshExpiration
                        )
        );

        refreshToken.setRevocado(false);

        return refreshTokenDao.save(
                refreshToken
        );
    }

    @Transactional(readOnly = true)
    public RefreshToken validar(
            String token) {

        RefreshToken refreshToken =
                refreshTokenDao.findByToken(token)
                        .orElseThrow(() ->
                                new RefreshTokenNotFoundException(
                                        "Refresh token no encontrado"
                                )
                        );

        if (refreshToken.isRevocado()) {
            throw new RefreshTokenRevokedException(
                    "Refresh token revocado"
            );
        }

        if (refreshToken.getFechaExpiracion()
                .isBefore(Instant.now())) {

            throw new RefreshTokenExpiredException(
                    "Refresh token expirado"
            );
        }

        return refreshToken;
    }

    @Transactional
    public void revocar(
            String token) {

        RefreshToken refreshToken =
                refreshTokenDao.findByToken(token)
                        .orElseThrow(() ->
                                new RefreshTokenNotFoundException(
                                        "Refresh token no encontrado"
                                )
                        );

        refreshToken.setRevocado(true);

        refreshTokenDao.save(
                refreshToken
        );
    }
}