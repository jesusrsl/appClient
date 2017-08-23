package com.example.jesus.appcliente.interfaces;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.DetalleAsignatura;
import com.example.jesus.appcliente.clases.AlumnoClase;
import com.example.jesus.appcliente.clases.AlumnoClaseAdapter;
import com.example.jesus.appcliente.clases.DialogoSelectorFecha;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DetalleAsignaturaFragment extends Fragment implements DatePickerDialog.OnDateSetListener{

    private TextView textViewAsignatura, textViewGrupo, textViewFecha;
    private int idAsignatura;
    private long fecha;

    private RecyclerView recyclerViewDetalleAsignatura;
    private RecyclerView.LayoutManager layoutManager;
    private AlumnoClaseAdapter adaptador;
    private Bundle parametros;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.detalle_asignatura, container, false);

        parametros = getActivity().getIntent().getExtras();
        this.idAsignatura = parametros.getInt("idAsignatura");
        this.fecha = parametros.getLong("fecha");

        this.textViewFecha = (TextView) view.findViewById(R.id.textViewFecha);
        textViewFecha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cambiarFecha();
            }
        });
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        textViewFecha.setText(formato.format(new Date(fecha)));

        this.recyclerViewDetalleAsignatura = (RecyclerView) view.findViewById(R.id.recycler_view_detalle_asignatura);
        this.textViewAsignatura = (TextView) view.findViewById(R.id.textViewAsignatura);
        this.textViewGrupo = (TextView) view.findViewById(R.id.textViewGrupo);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        ArrayList<AlumnoClase> alumnos = new ArrayList<AlumnoClase>();
        adaptador = new AlumnoClaseAdapter(getContext(), alumnos, idAsignatura, fecha);
        recyclerViewDetalleAsignatura.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(getContext());
        recyclerViewDetalleAsignatura.setLayoutManager(layoutManager);
        recyclerViewDetalleAsignatura.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        /*adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicion = (int) recyclerViewDetalleAsignatura.getChildAdapterPosition(v);
                Log.d("POSICION", Integer.toString(posicion));
            }
        });*/

        new DetalleAsignaturaFragment.GetAlumnado().execute();
    }


    public void cambiarFecha() {
        DialogoSelectorFecha dialogoFecha = new DialogoSelectorFecha();
        dialogoFecha.setOnDateSetListener(this);
        Bundle args = new Bundle();
        args.putLong("fecha", fecha);
        dialogoFecha.setArguments(args);
        dialogoFecha.show(getActivity().getSupportFragmentManager(), "selectorFecha");
    }

    @Override
    public void onDateSet(DatePicker vista, int anyo, int mes, int dia) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(fecha);
        calendario.set(Calendar.YEAR, anyo);
        calendario.set(Calendar.MONTH, mes);
        calendario.set(Calendar.DAY_OF_MONTH, dia);
        fecha = calendario.getTimeInMillis();

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        //DateFormat formato =  DateFormat.getDateInstance();
        textViewFecha.setText(formato.format(new Date(fecha)));
        //se actualiza las anotaciones del alumnado en la fecha indicada
        new DetalleAsignaturaFragment.GetAlumnado().execute();
    }


    //Get alumado
    private class GetAlumnado extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(Void... var1){

            StringBuilder result = new StringBuilder();

            try{
                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);

                Date fechaConsulta = new Date(fecha);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String fechaParameter = formatter.format(fechaConsulta);

                // Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/asignatura/" + Integer.toString(idAsignatura) + "/" + fechaParameter + "/detalle/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Authorization", "JWT " + token);

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                Log.d("JSON", result.toString());
            }
            catch (java.net.MalformedURLException e){
                return "";
            }
            catch(java.io.IOException e){
                return "";
            }
            catch(Exception e){
                return "";
            }
            finally {
                urlConnection.disconnect();
            }

            return result.toString();

        }

        public void onPostExecute(String result){

            if(result.isEmpty()){
                Toast.makeText(getActivity(),"No hay alumnado matriculado", Toast.LENGTH_LONG).show();

            }
            else{
                DetalleAsignatura alumnado = DetalleAsignatura.obtenerAlumnadoAsignatura(result);

                textViewAsignatura.setText(alumnado.getNombre());
                textViewGrupo.setText(alumnado.getGrupo());

                if(!alumnado.getAlumnos().isEmpty()) {
                    adaptador.actualizar(alumnado.getAlumnos(), idAsignatura, fecha);
                    recyclerViewDetalleAsignatura.getAdapter().notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getActivity(),"No se generaron resultados", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
