package com.example.jesus.appcliente.clases;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jesus.appcliente.R;

import java.util.ArrayList;

/**
 * Created by jesus on 12/07/17.
 */

public class AsignaturaProfesorAdapter extends RecyclerView.Adapter<AsignaturaProfesorAdapter.ViewHolder>  {

    protected ArrayList<AsignaturaProfesor> asignaturaProfesorArrayList;;           //Lista de asignaturas
    protected LayoutInflater inflador;   //Crea Layouts a partir del XML
    protected Context contexto;          //Lo necesitamos para el inflador
    protected View.OnClickListener onClickListener;


    public AsignaturaProfesorAdapter(Context contexto, ArrayList<AsignaturaProfesor> asignaturaProfesorArrayList) {
        this.contexto = contexto;
        this.asignaturaProfesorArrayList = asignaturaProfesorArrayList;
        inflador = (LayoutInflater) contexto
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void actualizar(ArrayList<AsignaturaProfesor> asignaturaProfesorArrayList){
        this.asignaturaProfesorArrayList = asignaturaProfesorArrayList;
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView nombreAsignatura, grupoAsignatura;

        public ViewHolder(View itemView) {
            super(itemView);

            nombreAsignatura = (TextView) itemView.findViewById(R.id.textViewNombreAsignaturaProf);
            grupoAsignatura = (TextView) itemView.findViewById(R.id.textViewGrupoAsignaturaProf);

        }
    }


    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = inflador.inflate(R.layout.layout_asignatura_profesor, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        AsignaturaProfesor asignaturaProfesor = this.asignaturaProfesorArrayList.get(posicion);
        personalizaVista(holder, asignaturaProfesor);
    }


    // Personalizamos un ViewHolder a partir de un lugar
    private void personalizaVista(ViewHolder holder, AsignaturaProfesor asignaturaProfesor) {
            holder.nombreAsignatura.setText(asignaturaProfesor.getNombre());
            holder.grupoAsignatura.setText(asignaturaProfesor.getGrupo());
    }

    @Override
    public int getItemCount() {
        return this.asignaturaProfesorArrayList.size();
    }


    @Override
    public long getItemId(int posicion) {
        return posicion;
    }

    public int getItemPk(int posicion){ return this.asignaturaProfesorArrayList.get(posicion).getPk();}

    public String getItemNombre(int posicion){ return this.asignaturaProfesorArrayList.get(posicion).getNombre();}

    public String getItemGrupo(int posicion){ return this.asignaturaProfesorArrayList.get(posicion).getGrupo();}

}
