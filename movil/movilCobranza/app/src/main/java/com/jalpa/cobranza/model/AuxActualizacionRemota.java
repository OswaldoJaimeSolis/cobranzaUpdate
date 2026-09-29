package com.jalpa.cobranza.model;

import com.jalpa.cobranza.model.entidades.Contribuyente;
import com.jalpa.cobranza.model.entidades.Plaza;
import com.jalpa.cobranza.model.entidades.PropietarioPlaza;
import com.jalpa.cobranza.model.entidades.Recaudador;
import com.jalpa.cobranza.model.entidades.TipoPlaza;

import java.util.ArrayList;
import java.util.List;

public class AuxActualizacionRemota {
    List<Contribuyente> contribuyentes;
    List<Plaza> plazas;
    List<PropietarioPlaza> propietarioPlazas;

    public List<Contribuyente> getContribuyentes() {
        if(contribuyentes==null){
            contribuyentes= new ArrayList<>();
        }
        return contribuyentes;
    }

    public void setContribuyentes(List<Contribuyente> contribuyentes) {
        this.contribuyentes = contribuyentes;
    }

    public List<Plaza> getPlazas() {
        if(plazas==null){
            plazas= new ArrayList<>();
        }
        return plazas;
    }

    public void setPlazas(List<Plaza> plazas) {
        this.plazas = plazas;
    }

    public List<PropietarioPlaza> getPropietarioPlazas() {
        if(propietarioPlazas==null){
            propietarioPlazas= new ArrayList<>();
        }
        return propietarioPlazas;
    }

    public void setPropietarioPlazas(List<PropietarioPlaza> propietarioPlazas) {
        this.propietarioPlazas = propietarioPlazas;
    }
}
