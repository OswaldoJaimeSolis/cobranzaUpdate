package com.cobranzaplazas.model;

public class JBContribuyente {
	String codigoContribuyente;
	String nombreContribuyente;
	String rfc;
	String tipoPlaza;
	String licenciaComercio;

	
	
	public JBContribuyente(String codigoContribuyente) {
		this.codigoContribuyente = codigoContribuyente;
	}

	public JBContribuyente(String codigoContribuyente, String nombreContribuyente, String rfc, String tipoPlaza) {
		this.codigoContribuyente = codigoContribuyente;
		this.nombreContribuyente = nombreContribuyente;
		this.rfc = rfc;
		this.tipoPlaza = tipoPlaza;
	}

	public JBContribuyente(String codigoContribuyente, String nombreContribuyente, String rfc, String tipoPlaza,
			Integer licenciaComercio) {
		this(codigoContribuyente, nombreContribuyente, rfc, tipoPlaza);
		this.licenciaComercio = Integer.valueOf(1).equals(licenciaComercio) ? "Sí" : "No";
	}

	public String getLicenciaComercio() {
		return licenciaComercio;
	}

	public String getCodigoContribuyente() {
		return codigoContribuyente;
	}

	public void setCodigoContribuyente(String codigoContribuyente) {
		this.codigoContribuyente = codigoContribuyente;
	}

	public String getNombreContribuyente() {
		return nombreContribuyente;
	}

	public void setNombreContribuyente(String nombreContribuyente) {
		this.nombreContribuyente = nombreContribuyente;
	}

	public String getRfc() {
		return rfc;
	}

	public void setRfc(String rfc) {
		this.rfc = rfc;
	}

	public String getTipoPlaza() {
		return tipoPlaza;
	}

	public void setTipoPlaza(String tipoPlaza) {
		this.tipoPlaza = tipoPlaza;
	}

}
