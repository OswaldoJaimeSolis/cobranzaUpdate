package com.cobranzaplazas.cobro;

import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cobranzaplazas.cobro.CalendarioCobro.DiaMes;
import com.cobranzaplazas.cobro.CalendarioCobro.Obligacion;
import com.cobranzaplazas.cobro.CalendarioCobro.Periodo;
import com.cobranzaplazas.model.JBAdeudo;
import com.cobranzaplazas.repo.CobroMensualDia;
import com.cobranzaplazas.repo.CobroMensualDiaDao;
import com.cobranzaplazas.repo.CobroPeriodoAnual;
import com.cobranzaplazas.repo.CobroPeriodoAnualDao;
import com.cobranzaplazas.repo.ConfiguracionCobro;
import com.cobranzaplazas.repo.ConfiguracionCobroDao;
import com.cobranzaplazas.repo.ContribucionDao;
import com.cobranzaplazas.repo.Contribuyente;
import com.cobranzaplazas.repo.PropietarioPlaza;
import com.cobranzaplazas.repo.PropietarioPlazaDao;
import com.cobranzaplazas.repo.TipoPlazaVigencia;
import com.cobranzaplazas.repo.TipoPlazaVigenciaDao;

/**
 * Calcula los adeudos vigentes: obligaciones generadas por la configuración de
 * cobro de cada tipo de plaza con importe global, dentro de la vigencia de cada
 * propietario, ya vencidas a la fecha de corte y sin pago, valuadas con el precio
 * normal de la vigencia del tipo de plaza en su fecha; más el adeudo anterior
 * capturado en el propietario de la plaza. Una contribución
 * condonada queda registrada como pagada, así que también cubre su obligación.
 */
@Service
public class CalculoCobroService {
	@Autowired
	ConfiguracionCobroDao configuracionCobroDao;
	@Autowired
	CobroMensualDiaDao cobroMensualDiaDao;
	@Autowired
	CobroPeriodoAnualDao cobroPeriodoAnualDao;
	@Autowired
	PropietarioPlazaDao propietarioPlazaDao;
	@Autowired
	ContribucionDao contribucionDao;
	@Autowired
	TipoPlazaVigenciaDao tipoPlazaVigenciaDao;

	public static LocalDate aLocalDate(Date fecha) {
		return fecha == null ? null : Instant.ofEpochMilli(fecha.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
	}

	public static Date aDate(LocalDate fecha) {
		return fecha == null ? null : Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	public CalendarioCobro calendario(ConfiguracionCobro config) {
		String codigo = config.getTipoPlaza().getCodigoTipoPlaza();
		List<DiaMes> diasMes = new ArrayList<>();
		for (CobroMensualDia d : cobroMensualDiaDao.porTipoPlaza(codigo)) {
			diasMes.add(new DiaMes(d.getAnio(), d.getMes(), d.getDia(), Boolean.TRUE.equals(d.getUltimoDia())));
		}
		Map<Integer, Periodo> periodos = new HashMap<>();
		for (CobroPeriodoAnual p : cobroPeriodoAnualDao.porTipoPlaza(codigo)) {
			periodos.put(p.getAnio(), new Periodo(aLocalDate(p.getFechaInicio()), aLocalDate(p.getFechaFin())));
		}
		return new CalendarioCobro(config.getTipoCobro(), CalendarioCobro.parseDiasSemana(config.getDiasSemana()),
				diasMes, periodos, config.getPeriodoInicioDia(), config.getPeriodoInicioMes(),
				config.getPeriodoFinDia(), config.getPeriodoFinMes());
	}

	private static String clave(String codigoTipoPlaza, String codigoPlaza) {
		return codigoTipoPlaza + "|" + codigoPlaza;
	}

	/** La configuración de cobro sólo aplica a tipos de plaza con importe global: el precio sale de sus vigencias. */
	public static boolean aplica(ConfiguracionCobro config) {
		return config != null && config.getTipoPlaza() != null && config.getFechaInicioCobro() != null
				&& Boolean.TRUE.equals(config.getTipoPlaza().getPorImporteGlobalTipoPlaza());
	}

	/** Vigencia del tipo de plaza que contiene la fecha, o null si no hay precio vigente ese día. */
	public static TipoPlazaVigencia vigenciaEn(List<TipoPlazaVigencia> vigencias, LocalDate fecha) {
		Date d = aDate(fecha);
		for (TipoPlazaVigencia v : vigencias) {
			if (v.getVigenciaInicial() != null && v.getVigenciaFinal() != null && v.getImporte() != null
					&& !d.before(v.getVigenciaInicial()) && !d.after(v.getVigenciaFinal())) {
				return v;
			}
		}
		return null;
	}

	private static boolean dentroDe(PropietarioPlaza pp, LocalDate fecha) {
		return !fecha.isBefore(aLocalDate(pp.getVigenciaInicial())) && !fecha.isAfter(aLocalDate(pp.getVigenciaFinal()));
	}

	/**
	 * @param codigoTP    filtra por tipo de plaza; null para todos
	 * @param codigoPlaza filtra por plaza; null para todas
	 * @param corte       fecha de corte; una obligación está vencida si su fecha es anterior. Si es
	 *                    pasada, sólo cuentan los pagos capturados hasta ese día
	 */
	public List<JBAdeudo> calcularAdeudos(String codigoTP, String codigoPlaza, LocalDate corte) {
		Map<String, ConfiguracionCobro> configs = new HashMap<>();
		Map<String, CalendarioCobro> calendarios = new HashMap<>();
		for (ConfiguracionCobro config : configuracionCobroDao.findAll()) {
			if (aplica(config)) {
				String codigo = config.getTipoPlaza().getCodigoTipoPlaza();
				configs.put(codigo, config);
				calendarios.put(codigo, calendario(config));
			}
		}
		Map<String, List<TipoPlazaVigencia>> vigencias = new HashMap<>();
		for (TipoPlazaVigencia v : tipoPlazaVigenciaDao.findAll()) {
			if (v.getTipoPlaza() != null) {
				vigencias.computeIfAbsent(v.getTipoPlaza().getCodigoTipoPlaza(), k -> new ArrayList<>()).add(v);
			}
		}

		// Registros de cada propietario en cada plaza: editar el registro en web lo cierra y crea
		// otro con nueva vigencia, pero el propietario y su adeudo son los mismos. Un propietario
		// anterior conserva lo que dejó vencido durante su vigencia.
		Map<String, List<PropietarioPlaza>> porPropietario = new HashMap<>();
		for (PropietarioPlaza pp : propietarioPlazaDao.findAll()) {
			if (pp.getPlaza() == null || pp.getTipoPlaza() == null || pp.getContribuyente() == null
					|| pp.getVigenciaInicial() == null || pp.getVigenciaFinal() == null) {
				continue;
			}
			String tp = pp.getTipoPlaza().getCodigoTipoPlaza();
			String plaza = pp.getPlaza().getCodigoPlaza();
			if ((codigoTP != null && !codigoTP.equals(tp)) || (codigoPlaza != null && !codigoPlaza.equals(plaza))) {
				continue;
			}
			porPropietario.computeIfAbsent(clave(tp, plaza) + "|" + pp.getContribuyente().getCodigoContribuyente(),
					k -> new ArrayList<>()).add(pp);
		}

		Map<String, Set<LocalDate>> cubiertas = new HashMap<>();
		// Con un corte en el pasado sólo cuentan los pagos capturados hasta ese día: el reporte
		// muestra el adeudo como estaba entonces. Con corte hoy o futuro cuentan todos.
		Date limite = corte.isBefore(LocalDate.now()) ? aDate(corte.plusDays(1)) : null;
		for (Object[] fila : contribucionDao.findFechasPagadas(limite)) {
			if (fila[2] != null) {
				cubiertas.computeIfAbsent(clave((String) fila[1], (String) fila[0]), k -> new HashSet<>())
						.add(aLocalDate((Date) fila[2]));
			}
		}
		Map<String, Double> anteriorPagado = new HashMap<>();
		for (Object[] fila : contribucionDao.findPagosAdeudoAnterior(limite)) {
			anteriorPagado.put(clave((String) fila[1], (String) fila[0]),
					fila[2] != null ? ((Number) fila[2]).doubleValue() : 0.0);
		}

		List<JBAdeudo> adeudos = new ArrayList<>();
		for (List<PropietarioPlaza> registros : porPropietario.values()) {
			registros.sort(Comparator.comparing(PropietarioPlaza::getVigenciaInicial));
			// El registro más reciente representa al propietario en el adeudo
			PropietarioPlaza pp = registros.get(registros.size() - 1);
			LocalDate inicio = aLocalDate(registros.get(0).getVigenciaInicial());
			if (inicio.isAfter(corte)) {
				continue;
			}
			String tp = pp.getTipoPlaza().getCodigoTipoPlaza();
			String clavePlaza = clave(tp, pp.getPlaza().getCodigoPlaza());
			ConfiguracionCobro config = configs.get(tp);
			String tipoCobro = config != null ? config.getTipoCobro() : null;

			// El adeudo anterior es del propietario vigente a la fecha de corte
			if (registros.stream().anyMatch(r -> dentroDe(r, corte))) {
				double anterior = (pp.getAdeudoAnterior() != null ? pp.getAdeudoAnterior() : 0.0)
						- anteriorPagado.getOrDefault(clavePlaza, 0.0);
				if (anterior > 0.005) {
					adeudos.add(fila(pp, tipoCobro, JBAdeudo.CONCEPTO_ADEUDO_ANTERIOR, "Adeudo anterior", null, anterior));
				}
			}

			if (config == null) {
				continue;
			}
			LocalDate desde = aLocalDate(config.getFechaInicioCobro());
			if (inicio.isAfter(desde)) {
				desde = inicio;
			}
			Set<LocalDate> pagadas = cubiertas.getOrDefault(clavePlaza, Set.of());
			List<TipoPlazaVigencia> vigenciasTP = vigencias.getOrDefault(tp, List.of());
			for (Obligacion ob : calendarios.get(tp).obligaciones(desde, corte.minusDays(1))) {
				// Sólo lo que cae dentro de la vigencia del propietario, sin contar huecos entre registros
				if (registros.stream().noneMatch(r -> dentroDe(r, ob.fecha())) || estaCubierta(ob, tipoCobro, pagadas)) {
					continue;
				}
				// Vencida se cobra a precio normal, el de la vigencia del tipo de plaza en la fecha de la
				// obligación; sin vigencia ese día no hay precio y no se genera
				TipoPlazaVigencia vigencia = vigenciaEn(vigenciasTP, ob.fecha());
				if (vigencia == null) {
					continue;
				}
				adeudos.add(fila(pp, tipoCobro, JBAdeudo.CONCEPTO_OBLIGACION, ob.periodo(), ob.fecha(),
						vigencia.getImporte()));
			}
		}
		adeudos.sort(Comparator.comparing(JBAdeudo::getTipoPlaza).thenComparing(JBAdeudo::getCodigoPlaza)
				.thenComparing(JBAdeudo::getFechaVencimiento, Comparator.nullsFirst(Comparator.naturalOrder())));
		return adeudos;
	}

	// El cobro anual se liquida con un solo pago registrado en cualquier fecha de ese año y el
	// mensual con un solo pago en cualquier fecha de ese mes; el diario, con el pago de ese día
	static boolean estaCubierta(Obligacion ob, String tipoCobro, Set<LocalDate> pagadas) {
		if (CalendarioCobro.ANUAL.equals(tipoCobro)) {
			return pagadas.stream().anyMatch(d -> d.getYear() == ob.fecha().getYear());
		}
		if (CalendarioCobro.MENSUAL.equals(tipoCobro)) {
			YearMonth mes = YearMonth.from(ob.fecha());
			return pagadas.stream().anyMatch(d -> YearMonth.from(d).equals(mes));
		}
		return pagadas.contains(ob.fecha());
	}

	private JBAdeudo fila(PropietarioPlaza pp, String tipoCobro, String concepto, String periodo, LocalDate fecha,
			Double monto) {
		Contribuyente con = pp.getContribuyente();
		String nombre = String.join(" ", nvl(con.getNombre()), nvl(con.getApePaterno()), nvl(con.getApeMaterno()))
				.trim();
		return new JBAdeudo(pp.getIdPropietarioPlaza(), pp.getPlaza().getCodigoPlaza(), pp.getTipoPlaza().getCodigoTipoPlaza(),
				con.getCodigoContribuyente(), nombre, pp.getGiroDescripcion(),
				Boolean.TRUE.equals(pp.getLicenciaComercio()) ? "Sí" : "No",
				tipoCobro != null ? tipoCobro : "SIN CONFIGURAR", concepto, periodo, aDate(fecha), monto);
	}

	private static String nvl(String s) {
		return s != null ? s : "";
	}
}
