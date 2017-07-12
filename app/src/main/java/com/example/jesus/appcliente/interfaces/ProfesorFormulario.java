package com.example.jesus.appcliente.interfaces;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.ProfesorAdapter;
import com.example.jesus.appcliente.clases.ProfesorUser;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.client.HttpClient;


public class ProfesorFormulario extends AppCompatActivity {

    EditText nombre, apellidos, user, correo;
    ProfesorUser profesor;
    int idProfesor;
    String operacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profesor_formulario);

        inicializar();

        Bundle param = getIntent().getExtras();
        this.operacion = param.getString("operacion");

        if(this.operacion.equals("actualizar")){
            this.idProfesor = param.getInt("idProfesor");
            new GetProfesor().execute();
        }
        else{
            this.idProfesor = -1;
        }
    }

    public void inicializar() {
        this.nombre = (EditText) findViewById(R.id.editTextNombre);
        this.apellidos = (EditText) findViewById(R.id.editTextApellidos);
        this.user = (EditText) findViewById(R.id.editTextUser);
        this.correo = (EditText) findViewById(R.id.editTextCorreo);
    }

    public void btn_guardarProfesor(View view) {
        profesor = new ProfesorUser();
        profesor.setFirst_name(nombre.getText().toString().trim());
        profesor.setLast_name(apellidos.getText().toString().trim());
        profesor.setUsername(user.getText().toString().trim());
        profesor.setEmail(correo.getText().toString().trim());

        if(this.operacion.equals("actualizar")){
            new ActualizarProfesor().execute();
            //vuelta al listado de todos los profesores
            Intent intent = new Intent(ProfesorFormulario.this, BuscarProfesor.class);
            startActivity(intent);
        }
        if(this.operacion.equals("insertar")){
            new InsertarProfesor().execute();
            //vuelta a la actividad principal
            Intent intent = new Intent(ProfesorFormulario.this, MainActivity.class);
            startActivity(intent);
        }

    }

    public void btn_eliminarProfesor(View view) {
        if (idProfesor > 0 ) {
            new EliminarProfesor().execute();
            //vuelta al listado de todos los profesores
            Intent intent = new Intent(ProfesorFormulario.this, BuscarProfesor.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(ProfesorFormulario.this, "Opción disponible para profesores registrados", Toast.LENGTH_LONG).show();
        }


    }

    //Insertar profesor
    private class InsertarProfesor extends AsyncTask<Void, Void, Boolean> {

        HttpURLConnection urlConnection;

        public Boolean doInBackground(Void... var1) {

            try {

                URL url = new URL("http://192.168.200.137:8000/api/profesores/");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                //urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("username", profesor.getUsername());
                jsonObject.put("first_name", profesor.getFirst_name());
                jsonObject.put("last_name", profesor.getLast_name());
                jsonObject.put("email", profesor.getEmail());
                //urlConnection.setFixedLengthStreamingMode(jsonObject.toString().length());

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                StringBuilder sb = new StringBuilder();
                int HttpResult = urlConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_CREATED) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    Log.d("CREATED","" + sb.toString());
                    return true;
                } else {
                    Log.d("NOTCREATED",urlConnection.getResponseMessage());
                    return false;
                }

            } catch (java.net.MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return false;
            } catch (org.json.JSONException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                urlConnection.disconnect();
            }



/*
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost("http://192.168.200.137:8000/api/profesores/");
            httpPost.setHeader("Content-Type", "application/json");

            JSONObject jsonObject = new JSONObject();
            try{
                jsonObject.put("first_name", profesor.getFirst_name());
                jsonObject.put("last_name", profesor.getLast_name());
                jsonObject.put("username", profesor.getUsername());
                jsonObject.put("email", profesor.getEmail());

                StringEntity stringEntity = new StringEntity(jsonObject.toString());
                httpPost.setEntity(stringEntity);
                httpClient.execute(httpPost);

                return true;
            }
            catch (org.json.JSONException e){
                return false;
            }
            catch (java.io.UnsupportedEncodingException e){
                return false;
            }
            catch (org.apache.http.client.ClientProtocolException e){
                return false;
            }
            catch (java.io.IOException e){
                return false;
            }
*/
        }

        public void onPostExecute(Boolean result) {

            if (result) {
                Toast.makeText(ProfesorFormulario.this, "Profesor insertado correctamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ProfesorFormulario.this, "Problemas al insertar. El nombre de usuario debe ser único", Toast.LENGTH_LONG).show();
            }

        }


    }

    private class ActualizarProfesor extends AsyncTask<Void, Void, Boolean> {

        HttpURLConnection urlConnection;

        public Boolean doInBackground(Void... var1) {
            try {

                URL url = new URL("http://192.168.200.137:8000/api/profesor/" + Integer.toString(idProfesor) + "/");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("first_name", profesor.getFirst_name());
                jsonObject.put("last_name", profesor.getLast_name());
                jsonObject.put("username", profesor.getUsername());
                jsonObject.put("email", profesor.getEmail());
                //urlConnection.setFixedLengthStreamingMode(jsonObject.toString().length());

                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();

                StringBuilder sb = new StringBuilder();
                int HttpResult = urlConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK) {
                    BufferedReader br = new BufferedReader(
                            new InputStreamReader(urlConnection.getInputStream(), "utf-8"));
                    String line = null;
                    while ((line = br.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    br.close();
                    Log.d("UPDATED","" + sb.toString());
                    return true;
                } else {
                    Log.d("NOTUPDATED",urlConnection.getResponseMessage());
                    return false;
                }

            } catch (java.net.MalformedURLException e) {
                e.printStackTrace();
                return false;
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return false;
            } catch (org.json.JSONException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                urlConnection.disconnect();
            }
        }

        public void onPostExecute(Boolean result) {
            String mensaje;
            if(result){
                mensaje = "Profesor actualizado correctamente";
            }
            else{
                mensaje = "Problemas al actualizar";
            }
            Toast.makeText(ProfesorFormulario.this, mensaje, Toast.LENGTH_LONG).show();
        }
    }


    //Get profesores
    private class GetProfesor extends AsyncTask<Void, Void, ProfesorUser> {

        HttpURLConnection urlConnection;

        public ProfesorUser doInBackground(Void... var1){

            StringBuilder result = new StringBuilder();

            try{
                URL url = new URL("http://192.168.200.137:8000/api/profesor/" + Integer.toString(idProfesor) + "/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonObject = new JSONObject(result.toString());

                profesor = new ProfesorUser();
                profesor.setFirst_name(jsonObject.getString("first_name"));
                profesor.setLast_name(jsonObject.getString("last_name"));
                profesor.setUsername(jsonObject.getString("username"));
                profesor.setEmail(jsonObject.getString("email"));

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

        public void onPostExecute(ProfesorUser profesor){
            super.onPostExecute(profesor);
            if(profesor != null){
                nombre.setText(profesor.getFirst_name());
                apellidos.setText(profesor.getLast_name());
                user.setText(profesor.getUsername());
                correo.setText(profesor.getEmail());
            }
            else{
                Toast.makeText(ProfesorFormulario.this,"Error al cargar el profesor", Toast.LENGTH_LONG).show();
            }
        }


    }

    private class EliminarProfesor extends AsyncTask<Void, Void, Boolean> {

        HttpURLConnection urlConnection;

        public Boolean doInBackground(Void... var1) {

            try {

                URL url = new URL("http://192.168.200.137:8000/api/profesor/" + Integer.toString(idProfesor) + "/");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                urlConnection.setRequestMethod("DELETE");

                urlConnection.connect();

                int HttpResult = urlConnection.getResponseCode();
                if (HttpResult == HttpURLConnection.HTTP_OK || HttpResult == HttpURLConnection.HTTP_NO_CONTENT) {
                    Log.d("DELETED","" + urlConnection.getResponseMessage());
                    return true;
                } else {
                    Log.d("NOTDELETED",urlConnection.getResponseMessage());
                    return false;
                }

            } catch (java.io.IOException e) {
                e.printStackTrace();
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally {
                urlConnection.disconnect();
            }
        }

        public void onPostExecute(Boolean result) {

            if (result) {
                Toast.makeText(ProfesorFormulario.this, "Profesor eliminado correctamente", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(ProfesorFormulario.this, "Problemas al eliminar", Toast.LENGTH_LONG).show();
            }

        }


    }


}
