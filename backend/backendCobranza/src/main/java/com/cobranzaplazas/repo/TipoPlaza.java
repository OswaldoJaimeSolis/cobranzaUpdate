package com.cobranzaplazas.repo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class TipoPlaza {
	@Id
	String codigoTipoPlaza;
	String descripcionTipoPlaza;
	Boolean porImporteGlobalTipoPlaza;
	String leyendaTipoPlaza;
	Boolean addLocal;

	public String getCodigoTipoPlaza() {
		return codigoTipoPlaza;
	}

	public void setCodigoTipoPlaza(String codigoTipoPlaza) {
		this.codigoTipoPlaza = codigoTipoPlaza;
	}

	public String getDescripcionTipoPlaza() {
		return descripcionTipoPlaza;
	}

	public void setDescripcionTipoPlaza(String descripcionTipoPlaza) {
		this.descripcionTipoPlaza = descripcionTipoPlaza;
	}

	public Boolean getPorImporteGlobalTipoPlaza() {
		return porImporteGlobalTipoPlaza;
	}

	public void setPorImporteGlobalTipoPlaza(Boolean porImporteGlobalTipoPlaza) {
		this.porImporteGlobalTipoPlaza = porImporteGlobalTipoPlaza;
	}

	public String getLeyendaTipoPlaza() {
		return leyendaTipoPlaza;
	}

	public void setLeyendaTipoPlaza(String leyendaTipoPlaza) {
		this.leyendaTipoPlaza = leyendaTipoPlaza;
	}

	public Boolean getAddLocal() {
		return addLocal;
	}

	public void setAddLocal(Boolean addLocal) {
		this.addLocal = addLocal;
	}

}
