package com.cobranzaplazas.model;



import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cobranzaplazas.repo.Contribuyente;

public class GeneradorCodigos {

    
    /**
     * Genera el código de un contribuyente cuando  no tiene rfc
     * @param apePaterno
     * @param apeMaterno
     * @param nombre
     * @param fechaNacimiento
     * @return
     */
    public String generarCodigoContribuyente(Contribuyente contribuyente){
    	String apePaterno= contribuyente.getApePaterno();
    	String apeMaterno= contribuyente.getApeMaterno();
    	String nombre=  contribuyente.getNombre();
    	//Date fechaNacimien
    			
        //SimpleDateFormat spd= new SimpleDateFormat("YYddMM");
        StringBuilder codigo= new StringBuilder();
        codigo.append(apePaterno.length()>2?apePaterno.substring(0,2):apePaterno);
        codigo.append(apeMaterno.length()>1?apeMaterno.substring(0,1):apeMaterno);
        codigo.append(nombre.length()>1?nombre.substring(0,1):nombre);
        /*SimpleDateFormat format= new SimpleDateFormat("dd/MM/yyyy");
        if(fechaNacimiento!=null && !fechaNacimiento.isEmpty()) {
            Date fecha = null;
            try {

                fecha = format.parse(fechaNacimiento);
                codigo.append(spd.format(fecha));
            } catch (ParseException e) {

            }
        }*/

        return codigo.toString();

    }
}
