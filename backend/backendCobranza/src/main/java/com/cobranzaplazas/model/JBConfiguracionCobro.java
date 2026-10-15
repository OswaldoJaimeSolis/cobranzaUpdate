package com.cobranzaplazas.model;

import java.util.ArrayList;
import java.util.List;

/** Configuración de cobro completa de un tipo de plaza, tal como la captura el frontend. Fechas en yyyy-MM-dd. */
public class JBConfiguracionCobro {
	public static class DiaMes {
		// null = todos los años
		public Integer anio;
		public Integer mes;
		public Integer dia;
		public Boolean ultimoDia;
	}

	public static class PeriodoAnual {
		public Integer anio;
		public String fechaInicio;
		public String fechaFin;
	}

	public String codigoTipoPlaza;
	public String tipoCobro;
	public List<Integer> diasSemana = new ArrayList<>();
	public String fechaInicioCobro;
	public List<DiaMes> diasMes = new ArrayList<>();
	public Integer periodoInicioDia;
	public Integer periodoInicioMes;
	public Integer periodoFinDia;
	public Integer periodoFinMes;
	public List<PeriodoAnual> periodosAnuales = new ArrayList<>();
}
