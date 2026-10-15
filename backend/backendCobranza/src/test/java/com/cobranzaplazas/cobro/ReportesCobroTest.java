package com.cobranzaplazas.cobro;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.cobranzaplazas.model.JBAdeudo;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

/** Las plantillas de morosidad y requerimientos compilan y se llenan con los campos de JBAdeudo. */
class ReportesCobroTest {

	@ParameterizedTest
	@ValueSource(strings = { "/reportes/morosidad_reporte.jrxml", "/reportes/requerimiento_pago_reporte.jrxml" })
	void generaPdf(String plantilla) throws Exception {
		List<JBAdeudo> adeudos = List.of(
				new JBAdeudo("PP1", "P1", "TP", "C1", "Nombre", "Giro", "Sí", "MENSUAL", JBAdeudo.CONCEPTO_OBLIGACION,
						"enero 2026 (día 10)", new Date(), 100.0),
				new JBAdeudo("PP2", "P2", "TP", "C2", "Otro", "Giro", "No", "MENSUAL", JBAdeudo.CONCEPTO_OBLIGACION,
						"enero 2026 (día 10)", new Date(), 150.0));
		Map<String, Object> parametros = new HashMap<>();
		parametros.put("encabezadoTiposPlaza", "Todos");
		parametros.put("observaciones", "");
		try (InputStream fuente = getClass().getResourceAsStream(plantilla);
				InputStream logo = getClass().getResourceAsStream("/logos/logo.png")) {
			parametros.put("logo", logo);
			JasperPrint print = JasperFillManager.fillReport(JasperCompileManager.compileReport(fuente), parametros,
					new JRBeanCollectionDataSource(adeudos));
			if (plantilla.contains("requerimiento")) {
				// una hoja por plaza
				assertEquals(2, print.getPages().size());
			}
			assertTrue(JasperExportManager.exportReportToPdf(print).length > 0);
		}
	}
}
