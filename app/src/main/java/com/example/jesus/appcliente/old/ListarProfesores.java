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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.ProfesorUserAdapter;
import com.example.jesus.appcliente.clases.ProfesorUser;
import com.example.jesus.appcliente.interfaces.ProfesorFormulario;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.HttpURLConnection;
import java.util.ArrayList;


public class ListarProfesores extends AppCompatActivity {

    private Spinner spinnerParametro;
    private EditText dato;
    private RecyclerView recyclerViewProfesores;
    private RecyclerView.LayoutManager layoutManager;
    private ProfesorUserAdapter adaptador;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listar_profesores);

        Bundle parametros = getIntent().getExtras();

        inicializar(parametros);
    }


    public void inicializar(Bundle param){
        this.spinnerParametro = (Spinner) findViewById(R.id.spinnerProfesorParametros);
        this.dato = (EditText) findViewById(R.id.editTextDato);


        this.recyclerViewProfesores = (RecyclerView) findViewById(R.id.recycler_view_profesores);
        ArrayList<ProfesorUser> profesores = new ArrayList<ProfesorUser>();
        adaptador = new ProfesorUserAdapter(ListarProfesores.this, profesores);
        recyclerViewProfesores.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(this);
        recyclerViewProfesores.setLayoutManager(layoutManager);
        recyclerViewProfesores.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ListarProfesores.this, ProfesorFormulario.class);
                i.putExtra("operacion", "actualizar");
                int posicion = (int) recyclerViewProfesores.getChildAdapterPosition(v);
                i.putExtra("idProfesor", adaptador.getItemPk(posicion));
                startActivity(i);
            }
        });



        if (param != null){
            this.spinnerParametro.setSelection((int)param.getLong("spinner"));
            this.dato.setText(param.getString("dato"));

        }

        new ListarProfesores.GetProfesores().execute();
    }

    public void btn_buscarProfe(View view){
        Intent intent = new Intent(this, ListarProfesores.class);
        intent.putExtra("spinner", spinnerParametro.getSelectedItemId());
        intent.putExtra("dato", dato.getText().toString().trim());
        startActivity(intent);
    }

    //Get profesores
    private class GetProfesores extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(Void... var1){
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
                        .getDefaultSharedPreferences(ListarProfesores.this);
                String token = settings.getString("auth_token", ""/*default value*/);


                //Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/profesores/");
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
                Toast.makeText(ListarProfesores.this,"No se generaron resultados", Toast.LENGTH_LONG).show();

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
                    /*ProfesorUserAdapter adapter = new ProfesorUserAdapter(ListarProfesores.this, profesores_aux);
                    listviewProfesor.setAdapter(adapter);
                    listviewProfesor.setOnItemClickListener( new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent , View view , int position ,long arg3)
                        {
                            Intent i = new Intent(ListarProfesores.this, ProfesorFormulario.class);
                            i.putExtra("operacion", "actualizar");
                            i.putExtra("idProfesor", ((ProfesorUser) parent.getAdapter().getItem(position)).getPk());
                            startActivity(i);
                        }
                    });*/

                    adaptador.actualizar(profesores_aux);
                    recyclerViewProfesores.getAdapter().notifyDataSetChanged();
                }
                else{
                    Toast.makeText(ListarProfesores.this,"No se generaron resultados", Toast.LENGTH_LONG).show();
                }

            }
        }


    }

}
