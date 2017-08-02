package com.example.jesus.appcliente.interfaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.example.jesus.appcliente.clases.AlumnadoGrupo;
import com.example.jesus.appcliente.clases.Alumno;
import com.example.jesus.appcliente.clases.AlumnoAdapter;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListarAlumnadoGrupoFragment extends Fragment {

    private TextView textViewGrupo;
    private TextView textViewTutor;
    private int idGrupo;

    private RecyclerView recyclerViewAlumnadoGrupo;
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
        View view = inflater.inflate(R.layout.activity_listar_alumnado_grupo, container, false);
        this.recyclerViewAlumnadoGrupo = (RecyclerView) view.findViewById(R.id.recycler_view_alumnado_grupo);
        this.textViewGrupo = (TextView) view.findViewById(R.id.textViewGrupo);
        this.textViewTutor = (TextView) view.findViewById(R.id.textViewTutor);
        parametros = getActivity().getIntent().getExtras();
        this.idGrupo = parametros.getInt("idGrupo");

        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        ArrayList<Alumno> alumnos = new ArrayList<Alumno>();
        adaptador = new AlumnoAdapter(getContext(), alumnos);
        recyclerViewAlumnadoGrupo.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(getContext());
        recyclerViewAlumnadoGrupo.setLayoutManager(layoutManager);
        recyclerViewAlumnadoGrupo.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        new ListarAlumnadoGrupoFragment.GetAlumnado().execute();
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
                URL url = new URL(domain + "api/alumnado/grupos/" + Integer.toString(idGrupo));
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
                Toast.makeText(getActivity(),"No se generaron resultados", Toast.LENGTH_LONG).show();

            }
            else{
                AlumnadoGrupo alumnado = AlumnadoGrupo.obtenerAlumnadoGrupo(result);

                textViewGrupo.setText(alumnado.getGrupo());
                textViewTutor.setText(alumnado.getTutor());

                if(!alumnado.getAlumnos().isEmpty()) {
                    adaptador.actualizar(alumnado.getAlumnos());
                    recyclerViewAlumnadoGrupo.getAdapter().notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getActivity(),"No existe alumnado matriculado en el grupo", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
