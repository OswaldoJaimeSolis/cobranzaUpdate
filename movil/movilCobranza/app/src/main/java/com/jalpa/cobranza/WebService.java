package com.jalpa.cobranza;

import android.util.Log;


import com.google.gson.JsonArray;
import com.jalpa.cobranza.model.AuxActualizacionRemota;
import com.jalpa.cobranza.model.entidades.Contribucion;
import com.jalpa.cobranza.model.entidades.Contribuyente;
import com.jalpa.cobranza.model.entidades.EquipoRecaudador;
import com.jalpa.cobranza.model.entidades.Plaza;
import com.jalpa.cobranza.model.entidades.PropietarioPlaza;
import com.jalpa.cobranza.model.entidades.Recaudador;
import com.jalpa.cobranza.model.entidades.RecaudadorTipoPlaza;
import com.jalpa.cobranza.model.entidades.TipoPlaza;
import com.jalpa.cobranza.model.entidades.TipoPlazaHistorial;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class WebService {
    // Antes apuntaba a los scripts PHP de producción (https://jalpa.gob.mx/cobranza/*.php);
    // ahora apunta al backend local (backendCobranza), que expone el mismo contrato bajo
    // los mismos nombres mostrados abajo pero sin ".php". 10.0.2.2 es el alias que el
    // emulador de Android usa para llegar al localhost de la máquina anfitriona; en un
    // dispositivo físico en la misma red hay que sustituirlo por la IP LAN del backend.
    private final String URL_BASE="http://10.0.2.2:8080/cobranzaPlaza/";
    //private final String URL_BASE="http://192.168.1.X:8080/cobranzaPlaza/";
    protected List<TipoPlaza> getTiposPlaza(String url, int timeOut) {
        List<TipoPlaza> tiposPlaza= new ArrayList<>();
        url=URL_BASE+"getTipoPlaza";
        timeOut=30;
        HttpURLConnection c = null;


        String result = null;


        InputStream inputStream = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();

            c.connect();

            int status = c.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    inputStream = c.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                    try {
                        JSONArray json = new JSONArray(result);
                        for(int i=0;i<json.length();i++){

                            JSONObject jo= (JSONObject) json.get(i);
                            String codigoTipoPlaza= jo.getString("codigoTipoPlaza");
                            TipoPlaza tp= new TipoPlaza();
                            tp.setCodigoTipoPlaza(codigoTipoPlaza);
                            tp.setDescripcionTipoPlaza(jo.getString("descripcionTipoPlaza"));
                            tp.setImporteGlobal(jo.getString("porImporteGlobal").equals("1")?true:false);
                            tp.setAddLocal(jo.getString("addLocal").equals("1")?true:false);

                            TipoPlazaHistorial tph=null;
                            if(tp.getImporteGlobal()) {
                                tph=  new TipoPlazaHistorial();
                                tph.setTipoPlazaH(tp.getCodigoTipoPlaza());
                                tph.setVigenciaInicial(new SimpleDateFormat("yyyy-MM-dd").parse(jo.getString("vigenciaInicial")));
                                tph.setVigenciaFinal(new SimpleDateFormat("yyyy-MM-dd").parse(jo.getString("vigenciaFinal")));
                                tph.setImporte(jo.getDouble("importe"));
                            }
                            int pos=  tiposPlaza.indexOf(tp);

                            //si no está en la lista hay que agregarla junto con su vigencia de ser el caso
                            if(pos<0){
                                tiposPlaza.add(tp);
                                if(tph!=null) {
                                    tiposPlaza.get(tiposPlaza.size() - 1).getHistorial().add(tph);
                                }
                            }
                            else{
                                if(tph!=null) {
                                    tiposPlaza.get(pos).getHistorial().add(tph);
                                }
                            }

                        }
                        return  tiposPlaza;

                    }
                    catch(Exception e){
                        Log.e("",e.toString());
                        return null;
                    }

            }


        } catch (IOException e) {
            Log.e("Json Exception", e.toString());
            return null;
        }

        return  null;
    }

    protected AuxActualizacionRemota getContribuyentesPlazas(String url, int timeOut, Date fecha, List<TipoPlaza> tiposPlaza) {
        SimpleDateFormat spdf=  new SimpleDateFormat("yyyy-MM-dd");
        AuxActualizacionRemota aux= new AuxActualizacionRemota();
        url=URL_BASE+"getContribuyentes";
        timeOut=30;
        HttpURLConnection c = null;


        String result = null;


        InputStream inputStream = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("POST");
            c.setDoInput(true);
            //c.setRequestProperty("fechaRecuperar",spdf.format(fecha));

            OutputStreamWriter writer = new OutputStreamWriter(
                    c.getOutputStream());
            writer.write("fechaRecuperar"+"="+URLEncoder.encode(spdf.format(fecha),"UTF-8"));

            writer.flush();
            writer.close();


            c.connect();

            int status = c.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    inputStream = c.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                    try {
                        JSONArray json = new JSONArray(result);
                        Log.d("PLAZASSSS", json.length()+"");
                        for(int i=0;i<json.length();i++){
                            Log.d("NUMERO", i+"");
                            JSONObject jo= (JSONObject) json.get(i);

                            Contribuyente con= new Contribuyente();
                            con.setCodigoContribuyente(jo.getString("codigoContribuyente"));
                            if(con.getCodigoContribuyente().equals("FIFA_304")){
                                System.out.println("aqui");
                            }
                            if(con.getCodigoContribuyente().equals("AMFA_305")){
                                System.out.println("aqui");
                            }
                            con.setNombre(URLDecoder.decode(jo.getString("nombre"),"UTF-8"));
                            con.setApePaterno(URLDecoder.decode(jo.getString("apePaterno"),"UTF-8"));
                            con.setApeMaterno(URLDecoder.decode(jo.getString("apeMaterno"),"UTF-8"));
                            con.setRfc(jo.getString("rfc"));
                            con.setInServerContribuyente(true);

                            if(con.getCodigoContribuyente()!=null && !con.getCodigoContribuyente().isEmpty() &&!aux.getContribuyentes().contains(con)){
                                aux.getContribuyentes().add(con);
                            }


                            Plaza plaza= new Plaza();
                            plaza.setInServerPlaza(true);
                            plaza.setCodigoPlaza(jo.getString("codigoPlaza"));
                            plaza.setLongitud(!jo.getString("longitudPlaza").isEmpty()?jo.getDouble("longitudPlaza"): null);
                            plaza.setLatitud(!jo.getString("latitudPlaza").isEmpty()?jo.getDouble("latitudPlaza"):null);
                            if(plaza.getCodigoPlaza()!=null && !plaza.getCodigoPlaza().isEmpty()) {
                                if (!aux.getPlazas().contains(plaza)) {
                                    aux.getPlazas().add(plaza);
                                }
                            }

                            TipoPlaza tt= new TipoPlaza();
                            String codigoTipoPlaza= jo.getString("codigoTipoPlaza");
                            tt.setCodigoTipoPlaza(codigoTipoPlaza);

                            if(con.getCodigoContribuyente().isEmpty()){
                                Log.d("eee","ee");
                            }

                            PropietarioPlaza pp= new PropietarioPlaza();
                            pp.setCodigoPropietarioPlaza(jo.getString("codigoPropietarioPlaza"));
                            if(pp.getCodigoPropietarioPlaza()!=null && !pp.getCodigoPropietarioPlaza().isEmpty()) {
                                pp.setVigenciaInicial(spdf.parse(jo.getString("vigenciaInicial")));
                                pp.setInServerPropietarioPlaza(true);
                                pp.setVigenciaFinal(spdf.parse(jo.getString("vigenciaFinal")));
                                pp.setImporte(!jo.getString("importe").isEmpty()?jo.getDouble("importe"):null);
                                pp.setPlaza(plaza);
                                pp.setTipoPlaza(tiposPlaza.get(tiposPlaza.indexOf(tt)));
                                pp.setContribuyente(con);
                                pp.setGiro(jo.getString("giro"));
                                if(!aux.getPropietarioPlazas().contains(pp)){
                                    aux.getPropietarioPlazas().add(pp);
                                }
                            }







                        }
                        return  aux;

                    }
                    catch(Exception e){
                        Log.e("",e.toString());
                        return null;
                    }

            }


        } catch (IOException e) {
            Log.e("Json Exception", e.toString());
            return null;
        }

        return  null;
    }

    /**
     * Sólo se recuperarán la contribuciones a partir de la fecha, por el tipo de plaza y que no se hayan subido por este equipo, quedarán marcadas como subidas
     * @param url
     * @param timeOut
     * @param fechaInicial
     * @param codigoTP
     * @param codigoEquipoRecaudador
     * @return
     */
    protected List<Contribucion> getContribucionesRemotas(String url, int timeOut, Date fechaInicial, String codigoTP, String codigoEquipoRecaudador) {
        SimpleDateFormat spdf=  new SimpleDateFormat("yyyy-MM-dd");
        List<Contribucion> contribucionesR= new ArrayList<>();
        url=URL_BASE+"getContribuciones";
        timeOut=30;
        HttpURLConnection c = null;


        String result = null;


        InputStream inputStream = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("POST");
            c.setDoInput(true);


            OutputStreamWriter writer = new OutputStreamWriter(
                    c.getOutputStream());
            writer.write("fechaRecuperar"+"="+URLEncoder.encode(spdf.format(fechaInicial),"UTF-8"));
            writer.write("&codigoTipoPlaza"+"="+URLEncoder.encode(codigoTP,"UTF-8"));
            writer.write("&codigoEquipo"+"="+URLEncoder.encode(codigoEquipoRecaudador,"UTF-8"));

            writer.flush();
            writer.close();


            c.connect();

            int status = c.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    inputStream = c.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                    try {
                        JSONArray json = new JSONArray(result);

                        for(int i=0;i<json.length();i++){

                            JSONObject jo= (JSONObject) json.get(i);

                            Contribuyente con= new Contribuyente();
                            con.setCodigoContribuyente(jo.getString("codigoContribuyente"));


                            Plaza plaza= new Plaza();
                            plaza.setInServerPlaza(true);
                            plaza.setCodigoPlaza(jo.getString("codigoPlaza"));


                            TipoPlaza tt= new TipoPlaza();
                            String codigoTipoPlaza= jo.getString("ctipoPlaza");
                            tt.setCodigoTipoPlaza(codigoTipoPlaza);

                            EquipoRecaudador eq= new EquipoRecaudador();
                            eq.setCodigoEquipo(jo.getString("codigoEquipo"));

                            Recaudador rec= new Recaudador();
                            rec.setCodigoRecaudador(jo.getString("codigoRecaudador"));


                            Contribucion contribucion= new Contribucion();
                            contribucion.setPlaza(plaza);
                            contribucion.setTipoPlaza(tt);
                            contribucion.setContribuyente(con);
                            contribucion.setFolioDedicado("R_"+jo.getString("folioDedicado"));
                            contribucion.setEquipoRecaudador(eq);
                            contribucion.setFecha(new SimpleDateFormat("yyyy-MM-dd").parse(jo.getString("fContribucion")));
                            contribucion.setFechaModificacion(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(jo.getString("fModificacion")));
                            contribucion.setImporte(Double.parseDouble(jo.getString("importe")));
                            contribucion.setEstadoServidor(true);
                            contribucion.setEstadoPago(Integer.parseInt(jo.getString("estadoPago")));
                            contribucion.setRecaudador(rec);
                            contribucion.setEstadoServidor(true);
                            contribucionesR.add(contribucion);







                        }
                        return  contribucionesR;

                    }
                    catch(Exception e){
                        Log.e("",e.toString());
                        return null;
                    }

            }


        } catch (IOException e) {
            Log.e("Json Exception", e.toString());
            return null;
        }

        return  null;
    }




    protected List<Recaudador> getRecaudadores(String url, int timeOut) {
        List<Recaudador> recaudadores= new ArrayList<>();
        url=URL_BASE+"getRecaudadores";
        timeOut=30;
        HttpURLConnection c = null;


        String result = null;


        InputStream inputStream = null;
        try {
            URL u = new URL(url);
            c = (HttpURLConnection) u.openConnection();

            c.connect();

            int status = c.getResponseCode();
            switch (status) {
                case 200:
                case 201:
                    inputStream = c.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                    try {
                        JSONArray json = new JSONArray(result);
                        for(int i=0;i<json.length();i++){

                            JSONObject jo= (JSONObject) json.get(i);
                            Recaudador recaudador= new Recaudador();
                            recaudador.setCodigoRecaudador(jo.getString("codigoR"));
                            recaudador.setPassRecaudador(jo.getString("passR"));
                            recaudador.setNombreRecaudador(URLDecoder.decode(jo.getString("nombreR"),"UTF-8"));
                            recaudador.setActivoRecaudador(jo.getString("activoR").equals("1")?true:false);
                            RecaudadorTipoPlaza rtp= new RecaudadorTipoPlaza();
                            rtp.setRecaudador(recaudador);
                            TipoPlaza tp= new TipoPlaza();
                            tp.setCodigoTipoPlaza(jo.getString("tipoPlaza"));
                            rtp.setTipoPlaza(tp);
                            if(!tp.getCodigoTipoPlaza().isEmpty()) {

                                int pos = recaudadores.indexOf(recaudador);

                                //si no está en la lista hay que agregarla junto con su vigencia de ser el caso
                                if (pos < 0) {
                                    recaudadores.add(recaudador);
                                    recaudadores.get(recaudadores.size() - 1).getTipoPlazas().add(rtp);
                                } else {
                                    recaudadores.get(pos).getTipoPlazas().add(rtp);

                                }
                            }
                            else{
                                recaudadores.add(recaudador);
                            }



                        }
                        return  recaudadores;

                    }
                    catch(Exception e){
                        Log.e("",e.toString());
                        return null;
                    }

            }


        } catch (IOException e) {
            Log.e("Json Exception", e.toString());
            return null;
        }

        return  null;
    }




    public JSONArray insertarContribucionesServidor(JSONArray aj){
        String requestURL=URL_BASE+"insert_contribuciones";
        //String requestURL="http://192.168.1.82/combustible/insert_cargo.php";



        String response = "";
        try {
            URL url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            OutputStreamWriter writer = new OutputStreamWriter(
                    conn.getOutputStream());
            writer.write("contribuciones"+"="+URLEncoder.encode(aj.toString(),"UTF-8"));

            writer.flush();
            writer.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                if(response.equals("correcto")){
                    return aj;
                }
                else{
                 return  null;
                }
            }
            else {
                response="";
                return null;

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }


    public String insertarEquipoRecaudadorServidor(String jsonEquipoRecaudador){
        String requestURL=URL_BASE+"insert_equipo_recaudador";
        //String requestURL="http://192.168.1.82/combustible/insert_cargo.php";



        String response = "";
        try {
            URL url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            OutputStreamWriter writer = new OutputStreamWriter(
                    conn.getOutputStream());
            writer.write("equipoRecaudador"+"="+URLEncoder.encode(jsonEquipoRecaudador.toString(),"UTF-8"));

            writer.flush();
            writer.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                if(response.equals("correcto")){
                    return jsonEquipoRecaudador;
                }
                else{
                    return  null;
                }
            }
            else {
                response="";
                return null;

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public JSONArray insertarPlazas(JSONArray aj){
        String requestURL=URL_BASE+"insert_plazas";
        String response = "";
        try {
            URL url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            OutputStreamWriter writer = new OutputStreamWriter(
                    conn.getOutputStream());
            String user="cobranza_89";
            writer.write("user"+"="+URLEncoder.encode(user,"UTF-8"));
            writer.write("&plazas"+"="+URLEncoder.encode(aj.toString(),"UTF-8"));

            writer.flush();
            writer.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                if(response.equals("correcto")){
                    return aj;
                }
                else{
                    return  null;
                }
            }
            else {
                response="";
                return null;

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public JSONArray insertarContribuyentes(JSONArray aj){
        String requestURL=URL_BASE+"insert_contribuyentes";
        String response = "";
        try {
            URL url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            OutputStreamWriter writer = new OutputStreamWriter(
                    conn.getOutputStream());
            writer.write("user"+"="+URLEncoder.encode("cobranza_89","UTF-8"));
            writer.write("&contribuyentes"+"="+URLEncoder.encode(aj.toString(),"UTF-8"));

            writer.flush();
            writer.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                if(response.equals("correcto")){
                    return aj;
                }
                else{
                    return  null;
                }
            }
            else {
                response="";
                return null;

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    public JSONArray insertarPropietarioPlaza(JSONArray aj){
        String requestURL=URL_BASE+"insert_propietario_plaza";
        String response = "";
        try {
            URL url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);

            conn.setRequestProperty("Accept-Charset", "UTF-8");
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            OutputStreamWriter writer = new OutputStreamWriter(
                    conn.getOutputStream());
            writer.write("user"+"="+URLEncoder.encode("cobranza_89","UTF-8"));
            writer.write("&propietarioplaza"+"="+URLEncoder.encode(aj.toString(),"UTF-8"));



            writer.flush();
            writer.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                if(response.equals("correcto")){
                    return aj;
                }
                else{
                    return  null;
                }
            }
            else {
                response="";
                return null;

            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }


    }

    /*
    el detalle aquí fue que no se pudo activar la variable allow_url_fopen que la que permite tratar como una rchivo y obtener el contenido de la pagina
    public boolean insertarCargoServidor(JSONObject aj){
        String requestURL="https://jalpa.gob.mx/combustible/insert_cargo.php";



        String response = "";
        try {
            URL url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);

            //conn.setUseCaches (false);
            //conn.setFixedLengthStreamingMode(aj.toString().getBytes().length);

            //conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

            //byte[] postDataBytes = aj.toString().getBytes("UTF-8");
            //conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes));

            conn.connect();



            DataOutputStream os = new DataOutputStream(conn.getOutputStream());
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            String code= aj.toString();
            writer.write(code);

            os.flush();
            os.close();

            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
                Log.e("respuestaaa",response);
                return true;
            }
            else {
                response="";
                return false;

            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }*/



}
