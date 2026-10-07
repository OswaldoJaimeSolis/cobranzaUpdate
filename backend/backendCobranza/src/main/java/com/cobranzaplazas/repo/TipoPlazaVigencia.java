package com.cobranzaplazas.repo;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class TipoPlazaVigencia  implements Serializable {
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	Integer idTipoPlazaVigencia;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_tipo_plaza")
	TipoPlaza tipoPlaza;
	Date vigenciaInicial;
	Date vigenciaFinal;
	Double importe;

	public Integer getIdTipoPlazaVigencia() {
		return idTipoPlazaVigencia;
	}

	public void setIdTipoPlazaVigencia(Integer idTipoPlazaVigencia) {
		this.idTipoPlazaVigencia = idTipoPlazaVigencia;
	}

	public TipoPlaza getTipoPlaza() {
		return tipoPlaza;
	}

	public void setTipoPlaza(TipoPlaza tipoPlaza) {
		this.tipoPlaza = tipoPlaza;
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
