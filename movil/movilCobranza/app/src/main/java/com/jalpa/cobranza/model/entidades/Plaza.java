package com.jalpa.cobranza.model.entidades;

import java.io.Serializable;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Plaza implements Serializable {
    @PrimaryKey
    @NonNull
    String codigoPlaza;
    Double latitud;
    Double longitud;
    Boolean inServerPlaza;

    public String getCodigoPlaza() {
        return codigoPlaza;
    }

    public void setCodigoPlaza(String codigoPlaza) {
        this.codigoPlaza = codigoPlaza;
    }

    public Boolean getInServerPlaza() {
        return inServerPlaza;
    }

    public void setInServerPlaza(Boolean inServerPlaza) {
        this.inServerPlaza = inServerPlaza;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public Double getLongitud() {
        return longitud;
    }

    public void setLongitud(Double longitud) {
        this.longitud = longitud;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Plaza plaza = (Plaza) o;
        return Objects.equals(codigoPlaza, plaza.codigoPlaza);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoPlaza);
    }
}
