package com.cobranzaplazas.repo;

import javax.annotation.Generated;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class RecaudadorTipoPlaza {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	Integer idRecaudadorTipoPlaza;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_recaudador")
	Recaudador recaudador;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_tipo_plaza")
	TipoPlaza tipoPlaza;

	public Integer getIdRecaudadorTipoPlaza() {
		return idRecaudadorTipoPlaza;
	}

	public void setIdRecaudadorTipoPlaza(Integer idRecaudadorTipoPlaza) {
		this.idRecaudadorTipoPlaza = idRecaudadorTipoPlaza;
	}

	public Recaudador getRecaudador() {
		return recaudador;
	}

	public void setRecaudador(Recaudador recaudador) {
		this.recaudador = recaudador;
	}

	public TipoPlaza getTipoPlaza() {
		return tipoPlaza;
	}

	public void setTipoPlaza(TipoPlaza tipoPlaza) {
		this.tipoPlaza = tipoPlaza;
	}

}
