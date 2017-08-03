package com.example.jesus.appcliente.interfaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.Alumno;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class AlumnoEditFragment extends Fragment {

    private TextView nombre, apellido1, apellido2, fecha_nacimiento, email;
    private Button botonGuardar, botonCancelar;
    private Alumno alumno;
    private int idAlumno;
    private Bundle parametros;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.alumno_formulario, container, false);
        this.nombre = (TextView) view.findViewById(R.id.textViewNombreAlumno);
        this.apellido1 = (TextView) view.findViewById(R.id.textViewAp1Alumno);
        this.apellido2 = (TextView) view.findViewById(R.id.textViewAp2Alumno);
        this.fecha_nacimiento = (TextView) view.findViewById(R.id.textViewFechaAlumno);
        this.email = (TextView) view.findViewById(R.id.textViewEmailAlumno);

        this.botonGuardar = (Button) view.findViewById(R.id.buttonGuardar);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                btn_guardarAlumno(null);
            }
        });

        this.botonCancelar = (Button) view.findViewById(R.id.buttonCancelar);
        botonCancelar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                btn_cancelarAlumno(null);
            }
        });

        parametros = getActivity().getIntent().getExtras();
        this.idAlumno = parametros.getInt("idAlumno");
        return view;
    }


    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        new AlumnoEditFragment.GetAlumno().execute();

    }


    public void btn_guardarAlumno(View view) {
        alumno = new Alumno();
        alumno.setNombre(nombre.getText().toString().trim());
        alumno.setApellido1(apellido1.getText().toString().trim());
        alumno.setApellido2(apellido2.getText().toString().trim());
        alumno.setEmail(email.getText().toString().trim());

        SimpleDateFormat formatterInput = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat formatterOutput = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date nac = formatterInput.parse(fecha_nacimiento.getText().toString().trim());
            alumno.setFecha_nacimiento(formatterOutput.format(nac));
        } catch (ParseException e) {
            e.printStackTrace();};

        new AlumnoEditFragment.ActualizarAlumno().execute();

        //vuelta al fragment AlumnoDetail
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();

    }

    public void btn_cancelarAlumno(View view) {
        //vuelta al fragment AlumnoDetail sin actualizar los datos
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.popBackStack();
    }

    /*public void volver(){
        //vuelta al fragment AlumnoDetail
        Intent intent = getActivity().getIntent();
        intent.putExtra("idAlumno", idAlumno);

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        AlumnoDetailFragment fragmentAlumnoDetail = new AlumnoDetailFragment();
        fragmentAlumnoDetail.setArguments(intent.getExtras());
        transaction.replace(R.id.container, fragmentAlumnoDetail);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null).commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }*/

    //Get profesores
    private class GetAlumno extends AsyncTask<Void, Void, Alumno> {

        HttpURLConnection urlConnection;

        public Alumno doInBackground(Void... var1){

            StringBuilder result = new StringBuilder();

            try{

                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);

                //Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/alumno/" + Integer.toString(idAlumno) + "/");
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

                alumno = Alumno.obtenerAlumno(result.toString());
                return alumno;

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

        public void onPostExecute(Alumno alumno){
            super.onPostExecute(alumno);

            if(alumno != null){
                nombre.setText(alumno.getNombre());
                apellido1.setText(alumno.getApellido1());
                apellido2.setText(alumno.getApellido2());
                SimpleDateFormat formatterInput = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat formatterOutput = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date nac = formatterInput.parse(alumno.getFecha_nacimiento());
                    fecha_nacimiento.setText(formatterOutput.format(nac));
                } catch (ParseException e) {
                    e.printStackTrace();}
                email.setText(alumno.getEmail());
            }
            else{
                Toast.makeText(getActivity(),"Error al cargar el alumno/a", Toast.LENGTH_LONG).show();
            }
        }

    }

    private class ActualizarAlumno extends AsyncTask<Void, Void, Boolean> {

        HttpURLConnection urlConnection;

        public Boolean doInBackground(Void... var1) {
            try {

                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);

                //Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/alumno/" + Integer.toString(idAlumno) + "/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "JWT " + token);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nombre", alumno.getNombre());
                jsonObject.put("apellido1", alumno.getApellido1());
                jsonObject.put("apellido2", alumno.getApellido2());
                jsonObject.put("fecha_nacimiento", alumno.getFecha_nacimiento());
                jsonObject.put("email", alumno.getEmail());
                //urlConnection.setFixedLengthStreamingMode(jsonObject.toString().length());

                Log.d("JSONUPDATE", jsonObject.toString());
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
                    urlConnection.disconnect();
                    Log.d("UPDATED","" + sb.toString());
                    return true;
                } else {
                    Log.d("NOTUPDATED",urlConnection.getResponseMessage());
                    urlConnection.disconnect();
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
                mensaje = "Alumno actualizado correctamente";
            }
            else{
                mensaje = "Problemas al actualizar el alumno/a";
            }
            Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();
        }
    }


}
