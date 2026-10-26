package com.cobranzaplazas.cobro;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * Cálculo puro (sin acceso a datos) de las fechas en que un esquema de cobro
 * genera obligaciones de pago. La fecha de una obligación es también su fecha
 * límite: pagada ese día o antes aplica el precio con descuento, después el
 * precio normal y la obligación se considera vencida.
 */
public class CalendarioCobro {
	public static final String DIARIO = "DIARIO";
	public static final String MENSUAL = "MENSUAL";
	public static final String ANUAL = "ANUAL";

	private static final DateTimeFormatter DMA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private static final Locale ES = Locale.forLanguageTag("es-MX");

	/**
	 * Día de cobro de un mes; dia es null cuando se trata del último día del mes. anio null
	 * aplica a todos los años; un año con días propios no usa los generales.
	 */
	public record DiaMes(Integer anio, int mes, Integer dia, boolean ultimoDia) {
		public DiaMes(int mes, Integer dia, boolean ultimoDia) {
			this(null, mes, dia, ultimoDia);
		}
	}

	public record Periodo(LocalDate inicio, LocalDate fin) {
	}

	public record Obligacion(LocalDate fecha, LocalDate inicioPeriodo, String periodo) {
	}

	private final String tipoCobro;
	private final Set<DayOfWeek> diasSemana;
	private final List<DiaMes> diasMes;
	private final Map<Integer, Periodo> periodosAnuales;
	private final Integer inicioDia, inicioMes, finDia, finMes;

	public CalendarioCobro(String tipoCobro, Collection<Integer> diasSemana, List<DiaMes> diasMes,
			Map<Integer, Periodo> periodosAnuales, Integer inicioDia, Integer inicioMes, Integer finDia,
			Integer finMes) {
		this.tipoCobro = tipoCobro;
		this.diasSemana = EnumSet.noneOf(DayOfWeek.class);
		if (diasSemana != null) {
			for (Integer d : diasSemana) {
				this.diasSemana.add(DayOfWeek.of(d));
			}
		}
		this.diasMes = diasMes != null ? diasMes : List.of();
		this.periodosAnuales = periodosAnuales != null ? periodosAnuales : Map.of();
		this.inicioDia = inicioDia;
		this.inicioMes = inicioMes;
		this.finDia = finDia;
		this.finMes = finMes;
	}

	/** Convierte "1,2,5" en la lista de días ISO; tolera nulos y espacios. */
	public static List<Integer> parseDiasSemana(String csv) {
		List<Integer> dias = new ArrayList<>();
		if (csv != null) {
			for (String parte : csv.split(",")) {
				if (!parte.isBlank()) {
					dias.add(Integer.parseInt(parte.trim()));
				}
			}
		}
		return dias;
	}

	/** Un día configurado mayor al largo del mes (p. ej. 29 de febrero) se ajusta al último día. */
	public static LocalDate fechaDe(DiaMes diaMes, int anio) {
		YearMonth ym = YearMonth.of(anio, diaMes.mes());
		if (diaMes.ultimoDia() || diaMes.dia() == null) {
			return ym.atEndOfMonth();
		}
		return ym.atDay(Math.min(diaMes.dia(), ym.lengthOfMonth()));
	}

	/** Días de cobro del año: los propios de ese año o, si no tiene, los generales. */
	public List<DiaMes> diasMesDelAnio(int anio) {
		List<DiaMes> propios = diasMes.stream().filter(d -> d.anio() != null && d.anio() == anio).toList();
		return !propios.isEmpty() ? propios : diasMes.stream().filter(d -> d.anio() == null).toList();
	}

	/** Período de pago del año dado: el específico de ese año o, si no hay, el período por omisión. */
	public Periodo periodoAnual(int anio) {
		Periodo especifico = periodosAnuales.get(anio);
		if (especifico != null) {
			return especifico;
		}
		if (inicioDia == null || inicioMes == null || finDia == null || finMes == null) {
			return null;
		}
		LocalDate inicio = fechaDe(new DiaMes(inicioMes, inicioDia, false), anio);
		LocalDate fin = fechaDe(new DiaMes(finMes, finDia, false), anio);
		return new Periodo(inicio, fin);
	}

	/** Obligaciones cuya fecha cae en [desde, hasta], en orden cronológico. */
	public List<Obligacion> obligaciones(LocalDate desde, LocalDate hasta) {
		List<Obligacion> resultado = new ArrayList<>();
		if (desde == null || hasta == null || hasta.isBefore(desde) || tipoCobro == null) {
			return resultado;
		}
		switch (tipoCobro) {
		case DIARIO:
			for (LocalDate d = desde; !d.isAfter(hasta); d = d.plusDays(1)) {
				if (diasSemana.contains(d.getDayOfWeek())) {
					resultado.add(new Obligacion(d, d, DMA.format(d)));
				}
			}
			break;
		case MENSUAL:
			// Una obligación por mes: las fechas del mes son opciones para pagar y con un pago
			// cualquiera de ellas queda cubierto. Vence en la última fecha de cobro del mes.
			TreeMap<YearMonth, TreeSet<LocalDate>> porMes = new TreeMap<>();
			for (int anio = desde.getYear(); anio <= hasta.getYear(); anio++) {
				for (DiaMes diaMes : diasMesDelAnio(anio)) {
					LocalDate d = fechaDe(diaMes, anio);
					porMes.computeIfAbsent(YearMonth.from(d), k -> new TreeSet<>()).add(d);
				}
			}
			for (Map.Entry<YearMonth, TreeSet<LocalDate>> mes : porMes.entrySet()) {
				TreeSet<LocalDate> fechas = mes.getValue();
				LocalDate vence = fechas.last();
				if (vence.isBefore(desde) || vence.isAfter(hasta)) {
					continue;
				}
				String nombreMes = mes.getKey().getMonth().getDisplayName(java.time.format.TextStyle.FULL, ES);
				String dias = String.join(", ", fechas.stream().map(f -> String.valueOf(f.getDayOfMonth())).toList());
				resultado.add(new Obligacion(vence, fechas.first(), nombreMes + " " + mes.getKey().getYear()
						+ (fechas.size() > 1 ? " (días " : " (día ") + dias + ")"));
			}
			break;
		case ANUAL:
			for (int anio = desde.getYear(); anio <= hasta.getYear(); anio++) {
				Periodo p = periodoAnual(anio);
				if (p != null && !p.fin().isBefore(desde) && !p.fin().isAfter(hasta)) {
					resultado.add(new Obligacion(p.fin(), p.inicio(),
							anio + ": del " + DMA.format(p.inicio()) + " al " + DMA.format(p.fin())));
				}
			}
			break;
		default:
			break;
		}
		return resultado;
	}

	/** Valida que el día exista en el mes (febrero admite 29 por los años bisiestos). */
	public static boolean diaValido(int mes, int dia) {
		if (mes < 1 || mes > 12 || dia < 1) {
			return false;
		}
		return dia <= YearMonth.of(2024, mes).lengthOfMonth();
	}
}
