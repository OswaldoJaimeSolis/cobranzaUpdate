package com.cobranzaplazas.model;

import java.util.Date;

public class JBContribucionReporte {
	private static final int ESTADO_PAGADO = 1;
	private static final int ESTADO_PENDIENTE = 2;
	private static final int ESTADO_AUSENTE = 3;
	Integer idContribucion;
	String codigoContribucionLocal;
	String codigoContribuyente;
	String nombreContribuyente;
	String codigoRecaudador;
	String codigoPlaza;
	String codigoTipoPlaza;
	Date fechaContribucion;
	Date fechaModificacion;
	Double importeContribucion;
	Integer estadoPago;
	String estadoContribucionToCode;
	String codigoEquipo;

	public JBContribucionReporte(Integer idContribucion, String codigoContribucionLocal, String codigoContribuyente,
			String nombreContribuyente, String codigoRecaudador, String codigoPlaza, Date fechaContribucion,
			Date fechaModificacion, Double importeContribucion, Integer estadoPago, String codigoEquipo, String codigoTipoPlaza) {

		this.idContribucion = idContribucion;
		this.codigoContribucionLocal = codigoContribucionLocal;
		this.codigoContribuyente = codigoContribuyente;
		this.nombreContribuyente = nombreContribuyente;
		this.codigoRecaudador = codigoRecaudador;
		this.codigoPlaza = codigoPlaza;
		this.fechaContribucion = fechaContribucion;
		this.fechaModificacion = fechaModificacion;
		this.importeContribucion = importeContribucion;
		this.estadoPago = estadoPago;
		this.codigoEquipo = codigoEquipo;
		this.codigoTipoPlaza= codigoTipoPlaza;
	}

	public Integer getIdContribucion() {
		return idContribucion;
	}

	public void setIdContribucion(Integer idContribucion) {
		this.idContribucion = idContribucion;
	}

	public String getCodigoContribucionLocal() {
		return codigoContribucionLocal;
	}

	public void setCodigoContribucionLocal(String codigoContribucionLocal) {
		this.codigoContribucionLocal = codigoContribucionLocal;
	}

	public String getCodigoContribuyente() {
		return codigoContribuyente;
	}

	public void setCodigoContribuyente(String codigoContribuyente) {
		this.codigoContribuyente = codigoContribuyente;
	}

	public String getNombreContribuyente() {
		return nombreContribuyente;
	}

	public void setNombreContribuyente(String nombreContribuyente) {
		this.nombreContribuyente = nombreContribuyente;
	}

	public String getCodigoRecaudador() {
		return codigoRecaudador;
	}

	public void setCodigoRecaudador(String codigoRecaudador) {
		this.codigoRecaudador = codigoRecaudador;
	}

	public String getCodigoPlaza() {
		return codigoPlaza;
	}

	public void setCodigoPlaza(String codigoPlaza) {
		this.codigoPlaza = codigoPlaza;
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

	public void setImporteContribucion(Double importeContribucion) {
		this.importeContribucion = importeContribucion;
	}

	public Integer getEstadoPago() {
		return estadoPago;
	}

	public void setEstadoPago(Integer estadoPago) {
		this.estadoPago = estadoPago;
	}
	
	

	public String getCodigoTipoPlaza() {
		return codigoTipoPlaza;
	}

	public void setCodigoTipoPlaza(String codigoTipoPlaza) {
		this.codigoTipoPlaza = codigoTipoPlaza;
	}

	public String getEstadoContribucionToCode() {
		String code = "";
		if (estadoPago != null) {
			if (estadoPago == ESTADO_PAGADO) {
				code = "PAGADO";
			} else if (estadoPago == ESTADO_AUSENTE) {
				code = "AUSENTE";
			} else if (estadoPago == ESTADO_PENDIENTE) {
				code = "PENDIENTE";
			}
			else {
				code="SR";
			}
		}
		return code;
	}

	public void setEstadoContribucionToCode(String estadoContribucionToCode) {
		this.estadoContribucionToCode = estadoContribucionToCode;
	}

	public String getCodigoEquipo() {
		return codigoEquipo;
	}

	public void setCodigoEquipo(String codigoEquipo) {
		this.codigoEquipo = codigoEquipo;
	}

}
