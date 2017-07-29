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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.ProfesorAdapter;
import com.example.jesus.appcliente.clases.ProfesorUser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;


public class BuscarProfesor extends AppCompatActivity {

    private Spinner spinnerParametro;
    private EditText dato;
    private ListView listviewProfesor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_profesor);

        Bundle parametros = getIntent().getExtras();

        inicializar(parametros);
    }


    public void inicializar(Bundle param){
        this.spinnerParametro = (Spinner) findViewById(R.id.spinnerProfesorParametros);
        this.dato = (EditText) findViewById(R.id.editTextDato);
        this.listviewProfesor = (ListView) findViewById(R.id.listViewProfesores);

        if (param != null){
            this.spinnerParametro.setSelection((int)param.getLong("spinner"));
            this.dato.setText(param.getString("dato"));

        }

        new GetProfesores().execute("http://192.168.200.137:8000/api/profesores/");
    }

    public void btn_buscarProfe(View view){
        Intent intent = new Intent(this, BuscarProfesor.class);
        intent.putExtra("spinner", spinnerParametro.getSelectedItemId());
        intent.putExtra("dato", dato.getText().toString().trim());
        startActivity(intent);
    }

    //Get profesores
    private class GetProfesores extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(String... var1){
            /*try{
                return HttpRequest.get(var1[0]).accept("application/json").body();
            }
            catch(Exception e){
                return "";
            }*/

            StringBuilder result = new StringBuilder();

            try{
                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(BuscarProfesor.this);
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
                Toast.makeText(BuscarProfesor.this,"No se generaron resultados", Toast.LENGTH_LONG).show();

            }
            else{
                ArrayList<ProfesorUser> profesores = ProfesorUser.obtenerProfesores(result);


                ArrayList<ProfesorUser> profesores_aux = new ArrayList<ProfesorUser>();

                if(spinnerParametro.getSelectedItem().toString().equals("Listar todo")){
                    profesores_aux = profesores;
                }
                else {
                    for (int i = 0; i < profesores.size(); i++){
                        switch(spinnerParametro.getSelectedItem().toString()){
                            case "Nombre":
                                if (profesores.get(i).getFirst_name().equals(dato.getText().toString().trim())){
                                    profesores_aux.add(profesores.get(i));
                                }
                                break;
                            case "Apellidos":
                                if (profesores.get(i).getLast_name().equals(dato.getText().toString().trim())){
                                    profesores_aux.add(profesores.get(i));
                                }
                                break;
                            case "Nombre de usuario":
                                if (profesores.get(i).getUsername().equals(dato.getText().toString().trim())){
                                    profesores_aux.add(profesores.get(i));
                                }
                                break;
                            case "E-mail":
                                if (profesores.get(i).getEmail().equals(dato.getText().toString().trim())){
                                    profesores_aux.add(profesores.get(i));
                                }
                                break;
                        }
                    }
                }

                if(profesores_aux.size() != 0){
                    ProfesorAdapter adapter = new ProfesorAdapter(BuscarProfesor.this, profesores_aux);
                    listviewProfesor.setAdapter(adapter);
                    listviewProfesor.setOnItemClickListener( new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent , View view , int position ,long arg3)
                        {
                            Intent i = new Intent(BuscarProfesor.this, ProfesorFormulario.class);
                            i.putExtra("operacion", "actualizar");
                            i.putExtra("idProfesor", ((ProfesorUser) parent.getAdapter().getItem(position)).getId());
                            startActivity(i);
                        }
                    });
                }
                else{
                    Toast.makeText(BuscarProfesor.this,"No se generaron resultados", Toast.LENGTH_LONG).show();
                }

            }
        }


    }

}
