package mx.com.gs.demogs.backend.core.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import lombok.RequiredArgsConstructor;
import mx.com.gs.demogs.backend.core.filter.JwtReqFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ConfigSecurity {
	@Bean
	public UserDetailsService userDetailsService(DataSource dataSource) {

		JdbcUserDetailsManager manager = new JdbcUserDetailsManager(dataSource);
		// Prueba Jenkins
		manager.setUsersByUsernameQuery("""
				    SELECT numero_empleado,
				           contrasenia,
				           activo
				    FROM usuario
				    WHERE numero_empleado = ?
				""");

		manager.setAuthoritiesByUsernameQuery("""
				    SELECT u.numero_empleado,
				           CONCAT('ROLE_', r.nombre)
				    FROM usuario u
				    INNER JOIN usuario_rol ur
				        ON ur.usuario_id = u.id
				    INNER JOIN rol r
				        ON r.id = ur.rol_id
				    WHERE u.numero_empleado = ?
				      AND r.activo = TRUE
				""");

		return manager;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

		return configuration.getAuthenticationManager();
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http, JwtReqFilter jwtReqFilter,
			DbAuthorizationManager dbAuthorizationManager) throws Exception {

		http.cors(Customizer.withDefaults())

				.csrf(csrf -> csrf.disable())

				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.exceptionHandling(exceptions -> exceptions

						.authenticationEntryPoint((request, response, exception) -> {

							response.setStatus(HttpStatus.UNAUTHORIZED.value());

							response.setContentType("application/json");

							response.setCharacterEncoding("UTF-8");

							response.getWriter().write("""
									    {
									        "status": 401,
									        "error": "Unauthorized",
									        "message": "No autenticado"
									    }
									""");
						})

						.accessDeniedHandler((request, response, exception) -> {

							response.setStatus(HttpStatus.FORBIDDEN.value());

							response.setContentType("application/json");

							response.setCharacterEncoding("UTF-8");

							response.getWriter().write("""
									    {
									        "status": 403,
									        "error": "Forbidden",
									        "message": "No tiene permisos para realizar esta operación"
									    }
									""");
						}))

				.authorizeHttpRequests(configure -> configure

						.requestMatchers("/v1/core/authenticate").permitAll()

						.requestMatchers("/v1/core/auth/refresh").permitAll()

						.requestMatchers("/v1/core/auth/logout").permitAll()

						.requestMatchers("/v1/core/correo/enviar").permitAll()
						
						.requestMatchers("/v1/core/dispositivo/activacion/**").permitAll()

						.requestMatchers("/v1/core/authenticate")

						.permitAll().requestMatchers("/v1/core/dispositivo/**").authenticated()

						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

						.anyRequest().access(dbAuthorizationManager))

				.addFilterBefore(jwtReqFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}