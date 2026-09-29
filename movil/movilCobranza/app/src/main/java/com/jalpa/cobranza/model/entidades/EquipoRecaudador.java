package com.jalpa.cobranza.model.entidades;

import java.io.Serializable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class EquipoRecaudador implements Serializable {
    @PrimaryKey
    @NonNull
    String codigoEquipo;
    String descripcionEquipo;
    Boolean inServer;

    public String getCodigoEquipo() {
        return codigoEquipo;
    }

    public void setCodigoEquipo(String codigoEquipo) {
        this.codigoEquipo = codigoEquipo;
    }

    public String getDescripcionEquipo() {
        return descripcionEquipo;
    }

    public void setDescripcionEquipo(String descripcionEquipo) {
        this.descripcionEquipo = descripcionEquipo;
    }

    public Boolean getInServer() {
        return inServer;
    }

    public void setInServer(Boolean inServer) {
        this.inServer = inServer;
    }
}
