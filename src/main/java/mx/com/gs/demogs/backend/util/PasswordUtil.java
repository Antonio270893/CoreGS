package mx.com.gs.demogs.backend.util;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PasswordUtil {

	private final PasswordEncoder passwordEncoder;

	public String encriptar(String contrasenia) {

		return passwordEncoder.encode(contrasenia);
	}

	public boolean validar(String contrasenia, String hash) {

		return passwordEncoder.matches(contrasenia, hash);
	}
}
