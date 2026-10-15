package com.cobranzaplazas.repo;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;

@Entity
public class CobroMensualDia implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer idCobroMensualDia;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_tipo_plaza")
	TipoPlaza tipoPlaza;
	// null: aplica a todos los años; con valor, reemplaza al calendario general en ese año
	Integer anio;
	Integer mes;
	// null cuando ultimoDia es true
	Integer dia;
	Boolean ultimoDia;

	public Integer getIdCobroMensualDia() {
		return idCobroMensualDia;
	}

	public void setIdCobroMensualDia(Integer idCobroMensualDia) {
		this.idCobroMensualDia = idCobroMensualDia;
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

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getDia() {
		return dia;
	}

	public void setDia(Integer dia) {
		this.dia = dia;
	}

	public Boolean getUltimoDia() {
		return ultimoDia;
	}

	public void setUltimoDia(Boolean ultimoDia) {
		this.ultimoDia = ultimoDia;
	}
}
