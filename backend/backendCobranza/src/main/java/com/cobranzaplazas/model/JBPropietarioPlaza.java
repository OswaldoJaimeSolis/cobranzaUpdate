package com.cobranzaplazas.model;

import java.util.Date;

public class JBPropietarioPlaza {
	String idPropietarioPlaza;
	String plaza;
	String tipoPlaza;
	String contribuyente;
	Date vigenciaInicial;
	Date vigenciaFinal;
	Double importe;

	
	
	

	public JBPropietarioPlaza(String idPropietarioPlaza,String plaza, String tipoPlaza, String contribuyente, Date vigenciaInicial,
			Date vigenciaFinal, Double importe) {
	    this.idPropietarioPlaza= idPropietarioPlaza;
		this.plaza = plaza;
		this.tipoPlaza = tipoPlaza;
		this.contribuyente = contribuyente;
		this.vigenciaInicial = vigenciaInicial;
		this.vigenciaFinal = vigenciaFinal;
		this.importe= importe;	
	}
	
	

	public String getIdPropietarioPlaza() {
		return idPropietarioPlaza;
	}

	public void setIdPropietarioPlaza(String idPropietarioPlaza) {
		this.idPropietarioPlaza = idPropietarioPlaza;
	}

	public String getPlaza() {
		return plaza;
	}
	public void setPlaza(String plaza) {
		this.plaza = plaza;
	}

	public String getTipoPlaza() {
		return tipoPlaza;
	}

	public void setTipoPlaza(String tipoPlaza) {
		this.tipoPlaza = tipoPlaza;
	}

	public String getContribuyente() {
		return contribuyente;
	}

	public void setContribuyente(String contribuyente) {
		this.contribuyente = contribuyente;
	}

	public Date getVigenciaInicial() {
		return vigenciaInicial;
	}

	public void setVigenciaInicial(Date vigenciaInicial) {
		this.vigenciaInicial = vigenciaInicial;
	}

	public Date getVigenciaFinal() {
		return vigenciaFinal;
	}

	public void setVigenciaFinal(Date vigenciaFinal) {
		this.vigenciaFinal = vigenciaFinal;
	}



	public Double getImporte() {
		return importe;
	}



	public void setImporte(Double importe) {
		this.importe = importe;
	}
	

}
