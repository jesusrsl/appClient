package com.example.jesus.appcliente.clases;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jesus.appcliente.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by jesus on 12/07/17.
 */

public class AlumnoAdapter extends BaseAdapter {

    Context context;
    ArrayList<Alumno> alumnoArrayList;

    public AlumnoAdapter(Context context, ArrayList<Alumno> alumnoArrayList) {
        this.context = context;
        this.alumnoArrayList = alumnoArrayList;
    }

    @Override
    public int getCount() {
        return this.alumnoArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.alumnoArrayList.get(i);
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
            view = layoutInflater.inflate(R.layout.layout_alumno, viewGroup, false);
        }


        //Objeto de formulario
        TextView pkAlumno = (TextView) view.findViewById(R.id.textViewPkAlumno);
        TextView nombreAlumno = (TextView) view.findViewById(R.id.textViewNombreAlumno);
        TextView ap1Alumno = (TextView) view.findViewById(R.id.textViewAp1Alumno);
        TextView ap2Alumno = (TextView) view.findViewById(R.id.textViewAp2Alumno);
        TextView fechaNacAlumno = (TextView) view.findViewById(R.id.textViewFechaNacAlumno);
        TextView emailAlumno = (TextView) view.findViewById(R.id.textViewEmailAlumno);
        ImageView fotoAlumno = (ImageView) view.findViewById(R.id.imageViewAlumno);

        Alumno alumno = this.alumnoArrayList.get(i);

        if (alumno != null){
            pkAlumno.setText("Identificador: "+ alumno.getIdToString());
            nombreAlumno.setText("Nombre: "+ alumno.getNombre());
            ap1Alumno.setText("Apellido1: "+ alumno.getApellido1());
            ap2Alumno.setText("Apellido2: "+ alumno.getApellido2());
            fechaNacAlumno.setText("Fecha de nacimiento: "+ alumno.getFecha_nacimiento());
            emailAlumno.setText("E-mail: "+ alumno.getEmail());

            ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
            String imageUri;
            if (alumno.getFoto() != null){

                Log.d("FOTO", alumno.getNombre() + alumno.getFoto());
                imageUri = alumno.getFoto();
            }
            else{
                imageUri="drawable://" + R.drawable.sinfoto;// from drawables (non-9patch images)
            }
            // Load image, decode it to Bitmap and display Bitmap in ImageView (or any other view
            //	which implements ImageAware interface)
            imageLoader.displayImage(imageUri, fotoAlumno);

        }

        return view;

    }

    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
