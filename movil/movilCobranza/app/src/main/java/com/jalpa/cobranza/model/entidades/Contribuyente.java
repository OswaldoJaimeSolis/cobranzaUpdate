package com.jalpa.cobranza.model.entidades;

import java.io.Serializable;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Contribuyente implements Serializable {
    @PrimaryKey
    @NonNull
    private String codigoContribuyente;
    private String nombre;
    private String apePaterno;
    private String apeMaterno;
    private String rfc;
    private Boolean inServerContribuyente;

    @NonNull
    public String getCodigoContribuyente() {
        return codigoContribuyente;
    }

    public void setCodigoContribuyente(@NonNull String codigoContribuyente) {
        this.codigoContribuyente = codigoContribuyente;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApePaterno() {
        return apePaterno;
    }

    public void setApePaterno(String apePaterno) {
        this.apePaterno = apePaterno;
    }

    public String getApeMaterno() {
        return apeMaterno;
    }

    public void setApeMaterno(String apeMaterno) {
        this.apeMaterno = apeMaterno;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public Boolean getInServerContribuyente() {
        return inServerContribuyente;
    }

    public void setInServerContribuyente(Boolean inServerContribuyente) {
        this.inServerContribuyente = inServerContribuyente;
    }

    public String getNombreFull(){
        String nombreFull= (nombre!=null?nombre:"").concat(" ").concat(apePaterno!=null?apePaterno:"").concat(" ").concat(apeMaterno!=null?apeMaterno:" ");
        return  nombreFull;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contribuyente that = (Contribuyente) o;
        return Objects.equals(codigoContribuyente, that.codigoContribuyente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoContribuyente);
    }
}
