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

public class ListarAlumnadoTutoriaFragment extends Fragment {

    private TextView textViewGrupo;

    private RecyclerView recyclerViewAlumnadoTutoria;
    private RecyclerView.LayoutManager layoutManager;
    private AlumnoAdapter adaptador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_listar_alumnado_tutoria, container, false);
        this.recyclerViewAlumnadoTutoria = (RecyclerView) view.findViewById(R.id.recycler_view_alumnado_tutoria);
        this.textViewGrupo = (TextView) view.findViewById(R.id.textViewGrupo);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        ArrayList<Alumno> alumnos = new ArrayList<Alumno>();
        adaptador = new AlumnoAdapter(getContext(), alumnos);
        recyclerViewAlumnadoTutoria.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(getContext());
        recyclerViewAlumnadoTutoria.setLayoutManager(layoutManager);
        recyclerViewAlumnadoTutoria.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getActivity().getIntent();
                int posicion = (int) recyclerViewAlumnadoTutoria.getChildAdapterPosition(v);
                intent.putExtra("idAlumno", adaptador.getItemPk(posicion));

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                AlumnoDetailFragment fragmentAlumnoDetail = new AlumnoDetailFragment();
                fragmentAlumnoDetail.setArguments(intent.getExtras());
                transaction.replace(R.id.container, fragmentAlumnoDetail);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null).commit();
                getActivity().getSupportFragmentManager().executePendingTransactions();
            }
        });

        new ListarAlumnadoTutoriaFragment.GetAlumnado().execute();
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
                URL url = new URL(domain + "api/tutoria/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Authorization", "JWT " + token);

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                JSONArray jsonArray = new JSONArray(result.toString());
                Log.d("JSON", jsonArray.getJSONObject(0).toString());
                return jsonArray.getJSONObject(0).toString();
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

            //return result.toString();

        }

        public void onPostExecute(String result){

            if(result.isEmpty()){
                textViewGrupo.setText("---");
                Toast.makeText(getActivity(),"Actualmente no tutoriza ningún grupo", Toast.LENGTH_LONG).show();

            }
            else{
                AlumnadoGrupo alumnado = AlumnadoGrupo.obtenerAlumnadoGrupo(result);

                textViewGrupo.setText(alumnado.getGrupo());

                if(!alumnado.getAlumnos().isEmpty()) {
                    adaptador.actualizar(alumnado.getAlumnos());
                    recyclerViewAlumnadoTutoria.getAdapter().notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getActivity(),"No existe alumnado matriculado en el grupo", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
