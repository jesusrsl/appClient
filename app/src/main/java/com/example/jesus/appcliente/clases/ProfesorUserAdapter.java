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
 * Created by jesus on 10/07/17.
 */

public class ProfesorUserAdapter extends RecyclerView.Adapter<ProfesorUserAdapter.ViewHolder>  {

    protected ArrayList<ProfesorUser> profesorUserArrayList;
    protected LayoutInflater inflador;
    protected Context contexto;
    protected View.OnClickListener onClickListener;


    public ProfesorUserAdapter(Context contexto, ArrayList<ProfesorUser> profesorUserArrayList) {
        this.contexto = contexto;
        this.profesorUserArrayList = profesorUserArrayList;
        inflador = (LayoutInflater) contexto
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void actualizar(ArrayList<ProfesorUser> profesorUserArrayList){
        this.profesorUserArrayList = profesorUserArrayList;
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView apellidos, nombre, tutoria, email, admin;

        public ViewHolder(View itemView) {
            super(itemView);

            apellidos = (TextView) itemView.findViewById(R.id.textViewLast_name);
            nombre = (TextView) itemView.findViewById(R.id.textViewFirst_name);
            tutoria = (TextView) itemView.findViewById(R.id.textViewGrupoTutor);
            email = (TextView) itemView.findViewById(R.id.textViewEmail);
            admin = (TextView) itemView.findViewById(R.id.textViewAdmin);

        }
    }

    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = inflador.inflate(R.layout.layout_profesor, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        ProfesorUser profesor = this.profesorUserArrayList.get(posicion);
        personalizaVista(holder, profesor);
    }


    // Personalizamos un ViewHolder a partir de un lugar
    private void personalizaVista(ViewHolder holder, ProfesorUser profesor) {
        holder.apellidos.setText(profesor.getLast_name());
        holder.nombre.setText(profesor.getFirst_name());
        if (profesor.getGrupo()==null){
            holder.tutoria.setText("Tutoría: ---");
        }
        else{
            holder.tutoria.setText("Tutoría: " + profesor.getGrupo());
        }
        if (profesor.getEmail().isEmpty()){
            holder.email.setText("E-mail: ---");
        }
        else{
            holder.email.setText("E-mail: " + profesor.getEmail());
        }
        if (profesor.getIs_superuser()){
            holder.admin.setText("Administrador");
        }
        else{
            holder.admin.setText("");
        }

    }


    @Override
    public int getItemCount() {
        return this.profesorUserArrayList.size();
    }


    @Override
    public long getItemId(int posicion) {
        return posicion;
    }

    public int getItemPk(int posicion){ return this.profesorUserArrayList.get(posicion).getPk();}


}
