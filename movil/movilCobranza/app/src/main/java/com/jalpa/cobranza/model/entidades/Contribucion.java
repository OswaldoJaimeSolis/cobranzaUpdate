package com.jalpa.cobranza.model.entidades;

import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.annotation.Nullable;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Contribucion {
    @Ignore
    private static final int ESTADO_PAGADO=1;
    @Ignore
    private static final int ESTADO_PENDIENTE=2;
    @Ignore
    private static final int ESTADO_AUSENTE=3;
    @Ignore
    private SimpleDateFormat spdf;

    @PrimaryKey(autoGenerate = true)
    private int idContribucion;
    @Embedded
    private Contribuyente contribuyente;
    @Embedded
    private Plaza plaza;
    @Embedded
    private TipoPlaza tipoPlaza;
    @Embedded
    private Recaudador recaudador;
    @Embedded
    private EquipoRecaudador equipoRecaudador;
    private String folioDedicado;
    private Date fecha;
    private Date fechaModificacion;
    private Double importe;
    private Integer estadoPago;

    private Boolean estadoServidor;


    public int getIdContribucion() {
        return idContribucion;
    }

    public void setIdContribucion(int idContribucion) {
        this.idContribucion = idContribucion;
    }

    public Contribuyente getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(Contribuyente contribuyente) {
        this.contribuyente = contribuyente;
    }

    public Plaza getPlaza() {
        return plaza;
    }

    public void setPlaza(Plaza plaza) {
        this.plaza = plaza;
    }

    public Recaudador getRecaudador() {
        return recaudador;
    }

    public void setRecaudador(Recaudador recaudador) {
        this.recaudador = recaudador;
    }

    public String getFolioDedicado() {
        return folioDedicado;
    }

    public void setFolioDedicado(String folioDedicado) {
        this.folioDedicado = folioDedicado;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Date getFechaModificacion() {
        return fechaModificacion;
    }

    public void setFechaModificacion(Date fechaModificacion) {
        this.fechaModificacion = fechaModificacion;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public Integer getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(Integer estadoPago) {
        this.estadoPago = estadoPago;
    }

    public Boolean getEstadoServidor() {
        return estadoServidor;
    }

    public void setEstadoServidor(Boolean estadoServidor) {
        this.estadoServidor = estadoServidor;
    }

    public TipoPlaza getTipoPlaza() {
        return tipoPlaza;
    }

    public void setTipoPlaza(TipoPlaza tipoPlaza) {
        this.tipoPlaza = tipoPlaza;
    }

    public EquipoRecaudador getEquipoRecaudador() {
        return equipoRecaudador;
    }

    public void setEquipoRecaudador(EquipoRecaudador equipoRecaudador) {
        this.equipoRecaudador = equipoRecaudador;
    }
    @Ignore
    public String getEstadoToString(){
        if(estadoPago!=null){
            if(estadoPago==ESTADO_PAGADO){
                return "PAGADO";
            }
            if(estadoPago== ESTADO_PENDIENTE){
                return "PENDIENTE";
            }
            if(estadoPago==ESTADO_AUSENTE){
                return "AUSENTE";
            }

        }
        return "NULO";
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Contribucion)) return false;
        if(this.idContribucion== ((Contribucion)obj).getIdContribucion()){
            return  true;
        }
        else {
            return  false;
        }
    }

    @Override
    public String toString() {

        return getSdf().format(fecha)+" "+ String.format("%.2f", importe);
    }

    private SimpleDateFormat getSdf(){
        if(spdf==null){
            spdf= new SimpleDateFormat("dd MMMM yyyy");
        }
        return  spdf;
    }
}
