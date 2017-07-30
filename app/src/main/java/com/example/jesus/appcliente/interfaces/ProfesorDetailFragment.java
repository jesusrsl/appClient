package com.example.jesus.appcliente.interfaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.ProfesorDetail;
import com.example.jesus.appcliente.clases.ProfesorUser;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class ProfesorDetailFragment extends Fragment {

    private TextView nombre, apellidos;
    private ListView asignaturas;
    private ProfesorDetail profesor;
    private int idProfesor;
    private Bundle parametros;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_profesor_detail, container, false);
        this.apellidos = (TextView) view.findViewById(R.id.textViewApellidosDetail);
        this.nombre = (TextView) view.findViewById(R.id.textViewNombreDetail);
        this.asignaturas = (ListView) view.findViewById(R.id.listViewAsignaturas);
        parametros = getActivity().getIntent().getExtras();
        this.idProfesor = parametros.getInt("idProfesor");
        return view;
    }


    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        new ProfesorDetailFragment.GetProfesor().execute();

    }


    //Get profesores
    private class GetProfesor extends AsyncTask<Void, Void, ProfesorDetail> {

        HttpURLConnection urlConnection;

        public ProfesorDetail doInBackground(Void... var1){

            StringBuilder result = new StringBuilder();

            try{

                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);

                //Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/profesor/" + Integer.toString(idProfesor) + "/");
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
                //JSONObject jsonObject = new JSONObject(result.toString());

                profesor = ProfesorDetail.obtenerProfesor(result.toString());
                return profesor;

            }
            catch (java.net.MalformedURLException e){
                e.printStackTrace();
                return null;
            }
            catch(java.io.IOException e){
                e.printStackTrace();
                return null;
            }
            catch(Exception e){
                e.printStackTrace();
                return null;
            }
            finally {
                urlConnection.disconnect();
            }

        }

        public void onPostExecute(ProfesorDetail profesor){
            super.onPostExecute(profesor);

            if(profesor != null){
                nombre.setText(profesor.getFirst_name());
                apellidos.setText(profesor.getLast_name());
                if (!profesor.getAsignatura_set().isEmpty()){
                    asignaturas.setAdapter(new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, profesor.getAsignatura_set()));
                }
            }
            else{
                Toast.makeText(getActivity(),"Error al cargar el profesor", Toast.LENGTH_LONG).show();
            }
        }

    }


}
