package com.example.jesus.appcliente.clases;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jesus.appcliente.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jesus on 12/07/17.
 */

public class AlumnoAdapter extends RecyclerView.Adapter<AlumnoAdapter.ViewHolder> {

    protected ArrayList<Alumno> alumnoArrayList;
    protected LayoutInflater inflador;
    protected Context contexto;
    protected View.OnClickListener onClickListener;

    public AlumnoAdapter(Context contexto, ArrayList<Alumno> alumnoArrayList) {
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
        public TextView apellido1, apellido2, nombre, grupo, edad, email;
        public ImageView foto;

        public ViewHolder(View itemView) {
            super(itemView);

            apellido1 = (TextView) itemView.findViewById(R.id.textViewAp1Alumno);
            apellido2 = (TextView) itemView.findViewById(R.id.textViewAp2Alumno);
            nombre = (TextView) itemView.findViewById(R.id.textViewNombreAlumno);
            grupo = (TextView) itemView.findViewById(R.id.textViewGrupoAlumno);
            edad = (TextView) itemView.findViewById(R.id.textViewEdadAlumno);
            email = (TextView) itemView.findViewById(R.id.textViewEmailAlumno);
            foto = (ImageView) itemView.findViewById(R.id.imageViewAlumno);
        }
    }

    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = inflador.inflate(R.layout.layout_alumno, parent, false);
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
        holder.apellido1.setText(alumno.getApellido1());
        holder.apellido2.setText(alumno.getApellido2());
        holder.nombre.setText(alumno.getNombre());
        holder.grupo.setText(alumno.getGrupo());

        String fecha_nacimiento = alumno.getFecha_nacimiento();
        if (fecha_nacimiento != null){
            int edad=0;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try {

                Date nac = formatter.parse(fecha_nacimiento);
                //inicializamos el objeto Calendar
                Calendar calendario = Calendar.getInstance();
                //colocamos la fecha en nuestro objeto Calendar
                calendario.setTime(nac);
                edad = calculaEdad(calendario);

            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.edad.setText(Integer.toString(edad) + " años");
        }

        if (alumno.getEmail().isEmpty()){
            holder.email.setText("E-mail: ---");
        }
        else{
            holder.email.setText("E-mail: " + alumno.getEmail());
        }

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
                .displayer(new CircleBitmapDisplayer())
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        imageLoader.displayImage(imageUri, holder.foto, options);


        /*
        //extraemos el drawable en un bitmap
        Drawable originalDrawable = getResources().getDrawable(R.drawable.image);
        Bitmap originalBitmap = ((BitmapDrawable) originalDrawable).getBitmap();

        //creamos el drawable redondeado
        RoundedBitmapDrawable roundedDrawable =
                RoundedBitmapDrawableFactory.create(getResources(), originalBitmap);

        //asignamos el CornerRadius
        roundedDrawable.setCornerRadius(originalBitmap.getHeight());

        ImageView imageView = (ImageView) findViewById(R.id.imageView);

        imageView.setImageDrawable(roundedDrawable);
        */






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



    private int calculaEdad(Calendar fechaNac) {
        Calendar today = Calendar.getInstance();

        int diff_year = today.get(Calendar.YEAR) -  fechaNac.get(Calendar.YEAR);
        int diff_month = today.get(Calendar.MONTH) - fechaNac.get(Calendar.MONTH);
        int diff_day = today.get(Calendar.DAY_OF_MONTH) - fechaNac.get(Calendar.DAY_OF_MONTH);

        //Si está en ese año pero todavía no los ha cumplido
        if (diff_month < 0 || (diff_month == 0 && diff_day < 0)) {
            diff_year = diff_year - 1; //no aparecían los dos guiones del postincremento :|
        }
        return diff_year;
    }
}
