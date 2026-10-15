package com.cobranzaplazas.model;

import java.util.Date;

/** Una obligación vencida sin pago (o el adeudo anterior) de un propietario de plaza. */
public class JBAdeudo {
	public static final String CONCEPTO_OBLIGACION = "OBLIGACION";
	public static final String CONCEPTO_ADEUDO_ANTERIOR = "ADEUDO_ANTERIOR";

	String idPropietarioPlaza;
	String codigoPlaza;
	String tipoPlaza;
	String codigoContribuyente;
	String nombreContribuyente;
	String giro;
	String licenciaComercio;
	String tipoCobro;
	String concepto;
	String periodo;
	Date fechaVencimiento;
	Double monto;

	public JBAdeudo(String idPropietarioPlaza, String codigoPlaza, String tipoPlaza, String codigoContribuyente, String nombreContribuyente,
			String giro, String licenciaComercio, String tipoCobro, String concepto, String periodo,
			Date fechaVencimiento, Double monto) {
		this.idPropietarioPlaza = idPropietarioPlaza;
		this.codigoPlaza = codigoPlaza;
		this.tipoPlaza = tipoPlaza;
		this.codigoContribuyente = codigoContribuyente;
		this.nombreContribuyente = nombreContribuyente;
		this.giro = giro;
		this.licenciaComercio = licenciaComercio;
		this.tipoCobro = tipoCobro;
		this.concepto = concepto;
		this.periodo = periodo;
		this.fechaVencimiento = fechaVencimiento;
		this.monto = monto;
	}

	public String getIdPropietarioPlaza() {
		return idPropietarioPlaza;
	}

	public String getCodigoPlaza() {
		return codigoPlaza;
	}

	public String getTipoPlaza() {
		return tipoPlaza;
	}

	public String getCodigoContribuyente() {
		return codigoContribuyente;
	}

	public String getNombreContribuyente() {
		return nombreContribuyente;
	}

	public String getGiro() {
		return giro;
	}

	public String getLicenciaComercio() {
		return licenciaComercio;
	}

	public String getTipoCobro() {
		return tipoCobro;
	}

	public String getConcepto() {
		return concepto;
	}

	public String getPeriodo() {
		return periodo;
	}

	public Date getFechaVencimiento() {
		return fechaVencimiento;
	}

	public Double getMonto() {
		return monto;
	}

	/** Clave de agrupación: una hoja de requerimiento por plaza. */
	public String getClavePlaza() {
		return tipoPlaza + "|" + codigoPlaza;
	}
}
