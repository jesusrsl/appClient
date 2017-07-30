package com.example.jesus.appcliente.interfaces;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.old.ListarAlumnos;
import com.example.jesus.appcliente.old.ListarAsignaturas;
import com.example.jesus.appcliente.old.ListarProfesores;


public class MainFragment extends Fragment {

    private Button boton1, boton2, boton3, boton4;

    public MainFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=true;
        MainActivity.isOtherFragmentShown=false;
        //Item del menú seleccionado
        MainActivity.navigationView.setCheckedItem(R.id.navigation_item_mis_asignaturas);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);


        boton1 = (Button)view.findViewById(R.id.button);
        boton2 = (Button)view.findViewById(R.id.button2);
        boton3 = (Button)view.findViewById(R.id.button3);
        boton4 = (Button)view.findViewById(R.id.button4);


        boton1.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view){
                Intent intent = new Intent(getActivity(), ProfesorFormulario.class);
                intent.putExtra("operacion", "insertar");
                startActivity(intent);
            }

        });

        boton2.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view){
                Intent intent = new Intent(getActivity(), ListarProfesores.class);
                startActivity(intent);
            }
        });

        boton3.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view){
                Intent intent = new Intent(getActivity(), ListarAlumnos.class);
                startActivity(intent);
            }
        });

        boton4.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view){
                Intent intent = new Intent(getActivity(), ListarAsignaturas.class);
                startActivity(intent);
            }
        });


        return view;


    }

}
