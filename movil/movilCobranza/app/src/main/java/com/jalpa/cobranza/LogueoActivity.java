package com.jalpa.cobranza;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.List;

public class LogueoActivity extends AppCompatActivity {
    private EditText etUsuario;
    private EditText etPassword;
    private Button btnConfirmarUsuario;
    private ProgressBar pbActualizaRecaudadores;
    private List<Recaudador> recaudadores;


    private WebService webService;



    int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logueo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        RecaudadorActivo.setRecaudadorActivo(null);
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


        FloatingActionButton fab = findViewById(R.id.fabActualizarRecaudadores);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ActualizarCatalogosRemoto().execute();
            }
        });
        enablePermisos();




    }
    private void validarUsuario(){
        String codigoRecaudador= etUsuario.getText().toString();
        String passRecaudador= etPassword.getText().toString();
        if(!codigoRecaudador.isEmpty() || !passRecaudador.isEmpty()) {
            if (recaudadores != null && recaudadores.size()>0) {
                boolean valido=false;
                for (Recaudador rec : recaudadores) {
                    if (rec.getCodigoRecaudador().equals(codigoRecaudador) && rec.getPassRecaudador().equals(passRecaudador)){
                        if(rec.getActivoRecaudador()){
                            RecaudadorActivo.setRecaudadorActivo(rec);
                            valido=true;
                            finish();
                        }
                        else{
                            Toast.makeText(getApplicationContext(),"El usuario no está activo, avise al administrador", Toast.LENGTH_SHORT);
                            limpiarCajas();
                            break;
                        }

                    }

                }
                if(!valido) {
                    Toast.makeText(getApplicationContext(), "Los datos son incorrectos o el usuario no existe", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(getApplicationContext(), "Actualiza los recaudadores", Toast.LENGTH_SHORT).show();
                limpiarCajas();
            }
        }
        else{
            Toast.makeText(getApplicationContext(), "Llena los datos correctamente", Toast.LENGTH_SHORT).show();
            limpiarCajas();
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

            actualizaRecaudadores();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pbActualizaRecaudadores.setVisibility(View.INVISIBLE);
            if(recaudadores==null || recaudadores.size()==0){
                Toast.makeText(getApplicationContext(), "Verifique su conexión a internet", Toast.LENGTH_LONG).show();
            }

        }
    }
    private void actualizaRecaudadores() {
        List<Recaudador> recaudadores = getWebService().getRecaudadores("", 100);
        if (recaudadores != null && recaudadores.size() > 0) {
            Database.getInstance(this).getAppDatabase().operacionesDao().borrarInsertarRecaudadoresWithTipoPlaza(recaudadores);
            this.recaudadores= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getAllRecaudadores();
        }
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
