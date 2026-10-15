package com.cobranzaplazas.repo;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;

@Entity
public class ConfiguracionCobro implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer idConfiguracionCobro;
	@OneToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_tipo_plaza", unique = true)
	TipoPlaza tipoPlaza;
	// DIARIO, MENSUAL o ANUAL
	String tipoCobro;
	// Sólo DIARIO: días ISO separados por coma (1 = lunes ... 7 = domingo)
	String diasSemana;
	// Las obligaciones se generan a partir de esta fecha; el historial anterior no cuenta
	@Temporal(TemporalType.DATE)
	Date fechaInicioCobro;
	// Sólo ANUAL: período por omisión para los años sin registro en CobroPeriodoAnual
	Integer periodoInicioDia;
	Integer periodoInicioMes;
	Integer periodoFinDia;
	Integer periodoFinMes;

	public Integer getIdConfiguracionCobro() {
		return idConfiguracionCobro;
	}

	public void setIdConfiguracionCobro(Integer idConfiguracionCobro) {
		this.idConfiguracionCobro = idConfiguracionCobro;
	}

	public TipoPlaza getTipoPlaza() {
		return tipoPlaza;
	}

	public void setTipoPlaza(TipoPlaza tipoPlaza) {
		this.tipoPlaza = tipoPlaza;
	}

	public String getTipoCobro() {
		return tipoCobro;
	}

	public void setTipoCobro(String tipoCobro) {
		this.tipoCobro = tipoCobro;
	}

	public String getDiasSemana() {
		return diasSemana;
	}

	public void setDiasSemana(String diasSemana) {
		this.diasSemana = diasSemana;
	}

	public Date getFechaInicioCobro() {
		return fechaInicioCobro;
	}

	public void setFechaInicioCobro(Date fechaInicioCobro) {
		this.fechaInicioCobro = fechaInicioCobro;
	}

	public Integer getPeriodoInicioDia() {
		return periodoInicioDia;
	}

	public void setPeriodoInicioDia(Integer periodoInicioDia) {
		this.periodoInicioDia = periodoInicioDia;
	}

	public Integer getPeriodoInicioMes() {
		return periodoInicioMes;
	}

	public void setPeriodoInicioMes(Integer periodoInicioMes) {
		this.periodoInicioMes = periodoInicioMes;
	}

	public Integer getPeriodoFinDia() {
		return periodoFinDia;
	}

	public void setPeriodoFinDia(Integer periodoFinDia) {
		this.periodoFinDia = periodoFinDia;
	}

	public Integer getPeriodoFinMes() {
		return periodoFinMes;
	}

	public void setPeriodoFinMes(Integer periodoFinMes) {
		this.periodoFinMes = periodoFinMes;
	}
}
