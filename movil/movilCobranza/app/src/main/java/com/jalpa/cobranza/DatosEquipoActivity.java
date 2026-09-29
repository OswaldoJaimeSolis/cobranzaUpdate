package com.jalpa.cobranza;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jalpa.cobranza.model.dao.AppDataBase;
import com.jalpa.cobranza.model.dao.Database;
import com.jalpa.cobranza.model.entidades.EquipoRecaudador;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DatosEquipoActivity extends AppCompatActivity {

    private EditText etCodigoEquipo;
    private EditText etDescripcionEquipo;
    private Button btnGuardarEquipo;

    private EquipoRecaudador equipoRecaudador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_equipo);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        equipoRecaudador=(EquipoRecaudador) getIntent().getExtras().get("recaudadorEquipo");
        etCodigoEquipo= findViewById(R.id.etCodigoEquipo);
        etDescripcionEquipo= findViewById(R.id.etDescripcionEquipo);
        btnGuardarEquipo= findViewById(R.id.btnGuardarEquipo);
        btnGuardarEquipo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarEquipo();
            }
        });
        mostrarDatos();
    }
    private void mostrarDatos(){
        if(equipoRecaudador!=null){
            etCodigoEquipo.setText(equipoRecaudador.getCodigoEquipo());
            etDescripcionEquipo.setText(equipoRecaudador.getDescripcionEquipo());
        }
    }

    private boolean validarIgualAnterior(EquipoRecaudador eq){
        if(equipoRecaudador==null){
            return false;
        }
        if(eq.getCodigoEquipo().equals(equipoRecaudador.getCodigoEquipo()) && eq.getDescripcionEquipo().equals(equipoRecaudador.getDescripcionEquipo()) ){
            return true;
        }
        return  false;
    }

    private boolean validarDatos(){
        if(etDescripcionEquipo.getText().toString().isEmpty() ||etCodigoEquipo.getText().toString().isEmpty()){
            Toast.makeText(getApplicationContext(), "Llene todos los campos", Toast.LENGTH_LONG).show();
        }
        return  true;
    }
    private void guardarEquipo(){
        if(validarDatos()){

            EquipoRecaudador equipoRecaudador= new EquipoRecaudador();
            equipoRecaudador.setCodigoEquipo(etCodigoEquipo.getText().toString());
            equipoRecaudador.setDescripcionEquipo(etDescripcionEquipo.getText().toString());
            equipoRecaudador.setInServer(false);
            if(!validarIgualAnterior(equipoRecaudador)) {
                new ActualizarInsertarNombreEquipo().execute(equipoRecaudador);
            }
            else{
                terminarActivity(equipoRecaudador);
            }

        }
    }

    private void terminarActivity(EquipoRecaudador equipoRecaudador){
        Intent  intent= getIntent();
        intent.putExtra("equipoRecaudador", equipoRecaudador);
        setResult(RESULT_OK, intent);
        finish();
    }

    private class ActualizarInsertarNombreEquipo extends AsyncTask<EquipoRecaudador, Void, EquipoRecaudador>{
        @Override
        protected EquipoRecaudador doInBackground(EquipoRecaudador... equipoRecaudadors) {
            try {
                EquipoRecaudador equipoRecaudador = equipoRecaudadors[0];
                Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().borrarEquipoRecaudador();
                Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().insertarUpdateEquipoRecaudador(equipoRecaudador);
                EquipoRecaudador eq= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().recuperarEquipoRecaudador();
                return equipoRecaudador;
            }
            catch (Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(EquipoRecaudador equipoRecaudador) {
            super.onPostExecute(equipoRecaudador);
            if(equipoRecaudador!=null){
                Toast.makeText(getApplicationContext(), "Se ha guardado correctamente", Toast.LENGTH_LONG);
                terminarActivity(equipoRecaudador);
            }
            else{
                Toast.makeText(getApplicationContext(), "Hubo un problema", Toast.LENGTH_SHORT);
            }
        }
    }

}
