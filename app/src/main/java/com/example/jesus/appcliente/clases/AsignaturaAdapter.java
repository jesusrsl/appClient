package com.example.jesus.appcliente.clases;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jesus.appcliente.R;

import java.util.ArrayList;

/**
 * Created by jesus on 12/07/17.
 */

public class AsignaturaAdapter extends BaseAdapter {

    Context context;
    ArrayList<Asignatura> asignaturaArrayList;

    public AsignaturaAdapter(Context context, ArrayList<Asignatura> asignaturaArrayList) {
        this.context = context;
        this.asignaturaArrayList = asignaturaArrayList;
    }

    @Override
    public int getCount() {
        return this.asignaturaArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.asignaturaArrayList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //creación de vista
        LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (view == null){
            view = layoutInflater.inflate(R.layout.layout_asignatura, viewGroup, false);
        }


        //Objeto de formulario
        TextView pkAsignatura = (TextView) view.findViewById(R.id.textViewPkAsignatura);
        TextView nombreAsignatura = (TextView) view.findViewById(R.id.textViewNombreAsignatura);
        TextView profesorAsignatura = (TextView) view.findViewById(R.id.textViewProfesorAsignatura);
        TextView grupoAsignatura = (TextView) view.findViewById(R.id.textViewGrupoAsignatura);
        TextView distribucion = (TextView) view.findViewById(R.id.textViewDistribucion);


        Asignatura asignatura = this.asignaturaArrayList.get(i);

        if (asignatura != null){
            pkAsignatura.setText("Identificador: "+ asignatura.getIdToString());
            nombreAsignatura.setText("Nombre: "+ asignatura.getNombre());
            profesorAsignatura.setText("Profesor/a: "+ asignatura.getProfesorText());
            grupoAsignatura.setText("Grupo: "+ asignatura.getGrupoText());
            distribucion.setText("Distribución: "+ Integer.toString(asignatura.getDistribucion()));
        }

        return view;
    }

    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
