package com.jalpa.cobranza.busqueda;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jalpa.cobranza.R;
import com.jalpa.cobranza.model.entidades.Contribuyente;


import java.util.ArrayList;

public class ContribuyenteAdapter extends BaseAdapter implements Filterable {
    public Context context;
    public ArrayList<Contribuyente> contribuyenteArrayList;
    public ArrayList<Contribuyente> orig;

    public ContribuyenteAdapter(Context context, ArrayList<Contribuyente> contribuyenteArrayList) {
        this.context = context;
        this.contribuyenteArrayList = contribuyenteArrayList;
    }

    public class OperadorHolder{
        TextView num;
        TextView nombre;
    }

    @Override
    public int getCount() {
        return contribuyenteArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return contribuyenteArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        OperadorHolder holder;
        if(view==null)
        {
            view= LayoutInflater.from(context).inflate(R.layout.row_busqueda, viewGroup, false);
            holder=new OperadorHolder();
            holder.num=(TextView) view.findViewById(R.id.txtNum);
            holder.nombre=(TextView) view.findViewById(R.id.txtNombre);
            view.setTag(holder);
        }
        else
        {
            holder=(OperadorHolder) view.getTag();
        }

        holder.num.setText(contribuyenteArrayList.get(i).getCodigoContribuyente());
        holder.nombre.setText(contribuyenteArrayList.get(i).getNombreFull());

        return view;
    }

    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final ArrayList<Contribuyente> results = new ArrayList<Contribuyente>();
                if (orig == null)
                    orig = contribuyenteArrayList;
                if (constraint != null) {
                    if (orig != null && orig.size() > 0) {
                        for (final Contribuyente g : orig) {

                            if ( g.getCodigoContribuyente().toLowerCase().contains(constraint.toString()) || g.getNombreFull().toLowerCase()
                                    .contains(constraint.toString()))
                                results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                contribuyenteArrayList = (ArrayList<Contribuyente>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
