package com.cobranzaplazas.cobro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.cobranzaplazas.model.JBAdeudo;
import com.cobranzaplazas.repo.CobroMensualDia;
import com.cobranzaplazas.repo.CobroMensualDiaDao;
import com.cobranzaplazas.repo.CobroPeriodoAnualDao;
import com.cobranzaplazas.repo.ConfiguracionCobro;
import com.cobranzaplazas.repo.ConfiguracionCobroDao;
import com.cobranzaplazas.repo.ContribucionDao;
import com.cobranzaplazas.repo.Contribuyente;
import com.cobranzaplazas.repo.Plaza;
import com.cobranzaplazas.repo.PropietarioPlaza;
import com.cobranzaplazas.repo.PropietarioPlazaDao;
import com.cobranzaplazas.repo.TipoPlaza;
import com.cobranzaplazas.repo.TipoPlazaVigencia;
import com.cobranzaplazas.repo.TipoPlazaVigenciaDao;

class CalculoCobroServiceTest {
	private CalculoCobroService service;
	private TipoPlaza tipoPlaza;
	private Plaza plaza;
	private final List<PropietarioPlaza> propietarios = new ArrayList<>();
	private final List<TipoPlazaVigencia> vigencias = new ArrayList<>();
	private final List<Object[]> pagadas = new ArrayList<>();

	private static java.util.Date d(String fecha) {
		return CalculoCobroService.aDate(LocalDate.parse(fecha));
	}

	@BeforeEach
	void preparar() {
		tipoPlaza = new TipoPlaza();
		tipoPlaza.setCodigoTipoPlaza("TP");
		tipoPlaza.setPorImporteGlobalTipoPlaza(true);
		plaza = new Plaza();
		plaza.setCodigoPlaza("P1");

		// Mensual, el día 10 de cada mes, desde enero de 2026
		ConfiguracionCobro config = new ConfiguracionCobro();
		config.setTipoPlaza(tipoPlaza);
		config.setTipoCobro(CalendarioCobro.MENSUAL);
		config.setFechaInicioCobro(d("2026-01-01"));
		List<CobroMensualDia> dias = new ArrayList<>();
		for (int mes = 1; mes <= 12; mes++) {
			CobroMensualDia dia = new CobroMensualDia();
			dia.setMes(mes);
			dia.setDia(10);
			dia.setUltimoDia(false);
			dias.add(dia);
		}

		service = new CalculoCobroService();
		service.configuracionCobroDao = mock(ConfiguracionCobroDao.class);
		service.cobroMensualDiaDao = mock(CobroMensualDiaDao.class);
		service.cobroPeriodoAnualDao = mock(CobroPeriodoAnualDao.class);
		service.propietarioPlazaDao = mock(PropietarioPlazaDao.class);
		service.contribucionDao = mock(ContribucionDao.class);
		service.tipoPlazaVigenciaDao = mock(TipoPlazaVigenciaDao.class);
		when(service.configuracionCobroDao.findAll()).thenReturn(List.of(config));
		when(service.cobroMensualDiaDao.porTipoPlaza("TP")).thenReturn(dias);
		when(service.cobroPeriodoAnualDao.porTipoPlaza("TP")).thenReturn(List.of());
		when(service.propietarioPlazaDao.findAll()).thenReturn(propietarios);
		when(service.tipoPlazaVigenciaDao.findAll()).thenReturn(vigencias);
		when(service.contribucionDao.findFechasPagadas(any())).thenReturn(pagadas);
		when(service.contribucionDao.findPagosAdeudoAnterior(any())).thenReturn(List.of());
	}

	private void vigencia(String inicio, String fin, double importe, double descuento) {
		TipoPlazaVigencia v = new TipoPlazaVigencia();
		v.setTipoPlaza(tipoPlaza);
		v.setVigenciaInicial(d(inicio));
		v.setVigenciaFinal(d(fin));
		v.setImporte(importe);
		v.setPrecioDescuento(descuento);
		vigencias.add(v);
	}

	private void propietario(String id, String contribuyente, String inicio, String fin) {
		Contribuyente c = new Contribuyente();
		c.setCodigoContribuyente(contribuyente);
		PropietarioPlaza pp = new PropietarioPlaza();
		pp.setIdPropietarioPlaza(id);
		pp.setPlaza(plaza);
		pp.setTipoPlaza(tipoPlaza);
		pp.setContribuyente(c);
		pp.setVigenciaInicial(d(inicio));
		pp.setVigenciaFinal(d(fin));
		propietarios.add(pp);
	}

	private List<JBAdeudo> adeudosDe(List<JBAdeudo> adeudos, String contribuyente) {
		return adeudos.stream().filter(a -> contribuyente.equals(a.getCodigoContribuyente())).toList();
	}

	@Test
	void valuaCadaObligacionConLaVigenciaDeSuFechaYRespetaLaVigenciaDelPropietario() {
		vigencia("2026-01-01", "2026-03-31", 100, 80);
		vigencia("2026-04-01", "2099-12-31", 150, 120);
		propietario("PP-A", "A", "2026-01-01", "2026-02-28");
		propietario("PP-B", "B", "2026-03-01", "2099-12-31");
		pagadas.add(new Object[] { "P1", "TP", d("2026-03-10") });

		List<JBAdeudo> adeudos = service.calcularAdeudos(null, null, LocalDate.parse("2026-05-15"));

		// El propietario anterior conserva lo que dejó vencido, al precio de su vigencia
		List<JBAdeudo> deA = adeudosDe(adeudos, "A");
		assertEquals(List.of(d("2026-01-10"), d("2026-02-10")), deA.stream().map(JBAdeudo::getFechaVencimiento).toList());
		assertTrue(deA.stream().allMatch(a -> a.getMonto() == 100.0 && "PP-A".equals(a.getIdPropietarioPlaza())));
		// Marzo está pagado; abril y mayo ya toman el precio de la nueva vigencia
		List<JBAdeudo> deB = adeudosDe(adeudos, "B");
		assertEquals(List.of(d("2026-04-10"), d("2026-05-10")), deB.stream().map(JBAdeudo::getFechaVencimiento).toList());
		assertTrue(deB.stream().allMatch(a -> a.getMonto() == 150.0));
	}

	@Test
	void sinVigenciaDelTipoNiImporteGlobalNoGeneraObligaciones() {
		vigencia("2026-03-01", "2099-12-31", 100, 80);
		propietario("PP-A", "A", "2026-01-01", "2099-12-31");
		// Enero y febrero no tienen precio vigente
		assertEquals(List.of(d("2026-03-10")), service.calcularAdeudos(null, null, LocalDate.parse("2026-03-15"))
				.stream().map(JBAdeudo::getFechaVencimiento).toList());

		tipoPlaza.setPorImporteGlobalTipoPlaza(false);
		assertTrue(service.calcularAdeudos(null, null, LocalDate.parse("2026-03-15")).isEmpty());
	}

	@Test
	void elCobroMensualSeCubreConUnPagoEnCualquierFechaDelMes() {
		// Además del 10, se cobra el 25 de cada mes
		List<CobroMensualDia> dias = new ArrayList<>(service.cobroMensualDiaDao.porTipoPlaza("TP"));
		for (int mes = 1; mes <= 12; mes++) {
			CobroMensualDia dia = new CobroMensualDia();
			dia.setMes(mes);
			dia.setDia(25);
			dia.setUltimoDia(false);
			dias.add(dia);
		}
		when(service.cobroMensualDiaDao.porTipoPlaza("TP")).thenReturn(dias);
		vigencia("2026-01-01", "2099-12-31", 100, 80);
		propietario("PP-A", "A", "2026-01-01", "2099-12-31");
		// Enero pagado el 10, febrero pagado el 25; marzo sin pago
		pagadas.add(new Object[] { "P1", "TP", d("2026-01-10") });
		pagadas.add(new Object[] { "P1", "TP", d("2026-02-25") });

		// Al 20 de marzo marzo aún no vence: le queda el 25
		assertTrue(service.calcularAdeudos(null, null, LocalDate.parse("2026-03-20")).isEmpty());
		// Al 26 de marzo se adeuda un solo mes, al precio normal, con vencimiento en su última fecha
		List<JBAdeudo> adeudos = service.calcularAdeudos(null, null, LocalDate.parse("2026-03-26"));
		assertEquals(List.of(d("2026-03-25")), adeudos.stream().map(JBAdeudo::getFechaVencimiento).toList());
		assertEquals(100.0, adeudos.get(0).getMonto());
	}

	@Test
	void conCorteEnElPasadoSoloCuentanLosPagosCapturadosHastaEseDia() {
		propietario("PP-A", "A", "2026-01-01", "2099-12-31");
		service.calcularAdeudos(null, null, LocalDate.parse("2026-03-15"));
		// el límite es el inicio del día siguiente al corte
		verify(service.contribucionDao).findFechasPagadas(d("2026-03-16"));
		verify(service.contribucionDao).findPagosAdeudoAnterior(d("2026-03-16"));

		service.calcularAdeudos(null, null, LocalDate.now());
		verify(service.contribucionDao).findFechasPagadas(null);
	}
}
