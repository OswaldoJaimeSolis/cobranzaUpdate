package com.jalpa.cobranza;

import android.content.Context;
import android.util.Log;

import com.bxl.BXLConst;
import com.bxl.config.editor.BXLConfigLoader;
import com.jalpa.cobranza.model.entidades.Contribucion;
import com.jalpa.cobranza.model.entidades.Contribuyente;
import com.jalpa.cobranza.model.entidades.Recaudador;
import com.jalpa.cobranza.model.entidades.TipoPlaza;


import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import jpos.POSPrinter;
import jpos.POSPrinterConst;
import jpos.config.JposEntry;

public class Imprimir {
    BXLConfigLoader bxlConfigLoader;
    Context contextCompat;
    List<Contribuyente> contribuyentes;
    List<TipoPlaza> tiposPlaza;
    List<Recaudador>recaudadores;
    public Imprimir() {


    }
    public void inicializar(Context contextCompat, List<Contribuyente> contribuyentes, List<TipoPlaza> tiposPlaza, List<Recaudador> recaudadores){
        this.contextCompat= contextCompat;
        this.contribuyentes= contribuyentes;
        this.tiposPlaza= tiposPlaza;
        this.recaudadores= recaudadores;
    }

    private void imprimirDo(String cadena){
        try {
            POSPrinter posPrinter = new POSPrinter(contextCompat);
            posPrinter.open("SPP-R310");
            posPrinter.claim(3000);
            posPrinter.setDeviceEnabled(true);
            posPrinter.setAsyncMode(true);
            //posPrinter.setCharacterSet(BXLConst.CS_437_USA_STANDARD_EUROPE);
            posPrinter.setCharacterSet(BXLConst.CS_852_LATIN2);

            posPrinter.setCharacterEncoding(BXLConst.CE_ASCII);
            //posPrinter.setPageModePrintArea("0, 0, 576, 1600");
            //posPrinter.setPageModePrintDirection(POSPrinterConst.PTR_PD_LEFT_TO_RIGHT);
            posPrinter.printNormal(POSPrinterConst.JPOS_EPTR_REC_HEAD_CLEANING, cadena);
        }
        catch (Exception e){
         Log.e("error", e.toString());
        }
    }

    public String generarCadena(Contribucion contribucion){
        StringBuilder sb= new StringBuilder();
        sb.append(formateo("     PRESIDENCIA MUNICIPAL DE JALPA, ZAC   ",0, 1));
        sb.append(formateo("   palacio municipal No. 115 Jalpa, Zac     ",0, 1));

        sb.append(formateo("               PAGO PLAZA                   ",0, 1));
        sb.append(formateo("            ".concat(contribucion.getTipoPlaza()!=null?contribucion.getTipoPlaza().getCodigoTipoPlaza():""),0, 1));

        sb.append(formateo("Fecha Actual:    ".concat(getFechaFormat(new Date())),1,2));
        sb.append(formateo("Folio:           ".concat(contribucion.getFolioDedicado()+""),0,1));
        sb.append(formateo("F. contribución: ".concat(getFechaFormat(contribucion.getFecha())),0,1));
        sb.append(formateo("F. registro:     ".concat(getFechaFormat(contribucion.getFechaModificacion())),0,1));

        sb.append(formateo("Contribuyente:   ".concat(recuperaContribuyente(contribucion.getContribuyente().getCodigoContribuyente())),0,1));
        sb.append(formateo("Importe:         ".concat(String.format("%6.2f", contribucion.getImporte())),0,1));
        sb.append(formateo("Estado:          ".concat(contribucion.getEstadoToString()),0,3));

        sb.append(formateo("".concat(recuperaLeyendaTipoPlaza(contribucion.getTipoPlaza().getLeyendaTipoPlaza()!=null?contribucion.getTipoPlaza().getCodigoTipoPlaza():"")),0,2));

        sb.append("   ________________________________________".concat("\n"));
        sb.append(formateo("           ".concat("Encargado de cobro de plaza:"),0,1));
        String recaudadorN= contribucion.getRecaudador()!=null? (contribucion.getRecaudador().getNombreRecaudador()==null? recuperaRecaudador(contribucion.getRecaudador().getCodigoRecaudador()):contribucion.getRecaudador().getNombreRecaudador()):" ";
        sb.append(formateo("           ".concat(recaudadorN),0,5));



        return sb.toString();

    }
    private String recuperaRecaudador(String codigoRecaudador){

        for(Recaudador rec: recaudadores){
            if(rec.getCodigoRecaudador().equals(codigoRecaudador)){
                return rec.getNombreRecaudador()!=null? rec.getNombreRecaudador():rec.getCodigoRecaudador();
            }
        }
        return  "";
    }
    private String recuperaContribuyente(String codigoContribuyente){
        for(Contribuyente con: contribuyentes){
            if(con.getCodigoContribuyente().equals(codigoContribuyente)){
                return con.getNombreFull();
            }
        }
        return "";
    }

    private String recuperaLeyendaTipoPlaza(String codigoTipoPlaza){
        for(TipoPlaza tp: tiposPlaza){
            if(tp.getCodigoTipoPlaza().equals(codigoTipoPlaza)){
                return tp.getLeyendaTipoPlaza();
            }
        }
        return "";
    }



    private String getFechaFormat(Date date){
        SimpleDateFormat sp= new SimpleDateFormat("dd MMMM yyyy");
        return  sp.format(date);
    }
    /*

    align 0 para izquierda 1 para derecha
     */
    private String formateo(String cad, int aling, int numRetornos){
        String cadFinal="";
        if(aling==0){

                cadFinal = String.format("%-45s", cad);

        }
        else{

                cadFinal = String.format("%45s", cad);

        }
        return addRetornos(cadFinal, numRetornos);
    }
    private String addRetornos(String cad, Integer numRetornos){
        for(int i=0;i<numRetornos;i++){
            cad=cad.concat("\n");
        }
        return  cad;
    }
    private void validarRegistro(){

     boolean existe=false;
        try
        {
            if(bxlConfigLoader==null){
                bxlConfigLoader= new BXLConfigLoader(contextCompat);
            }
            // BXLConfigLoader creating / setting file open
                        bxlConfigLoader.openFile();
                        existe=true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            bxlConfigLoader.newFile();
        }

         try{
             //bxlConfigLoader.newFile();

             for (Object entry : bxlConfigLoader.getEntries())
             {
                 JposEntry jposEntry = (JposEntry)entry;
                 String strLogicalname = jposEntry.getLogicalName();
                 bxlConfigLoader.removeEntry(strLogicalname);
             }


             bxlConfigLoader.addEntry("SPP-R310",
                         BXLConfigLoader.DEVICE_CATEGORY_POS_PRINTER,
                         BXLConfigLoader.PRODUCT_NAME_SPP_R310,
                         BXLConfigLoader.DEVICE_BUS_BLUETOOTH,
                         "74:F0:7D:E8:D8:F3");
                 bxlConfigLoader.saveFile();

         }
         catch (Exception e){
             Log.e("error",e.toString());
         }


    }





}
