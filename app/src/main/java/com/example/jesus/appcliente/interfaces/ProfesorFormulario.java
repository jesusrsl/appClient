package com.example.jesus.appcliente.interfaces;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.ProfesorUser;

import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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
        Bundle param = getIntent().getExtras();
        this.operacion = param.getString("operacion");

        if(this.operacion.equals("actualizar")){
            this.idProfesor = param.getInt("idObjeto");
        }

        inicializar();
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
        }
        if(this.operacion.equals("insertar")){
            new InsertarProfesor().execute();
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
                urlConnection.setFixedLengthStreamingMode(jsonObject.toString().length());

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
                return false;
            } catch (java.io.IOException e) {
                return false;
            } catch (org.json.JSONException e) {
                return false;
            } catch (Exception e) {
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

                URL url = new URL("http://192.168.200.137:8000/api/profesores/" + Integer.toString(idProfesor) + "/");
                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("first_name", profesor.getFirst_name());
                jsonObject.put("last_name", profesor.getLast_name());
                jsonObject.put("username", profesor.getUsername());
                jsonObject.put("email", profesor.getEmail());


                OutputStreamWriter wr = new OutputStreamWriter(urlConnection.getOutputStream());
                wr.write(jsonObject.toString());
                wr.flush();

                /*
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                OutputStream os = con.getOutputStream();
                os.write(jsonObject.toString().getBytes("UTF-8"));
                os.close();
                 */

               return true;
            } catch (java.net.MalformedURLException e) {
                return false;
            } catch (java.io.IOException e) {
                return false;
            } catch (org.json.JSONException e) {
                return false;
            } catch (Exception e) {
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
}
