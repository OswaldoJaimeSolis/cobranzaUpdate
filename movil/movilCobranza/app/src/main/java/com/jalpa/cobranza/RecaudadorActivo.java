package com.jalpa.cobranza;

import com.jalpa.cobranza.model.entidades.Recaudador;
import com.jalpa.cobranza.model.entidades.RecaudadorTipoPlaza;
import com.jalpa.cobranza.model.entidades.TipoPlaza;

public class RecaudadorActivo {
    private static Recaudador recaudadorActivo;


    public static Recaudador getRecaudadorActivo() {
        return recaudadorActivo;
    }

    public static void setRecaudadorActivo(Recaudador recaudadorActivo) {
        RecaudadorActivo.recaudadorActivo = recaudadorActivo;
    }

    public static boolean validarAutorizacion(TipoPlaza tipoPlaza){
        if(recaudadorActivo!=null) {
            for (RecaudadorTipoPlaza rtp : recaudadorActivo.getTipoPlazas()) {
                if (rtp.getTipoPlaza().equals(tipoPlaza)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

}
