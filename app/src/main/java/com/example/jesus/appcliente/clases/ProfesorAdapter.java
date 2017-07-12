package com.example.jesus.appcliente.clases;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;

import java.util.ArrayList;
import android.widget.Toast;


/**
 * Created by jesus on 10/07/17.
 */

public class ProfesorAdapter extends BaseAdapter {

    Context context;
    ArrayList<ProfesorUser> profesorArrayList;

    public ProfesorAdapter(Context context, ArrayList<ProfesorUser> profesorArrayList) {
        this.context = context;
        this.profesorArrayList = profesorArrayList;
    }

    @Override
    public int getCount() {
        return this.profesorArrayList.size();
    }

    @Override
    public Object getItem(int i) {
        return this.profesorArrayList.get(i);
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
            view = layoutInflater.inflate(R.layout.layout_profesor, viewGroup, false);
        }


        //Objeto de formulario
        TextView id = (TextView) view.findViewById(R.id.textViewId);
        TextView first_name = (TextView) view.findViewById(R.id.textViewFirst_name);
        TextView last_name = (TextView) view.findViewById(R.id.textViewLast_name);
        TextView username = (TextView) view.findViewById(R.id.textViewUsername);
        TextView email = (TextView) view.findViewById(R.id.textViewEmail);

        ProfesorUser profesor = this.profesorArrayList.get(i);

        if (profesor != null){
            id.setText("Id: "+ profesor.getIdToString());
            first_name.setText("Nombre: "+ profesor.getFirst_name());
            last_name.setText("Apellidos: "+ profesor.getLast_name());
            username.setText("Nombre de usuario: "+ profesor.getUsername());
            email.setText("E-mail: "+ profesor.getEmail());
        }

        return view;

    }

    @Override
    public CharSequence[] getAutofillOptions() {
        return new CharSequence[0];
    }
}
