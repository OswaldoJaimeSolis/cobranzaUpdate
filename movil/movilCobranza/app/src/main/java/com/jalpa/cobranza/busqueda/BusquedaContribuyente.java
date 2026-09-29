package com.jalpa.cobranza.busqueda;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;

import com.jalpa.cobranza.R;
import com.jalpa.cobranza.model.dao.Database;
import com.jalpa.cobranza.model.entidades.Contribuyente;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BusquedaContribuyente extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private SearchView mSearchView;
    private ListView mListView;
    private ArrayList<Contribuyente> contribuyentes;
    private  ContribuyenteAdapter contribuyenteAdapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_contribuyente);
        mSearchView=(SearchView) findViewById(R.id.searchView1);
        mListView=(ListView) findViewById(R.id.listView1);
        new CargaContribuyentes().execute();
        mListView.setTextFilterEnabled(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent();
                String val= ((Contribuyente)mListView.getAdapter().getItem(i)).getCodigoContribuyente()+" "+((Contribuyente)mListView.getAdapter().getItem(i)).getNombreFull();
                intent.putExtra("contribuyente", val);
                setResult(RESULT_OK,intent);
                finish();

            }
        });
        setupSearchView();
    }

    private void setupSearchView(){
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Indica el nombre del contribuyente");
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (TextUtils.isEmpty(s)) {
            mListView.clearTextFilter();
        } else {
            mListView.setFilterText(s);
        }
        return true;
    }


     private class CargaContribuyentes extends AsyncTask<Void, Void, Void > {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();



        }
        @Override
        protected Void doInBackground(Void... voids) {
            contribuyentes= new ArrayList<Contribuyente>(Database.getInstance(getApplicationContext()).getAppDatabase().contribuyentesDao().getAllContribuyentes())  ;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            contribuyenteAdapter=new ContribuyenteAdapter(BusquedaContribuyente.this, contribuyentes);
            mListView.setAdapter(contribuyenteAdapter);


        }
    }

}
