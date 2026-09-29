package com.jalpa.cobranza.model.entidades;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Folio {
    @PrimaryKey
    @NonNull
    String codigoFolio;
    Integer folio;

    @NonNull
    public String getCodigoFolio() {
        return codigoFolio;
    }

    public void setCodigoFolio(@NonNull String codigoFolio) {
        this.codigoFolio = codigoFolio;
    }

    public Integer getFolio() {
        return folio;
    }

    public void setFolio(Integer folio) {
        this.folio = folio;
    }
}
