package com.cobranzaplazas.repo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Contribuyente {
	@Id
	String codigoContribuyente;
	String nombre;
	String apePaterno;
	String apeMaterno;
	String rfcContribuyente;

	public String getCodigoContribuyente() {
		return codigoContribuyente;
	}

	public void setCodigoContribuyente(String codigoContribuyente) {
		this.codigoContribuyente = codigoContribuyente;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApePaterno() {
		return apePaterno;
	}

	public void setApePaterno(String apePaterno) {
		this.apePaterno = apePaterno;
	}

	public String getApeMaterno() {
		return apeMaterno;
	}

	public void setApeMaterno(String apeMaterno) {
		this.apeMaterno = apeMaterno;
	}

	public String getRfcContribuyente() {
		return rfcContribuyente;
	}

	public void setRfcContribuyente(String rfcContribuyente) {
		this.rfcContribuyente = rfcContribuyente;
	}

}
