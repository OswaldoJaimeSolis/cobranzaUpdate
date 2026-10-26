package com.cobranzaplazas.seguridad;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cobranzaplazas.repo.Recaudador;
import com.cobranzaplazas.repo.RecaudadorDao;
import com.cobranzaplazas.repo.Usuario;
import com.cobranzaplazas.repo.UsuarioDao;

@RestController
@RequestMapping("/auth")
public class AuthController {
	private static final String CREDENCIALES_INCORRECTAS = "Usuario o contraseña incorrectos";

	private final UsuarioDao usuarioDao;
	private final RecaudadorDao recaudadorDao;
	private final PasswordEncoder passwordEncoder;
	private final TokenService tokenService;

	public AuthController(UsuarioDao usuarioDao, RecaudadorDao recaudadorDao, PasswordEncoder passwordEncoder,
			TokenService tokenService) {
		this.usuarioDao = usuarioDao;
		this.recaudadorDao = recaudadorDao;
		this.passwordEncoder = passwordEncoder;
		this.tokenService = tokenService;
	}

	/** Login del sistema web: {usuario, password} */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
		String password = credenciales.get("password");
		Optional<Usuario> encontrado = Optional.ofNullable(credenciales.get("usuario")).flatMap(usuarioDao::findById);
		if (encontrado.isEmpty() || password == null || !passwordEncoder.matches(password, encontrado.get().getPassword())) {
			return rechazo(CREDENCIALES_INCORRECTAS);
		}
		Usuario usuario = encontrado.get();
		if (!Boolean.TRUE.equals(usuario.getActivo())) {
			return rechazo("El usuario no está activo");
		}
		if (passwordEncoder.upgradeEncoding(usuario.getPassword())) {
			usuario.setPassword(passwordEncoder.encode(password));
			usuarioDao.save(usuario);
		}
		return ResponseEntity.ok(tokenService.emitir(usuario.getUsuario(), usuario.getNombre(), usuario.getRol(),
				TokenService.TIPO_WEB));
	}

	/**
	 * Login de la app móvil: {usuario, password} con el código y la contraseña del
	 * recaudador. La app inicia sesión localmente y sólo pide el token al sincronizar.
	 */
	@PostMapping("/movil/login")
	public ResponseEntity<?> loginMovil(@RequestBody Map<String, String> credenciales) {
		String password = credenciales.get("password");
		Optional<Recaudador> encontrado = Optional.ofNullable(credenciales.get("usuario"))
				.flatMap(recaudadorDao::findById);
		if (encontrado.isEmpty() || password == null
				|| !passwordEncoder.matches(password, encontrado.get().getPassRecaudador())) {
			return rechazo(CREDENCIALES_INCORRECTAS);
		}
		Recaudador recaudador = encontrado.get();
		if (!Boolean.TRUE.equals(recaudador.getActivoRecaudador())) {
			return rechazo("El recaudador no está activo");
		}
		if (passwordEncoder.upgradeEncoding(recaudador.getPassRecaudador())) {
			recaudador.setPassRecaudador(passwordEncoder.encode(password));
			recaudadorDao.save(recaudador);
		}
		return ResponseEntity.ok(tokenService.emitir(recaudador.getCodigoRecaudador(),
				recaudador.getNombreRecaudador(), Roles.RECAUDADOR, TokenService.TIPO_MOVIL));
	}

	@GetMapping("/me")
	public Map<String, Object> me(@AuthenticationPrincipal Jwt jwt) {
		return Map.of("usuario", jwt.getSubject(), "nombre", jwt.getClaimAsString(TokenService.CLAIM_NOMBRE), "rol",
				jwt.getClaimAsString(TokenService.CLAIM_ROL));
	}

	/** Cambio de la contraseña propia (usuarios web): {actual, nueva} */
	@PutMapping("/password")
	public ResponseEntity<?> cambiarPassword(@AuthenticationPrincipal Jwt jwt, @RequestBody Map<String, String> datos) {
		if (!TokenService.TIPO_WEB.equals(jwt.getClaimAsString(TokenService.CLAIM_TIPO))) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("mensaje", "Sólo para usuarios web"));
		}
		String actual = datos.get("actual");
		String nueva = datos.get("nueva");
		Optional<Usuario> encontrado = usuarioDao.findById(jwt.getSubject());
		if (encontrado.isEmpty() || actual == null || !passwordEncoder.matches(actual, encontrado.get().getPassword())) {
			return ResponseEntity.badRequest().body(Map.of("mensaje", "La contraseña actual es incorrecta"));
		}
		if (nueva == null || nueva.length() < UsuariosController.LONGITUD_MINIMA_PASSWORD) {
			return ResponseEntity.badRequest().body(Map.of("mensaje",
					"La contraseña nueva debe tener al menos " + UsuariosController.LONGITUD_MINIMA_PASSWORD + " caracteres"));
		}
		Usuario usuario = encontrado.get();
		usuario.setPassword(passwordEncoder.encode(nueva));
		usuarioDao.save(usuario);
		return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada"));
	}

	private ResponseEntity<?> rechazo(String mensaje) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("mensaje", mensaje));
	}
}
