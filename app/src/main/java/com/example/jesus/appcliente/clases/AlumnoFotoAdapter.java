package com.example.jesus.appcliente.clases;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jesus.appcliente.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jesus on 12/07/17.
 */

public class AlumnoFotoAdapter extends RecyclerView.Adapter<AlumnoFotoAdapter.ViewHolder> {

    protected ArrayList<Alumno> alumnoArrayList;
    protected LayoutInflater inflador;
    protected Context contexto;
    protected View.OnClickListener onClickListener;

    public AlumnoFotoAdapter(Context contexto, ArrayList<Alumno> alumnoArrayList) {
        this.contexto = contexto;
        this.alumnoArrayList = alumnoArrayList;
        inflador = (LayoutInflater) contexto
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void actualizar(ArrayList<Alumno> alumnoArrayList){
        this.alumnoArrayList = alumnoArrayList;
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView alumno;
        public ImageView foto;

        public ViewHolder(View itemView) {
            super(itemView);

            alumno = (TextView) itemView.findViewById(R.id.textViewAlumno);
            foto = (ImageView) itemView.findViewById(R.id.imageViewAlumno);
        }
    }

    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = inflador.inflate(R.layout.layout_alumno_foto, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        Alumno alumno = this.alumnoArrayList.get(posicion);
        personalizaVista(holder, alumno);
    }

    // Personalizamos un ViewHolder a partir de un lugar
    private void personalizaVista(ViewHolder holder, Alumno alumno) {

        holder.alumno.setText(alumno.getNombre()+" "+alumno.getApellido1()+" "+alumno.getApellido2());
        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        String imageUri;
        if (alumno.getFoto() != null){

            Log.d("FOTO", alumno.getNombre() + " " + alumno.getFoto());
            imageUri = alumno.getFoto();
        }
        else{
            imageUri="drawable://" + R.drawable.sinfoto;// from drawables (non-9patch images)
        }
        // Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view
        //	which implements ImageAware interface)
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //.displayer(new CircleBitmapDisplayer())
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        imageLoader.displayImage(imageUri, holder.foto, options);




    }

    @Override
    public int getItemCount() {
        return this.alumnoArrayList.size();
    }


    @Override
    public long getItemId(int posicion) {
        return posicion;
    }

    public int getItemPk(int posicion){ return this.alumnoArrayList.get(posicion).getPk();}

    public String getItemAlumno(int posicion){
        return this.alumnoArrayList.get(posicion).getNombre() + " " +
                this.alumnoArrayList.get(posicion).getApellido1() + " " +
                this.alumnoArrayList.get(posicion).getApellido2();}

}
