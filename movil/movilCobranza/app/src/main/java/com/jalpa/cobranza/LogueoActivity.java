package com.jalpa.cobranza;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.jalpa.cobranza.model.dao.Database;
import com.jalpa.cobranza.model.entidades.Recaudador;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

public class LogueoActivity extends AppCompatActivity {
    private EditText etUsuario;
    private EditText etPassword;
    private Button btnConfirmarUsuario;
    private ProgressBar pbActualizaRecaudadores;
    private List<Recaudador> recaudadores;


    private WebService webService;



    int PERMISSION_ALL = 1;

    /**
     * Los permisos que hay que pedir en tiempo de ejecución dependen de la versión de
     * Android: desde API 29 la app sólo escribe en su propio directorio externo (no
     * necesita permisos de almacenamiento) y desde API 31 el Bluetooth clásico requiere
     * BLUETOOTH_CONNECT en lugar de los antiguos BLUETOOTH / BLUETOOTH_ADMIN, que ya no
     * son permisos solicitables, y BLUETOOTH_SCAN porque el SDK de Bixolon llama a
     * cancelDiscovery() al abrir la impresora.
     */
    private static String[] permisosRequeridos() {
        List<String> permisos = new ArrayList<>();
        permisos.add(Manifest.permission.CAMERA);
        permisos.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permisos.add(Manifest.permission.ACCESS_FINE_LOCATION);
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            permisos.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2) {
            permisos.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permisos.add(Manifest.permission.BLUETOOTH_CONNECT);
            permisos.add(Manifest.permission.BLUETOOTH_SCAN);
        } else {
            permisos.add(Manifest.permission.BLUETOOTH);
        }
        return permisos.toArray(new String[0]);
    }

    String[] PERMISSIONS = permisosRequeridos();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logueo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecaudadorActivo.setRecaudadorActivo(null);
        Sesion.init(this);
        Sesion.cerrar();
        recaudadores=(List<Recaudador>)getIntent().getExtras().get("recaudadores");

        etUsuario= findViewById(R.id.etUsuario);
        etPassword= findViewById(R.id.etPass);
        btnConfirmarUsuario= findViewById(R.id.btnConfirmarUsuario);
        pbActualizaRecaudadores= findViewById(R.id.pbActualizaRecaudadores);

        btnConfirmarUsuario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validarUsuario();
            }
        });


        Button fab = findViewById(R.id.fabActualizarRecaudadores);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(etUsuario.getText().toString().trim().isEmpty() || etPassword.getText().toString().isEmpty()){
                    Avisos.error(LogueoActivity.this, "Datos incompletos", "Escribe tu usuario y contraseña para descargar los recaudadores");
                    return;
                }
                new ActualizarCatalogosRemoto().execute();
            }
        });
        enablePermisos();




    }
    /**
     * El inicio de sesión es local: compara contra el hash descargado del servidor, así que
     * funciona sin conexión. Sólo si no coincide localmente (recaudador nuevo, contraseña
     * cambiada o primera vez en el equipo) se intenta con el servidor y, si acepta, se
     * vuelven a descargar los recaudadores.
     */
    private void validarUsuario(){
        String codigoRecaudador= etUsuario.getText().toString().trim();
        String passRecaudador= etPassword.getText().toString();
        if(codigoRecaudador.isEmpty() || passRecaudador.isEmpty()) {
            Avisos.error(this, "Datos incompletos", "Llena los datos correctamente");
            return;
        }
        Recaudador rec= buscarLocal(codigoRecaudador, passRecaudador);
        if(rec!=null){
            entrar(rec, passRecaudador);
            return;
        }
        new ValidarEnServidor(codigoRecaudador, passRecaudador).execute();
    }

    private Recaudador buscarLocal(String codigoRecaudador, String passRecaudador){
        if(recaudadores!=null){
            for (Recaudador rec : recaudadores) {
                if (rec.getCodigoRecaudador().equals(codigoRecaudador) && Contrasenas.coincide(passRecaudador, rec.getPassRecaudador())){
                    return rec;
                }
            }
        }
        return null;
    }

    private void entrar(Recaudador rec, String passRecaudador){
        if(!Boolean.TRUE.equals(rec.getActivoRecaudador())){
            Avisos.error(this, "Usuario inactivo", "El usuario no está activo, avise al administrador");
            limpiarCajas();
            return;
        }
        Sesion.setCredenciales(rec.getCodigoRecaudador(), passRecaudador);
        RecaudadorActivo.setRecaudadorActivo(rec);
        finish();
    }

    private class ValidarEnServidor extends AsyncTask<Void, Void, Integer> {
        private final String codigoRecaudador;
        private final String passRecaudador;

        ValidarEnServidor(String codigoRecaudador, String passRecaudador){
            this.codigoRecaudador= codigoRecaudador;
            this.passRecaudador= passRecaudador;
        }

        @Override
        protected void onPreExecute() {
            pbActualizaRecaudadores.setVisibility(View.VISIBLE);
            pbActualizaRecaudadores.bringToFront();
            btnConfirmarUsuario.setEnabled(false);
        }

        @Override
        protected Integer doInBackground(Void... voids) {
            int resultado= getWebService().login(codigoRecaudador, passRecaudador);
            if(resultado==WebService.LOGIN_CORRECTO){
                actualizaRecaudadores();
            }
            return resultado;
        }

        @Override
        protected void onPostExecute(Integer resultado) {
            pbActualizaRecaudadores.setVisibility(View.INVISIBLE);
            btnConfirmarUsuario.setEnabled(true);
            Recaudador rec= resultado==WebService.LOGIN_CORRECTO? buscarLocal(codigoRecaudador, passRecaudador): null;
            if(rec!=null){
                entrar(rec, passRecaudador);
            }
            else if(resultado==WebService.LOGIN_SIN_CONEXION && (recaudadores==null || recaudadores.isEmpty())){
                Avisos.error(LogueoActivity.this, "Sin conexión", "La primera vez hay que conectarse para descargar los recaudadores");
            }
            else{
                Avisos.error(LogueoActivity.this, "Datos incorrectos", "Los datos son incorrectos o el usuario no existe");
                etPassword.setText("");
            }
        }
    }

    private void enablePermisos(){
        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
    }



    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private class ActualizarCatalogosRemoto extends AsyncTask<Void, Void, Void > {
        private final String usuario= etUsuario.getText().toString().trim();
        private final String password= etPassword.getText().toString();
        private boolean descargados;
        private int resultadoLogin;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pbActualizaRecaudadores.setVisibility(View.VISIBLE);
            pbActualizaRecaudadores.bringToFront();

        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            pbActualizaRecaudadores.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            //descargar los recaudadores requiere token: se pide con el usuario y contraseña escritos
            resultadoLogin= getWebService().login(usuario, password);
            descargados= resultadoLogin==WebService.LOGIN_CORRECTO && actualizaRecaudadores();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pbActualizaRecaudadores.setVisibility(View.INVISIBLE);
            if(descargados){
                Avisos.exito(LogueoActivity.this, "Recaudadores actualizados");
            }
            else if(resultadoLogin==WebService.LOGIN_RECHAZADO){
                Avisos.error(LogueoActivity.this, "No se pudo actualizar", "Usuario o contraseña incorrectos, o recaudador inactivo");
            }
            else if(resultadoLogin==WebService.LOGIN_SIN_CONEXION){
                Avisos.error(LogueoActivity.this, "Sin conexión", "No hay conexión con el servidor");
            }
            else{
                Avisos.error(LogueoActivity.this, "Sin recaudadores", "El servidor no devolvió recaudadores");
            }

        }
    }
    /** true si se descargaron y guardaron los recaudadores (requiere token). */
    private boolean actualizaRecaudadores() {
        List<Recaudador> recaudadores = getWebService().getRecaudadores("", 100);
        if (recaudadores != null && recaudadores.size() > 0) {
            Database.getInstance(this).getAppDatabase().operacionesDao().borrarInsertarRecaudadoresWithTipoPlaza(recaudadores);
            this.recaudadores= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getAllRecaudadores();
            return true;
        }
        return false;
    }

    private void limpiarCajas(){
        etUsuario.setText("");
        etPassword.setText("");
        etUsuario.requestFocus();
    }

    public WebService getWebService() {
        if(webService==null){
            webService= new WebService();
        }
        return webService;
    }

}
