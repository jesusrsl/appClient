package com.example.jesus.appcliente.interfaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.Asignatura;
import com.example.jesus.appcliente.clases.AsignaturaAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListarAsignaturas extends AppCompatActivity {

    private ListView listviewAsignaturas;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_asignaturas);

        inicializar();
    }

    public void inicializar(){
        this.listviewAsignaturas = (ListView) findViewById(R.id.listViewAsignaturas);


        new ListarAsignaturas.GetAsignaturas().execute();
    }

    //Get profesores
    private class GetAsignaturas extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(Void... var1){

            StringBuilder result = new StringBuilder();

            try{

                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(ListarAsignaturas.this);
                String token = settings.getString("auth_token", ""/*default value*/);

                URL url = new URL("http://192.168.200.137:8000/api/asignaturas/");
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
                Toast.makeText(ListarAsignaturas.this,"No se generaron resultados", Toast.LENGTH_LONG).show();

            }
            else{
                ArrayList<Asignatura> asignaturas = Asignatura.obtenerAsignaturas(result);

                if(asignaturas.size() != 0){
                    AsignaturaAdapter adapter = new AsignaturaAdapter(ListarAsignaturas.this, asignaturas);
                    listviewAsignaturas.setAdapter(adapter);
                    listviewAsignaturas.setOnItemClickListener( new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent , View view , int position , long arg3)
                        {
                            Intent i = new Intent(ListarAsignaturas.this, ListarAlumnadoAsignatura.class);
                            i.putExtra("idAsignatura", ((Asignatura) parent.getAdapter().getItem(position)).getPk());
                            startActivity(i);
                        }
                    });
                }
                else{
                    Toast.makeText(ListarAsignaturas.this,"No se generaron resultados", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
