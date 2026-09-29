package com.jalpa.cobranza;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.jalpa.cobranza.busqueda.contribuciones.BusquedaContribuciones;
import com.jalpa.cobranza.model.entidades.TipoPlaza;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ActualizarContribucionesRemotasActivity extends AppCompatActivity {
    private EditText etFechaInicial;
    private Spinner spTiposPlaza;
    private Button btnRecupera;
    private List<TipoPlaza> tiposPlaza;

    private DatePickerDialog.OnDateSetListener mDateSetListener;
    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_contribuciones);

        this.btnRecupera = findViewById(R.id.btnRecuperaContribucionesRemotas);
        this.etFechaInicial = findViewById(R.id.etFechaInicial);
        this.spTiposPlaza = findViewById(R.id.cbTipoPlazaRecuperarContribuciones);

        tiposPlaza = (List<TipoPlaza>) getIntent().getExtras().get("tiposPlaza");
        ArrayAdapter adapterPP = new ArrayAdapter<TipoPlaza>(this, android.R.layout.simple_spinner_item, tiposPlaza);
        spTiposPlaza.setAdapter(adapterPP);

        etFechaInicial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = 2019;
                int month = 8;
                int day = 1;
                if (etFechaInicial.getText().toString().isEmpty()) {
                    year = cal.get(Calendar.YEAR);
                    month = cal.get(Calendar.MONTH);
                    day = cal.get(Calendar.DAY_OF_MONTH);

                } else {
                    String[] fecha = etFechaInicial.getText().toString().split("/");
                    day = Integer.parseInt(fecha[0]);
                    month = Integer.parseInt(fecha[1]) - 1;
                    year = Integer.parseInt(fecha[2]);
                }

                DatePickerDialog dialog = new DatePickerDialog(ActualizarContribucionesRemotasActivity.this, android.R.style.Theme_Holo_Dialog_MinWidth, mDateSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
        setCurrentDateOnView();
        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                etFechaInicial.setText(day + "/" + month + "/" + year);
            }
        };
        btnRecupera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                Date fechaInicial=null;
                if (etFechaInicial.getText() != null) {
                    try {
                        fechaInicial = format.parse(etFechaInicial.getText().toString());
                    } catch (ParseException e) {

                    }
                }
                intent.putExtra("fechaInicial", fechaInicial!=null? etFechaInicial.getText():"");
                intent.putExtra("tipoPlaza", spTiposPlaza.getSelectedItem().toString());
                setResult(RESULT_OK, intent);
                finish();

            }
        });

    }

    private void setCurrentDateOnView() {
        int year;
        int month;
        int day;


        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        etFechaInicial.setText(new StringBuilder()
                // Month is 0 based, just add 1
                .append(day).append("/").append(month + 1).append("/")
                .append(year));


    }

}

