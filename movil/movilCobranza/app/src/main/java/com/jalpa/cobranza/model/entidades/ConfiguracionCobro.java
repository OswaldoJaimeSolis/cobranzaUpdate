package com.jalpa.cobranza.model.entidades;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Date;

/**
 * Esquema de cobro de un tipo de plaza con importe global, tal como lo entrega el servidor
 * (getConfiguracionCobro). Los precios salen de la vigencia del tipo de plaza (TipoPlazaHistorial)
 * en la fecha de la obligación. La fecha de una obligación es también su fecha límite:
 * pagada ese día o antes aplica el precio con descuento, después el precio normal.
 * En el cobro anual la obligación es el fin del período de pago y el descuento sólo
 * aplica si se paga dentro del período (ni antes de su inicio ni después de su fin).
 */
@Entity
public class ConfiguracionCobro implements Serializable {
    public static final String DIARIO="DIARIO";
    public static final String MENSUAL="MENSUAL";
    public static final String ANUAL="ANUAL";

    @PrimaryKey
    @NonNull
    String codigoTipoPlazaCobro;
    String tipoCobro;
    //DIARIO: días ISO separados por coma (1 = lunes ... 7 = domingo)
    String diasSemana;
    Date fechaInicioCobro;
    //MENSUAL: "mes:dia" separados por coma; U en lugar del día es el último día del mes.
    //"anio:mes:dia" aplica sólo a ese año y, si el año tiene alguno, reemplaza a los "mes:dia"
    String diasMes;
    //ANUAL: período por omisión y períodos específicos "anio:inicio:fin" (yyyy-MM-dd) separados por coma
    Integer periodoInicioDia;
    Integer periodoInicioMes;
    Integer periodoFinDia;
    Integer periodoFinMes;
    String periodosAnuales;

    public static LocalDate aLocalDate(Date fecha){
        return fecha.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date aDate(LocalDate fecha){
        return Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    public boolean esAnual(){
        return ANUAL.equals(tipoCobro);
    }

    /** Período de pago del año: {inicio, fin}; el específico de ese año o, si no hay, el período por omisión. */
    private LocalDate[] periodoAnual(int anio){
        if(periodosAnuales!=null){
            for(String periodo: periodosAnuales.split(",")){
                String[] partes= periodo.split(":");
                if(partes.length==3 && partes[0].trim().equals(String.valueOf(anio))){
                    return new LocalDate[]{LocalDate.parse(partes[1].trim()), LocalDate.parse(partes[2].trim())};
                }
            }
        }
        if(periodoInicioDia==null || periodoInicioMes==null || periodoFinDia==null || periodoFinMes==null){
            return null;
        }
        YearMonth inicio= YearMonth.of(anio, periodoInicioMes);
        YearMonth fin= YearMonth.of(anio, periodoFinMes);
        return new LocalDate[]{inicio.atDay(Math.min(periodoInicioDia, inicio.lengthOfMonth())),
                fin.atDay(Math.min(periodoFinDia, fin.lengthOfMonth()))};
    }

    private LocalDate finPeriodoAnual(int anio){
        LocalDate[] periodo= periodoAnual(anio);
        return periodo!=null? periodo[1]: null;
    }

    private boolean hayCobro(LocalDate fecha){
        if(DIARIO.equals(tipoCobro) && diasSemana!=null){
            for(String dia: diasSemana.split(",")){
                if(dia.trim().equals(String.valueOf(fecha.getDayOfWeek().getValue()))){
                    return true;
                }
            }
        }
        if(MENSUAL.equals(tipoCobro) && diasMes!=null){
            int largoMes= fecha.lengthOfMonth();
            String anio= String.valueOf(fecha.getYear());
            boolean anioPropio= false;
            for(String diaMes: diasMes.split(",")){
                String[] partes= diaMes.split(":");
                if(partes.length==3 && partes[0].trim().equals(anio)){
                    anioPropio= true;
                    break;
                }
            }
            for(String diaMes: diasMes.split(",")){
                String[] partes= diaMes.split(":");
                //con fechas propias del año sólo cuentan esas; si no, las generales "mes:dia"
                if(anioPropio? partes.length!=3 || !partes[0].trim().equals(anio): partes.length!=2){
                    continue;
                }
                String mes= partes[partes.length-2].trim();
                String valorDia= partes[partes.length-1].trim();
                if(!mes.equals(String.valueOf(fecha.getMonthValue()))){
                    continue;
                }
                //un día mayor al largo del mes (29 de febrero) se cobra el último día
                int dia= valorDia.equals("U")? largoMes: Math.min(Integer.parseInt(valorDia), largoMes);
                if(dia==fecha.getDayOfMonth()){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Fecha de la obligación que corresponde a la fecha elegida, o null si ese día no hay
     * cobro programado. En el cobro anual cualquier fecha del año corresponde al fin del
     * período de pago de ese año.
     */
    public Date fechaObligacion(Date fechaElegida){
        LocalDate fecha= aLocalDate(fechaElegida);
        LocalDate obligacion= null;
        if(esAnual()){
            obligacion= finPeriodoAnual(fecha.getYear());
        }
        else if(hayCobro(fecha)){
            obligacion= fecha;
        }
        if(obligacion==null || (fechaInicioCobro!=null && obligacion.isBefore(aLocalDate(fechaInicioCobro)))){
            return null;
        }
        return aDate(obligacion);
    }

    /** Siguiente fecha de cobro a partir de la dada, o null si no hay ninguna en los próximos dos años. */
    public Date proximaFecha(Date desde){
        LocalDate fecha= aLocalDate(desde);
        for(int i=0;i<731;i++){
            Date obligacion= fechaObligacion(aDate(fecha.plusDays(i)));
            if(obligacion!=null && !obligacion.before(aDate(fecha))){
                return obligacion;
            }
        }
        return null;
    }

    /**
     * Precio con descuento si se paga a más tardar en la fecha de la obligación; después, precio
     * normal. En el cobro anual el pago además debe caer dentro del período de pago. Los precios
     * son los de la vigencia del tipo de plaza en la fecha de la obligación; null si no hay.
     */
    public Double importe(Date fechaObligacion, Date fechaPago, TipoPlazaHistorial vigencia){
        if(vigencia==null || vigencia.getImporte()==null){
            return null;
        }
        LocalDate pago= aLocalDate(fechaPago);
        boolean puntual= !pago.isAfter(aLocalDate(fechaObligacion));
        if(puntual && esAnual()){
            LocalDate[] periodo= periodoAnual(aLocalDate(fechaObligacion).getYear());
            puntual= periodo!=null && !pago.isBefore(periodo[0]);
        }
        //sin precio con descuento capturado en la vigencia se cobra el normal
        return puntual && vigencia.getPrecioDescuento()!=null? vigencia.getPrecioDescuento(): vigencia.getImporte();
    }

    @NonNull
    public String getCodigoTipoPlazaCobro() {
        return codigoTipoPlazaCobro;
    }

    public void setCodigoTipoPlazaCobro(@NonNull String codigoTipoPlazaCobro) {
        this.codigoTipoPlazaCobro = codigoTipoPlazaCobro;
    }

    public String getTipoCobro() {
        return tipoCobro;
    }

    public void setTipoCobro(String tipoCobro) {
        this.tipoCobro = tipoCobro;
    }

    public String getDiasSemana() {
        return diasSemana;
    }

    public void setDiasSemana(String diasSemana) {
        this.diasSemana = diasSemana;
    }

    public Date getFechaInicioCobro() {
        return fechaInicioCobro;
    }

    public void setFechaInicioCobro(Date fechaInicioCobro) {
        this.fechaInicioCobro = fechaInicioCobro;
    }

    public String getDiasMes() {
        return diasMes;
    }

    public void setDiasMes(String diasMes) {
        this.diasMes = diasMes;
    }

    public Integer getPeriodoInicioDia() {
        return periodoInicioDia;
    }

    public void setPeriodoInicioDia(Integer periodoInicioDia) {
        this.periodoInicioDia = periodoInicioDia;
    }

    public Integer getPeriodoInicioMes() {
        return periodoInicioMes;
    }

    public void setPeriodoInicioMes(Integer periodoInicioMes) {
        this.periodoInicioMes = periodoInicioMes;
    }

    public Integer getPeriodoFinDia() {
        return periodoFinDia;
    }

    public void setPeriodoFinDia(Integer periodoFinDia) {
        this.periodoFinDia = periodoFinDia;
    }

    public Integer getPeriodoFinMes() {
        return periodoFinMes;
    }

    public void setPeriodoFinMes(Integer periodoFinMes) {
        this.periodoFinMes = periodoFinMes;
    }

    public String getPeriodosAnuales() {
        return periodosAnuales;
    }

    public void setPeriodosAnuales(String periodosAnuales) {
        this.periodosAnuales = periodosAnuales;
    }
}
