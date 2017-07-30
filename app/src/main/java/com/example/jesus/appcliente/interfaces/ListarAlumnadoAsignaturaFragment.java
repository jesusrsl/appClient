package com.example.jesus.appcliente.interfaces;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.AlumnadoAsignatura;
import com.example.jesus.appcliente.clases.Alumno;
import com.example.jesus.appcliente.clases.AlumnoAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListarAlumnadoAsignaturaFragment extends Fragment {

    private TextView textViewAsignatura;
    private TextView textViewGrupo;
    private int idAsignatura;

    private RecyclerView recyclerViewAlumnadoAsignatura;
    private RecyclerView.LayoutManager layoutManager;
    private AlumnoAdapter adaptador;
    private Bundle parametros;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_listar_alumnado_asignatura, container, false);
        this.recyclerViewAlumnadoAsignatura = (RecyclerView) view.findViewById(R.id.recycler_view_alumnado_asignatura);
        this.textViewAsignatura = (TextView) view.findViewById(R.id.textViewAsignatura);
        this.textViewGrupo = (TextView) view.findViewById(R.id.textViewGrupo);
        parametros = getActivity().getIntent().getExtras();
        this.idAsignatura = parametros.getInt("idAsignatura");


        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        ArrayList<Alumno> alumnos = new ArrayList<Alumno>();
        adaptador = new AlumnoAdapter(getContext(), alumnos);
        recyclerViewAlumnadoAsignatura.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(getContext());
        recyclerViewAlumnadoAsignatura.setLayoutManager(layoutManager);
        recyclerViewAlumnadoAsignatura.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        new ListarAlumnadoAsignaturaFragment.GetAlumnado().execute();
    }


    //Get profesores
    private class GetAlumnado extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(Void... var1){

            StringBuilder result = new StringBuilder();

            try{
                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);

                // Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/alumnado/" + Integer.toString(idAsignatura));
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
                AlumnadoAsignatura alumnado = AlumnadoAsignatura.obtenerAlumnadoAsignatura(result);

                textViewAsignatura.setText(alumnado.getNombre());
                textViewGrupo.setText(alumnado.getGrupoText());

                if(alumnado.getAlumnos() != null) {
                    adaptador.actualizar(alumnado.getAlumnos());
                    recyclerViewAlumnadoAsignatura.getAdapter().notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getActivity(),"No se generaron resultados", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
