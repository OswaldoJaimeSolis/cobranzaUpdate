package com.cobranzaplazas.repo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Recaudador  implements Serializable {
	@Id
	String codigoRecaudador;
	String passRecaudador;
	String nombreRecaudador;
	Boolean activoRecaudador;
	
	public String getCodigoRecaudador() {
		return codigoRecaudador;
	}
	public void setCodigoRecaudador(String codigoRecaudador) {
		this.codigoRecaudador = codigoRecaudador;
	}
	public String getPassRecaudador() {
		return passRecaudador;
	}
	public void setPassRecaudador(String passRecaudador) {
		this.passRecaudador = passRecaudador;
	}
	public String getNombreRecaudador() {
		return nombreRecaudador;
	}
	public void setNombreRecaudador(String nombreRecaudador) {
		this.nombreRecaudador = nombreRecaudador;
	}
	public Boolean getActivoRecaudador() {
		return activoRecaudador;
	}
	public void setActivoRecaudador(Boolean activoRecaudador) {
		this.activoRecaudador = activoRecaudador;
	}
	
	

}
