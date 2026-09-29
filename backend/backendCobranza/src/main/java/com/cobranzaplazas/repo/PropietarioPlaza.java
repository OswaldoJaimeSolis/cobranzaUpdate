package com.cobranzaplazas.repo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class PropietarioPlaza implements Serializable {
	@Id 
	String idPropietarioPlaza ;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_plaza")
	Plaza plaza;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "tipo_plaza")
	TipoPlaza tipoPlaza;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_contribuyente")
	Contribuyente contribuyente;
	Date vigenciaInicial;
	Date vigenciaFinal;
	String giroDescripcion;
	Double importe;

	public String getIdPropietarioPlaza() {
		return idPropietarioPlaza;
	}

	public void setIdPropietarioPlaza(String idPropietarioPlaza) {
		this.idPropietarioPlaza = idPropietarioPlaza;
	}

	public Plaza getPlaza() {
		return plaza;
	}

	public void setPlaza(Plaza plaza) {
		this.plaza = plaza;
	}

	public TipoPlaza getTipoPlaza() {
		return tipoPlaza;
	}

	public void setTipoPlaza(TipoPlaza tipoPlaza) {
		this.tipoPlaza = tipoPlaza;
	}

	public Contribuyente getContribuyente() {
		return contribuyente;
	}

	public void setContribuyente(Contribuyente contribuyente) {
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

	public String getGiroDescripcion() {
		return giroDescripcion;
	}

	public void setGiroDescripcion(String giroDescripcion) {
		this.giroDescripcion = giroDescripcion;
	}

	public Double getImporte() {
		return importe;
	}

	public void setImporte(Double importe) {
		this.importe = importe;
	}

}
