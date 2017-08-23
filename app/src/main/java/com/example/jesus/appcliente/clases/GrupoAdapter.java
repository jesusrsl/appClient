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

public class GrupoAdapter extends RecyclerView.Adapter<GrupoAdapter.ViewHolder>  {

    protected ArrayList<Grupo> grupoArrayList;;           //Lista de asignaturas
    protected LayoutInflater inflador;   //Crea Layouts a partir del XML
    protected Context contexto;          //Lo necesitamos para el inflador
    protected View.OnClickListener onClickListener;


    public GrupoAdapter(Context contexto, ArrayList<Grupo> grupoArrayList) {
        this.contexto = contexto;
        this.grupoArrayList = grupoArrayList;
        inflador = (LayoutInflater) contexto
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void actualizar(ArrayList<Grupo> grupoArrayList){
        this.grupoArrayList = grupoArrayList;
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView cursoGrupo, unidadGrupo, tutorGrupo, numAlumnosGrupo;

        public ViewHolder(View itemView) {
            super(itemView);

            cursoGrupo = (TextView) itemView.findViewById(R.id.textViewCurso);
            unidadGrupo = (TextView) itemView.findViewById(R.id.textViewUnidad);
            tutorGrupo = (TextView) itemView.findViewById(R.id.textViewTutor);
            numAlumnosGrupo = (TextView) itemView.findViewById(R.id.textViewNumAlumnosValue);

        }
    }


    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = inflador.inflate(R.layout.layout_grupo, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        Grupo grupo = this.grupoArrayList.get(posicion);
        personalizaVista(holder, grupo);
    }


    // Personalizamos un ViewHolder a partir de un lugar
    private void personalizaVista(ViewHolder holder, Grupo grupo) {
        holder.cursoGrupo.setText(grupo.getCurso());
        holder.unidadGrupo.setText(grupo.getUnidad());
        holder.tutorGrupo.setText(grupo.getTutor());
        holder.numAlumnosGrupo.setText(Integer.toString(grupo.getNum_alumnos()));
    }

    @Override
    public int getItemCount() {
        return this.grupoArrayList.size();
    }


    @Override
    public long getItemId(int posicion) {
        return posicion;
    }

    public int getItemPk(int posicion){ return this.grupoArrayList.get(posicion).getPk();}

    public int getItemDistribucion(int posicion){ return this.grupoArrayList.get(posicion).getDistribucion();}




}
