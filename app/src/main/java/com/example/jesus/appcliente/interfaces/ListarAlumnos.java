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
import com.example.jesus.appcliente.clases.Alumno;
import com.example.jesus.appcliente.clases.AlumnoAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ListarAlumnos extends AppCompatActivity {

    private ListView listviewAlumnos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_alumnos);
        inicializar();

        new GetAlumnos().execute("http://192.168.200.137:8000/api/alumnos/");
    }

    public void inicializar(){
        this.listviewAlumnos = (ListView) findViewById(R.id.listViewAlumnos);
    }

    //Get profesores
    private class GetAlumnos extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(String... var1){

            StringBuilder result = new StringBuilder();

            try{
                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(ListarAlumnos.this);
                String token = settings.getString("auth_token", ""/*default value*/);


                URL url = new URL(var1[0]);
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
                Toast.makeText(ListarAlumnos.this,"No se generaron resultados", Toast.LENGTH_LONG).show();

            }
            else{
                ArrayList<Alumno> alumnos = Alumno.obtenerAlumnos(result);

                if(alumnos.size() != 0){
                    AlumnoAdapter adapter = new AlumnoAdapter(ListarAlumnos.this, alumnos);
                    listviewAlumnos.setAdapter(adapter);
                    /*listviewAlumnos.setOnItemClickListener( new AdapterView.OnItemClickListener()
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
                    Toast.makeText(ListarAlumnos.this,"No se generaron resultados", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
