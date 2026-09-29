package com.cobranzaplazas.repo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Plaza implements Serializable {
	@Id
	String codigoPlaza;
	Double latitudPlaza;
	Double longitudPlaza;
	public String getCodigoPlaza() {
		return codigoPlaza;
	}
	public void setCodigoPlaza(String codigoPlaza) {
		this.codigoPlaza = codigoPlaza;
	}
	public Double getLatitudPlaza() {
		return latitudPlaza;
	}
	public void setLatitudPlaza(Double latitudPlaza) {
		this.latitudPlaza = latitudPlaza;
	}
	public Double getLongitudPlaza() {
		return longitudPlaza;
	}
	public void setLongitudPlaza(Double longitudPlaza) {
		this.longitudPlaza = longitudPlaza;
	}
	
	

}
