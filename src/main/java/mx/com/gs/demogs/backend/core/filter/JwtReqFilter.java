
package mx.com.gs.demogs.backend.core.filter;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import mx.com.gs.demogs.backend.core.service.JwtService;

@Component
@RequiredArgsConstructor
public class JwtReqFilter extends OncePerRequestFilter {

	private final UserDetailsService userDetailsService;
	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		String authorizationHeader = request.getHeader("Authorization");

		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {

			chain.doFilter(request, response);
			return;
		}

		String jwt = authorizationHeader.substring(7).trim();

		if (jwt.isBlank()) {
			sendUnauthorized(response);
			return;
		}

		try {

			String username = jwtService.extractUsername(jwt);

			if (SecurityContextHolder.getContext().getAuthentication() == null) {

				UserDetails userDetails = userDetailsService.loadUserByUsername(username);

				if (!jwtService.validateToken(jwt, userDetails)) {
					sendUnauthorized(response);
					return;
				}

				Long usuarioId = jwtService.extractUsuarioId(jwt);

				if (usuarioId == null || usuarioId <= 0) {
					sendUnauthorized(response);
					return;
				}

				UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails, usuarioId, userDetails.getAuthorities());

				authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

				SecurityContextHolder.getContext().setAuthentication(authenticationToken);
			}

		} catch (JwtException | IllegalArgumentException e) {

			sendUnauthorized(response);
			return;

		} catch (Exception e) {

			sendUnauthorized(response);
			return;
		}

		chain.doFilter(request, response);
	}

	private void sendUnauthorized(HttpServletResponse response) throws IOException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

		response.setContentType("application/json");

		response.setCharacterEncoding("UTF-8");

		response.getWriter().write("""
				{
				    "status": 401,
				    "error": "Unauthorized",
				    "message": "Token inválido, expirado o usuario no válido"
				}
				""");
	}
}
