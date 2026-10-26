package com.jalpa.cobranza;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.jalpa.cobranza.model.dao.AppDataBase;
import com.jalpa.cobranza.model.dao.Database;
import com.jalpa.cobranza.model.entidades.EquipoRecaudador;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class DatosEquipoActivity extends AppCompatActivity {

    private EditText etCodigoEquipo;
    private EditText etDescripcionEquipo;
    private Button btnGuardarEquipo;

    private EquipoRecaudador equipoRecaudador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_equipo);
        Pantalla.configurarBarra(this, true);
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
            Avisos.error(this, "Datos incompletos", "Llene todos los campos");
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
                terminarActivity(equipoRecaudador);
            }
            else{
                Avisos.error(DatosEquipoActivity.this, "Error", "Hubo un problema al guardar los datos del equipo");
            }
        }
    }

}
