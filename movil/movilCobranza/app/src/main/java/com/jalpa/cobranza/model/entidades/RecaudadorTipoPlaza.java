package com.jalpa.cobranza.model.entidades;

import java.io.Serializable;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class RecaudadorTipoPlaza implements Serializable {
    @PrimaryKey (autoGenerate = true)
    Integer idRecaudadorTipoPlaza;
    @Embedded
    Recaudador recaudador;
    @Embedded
    TipoPlaza tipoPlaza;

    public Integer getIdRecaudadorTipoPlaza() {
        return idRecaudadorTipoPlaza;
    }

    public void setIdRecaudadorTipoPlaza(Integer idRecaudadorTipoPlaza) {
        this.idRecaudadorTipoPlaza = idRecaudadorTipoPlaza;
    }

    public Recaudador getRecaudador() {
        return recaudador;
    }

    public void setRecaudador(Recaudador recaudador) {
        this.recaudador = recaudador;
    }

    public TipoPlaza getTipoPlaza() {
        return tipoPlaza;
    }

    public void setTipoPlaza(TipoPlaza tipoPlaza) {
        this.tipoPlaza = tipoPlaza;
    }
}
