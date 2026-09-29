package com.jalpa.cobranza.model.entidades;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Recaudador implements Serializable {
    @PrimaryKey
    @NonNull
    private String codigoRecaudador;
    private String passRecaudador;
    private String nombreRecaudador;
    private Boolean activoRecaudador;
    @Ignore
    List<RecaudadorTipoPlaza> tipoPlazas;


    public String getCodigoRecaudador() {
        return codigoRecaudador;
    }

    public void setCodigoRecaudador(String codigoRecaudador) {
        this.codigoRecaudador = codigoRecaudador;
    }

    public String getPassRecaudador() {
        return passRecaudador;
    }

    public void setPassRecaudador(String passRecaudador) {
        this.passRecaudador = passRecaudador;
    }

    public String getNombreRecaudador() {
        return nombreRecaudador;
    }

    public void setNombreRecaudador(String nombreRecaudador) {
        this.nombreRecaudador = nombreRecaudador;
    }

    public Boolean getActivoRecaudador() {
        return activoRecaudador;
    }

    public void setActivoRecaudador(Boolean activoRecaudador) {
        this.activoRecaudador = activoRecaudador;
    }

    public List<RecaudadorTipoPlaza> getTipoPlazas() {
        if(tipoPlazas==null){
            tipoPlazas= new ArrayList<>();
        }
        return tipoPlazas;
    }

    public void setTipoPlazas(List<RecaudadorTipoPlaza> tipoPlazas) {
        this.tipoPlazas = tipoPlazas;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Recaudador that = (Recaudador) o;
        return Objects.equals(codigoRecaudador, that.codigoRecaudador);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoRecaudador);
    }
}
