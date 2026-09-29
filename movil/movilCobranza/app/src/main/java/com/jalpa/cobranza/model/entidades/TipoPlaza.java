package com.jalpa.cobranza.model.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import androidx.room.Relation;

@Entity//(indices = {@Index("codigoTipoPlaza")})
public class TipoPlaza implements Serializable {
    @PrimaryKey
    @NonNull
    String codigoTipoPlaza;
    String descripcionTipoPlaza;
    String leyendaTipoPlaza;
    Boolean importeGlobal;
    Boolean addLocal;
    @Ignore
    List<TipoPlazaHistorial> historial;
    //@Relation(parentColumn = "codigoTipoPlaza",
    //entityColumn = "tipoPlazaH" )
    //List<TipoPlazaHistorial> historial;


    public String getCodigoTipoPlaza() {
        return codigoTipoPlaza;
    }

    public void setCodigoTipoPlaza(String codigoTipoPlaza) {
        this.codigoTipoPlaza = codigoTipoPlaza;
    }

    public String getDescripcionTipoPlaza() {
        return descripcionTipoPlaza;
    }

    public void setDescripcionTipoPlaza(String descripcionTipoPlaza) {
        this.descripcionTipoPlaza = descripcionTipoPlaza;
    }

    public Boolean getImporteGlobal() {
        return importeGlobal;
    }

    public void setImporteGlobal(Boolean importeGlobal) {
        this.importeGlobal = importeGlobal;
    }

    public Boolean getAddLocal() {
        return addLocal;
    }

    public void setAddLocal(Boolean addLocal) {
        this.addLocal = addLocal;
    }

    public String getLeyendaTipoPlaza() {
        return leyendaTipoPlaza;
    }

    public void setLeyendaTipoPlaza(String leyendaTipoPlaza) {
        this.leyendaTipoPlaza = leyendaTipoPlaza;
    }

    public List<TipoPlazaHistorial> getHistorial() {
        if(historial==null){
            historial= new ArrayList<>();
        }
        return historial;
    }

    /*public void setHistorial(List<TipoPlazaHistorial> historial) {
        this.historial = historial;
    }*/

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipoPlaza tipoPlaza = (TipoPlaza) o;
        return Objects.equals(codigoTipoPlaza, tipoPlaza.codigoTipoPlaza);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoTipoPlaza);
    }


    @Override
    public String toString(){
        return codigoTipoPlaza;
    }
}
