package com.cobranzaplazas.repo;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class EquipoRecaudador implements Serializable {
	@Id
	String codigoEquipoRec;
	String descripcionEquipoRec;
	public String getCodigoEquipoRec() {
		return codigoEquipoRec;
	}
	public void setCodigoEquipoRec(String codigoEquipoRec) {
		this.codigoEquipoRec = codigoEquipoRec;
	}
	public String getDescripcionEquipoRec() {
		return descripcionEquipoRec;
	}
	public void setDescripcionEquipoRec(String descripcionEquipoRec) {
		this.descripcionEquipoRec = descripcionEquipoRec;
	}

}
