package com.jalpa.cobranza.contribuyente;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.jalpa.cobranza.R;
import com.jalpa.cobranza.RecaudadorActivo;
import com.jalpa.cobranza.model.dao.Database;
import com.jalpa.cobranza.model.entidades.Contribuyente;
import com.jalpa.cobranza.model.entidades.Plaza;
import com.jalpa.cobranza.model.entidades.PropietarioPlaza;
import com.jalpa.cobranza.model.entidades.Recaudador;
import com.jalpa.cobranza.model.entidades.TipoPlaza;
import com.jalpa.cobranza.model.entidades.TipoPlazaHistorial;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ContribuyenteActivity extends AppCompatActivity {
    private EditText etCNombre;
    private EditText etCApePaterno;
    private EditText etCApeMaterno;
    private EditText etCFechaNacimiento;
    private EditText etCRfc;
    private CheckBox cbSinFechaNacimiento;

    private Spinner cbTipoPlaza;
    private EditText etCImporte;
    private Button btnCrear;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private List<Contribuyente> contribuyentes;
    //private List<Plaza> plazas;
    //private List<PropietarioPlaza> propietarioPlazas;
    private List<TipoPlaza> tiposPlaza;
    //private List<TipoPlazaHistorial> tipoPlazaHistorial;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private GeneradorCodigos generadorCodigos;

    private int year;
    private int month;
    private int day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contribuyente);
        fusedLocationProviderClient= LocationServices.getFusedLocationProviderClient(this);
        //contribuyentes=(List<Contribuyente>)getIntent().getExtras().get("contribuyentes");
        tiposPlaza =(List<TipoPlaza>)getIntent().getExtras().get("tiposPlaza");


        etCNombre= findViewById(R.id.etCNombre);
        etCApePaterno=findViewById(R.id.etCApePaterno);
        etCApeMaterno= findViewById(R.id.etCApeMaterno);
        etCFechaNacimiento= findViewById(R.id.etCFechaNacimiento);
        etCRfc= findViewById(R.id.etCRfc);
        cbTipoPlaza= findViewById(R.id.cbTipoPlaza);
        etCImporte= findViewById(R.id.etCImporte);
        cbSinFechaNacimiento= findViewById(R.id.cbSinFechaNacimiento);
        cbSinFechaNacimiento.setChecked(false);
        btnCrear= findViewById(R.id.btnCrear);
        setCurrentDateOnView();
        mDateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month= month+1;
                etCFechaNacimiento.setText(day+"/"+month+"/"+year);
            }
        };
        etCFechaNacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal= Calendar.getInstance();
                int year= cal.get(Calendar.YEAR);
                int month= cal.get(Calendar.MONTH);
                int day= cal.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog= new DatePickerDialog(ContribuyenteActivity.this,android.R.style.Theme_Holo_Dialog_MinWidth,mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        btnCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearRegistro();
            }
        });
        cargarTiposPlaza();

        etCImporte.setEnabled(false);

        cbTipoPlaza.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                establecerImporte((TipoPlaza) cbTipoPlaza.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                cargarCatalogoContribuyentes();
            }
        }).start();


    }

    private void cargarCatalogoContribuyentes(){
        contribuyentes = Database.getInstance(this).getAppDatabase().operacionesDao().getAllContribuyentes();
    }

    private void cargarTiposPlaza(){
        ArrayAdapter<TipoPlaza> adapterTipos= new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_spinner_item,tiposPlaza);
        cbTipoPlaza.setAdapter(adapterTipos);
        establecerImporte((TipoPlaza) cbTipoPlaza.getSelectedItem());

    }

    private boolean crearRegistro(){

        if(tiposPlaza.size()>0) {
            TipoPlaza tp=(TipoPlaza) cbTipoPlaza.getSelectedItem();
            if (tp.getAddLocal()) {
                Recaudador rec= RecaudadorActivo.getRecaudadorActivo();
                if (RecaudadorActivo.getRecaudadorActivo() != null && RecaudadorActivo.validarAutorizacion(tp)) {

                    boolean creacionCorrecta = true;
                    Contribuyente contribuyente = crearContribuyente();
                    //si el contribuyente es null, el error que haya pasado ya se mostró

                    if (contribuyente != null) {

                        Plaza plaza = crearPlaza(contribuyente, tp);
                        PropietarioPlaza propietarioPlaza = crearPropietarioPlaza(contribuyente, plaza, tp);
                        PlazaTipoContribuyenteContenedor contenedor = new PlazaTipoContribuyenteContenedor(plaza, contribuyente, propietarioPlaza);
                        new CrearContribuyente().execute(contenedor);
                    } else {
                        creacionCorrecta = false;
                    }

                    return creacionCorrecta;
                }
                else{
                    Toast.makeText(getApplicationContext(),"USTED no tiene permiso para crear registros de este tipo", Toast.LENGTH_LONG).show();
                    return  false;
                }
            }
            else{
                Toast.makeText(getApplicationContext(),"NO se permite crear registros de este tipo localmente", Toast.LENGTH_LONG).show();
                return  false;
            }
        }
        else{
            Toast.makeText(getApplicationContext(),"No se puede crear sin tipo de plaza", Toast.LENGTH_LONG).show();
            return false;
        }
    }



    private PropietarioPlaza crearPropietarioPlaza(Contribuyente contribuyente, Plaza plaza, TipoPlaza tipoPlaza){
        PropietarioPlaza pp= new PropietarioPlaza();
        pp.setCodigoPropietarioPlaza(getGeneradorCodigos().getCodigoPropietarioPlaza(contribuyente,plaza));
        pp.setContribuyente(contribuyente);
        pp.setTipoPlaza(tipoPlaza);
        pp.setPlaza(plaza);
        if(!tipoPlaza.getImporteGlobal()){
            String importe= etCImporte.getText().toString();
            if(!importe.isEmpty()){
                pp.setImporte(Double.parseDouble(importe));
            }
        }

        Calendar calendar= Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);

        pp.setVigenciaInicial(calendar.getTime());
        pp.setVigenciaFinal(getFechaFinal());
        pp.setInServerPropietarioPlaza(false);


        return pp;

    }

    private void establecerImporte(TipoPlaza tipoPlaza){
        if(tipoPlaza.getImporteGlobal()){
            etCImporte.setEnabled(false);
            if(tipoPlaza.getHistorial().size()>0){
             etCImporte.setText(String.format("%.2f",tipoPlaza.getHistorial().get(0).getImporte()));
         }
        }
        else{
            etCImporte.setEnabled(true);
        }
    }


    private Date getFechaFinal(){
        Calendar calendar= Calendar.getInstance();
        calendar.set(2099,0,1);
        return calendar.getTime();
    }



    private Plaza crearPlaza(Contribuyente contribuyente, TipoPlaza tipoPlaza){
        final Plaza plaza = new Plaza();
        //plaza.setCodigoPlaza(getGeneradorCodigos().nombrePlaza(contribuyente, tipoPlaza));
        if (ContextCompat.checkSelfPermission( this,android.Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    plaza.setLatitud(location.getLatitude());
                    plaza.setLongitud(location.getLongitude());
                }
            });
        }
        plaza.setInServerPlaza(false);
        return plaza;
    }



    private Contribuyente crearContribuyente(){
        String respuestaValidacion= validarDatos();
        if(respuestaValidacion.equals("OK")){
            String apePaterno= etCApePaterno.getText().toString();
            String apeMaterno= etCApeMaterno.getText().toString();
            String nombre= etCNombre.getText().toString();
            String rfc= etCRfc.getText().toString();
            String fechaNacimiento="";
            if(!cbSinFechaNacimiento.isChecked()) {
                fechaNacimiento = etCFechaNacimiento.getText().toString();
            }
            Contribuyente contribuyente= new Contribuyente();
            contribuyente.setNombre(nombre);
            contribuyente.setApeMaterno(apeMaterno);
            contribuyente.setApePaterno(apePaterno);
            if(rfc.isEmpty()){
                contribuyente.setCodigoContribuyente(getGeneradorCodigos().generarCodigoContribuyente(apePaterno, apeMaterno, nombre,fechaNacimiento ));
            }
            else{
                contribuyente.setRfc(etCRfc.getText().toString());
                contribuyente.setCodigoContribuyente(etCRfc.getText().toString());
            }

            //validar si no está dado de alta
            if(inLista(contribuyente)){
                Toast.makeText(this,"El contribuyente ya estaba registrado",Toast.LENGTH_SHORT).show();
            }
            else{
                return contribuyente;
            }


            return null;


        }
        else{
            Toast.makeText(this, respuestaValidacion,Toast.LENGTH_SHORT).show();
        }
        return null;
    }



    private boolean inLista(Contribuyente contribuyente){
        String codeContribuyente= contribuyente.getCodigoContribuyente().toLowerCase();
        for(Contribuyente con: contribuyentes){
            String codeList= con.getCodigoContribuyente().toLowerCase();
            if(codeList.contains(codeContribuyente)){
                return true;
            }

        }
        return false;
    }



    /**
     * Si el mensaje es OK, todos los datos están llenados correctamente de lo contrario indica qué campo no está llenado correctamente
     * @return
     */
    private String validarDatos(){
        String respuesta="OK";
        if(etCNombre.getText().toString().isEmpty()){
            respuesta= "Indique el nombre";
        }
        else if(etCApePaterno.getText().toString().isEmpty()){
            respuesta="Indique el apellido paterno";
        }
        else if(etCApeMaterno.getText().toString().isEmpty()){
            respuesta="Indique el apellido materno";
        }
        else if(!cbSinFechaNacimiento.isChecked() && etCFechaNacimiento.getText().toString().isEmpty()){
            respuesta="Fecha incorrecta";
        }
        else if(cbTipoPlaza.getSelectedItem().toString().isEmpty()){
            respuesta="Seleccione un tipo de plaza";
        }


        return respuesta;
    }

    private class CrearContribuyente extends AsyncTask<PlazaTipoContribuyenteContenedor, Void, PlazaTipoContribuyenteContenedor>{
        @Override
        protected PlazaTipoContribuyenteContenedor doInBackground(PlazaTipoContribuyenteContenedor... contenedor) {
            PlazaTipoContribuyenteContenedor con= contenedor[0];
            try {
                //Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().insertarContribuyentePlazaPropietario(con.getContribuyente(), con.getPlaza(), con.getPropietarioPlaza());
                Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().insertarContribuyenteNuevo(con.getContribuyente(), con.getPlaza(), con.getPropietarioPlaza());

                return  con;
            }
            catch(Exception e) {
                return null;
            }
        }

        @Override
        protected void onPostExecute(PlazaTipoContribuyenteContenedor con) {
            super.onPostExecute(con);
            if(con!=null) {
                Toast.makeText(ContribuyenteActivity.this, "Se ha agregado correctamente", Toast.LENGTH_SHORT).show();
                contribuyentes.add(con.getContribuyente());
                //plazas.add(con.getPlaza());
                //propietarioPlazas.add(con.getPropietarioPlaza());

                Intent intent = getIntent();
                intent.putExtra("plaza", con.getPlaza());
                intent.putExtra("contribuyente", con.getContribuyente());
                intent.putExtra("propietarioPlaza", con.getPropietarioPlaza());
                setResult(RESULT_OK, intent);
                finish();

            }
            else{
                Toast.makeText(ContribuyenteActivity.this, "Hubo un problema, intentalo de nuevo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setCurrentDateOnView() {



        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        etCFechaNacimiento.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append("/").append(month+1).append("/")
                .append(year));



    }

    private GeneradorCodigos getGeneradorCodigos(){
        if(generadorCodigos==null){
            generadorCodigos= new GeneradorCodigos();

        }
        return  generadorCodigos;
    }


}
