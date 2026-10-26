package com.cobranzaplazas.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.cobranzaplazas.seguridad.Roles;
import com.cobranzaplazas.seguridad.TokenService;
import com.nimbusds.jose.jwk.source.ImmutableSecret;

/**
 * API sin estado protegida con JWT (Authorization: Bearer). El token se obtiene en
 * /auth/login (usuarios web) o /auth/movil/login (recaudadores).
 *
 * Reglas: endpoints de la app móvil para RECAUDADOR y ADMIN; /usuarios sólo ADMIN;
 * cualquier otro GET (catálogos y reportes) para ADMIN y CONSULTA; el resto de las
 * escrituras sólo ADMIN.
 *
 * seguridad.exigir-autenticacion=false deja toda la API abierta (los tokens se siguen
 * emitiendo y aceptando); sirve para desplegar el backend antes de que la web y todos
 * los equipos móviles tengan la versión que manda el token.
 */
@Configuration
@EnableMethodSecurity
public class SpringSecurityConfig {

	/** Endpoints que usa la app móvil (WebService.java). */
	private static final String[] RUTAS_MOVIL = { "/getTipoPlaza", "/getContribuyentes", "/getContribuciones",
			"/getRecaudadores", "/getConfiguracionCobro", "/insert_*" };

	@Value("${seguridad.exigir-autenticacion:true}")
	private boolean exigirAutenticacion;

	@Value("${seguridad.cors.origenes:http://localhost:4200}")
	private List<String> origenesCors;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.cors(Customizer.withDefaults())
				.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.oauth2ResourceServer(rs -> rs.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
				.authorizeHttpRequests(requests -> {
					requests.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
							.requestMatchers("/auth/login", "/auth/movil/login", "/error").permitAll();
					if (!exigirAutenticacion) {
						requests.anyRequest().permitAll();
						return;
					}
					requests.requestMatchers("/auth/**").authenticated()
							.requestMatchers(RUTAS_MOVIL).hasAnyRole(Roles.RECAUDADOR, Roles.ADMIN)
							.requestMatchers("/usuarios", "/usuarios/**").hasRole(Roles.ADMIN)
							.requestMatchers(HttpMethod.GET, "/**").hasAnyRole(Roles.ADMIN, Roles.CONSULTA)
							.anyRequest().hasRole(Roles.ADMIN);
				});
		return http.build();
	}

	/** El rol del token (claim "rol") se convierte en la autoridad ROLE_<rol>. */
	private JwtAuthenticationConverter jwtAuthenticationConverter() {
		JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
		roles.setAuthoritiesClaimName(TokenService.CLAIM_ROL);
		roles.setAuthorityPrefix("ROLE_");
		JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
		converter.setJwtGrantedAuthoritiesConverter(roles);
		return converter;
	}

	@Bean
	public SecretKey jwtSecretKey(@Value("${seguridad.jwt.secreto}") String secreto) {
		byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
		if (bytes.length < 32) {
			throw new IllegalStateException("seguridad.jwt.secreto debe tener al menos 32 bytes");
		}
		return new SecretKeySpec(bytes, "HmacSHA256");
	}

	@Bean
	public JwtEncoder jwtEncoder(SecretKey jwtSecretKey) {
		return new NimbusJwtEncoder(new ImmutableSecret<>(jwtSecretKey));
	}

	@Bean
	public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
		return NimbusJwtDecoder.withSecretKey(jwtSecretKey).macAlgorithm(MacAlgorithm.HS256).build();
	}

	/**
	 * BCrypt para las contraseñas nuevas ({bcrypt}...). Una contraseña sin prefijo es texto
	 * plano heredado: se acepta y se vuelve a guardar con BCrypt al iniciar sesión
	 * (ver MigracionContrasenas, que además las convierte todas al arrancar).
	 */
	@Bean
	@SuppressWarnings("deprecation")
	public PasswordEncoder passwordEncoder() {
		DelegatingPasswordEncoder encoder = (DelegatingPasswordEncoder) PasswordEncoderFactories
				.createDelegatingPasswordEncoder();
		encoder.setDefaultPasswordEncoderForMatches(NoOpPasswordEncoder.getInstance());
		return encoder;
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(origenesCors);
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
		config.setExposedHeaders(List.of("Content-Disposition"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}
}
