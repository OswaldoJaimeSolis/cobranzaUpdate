package com.jalpa.cobranza.model.entidades;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class PropietarioPlaza implements Serializable {
    @PrimaryKey
    @NonNull
    String codigoPropietarioPlaza;
    @Embedded
    Plaza plaza;
    @Embedded
    TipoPlaza tipoPlaza;
    @Embedded
    Contribuyente contribuyente;
    String giro;
    Date vigenciaInicial;
    Date vigenciaFinal;
    Double importe;
    Boolean inServerPropietarioPlaza;
    //Adeudo calculado por el servidor en la última actualización de catálogos
    Double adeudo;
    //Fechas de las obligaciones vencidas (yyyy-MM-dd) separadas por coma
    String fechasVencidas;
    //Saldo del adeudo anterior (ya incluido en adeudo); se cobra en días sin cobro programado
    Double adeudoAnterior;

    public String getCodigoPropietarioPlaza() {
        return codigoPropietarioPlaza;
    }

    public void setCodigoPropietarioPlaza(String codigoPropietarioPlaza) {
        this.codigoPropietarioPlaza = codigoPropietarioPlaza;
    }

    public Plaza getPlaza() {
        return plaza;
    }

    public void setPlaza(Plaza plaza) {
        this.plaza = plaza;
    }

    public TipoPlaza getTipoPlaza() {
        return tipoPlaza;
    }

    public void setTipoPlaza(TipoPlaza tipoPlaza) {
        this.tipoPlaza = tipoPlaza;
    }

    public Contribuyente getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(Contribuyente contribuyente) {
        this.contribuyente = contribuyente;
    }

    public String getGiro() {
        return giro;
    }

    public void setGiro(String giro) {
        this.giro = giro;
    }

    public Date getVigenciaInicial() {
        return vigenciaInicial;
    }

    public void setVigenciaInicial(Date vigenciaInicial) {
        this.vigenciaInicial = vigenciaInicial;
    }

    public Date getVigenciaFinal() {
        return vigenciaFinal;
    }

    public void setVigenciaFinal(Date vigenciaFinal) {
        this.vigenciaFinal = vigenciaFinal;
    }

    public Double getImporte() {
        return importe;
    }

    public void setImporte(Double importe) {
        this.importe = importe;
    }

    public Boolean getInServerPropietarioPlaza() {
        return inServerPropietarioPlaza;
    }

    public void setInServerPropietarioPlaza(Boolean inServerPropietarioPlaza) {
        this.inServerPropietarioPlaza = inServerPropietarioPlaza;
    }

    @NonNull
    @Override
    public String toString() {
        return  tipoPlaza!=null? tipoPlaza.getCodigoTipoPlaza():"";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropietarioPlaza that = (PropietarioPlaza) o;
        return Objects.equals(codigoPropietarioPlaza, that.codigoPropietarioPlaza);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoPropietarioPlaza);
    }

    public Double getAdeudo() {
        return adeudo;
    }

    public void setAdeudo(Double adeudo) {
        this.adeudo = adeudo;
    }

    public String getFechasVencidas() {
        return fechasVencidas;
    }

    public void setFechasVencidas(String fechasVencidas) {
        this.fechasVencidas = fechasVencidas;
    }

    public Double getAdeudoAnterior() {
        return adeudoAnterior;
    }

    public void setAdeudoAnterior(Double adeudoAnterior) {
        this.adeudoAnterior = adeudoAnterior;
    }
}
