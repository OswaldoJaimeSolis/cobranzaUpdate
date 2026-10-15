package com.cobranzaplazas.repo;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.*;

@Entity
public class Condonacion implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Integer idCondonacion;
	// Folio de la contribución condonada en el equipo móvil; evita duplicar al reenviar
	@Column(unique = true)
	String idCondonacionLocal;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_recaudador")
	Recaudador recaudador;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_equipo_rec")
	EquipoRecaudador equipoRecaudador;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_plaza")
	Plaza plaza;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_tipo_plaza")
	TipoPlaza tipoPlaza;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "codigo_contribuyente")
	Contribuyente contribuyente;
	Date fechaCondonacion;
	// Fecha de la contribución condonada
	@Temporal(TemporalType.DATE)
	Date fechaObligacion;
	Double montoCondonado;
	@Column(nullable = false, length = 500)
	String motivo;
	// Dispositivo origen; hoy sólo MOBILE
	String origen;

	public Integer getIdCondonacion() {
		return idCondonacion;
	}

	public void setIdCondonacion(Integer idCondonacion) {
		this.idCondonacion = idCondonacion;
	}

	public String getIdCondonacionLocal() {
		return idCondonacionLocal;
	}

	public void setIdCondonacionLocal(String idCondonacionLocal) {
		this.idCondonacionLocal = idCondonacionLocal;
	}

	public Recaudador getRecaudador() {
		return recaudador;
	}

	public void setRecaudador(Recaudador recaudador) {
		this.recaudador = recaudador;
	}

	public EquipoRecaudador getEquipoRecaudador() {
		return equipoRecaudador;
	}

	public void setEquipoRecaudador(EquipoRecaudador equipoRecaudador) {
		this.equipoRecaudador = equipoRecaudador;
	}

	public Plaza getPlaza() {
		return plaza;
	}

	public void setPlaza(Plaza plaza) {
		this.plaza = plaza;
	}

	public TipoPlaza getTipoPlaza() {
		return tipoPlaza;
	}

	public void setTipoPlaza(TipoPlaza tipoPlaza) {
		this.tipoPlaza = tipoPlaza;
	}

	public Contribuyente getContribuyente() {
		return contribuyente;
	}

	public void setContribuyente(Contribuyente contribuyente) {
		this.contribuyente = contribuyente;
	}

	public Date getFechaCondonacion() {
		return fechaCondonacion;
	}

	public void setFechaCondonacion(Date fechaCondonacion) {
		this.fechaCondonacion = fechaCondonacion;
	}

	public Date getFechaObligacion() {
		return fechaObligacion;
	}

	public void setFechaObligacion(Date fechaObligacion) {
		this.fechaObligacion = fechaObligacion;
	}

	public Double getMontoCondonado() {
		return montoCondonado;
	}

	public void setMontoCondonado(Double montoCondonado) {
		this.montoCondonado = montoCondonado;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}
}
