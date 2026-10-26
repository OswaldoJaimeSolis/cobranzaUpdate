package com.cobranzaplazas.seguridad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cobranzaplazas.repo.Recaudador;
import com.cobranzaplazas.repo.RecaudadorDao;
import com.cobranzaplazas.repo.Usuario;
import com.cobranzaplazas.repo.UsuarioDao;

/**
 * Al arrancar: convierte a BCrypt las contraseñas que sigan en texto plano (las heredadas
 * de recaudador) y, si no hay ningún usuario web, crea el administrador inicial.
 */
@Component
public class MigracionContrasenas implements ApplicationRunner {
	private static final Logger log = LoggerFactory.getLogger(MigracionContrasenas.class);

	private final RecaudadorDao recaudadorDao;
	private final UsuarioDao usuarioDao;
	private final PasswordEncoder passwordEncoder;
	private final String usuarioAdmin;
	private final String passwordAdmin;

	public MigracionContrasenas(RecaudadorDao recaudadorDao, UsuarioDao usuarioDao, PasswordEncoder passwordEncoder,
			@Value("${seguridad.admin-inicial.usuario:admin}") String usuarioAdmin,
			@Value("${seguridad.admin-inicial.password:admin123}") String passwordAdmin) {
		this.recaudadorDao = recaudadorDao;
		this.usuarioDao = usuarioDao;
		this.passwordEncoder = passwordEncoder;
		this.usuarioAdmin = usuarioAdmin;
		this.passwordAdmin = passwordAdmin;
	}

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		int convertidas = 0;
		for (Recaudador recaudador : recaudadorDao.findAll()) {
			String password = recaudador.getPassRecaudador();
			if (password != null && passwordEncoder.upgradeEncoding(password)) {
				recaudador.setPassRecaudador(passwordEncoder.encode(password));
				recaudadorDao.save(recaudador);
				convertidas++;
			}
		}
		for (Usuario usuario : usuarioDao.findAll()) {
			if (usuario.getPassword() != null && passwordEncoder.upgradeEncoding(usuario.getPassword())) {
				usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
				usuarioDao.save(usuario);
				convertidas++;
			}
		}
		if (convertidas > 0) {
			log.info("Contraseñas convertidas a BCrypt: {}", convertidas);
		}

		if (usuarioDao.count() == 0) {
			Usuario admin = new Usuario();
			admin.setUsuario(usuarioAdmin);
			admin.setNombre("Administrador");
			admin.setRol(Roles.ADMIN);
			admin.setActivo(true);
			admin.setPassword(passwordEncoder.encode(passwordAdmin));
			usuarioDao.save(admin);
			log.warn("Se creó el usuario administrador inicial '{}'; cambie su contraseña", usuarioAdmin);
		}
	}
}
