package com.cobranzaplazas.repo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

/** Usuario del sistema web. Los recaudadores (app móvil) se autentican con su propia tabla. */
@Entity
public class Usuario implements Serializable {
	@Id
	String usuario;
	// hash con prefijo de algoritmo ({bcrypt}...); nunca se devuelve en JSON
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	String password;
	String nombre;
	String rol; // ADMIN o CONSULTA
	Boolean activo;

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getRol() {
		return rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}

	public Boolean getActivo() {
		return activo;
	}

	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
}
