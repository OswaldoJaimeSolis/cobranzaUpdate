package com.cobranzaplazas.controller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.cobranzaplazas.cobro.CalculoCobroService;
import com.cobranzaplazas.cobro.CalendarioCobro;
import com.cobranzaplazas.model.JBAdeudo;
import com.cobranzaplazas.model.JBConfiguracionCobro;
import com.cobranzaplazas.repo.CobroMensualDia;
import com.cobranzaplazas.repo.CobroMensualDiaDao;
import com.cobranzaplazas.repo.CobroPeriodoAnual;
import com.cobranzaplazas.repo.CobroPeriodoAnualDao;
import com.cobranzaplazas.repo.Condonacion;
import com.cobranzaplazas.repo.CondonacionDao;
import com.cobranzaplazas.repo.ConfiguracionCobro;
import com.cobranzaplazas.repo.ConfiguracionCobroDao;
import com.cobranzaplazas.repo.PropietarioPlaza;
import com.cobranzaplazas.repo.PropietarioPlazaDao;
import com.cobranzaplazas.repo.TipoPlaza;
import com.cobranzaplazas.repo.TipoPlazaDao;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

/**
 * Configuración de cobro por tipo de plaza, morosidad, requerimientos de pago
 * y bitácora de condonaciones. Las condonaciones no se registran aquí: llegan
 * con la contribución desde la app móvil (insert_contribuciones).
 */
@RestController
@RequestMapping("/")
public class CobrosController {
	private static final int REPORTE_PDF = 1;

	@Autowired
	ConfiguracionCobroDao configuracionCobroDao;
	@Autowired
	CobroMensualDiaDao cobroMensualDiaDao;
	@Autowired
	CobroPeriodoAnualDao cobroPeriodoAnualDao;
	@Autowired
	CondonacionDao condonacionDao;
	@Autowired
	TipoPlazaDao tipoPlazaDao;
	@Autowired
	PropietarioPlazaDao propietarioPlazaDao;
	@Autowired
	CalculoCobroService calculoCobroService;

	// --- Configuración de cobro ---

	@GetMapping("/configuracionCobro/{codigoTipoPlaza}")
	public JBConfiguracionCobro getConfiguracionCobro(@PathVariable String codigoTipoPlaza) {
		JBConfiguracionCobro jb = new JBConfiguracionCobro();
		jb.codigoTipoPlaza = codigoTipoPlaza;
		ConfiguracionCobro config = configuracionCobroDao.porTipoPlaza(codigoTipoPlaza);
		if (config == null) {
			return jb;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		jb.tipoCobro = config.getTipoCobro();
		jb.diasSemana = CalendarioCobro.parseDiasSemana(config.getDiasSemana());
		jb.fechaInicioCobro = config.getFechaInicioCobro() != null ? sdf.format(config.getFechaInicioCobro()) : null;
		jb.periodoInicioDia = config.getPeriodoInicioDia();
		jb.periodoInicioMes = config.getPeriodoInicioMes();
		jb.periodoFinDia = config.getPeriodoFinDia();
		jb.periodoFinMes = config.getPeriodoFinMes();
		for (CobroMensualDia d : cobroMensualDiaDao.porTipoPlaza(codigoTipoPlaza)) {
			JBConfiguracionCobro.DiaMes dm = new JBConfiguracionCobro.DiaMes();
			dm.anio = d.getAnio();
			dm.mes = d.getMes();
			dm.dia = d.getDia();
			dm.ultimoDia = Boolean.TRUE.equals(d.getUltimoDia());
			jb.diasMes.add(dm);
		}
		for (CobroPeriodoAnual p : cobroPeriodoAnualDao.porTipoPlaza(codigoTipoPlaza)) {
			JBConfiguracionCobro.PeriodoAnual pa = new JBConfiguracionCobro.PeriodoAnual();
			pa.anio = p.getAnio();
			pa.fechaInicio = sdf.format(p.getFechaInicio());
			pa.fechaFin = sdf.format(p.getFechaFin());
			jb.periodosAnuales.add(pa);
		}
		return jb;
	}

	@PutMapping("/configuracionCobro/{codigoTipoPlaza}")
	@Transactional
	public ResponseEntity<?> guardarConfiguracionCobro(@PathVariable String codigoTipoPlaza,
			@RequestBody JBConfiguracionCobro jb) {
		TipoPlaza tipoPlaza = tipoPlazaDao.findById(codigoTipoPlaza).orElse(null);
		if (tipoPlaza == null) {
			return error("No existe el tipo de plaza " + codigoTipoPlaza);
		}
		if (!Boolean.TRUE.equals(tipoPlaza.getPorImporteGlobalTipoPlaza())) {
			return error("La configuración de cobro sólo aplica a tipos de plaza con importe global");
		}
		String invalido = validar(jb);
		if (invalido != null) {
			return error(invalido);
		}

		ConfiguracionCobro config = configuracionCobroDao.porTipoPlaza(codigoTipoPlaza);
		if (config == null) {
			config = new ConfiguracionCobro();
			config.setTipoPlaza(tipoPlaza);
		}
		boolean diario = CalendarioCobro.DIARIO.equals(jb.tipoCobro);
		boolean mensual = CalendarioCobro.MENSUAL.equals(jb.tipoCobro);
		boolean anual = CalendarioCobro.ANUAL.equals(jb.tipoCobro);
		config.setTipoCobro(jb.tipoCobro);
		config.setDiasSemana(diario
				? jb.diasSemana.stream().distinct().sorted().map(String::valueOf).collect(Collectors.joining(","))
				: null);
		config.setFechaInicioCobro(CalculoCobroService.aDate(LocalDate.parse(jb.fechaInicioCobro)));
		config.setPeriodoInicioDia(anual ? jb.periodoInicioDia : null);
		config.setPeriodoInicioMes(anual ? jb.periodoInicioMes : null);
		config.setPeriodoFinDia(anual ? jb.periodoFinDia : null);
		config.setPeriodoFinMes(anual ? jb.periodoFinMes : null);
		configuracionCobroDao.save(config);

		// Sólo queda activo el detalle del tipo de cobro elegido
		cobroMensualDiaDao.deleteAll(cobroMensualDiaDao.porTipoPlaza(codigoTipoPlaza));
		cobroPeriodoAnualDao.deleteAll(cobroPeriodoAnualDao.porTipoPlaza(codigoTipoPlaza));
		if (mensual) {
			for (JBConfiguracionCobro.DiaMes dm : jb.diasMes) {
				CobroMensualDia d = new CobroMensualDia();
				d.setTipoPlaza(tipoPlaza);
				d.setAnio(dm.anio);
				d.setMes(dm.mes);
				d.setUltimoDia(Boolean.TRUE.equals(dm.ultimoDia));
				d.setDia(Boolean.TRUE.equals(dm.ultimoDia) ? null : dm.dia);
				cobroMensualDiaDao.save(d);
			}
		}
		if (anual) {
			for (JBConfiguracionCobro.PeriodoAnual pa : jb.periodosAnuales) {
				CobroPeriodoAnual p = new CobroPeriodoAnual();
				p.setTipoPlaza(tipoPlaza);
				p.setAnio(pa.anio);
				p.setFechaInicio(CalculoCobroService.aDate(LocalDate.parse(pa.fechaInicio)));
				p.setFechaFin(CalculoCobroService.aDate(LocalDate.parse(pa.fechaFin)));
				cobroPeriodoAnualDao.save(p);
			}
		}
		return ResponseEntity.ok(getConfiguracionCobro(codigoTipoPlaza));
	}

	private ResponseEntity<?> error(String mensaje) {
		return ResponseEntity.badRequest().body(Map.of("mensaje", mensaje));
	}

	/** @return el motivo por el que la configuración no es válida, o null si lo es */
	private String validar(JBConfiguracionCobro jb) {
		if (jb.tipoCobro == null
				|| !List.of(CalendarioCobro.DIARIO, CalendarioCobro.MENSUAL, CalendarioCobro.ANUAL).contains(jb.tipoCobro)) {
			return "El tipo de cobro debe ser DIARIO, MENSUAL o ANUAL";
		}
		try {
			LocalDate.parse(jb.fechaInicioCobro);
		} catch (Exception e) {
			return "Indique la fecha de inicio de cobro";
		}
		if (CalendarioCobro.DIARIO.equals(jb.tipoCobro)) {
			if (jb.diasSemana == null || jb.diasSemana.isEmpty()) {
				return "Seleccione al menos un día de la semana";
			}
			for (Integer d : jb.diasSemana) {
				if (d == null || d < 1 || d > 7) {
					return "Día de la semana no válido: " + d;
				}
			}
		}
		if (CalendarioCobro.MENSUAL.equals(jb.tipoCobro)) {
			if (jb.diasMes == null || jb.diasMes.isEmpty()) {
				return "Configure al menos una fecha de cobro";
			}
			for (JBConfiguracionCobro.DiaMes dm : jb.diasMes) {
				boolean ultimo = Boolean.TRUE.equals(dm.ultimoDia);
				if (dm.mes == null || dm.mes < 1 || dm.mes > 12) {
					return "Mes no válido: " + dm.mes;
				}
				if (dm.anio != null && (dm.anio < 2000 || dm.anio > 2100)) {
					return "Año no válido: " + dm.anio;
				}
				if (!ultimo && (dm.dia == null || !CalendarioCobro.diaValido(dm.mes, dm.dia)
						|| (dm.anio != null && dm.dia > java.time.YearMonth.of(dm.anio, dm.mes).lengthOfMonth()))) {
					return "El día " + dm.dia + " no existe en el mes " + dm.mes + (dm.anio != null ? " de " + dm.anio : "");
				}
			}
		}
		if (CalendarioCobro.ANUAL.equals(jb.tipoCobro)) {
			if (jb.periodoInicioDia == null || jb.periodoInicioMes == null || jb.periodoFinDia == null
					|| jb.periodoFinMes == null
					|| !CalendarioCobro.diaValido(jb.periodoInicioMes, jb.periodoInicioDia)
					|| !CalendarioCobro.diaValido(jb.periodoFinMes, jb.periodoFinDia)) {
				return "Indique fechas válidas de inicio y fin del período de pago";
			}
			if (jb.periodoInicioMes * 100 + jb.periodoInicioDia > jb.periodoFinMes * 100 + jb.periodoFinDia) {
				return "El inicio del período de pago debe ser anterior al fin";
			}
			List<Integer> anios = new ArrayList<>();
			for (JBConfiguracionCobro.PeriodoAnual pa : jb.periodosAnuales) {
				try {
					LocalDate inicio = LocalDate.parse(pa.fechaInicio);
					LocalDate fin = LocalDate.parse(pa.fechaFin);
					if (pa.anio == null || inicio.isAfter(fin) || fin.getYear() != pa.anio) {
						return "El período del año " + pa.anio + " no es válido: debe terminar en ese año y su inicio ser anterior al fin";
					}
				} catch (Exception e) {
					return "Indique fechas válidas para el período del año " + pa.anio;
				}
				if (anios.contains(pa.anio)) {
					return "El año " + pa.anio + " tiene más de un período";
				}
				anios.add(pa.anio);
			}
		}
		return null;
	}

	/**
	 * Licencia de comercio y adeudo anterior se actualizan sobre el mismo registro:
	 * no cambian la vigencia, así que no pasan por el PUT de propietarioPlaza, que
	 * cierra el registro y crea uno nuevo.
	 */
	@PutMapping("/propietarioPlaza/{idPropietarioPlaza}/datosCobro")
	public ResponseEntity<?> actualizarDatosCobro(@PathVariable String idPropietarioPlaza,
			@RequestBody Map<String, Object> datos) {
		PropietarioPlaza pp = propietarioPlazaDao.findById(idPropietarioPlaza).orElse(null);
		if (pp == null) {
			return error("No existe el propietario de plaza " + idPropietarioPlaza);
		}
		Object adeudo = datos.get("adeudoAnterior");
		if (adeudo != null && !(adeudo instanceof Number)) {
			return error("El adeudo anterior debe ser un importe");
		}
		if (adeudo != null && ((Number) adeudo).doubleValue() < 0) {
			return error("El adeudo anterior no puede ser negativo");
		}
		pp.setLicenciaComercio(Boolean.TRUE.equals(datos.get("licenciaComercio")));
		pp.setAdeudoAnterior(adeudo != null ? ((Number) adeudo).doubleValue() : null);
		propietarioPlazaDao.save(pp);
		return ResponseEntity.ok(Map.of("idPropietarioPlaza", idPropietarioPlaza));
	}

	/**
	 * Corrige hacia atrás la fecha inicial del mismo registro ({vigenciaInicial: "yyyy-MM-dd"}).
	 * Editar con una fecha posterior cierra el registro y crea otro (PUT /propietarioPlaza/{id});
	 * una fecha anterior es una corrección, así que no puede encimarse con otro registro de la plaza.
	 */
	@PutMapping("/propietarioPlaza/{idPropietarioPlaza}/vigenciaInicial")
	public ResponseEntity<?> corregirVigenciaInicial(@PathVariable String idPropietarioPlaza,
			@RequestBody Map<String, String> datos) {
		PropietarioPlaza pp = propietarioPlazaDao.findById(idPropietarioPlaza).orElse(null);
		if (pp == null) {
			return error("No existe el propietario de plaza " + idPropietarioPlaza);
		}
		LocalDate nueva;
		try {
			nueva = LocalDate.parse(datos.get("vigenciaInicial"));
		} catch (Exception e) {
			return error("La fecha inicial no es válida");
		}
		if (pp.getVigenciaFinal() != null && nueva.isAfter(CalculoCobroService.aLocalDate(pp.getVigenciaFinal()))) {
			return error("La fecha inicial no puede ser posterior a la final");
		}
		String codigoPlaza = pp.getPlaza() != null ? pp.getPlaza().getCodigoPlaza() : null;
		for (PropietarioPlaza otro : propietarioPlazaDao.findAll()) {
			if (otro.getIdPropietarioPlaza().equals(idPropietarioPlaza) || otro.getPlaza() == null
					|| !otro.getPlaza().getCodigoPlaza().equals(codigoPlaza) || otro.getVigenciaInicial() == null
					|| otro.getVigenciaFinal() == null) {
				continue;
			}
			LocalDate inicioOtro = CalculoCobroService.aLocalDate(otro.getVigenciaInicial());
			LocalDate finOtro = CalculoCobroService.aLocalDate(otro.getVigenciaFinal());
			// sólo importan los registros anteriores de la plaza que terminan en o después de la nueva fecha
			if (inicioOtro.isBefore(CalculoCobroService.aLocalDate(pp.getVigenciaInicial())) && !finOtro.isBefore(nueva)) {
				return error("La plaza " + codigoPlaza + " tiene otro registro vigente hasta el "
						+ finOtro.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy"))
						+ ": la fecha inicial debe ser posterior");
			}
		}
		pp.setVigenciaInicial(CalculoCobroService.aDate(nueva));
		propietarioPlazaDao.save(pp);
		return ResponseEntity.ok(Map.of("idPropietarioPlaza", idPropietarioPlaza));
	}

	// --- Morosidad y requerimientos de pago ---

	/** Fecha de corte "yyyy-MM-dd"; sin ella, hoy */
	private static LocalDate corte(String fechaCorte) {
		return fechaCorte == null || fechaCorte.isBlank() ? LocalDate.now() : LocalDate.parse(fechaCorte);
	}

	@GetMapping("/morosidad")
	public List<JBAdeudo> getMorosidad(@RequestParam(defaultValue = "true") Boolean todosTP,
			@RequestParam(required = false) String codigoTP, @RequestParam(required = false) String fechaCorte) {
		return calculoCobroService.calcularAdeudos(todosTP ? null : codigoTP, null, corte(fechaCorte));
	}

	@GetMapping("/morosidadreporte")
	public byte[] reporteMorosidad(HttpServletResponse response, @RequestParam Integer formato,
			@RequestParam(defaultValue = "true") Boolean todosTP, @RequestParam(required = false) String codigoTP,
			@RequestParam(required = false) String fechaCorte) throws Exception {
		LocalDate corte = corte(fechaCorte);
		List<JBAdeudo> adeudos = calculoCobroService.calcularAdeudos(todosTP ? null : codigoTP, null, corte);
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("encabezadoTiposPlaza", todosTP ? "Todos" : codigoTP);
		parametros.put("fechaCorte", CalculoCobroService.aDate(corte));
		return generar("/reportes/morosidad_reporte.jrxml", parametros, adeudos, formato, "morosidad", response);
	}

	/** Una hoja por cada plaza con adeudo; con codigoPlaza se genera sólo la de esa plaza. */
	@GetMapping("/requerimientopagoreporte")
	public byte[] reporteRequerimientoPago(HttpServletResponse response,
			@RequestParam(defaultValue = "true") Boolean todosTP, @RequestParam(required = false) String codigoTP,
			@RequestParam(required = false) String codigoPlaza, @RequestParam(required = false) String observaciones,
			@RequestParam(required = false) String fechaCorte) throws Exception {
		boolean unaPlaza = codigoPlaza != null && !codigoPlaza.isEmpty();
		LocalDate corte = corte(fechaCorte);
		List<JBAdeudo> adeudos = calculoCobroService.calcularAdeudos(todosTP ? null : codigoTP,
				unaPlaza ? codigoPlaza : null, corte);
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("observaciones", observaciones != null ? observaciones : "");
		parametros.put("fechaCorte", CalculoCobroService.aDate(corte));
		return generar("/reportes/requerimiento_pago_reporte.jrxml", parametros, adeudos, REPORTE_PDF,
				unaPlaza ? "requerimiento_" + codigoPlaza : "requerimientos_pago", response);
	}

	private byte[] generar(String plantilla, Map<String, Object> parametros, List<JBAdeudo> filas, Integer formato,
			String nombre, HttpServletResponse response) throws Exception {
		return generar(plantilla, parametros, new JRBeanCollectionDataSource(filas), formato, nombre, response);
	}

	private byte[] generar(String plantilla, Map<String, Object> parametros, JRDataSource datos, Integer formato,
			String nombre, HttpServletResponse response) throws Exception {
		parametros.put("logo", this.getClass().getResourceAsStream("/logos/logo.png"));
		byte[] archivo;
		try (InputStream plantillaStream = this.getClass().getResourceAsStream(plantilla)) {
			JasperReport jasperReport = JasperCompileManager.compileReport(plantillaStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, datos);
			if (formato == REPORTE_PDF) {
				archivo = JasperExportManager.exportReportToPdf(jasperPrint);
				response.setContentType("application/pdf");
				nombre += ".pdf";
			} else {
				ByteArrayOutputStream salida = new ByteArrayOutputStream();
				JRXlsxExporter exporter = new JRXlsxExporter();
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(salida));
				SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
				reportConfig.setRemoveEmptySpaceBetweenColumns(true);
				reportConfig.setRemoveEmptySpaceBetweenRows(true);
				reportConfig.setWhitePageBackground(false);
				reportConfig.setDetectCellType(true);
				exporter.setConfiguration(reportConfig);
				exporter.exportReport();
				archivo = salida.toByteArray();
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
				nombre += ".xlsx";
			}
		}
		response.setHeader("Content-disposition", "attachment; filename=" + nombre);
		response.setContentLength(archivo.length);
		return archivo;
	}

	// --- Bitácora de condonaciones ---

	@GetMapping("/condonaciones")
	public List<Map<String, Object>> getCondonaciones(@RequestParam String fechaInicial,
			@RequestParam String fechaFinal) throws Exception {
		return condonacionesEnPeriodo(fechaInicial, fechaFinal);
	}

	/** La bitácora del período en PDF; mismas filas que /condonaciones. Fechas dd-MM-yyyy. */
	@GetMapping("/condonacionesreporte")
	public byte[] reporteCondonaciones(HttpServletResponse response, @RequestParam String fechaInicial,
			@RequestParam String fechaFinal) throws Exception {
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("periodo", "Del " + fechaInicial.replace('-', '/') + " al " + fechaFinal.replace('-', '/'));
		Collection<Map<String, ?>> filas = new ArrayList<>(condonacionesEnPeriodo(fechaInicial, fechaFinal));
		return generar("/reportes/condonaciones_reporte.jrxml", parametros, new JRMapCollectionDataSource(filas),
				REPORTE_PDF, "condonaciones", response);
	}

	private List<Map<String, Object>> condonacionesEnPeriodo(String fechaInicial, String fechaFinal) throws Exception {
		SimpleDateFormat sp = new SimpleDateFormat("dd-MM-yyyy");
		Calendar hasta = Calendar.getInstance();
		hasta.setTime(sp.parse(fechaFinal));
		hasta.add(Calendar.DAY_OF_MONTH, 1);
		SimpleDateFormat sdfDia = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		List<Map<String, Object>> filas = new ArrayList<>();
		for (Condonacion c : condonacionDao.enPeriodo(sp.parse(fechaInicial), hasta.getTime())) {
			Map<String, Object> fila = new LinkedHashMap<>();
			fila.put("idCondonacion", c.getIdCondonacion());
			fila.put("fechaCondonacion", sdfHora.format(c.getFechaCondonacion()));
			fila.put("codigoRecaudador", c.getRecaudador() != null ? c.getRecaudador().getCodigoRecaudador() : "");
			fila.put("nombreRecaudador", c.getRecaudador() != null ? c.getRecaudador().getNombreRecaudador() : "");
			fila.put("codigoPlaza", c.getPlaza() != null ? c.getPlaza().getCodigoPlaza() : "");
			fila.put("tipoPlaza", c.getTipoPlaza() != null ? c.getTipoPlaza().getCodigoTipoPlaza() : "");
			fila.put("codigoContribuyente",
					c.getContribuyente() != null ? c.getContribuyente().getCodigoContribuyente() : "");
			fila.put("nombreContribuyente", c.getContribuyente() == null ? ""
					: (c.getContribuyente().getNombre() + " " + c.getContribuyente().getApePaterno() + " "
							+ c.getContribuyente().getApeMaterno()));
			fila.put("fechaObligacion", c.getFechaObligacion() != null ? sdfDia.format(c.getFechaObligacion()) : "");
			fila.put("montoCondonado", c.getMontoCondonado());
			fila.put("motivo", c.getMotivo());
			fila.put("origen", c.getOrigen());
			fila.put("codigoEquipo",
					c.getEquipoRecaudador() != null ? c.getEquipoRecaudador().getCodigoEquipoRec() : "");
			filas.add(fila);
		}
		return filas;
	}

	// --- App móvil ---

	/** Configuración de cobro de todos los tipos de plaza, con todos los valores como cadena. */
	@GetMapping("/getConfiguracionCobro")
	public List<Map<String, Object>> getConfiguracionCobroMovil() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Map<String, Object>> filas = new ArrayList<>();
		for (ConfiguracionCobro config : configuracionCobroDao.findAll()) {
			if (!CalculoCobroService.aplica(config)) {
				continue;
			}
			String codigo = config.getTipoPlaza().getCodigoTipoPlaza();
			Map<String, Object> fila = new HashMap<>();
			fila.put("codigoTipoPlaza", codigo);
			fila.put("tipoCobro", config.getTipoCobro());
			fila.put("diasSemana", cadena(config.getDiasSemana()));
			fila.put("fechaInicioCobro", sdf.format(config.getFechaInicioCobro()));
			// "mes:dia" separados por coma; U en lugar del día significa último día del mes.
			// Los de un año específico van como "anio:mes:dia" (versiones anteriores de la app los ignoran)
			fila.put("diasMes", cobroMensualDiaDao.porTipoPlaza(codigo).stream()
					.map(d -> (d.getAnio() != null ? d.getAnio() + ":" : "") + d.getMes() + ":"
							+ (Boolean.TRUE.equals(d.getUltimoDia()) ? "U" : d.getDia()))
					.collect(Collectors.joining(",")));
			fila.put("periodoInicioDia", cadena(config.getPeriodoInicioDia()));
			fila.put("periodoInicioMes", cadena(config.getPeriodoInicioMes()));
			fila.put("periodoFinDia", cadena(config.getPeriodoFinDia()));
			fila.put("periodoFinMes", cadena(config.getPeriodoFinMes()));
			// "anio:inicio:fin" separados por coma
			fila.put("periodosAnuales", cobroPeriodoAnualDao.porTipoPlaza(codigo).stream()
					.map(p -> p.getAnio() + ":" + sdf.format(p.getFechaInicio()) + ":" + sdf.format(p.getFechaFin()))
					.collect(Collectors.joining(",")));
			filas.add(fila);
		}
		return filas;
	}

	private String cadena(Object valor) {
		return valor != null ? valor.toString() : "";
	}
}
