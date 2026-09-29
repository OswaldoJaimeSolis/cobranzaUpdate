package com.jalpa.cobranza.busqueda.contribuciones;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jalpa.cobranza.MainActivity;
import com.jalpa.cobranza.R;
import com.jalpa.cobranza.busqueda.BusquedaContribuyente;
import com.jalpa.cobranza.model.dao.Database;
import com.jalpa.cobranza.model.entidades.Contribucion;
import com.jalpa.cobranza.model.entidades.PropietarioPlaza;
import com.jalpa.cobranza.model.entidades.Recaudador;
import com.jalpa.cobranza.model.entidades.TipoPlaza;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class BusquedaContribuciones extends AppCompatActivity {
    private EditText etFechaCB;
    private Spinner cbTipoPlazaCB;
    private Spinner cbEstadoCB;
    private List<TipoPlaza> tiposPlaza;
    private ListView lvContribuciones;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private ArrayList<Contribucion> contribuciones;
    ContribucionAdapter contribucionAdapter;

    private final static int ESTADO_PAGADO=1;
    private final static int ESTADO_PENDIENTE=2;
    private final static int ESTADO_AUSENTE=3;
    private final static int ESTADO_SIN_VERIFICAR=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_contribuciones);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        etFechaCB= findViewById(R.id.etDateCB);
        cbTipoPlazaCB= findViewById(R.id.cbTipoPlazasBC);
        cbEstadoCB= findViewById(R.id.cbEstadoCB);
        lvContribuciones= findViewById(R.id.lvContribuciones);

        tiposPlaza=(List<TipoPlaza>)getIntent().getExtras().get("tiposPlaza");
        ArrayAdapter adapterPP= new ArrayAdapter<TipoPlaza>(this, android.R.layout.simple_spinner_item, tiposPlaza);
        cbTipoPlazaCB.setAdapter(adapterPP);
        cbTipoPlazaCB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cargarContribuciones();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        List<String> estados= new ArrayList<>();
        estados.add("PAGADO");
        estados.add("PENDIENTE");
        estados.add("AUSENTE");
        estados.add("SIN REGISTRAR");
        ArrayAdapter adapterEstados= new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,estados);
        cbEstadoCB.setAdapter(adapterEstados);

        cbEstadoCB.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cargarContribuciones();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        etFechaCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal= Calendar.getInstance();
                int year=2019;
                int month=8;
                int day=1;
                if(etFechaCB.getText().toString().isEmpty()) {
                     year = cal.get(Calendar.YEAR);
                     month = cal.get(Calendar.MONTH);
                     day = cal.get(Calendar.DAY_OF_MONTH);

                }
                else{
                    String[] fecha= etFechaCB.getText().toString().split("/");
                    day= Integer.parseInt(fecha[0]);
                    month= Integer.parseInt(fecha[1])-1;
                    year= Integer.parseInt(fecha[2]);
                }

                DatePickerDialog dialog = new DatePickerDialog(BusquedaContribuciones.this, android.R.style.Theme_Holo_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        etFechaCB.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

                cargarContribuciones();

            }
        });

        setCurrentDateOnView();
        mDateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month= month+1;
                etFechaCB.setText(day+"/"+month+"/"+year);
            }
        };
    }

    private void cargarContribuciones(){
        new CargaContribuyentes().execute();
    }

    private void setCurrentDateOnView() {
        int year;
        int month;
        int day;


        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        etFechaCB.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append("/").append(month+1).append("/")
                .append(year));



    }

    private int recuperaEstado(){
        if(cbEstadoCB.getSelectedItem().equals("PAGADO")){
            return ESTADO_PAGADO;
        }
        if(cbEstadoCB.getSelectedItem().equals("PENDIENTE")){
            return ESTADO_PENDIENTE;
        }
        if(cbEstadoCB.getSelectedItem().equals("AUSENTE")){
            return ESTADO_AUSENTE;
        }
        return ESTADO_SIN_VERIFICAR;
    }

    private class CargaContribuyentes extends AsyncTask<Void, Void, Void > {
        Integer estado;
        String tipoPlaza;
        Date fecha;
        //Date fechaFinal;

        public CargaContribuyentes() {
            estado= recuperaEstado();
            tipoPlaza= cbTipoPlazaCB.getSelectedItem().toString();

            SimpleDateFormat format= new SimpleDateFormat("dd/MM/yyyy");
            try {
                fecha =format.parse(etFechaCB.getText().toString());
                /*Calendar cal= Calendar.getInstance();
                cal.setTime(fecha);
                cal.set(Calendar.DAY_OF_MONTH,cal.get(Calendar.DAY_OF_MONTH)+1);
                fechaFinal= cal.getTime();*/
            }
            catch (ParseException e){

            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();



        }
        @Override
        protected Void doInBackground(Void... voids) {
            if(estado!=null && tipoPlaza!=null && fecha!=null  ) {
                if (estado != ESTADO_SIN_VERIFICAR) {
                    //List<Contribucion> tmplist= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getContribucionesAll();
                    contribuciones = new ArrayList<Contribucion>(Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getContribuciones(tipoPlaza, estado,fecha));

                } else {
                    //revisar para que solo regrese los que no estan
                    List<PropietarioPlaza> propietarios = Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getPlazasSinRevisar(tipoPlaza, fecha);
                    if (propietarios.size() > 0) {
                        ArrayList<Contribucion> cont = new ArrayList<>();
                        for (PropietarioPlaza pp : propietarios) {
                            Contribucion con = new Contribucion();
                            con.setContribuyente(pp.getContribuyente());
                            con.setTipoPlaza(pp.getTipoPlaza());
                            con.setPlaza(pp.getPlaza());
                            cont.add(con);
                        }
                        contribuciones = cont;
                    } else {
                        return null;
                    }

                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contribucionAdapter=new ContribucionAdapter(BusquedaContribuciones.this, contribuciones);
            lvContribuciones.setAdapter(contribucionAdapter);


        }
    }


}
