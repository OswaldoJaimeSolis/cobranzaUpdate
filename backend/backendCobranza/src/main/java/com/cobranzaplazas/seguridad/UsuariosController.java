package com.cobranzaplazas.seguridad;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cobranzaplazas.repo.Usuario;
import com.cobranzaplazas.repo.UsuarioDao;

/** Catálogo de usuarios web (sólo ADMIN, ver SpringSecurityConfig). */
@RestController
@RequestMapping("/usuarios")
public class UsuariosController {
	public static final int LONGITUD_MINIMA_PASSWORD = 6;

	private final UsuarioDao usuarioDao;
	private final PasswordEncoder passwordEncoder;

	public UsuariosController(UsuarioDao usuarioDao, PasswordEncoder passwordEncoder) {
		this.usuarioDao = usuarioDao;
		this.passwordEncoder = passwordEncoder;
	}

	@GetMapping
	public List<Usuario> getUsuarios() {
		return usuarioDao.findAll().stream().sorted(Comparator.comparing(Usuario::getUsuario)).toList();
	}

	@PostMapping
	public ResponseEntity<?> nuevoUsuario(@RequestBody Usuario usuario) {
		if (usuario.getUsuario() == null || usuario.getUsuario().isBlank()) {
			return error("Capture el usuario");
		}
		if (usuarioDao.existsById(usuario.getUsuario())) {
			return error("El usuario ya existe");
		}
		String mensaje = validar(usuario, true);
		if (mensaje != null) {
			return error(mensaje);
		}
		usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
		return ResponseEntity.ok(usuarioDao.save(usuario));
	}

	/** Sin password conserva la actual. */
	@PutMapping("/{id}")
	public ResponseEntity<?> editarUsuario(@PathVariable String id, @RequestBody Usuario datos,
			@AuthenticationPrincipal Jwt jwt) {
		Usuario usuario = usuarioDao.findById(id).orElse(null);
		if (usuario == null) {
			return ResponseEntity.notFound().build();
		}
		String mensaje = validar(datos, false);
		if (mensaje != null) {
			return error(mensaje);
		}
		boolean propio = id.equals(jwt.getSubject());
		if (propio && (!Roles.ADMIN.equals(datos.getRol()) || !Boolean.TRUE.equals(datos.getActivo()))) {
			return error("No puede quitarse el rol de administrador ni desactivarse a sí mismo");
		}
		usuario.setNombre(datos.getNombre());
		usuario.setRol(datos.getRol());
		usuario.setActivo(datos.getActivo());
		if (datos.getPassword() != null && !datos.getPassword().isEmpty()) {
			usuario.setPassword(passwordEncoder.encode(datos.getPassword()));
		}
		return ResponseEntity.ok(usuarioDao.save(usuario));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<?> borrarUsuario(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
		if (id.equals(jwt.getSubject())) {
			return error("No puede eliminar su propio usuario");
		}
		usuarioDao.deleteById(id);
		return ResponseEntity.ok().build();
	}

	private String validar(Usuario usuario, boolean nuevo) {
		if (usuario.getNombre() == null || usuario.getNombre().isBlank()) {
			return "Capture el nombre";
		}
		if (!Roles.WEB.contains(usuario.getRol())) {
			return "Rol inválido";
		}
		String password = usuario.getPassword();
		if ((nuevo || (password != null && !password.isEmpty()))
				&& (password == null || password.length() < LONGITUD_MINIMA_PASSWORD)) {
			return "La contraseña debe tener al menos " + LONGITUD_MINIMA_PASSWORD + " caracteres";
		}
		return null;
	}

	private ResponseEntity<?> error(String mensaje) {
		return ResponseEntity.badRequest().body(Map.of("mensaje", mensaje));
	}
}
