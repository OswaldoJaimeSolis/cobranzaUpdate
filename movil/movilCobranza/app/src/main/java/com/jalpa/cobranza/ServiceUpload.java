package com.jalpa.cobranza;

import android.app.IntentService;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.jalpa.cobranza.model.dao.Database;
import com.jalpa.cobranza.model.entidades.Contribucion;
import com.jalpa.cobranza.model.entidades.Contribuyente;
import com.jalpa.cobranza.model.entidades.EquipoRecaudador;
import com.jalpa.cobranza.model.entidades.Plaza;
import com.jalpa.cobranza.model.entidades.PropietarioPlaza;
import com.jalpa.cobranza.model.entidades.Recaudador;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Objects;
import java.util.Timer;

import androidx.annotation.Nullable;

public class ServiceUpload extends IntentService {
    WebService wS;
    boolean repetirCiclo=true;

    SimpleDateFormat format;
    private static Timer timer;

    public ServiceUpload() {
        super("service");

    }
    public ServiceUpload(String name)
    {
        super(name);


    }


    private void _startService(Boolean forzarEnvio)
    {



    while(repetirCiclo){
        try {

            doServiceWork();
            if(forzarEnvio){
                repetirCiclo=false;
            }
            Thread.sleep(300000);
        }
        catch(InterruptedException e){

        }

    }

    }

    private void doServiceWork()
    {
        //do something wotever you want
        //like reading file or getting data from network
        try {
            String jsonEquipoRecaudador="";
            EquipoRecaudador equipoRecaudador= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().recuperarEquipoRecaudador();
            if(equipoRecaudador!=null && !equipoRecaudador.getInServer()){
                jsonEquipoRecaudador= convertEquipoRecaudadorToJSON(equipoRecaudador);

            }
            List<Plaza> plazasToUpload= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getPlazasToUpload();
            JSONArray jsonPlazas=null;
            if(plazasToUpload.size()>0){
                jsonPlazas= new JSONArray();
                for(Plaza pl: plazasToUpload){
                    JSONObject jo= convertPlazaToJson(pl);
                    if(jo!=null){
                        jsonPlazas.put(jo);
                    }
                }
            }

            List<Contribuyente> contribuyentesToUpload= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getContribuyenteToUpload();
            JSONArray jsonContribuyente=null;
            if(contribuyentesToUpload.size()>0){
                jsonContribuyente= new JSONArray();
                for(Contribuyente con: contribuyentesToUpload){
                    JSONObject jo= convertContribuyenteToJson(con);
                    if(jo!=null){
                        jsonContribuyente.put(jo);
                    }
                }
            }

            List<PropietarioPlaza> ppToUpload= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getPropietarioPlazaToUpload();
            JSONArray jsonPP=null;
            if(ppToUpload.size()>0){
                jsonPP= new JSONArray();
                for(PropietarioPlaza pp: ppToUpload){
                    JSONObject  jo= convertPropietarioPlazaToJson(pp);
                    if(jo!=null){
                        jsonPP.put(jo);
                    }

                }
            }
            //List<Contribucion> contribuciones= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getContribucionesAll();
            List<Contribucion> contribuciones= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().recuperarContribucionesEstadoServidor();
            JSONArray jsonContribuciones=null;
            if(contribuciones.size()>0){
                jsonContribuciones=new JSONArray(); //(JsonArray) new Gson().toJsonTree(contribuciones, new TypeToken<List<Contribucion>>(){}.getType());

                for(Contribucion contribucion: contribuciones){
                   JSONObject jo= convertContribucionToJson(contribucion);
                   if(jo!=null){
                       jsonContribuciones.put(jo);
                   }
                    
                }

            }
                new UploadToServer(jsonContribuciones, jsonEquipoRecaudador, jsonPlazas, jsonContribuyente, jsonPP).execute();
                //new UploadToServer(jsonContribuciones, jsonEquipoRecaudador, null, null).execute();



        }
        catch (Exception e) {
            Log.e("service", e.toString());
        }

    }

    private class UploadToServer extends AsyncTask<Void, Void, Void>{
        String equipoRecaudador;
        JSONArray jsPlazas;
        JSONArray jsContribuyente;
        JSONArray jsContribuciones;
        JSONArray jsPropietarioPlaza;
        public UploadToServer(JSONArray jsContribuciones, String equipoRecaudador, JSONArray jsplazas, JSONArray jsContribuyente, JSONArray jsPropietarioPlaza){
            this.equipoRecaudador= equipoRecaudador;
            this.jsContribuciones= jsContribuciones;
            this.jsPlazas= jsplazas;
            this.jsContribuyente= jsContribuyente;
            this.jsPropietarioPlaza= jsPropietarioPlaza;
        }
        @Override
        protected Void doInBackground(Void... jsonObjects) {
            //sólo si se pudo hacer el cargo regresa el json
            Gson  gson= new Gson();
             if(equipoRecaudador!=null && !equipoRecaudador.isEmpty()) {
                String jsResEquipoRecaudador = getwS().insertarEquipoRecaudadorServidor(equipoRecaudador);
                if (jsResEquipoRecaudador != null) {
                    EquipoRecaudador resEquipoRecaudador = gson.fromJson(jsResEquipoRecaudador, EquipoRecaudador.class);
                    resEquipoRecaudador.setInServer(true);
                    Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().insertarUpdateEquipoRecaudador(resEquipoRecaudador);
                }
            }
            boolean insertarPlazasCorrecto=true;
            if(jsPlazas!=null && jsPlazas.length()>0){
                JSONArray arr= getwS().insertarPlazas(jsPlazas);
                if(arr!=null){
                    for(int i=0;i<arr.length();i++){
                        try {
                            JSONObject js = (JSONObject) arr.get(i);
                            Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().updatePlaza(true, js.getString("codigoPlaza"));
                        }
                        catch (Exception e){
                            insertarPlazasCorrecto=false;
                        }
                    }
                }
                else {
                    insertarPlazasCorrecto=false;
                }
            }
            boolean insertarContribuyenteCorrecto=true;
            if(jsContribuyente!=null && jsContribuyente.length()>0 && insertarPlazasCorrecto){
                JSONArray arr= getwS().insertarContribuyentes(jsContribuyente);
                if(arr!=null){
                    for(int i=0;i<arr.length();i++){
                        try {
                            JSONObject js = (JSONObject) arr.get(i);
                            Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().updateContribuyente(true, js.getString("codigoContribuyente"));

                        }
                        catch (Exception e){
                            insertarContribuyenteCorrecto=false;
                        }
                    }
                }
                else{
                    insertarContribuyenteCorrecto=false;
                }
            }

            if(jsPropietarioPlaza!=null && jsPropietarioPlaza.length()>0 && insertarPlazasCorrecto && insertarContribuyenteCorrecto){
                JSONArray arr= getwS().insertarPropietarioPlaza(jsPropietarioPlaza);
                if(arr!=null){
                    for(int i=0;i<arr.length();i++){
                        try {
                            JSONObject js = (JSONObject) arr.get(i);
                            Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().updatePropietarioPlaza(true, js.getString("codigoPropietarioPlaza"));

                        }
                        catch (Exception e){
                            insertarContribuyenteCorrecto=false;
                        }
                    }
                }
                else{
                    insertarContribuyenteCorrecto=false;
                }
            }


            if(jsContribuciones!=null && jsContribuciones.length()>0) {
                JSONArray arr = getwS().insertarContribucionesServidor(jsContribuciones);
                if (arr != null) {

                    for (int i = 0; i < arr.length(); i++) {
                        try {

                            //Contribucion con = gson.fromJson((JsonElement) arr.get(i), Contribucion.class);
                            //Contribucion con= (Contribucion)js;
                            //Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().updateContribucion(true, con.getIdContribucion());
                            JSONObject js= (JSONObject) arr.get(i);
                            Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().updateContribucion(true, js.getString("idContribucion"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

             return null;

        }

        @Override
        protected void onPostExecute(Void res) {
            super.onPostExecute(res);

        }
    }

    private String convertEquipoRecaudadorToJSON(EquipoRecaudador equipoRecaudador){
        JSONObject jo= new JSONObject();
        Gson  gson= new Gson();

        String json= gson.toJson(equipoRecaudador);
        return json;


    }

    private JSONObject convertPropietarioPlazaToJson(PropietarioPlaza pp){
        JSONObject jo= new JSONObject();
        try {
            jo.put("codigoPropietarioPlaza", pp.getCodigoPropietarioPlaza());
            jo.put("plaza",pp.getPlaza().getCodigoPlaza());
            jo.put("tipoPlaza", pp.getTipoPlaza().getCodigoTipoPlaza());
            jo.put("contribuyente", pp.getContribuyente().getCodigoContribuyente());
            jo.put("giro", pp.getGiro());
            jo.put("vigenciaInicial", getFormat().format(pp.getVigenciaInicial()));
            jo.put("vigenciaFinal", getFormat().format(pp.getVigenciaFinal()));
            jo.put("importe", pp.getImporte());




            return  jo;


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }


    private JSONObject convertContribuyenteToJson(Contribuyente con){
        JSONObject jo= new JSONObject();
        try {
            jo.put("codigoContribuyente", con.getCodigoContribuyente());
            jo.put("nombre",con.getNombre());
            jo.put("apePaterno", con.getApePaterno());
            jo.put("apeMaterno", con.getApeMaterno());
            jo.put("rfc", con.getRfc());


            return  jo;


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject convertPlazaToJson(Plaza plaza){
        JSONObject jo= new JSONObject();
        try {
            jo.put("codigoPlaza", plaza.getCodigoPlaza());
            jo.put("latitud",plaza.getLatitud());
            jo.put("longitud", plaza.getLongitud());


            return  jo;


        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private JSONObject convertContribucionToJson(Contribucion con){
    JSONObject jo= new JSONObject();
        String idContribucion= con.getFolioDedicado()+"";
        //String estadoPago = con.getEstadoToString();
        //hice este cambio despues de entregarlo
        String estadoPago= con.getEstadoPago().toString();
        String fechaContribucion= getFormat().format(con.getFecha());
        String fechaModificacion= getFormat().format(con.getFechaModificacion());
        String tipoPlaza=con.getTipoPlaza().getCodigoTipoPlaza();
        String plaza= con.getPlaza().getCodigoPlaza();
        String contribuyente= con.getContribuyente().getCodigoContribuyente();
        String recaudador= con.getRecaudador()!=null? con.getRecaudador().getCodigoRecaudador():null;
        String equipo= con.getEquipoRecaudador()!=null? con.getEquipoRecaudador().getCodigoEquipo():null;
        String importe= con.getImporte().toString();
        String codigoTipoPlaza= con.getTipoPlaza().getCodigoTipoPlaza();


        try {
            jo.put("idContribucion", idContribucion);
            jo.put("estadoPago",estadoPago);
        
            jo.put("fecha", fechaContribucion);
            jo.put("fechaModificacion", fechaModificacion);
            jo.put("tipoPlaza",tipoPlaza);
            jo.put("plaza", plaza);
            jo.put("contribuyente",contribuyente);
            jo.put("recaudador",recaudador);
            jo.put("equipoRecaudador", equipo);
            jo.put("importe", importe);
            jo.put("codigoTipoPlaza", codigoTipoPlaza);


            return  jo;
        
        
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void _shutdownService()
    {
        if (timer != null) timer.cancel();
        Log.i(getClass().getSimpleName(), "Timer stopped...");
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();

        _shutdownService();

        // if (MAIN_ACTIVITY != null)  Log.d(getClass().getSimpleName(), "FileScannerService stopped");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Boolean fozarEnvio=(Boolean)intent.getExtras().get("fozarEnvio");
        if(fozarEnvio==null){
            fozarEnvio=false;
        }

        _startService(fozarEnvio);
    }

    public WebService getwS() {
        if(wS==null){
            wS= new WebService();
        }
        return wS;
    }

    public SimpleDateFormat getFormat() {
        if(format==null){
            format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        }
        return format;
    }
}
