package com.jalpa.cobranza.busqueda.contribuciones;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.jalpa.cobranza.R;
import com.jalpa.cobranza.model.entidades.Contribucion;
import com.jalpa.cobranza.model.entidades.Contribuyente;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ContribucionAdapter extends BaseAdapter{//  implements Filterable {
    public Context context;
    public ArrayList<Contribucion> contribucionArrayList;
    public ArrayList<Contribucion> origCon;

    public ContribucionAdapter(Context context, ArrayList<Contribucion> contribucionArrayList) {
        this.context = context;
        this.contribucionArrayList = contribucionArrayList;
    }

    public class OperadorHolder{
        TextView tvPlaza;
        TextView tvNombre;
        TextView tvEstado;
        TextView tvImporte;
    }

    @Override
    public int getCount() {
        return contribucionArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return contribucionArrayList.get(i);
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
            view= LayoutInflater.from(context).inflate(R.layout.row_busqueda_contribucion, viewGroup, false);
            holder=new OperadorHolder();
            holder.tvPlaza=(TextView) view.findViewById(R.id.txtPlazaCB);
            holder.tvNombre=(TextView) view.findViewById(R.id.txtNombreCB);
            holder.tvEstado=(TextView) view.findViewById(R.id.txtEstadoCB);
            holder.tvImporte=(TextView) view.findViewById(R.id.txtImporteCB);
            view.setTag(holder);
        }
        else
        {
            holder=(OperadorHolder) view.getTag();
        }

        holder.tvPlaza.setText(contribucionArrayList.get(i).getPlaza().getCodigoPlaza());
        holder.tvNombre.setText(contribucionArrayList.get(i).getContribuyente().getNombreFull());
        holder.tvEstado.setText(getEstadoPago(contribucionArrayList.get(i).getEstadoPago()));
        holder.tvImporte.setText(contribucionArrayList.get(i).getImporte()!=null?String.format("%.2f", contribucionArrayList.get(i).getImporte()):"");


        return view;
    }

    private String getEstadoPago(Integer estadoPago){
        if(estadoPago==null){
            return "SIN REVISAR";
        }
        if(estadoPago==1){
            return "PAGADO";
        }
        if(estadoPago==2){
            return  "PENDIENTE";
        }
        if(estadoPago==3){
            return "NO ENCONTRADO";
        }
        else{
            return "SIN REVISAR";
        }

    }
/*
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
    }*/
}
