package com.jalpa.cobranza;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import com.jalpa.cobranza.model.entidades.ConfiguracionCobro;
import com.jalpa.cobranza.model.entidades.TipoPlazaHistorial;

import org.junit.Test;

import java.time.LocalDate;
import java.util.Date;

public class ConfiguracionCobroTest {

    private Date d(String fecha){
        return ConfiguracionCobro.aDate(LocalDate.parse(fecha));
    }

    private ConfiguracionCobro config(String tipoCobro){
        ConfiguracionCobro config= new ConfiguracionCobro();
        config.setCodigoTipoPlazaCobro("TP");
        config.setTipoCobro(tipoCobro);
        config.setFechaInicioCobro(d("2026-01-01"));
        return config;
    }

    private TipoPlazaHistorial vigencia(Double precioDescuento){
        TipoPlazaHistorial vigencia= new TipoPlazaHistorial();
        vigencia.setImporte(1000.0);
        vigencia.setPrecioDescuento(precioDescuento);
        return vigencia;
    }

    @Test
    public void anualSoloDaDescuentoDentroDelPeriodoDePago() {
        ConfiguracionCobro config= config(ConfiguracionCobro.ANUAL);
        TipoPlazaHistorial v= vigencia(800.0);
        config.setPeriodoInicioDia(1);
        config.setPeriodoInicioMes(2);
        config.setPeriodoFinDia(31);
        config.setPeriodoFinMes(3);
        config.setPeriodosAnuales("2027:2027-07-01:2027-08-31");

        //cualquier fecha del año corresponde al fin del período de ese año
        Date obligacion= config.fechaObligacion(d("2026-10-07"));
        assertEquals(d("2026-03-31"), obligacion);
        assertEquals(1000.0, config.importe(obligacion, d("2026-01-31"), v), 0.001);
        assertEquals(800.0, config.importe(obligacion, d("2026-02-01"), v), 0.001);
        assertEquals(800.0, config.importe(obligacion, d("2026-03-31"), v), 0.001);
        assertEquals(1000.0, config.importe(obligacion, d("2026-04-01"), v), 0.001);

        //el año con período propio usa ese rango
        Date obligacion2027= config.fechaObligacion(d("2027-01-15"));
        assertEquals(d("2027-08-31"), obligacion2027);
        assertEquals(1000.0, config.importe(obligacion2027, d("2027-03-15"), v), 0.001);
        assertEquals(800.0, config.importe(obligacion2027, d("2027-07-15"), v), 0.001);
    }

    @Test
    public void diarioYMensualSoloCobranEnFechasProgramadas() {
        ConfiguracionCobro diario= config(ConfiguracionCobro.DIARIO);
        diario.setDiasSemana("6,7");
        assertNull(diario.fechaObligacion(d("2026-10-07")));
        assertEquals(d("2026-10-10"), diario.fechaObligacion(d("2026-10-10")));
        assertEquals(d("2026-10-10"), diario.proximaFecha(d("2026-10-07")));

        ConfiguracionCobro mensual= config(ConfiguracionCobro.MENSUAL);
        TipoPlazaHistorial v= vigencia(800.0);
        mensual.setDiasMes("2:U,10:15");
        assertEquals(d("2026-02-28"), mensual.fechaObligacion(d("2026-02-28")));
        assertNull(mensual.fechaObligacion(d("2026-10-07")));
        //pagado antes o el mismo día: descuento; después: precio normal
        assertEquals(800.0, mensual.importe(d("2026-10-15"), d("2026-10-07"), v), 0.001);
        assertEquals(1000.0, mensual.importe(d("2026-10-15"), d("2026-10-16"), v), 0.001);
    }

    @Test
    public void mensualUsaLasFechasPropiasDelAnioEnLugarDeLasGenerales() {
        ConfiguracionCobro mensual= config(ConfiguracionCobro.MENSUAL);
        mensual.setDiasMes("1:10,2:10,2027:3:15,2027:2:U");
        assertEquals(d("2026-01-10"), mensual.fechaObligacion(d("2026-01-10")));
        assertNull(mensual.fechaObligacion(d("2026-03-15")));
        //2027 tiene sus propias fechas: el 10 de enero ya no se cobra
        assertNull(mensual.fechaObligacion(d("2027-01-10")));
        assertEquals(d("2027-02-28"), mensual.fechaObligacion(d("2027-02-28")));
        assertEquals(d("2027-03-15"), mensual.fechaObligacion(d("2027-03-15")));
        assertEquals(d("2028-02-10"), mensual.fechaObligacion(d("2028-02-10")));
    }

    @Test
    public void sinVigenciaNoHayImporteYSinDescuentoSeCobraElNormal() {
        ConfiguracionCobro mensual= config(ConfiguracionCobro.MENSUAL);
        mensual.setDiasMes("10:15");
        assertNull(mensual.importe(d("2026-10-15"), d("2026-10-07"), null));
        assertEquals(1000.0, mensual.importe(d("2026-10-15"), d("2026-10-07"), vigencia(null)), 0.001);
    }
}
