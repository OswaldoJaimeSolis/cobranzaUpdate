package com.cobranzaplazas.repo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Contribucion implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer idContribucion;
	String idContribucionLocal;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_contribuyente")
	Contribuyente contribuyente;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_recaudador")
	Recaudador recaudador;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_plaza")
	Plaza plaza;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_tipo_plaza")
	TipoPlaza tipoPlaza;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_equipo_rec")
	EquipoRecaudador equipoRecaudador;
	Date fechaContribucion;
	Date fechaModificacion;
	Double importeContribucion;
	Integer estadoPago;
	Boolean estadoServidor;

	public Integer getIdContribucion() {
		return idContribucion;
	}

	public void setIdContribucion(Integer idContribucion) {
		this.idContribucion = idContribucion;
	}

	public String getIdContribucionLocal() {
		return idContribucionLocal;
	}

	public void setIdContribucionLocal(String idContribucionLocal) {
		this.idContribucionLocal = idContribucionLocal;
	}

	public Contribuyente getContribuyente() {
		return contribuyente;
	}

	public void setContribuyente(Contribuyente contribuyente) {
		this.contribuyente = contribuyente;
	}

	public Recaudador getRecaudador() {
		return recaudador;
	}

	public void setRecaudador(Recaudador recaudador) {
		this.recaudador = recaudador;
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

	public EquipoRecaudador getEquipoRecaudador() {
		return equipoRecaudador;
	}

	public void setEquipoRecaudador(EquipoRecaudador equipoRecaudador) {
		this.equipoRecaudador = equipoRecaudador;
	}

	public Date getFechaContribucion() {
		return fechaContribucion;
	}

	public void setFechaContribucion(Date fechaContribucion) {
		this.fechaContribucion = fechaContribucion;
	}

	public Date getFechaModificacion() {
		return fechaModificacion;
	}

	public void setFechaModificacion(Date fechaModificacion) {
		this.fechaModificacion = fechaModificacion;
	}

	public Double getImporteContribucion() {
		return importeContribucion;
	}

	public void setImporte(Double importeContribucion) {
		this.importeContribucion = importeContribucion;
	}

	public Integer getEstadoPago() {
		return estadoPago;
	}

	public void setEstadoPago(Integer estadoPago) {
		this.estadoPago = estadoPago;
	}

	public Boolean getEstadoServidor() {
		return estadoServidor;
	}

	public void setEstadoServidor(Boolean estadoServidor) {
		this.estadoServidor = estadoServidor;
	}

	public void setImporteContribucion(Double importeContribucion) {
		this.importeContribucion = importeContribucion;
	}

}
