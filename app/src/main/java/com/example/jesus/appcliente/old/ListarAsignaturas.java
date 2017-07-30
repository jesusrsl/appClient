package com.example.jesus.appcliente.old;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
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

    private RecyclerView recyclerViewAsignaturas;
    private RecyclerView.LayoutManager layoutManager;
    private AsignaturaAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listar_asignaturas);

        inicializar();
    }

    public void inicializar(){
        this.recyclerViewAsignaturas = (RecyclerView) findViewById(R.id.recycler_view_asignaturas);
        ArrayList<Asignatura> asignaturas = new ArrayList<Asignatura>();
        adaptador = new AsignaturaAdapter(ListarAsignaturas.this, asignaturas);
        recyclerViewAsignaturas.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(this);
        recyclerViewAsignaturas.setLayoutManager(layoutManager);
        recyclerViewAsignaturas.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        adaptador.setOnItemClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Intent i = new Intent(ListarAsignaturas.this, ListarAlumnadoAsignatura.class);
                            int posicion = (int) recyclerViewAsignaturas.getChildAdapterPosition(v);
                            i.putExtra("idAsignatura", adaptador.getItemPk(posicion));
                            startActivity(i);
                        }
                    });

        new ListarAsignaturas.GetAsignaturas().execute();
    }

    //Get asignaturas
    private class GetAsignaturas extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(Void... var1){

            StringBuilder result = new StringBuilder();

            try{

                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(ListarAsignaturas.this);
                String token = settings.getString("auth_token", ""/*default value*/);

                //Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/lista/asignaturas/");
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

                    adaptador.actualizar(asignaturas);
                    recyclerViewAsignaturas.getAdapter().notifyDataSetChanged();


                }
                else{
                    Toast.makeText(ListarAsignaturas.this,"No se generaron resultados", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}

