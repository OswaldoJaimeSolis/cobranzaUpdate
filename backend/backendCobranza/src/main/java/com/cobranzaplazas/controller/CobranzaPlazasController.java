package com.cobranzaplazas.controller;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cobranzaplazas.model.GeneradorCodigos;
import com.cobranzaplazas.model.JBContribucionReporte;
import com.cobranzaplazas.model.JBContribuyente;
import com.cobranzaplazas.model.JBPropietarioPlaza;
import com.cobranzaplazas.repo.Contribucion;
import com.cobranzaplazas.repo.ContribucionDao;
import com.cobranzaplazas.repo.Contribuyente;
import com.cobranzaplazas.repo.ContribuyenteDao;
import com.cobranzaplazas.repo.EquipoRecaudador;
import com.cobranzaplazas.repo.EquipoRecaudadorDao;
import com.cobranzaplazas.repo.Folio;
import com.cobranzaplazas.repo.FolioDao;
import com.cobranzaplazas.repo.Plaza;
import com.cobranzaplazas.repo.PlazaDao;
import com.cobranzaplazas.repo.PropietarioPlaza;
import com.cobranzaplazas.repo.PropietarioPlazaDao;
import com.cobranzaplazas.repo.Recaudador;
import com.cobranzaplazas.repo.RecaudadorDao;
import com.cobranzaplazas.repo.RecaudadorTipoPlaza;
import com.cobranzaplazas.repo.RecaudadorTipoPlazaDao;
import com.cobranzaplazas.repo.TipoPlaza;
import com.cobranzaplazas.repo.TipoPlazaDao;
import com.cobranzaplazas.repo.TipoPlazaVigencia;
import com.cobranzaplazas.repo.TipoPlazaVigenciaDao;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;
import net.sf.jasperreports.export.SimplePdfReportConfiguration;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;

@RestController
@RequestMapping("/")
public class CobranzaPlazasController {
	private final Integer REPORTE_PDF = 1;
	private final Integer REPORTE_EXCEL = 2;
	private final String port = "4200";

	@Autowired
	RecaudadorDao recaudadorDao;

	@Autowired
	RecaudadorTipoPlazaDao recaudadorTipoPlazaDao;

	@Autowired
	ContribuyenteDao contribuyenteDao;

	@Autowired
	TipoPlazaDao tipoPlazaDao;

	@Autowired
	TipoPlazaVigenciaDao tipoPlazaVigenciaDao;

	@Autowired
	PropietarioPlazaDao propietarioPlazaDao;

	@Autowired
	FolioDao folioDao;

	@Autowired
	PlazaDao plazaDao;

	@Autowired
	ContribucionDao contribucionDao;

	@Autowired
	EquipoRecaudadorDao equipoRecaudadorDao;

	private SimpleDateFormat spdf;
	private GeneradorCodigos generadorCodigos;

	// @Secured("ROLE_ADMIN")
	@GetMapping("/tiposPlazaVigencia/{codigoTipoPlaza}")
	@CrossOrigin(origins = "http://localhost:" + port)
	public Collection<TipoPlazaVigencia> getTiposPlaza(@PathVariable String codigoTipoPlaza) {

		return tipoPlazaVigenciaDao.historialTipoPlaza(codigoTipoPlaza);
	}

	@PostMapping("/tiposPlazaVigencia")
	@CrossOrigin(origins = "http://localhost:" + port)
	TipoPlazaVigencia newTipoPlaza(@RequestBody TipoPlazaVigencia tipoPlazaVigencia) {
		return tipoPlazaVigenciaDao.save(tipoPlazaVigencia);
	}

	@PutMapping("/tiposPlazaVigencia/{id}")
	@CrossOrigin(origins = "http://localhost:" + port)
	TipoPlazaVigencia replaceTipoPlazaVigencia(@RequestBody TipoPlazaVigencia newTipoPlazaVigencia,
			@PathVariable Integer id) {

		return tipoPlazaVigenciaDao.findById(id).map(tpv -> {
			tpv.setVigenciaFinal(newTipoPlazaVigencia.getVigenciaFinal());
			return tipoPlazaVigenciaDao.save(tpv);
		}).orElseGet(() -> {

			return tipoPlazaVigenciaDao.save(newTipoPlazaVigencia);
		});
	}

	@DeleteMapping("/tiposPlazaVigencia/{id}")

	@CrossOrigin(origins = "http://localhost:" + port)
	void deleteTipoPlazaVigencia(@PathVariable Integer id) {
		tipoPlazaVigenciaDao.deleteById(id);
	}

	// @Secured("ROLE_ADMIN")

	@GetMapping("/tiposPlaza")

	@CrossOrigin(origins = "http://localhost:" + port)
	public Collection<TipoPlaza> getTiposPlaza() {
		return tipoPlazaDao.findAll().stream().collect(Collectors.toList());
	}

	@PostMapping("/tiposPlaza")

	@CrossOrigin(origins = "http://localhost:" + port)
	TipoPlaza newTipoPlaza(@RequestBody TipoPlaza tipoPlaza) {
		return tipoPlazaDao.save(tipoPlaza);
	}

	@GetMapping("/tiposPlaza/{id}")

	@CrossOrigin(origins = "http://localhost:" + port)
	TipoPlaza oneTipoPlaza(@PathVariable String id) {

		return tipoPlazaDao.findById(id).orElseThrow(() -> new RuntimeException(id));
	}

	@PutMapping("/tiposPlaza/{id}")

	@CrossOrigin(origins = "http://localhost:" + port)
	TipoPlaza replaceTipoPlaza(@RequestBody TipoPlaza newTipoPlaza, @PathVariable String id) {

		return tipoPlazaDao.findById(id).map(tp -> {
			tp.setAddLocal(newTipoPlaza.getAddLocal());
			tp.setDescripcionTipoPlaza(newTipoPlaza.getDescripcionTipoPlaza());
			tp.setLeyendaTipoPlaza(newTipoPlaza.getLeyendaTipoPlaza());
			tp.setPorImporteGlobalTipoPlaza(newTipoPlaza.getPorImporteGlobalTipoPlaza());

			return tipoPlazaDao.save(tp);
		}).orElseGet(() -> {

			return tipoPlazaDao.save(newTipoPlaza);
		});
	}

	@DeleteMapping("/tiposPlaza/{id}")

	@CrossOrigin(origins = "http://localhost:" + port)
	void deleteTipoPlaza(@PathVariable String id) {
		tipoPlazaDao.deleteById(id);
	}

	// @Secured("ROLE_ADMIN")

	@GetMapping("/contribuyentes")

	@CrossOrigin(origins = "http://localhost:" + port)
	public Collection<Contribuyente> getContribuyentes() {
		return contribuyenteDao.findAll().stream().collect(Collectors.toList());
	}

	@PostMapping("/contribuyentes")

	@CrossOrigin(origins = "http://localhost:" + port)
	Contribuyente newContribuyene(@RequestBody Contribuyente contribuyente) {

		return contribuyenteDao.save(crearContribuyente(contribuyente));
	}

	private Contribuyente crearContribuyente(Contribuyente contribuyente) {
		String codigoTmp = getGeneradorCodigos().generarCodigoContribuyente(contribuyente);
		if (contribuyente.getRfcContribuyente() != null && contribuyente.getRfcContribuyente().contains(codigoTmp)) {
			contribuyente.setCodigoContribuyente(contribuyente.getRfcContribuyente());
		} else {
			String codeFolio = "CONTRIBUYENTES";
			Folio folio = null;
			Optional<Folio> optionalFolio = folioDao.findById(codeFolio);
			if (optionalFolio.isPresent()) {
				folio = optionalFolio.get();
				folio.setFolio(folio.getFolio() + 1);
			} else {
				folio = new Folio();
				folio.setCodigoFolio(codeFolio);
				folio.setFolio(1);
			}
			folioDao.save(folio);
			contribuyente.setCodigoContribuyente(codigoTmp + "_" + String.format("%03d", folio.getFolio()));

		}
		return contribuyente;
	}

	@GetMapping("/contribuyentes/{id}")

	@CrossOrigin(origins = "http://localhost:" + port)
	Contribuyente oneContribuyente(@PathVariable String id) {

		return contribuyenteDao.findById(id).orElseThrow(() -> new RuntimeException(id));
	}

	@PutMapping("/contribuyentes/{id}")

	@CrossOrigin(origins = "http://localhost:" + port)
	Contribuyente replaceContribuyente(@RequestBody Contribuyente newContribuyente, @PathVariable String id) {

		return contribuyenteDao.findById(id).map(con -> {
			con.setNombre(newContribuyente.getNombre());
			con.setApeMaterno(newContribuyente.getApeMaterno());
			con.setApePaterno(newContribuyente.getApePaterno());
			con.setRfcContribuyente(newContribuyente.getRfcContribuyente());

			return contribuyenteDao.save(con);
		}).orElseGet(() -> {

			return contribuyenteDao.save(newContribuyente);
		});
	}

	@DeleteMapping("/contribuyentes/{id}")

	@CrossOrigin(origins = "http://localhost:" + port)
	void deleteContribuyente(@PathVariable String id) {
		contribuyenteDao.deleteById(id);
	}

	@GetMapping("/recaudadores")

	@CrossOrigin(origins = "http://localhost:" + port)
	public Collection<Recaudador> getRecaudadores() {
		return recaudadorDao.findAll().stream().collect(Collectors.toList());
	}

	@PostMapping("/recaudadores")

	@CrossOrigin(origins = "http://localhost:" + port)
	Recaudador newRecaudador(@RequestBody Recaudador recaudador) {
		return recaudadorDao.save(recaudador);
	}

	@GetMapping("/recaudadores/{id}")

	@CrossOrigin(origins = "http://localhost:" + port)
	Recaudador one(@PathVariable String id) {

		return recaudadorDao.findById(id).orElseThrow(() -> new RuntimeException(id));
	}

	@PutMapping("/recaudadores/{id}")

	@CrossOrigin(origins = "http://localhost:" + port)
	Recaudador replaceRecaudador(@RequestBody Recaudador newRecaudador, @PathVariable String id) {

		return recaudadorDao.findById(id).map(rec -> {
			rec.setCodigoRecaudador(newRecaudador.getCodigoRecaudador());
			rec.setActivoRecaudador(newRecaudador.getActivoRecaudador());
			rec.setNombreRecaudador(newRecaudador.getNombreRecaudador());
			rec.setPassRecaudador(newRecaudador.getPassRecaudador());

			return recaudadorDao.save(rec);
		}).orElseGet(() -> {
			newRecaudador.setCodigoRecaudador(id);
			return recaudadorDao.save(newRecaudador);
		});
	}

	@DeleteMapping("/recaudadores/{id}")

	@CrossOrigin(origins = "http://localhost:" + port)
	void deleteRecaudador(@PathVariable String id) {
		recaudadorDao.deleteById(id);
	}

	@GetMapping("/recaudadoresTP/{codigoRecaudador}")
	@CrossOrigin(origins = "http://localhost:" + port)
	public Collection<RecaudadorTipoPlaza> getRecaudadorTP(@PathVariable String codigoRecaudador) {

		return recaudadorTipoPlazaDao.recaudadorTiposPlaza(codigoRecaudador);
	}

	@PostMapping("/recaudadoresTP")
	@CrossOrigin(origins = "http://localhost:" + port)
	RecaudadorTipoPlaza newRecaudadorTP(@RequestBody RecaudadorTipoPlaza recaudadorTipoPlaza) {
		return recaudadorTipoPlazaDao.save(recaudadorTipoPlaza);
	}

	@DeleteMapping("/recaudadoresTP/{id}")

	@CrossOrigin(origins = "http://localhost:" + port)
	void deleteRecaudadorTP(@PathVariable Integer id) {
		recaudadorTipoPlazaDao.deleteById(id);
	}

	@GetMapping("/propietarioPlaza")

	@CrossOrigin(origins = "http://localhost:" + port)
	public Collection<PropietarioPlaza> getPropietariosPlaza() {
		try {
			Pageable pp = PageRequest.of(0, 1);
			return propietarioPlazaDao.findAll().stream().collect(Collectors.toList());
		} catch (Exception e) {

			return null;
		}
	}

	@GetMapping("/propietarioPlazaJB")
	@CrossOrigin(origins = "http://localhost:" + port)
	public Collection<JBPropietarioPlaza> getPropietariosPlazaJB() {
		try {
			Pageable pp = PageRequest.of(0, 1);
			return propietarioPlazaDao.recuperarPropietarios().stream().collect(Collectors.toList());
		} catch (Exception e) {

			return null;
		}
	}

	@GetMapping("/propietarioPlaza/{id}")

	@CrossOrigin(origins = "http://localhost:" + port)
	PropietarioPlaza onePropietarioPlaza(@PathVariable String id) {

		return propietarioPlazaDao.findById(id).orElseThrow(() -> new RuntimeException(id));
	}

	@PostMapping("/propietarioPlaza")
	@CrossOrigin(origins = "http://localhost:" + port)
	@Transactional
	PropietarioPlaza newPropietarioPlaza(@RequestBody PropietarioPlaza propietarioPlaza) {

		// recuperar el número que le toca a la plaza
		Folio folio = null;
		Optional<Folio> optionalfolio = folioDao.findById(propietarioPlaza.getTipoPlaza().getCodigoTipoPlaza());
		if (optionalfolio.isPresent()) {
			folio = optionalfolio.get();
			folio.setFolio(folio.getFolio() + 1);

		} else {
			folio = new Folio();
			folio.setCodigoFolio(propietarioPlaza.getTipoPlaza().getCodigoTipoPlaza());

			folio.setFolio(1);
		}
		folioDao.save(folio);

		// Plaza
		Plaza plaza = new Plaza();
		plaza.setCodigoPlaza(
				propietarioPlaza.getTipoPlaza().getCodigoTipoPlaza() + "_" + String.format("%03d", folio.getFolio()));
		plazaDao.save(plaza);

		propietarioPlaza.setPlaza(plaza);
		propietarioPlaza.setVigenciaFinal(getFechaFinal());

		// Folio propietarioPlaza
		Folio folioPP = null;
		Optional<Folio> optionalfolioPP = folioDao.findById("PP");
		if (optionalfolioPP.isPresent()) {
			folioPP = optionalfolioPP.get();
			folioPP.setFolio(folioPP.getFolio() + 1);

		} else {
			folioPP = new Folio();
			folioPP.setCodigoFolio("PP");
			folioPP.setFolio(1);
		}
		folioDao.save(folioPP);
		propietarioPlaza.setIdPropietarioPlaza(folioPP.getFolio().toString());

		return propietarioPlazaDao.save(propietarioPlaza);

	}

	@PutMapping("/propietarioPlaza/{idPropietarioPlaza}")
	@CrossOrigin(origins = "http://localhost:" + port)
	PropietarioPlaza propietarioPlazaEditar(@RequestBody PropietarioPlaza propietarioPlaza,
			@PathVariable String idPropietarioPlaza) {
		PropietarioPlaza ppOld = propietarioPlazaDao.getReferenceById(idPropietarioPlaza);
		System.out.println("EEEEEEEEEEEEEEE" + idPropietarioPlaza);
		ppOld.setVigenciaFinal(getVigenciaFinalFromVINew(propietarioPlaza.getVigenciaInicial()));
		propietarioPlazaDao.save(ppOld);

		// se guarada como nuevo el modificado
		propietarioPlaza.setIdPropietarioPlaza(null);
		// Folio propietarioPlaza
		Folio folioPP = null;
		Optional<Folio> optionalfolioPP = folioDao.findById("PP");
		if (optionalfolioPP.isPresent()) {
			folioPP = optionalfolioPP.get();
			folioPP.setFolio(folioPP.getFolio() + 1);

		} else {
			folioPP = new Folio();
			folioPP.setCodigoFolio("PP");
			folioPP.setFolio(1);
		}
		propietarioPlaza.setIdPropietarioPlaza(folioPP.getFolio().toString());

		return propietarioPlazaDao.save(propietarioPlaza);

	}

	// --- Sincronización con la app móvil (movilCobranza / WebService.java) ---
	// Estos 9 endpoints reproducen el contrato de los antiguos scripts PHP en
	// https://jalpa.gob.mx/cobranza/*.php que la app usaba directamente. Los
	// nombres y tipos de campo (p. ej. cadenas "1"/"0" en vez de booleanos,
	// nombres URL-encoded) se conservan tal cual porque WebService.java los
	// parsea así y no se modificó ese lado.

	@GetMapping("/getTipoPlaza")
	public List<Map<String, Object>> getTipoPlazaMovil() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Map<String, Object>> filas = new ArrayList<>();
		for (TipoPlaza tp : tipoPlazaDao.findAll()) {
			boolean global = Boolean.TRUE.equals(tp.getPorImporteGlobalTipoPlaza());
			if (global) {
				Collection<TipoPlazaVigencia> historial = tipoPlazaVigenciaDao.historialTipoPlaza(tp.getCodigoTipoPlaza());
				if (historial.isEmpty()) {
					filas.add(filaTipoPlaza(tp, null, sdf));
				} else {
					for (TipoPlazaVigencia vigencia : historial) {
						filas.add(filaTipoPlaza(tp, vigencia, sdf));
					}
				}
			} else {
				filas.add(filaTipoPlaza(tp, null, sdf));
			}
		}
		return filas;
	}

	private Map<String, Object> filaTipoPlaza(TipoPlaza tp, TipoPlazaVigencia vigencia, SimpleDateFormat sdf) {
		Map<String, Object> fila = new HashMap<>();
		fila.put("codigoTipoPlaza", tp.getCodigoTipoPlaza());
		fila.put("descripcionTipoPlaza", tp.getDescripcionTipoPlaza());
		fila.put("porImporteGlobal", Boolean.TRUE.equals(tp.getPorImporteGlobalTipoPlaza()) ? "1" : "0");
		fila.put("addLocal", Boolean.TRUE.equals(tp.getAddLocal()) ? "1" : "0");
		if (vigencia != null) {
			fila.put("vigenciaInicial", sdf.format(vigencia.getVigenciaInicial()));
			fila.put("vigenciaFinal", sdf.format(vigencia.getVigenciaFinal()));
			fila.put("importe", vigencia.getImporte());
		}
		return fila;
	}

	// La app manda la fecha de hoy (MainActivity.actualizarCatalogosRemoto) y espera el
	// catálogo de asignaciones con el que va a cobrar, así que se devuelven las
	// propietarioPlaza que no han vencido a esa fecha (vigentes o que inician después).
	// La app inserta con REPLACE, por lo que recibir el catálogo completo es seguro.
	@PostMapping("/getContribuyentes")
	public List<Map<String, Object>> getContribuyentesMovil(@RequestParam String fechaRecuperar) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date fecha = sdf.parse(fechaRecuperar);
		List<Map<String, Object>> filas = new ArrayList<>();
		for (PropietarioPlaza pp : propietarioPlazaDao.findAll()) {
			// La app parsea ambas fechas; sin ellas el registro rompería toda la sincronización.
			if (pp.getVigenciaInicial() == null || pp.getVigenciaFinal() == null
					|| pp.getVigenciaFinal().before(fecha)) {
				continue;
			}
			Contribuyente con = pp.getContribuyente();
			Plaza plaza = pp.getPlaza();
			TipoPlaza tp = pp.getTipoPlaza();
			Map<String, Object> fila = new HashMap<>();
			fila.put("codigoContribuyente", con.getCodigoContribuyente());
			fila.put("nombre", urlEncode(con.getNombre()));
			fila.put("apePaterno", urlEncode(con.getApePaterno()));
			fila.put("apeMaterno", urlEncode(con.getApeMaterno()));
			fila.put("rfc", con.getRfcContribuyente() != null ? con.getRfcContribuyente() : "");
			fila.put("codigoPlaza", plaza.getCodigoPlaza());
			fila.put("longitudPlaza", plaza.getLongitudPlaza() != null ? plaza.getLongitudPlaza().toString() : "");
			fila.put("latitudPlaza", plaza.getLatitudPlaza() != null ? plaza.getLatitudPlaza().toString() : "");
			fila.put("codigoTipoPlaza", tp.getCodigoTipoPlaza());
			fila.put("codigoPropietarioPlaza", pp.getIdPropietarioPlaza());
			fila.put("vigenciaInicial", sdf.format(pp.getVigenciaInicial()));
			fila.put("vigenciaFinal", sdf.format(pp.getVigenciaFinal()));
			fila.put("importe", pp.getImporte() != null ? pp.getImporte().toString() : "");
			fila.put("giro", pp.getGiroDescripcion() != null ? pp.getGiroDescripcion() : "");
			filas.add(fila);
		}
		return filas;
	}

	// Sólo contribuciones del tipo de plaza y fecha pedidos que NO hayan sido
	// capturadas por este mismo equipo recaudador (ver el javadoc original en
	// WebService.getContribucionesRemotas): sirven para que un equipo absorba
	// lo que capturaron otros equipos.
	@PostMapping("/getContribuciones")
	public List<Map<String, Object>> getContribucionesMovil(@RequestParam String fechaRecuperar,
			@RequestParam String codigoTipoPlaza, @RequestParam String codigoEquipo) throws Exception {
		SimpleDateFormat sdfDia = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date desde = sdfDia.parse(fechaRecuperar);
		List<Map<String, Object>> filas = new ArrayList<>();
		for (Contribucion c : contribucionDao.findAll()) {
			if (c.getTipoPlaza() == null || !codigoTipoPlaza.equals(c.getTipoPlaza().getCodigoTipoPlaza())) {
				continue;
			}
			if (c.getFechaContribucion() == null || c.getFechaContribucion().before(desde)) {
				continue;
			}
			String equipo = c.getEquipoRecaudador() != null ? c.getEquipoRecaudador().getCodigoEquipoRec() : null;
			if (codigoEquipo.equals(equipo)) {
				continue;
			}
			Map<String, Object> fila = new HashMap<>();
			fila.put("codigoContribuyente", c.getContribuyente().getCodigoContribuyente());
			fila.put("codigoPlaza", c.getPlaza().getCodigoPlaza());
			fila.put("ctipoPlaza", c.getTipoPlaza().getCodigoTipoPlaza());
			fila.put("codigoEquipo", equipo);
			fila.put("codigoRecaudador", c.getRecaudador() != null ? c.getRecaudador().getCodigoRecaudador() : null);
			fila.put("folioDedicado", c.getIdContribucionLocal());
			fila.put("fContribucion", sdfDia.format(c.getFechaContribucion()));
			fila.put("fModificacion", sdfHora.format(c.getFechaModificacion()));
			fila.put("importe", c.getImporteContribucion() != null ? c.getImporteContribucion().toString() : "0");
			fila.put("estadoPago", c.getEstadoPago() != null ? c.getEstadoPago().toString() : "0");
			filas.add(fila);
		}
		return filas;
	}

	@GetMapping("/getRecaudadores")
	public List<Map<String, Object>> getRecaudadoresMovil() {
		List<Map<String, Object>> filas = new ArrayList<>();
		for (Recaudador r : recaudadorDao.findAll()) {
			Collection<RecaudadorTipoPlaza> asignaciones = recaudadorTipoPlazaDao.recaudadorTiposPlaza(r.getCodigoRecaudador());
			if (asignaciones.isEmpty()) {
				filas.add(filaRecaudador(r, null));
			} else {
				for (RecaudadorTipoPlaza rtp : asignaciones) {
					filas.add(filaRecaudador(r, rtp.getTipoPlaza()));
				}
			}
		}
		return filas;
	}

	private Map<String, Object> filaRecaudador(Recaudador r, TipoPlaza tp) {
		Map<String, Object> fila = new HashMap<>();
		fila.put("codigoR", r.getCodigoRecaudador());
		fila.put("passR", r.getPassRecaudador());
		fila.put("nombreR", urlEncode(r.getNombreRecaudador()));
		fila.put("activoR", Boolean.TRUE.equals(r.getActivoRecaudador()) ? "1" : "0");
		fila.put("tipoPlaza", tp != null ? tp.getCodigoTipoPlaza() : "");
		return fila;
	}

	private String urlEncode(String valor) {
		if (valor == null) {
			return "";
		}
		try {
			return java.net.URLEncoder.encode(valor, "UTF-8");
		} catch (java.io.UnsupportedEncodingException e) {
			return valor;
		}
	}

	// Los 5 endpoints insert_*.php originales sólo respondían el texto plano
	// "correcto"; WebService.java comprueba exactamente esa cadena y, si
	// coincide, le hace eco al llamador de los datos que él mismo envió (no
	// hay nada más que parsear en la respuesta), así que aquí se conserva el
	// mismo contrato mínimo.

	@PostMapping("/insert_contribuciones")
	public String insertarContribucionesMovil(@RequestParam String contribuciones) {
		try {
			JsonNode arreglo = new ObjectMapper().readTree(contribuciones);
			SimpleDateFormat sdfHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (JsonNode jo : arreglo) {
				Contribucion c = new Contribucion();
				c.setIdContribucionLocal(jo.path("idContribucion").asText(null));
				c.setEstadoPago(jo.has("estadoPago") ? Integer.parseInt(jo.get("estadoPago").asText()) : null);
				c.setFechaContribucion(sdfHora.parse(jo.path("fecha").asText()));
				c.setFechaModificacion(sdfHora.parse(jo.path("fechaModificacion").asText()));
				c.setTipoPlaza(tipoPlazaDao.findById(jo.path("tipoPlaza").asText()).orElse(null));
				c.setPlaza(plazaDao.findById(jo.path("plaza").asText()).orElse(null));
				c.setContribuyente(contribuyenteDao.findById(jo.path("contribuyente").asText()).orElse(null));
				String codigoRecaudador = jo.path("recaudador").asText(null);
				if (codigoRecaudador != null) {
					c.setRecaudador(recaudadorDao.findById(codigoRecaudador).orElse(null));
				}
				String codigoEquipo = jo.path("equipoRecaudador").asText(null);
				if (codigoEquipo != null) {
					c.setEquipoRecaudador(equipoRecaudadorDao.findById(codigoEquipo).orElse(null));
				}
				c.setImporteContribucion(Double.parseDouble(jo.path("importe").asText()));
				c.setEstadoServidor(true);
				contribucionDao.save(c);
			}
			return "correcto";
		} catch (Exception e) {
			return "error";
		}
	}

	@PostMapping("/insert_equipo_recaudador")
	public String insertarEquipoRecaudadorMovil(@RequestParam String equipoRecaudador) {
		try {
			JsonNode jo = new ObjectMapper().readTree(equipoRecaudador);
			String codigo = jo.path("codigoEquipo").asText(null);
			EquipoRecaudador eq = equipoRecaudadorDao.findById(codigo).orElse(new EquipoRecaudador());
			eq.setCodigoEquipoRec(codigo);
			eq.setDescripcionEquipoRec(jo.path("descripcionEquipo").asText(null));
			equipoRecaudadorDao.save(eq);
			return "correcto";
		} catch (Exception e) {
			return "error";
		}
	}

	@PostMapping("/insert_plazas")
	public String insertarPlazasMovil(@RequestParam(required = false) String user, @RequestParam String plazas) {
		try {
			JsonNode arreglo = new ObjectMapper().readTree(plazas);
			for (JsonNode jo : arreglo) {
				String codigo = jo.path("codigoPlaza").asText(null);
				Plaza plaza = plazaDao.findById(codigo).orElse(new Plaza());
				plaza.setCodigoPlaza(codigo);
				if (jo.hasNonNull("latitud")) {
					plaza.setLatitudPlaza(jo.get("latitud").asDouble());
				}
				if (jo.hasNonNull("longitud")) {
					plaza.setLongitudPlaza(jo.get("longitud").asDouble());
				}
				plazaDao.save(plaza);
			}
			return "correcto";
		} catch (Exception e) {
			return "error";
		}
	}

	@PostMapping("/insert_contribuyentes")
	public String insertarContribuyentesMovil(@RequestParam(required = false) String user,
			@RequestParam String contribuyentes) {
		try {
			JsonNode arreglo = new ObjectMapper().readTree(contribuyentes);
			for (JsonNode jo : arreglo) {
				String codigo = jo.path("codigoContribuyente").asText(null);
				Contribuyente con = contribuyenteDao.findById(codigo).orElse(new Contribuyente());
				con.setCodigoContribuyente(codigo);
				con.setNombre(jo.path("nombre").asText(null));
				con.setApePaterno(jo.path("apePaterno").asText(null));
				con.setApeMaterno(jo.path("apeMaterno").asText(null));
				con.setRfcContribuyente(jo.path("rfc").asText(null));
				contribuyenteDao.save(con);
			}
			return "correcto";
		} catch (Exception e) {
			return "error";
		}
	}

	@PostMapping("/insert_propietario_plaza")
	public String insertarPropietarioPlazaMovil(@RequestParam(required = false) String user,
			@RequestParam("propietarioplaza") String propietarioPlazaJson) {
		try {
			JsonNode arreglo = new ObjectMapper().readTree(propietarioPlazaJson);
			SimpleDateFormat sdfHora = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			for (JsonNode jo : arreglo) {
				String id = jo.path("codigoPropietarioPlaza").asText(null);
				PropietarioPlaza pp = propietarioPlazaDao.findById(id).orElse(new PropietarioPlaza());
				pp.setIdPropietarioPlaza(id);
				pp.setPlaza(plazaDao.findById(jo.path("plaza").asText()).orElse(null));
				pp.setTipoPlaza(tipoPlazaDao.findById(jo.path("tipoPlaza").asText()).orElse(null));
				pp.setContribuyente(contribuyenteDao.findById(jo.path("contribuyente").asText()).orElse(null));
				pp.setGiroDescripcion(jo.path("giro").asText(null));
				pp.setVigenciaInicial(sdfHora.parse(jo.path("vigenciaInicial").asText()));
				pp.setVigenciaFinal(sdfHora.parse(jo.path("vigenciaFinal").asText()));
				if (jo.hasNonNull("importe")) {
					pp.setImporte(jo.get("importe").asDouble());
				}
				propietarioPlazaDao.save(pp);
			}
			return "correcto";
		} catch (Exception e) {
			return "error";
		}
	}

	@RequestMapping(value = "/contribucionesreporte", method = RequestMethod.GET)
	@CrossOrigin(origins = "http://localhost:" + port)
	public @ResponseBody byte[] generateReport(HttpServletResponse response, @RequestParam String fechaInicial,
			@RequestParam String fechaFinal, @RequestParam Integer formato, @RequestParam Boolean todosTP,
			@RequestParam Boolean todosContribuyente, @RequestParam String codigoContribuyente,
			@RequestParam String codigoTP, @RequestParam Integer estadoPago, @RequestParam(required = false) String recaudador  ) throws Exception {

		SimpleDateFormat sp = new SimpleDateFormat("dd-MM-yyyy");
		Date fInicial = sp.parse(fechaInicial);
		Date fFinal = sp.parse(fechaFinal);
		JRBeanCollectionDataSource dataSource = null;
		String pathReport = "/reportes/contribuciones_reporte.jrxml";
		List<String> fieldOrders = new ArrayList<>();
		List<Order> orders = new ArrayList<>();

		orders.add(Order.asc("idContribucion"));
		List<Integer> estadosPago = new ArrayList<>();
		if (estadoPago.equals(0) || estadoPago==4) {
			estadosPago.add(0);
			estadosPago.add(1);
			estadosPago.add(2);
			estadosPago.add(3);
		} else {
			estadosPago.add(estadoPago);
		}

		List<JBContribucionReporte> contribuciones;
		if ((todosTP && todosContribuyente) || estadoPago==4) {
			contribuciones = new ArrayList<>(
					contribucionDao.findContribucionesPeriodo(fInicial, fFinal, estadosPago, Sort.by(orders)));
		} else if (!todosTP && !todosContribuyente) {
			contribuciones = new ArrayList<>(contribucionDao.findContribucionesPeriodoTPContribuyentes(fInicial, fFinal,
					estadosPago, codigoContribuyente, codigoTP, Sort.by(orders)));
		} else if (!todosTP) {
			contribuciones = new ArrayList<>(contribucionDao.findContribucionesPeriodoTP(fInicial, fFinal, estadosPago,
					codigoTP, Sort.by(orders)));
		} else {
			contribuciones = new ArrayList<>(contribucionDao.findContribucionesPeriodoContribuyentes(fInicial, fFinal,
					estadosPago, codigoContribuyente, Sort.by(orders)));
		}

		if (estadoPago == 0 || estadoPago == 4) {
			// hasta este momento ya se recuperaron las contribucines bajo los filtros dados
			// si la bandera está activada quiere decir que se debe rellenar la información

			contribuciones = poblarSinRegistro(contribuciones, fInicial, fFinal, true, todosTP, todosContribuyente,
					codigoTP, codigoContribuyente, estadoPago==4);
		}
		String recaudadorFilter="Todos";
		if(recaudador!=null  && !recaudador.equals("undefined") && !recaudador.isEmpty()){
			contribuciones = contribuciones.stream().filter(con-> con.getCodigoRecaudador().equals(recaudador)).collect(Collectors.toList());
			recaudadorFilter= recaudador;
		}

		dataSource = new JRBeanCollectionDataSource(contribuciones);

		InputStream inputStream = this.getClass().getResourceAsStream(pathReport);
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("encabezadoReporte", "CONTRIBUCIONES EN EL PERÍODO");
		parametros.put("encabezadoContribuyentes", todosContribuyente ? "Todos" : codigoContribuyente);
		parametros.put("encabezadoTiposPlaza", todosTP ? "Todos" : codigoTP);
		parametros.put("encabezadoEstadoPago", estadoPagoToString(estadoPago));
		parametros.put("fechaInicial", fInicial);
		parametros.put("fechaFinal", fFinal);
		parametros.put("encabezadoRecaudador", recaudadorFilter);
		parametros.put("SUBREPORT_DIR", "reportes/");

		try {
			InputStream imagen = this.getClass().getResourceAsStream("/logos/logo.png");
			parametros.put("logo", imagen);
		} catch (Exception e) {
			Log.error(e.toString());
		}

		try {
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

			JRExporter exporter = null;
			String nameFile = "";
			if (formato.equals(REPORTE_PDF)) {

				exporter = new JRPdfExporter();
				nameFile = "cargos.pdf";
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(nameFile));

				SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
				reportConfig.setSizePageToContent(true);
				reportConfig.setForceLineBreakPolicy(false);

				SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
				exportConfig.setEncrypted(true);
				exportConfig.setAllowedPermissionsHint("PRINTING");

				exporter.setConfiguration(reportConfig);
				exporter.setConfiguration(exportConfig);
				response.setContentType("application/pdf");

			} else if (formato.equals(REPORTE_EXCEL)) {
				exporter = new JRXlsExporter();

				nameFile = "cargos.xls";
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(nameFile));

				SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
				reportConfig.setRemoveEmptySpaceBetweenColumns(true);
				reportConfig.setRemoveEmptySpaceBetweenRows(true);
				reportConfig.setWhitePageBackground(false);
				reportConfig.setDetectCellType(true);

				exporter.setConfiguration(reportConfig);
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			}

			exporter.exportReport();
			Path pathFile = Paths.get(nameFile);
			byte[] archivo = Files.readAllBytes(pathFile);

			response.setHeader("Content-disposition", "attachment; filename=" + nameFile);
			response.setContentLength(archivo.length);

			// streamReport(response, pdf, "gasolineras.pdf");\
			return archivo;

		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}

	}

	@RequestMapping(value = "/contribucionespagoreporte", method = RequestMethod.GET)
	@CrossOrigin(origins = "http://localhost:" + port)
	public @ResponseBody byte[] generateReportPago(HttpServletResponse response, @RequestParam String fechaInicial,
			@RequestParam String fechaFinal, @RequestParam Integer formato, @RequestParam Boolean todosTP,
			@RequestParam Boolean todosContribuyente, @RequestParam String codigoContribuyente,
			@RequestParam String codigoTP, @RequestParam Integer estadoPago, @RequestParam(required = false) String recaudador) throws Exception {

		Map<String, Object> parametros = new HashMap<>();
		SimpleDateFormat sp = new SimpleDateFormat("dd-MM-yyyy");
		Date fInicial = sp.parse(fechaInicial);
		Date fFinal = sp.parse(fechaFinal);
		JRBeanCollectionDataSource dataSource = null;
		String pathReport = "/reportes/contribuciones_reporte.jrxml";
		List<String> fieldOrders = new ArrayList<>();
		List<Order> orders = new ArrayList<>();

		orders.add(Order.asc("idContribucion"));
		List<Integer> estadosPago = new ArrayList<>();
		if (estadoPago.equals(0) || estadoPago==4) {
			estadosPago.add(0);
			estadosPago.add(1);
			estadosPago.add(2);
			estadosPago.add(3);

		} else {
			estadosPago.add(estadoPago);
		}

		List<JBContribucionReporte> contribuciones;
		// si son todas los tipos de plaza o solo lo sin registro(4) se recupera todo
		if ((todosTP && todosContribuyente) || estadoPago == 4) {
			contribuciones = new ArrayList<>(
					contribucionDao.findContribucionesPagoPeriodo(fInicial, fFinal, estadosPago, Sort.by(orders)));
		} else if (!todosTP && !todosContribuyente) {
			contribuciones = new ArrayList<>(contribucionDao.findContribucionesPagoPeriodoTPContribuyentes(fInicial,
					fFinal, estadosPago, codigoContribuyente, codigoTP, Sort.by(orders)));
		} else if (!todosTP) {
			contribuciones = new ArrayList<>(contribucionDao.findContribucionesPagoPeriodoTP(fInicial, fFinal,
					estadosPago, codigoTP, Sort.by(orders)));
		} else {
			contribuciones = new ArrayList<>(contribucionDao.findContribucionesPagoPeriodoContribuyentes(fInicial,
					fFinal, estadosPago, codigoContribuyente, Sort.by(orders)));
		}
		//cuadno sea el reporte de pago no tiene caso mostrar los sin registro, en todo caso saca el reporte de contribuciones para ese dia o periodo
		/*if (estadoPago == 0 || estadoPago == 4) {
			// hasta este momento ya se recuperaron las contribucines bajo los filtros dados
			// si la bandera está activada quiere decir que se debe rellenar la información

			contribuciones = poblarSinRegistro(contribuciones, fInicial, fFinal, false, todosTP, todosContribuyente,
					codigoTP, codigoContribuyente, estadoPago == 4);
		}*/
		String recaudadorFilter="Todos";
		if(recaudador!=null && !recaudador.equals("undefined") && !recaudador.isEmpty()){
			contribuciones = contribuciones.stream().filter(con-> con.getCodigoRecaudador().equals(recaudador)).collect(Collectors.toList());
			recaudadorFilter=recaudador;
		}

		dataSource = new JRBeanCollectionDataSource(contribuciones);

		InputStream inputStream = this.getClass().getResourceAsStream(pathReport);

		parametros.put("encabezadoReporte", "MOVIMIENTOS EN EL PERÍODO");
		parametros.put("encabezadoContribuyentes", todosContribuyente ? "Todos" : codigoContribuyente);
		parametros.put("encabezadoTiposPlaza", todosTP ? "Todos" : codigoTP);
		parametros.put("encabezadoEstadoPago", estadoPagoToString(estadoPago));
		parametros.put("encabezadoRecaudador", recaudadorFilter);
		parametros.put("fechaInicial", fInicial);
		parametros.put("fechaFinal", fFinal);
		parametros.put("SUBREPORT_DIR", "reportes/");

		try {
			InputStream imagen = this.getClass().getResourceAsStream("/logos/logo.png");
			parametros.put("logo", imagen);
		} catch (Exception e) {
			Log.error(e.toString());
		}

		try {
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

			JRExporter exporter = null;
			String nameFile = "";
			if (formato.equals(REPORTE_PDF)) {

				exporter = new JRPdfExporter();
				nameFile = "cargos.pdf";
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(nameFile));

				SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
				reportConfig.setSizePageToContent(true);
				reportConfig.setForceLineBreakPolicy(false);

				SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
				exportConfig.setEncrypted(true);
				exportConfig.setAllowedPermissionsHint("PRINTING");

				exporter.setConfiguration(reportConfig);
				exporter.setConfiguration(exportConfig);
				response.setContentType("application/pdf");

			} else if (formato.equals(REPORTE_EXCEL)) {
				exporter = new JRXlsExporter();

				nameFile = "cargos.xls";
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(nameFile));

				SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
				reportConfig.setRemoveEmptySpaceBetweenColumns(true);
				reportConfig.setRemoveEmptySpaceBetweenRows(true);
				reportConfig.setWhitePageBackground(false);
				reportConfig.setDetectCellType(true);

				exporter.setConfiguration(reportConfig);
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			}

			exporter.exportReport();
			Path pathFile = Paths.get(nameFile);
			byte[] archivo = Files.readAllBytes(pathFile);

			response.setHeader("Content-disposition", "attachment; filename=" + nameFile);
			response.setContentLength(archivo.length);

			// streamReport(response, pdf, "gasolineras.pdf");\
			return archivo;

		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}

	}

	private List<JBContribucionReporte> poblarSinRegistro(List<JBContribucionReporte> jbContribuciones,
			Date fechaInicial, Date fechaFinal, boolean contribucionMov, Boolean todosTP, Boolean todosContribuyentes,
			String tipoPlaza, String codigoContribuyente, boolean soloSinRegistro) {
		List<JBContribucionReporte> contribucionesConSinRegistro = new ArrayList<>();
		// 1. traer todas los propietario-plaza activos en el periodo buscado, filtrado
		// por tipo de plaza, contribuyentees
		// 2. recuperar los tipos de plaza, así com las vigencias para cuando sea
		// necesario crear un registro
		List<JBPropietarioPlaza> propietariosPlazaA = new ArrayList<>();
		if (!todosTP && !todosContribuyentes) {
			propietariosPlazaA = propietarioPlazaDao.recuperarPropietarios(fechaInicial, fechaFinal,
					codigoContribuyente, tipoPlaza);
		} else if (!todosTP) {
			propietariosPlazaA = propietarioPlazaDao.recuperarPropietariosTipoPlaza(fechaInicial, fechaFinal,
					tipoPlaza);

		} else if (!todosContribuyentes) {

			propietariosPlazaA = propietarioPlazaDao.recuperarPropietariosContribuyente(fechaInicial, fechaFinal,
					codigoContribuyente);
		} else {

			propietariosPlazaA = propietarioPlazaDao.recuperarPropietarios(fechaInicial, fechaFinal);
		}

		List<TipoPlazaVigencia> vigenciasTipoPlaza = tipoPlazaVigenciaDao.findAll();
		/**
		 * 0. hacemos elproceso para cada propietario laza 1.recorremos todas las fechas
		 * con el while ya definido 2.buscamos 2. buscamos en las contribuciones is
		 * existe algo en el día dado 3. crear el registro virtual de no exitir
		 */
		Calendar cal = Calendar.getInstance();
		for (JBPropietarioPlaza propPlaza : propietariosPlazaA) {
			cal.setTime(fechaInicial);
			while (cal.getTime().compareTo(fechaFinal) <= 0) {
				// revisa por el dia en curso si la plaza dada tiene registro de contribucion,
				// si tiene lo agrega y si no crea uno
				JBContribucionReporte jbA = existeContribucion(propPlaza, jbContribuciones, vigenciasTipoPlaza,
						cal.getTime(), contribucionMov);
				if (soloSinRegistro) {
					if (jbA.getIdContribucion() == -1)
						contribucionesConSinRegistro.add(jbA);
				} else {
					contribucionesConSinRegistro.add(jbA);
				}

				cal.add(Calendar.DAY_OF_MONTH, 1);
			}
		}
		
		Collections.sort(contribucionesConSinRegistro, new Comparator<JBContribucionReporte>() {

			@Override
			public int compare(JBContribucionReporte o1, JBContribucionReporte o2) {
				if(o1.getFechaContribucion().compareTo(o2.getFechaContribucion())==0) {
				  	return o1.getCodigoContribuyente().compareTo(o2.getCodigoContribuyente());
				}
				
				return o1.getFechaContribucion().compareTo(o2.getFechaContribucion());
			}
			
		});
/*
		Collections.sort(contribucionesConSinRegistro, new Comparator<JBContribucionReporte>() {

			@Override
			public int compare(JBContribucionReporte o1, JBContribucionReporte o2) {
				
				try {
			    if(o1.getCodigoContribuyente()==null || o2.getCodigoContribuyente()==null || o1.getFechaContribucion()==null || o2.getFechaContribucion()==null) {
			    	System.out.println(o1.getCodigoContribuyente()+"  "+o2.getCodigoContribuyente());
			    }
				if (o1.getFechaContribucion().equals(o2.getFechaContribucion())) {
					return o1.getCodigoContribuyente().compareTo(o2.getCodigoContribuyente());
				} else {
					return o1.getFechaContribucion().compareTo(o2.getFechaContribucion());
				}
				}
				catch(Exception e) {
					System.out.println(o1.getCodigoContribuyente()+" "+o1.getFechaContribucion().toGMTString()+" "+o2.getCodigoContribuyente()+o2.getFechaContribucion().toGMTString());
					return -1;
				}

			}

		});*/
		return contribucionesConSinRegistro;
	}
	
	

	private JBContribucionReporte existeContribucion(JBPropietarioPlaza propietarioPlaza,
			List<JBContribucionReporte> contribuciones, List<TipoPlazaVigencia> vigencias, Date fechaA,
			boolean contribucionMov) {
		String[] desCon = propietarioPlaza.getContribuyente().split(" ", 2);
		String codigoC = desCon.length > 0 ? desCon[0] : " ";
		for (JBContribucionReporte contribucion : contribuciones) {
			// revisamos si se tiene que comparar con la fecha de contribución o se tiene
			// que revisar con la fecha de modificación
			
			if (contribucionMov) {
				
				if (fechaA.equals(contribucion.getFechaContribucion())
						&& codigoC.equals(contribucion.getCodigoContribuyente())
						&& propietarioPlaza.getPlaza().equals(contribucion.getCodigoPlaza())
						&& propietarioPlaza.getTipoPlaza().equals(contribucion.getCodigoTipoPlaza())) {
					return contribucion;
				}
			} else {
				if (DateUtils.isSameDay(fechaA, contribucion.getFechaModificacion())
						&& codigoC.equals(contribucion.getCodigoContribuyente())
						&& propietarioPlaza.getPlaza().equals(contribucion.getCodigoPlaza())
						&& propietarioPlaza.getTipoPlaza().equals(contribucion.getCodigoTipoPlaza())) {
					return contribucion;
				}
			}
		}
		// si no existe registro, se va a crear uno

		String nombreC = desCon.length > 1 ? desCon[1] : " ";
		Double importe = 0.0;
		if (propietarioPlaza.getImporte() != null) {
			importe = propietarioPlaza.getImporte();
		} else {
			TipoPlazaVigencia tpv = recuperaSiExisteVigente(vigencias, propietarioPlaza.getTipoPlaza(), fechaA);
			if (tpv != null) {
				importe = tpv.getImporte();
			}
		}

		JBContribucionReporte jb = new JBContribucionReporte(-1, "SR", codigoC, nombreC, "SR",
				propietarioPlaza.getPlaza(), fechaA, null, importe, 4, "SR", propietarioPlaza.getTipoPlaza());
		return jb;
	}

	private TipoPlazaVigencia recuperaSiExisteVigente(List<TipoPlazaVigencia> vigencias, String tipoPlaza,
			Date fechaA) {
		for (TipoPlazaVigencia vig : vigencias) {
			if (vig.getTipoPlaza().equals(tipoPlaza) && fechaA.compareTo(vig.getVigenciaInicial()) >= 0
					&& fechaA.compareTo(vig.getVigenciaFinal()) <= 0) {
				return vig;
			}
		}

		return null;
	}

	private String estadoPagoToString(Integer estado) {
		if (estado.equals(0)) {
			return "TODOS";
		}
		if (estado.equals(1)) {
			return "PAGADOS";
		}
		if (estado.equals(2)) {
			return "PENDIENTES";
		}
		if (estado.equals(3)) {
			return "AUSENTES";
		}
		return "SIN REGISTRO";

	}

	@RequestMapping(value = "/contribuyentesreporte", method = RequestMethod.GET)
	@CrossOrigin(origins = "http://localhost:" + port)
	public @ResponseBody byte[] generateReportContribuyentes(HttpServletResponse response,
			@RequestParam Integer formato, @RequestParam Boolean todosTP, @RequestParam String codigoTP)
			throws Exception {

		JRBeanCollectionDataSource dataSource = null;
		String pathReport = "/reportes/contribuyentes_reporte.jrxml";
		List<String> fieldOrders = new ArrayList<>();
		List<Order> orders = new ArrayList<>();

		orders.add(Order.asc("contribuyente.codigoContribuyente"));

		List<JBContribuyente> contribuyentes;
		if (todosTP) {
			contribuyentes = new ArrayList<>(contribucionDao.findTodosConribuyentes(Sort.by(orders)));
		} else
			contribuyentes = new ArrayList<>(contribucionDao.findTPConribuyentes(codigoTP, Sort.by(orders)));

		dataSource = new JRBeanCollectionDataSource(contribuyentes);

		InputStream inputStream = this.getClass().getResourceAsStream(pathReport);
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("SUBREPORT_DIR", "reportes/");

		try {
			InputStream imagen = this.getClass().getResourceAsStream("/logos/logo.png");
			parametros.put("logo", imagen);
		} catch (Exception e) {
			Log.error(e.toString());
		}

		try {
			JasperReport jasperReport = JasperCompileManager.compileReport(inputStream);
			JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parametros, dataSource);

			JRExporter exporter = null;
			String nameFile = "";
			if (formato.equals(REPORTE_PDF)) {

				exporter = new JRPdfExporter();
				nameFile = "contribuyentes.pdf";
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(nameFile));

				SimplePdfReportConfiguration reportConfig = new SimplePdfReportConfiguration();
				reportConfig.setSizePageToContent(true);
				reportConfig.setForceLineBreakPolicy(false);

				SimplePdfExporterConfiguration exportConfig = new SimplePdfExporterConfiguration();
				exportConfig.setEncrypted(true);
				exportConfig.setAllowedPermissionsHint("PRINTING");

				exporter.setConfiguration(reportConfig);
				exporter.setConfiguration(exportConfig);
				response.setContentType("application/pdf");

			} else if (formato.equals(REPORTE_EXCEL)) {
				exporter = new JRXlsExporter();

				nameFile = "contribuyentes.xls";
				exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
				exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(nameFile));

				SimpleXlsxReportConfiguration reportConfig = new SimpleXlsxReportConfiguration();
				reportConfig.setRemoveEmptySpaceBetweenColumns(true);
				reportConfig.setRemoveEmptySpaceBetweenRows(true);
				reportConfig.setWhitePageBackground(false);
				reportConfig.setDetectCellType(true);

				exporter.setConfiguration(reportConfig);
				response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
			}

			exporter.exportReport();
			Path pathFile = Paths.get(nameFile);
			byte[] archivo = Files.readAllBytes(pathFile);

			response.setHeader("Content-disposition", "attachment; filename=" + nameFile);
			response.setContentLength(archivo.length);

			// streamReport(response, pdf, "gasolineras.pdf");\
			return archivo;

		} catch (Exception e) {
			System.out.println(e.toString());
			return null;
		}

	}

	private Date getVigenciaFinalFromVINew(Date vigenciaInicialNueva) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(vigenciaInicialNueva);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();

	}

	private Date getFechaFinal() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2099, 0, 1);
		return calendar.getTime();
	}

	private SimpleDateFormat getSdf() {
		if (spdf == null) {
			spdf = new SimpleDateFormat("yyyy-MM-dd");
		}
		return spdf;
	}

	private GeneradorCodigos getGeneradorCodigos() {
		if (generadorCodigos == null) {
			generadorCodigos = new GeneradorCodigos();
		}
		return generadorCodigos;
	}

	/**
	 * Se especifica de forma manual el tipo de plaza. La base son 4 columnas,
	 * nombre, apePaterno, apeMaterno, giro. Si es una plaza que requiere
	 * contribuciones hay que agregar la 5 celda y modificar manualmente
	 * 
	 * @param reapExcelDataFile
	 * @throws IOException
	 */
	/*
	 * @GetMapping("/UploadFromXLS") public String UploadFromXLS(@RequestParam(name
	 * = "name", required = false, defaultValue = "World") String name) {
	 * 
	 * System.out.println("nombre " + name); System.out.println("aaaaaaaa");
	 * 
	 * List<PropietarioPlaza> propietarios = new ArrayList<>(); String path =
	 * "/padron_ambulante.xls"; InputStream is =
	 * this.getClass().getResourceAsStream(path); HSSFWorkbook workbook; try {
	 * workbook = new HSSFWorkbook(is); HSSFSheet worksheet =
	 * workbook.getSheetAt(0); for (int i = 1; i <
	 * worksheet.getPhysicalNumberOfRows(); i++) { System.out.println("rows" + i);
	 * System.out.println("rows2 " + worksheet.getLastRowNum()); Contribuyente con =
	 * new Contribuyente(); Integer NOMBRE_POS = 0; Integer APEPATERNO_POS = 1;
	 * Integer APEMATERNO_POS = 2; Integer GIRO = 3; Integer IMPORTE_POS = 4;
	 * 
	 * HSSFRow row = worksheet.getRow(i); // variable para indicar si el nombr está
	 * dividido en 3 boolean dividido = false;
	 * 
	 * if (dividido) {
	 * 
	 * con.setNombre(row.getCell(NOMBRE_POS).getStringCellValue());
	 * con.setApePaterno(row.getCell(APEPATERNO_POS).getStringCellValue());
	 * con.setApeMaterno(row.getCell(APEPATERNO_POS).getStringCellValue()); } else {
	 * GIRO = 1; IMPORTE_POS = 2; String[] nombreA =
	 * row.getCell(NOMBRE_POS).getStringCellValue().split(" "); int separador =
	 * nombreA.length; if (separador > 3) { con.setApePaterno(nombreA[separador -
	 * 2]); con.setApeMaterno(nombreA[separador - 1]); String nombre = ""; for (int
	 * j = 0; j < separador - 2; j++) { nombre += nombreA[j] + " "; }
	 * con.setNombre(nombre.substring(0, nombre.length() - 1));
	 * 
	 * } else { con.setNombre(nombreA[0]); if(separador>1) {
	 * con.setApePaterno(nombreA[1]); } else { con.setApePaterno(""); }
	 * if(separador>2) { con.setApeMaterno(nombreA[2] != null ? nombreA[2] : ""); }
	 * else { con.setApePaterno(""); } } } if (con.getNombre() != null &&
	 * !con.getNombre().isEmpty() && con.getApePaterno() != null &&
	 * !con.getApePaterno().isEmpty() && con.getApeMaterno() != null &&
	 * !con.getApeMaterno().isEmpty()) {
	 * 
	 * // vamos a dar de alta los tianguis locatarios // ser muy cuidadoso con el
	 * código de la plaza TipoPlaza tipoPlaza = new TipoPlaza();
	 * tipoPlaza.setCodigoTipoPlaza("AMBULANTE");
	 * 
	 * PropietarioPlaza propietarioPlaza = new PropietarioPlaza();
	 * propietarioPlaza.setGiroDescripcion(row.getCell(GIRO).getStringCellValue());
	 * propietarioPlaza.setTipoPlaza(tipoPlaza); // esto solo es cuando no es un
	 * importe general boolean importePorPlaza =false; if (importePorPlaza) {
	 * propietarioPlaza.setImporte(row.getCell(IMPORTE_POS).getNumericCellValue());
	 * }
	 * 
	 * // recuperar el número que le toca a la plaza Folio folio = null;
	 * Optional<Folio> optionalfolio = folioDao
	 * .findById(propietarioPlaza.getTipoPlaza().getCodigoTipoPlaza()); if
	 * (optionalfolio.isPresent()) { folio = optionalfolio.get();
	 * folio.setFolio(folio.getFolio() + 1);
	 * 
	 * } else { folio = new Folio();
	 * folio.setCodigoFolio(propietarioPlaza.getTipoPlaza().getCodigoTipoPlaza());
	 * 
	 * folio.setFolio(1); } folioDao.save(folio);
	 * 
	 * // Plaza Plaza plaza = new Plaza();
	 * plaza.setCodigoPlaza(propietarioPlaza.getTipoPlaza().getCodigoTipoPlaza() +
	 * "_" + String.format("%03d", folio.getFolio())); plazaDao.save(plaza);
	 * 
	 * propietarioPlaza.setPlaza(plaza); // REVISAR
	 * propietarioPlaza.setVigenciaInicial(new Date());
	 * propietarioPlaza.setVigenciaFinal(getFechaFinal());
	 * 
	 * // Folio propietarioPlaza Folio folioPP = null; Optional<Folio>
	 * optionalfolioPP = folioDao.findById("PP"); if (optionalfolioPP.isPresent()) {
	 * folioPP = optionalfolioPP.get(); folioPP.setFolio(folioPP.getFolio() + 1);
	 * 
	 * } else { folioPP = new Folio(); folioPP.setCodigoFolio("PP");
	 * folioPP.setFolio(1); } folioDao.save(folioPP);
	 * propietarioPlaza.setIdPropietarioPlaza(folioPP.getFolio().toString());
	 * Contribuyente conFinal = crearContribuyente(con);
	 * 
	 * contribuyenteDao.save(conFinal); propietarioPlaza.setContribuyente(conFinal);
	 * propietarioPlazaDao.save(propietarioPlaza);
	 * 
	 * } else { System.out.print("nombreAAAAAAAAAAAAAAAAAAAAAAAAAAA" +
	 * con.getNombre()!=null?con.getNombre(): "no se puede recuperar nada");
	 * System.out.print("nombre AAAAAAAAAAAAAAAAAAAAAAAAAAA" );
	 * 
	 * }
	 * 
	 * }
	 * 
	 * } catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }
	 * 
	 * return "UploadFromXLS"; }
	 * 
	 */

	/*
	 * este es le metodo inicial para generar el reporte, sin embargo este reporte
	 * no generaba los sin registro
	 * 
	 * @RequestMapping(value = "/contribucionespagoreporte", method =
	 * RequestMethod.GET)
	 * 
	 * @CrossOrigin(origins = "http://localhost:" + port) public @ResponseBody
	 * byte[] generateReportPago(HttpServletResponse response, @RequestParam String
	 * fechaInicial,
	 * 
	 * @RequestParam String fechaFinal, @RequestParam Integer formato, @RequestParam
	 * Boolean todosTP,
	 * 
	 * @RequestParam Boolean todosContribuyente, @RequestParam String
	 * codigoContribuyente,
	 * 
	 * @RequestParam String codigoTP, @RequestParam Integer estadoPago) throws
	 * Exception {
	 * 
	 * Map<String, Object> parametros = new HashMap<>(); SimpleDateFormat sp = new
	 * SimpleDateFormat("dd-MM-yyyy"); Date fInicial = sp.parse(fechaInicial); Date
	 * fFinal = sp.parse(fechaFinal); JRBeanCollectionDataSource dataSource = null;
	 * String pathReport = "/reportes/contribuciones_reporte.jrxml"; List<String>
	 * fieldOrders = new ArrayList<>(); List<Order> orders = new ArrayList<>();
	 * 
	 * orders.add(Order.asc("idContribucion")); List<Integer> estadosPago = new
	 * ArrayList<>(); if (estadoPago.equals(0)) { estadosPago.add(0);
	 * estadosPago.add(1); estadosPago.add(2); estadosPago.add(3); } else {
	 * estadosPago.add(estadoPago); }
	 * 
	 * List<JBContribucionReporte> contribuciones; if (todosTP &&
	 * todosContribuyente) { contribuciones = new ArrayList<>(
	 * contribucionDao.findContribucionesPagoPeriodo(fInicial, fFinal, estadosPago,
	 * Sort.by(orders))); } else if (!todosTP && !todosContribuyente) {
	 * contribuciones = new
	 * ArrayList<>(contribucionDao.findContribucionesPagoPeriodoTPContribuyentes(
	 * fInicial, fFinal, estadosPago, codigoContribuyente, codigoTP,
	 * Sort.by(orders))); } else if (!todosTP) { contribuciones = new
	 * ArrayList<>(contribucionDao.findContribucionesPagoPeriodoTP(fInicial, fFinal,
	 * estadosPago, codigoTP, Sort.by(orders))); } else { contribuciones = new
	 * ArrayList<>(contribucionDao.findContribucionesPagoPeriodoContribuyentes(
	 * fInicial, fFinal, estadosPago, codigoContribuyente, Sort.by(orders))); }
	 * 
	 * 
	 * dataSource = new JRBeanCollectionDataSource(contribuciones);
	 * 
	 * InputStream inputStream = this.getClass().getResourceAsStream(pathReport);
	 * 
	 * parametros.put("encabezadoReporte", "MOVIMIENTOS EN EL PERÍODO");
	 * parametros.put("encabezadoContribuyentes", todosContribuyente ? "Todos" :
	 * codigoContribuyente); parametros.put("encabezadoTiposPlaza", todosTP ?
	 * "Todos" : codigoTP); parametros.put("encabezadoEstadoPago",
	 * estadoPagoToString(estadoPago)); parametros.put("fechaInicial", fInicial);
	 * parametros.put("fechaFinal", fFinal); parametros.put("SUBREPORT_DIR",
	 * "reportes/");
	 * 
	 * try { InputStream imagen =
	 * this.getClass().getResourceAsStream("/logos/logo.png");
	 * parametros.put("logo", imagen); } catch (Exception e) {
	 * Log.error(e.toString()); }
	 * 
	 * try { JasperReport jasperReport =
	 * JasperCompileManager.compileReport(inputStream); JasperPrint jasperPrint =
	 * JasperFillManager.fillReport(jasperReport, parametros, dataSource);
	 * 
	 * JRExporter exporter = null; String nameFile = ""; if
	 * (formato.equals(REPORTE_PDF)) {
	 * 
	 * exporter = new JRPdfExporter(); nameFile = "cargos.pdf";
	 * exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
	 * exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(nameFile));
	 * 
	 * SimplePdfReportConfiguration reportConfig = new
	 * SimplePdfReportConfiguration(); reportConfig.setSizePageToContent(true);
	 * reportConfig.setForceLineBreakPolicy(false);
	 * 
	 * SimplePdfExporterConfiguration exportConfig = new
	 * SimplePdfExporterConfiguration(); exportConfig.setEncrypted(true);
	 * exportConfig.setAllowedPermissionsHint("PRINTING");
	 * 
	 * exporter.setConfiguration(reportConfig);
	 * exporter.setConfiguration(exportConfig);
	 * response.setContentType("application/pdf");
	 * 
	 * } else if (formato.equals(REPORTE_EXCEL)) { exporter = new JRXlsExporter();
	 * 
	 * nameFile = "cargos.xls"; exporter.setExporterInput(new
	 * SimpleExporterInput(jasperPrint)); exporter.setExporterOutput(new
	 * SimpleOutputStreamExporterOutput(nameFile));
	 * 
	 * SimpleXlsxReportConfiguration reportConfig = new
	 * SimpleXlsxReportConfiguration();
	 * reportConfig.setRemoveEmptySpaceBetweenColumns(true);
	 * reportConfig.setRemoveEmptySpaceBetweenRows(true);
	 * reportConfig.setWhitePageBackground(false);
	 * reportConfig.setDetectCellType(true);
	 * 
	 * exporter.setConfiguration(reportConfig); response.setContentType(
	 * "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"); }
	 * 
	 * exporter.exportReport(); Path pathFile = Paths.get(nameFile); byte[] archivo
	 * = Files.readAllBytes(pathFile);
	 * 
	 * response.setHeader("Content-disposition", "attachment; filename=" +
	 * nameFile); response.setContentLength(archivo.length);
	 * 
	 * // streamReport(response, pdf, "gasolineras.pdf");\ return archivo;
	 * 
	 * } catch (Exception e) { System.out.println(e.toString()); return null; }
	 */

}
