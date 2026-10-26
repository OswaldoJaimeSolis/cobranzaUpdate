package com.cobranzaplazas.seguridad;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

/** Emite los JWT (HS256) que el resource server valida en cada petición. */
@Service
public class TokenService {
	public static final String CLAIM_ROL = "rol";
	public static final String CLAIM_NOMBRE = "nombre";
	/** WEB (tabla usuario) o MOVIL (tabla recaudador) */
	public static final String CLAIM_TIPO = "tipo";
	public static final String TIPO_WEB = "WEB";
	public static final String TIPO_MOVIL = "MOVIL";

	private final JwtEncoder encoder;
	private final long horasWeb;
	private final long horasMovil;

	public TokenService(JwtEncoder encoder, @Value("${seguridad.jwt.horas-web:8}") long horasWeb,
			@Value("${seguridad.jwt.horas-movil:12}") long horasMovil) {
		this.encoder = encoder;
		this.horasWeb = horasWeb;
		this.horasMovil = horasMovil;
	}

	/** Respuesta del login: token, expiración (epoch en milisegundos) y datos del usuario. */
	public Map<String, Object> emitir(String usuario, String nombre, String rol, String tipo) {
		Instant ahora = Instant.now();
		Instant expira = ahora.plus(TIPO_MOVIL.equals(tipo) ? horasMovil : horasWeb, ChronoUnit.HOURS);
		JwtClaimsSet claims = JwtClaimsSet.builder().issuer("cobranza").issuedAt(ahora).expiresAt(expira)
				.subject(usuario).claim(CLAIM_NOMBRE, nombre != null ? nombre : usuario).claim(CLAIM_ROL, rol)
				.claim(CLAIM_TIPO, tipo).build();
		String token = encoder
				.encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
				.getTokenValue();

		Map<String, Object> respuesta = new LinkedHashMap<>();
		respuesta.put("token", token);
		respuesta.put("expira", expira.toEpochMilli());
		respuesta.put("usuario", usuario);
		respuesta.put("nombre", nombre != null ? nombre : usuario);
		respuesta.put("rol", rol);
		return respuesta;
	}
}
