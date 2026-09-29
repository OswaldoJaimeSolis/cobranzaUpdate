package com.jalpa.cobranza;

import android.Manifest;
import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;


import com.bxl.BXLConst;
import com.bxl.config.editor.BXLConfigLoader;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.jalpa.cobranza.busqueda.BusquedaContribuyente;
import com.jalpa.cobranza.busqueda.contribuciones.BusquedaContribuciones;
import com.jalpa.cobranza.contribuyente.ContribuyenteActivity;
import com.jalpa.cobranza.model.AuxActualizacionRemota;
import com.jalpa.cobranza.model.dao.AppDataBase;
import com.jalpa.cobranza.model.dao.Database;
import com.jalpa.cobranza.model.entidades.Contribucion;
import com.jalpa.cobranza.model.entidades.Contribuyente;
import com.jalpa.cobranza.model.entidades.EquipoRecaudador;
import com.jalpa.cobranza.model.entidades.Folio;
import com.jalpa.cobranza.model.entidades.Plaza;
import com.jalpa.cobranza.model.entidades.PropietarioPlaza;
import com.jalpa.cobranza.model.entidades.Recaudador;
import com.jalpa.cobranza.model.entidades.RecaudadorTipoPlaza;
import com.jalpa.cobranza.model.entidades.TipoPlaza;
import com.jalpa.cobranza.model.entidades.TipoPlazaHistorial;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import jpos.JposException;
import jpos.POSPrinter;
import jpos.POSPrinterConst;

import static android.os.Environment.getExternalStoragePublicDirectory;


public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_SCAN_CONTRIBUYENTE=1;
    private static final int REQUEST_BUSCAR_CONTRIBUYENTE=2;
    private static final int REQUEST_AGREGAR_CONTRIBUYENTE=3;
    private static final int REQUEST_VALIDAR_RECAUDADOR=4;
    private static final int REQUEST_ACTUALIZAR_DATOS_EQUIPO_RECAUDADOR=5;
    private static final int REQUEST_RECUPERAR_CONTRIBUCIONES_REMOTE=6;


    private ImageButton btnScanContribuyente;
    private EditText etContribuyente;
    private EditText etFecha;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    private Spinner cbPlazas;
    private EditText etImporte;
    private Button btnConfirmar;
    private Button btnReimprimir;
    private Button btnGenerarQR;
    private RadioGroup rgEstado;
    private RadioButton rbPagado;
    private RadioButton rbPendiente;
    private RadioButton rbAusente;
    private ProgressBar pbActualizarCatalogos;
    private ProgressBar pbBusqueda;
    private SimpleDateFormat sdf;

    private WebService service;

    private List<Recaudador> recaudadores;
    private List<TipoPlaza> tiposPlaza;
    private List<Contribuyente> contribuyentes;
    private EquipoRecaudador equipoRecaudador;

    private List<PropietarioPlaza> propietarioPlazas;

    private int year;
    private int month;
    private int day;

    private static final int ESTADO_PAGADO=1;
    private static final int ESTADO_PENDIENTE=2;
    private static final int ESTADO_AUSENTE=3;

    private Imprimir imprimir;

    private Contribucion contribucionGlobal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //enableRuntimePermission();
        new Thread(new Runnable() {
            @Override
            public void run() {
                //imprimirTicket("hola");
                /*Calendar cal= Calendar.getInstance();
                cal.set(2020,9,26);
                new ActualizarCatalogosRemoto(true, cal.getTime(),"AMBULANTE", "test").execute();*/
                cargarCatalogosLocal();
                Intent intent = new Intent(MainActivity.this, LogueoActivity.class);
                intent.putExtra("recaudadores", (Serializable) recaudadores);
                startActivityForResult(intent, REQUEST_VALIDAR_RECAUDADOR);


            }
        }).start();
        Intent service= new Intent(this, ServiceUpload.class);
        service.putExtra("fozarEnvio", false);
        startService(service);
        //startService(new Intent(this, ServiceUpload.class));



        btnScanContribuyente= (ImageButton) findViewById(R.id.btnScan);
        btnScanContribuyente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ScanActivity.class);

                startActivityForResult(intent, REQUEST_SCAN_CONTRIBUYENTE);
            }
        });
        etContribuyente= findViewById(R.id.etContribuyente);

        etFecha=  findViewById(R.id.etDate);
        cbPlazas= findViewById(R.id.cbPlaza);
        etImporte= findViewById(R.id.etImporte);
        rgEstado= findViewById(R.id.rgEstado);
        rbPagado= findViewById(R.id.rbPagado);
        rbPendiente=findViewById(R.id.rbPendiente);
        rbAusente= findViewById(R.id.rbAusente);
        pbActualizarCatalogos=findViewById(R.id.pbActualizarCatalogos);
        pbBusqueda= findViewById(R.id.pbBusqueda);
        btnConfirmar= findViewById(R.id.btnConfirmar);
        btnGenerarQR= findViewById(R.id.btnGenerarQR);
        btnGenerarQR.setVisibility(View.INVISIBLE);
        btnReimprimir= findViewById(R.id.btnReimprimir);
        btnReimprimir.setVisibility(View.INVISIBLE);

        etFecha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal= Calendar.getInstance();
                int year= cal.get(Calendar.YEAR);
                int month= cal.get(Calendar.MONTH);
                int day= cal.get(Calendar.DAY_OF_MONTH);


                if(!etFecha.getText().toString().isEmpty()) {
                    String[] fecha= etFecha.getText().toString().split("/");
                    day= Integer.parseInt(fecha[0]);
                    month= Integer.parseInt(fecha[1])-1;
                    year= Integer.parseInt(fecha[2]);
                }


                DatePickerDialog dialog= new DatePickerDialog(MainActivity.this,android.R.style.Theme_Holo_Dialog_MinWidth,mDateSetListener,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        etFecha.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {

               contribuyenteFechaCambio();

            }
        });

        setCurrentDateOnView(null);
        mDateSetListener= new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month= month+1;
                etFecha.setText(day+"/"+month+"/"+year);
            }
        };

        etContribuyente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!etContribuyente.getText().toString().isEmpty()){
                    btnGenerarQR.setVisibility(View.VISIBLE);
                }
                else{
                    btnGenerarQR.setVisibility(View.INVISIBLE);
                }
                contribuyenteFechaCambio();

            }
        });
        etContribuyente.setEnabled(false);


        rbPagado.setEnabled(true);
        rbPagado.setChecked(true);
        cbPlazas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                actualizaImporte(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmarContribucion();
            }
        });
        btnReimprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contribucionGlobal!=null) {
                    new ReImprimir().execute(contribucionGlobal);
                }
            }
        });

        btnGenerarQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!etContribuyente.getText().toString().isEmpty()) {
                    new GeneraQR().execute();
                }
            }
        });



    }

    private  class GeneraQR extends  AsyncTask<Void, Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            generarImprimirQR(etContribuyente.getText().toString());
            return null;
        }
    }

    private void generarImprimirQR(String codigoContribuyente){
        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(codigoContribuyente, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            String path=Environment.getExternalStorageDirectory().getPath().concat("/Cobranza");
            File dir= new File(path);
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file= new File(dir,"qr_contribuyente"+".png");
            FileOutputStream fOut = new FileOutputStream(file);

            bmp.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
            imprimirTicket(false,null,path.concat("/qr_contribuyente.png"));


        } catch (WriterException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    /**
     * Si el código del contribuyente es válido  actualiza las plazas del contribuyente en base a la fecha correspondiente
     */
    private void contribuyenteFechaCambio(){
        if(!etContribuyente.getText().toString().isEmpty()){
            String datos[]= etContribuyente.getText().toString().split(" ");
            String codigoContribuyente= datos[0];
            if(validaContribuyente(codigoContribuyente)) {
                new ActualizarPlazasContribuyenteFecha().execute(codigoContribuyente);
            }
            else{
                propietarioPlazas= new ArrayList<>();
                etContribuyente.setText("");
                actualizaImporte(true);
                Toast.makeText(getApplicationContext(), "Verique el código. \nActualice el catálogo", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean validaContribuyente(String codigoContribuyente){
    if(contribuyentes!=null) {
        for (Contribuyente con : contribuyentes) {
            if (con.getCodigoContribuyente().equals(codigoContribuyente)) {

                return true;
            }
        }
    }
    return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if(id== R.id.busqueda_contribuyente_setting){
            Intent intent= new Intent(MainActivity.this, BusquedaContribuyente.class);
            startActivityForResult(intent, REQUEST_BUSCAR_CONTRIBUYENTE);
        }
        if(id== R.id.actualizar_catalogo_setting){
           new  ActualizarCatalogosRemoto(false, null, null, null).execute();
            /*if(tiposPlaza==null || tiposPlaza.size()<1){
                Toast.makeText(this, "No existen tipos de plaza", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(MainActivity.this, ActualizarContribucionesRemotasActivity.class);
                intent.putExtra("tiposPlaza", (Serializable) tiposPlaza);
                startActivityForResult(intent,REQUEST_RECUPERAR_CONTRIBUCIONES_REMOTE );
            }*/

        }

        if(id== R.id.actualizar_catalogo_contribuciones_setting){
            if(tiposPlaza==null || tiposPlaza.size()<1){
                Toast.makeText(this, "No existen tipos de plaza", Toast.LENGTH_SHORT).show();
            }
            else if (equipoRecaudador!=null && !equipoRecaudador.getCodigoEquipo().isEmpty()){
                Intent intent = new Intent(MainActivity.this, ActualizarContribucionesRemotasActivity.class);
                //intent.putExtra("contribuyentes", (Serializable) contribuyentes);
                intent.putExtra("tiposPlaza", (Serializable) tiposPlaza);
                startActivityForResult(intent,REQUEST_RECUPERAR_CONTRIBUCIONES_REMOTE );
            }
            else {

                Toast.makeText(getApplicationContext(),"Debe asignar un código al equipo",Toast.LENGTH_LONG).show();
            }




        }
        if(id== R.id.agregar_contribuyente_setting){
            if(tiposPlaza==null || tiposPlaza.size()<1){
                Toast.makeText(this, "No existen tipos de plaza", Toast.LENGTH_SHORT).show();
            }
            else {
                Intent intent = new Intent(MainActivity.this, ContribuyenteActivity.class);
                //intent.putExtra("contribuyentes", (Serializable) contribuyentes);
                intent.putExtra("tiposPlaza", (Serializable) tiposPlaza);
                startActivityForResult(intent,REQUEST_AGREGAR_CONTRIBUYENTE );
            }
        }

        if(id== R.id.datos_equipo_setting){
            Intent intent= new Intent(MainActivity.this, DatosEquipoActivity.class);
            intent.putExtra("recaudadorEquipo", equipoRecaudador );
            startActivityForResult(intent, REQUEST_ACTUALIZAR_DATOS_EQUIPO_RECAUDADOR);
        }

        if(id== R.id.forzar_envio_setting){
            Intent service= new Intent(this, ServiceUpload.class);
            service.putExtra("fozarEnvio", true);
            startService(service);
        }
        if(id== R.id.busqueda_contribucion_setting){
            Intent bus= new Intent(this, BusquedaContribuciones.class);
            bus.putExtra("tiposPlaza",(Serializable) tiposPlaza);
            startActivity(bus);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_SCAN_CONTRIBUYENTE){
            if(data!=null){
                final Barcode barcode= data.getParcelableExtra("barCode");
                etContribuyente.post(new Runnable() {
                    @Override
                    public void run() {
                        etContribuyente.setText(barcode.displayValue);

                    }
                });
            }
        }
        if(requestCode==REQUEST_BUSCAR_CONTRIBUYENTE){
            if(data!=null){
                final String contribuyente= data.getExtras().get("contribuyente").toString();
                etContribuyente.post(new Runnable() {
                    @Override
                    public void run() {
                        etContribuyente.setText(contribuyente);
                    }
                });
            }
        }

        if(resultCode==RESULT_OK && requestCode== REQUEST_AGREGAR_CONTRIBUYENTE){
            if(data!=null){
                final Contribuyente contribuyente= (Contribuyente) data.getExtras().get("contribuyente");
                //Plaza plaza= (Plaza) data.getExtras().get("plaza");
                //PropietarioPlaza propietarioPlaza= (PropietarioPlaza) data.getExtras().get("propietarioPlaza");
                contribuyentes.add(contribuyente);
                etContribuyente.post(new Runnable() {
                    @Override
                    public void run() {
                        etContribuyente.setText(contribuyente.getCodigoContribuyente());
                        setCurrentDateOnView(null);
                    }
                });

            }
        }

        if(requestCode==REQUEST_VALIDAR_RECAUDADOR){
           if(RecaudadorActivo.getRecaudadorActivo()==null){
               Toast.makeText(getApplicationContext(),"Debe loguearse correctamente",Toast.LENGTH_LONG).show();
               finish();
           }
        }
        if(resultCode==RESULT_OK && requestCode== REQUEST_ACTUALIZAR_DATOS_EQUIPO_RECAUDADOR){
            if(data!=null){
                EquipoRecaudador equipoRecaudador= (EquipoRecaudador) data.getExtras().get("equipoRecaudador");
                if(equipoRecaudador!=null) {
                    this.equipoRecaudador = equipoRecaudador;
                }
            }
        }
        if(requestCode==REQUEST_RECUPERAR_CONTRIBUCIONES_REMOTE ){
            if(resultCode==RESULT_OK){
                if(data!=null){
                    try {
                        final Date fechaA = new SimpleDateFormat("dd/MM/yyyy").parse(data.getExtras().get("fechaInicial").toString());
                        final String codigoTipoPlaza= data.getExtras().get("tipoPlaza").toString();
                        new ActualizarCatalogosRemoto(true,fechaA,codigoTipoPlaza,equipoRecaudador.getCodigoEquipo()).execute();
                    }
                    catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Hubo un error no se pudo actualizar las constribuciones",Toast.LENGTH_LONG).show();
                    }
                }

            }
            else{
                Toast.makeText(getApplicationContext(),"Canceló la actualización",Toast.LENGTH_LONG).show();
            }
        }

    }

    private class ActualizarPlazasContribuyenteFecha extends AsyncTask<String, Void, String > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbActualizarCatalogos.bringToFront();

            pbActualizarCatalogos.setVisibility(View.VISIBLE);


        }
        @Override
        protected String doInBackground(String... codigoContribuyente) {
            String codigoContribuyetne= codigoContribuyente[0];
            if(!etFecha.getText().toString().isEmpty()) {

                Date fecha=null;
                try {
                    fecha =getSdf().parse(etFecha.getText().toString());
                    propietarioPlazas = Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().recuperaPlazasPropietario(fecha, codigoContribuyetne);
                    if(propietarioPlazas==null || propietarioPlazas.size()<1){
                        return "No existen plazas vigentes";
                    }
                    return "OK";

                }
                catch (ParseException e){
                    return "La fecha no es correcta";
                }




            }
            else{
                return "Indica una fecha";
            }
        }

        @Override
        protected void onPostExecute(String message) {
            super.onPostExecute(message);
            pbActualizarCatalogos.setVisibility(View.INVISIBLE);
            if(message.equals("OK")){
                actualizaImporte(true);
            }
            else {
                propietarioPlazas= new ArrayList<>();
                actualizaImporte(true);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }

        }
    }

    /**
     * Revisa si hay plaza seleccionada, de haberlo manda revisar si existe una contribución hecha
     */
    private void actualizaImporte(boolean actualizaCB){
      if(actualizaCB){
          ArrayAdapter adapterPP= new ArrayAdapter<PropietarioPlaza>(this, android.R.layout.simple_spinner_item, propietarioPlazas);
          cbPlazas.setAdapter(adapterPP);
      }
     if(cbPlazas.getAdapter().getCount()>0){
         try {

             PropietarioPlaza propietarioPlaza = (PropietarioPlaza) cbPlazas.getSelectedItem();
             Date fecha =getSdf().parse(etFecha.getText().toString());


             new VerificarExistenciaContribucion(null,false).execute(propietarioPlaza.getContribuyente().getCodigoContribuyente(), fecha,propietarioPlaza.getPlaza().getCodigoPlaza() );
         }
         catch (ParseException e){

         }


     }
     else{
         etImporte.setText("");
     }

    }

    private class GuardarContribucion extends  AsyncTask<Contribucion, Void, Contribucion>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbBusqueda.setVisibility(View.VISIBLE);

        }
        @Override
        protected Contribucion doInBackground(Contribucion... contribucions) {
            Contribucion contribucion= contribucions[0];
            return null;
        }

        @Override
        protected void onPostExecute(Contribucion contribucion) {
            super.onPostExecute(contribucion);
            pbBusqueda.setVisibility(View.INVISIBLE);
        }


    }

    private int getEstadoPago(){
        if(rbPagado.isChecked()){
            return ESTADO_PAGADO;
        }
        if(rbPendiente.isChecked()){
            return  ESTADO_PENDIENTE;
        }
        if(rbAusente.isChecked()){
            return ESTADO_AUSENTE;
        }
        return ESTADO_PAGADO;
    }

    private Contribucion poblarContribucion(Contribucion contribucionG){
        try {
            PropietarioPlaza propietarioPlaza = (PropietarioPlaza) cbPlazas.getSelectedItem();
            Contribucion con = new Contribucion();
            if(contribucionG!=null) {
                con.setIdContribucion(contribucionG.getIdContribucion());
                if(contribucionG.getFolioDedicado()!=null && !contribucionG.getFolioDedicado().isEmpty()){
                    con.setFolioDedicado(contribucionG.getFolioDedicado());
                }

            }

            con.setContribuyente(propietarioPlaza.getContribuyente());
            con.setEstadoPago(getEstadoPago());
            con.setEstadoServidor(false);
            con.setImporte(Double.parseDouble(etImporte.getText().toString()));
            con.setFecha(getSdf().parse(etFecha.getText().toString()));
            con.setFechaModificacion(new Date());
            con.setPlaza(propietarioPlaza.getPlaza());
            con.setTipoPlaza(propietarioPlaza.getTipoPlaza());
            con.setRecaudador(RecaudadorActivo.getRecaudadorActivo());
            con.setEquipoRecaudador(equipoRecaudador);
            return con;
        }
        catch (Exception e){
            return  null;
        }

    }

    private boolean validarCampos(){
        if(etContribuyente.getText().toString().isEmpty()|| cbPlazas.getSelectedItem()==null || etImporte.getText().toString()==null || equipoRecaudador==null){
            return false;
        }
        else {return true;}
    }

    private class VerificarExistenciaContribucion extends  AsyncTask<Object, Void, Contribucion> {
        private boolean imprimir=false;
        private Contribucion contribucionG;
        public VerificarExistenciaContribucion(Contribucion contribucion, boolean imprimir){
            this.contribucionG= contribucion;
                    ///si se manda la contribucion no debe de revisar si existe, sólo  hacer lo correspondiente
            this.imprimir= imprimir;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbBusqueda.setVisibility(View.VISIBLE);
        }

        @Override
        protected Contribucion doInBackground(Object... objects) {
            Contribucion conLocal=contribucionG;
            if(conLocal==null) {
                conLocal = recuperaContribucion((String) objects[0], (Date) (objects[1]), (String) (objects[2]));
            }


            if(conLocal!=null && imprimir){
                new ReImprimir().execute(conLocal);

            }

            return conLocal;
        }


        @Override
        protected void onPostExecute(Contribucion contribucion) {
            super.onPostExecute(contribucion);
            pbBusqueda.setVisibility(View.INVISIBLE);
            rbPagado.setChecked(true);
            rbPagado.setEnabled(true);
            rbPendiente.setEnabled(true);
            rbAusente.setEnabled(true);
            etImporte.setEnabled(true);
            btnConfirmar.setEnabled(true);
            btnReimprimir.setVisibility(View.INVISIBLE);
            contribucionGlobal=null;
            //si existe se cargan los valores anterorioes, de lo contrario se procesa
            if (contribucion != null) {
                etImporte.setText(String.format("%.2f", contribucion.getImporte()));
                //si ya existe para recuperar su código
                contribucionGlobal= contribucion;
                //se revisa si está pagada. Si si sólo se puede reimprimir. No importa si no se ha mandado al servidor
                if (contribucion.getEstadoPago()== ESTADO_PAGADO) {
                        btnConfirmar.setEnabled(false);
                        btnReimprimir.setVisibility(View.VISIBLE);
                        rbPagado.setChecked(true);

                        etImporte.setEnabled(false);
                        rbPagado.setEnabled(false);
                        rbPendiente.setEnabled(false);
                        rbAusente.setEnabled(false);

                    } else if(contribucion.getEstadoPago()== ESTADO_PENDIENTE) {
                        rbPendiente.setChecked(true);

                    }
                    else{
                        rbAusente.setChecked(true);
                }


            }
            else{

                Double importe=0.0;
                PropietarioPlaza propietarioPlaza= (PropietarioPlaza) cbPlazas.getSelectedItem();
                if(propietarioPlaza.getTipoPlaza().getImporteGlobal()){
                    if(propietarioPlaza.getTipoPlaza().getHistorial().size()>0){
                        TipoPlazaHistorial tph= propietarioPlaza.getTipoPlaza().getHistorial().get(0);
                        importe= tph.getImporte();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "No existen importe vigente", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    importe= propietarioPlaza.getImporte();

                }

                etImporte.setText(String.format("%.2f", importe));

            }
        }
    }

    private class ActualizarCatalogosRemoto extends AsyncTask<Void, Void, Void > {
        private boolean contribuciones;
        Date fechaInicial;
        String codigoTipoPlaza;
        String codigoEquipo;

        public ActualizarCatalogosRemoto(boolean contribuciones, Date fechaInicial, String codigoTipoPlaza, String codigoEquipo) {
            this.contribuciones = contribuciones;
            this.fechaInicial = fechaInicial;
            this.codigoTipoPlaza = codigoTipoPlaza;
            this.codigoEquipo = codigoEquipo;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

           pbActualizarCatalogos.setVisibility(View.VISIBLE);
            pbBusqueda.setVisibility(View.VISIBLE);

        }
        @Override
        protected Void doInBackground(Void... voids) {

            actualizarCatalogosRemoto(contribuciones, fechaInicial, codigoTipoPlaza, codigoEquipo);
            cargarCatalogosLocal();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            pbActualizarCatalogos.setVisibility(View.INVISIBLE);
            pbBusqueda.setVisibility(View.INVISIBLE);

        }
    }

    private void actualizarCatalogosRemoto(boolean contribuciones, Date fechaInicial, String codigoTipoPlaza, String codigoEquipoRecaudador){
        List<Recaudador> recaudadores= getServicios().getRecaudadores("",100);
        if(recaudadores!=null && recaudadores.size()>0){
            Database.getInstance(this).getAppDatabase().operacionesDao().borrarInsertarRecaudadoresWithTipoPlaza(recaudadores);
        }

        List<TipoPlaza> tiposPlaza= getServicios().getTiposPlaza("",100);
        if(tiposPlaza!=null && tiposPlaza.size()>0){

            Database.getInstance(this).getAppDatabase().operacionesDao().borrarInsertarTiposPlazaWithHistorial(tiposPlaza);

            AuxActualizacionRemota aux= getServicios().getContribuyentesPlazas("", 100, new Date(), tiposPlaza);
            if(aux!=null){
                Database.getInstance(this).getAppDatabase().operacionesDao().insertarContribuyentesPlazasEstablecidasPropietarios(aux);

            }
            if(contribuciones && fechaInicial!=null && codigoTipoPlaza!=null && codigoEquipoRecaudador!=null){
                recuperarContribucionesRemotas(fechaInicial, codigoTipoPlaza, codigoEquipoRecaudador);
            }


        }


    }

    private void  recuperarContribucionesRemotas(Date fechaInicial, String codigoTipoPlaza, String codigoEquipoRecaudador){
        List<Contribucion> contribucionesRemote= getServicios().getContribucionesRemotas("",100,fechaInicial, codigoTipoPlaza, codigoEquipoRecaudador);
        if(contribucionesRemote!=null && contribucionesRemote.size()>0){
            Database.getInstance(this).getAppDatabase().operacionesDao().insertarContribuciones(contribucionesRemote);
        }
    }



    private class ActualizaCatalogosLocal extends  AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            cargarCatalogosLocal();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    /**
     * Revisa si existe una contribución en la fecha dada y la plaza dada
     * @param codigoContribuyente
     * @param fecha
     * @param codigoPlaza
     * @return
     */
    private Contribucion recuperaContribucion(String codigoContribuyente, Date fecha, String codigoPlaza){
       List<Contribucion> contribuciones= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getContribuciones(codigoContribuyente, codigoPlaza, fecha, fecha);
       if(contribuciones.size()>0){
           return contribuciones.get(0);
       }
       return  null;
    }

    private void setCurrentDateOnView(Date fecha) {



        final Calendar c = Calendar.getInstance();
        if(fecha!=null){
            c.setTime(fecha);
        }
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        etFecha.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append("/").append(month+1).append("/")
                .append(year));



    }


    private void cargarCatalogosLocal() {
        //Database.getInstance(this).getAppDatabase().operacionesDao().borrarBase();
        //Database.getInstance(this).getAppDatabase().operacionesDao().borrarContribucionFolioDedicado("AMBULANTE_00000160");
        //Database.getInstance(this).getAppDatabase().operacionesDao().borrarContribucionFolioDedicado("MERCADO_00005235");
        //Database.getInstance(this).getAppDatabase().operacionesDao().borrarContribucionFolioDedicado("MERCADO_00005298");

        /*List<Contribucion> cons= Database.getInstance(this).getAppDatabase().operacionesDao().getContribucion("DUVR_0002","MERCADO");
        List<Contribucion> cons2= Database.getInstance(this).getAppDatabase().operacionesDao().getContribucion("ORGA_333","AMBULANTE");
        List<Contribucion> con3= Database.getInstance(this).getAppDatabase().operacionesDao().getContribucion("CAPA_0045","MERCADO");
        List<Contribucion> con4= Database.getInstance(this).getAppDatabase().operacionesDao().getContribucion("DUAM_0028","MERCADO");*/

        //List consD= Database.getInstance(this).getAppDatabase().operacionesDao().getContribucionDuplicadas();

        //backUp();
        Calendar cal= Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        Date fecha= cal.getTime();
       // List<Contribucion> p1=Database.getInstance(this).getAppDatabase().operacionesDao().getContribucionesAll();
       // Database.getInstance(this).getAppDatabase().operacionesDao().establecerEstadoServerSinUpload(false,1523,2000);
        //Database.getInstance(this).getAppDatabase().operacionesDao().updateContribuyente(false,"LEGO190910_0078");
        /*Database.getInstance(this).getAppDatabase().operacionesDao().actualizarContribuyenteContribucion("ZALA190410_0065","ZÁLA190410_0065");
        Database.getInstance(this).getAppDatabase().operacionesDao().actualizarPlazaContribucion("ZALA190410_0065","ZÁLA190410_0065");
        Database.getInstance(this).getAppDatabase().operacionesDao().actualizarContribuyenteContribucion("CAGB192709_0043","CÁGB192709_0043");
        Database.getInstance(this).getAppDatabase().operacionesDao().actualizarPlazaContribucion("CAGB192709_0043","CÁGB192709_0043");

        Database.getInstance(this).getAppDatabase().operacionesDao().actualizarContribuyenteContribucion("VAVY190410_0062","VÁVY190410_0062");
        Database.getInstance(this).getAppDatabase().operacionesDao().actualizarPlazaContribucion("VAVY190410_0062","VÁVY190410_0062");*/


         /*PropietarioPlaza pp=  Database.getInstance(this).getAppDatabase().operacionesDao().recuperaPPxContribuyente("VÁVY190410_0062");
         List<Contribucion> ppc=Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribucionxContribuyente("VÁVY190410_0062");
         Plaza p=Database.getInstance(this).getAppDatabase().operacionesDao().recuperaPlazaxCodigo("VÁVY190410_0062");
        PropietarioPlaza pp2=  Database.getInstance(this).getAppDatabase().operacionesDao().recuperaPPxContribuyente("VAVY190410_0062");
        List<Contribucion> pp2c=Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribucionxContribuyente("VAVY190410_0062");
        Plaza p2=Database.getInstance(this).getAppDatabase().operacionesDao().recuperaPlazaxCodigo("VAVY190410_0062");
        PropietarioPlaza pp3=  Database.getInstance(this).getAppDatabase().operacionesDao().recuperaPPxContribuyente("CÃ\u0081GB192709_0043");
        List<Contribucion> pp3c=Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribucionxContribuyente("CÃ\u0081GB192709_0043");
        Plaza p3=Database.getInstance(this).getAppDatabase().operacionesDao().recuperaPlazaxCodigo("CÃ\u0081VY190410_0062");*/

        /*Database.getInstance(this).getAppDatabase().operacionesDao().deletePPxContribuyente("CÃ\u0081GB192709_0043");
        Database.getInstance(this).getAppDatabase().operacionesDao().deletePlazaxCodigo("CÃ\u0081GB192709_0043");
        Database.getInstance(this).getAppDatabase().operacionesDao().deleteContribuyente("CÃ\u0081GB192709_0043");
        Database.getInstance(this).getAppDatabase().operacionesDao().deletePPxContribuyente("CÃGB192709_0043");
        Database.getInstance(this).getAppDatabase().operacionesDao().deletePlazaxCodigo("CÃGB192709_0043");
        Database.getInstance(this).getAppDatabase().operacionesDao().deleteContribuyente("CÃGB192709_0043");
        List<Contribucion> pp2c=Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribucionxContribuyente("%VY190410_0062%");
        List<PropietarioPlaza> pp4=  Database.getInstance(this).getAppDatabase().operacionesDao().recuperaPPxContribuyentes("%VY190410_0062");
        List<Contribuyente> cons2= Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribuyentexcodigo("%VY190410_0062");

        List<Contribucion> p1=Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribucionxContribuyente("%GB192709_0043%");
        List<PropietarioPlaza> p2=  Database.getInstance(this).getAppDatabase().operacionesDao().recuperaPPxContribuyentes("%GB192709_0043");
        List<Contribuyente> p3= Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribuyentexcodigo("%GB192709_0043");

        List<Contribucion> pl1=Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribucionxContribuyente("%LA190410_0065%");
        List<PropietarioPlaza> pl2=  Database.getInstance(this).getAppDatabase().operacionesDao().recuperaPPxContribuyentes("%LA190410_0065%");
        List<Contribuyente> pl3= Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribuyentexcodigo("%LA190410_0065%");*/



        /*Database.getInstance(this).getAppDatabase().operacionesDao().deletePPxContribuyente("ZÁLA190410_0065");
        Database.getInstance(this).getAppDatabase().operacionesDao().deletePlazaxCodigo("ZÁLA190410_0065");
        Database.getInstance(this).getAppDatabase().operacionesDao().deleteContribuyente("ZÁLA190410_0065");*/
//CÃGB192709_0043



        //List<PropietarioPlaza> pp= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getPlazasSinRevisar("MERCADO",fecha);
       //Database.getInstance(this).getAppDatabase().operacionesDao().establecerEstadoServerSinUpload(false, "MERCADO");
        //Database.getInstance(this).getAppDatabase().operacionesDao().establecerEstadoServerSinUpload(true);
       // Database.getInstance(this).getAppDatabase().operacionesDao().establecerEstadoServerSinUpload(false,0,501);
        //Contribuyente con=Database.getInstance(this).getAppDatabase().operacionesDao().getContribuyente("ZÁLA190410_0065");
        //Contribuyente con2=Database.getInstance(this).getAppDatabase().operacionesDao().getContribuyente("ZALA190410_0065");
        //Database.getInstance(this).getAppDatabase().operacionesDao().actualizarPP("LOBM190810_0006","LOBM190810_0006");
        //Database.getInstance(this).getAppDatabase().operacionesDao().establecerEstadoServerSinUpload(false,2200);
        //Database.getInstance(this).getAppDatabase().operacionesDao().establecerEstadoServerSinUpload(true,0,2000);
        //Database.getInstance(this).getAppDatabase().operacionesDao().establecerEstadoServerSinUpload(false,24,26);
        //List<Contribucion> ppc=Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribucionxContribuyente("LÓBM190810_0006");
        //List<Contribucion> ppc2=Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribucionxContribuyente("LOBM190810_0006");


        /*Database.getInstance(this).getAppDatabase().operacionesDao().actualizarPlaza("LOBM190810_0006","LÓBM190810_0006");
        Database.getInstance(this).getAppDatabase().operacionesDao().updatePlaza(false, "LOBM190810_0006");
        Database.getInstance(this).getAppDatabase().operacionesDao().updatePropietarioPlaza(false, "LOBM190810_0006");
        List<Plaza> plazas=Database.getInstance(this).getAppDatabase().operacionesDao().getPlazasToUpload();*/
        //Database.getInstance(this).getAppDatabase().operacionesDao().updatePropietarioPlaza(false, "LOBM190810_0006");


       // Database.getInstance(this).getAppDatabase().operacionesDao().updateContribuyente(false,"LÓBM190810_0006","LOBM190810_0006" );
        //Database.getInstance(this).getAppDatabase().operacionesDao().actualizarContribuyenteContribucion("LOBM190810_0006","LÓBM190810_0006");
        //Database.getInstance(this).getAppDatabase().operacionesDao().actualizarPlazaContribucion("LOBM190810_0006","LÓBM190810_0006");


        /*List<Contribucion> cons= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().recuperarContribucionesEstadoServidor();
        List<Contribucion> cc=Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getContribucionesAll();
        List<Contribucion> cc2= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getContribucionesAll("AMB_FORANEO_MOVIL");
        List<Contribucion> cc3= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getContribucionesAll("AMB_FORANEO_FIJO");*/
/* Este bloque de codigo nos permite eliminar y actualizar un contribuyente con acento. Además de modificar aquí posiblmente hay modificar en el server
        List<Contribucion> p1=Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribucionxContribuyente("%RE190410_0069%");
        List<PropietarioPlaza> p2=  Database.getInstance(this).getAppDatabase().operacionesDao().recuperaPPxContribuyentes("%RE190410_0069");
        List<Contribuyente> p3= Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribuyentexcodigo("%RE190410_0069");



        Database.getInstance(this).getAppDatabase().operacionesDao().deletePPxContribuyente("LÃ\u0093RE190410_0069");
        Database.getInstance(this).getAppDatabase().operacionesDao().deletePlazaxCodigo("LÃ\u0093RE190410_0069");
        Database.getInstance(this).getAppDatabase().operacionesDao().deleteContribuyente("LÃ\u0093RE190410_0069");

                 Database.getInstance(this).getAppDatabase().operacionesDao().updateContribuyente(true,"LÓRE190410_0069","LORE190410_0069" );
                Database.getInstance(this).getAppDatabase().operacionesDao().actualizarContribuyenteContribucion("LORE190410_0069","LÓRE190410_0069");
                Database.getInstance(this).getAppDatabase().operacionesDao().actualizarPlazaContribucion("LORE190410_0069","LÓRE190410_0069");
                Database.getInstance(this).getAppDatabase().operacionesDao().actualizarContribuyentePP("LORE190410_0069","LÓRE190410_0069");
                Database.getInstance(this).getAppDatabase().operacionesDao().actualizarPP("LORE190410_0069","LÓRE190410_0069");

        List<Contribucion> a=Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribucionxContribuyente("%RE190410_0069%");
        List<PropietarioPlaza>b =  Database.getInstance(this).getAppDatabase().operacionesDao().recuperaPPxContribuyentes("%RE190410_0069");
        List<Contribuyente> c= Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribuyentexcodigo("%RE190410_0069");*/

        /*List<Contribuyente> c= Database.getInstance(this).getAppDatabase().operacionesDao().recuperaContribuyentexcodigo("LOVM201001_0116");
        //Database.getInstance(this).getAppDatabase().operacionesDao().updateContribucion(true, 2, "R_AMBULANTE_00005776");
        List<Contribucion> tmplist= Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().getContribucionesAll();*/


        contribuyentes = Database.getInstance(this).getAppDatabase().operacionesDao().getAllContribuyentes();

        tiposPlaza = Database.getInstance(this).getAppDatabase().operacionesDao().getAllTiposPlaza();
        recaudadores= Database.getInstance(this).getAppDatabase().operacionesDao().getAllRecaudadores();
        equipoRecaudador= Database.getInstance(this).getAppDatabase().operacionesDao().recuperarEquipoRecaudador();
        //backUp();


    }

    private void backUp(){

            //https://stackoverflow.com/questions/50916380/room-best-ways-to-create-backups-for-offline-application
            Database.getInstance(this).getAppDatabase().close();
            File dbfile = this.getDatabasePath("cobranza");
            File sdir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),"DBsaves");
            String sfpath = sdir.getPath() + File.separator + "DBsave" + String.valueOf(System.currentTimeMillis());
            if (!sdir.exists()) {
                sdir.mkdirs();
            }
            File savefile = new File(sfpath);
            try {
                savefile.createNewFile();
                int buffersize = 8 * 1024;
                byte[] buffer = new byte[buffersize];
                int bytes_read = buffersize;
                OutputStream savedb = new FileOutputStream(sfpath);
                InputStream indb = new FileInputStream(dbfile);
                while ((bytes_read = indb.read(buffer,0,buffersize)) > 0) {
                    savedb.write(buffer,0,bytes_read);
                }
                savedb.flush();
                indb.close();
                savedb.close();

            } catch (Exception e) {
                e.printStackTrace();
            }

    }


    private class ReImprimir extends  AsyncTask<Contribucion, Void, Void>{


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbBusqueda.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Contribucion... cons) {
            Contribucion contribucion= cons[0];
            getImprimir().inicializar(getApplicationContext(),contribuyentes, tiposPlaza, recaudadores);
            imprimirTicket(true,getImprimir().generarCadena(contribucion),null);
            try {
                Thread.sleep(5000);
                //imprimirTicket(getImprimir().generarCadena(contribucion));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            pbBusqueda.setVisibility(View.INVISIBLE);
            super.onPostExecute(aVoid);
        }
    }

    private void confirmarContribucion(){

        if(validarCampos()) {


            Contribucion contribucion= poblarContribucion(contribucionGlobal!=null?contribucionGlobal:null);
            if(RecaudadorActivo.getRecaudadorActivo()!=null) {
                if(RecaudadorActivo.validarAutorizacion(contribucion.getTipoPlaza())) {
                    if (fechaDentroDeMeses(contribucion.getFecha(), 1)) {

                        new ConfirmarContribucion(contribucion).execute();
                    } else {
                        Toast.makeText(getApplicationContext(), "No se puden pagar contribuciones mayores a un mes", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "No está autorizado cobrar", Toast.LENGTH_LONG).show();
                }
            }
            else{
                Toast.makeText(getApplicationContext(), "No hay recaudador seleccioando", Toast.LENGTH_LONG).show();
            }

        }

        else{
            Toast.makeText(getApplicationContext(), "Llene todos los datos", Toast.LENGTH_LONG).show();
        }

    }



    private boolean fechaDentroDeMeses(Date fecha, int meses){
        Calendar cal= Calendar.getInstance();
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH)+meses);
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH)+1);
        cal.set(Calendar.HOUR,0);
        cal.set(Calendar.MINUTE,0);
        cal.set(Calendar.SECOND,0);
        cal.set(Calendar.MILLISECOND,0);
        Date fechaFin= cal.getTime();
        if(fechaFin.compareTo(fecha)>=0){
            return  true;
        }
        else{
            return  false;
        }
    }

    private class ConfirmarContribucion extends AsyncTask<Void, Void, Void>{
        private List<Contribucion> contribucionesAnteriores;
        private Contribucion contribucion;

        public ConfirmarContribucion(Contribucion contribucion) {
            this.contribucion = contribucion;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pbBusqueda.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                //si se va a intentar pagar, es necesario que no haya pendientes de pago
                if(contribucion.getEstadoPago()==ESTADO_PAGADO){
                    contribucionesAnteriores= Database.getInstance(getApplicationContext())
                            .getAppDatabase().operacionesDao()
                            .getContribucionesAnteriores(contribucion.getContribuyente().getCodigoContribuyente(), contribucion.getPlaza().getCodigoPlaza(), ESTADO_PENDIENTE,contribucion.getFecha());

                }

                //si no hay pedientes de pago, intenta hacer la inserción
                if(contribucionesAnteriores==null || contribucionesAnteriores.size()==0) {
                    contribucion = Database.getInstance(getApplicationContext()).getAppDatabase().operacionesDao().finalizarContribucion(contribucion);

                }
                return null;
            }
            catch(Exception e){
                return null;

            }
        }

        @Override
        protected void onPostExecute(Void vois) {
            super.onPostExecute(vois);
            pbBusqueda.setVisibility(View.INVISIBLE);
            //si hay contribuciones anteriores las muestra, de lo cotrario carga la
            if(contribucionesAnteriores!=null && contribucionesAnteriores.size()>0  ){
                StringBuilder sb= new StringBuilder();
                for(Contribucion con: contribucionesAnteriores){
                    sb.append(con.toString()).append("\n");
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                // Add the buttons
                builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        setCurrentDateOnView(contribucionesAnteriores.get(0).getFecha());
                         //new VerificarExistenciaContribucion(contribucionesAnteriores.get(0),false).execute();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
                builder.setTitle("CONTRIBUCIONES PENDIENTES");
                sb.append("\n¿Desea hacer el pago del anterior?");
                builder.setMessage(sb.toString());

                AlertDialog dialog = builder.create();
                dialog.show();


            }

            else if(contribucion!=null){
                boolean imprimir=true;
                if(contribucion.getEstadoPago().equals(ESTADO_AUSENTE)){
                    imprimir=false;
                }
                new VerificarExistenciaContribucion(contribucion,imprimir).execute(contribucion.getContribuyente().getCodigoContribuyente(), contribucion.getFecha(), contribucion.getPlaza().getCodigoPlaza());
                Toast.makeText(getApplicationContext(),"Contribución correcta",Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(getApplicationContext(),"Contribución correcta",Toast.LENGTH_SHORT).show();
            }


            }
    }



    private void imprimirTicket(boolean ticket,String cadena, String path){


        BXLConfigLoader bxlConfigLoader=new BXLConfigLoader(this);
        try
        {


            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            String name="";
            String tt;
            String address="";
            for(BluetoothDevice bt: pairedDevices) {
                name = bt.getName();
                tt= bt.getAddress();
                if(name.substring(0,3).equals("SPP")){
                    address= tt;
                }
            }
            if(name.equals("")){
                new Exception("No exitest al impresora\n Agregala como SPP-R310-COBRANZA ");
            }
            try
            {
                // BXLConfigLoader creating / setting file open//bxlConfigLoader =
                bxlConfigLoader.openFile();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                bxlConfigLoader.newFile();
            }


            if(bxlConfigLoader.getEntries().size()>=0) {
                bxlConfigLoader.removeAllEntries();
                bxlConfigLoader.addEntry(name,
                        BXLConfigLoader.DEVICE_CATEGORY_POS_PRINTER,
                        BXLConfigLoader.PRODUCT_NAME_SPP_R310,
                        BXLConfigLoader.DEVICE_BUS_BLUETOOTH,
                        address);
                bxlConfigLoader.saveFile();
            }

            POSPrinter posPrinter= new POSPrinter(this);
            posPrinter.open(name);
            posPrinter.claim(3000);
            posPrinter.setDeviceEnabled(true);
            posPrinter.setAsyncMode(true);
            posPrinter.setCharacterSet(BXLConst.CS_850_MULTILINGUAL);
            posPrinter.setCharacterEncoding(BXLConst.CE_ASCII);
            //posPrinter.setPageModePrintArea("0, 0, 576, 1600");

            if(ticket) {
                String pathLogoticket = Environment.getExternalStorageDirectory().getPath().concat("/Cobranza/logo_ticket.png");
                File images = new File(pathLogoticket);
                if (images.exists()) {
                    posPrinter.setPageModePrintDirection(POSPrinterConst.PTR_PD_LEFT_TO_RIGHT);
                    ByteBuffer buffer = ByteBuffer.allocate(4);
                    buffer.put((byte) POSPrinterConst.PTR_S_RECEIPT);
                    buffer.put((byte) 80); // brightness
                    buffer.put((byte) 0x01); // compress
                    buffer.put((byte) 0x00);
                    posPrinter.printBitmap(buffer.getInt(0),
                            pathLogoticket,
                            200,
                            POSPrinterConst.PTR_BM_CENTER);
                }


                posPrinter.printNormal(POSPrinterConst.JPOS_EPTR_REC_HEAD_CLEANING, cadena);

            }
            //si es la imagen
            else{
                File images = new File(path);
                if (images.exists()) {
                    posPrinter.setPageModePrintDirection(POSPrinterConst.PTR_PD_LEFT_TO_RIGHT);
                    ByteBuffer buffer = ByteBuffer.allocate(4);
                    buffer.put((byte) POSPrinterConst.PTR_S_RECEIPT);
                    buffer.put((byte) 80); // brightness
                    buffer.put((byte) 0x01); // compress
                    buffer.put((byte) 0x00);
                    posPrinter.printBitmap(buffer.getInt(0),
                            path,
                            400,
                            POSPrinterConst.PTR_BM_CENTER);
                }
            }

            Log.e("correcto", "correcto");
        }
        catch(Exception e)
        {
            Log.e("error",e.toString());
            //Toast.makeText(this, "Verifique los datos de la impresora",Toast.LENGTH_LONG);
           // e.printStackTrace();
        }
    }

    private WebService getServicios(){
        if(service==null){
            service= new WebService();
        }
        return service;
    }

    private SimpleDateFormat getSdf(){
        if(sdf==null){
            sdf= new SimpleDateFormat("dd/MM/yyyy");
        }
        return  sdf;
    }

    private Imprimir getImprimir(){
        if(imprimir==null){
            imprimir= new Imprimir();

        }
        return  imprimir;
    }
}

