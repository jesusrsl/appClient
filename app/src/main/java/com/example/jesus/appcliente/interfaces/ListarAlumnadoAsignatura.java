package com.example.jesus.appcliente.interfaces;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.AlumnadoAsignatura;
import com.example.jesus.appcliente.clases.AlumnoAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ListarAlumnadoAsignatura extends AppCompatActivity {

    private TextView textViewAsignatura;
    private TextView textViewGrupo;
    private ListView listviewAlumnadoAsignatura;
    private int idAsignatura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_alumnado_asignatura);

        inicializar();

        Bundle param = getIntent().getExtras();
        this.idAsignatura = param.getInt("idAsignatura");

        new ListarAlumnadoAsignatura.GetAlumnado().execute("http://192.168.200.137:8000/api/alumnado/" + Integer.toString(idAsignatura));
    }

    public void inicializar(){
        this.textViewAsignatura = (TextView) findViewById(R.id.textViewAsignatura);
        this.textViewGrupo = (TextView) findViewById(R.id.textViewGrupo);
        this.listviewAlumnadoAsignatura = (ListView) findViewById(R.id.listViewAlumnadoAsignatura);
    }

    //Get profesores
    private class GetAlumnado extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(String... var1){

            StringBuilder result = new StringBuilder();

            try{
                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(ListarAlumnadoAsignatura.this);
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
                Toast.makeText(ListarAlumnadoAsignatura.this,"No hay alumnado matriculado", Toast.LENGTH_LONG).show();

            }
            else{
                AlumnadoAsignatura alumnado = AlumnadoAsignatura.obtenerAlumnadoAsignatura(result);

                textViewAsignatura.setText(alumnado.getNombre());
                textViewGrupo.setText(alumnado.getGrupoText());

                if(alumnado.getAlumnos() != null) {
                    AlumnoAdapter adapter = new AlumnoAdapter(ListarAlumnadoAsignatura.this, alumnado.getAlumnos());
                    listviewAlumnadoAsignatura.setAdapter(adapter);
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
                    Toast.makeText(ListarAlumnadoAsignatura.this,"No se generaron resultados", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
