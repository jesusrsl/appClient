package com.example.jesus.appcliente.interfaces;

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
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.Alumno;
import com.example.jesus.appcliente.clases.AlumnoAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListarAlumnosFragment extends Fragment {

    private RecyclerView recyclerViewAlumnos;
    private RecyclerView.LayoutManager layoutManager;
    private AlumnoAdapter adaptador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.listar_alumnos, container, false);
        this.recyclerViewAlumnos = (RecyclerView) view.findViewById(R.id.recycler_view_alumnos);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        ArrayList<Alumno> alumnos = new ArrayList<Alumno>();
        adaptador = new AlumnoAdapter(getContext(), alumnos);
        recyclerViewAlumnos.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(getContext());
        recyclerViewAlumnos.setLayoutManager(layoutManager);
        recyclerViewAlumnos.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        /*adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ListarAlumnos.this, EditarAlumno.class);
                int posicion = (int) recyclerViewAlumnos.getChildAdapterPosition(v);
                i.putExtra("idAlumno", adaptador.getItemPk(posicion));
                startActivity(i);
            }
        });*/

        new ListarAlumnosFragment.GetAlumnos().execute();
    }


    //Get profesores
    private class GetAlumnos extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(Void... var1){

            StringBuilder result = new StringBuilder();

            try{
                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);


                //Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/alumnos/");
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
                ArrayList<Alumno> alumnos = Alumno.obtenerAlumnos(result);


                if(alumnos.size() != 0){

                    adaptador.actualizar(alumnos);
                    recyclerViewAlumnos.getAdapter().notifyDataSetChanged();

                    /*AlumnoAdapter adapter = new AlumnoAdapter(ListarAlumnos.this, alumnos);
                    listviewAlumnos.setAdapter(adapter);
                    listviewAlumnos.setOnItemClickListener( new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent , View view , int position , long arg3)
                        {
                            Intent i = new Intent(ListarAlumnos.this, AlumnoFormulario.class);
                            i.putExtra("operacion", "actualizar");
                            i.putExtra("idAlumno", ((Alumno) parent.getAdapter().getItem(position)).getId());
                            startActivity(i);
                        }
                    });*/
                }
                else{
                    Toast.makeText(getActivity(),"No se generaron resultados", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
