package com.cobranzaplazas.cobro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.cobranzaplazas.cobro.CalendarioCobro.DiaMes;
import com.cobranzaplazas.cobro.CalendarioCobro.Obligacion;
import com.cobranzaplazas.cobro.CalendarioCobro.Periodo;

class CalendarioCobroTest {

	private List<LocalDate> fechas(CalendarioCobro calendario, String desde, String hasta) {
		return calendario.obligaciones(LocalDate.parse(desde), LocalDate.parse(hasta)).stream().map(Obligacion::fecha)
				.collect(Collectors.toList());
	}

	@Test
	void diarioSoloGeneraLosDiasConfigurados() {
		// 2026-10-05 es lunes; cobro lunes, miércoles y viernes
		CalendarioCobro calendario = new CalendarioCobro(CalendarioCobro.DIARIO, List.of(1, 3, 5), null, null, null,
				null, null, null);
		assertEquals(List.of(LocalDate.parse("2026-10-05"), LocalDate.parse("2026-10-07"),
				LocalDate.parse("2026-10-09"), LocalDate.parse("2026-10-12")),
				fechas(calendario, "2026-10-05", "2026-10-13"));
	}

	@Test
	void mensualAdmiteVariasFechasMesesSinCobroYUltimoDia() {
		List<DiaMes> dias = List.of(new DiaMes(1, 5, false), new DiaMes(1, 20, false), new DiaMes(2, null, true),
				new DiaMes(4, 31, false));
		CalendarioCobro calendario = new CalendarioCobro(CalendarioCobro.MENSUAL, null, dias, null, null, null, null,
				null);
		// una obligación por mes que vence en su última fecha; marzo no tiene cobro; febrero toma su
		// último día; el 31 de abril se ajusta al 30
		assertEquals(List.of(LocalDate.parse("2026-01-20"), LocalDate.parse("2026-02-28"), LocalDate.parse("2026-04-30")),
				fechas(calendario, "2026-01-01", "2026-12-31"));
		Obligacion enero = calendario.obligaciones(LocalDate.parse("2026-01-01"), LocalDate.parse("2026-01-31")).get(0);
		assertEquals(LocalDate.parse("2026-01-05"), enero.inicioPeriodo());
		assertEquals("enero 2026 (días 5, 20)", enero.periodo());
		// el mes no se genera mientras no llegue su última fecha
		assertTrue(fechas(calendario, "2026-01-01", "2026-01-19").isEmpty());
		assertEquals(List.of(LocalDate.parse("2028-02-29")), fechas(calendario, "2028-02-01", "2028-02-29"));
	}

	@Test
	void mensualUsaLosDiasPropiosDelAnioEnLugarDeLosGenerales() {
		List<DiaMes> dias = List.of(new DiaMes(1, 10, false), new DiaMes(2, 10, false),
				new DiaMes(2027, 3, 15, false), new DiaMes(2027, 2, null, true));
		CalendarioCobro calendario = new CalendarioCobro(CalendarioCobro.MENSUAL, null, dias, null, null, null, null,
				null);
		assertEquals(List.of(LocalDate.parse("2026-01-10"), LocalDate.parse("2026-02-10")),
				fechas(calendario, "2026-01-01", "2026-12-31"));
		// 2027 tiene sus propias fechas: no cobra el 10 de enero ni el 10 de febrero
		assertEquals(List.of(LocalDate.parse("2027-02-28"), LocalDate.parse("2027-03-15")),
				fechas(calendario, "2027-01-01", "2027-12-31"));
		assertEquals(List.of(LocalDate.parse("2028-01-10"), LocalDate.parse("2028-02-10")),
				fechas(calendario, "2028-01-01", "2028-12-31"));
	}

	@Test
	void anualVenceAlFinDelPeriodoYRespetaElPeriodoEspecificoDelAnio() {
		Map<Integer, Periodo> especificos = Map.of(2027,
				new Periodo(LocalDate.parse("2027-07-01"), LocalDate.parse("2027-08-31")));
		CalendarioCobro calendario = new CalendarioCobro(CalendarioCobro.ANUAL, null, null, especificos, 1, 1, 31, 3);
		List<Obligacion> obligaciones = calendario.obligaciones(LocalDate.parse("2026-01-01"),
				LocalDate.parse("2027-12-31"));
		assertEquals(2, obligaciones.size());
		assertEquals(LocalDate.parse("2026-03-31"), obligaciones.get(0).fecha());
		assertEquals(LocalDate.parse("2026-01-01"), obligaciones.get(0).inicioPeriodo());
		assertEquals(LocalDate.parse("2027-08-31"), obligaciones.get(1).fecha());
		// el período aún no termina: no hay obligación vencida todavía
		assertTrue(fechas(calendario, "2026-01-01", "2026-03-30").isEmpty());
	}

	@Test
	void validaDiasSegunElMes() {
		assertTrue(CalendarioCobro.diaValido(2, 29));
		assertFalse(CalendarioCobro.diaValido(2, 30));
		assertFalse(CalendarioCobro.diaValido(4, 31));
		assertTrue(CalendarioCobro.diaValido(12, 31));
		assertFalse(CalendarioCobro.diaValido(13, 1));
	}
}
