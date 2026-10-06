package mx.com.gs.demogs.backend.core.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mx.com.gs.demogs.backend.core.model.RefreshToken;

public interface IRefreshTokenDao extends JpaRepository<RefreshToken, Long> {

	Optional<RefreshToken> findByToken(String token);

	void deleteByNumeroEmpleado(Long numeroEmpleado);
}