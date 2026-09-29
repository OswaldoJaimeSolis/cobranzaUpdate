package com.jalpa.cobranza.contribuyente;

import com.jalpa.cobranza.model.entidades.Contribuyente;
import com.jalpa.cobranza.model.entidades.Plaza;
import com.jalpa.cobranza.model.entidades.PropietarioPlaza;

public class PlazaTipoContribuyenteContenedor {
    private Plaza plaza;
    private Contribuyente contribuyente;
    private PropietarioPlaza propietarioPlaza;

    /**
     *
     * @param plaza
     * @param contribuyente
     * @param propietarioPlaza
     */
    public PlazaTipoContribuyenteContenedor(Plaza plaza, Contribuyente contribuyente, PropietarioPlaza propietarioPlaza) {
        this.plaza = plaza;
        this.contribuyente = contribuyente;
        this.propietarioPlaza = propietarioPlaza;
    }

    public Plaza getPlaza() {
        return plaza;
    }

    public Contribuyente getContribuyente() {
        return contribuyente;
    }

    public PropietarioPlaza getPropietarioPlaza() {
        return propietarioPlaza;
    }
}
