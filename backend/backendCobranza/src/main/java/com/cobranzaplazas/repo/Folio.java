package com.cobranzaplazas.repo;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Folio {
	@Id
	String codigoFolio;
	Integer folio;
	public String getCodigoFolio() {
		return codigoFolio;
	}
	public void setCodigoFolio(String codigoFolio) {
		this.codigoFolio = codigoFolio;
	}
	public Integer getFolio() {
		return folio;
	}
	public void setFolio(Integer folio) {
		this.folio = folio;
	}
	
	
}
