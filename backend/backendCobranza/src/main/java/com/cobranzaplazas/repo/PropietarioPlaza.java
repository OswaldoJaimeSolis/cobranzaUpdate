package com.cobranzaplazas.repo;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

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
	Boolean licenciaComercio;
	// Adeudo previo a la entrada en vigor de la configuración de cobro
	Double adeudoAnterior;

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

	public Boolean getLicenciaComercio() {
		return licenciaComercio;
	}

	public void setLicenciaComercio(Boolean licenciaComercio) {
		this.licenciaComercio = licenciaComercio;
	}

	public Double getAdeudoAnterior() {
		return adeudoAnterior;
	}

	public void setAdeudoAnterior(Double adeudoAnterior) {
		this.adeudoAnterior = adeudoAnterior;
	}
}
