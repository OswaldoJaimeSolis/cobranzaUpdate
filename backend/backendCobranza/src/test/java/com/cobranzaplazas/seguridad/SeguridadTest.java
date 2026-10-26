package com.cobranzaplazas.seguridad;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.cobranzaplazas.repo.Recaudador;
import com.cobranzaplazas.repo.RecaudadorDao;
import com.cobranzaplazas.repo.Usuario;
import com.cobranzaplazas.repo.UsuarioDao;

/** Usa la base local: crea y borra sus propios usuario y recaudador de prueba. */
@SpringBootTest
@AutoConfigureMockMvc
public class SeguridadTest {
	private static final String USUARIO = "test_seguridad";
	private static final String RECAUDADOR = "TEST_SEGURIDAD";

	@Autowired
	MockMvc mvc;
	@Autowired
	TokenService tokenService;
	@Autowired
	UsuarioDao usuarioDao;
	@Autowired
	RecaudadorDao recaudadorDao;
	@Autowired
	PasswordEncoder passwordEncoder;

	@AfterEach
	void limpiar() {
		usuarioDao.deleteById(USUARIO);
		recaudadorDao.deleteById(RECAUDADOR);
	}

	private String token(String rol, String tipo) {
		return "Bearer " + tokenService.emitir(USUARIO, "Prueba", rol, tipo).get("token");
	}

	private String credenciales(String usuario, String password) {
		return "{\"usuario\":\"" + usuario + "\",\"password\":\"" + password + "\"}";
	}

	@Test
	void sinTokenResponde401() throws Exception {
		mvc.perform(get("/tiposPlaza")).andExpect(status().isUnauthorized());
		mvc.perform(get("/getRecaudadores")).andExpect(status().isUnauthorized());
	}

	@Test
	void tokenInvalidoResponde401() throws Exception {
		mvc.perform(get("/tiposPlaza").header("Authorization", "Bearer abc.def.ghi"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void loginWebCorrectoEIncorrecto() throws Exception {
		Usuario u = new Usuario();
		u.setUsuario(USUARIO);
		u.setNombre("Prueba");
		u.setRol(Roles.CONSULTA);
		u.setActivo(true);
		u.setPassword(passwordEncoder.encode("secreto1"));
		usuarioDao.save(u);

		mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(credenciales(USUARIO, "otra")))
				.andExpect(status().isUnauthorized());
		mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(credenciales(USUARIO, "secreto1")))
				.andExpect(status().isOk()).andExpect(jsonPath("$.rol").value(Roles.CONSULTA))
				.andExpect(jsonPath("$.token").isNotEmpty());

		u.setActivo(false);
		usuarioDao.save(u);
		mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(credenciales(USUARIO, "secreto1")))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void permisosPorRol() throws Exception {
		String admin = token(Roles.ADMIN, TokenService.TIPO_WEB);
		String consulta = token(Roles.CONSULTA, TokenService.TIPO_WEB);
		String recaudador = token(Roles.RECAUDADOR, TokenService.TIPO_MOVIL);

		mvc.perform(get("/tiposPlaza").header("Authorization", admin)).andExpect(status().isOk());
		mvc.perform(get("/usuarios").header("Authorization", admin)).andExpect(status().isOk());

		mvc.perform(get("/tiposPlaza").header("Authorization", consulta)).andExpect(status().isOk());
		mvc.perform(get("/usuarios").header("Authorization", consulta)).andExpect(status().isForbidden());
		mvc.perform(post("/tiposPlaza").header("Authorization", consulta).contentType(MediaType.APPLICATION_JSON)
				.content("{}")).andExpect(status().isForbidden());
		mvc.perform(get("/getConfiguracionCobro").header("Authorization", consulta)).andExpect(status().isForbidden());

		mvc.perform(get("/getConfiguracionCobro").header("Authorization", recaudador)).andExpect(status().isOk());
		mvc.perform(get("/tiposPlaza").header("Authorization", recaudador)).andExpect(status().isForbidden());

		mvc.perform(get("/auth/me").header("Authorization", consulta)).andExpect(status().isOk())
				.andExpect(jsonPath("$.usuario").value(USUARIO));
	}

	@Test
	void loginMovilConvierteContrasenaHeredadaYNoLaExpone() throws Exception {
		Recaudador r = new Recaudador();
		r.setCodigoRecaudador(RECAUDADOR);
		r.setNombreRecaudador("Prueba");
		r.setActivoRecaudador(true);
		r.setPassRecaudador("plano123");
		recaudadorDao.save(r);

		mvc.perform(post("/auth/movil/login").contentType(MediaType.APPLICATION_JSON)
				.content(credenciales(RECAUDADOR, "plano123"))).andExpect(status().isOk())
				.andExpect(jsonPath("$.rol").value(Roles.RECAUDADOR));

		String guardada = recaudadorDao.findById(RECAUDADOR).get().getPassRecaudador();
		assertTrue(guardada.startsWith("{bcrypt}"));
		assertTrue(passwordEncoder.matches("plano123", guardada));

		String lista = mvc.perform(get("/recaudadores").header("Authorization", token(Roles.ADMIN, TokenService.TIPO_WEB)))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
		assertFalse(lista.contains("passRecaudador"));
	}
}
