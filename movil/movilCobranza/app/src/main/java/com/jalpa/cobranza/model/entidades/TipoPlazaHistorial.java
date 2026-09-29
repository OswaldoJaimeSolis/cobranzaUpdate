package com.jalpa.cobranza.model.entidades;

import java.io.Serializable;
import java.util.Date;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity/*(foreignKeys = @ForeignKey(entity = TipoPlaza.class,parentColumns = "codigoTipoPlaza",
        childColumns = "tipoPlazaH",
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE))*/
public class TipoPlazaHistorial implements Serializable {
    @PrimaryKey(autoGenerate = true)
    Integer idTipoPlazaHistorial;
    String tipoPlazaH;
    Date vigenciaInicial;
    Date vigenciaFinal;
    Double importe;

    public Integer getIdTipoPlazaHistorial() {
        return idTipoPlazaHistorial;
    }

    public void setIdTipoPlazaHistorial(Integer idTipoPlazaHistorial) {
        this.idTipoPlazaHistorial = idTipoPlazaHistorial;
    }

    public String getTipoPlazaH() {
        return tipoPlazaH;
    }

    public void setTipoPlazaH(String tipoPlazaH) {
        this.tipoPlazaH = tipoPlazaH;
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
}
