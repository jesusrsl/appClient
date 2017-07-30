package com.example.jesus.appcliente.clases;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jesus.appcliente.R;

import java.util.ArrayList;

/**
 * Created by jesus on 12/07/17.
 */

public class AsignaturaAdapter extends RecyclerView.Adapter<AsignaturaAdapter.ViewHolder>  {

    protected ArrayList<Asignatura> asignaturaArrayList;;           //Lista de asignaturas
    protected LayoutInflater inflador;   //Crea Layouts a partir del XML
    protected Context contexto;          //Lo necesitamos para el inflador
    protected View.OnClickListener onClickListener;


    public AsignaturaAdapter(Context contexto, ArrayList<Asignatura> asignaturaArrayList) {
        this.contexto = contexto;
        this.asignaturaArrayList = asignaturaArrayList;
        inflador = (LayoutInflater) contexto
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void actualizar(ArrayList<Asignatura> asignaturaArrayList){
        this.asignaturaArrayList = asignaturaArrayList;
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreAsignatura, profesorAsignatura, grupoAsignatura;

        public ViewHolder(View itemView) {
            super(itemView);

            nombreAsignatura = (TextView) itemView.findViewById(R.id.textViewNombreAsignatura);
            profesorAsignatura = (TextView) itemView.findViewById(R.id.textViewProfesorAsignatura);
            grupoAsignatura = (TextView) itemView.findViewById(R.id.textViewGrupoAsignatura);

        }
    }


    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = inflador.inflate(R.layout.layout_asignatura, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        Asignatura asignatura = this.asignaturaArrayList.get(posicion);
        personalizaVista(holder, asignatura);
    }


    // Personalizamos un ViewHolder a partir de un lugar
    private void personalizaVista(ViewHolder holder, Asignatura asignatura) {
            holder.nombreAsignatura.setText(asignatura.getNombre());
            holder.profesorAsignatura.setText(asignatura.getProfesorText());
            holder.grupoAsignatura.setText(asignatura.getGrupoText());
    }

    @Override
    public int getItemCount() {
        return this.asignaturaArrayList.size();
    }


    @Override
    public long getItemId(int posicion) {
        return posicion;
    }

    public int getItemPk(int posicion){ return this.asignaturaArrayList.get(posicion).getPk();}




}
