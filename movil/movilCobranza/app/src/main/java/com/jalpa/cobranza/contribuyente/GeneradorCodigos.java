package com.jalpa.cobranza.contribuyente;

import com.google.zxing.common.StringUtils;
import com.jalpa.cobranza.model.entidades.Contribuyente;
import com.jalpa.cobranza.model.entidades.Plaza;
import com.jalpa.cobranza.model.entidades.TipoPlaza;

import java.text.Normalizer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import androidx.room.util.StringUtil;

public class GeneradorCodigos {

    public String nombrePlaza(Contribuyente contribuyente, TipoPlaza tipoPlaza){
        return contribuyente.getCodigoContribuyente()+"_"+tipoPlaza.getCodigoTipoPlaza();

    }

    public String getCodigoPropietarioPlaza(Contribuyente contribuyente, Plaza plaza){
        return contribuyente.getCodigoContribuyente()+"_"+plaza.getCodigoPlaza();

    }

    /**
     * Genera el código de un contribuyente cuando  no tiene rfc
     * @param apePaterno
     * @param apeMaterno
     * @param nombre
     * @param fechaNacimiento
     * @return
     */
    public String generarCodigoContribuyente(String apePaterno, String apeMaterno, String nombre, String fechaNacimiento){
        SimpleDateFormat spd= new SimpleDateFormat("YYddMM");
        StringBuilder codigo= new StringBuilder();
        codigo.append(apePaterno.length()>2?apePaterno.substring(0,2):apePaterno);
        codigo.append(apeMaterno.length()>1?apeMaterno.substring(0,1):apeMaterno);
        codigo.append(nombre.length()>1?nombre.substring(0,1):nombre);
        SimpleDateFormat format= new SimpleDateFormat("dd/MM/yyyy");
        if(fechaNacimiento!=null && !fechaNacimiento.isEmpty()) {
            Date fecha = null;
            try {

                fecha = format.parse(fechaNacimiento);
                codigo.append(spd.format(fecha));
            } catch (ParseException e) {

            }
        }
        String codeNorm=Normalizer.normalize(codigo.toString(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");

        return codeNorm;

    }
}
