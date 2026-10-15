package com.cobranzaplazas.repo;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;

@Entity
public class CobroPeriodoAnual implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer idCobroPeriodoAnual;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_tipo_plaza")
	TipoPlaza tipoPlaza;
	Integer anio;
	@Temporal(TemporalType.DATE)
	Date fechaInicio;
	@Temporal(TemporalType.DATE)
	Date fechaFin;

	public Integer getIdCobroPeriodoAnual() {
		return idCobroPeriodoAnual;
	}

	public void setIdCobroPeriodoAnual(Integer idCobroPeriodoAnual) {
		this.idCobroPeriodoAnual = idCobroPeriodoAnual;
	}

	public TipoPlaza getTipoPlaza() {
		return tipoPlaza;
	}

	public void setTipoPlaza(TipoPlaza tipoPlaza) {
		this.tipoPlaza = tipoPlaza;
	}

	public Integer getAnio() {
		return anio;
	}

	public void setAnio(Integer anio) {
		this.anio = anio;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
}
