package com.example.jesus.appcliente.clases;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.interfaces.DetalleAsignaturaFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jesus on 12/07/17.
 */

public class AlumnoClaseAdapter extends RecyclerView.Adapter<AlumnoClaseAdapter.ViewHolder> {

    protected ArrayList<AlumnoClase> alumnoClaseArrayList;
    protected int idAsignatura;
    protected long fecha;
    protected LayoutInflater inflador;
    protected Context contexto;
    protected View.OnClickListener onClickListener;

    public AlumnoClaseAdapter(Context contexto, ArrayList<AlumnoClase> alumnoClaseArrayList, int idAsignatura, long fecha) {
        this.contexto = contexto;
        this.alumnoClaseArrayList = alumnoClaseArrayList;
        this.idAsignatura = idAsignatura;
        this.fecha = fecha;
        inflador = (LayoutInflater) contexto
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void actualizar(ArrayList<AlumnoClase> alumnoClaseArrayList, int idAsignatura, long fecha){
        this.alumnoClaseArrayList = alumnoClaseArrayList;
        this.idAsignatura = idAsignatura;
        this.fecha = fecha;
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public TextView nombreAlumno;
        public ImageView foto;
        public Button botonFalta, botonTrabaja, botonPositivo, botonNegativo, botonEditar;

        public ViewHolder(View itemView) {
            super(itemView);

            nombreAlumno = (TextView) itemView.findViewById(R.id.textViewNombreAlumno);
            foto = (ImageView) itemView.findViewById(R.id.imageViewAlumno);
            botonFalta = (Button) itemView.findViewById(R.id.buttonFalta);
            botonTrabaja = (Button) itemView.findViewById(R.id.buttonTrabaja);
            botonPositivo = (Button) itemView.findViewById(R.id.buttonPositivo);
            botonNegativo = (Button) itemView.findViewById(R.id.buttonNegativo);
            botonEditar = (Button) itemView.findViewById(R.id.buttonEdit);

            itemView.setOnClickListener(this);
            botonFalta.setOnClickListener(this);
            botonTrabaja.setOnClickListener(this);
            botonPositivo.setOnClickListener(this);
            botonNegativo.setOnClickListener(this);
            botonEditar.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
        }

        // onClick Listener for view
        @Override
        public void onClick(View v) {

            int pk = alumnoClaseArrayList.get(getAdapterPosition()).getPk();
            Anotacion anotacion = alumnoClaseArrayList.get(getAdapterPosition()).getAnotacion();
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            String fechaString = formato.format(new Date(fecha));
            String urlEditar="api/anotaciones";

            String urlPoner = "api/anotacion/nueva/" + Integer.toString(pk) + "/" + Integer.toString(idAsignatura) + "/"+fechaString + "/";
            if (anotacion != null){
                urlEditar = "api/anotacion/" + Integer.toString(anotacion.getPk()) + "/editar/";
            }

            if (v.getId() == botonFalta.getId()){
                if (anotacion != null){
                    ParametrosValoracion parametros = new ParametrosValoracion(urlEditar, "falta", pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosValoracion, Void, Boolean> task = new EditarAnotacion();
                    task.execute(parametros);
                }
                else{
                    ParametrosValoracion parametros = new ParametrosValoracion(urlPoner, "falta", pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosValoracion, Void, Boolean> task = new PonerAnotacion();
                    task.execute(parametros);
                }
            }
            else if (v.getId() == botonTrabaja.getId()){
                if (anotacion != null){
                    ParametrosValoracion parametros = new ParametrosValoracion(urlEditar, "trabaja", pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosValoracion, Void, Boolean> task = new EditarAnotacion();
                    task.execute(parametros);
                }
                else{
                    ParametrosValoracion parametros = new ParametrosValoracion(urlPoner, "trabaja", pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosValoracion, Void, Boolean> task = new PonerAnotacion();
                    task.execute(parametros);
                }
            }
            else if (v.getId() == botonPositivo.getId()) {
                if (anotacion != null) {
                    ParametrosValoracion parametros = new ParametrosValoracion(urlEditar, "positivo", pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosValoracion, Void, Boolean> task = new EditarAnotacion();
                    task.execute(parametros);
                }
                else{
                    ParametrosValoracion parametros = new ParametrosValoracion(urlPoner, "positivo", pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosValoracion, Void, Boolean> task = new PonerAnotacion();
                    task.execute(parametros);
                }
            }
            else if (v.getId() == botonNegativo.getId()){
                if (anotacion != null){
                    ParametrosValoracion parametros = new ParametrosValoracion(urlEditar, "negativo", pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosValoracion, Void, Boolean> task = new EditarAnotacion();
                    task.execute(parametros);
                }
                else{
                    ParametrosValoracion parametros = new ParametrosValoracion(urlPoner, "negativo", pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosValoracion, Void, Boolean> task = new PonerAnotacion();
                    task.execute(parametros);
                }
            }
            else if (v.getId() == botonEditar.getId()){

                mostrarDialogAnotaciones(v, anotacion);
                Toast.makeText(v.getContext(), "EDIT PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_LONG).show();
            }
        }


        public void mostrarDialogAnotaciones(View v, Anotacion anotacion) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
            View dialogView = inflador.inflate(R.layout.dialogo_anotaciones, null);
            Switch falta = (Switch) dialogView.findViewById(R.id.switchFalta);
            Switch trabaja = (Switch) dialogView.findViewById(R.id.switchTrabaja);
            EditText positivos = (EditText) dialogView.findViewById(R.id.editTextPositivos);
            EditText negativos = (EditText) dialogView.findViewById(R.id.editTextNegativos);

            if(anotacion != null){
                if (anotacion.getFalta()!=null){falta.setChecked(true);}else{falta.setChecked(false);}
                if (anotacion.isTrabaja()){trabaja.setChecked(true);}else{trabaja.setChecked(false);}
                if (anotacion.getPositivos()>0){
                    positivos.setText(Integer.toString(anotacion.getPositivos()));
                }
                if (anotacion.getNegativos()>0){
                    negativos.setText(Integer.toString(anotacion.getNegativos()));
                }
            }

            dialogBuilder.setView(dialogView);
            dialogBuilder.setPositiveButton("Guadar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialogBuilder.setNegativeButton("Cancelar", null);
            AlertDialog alertDialog = dialogBuilder.create();
            //alertDialog.getWindow().setLayout(100, 100);
            alertDialog.show();

            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            lp.copyFrom(alertDialog.getWindow().getAttributes());
            lp.width = 750;
            lp.height = 750;
            alertDialog.getWindow().setAttributes(lp);
            ///////////////////////////////////////////////////////////////

        }
        /*
        public void lanzarVistaLugar(View view){
            final EditText entrada = new EditText(this);
            entrada.setText("0");
            new AlertDialog.Builder(this)
                    .setTitle("Selección de lugar")
                    .setMessage("Indica su id:")
                    .setView(entrada)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int whichButton) {
                            long id = Long.parseLong(entrada.getText().toString());
                            Intent i = new Intent(MainActivity.this, VistaLugarActivity.class);
                            i.putExtra("id", id);
                            startActivityForResult(i, RESULTADO_VISTA_LUGAR);
                        }})
                    .setNegativeButton("Cancelar", null)
                    .show();
        }*/

        //onLongClickListener for view
        @Override
        public boolean onLongClick(View v) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setTitle ("Hello Dialog")
                    .setMessage ("LONG CLICK DIALOG WINDOW FOR ICON " + String.valueOf(getAdapterPosition()))
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

            builder.create().show();
            return true;
        }
    }

    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = inflador.inflate(R.layout.layout_alumno_clase, parent, false);
        v.setOnClickListener(onClickListener);
        return new ViewHolder(v);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        AlumnoClase alumnoClase = this.alumnoClaseArrayList.get(posicion);
        personalizaVista(holder, alumnoClase);
    }

    // Personalizamos un ViewHolder a partir de un lugar
    private void personalizaVista(ViewHolder holder, AlumnoClase alumnoClase) {
        holder.nombreAlumno.setText(alumnoClase.getApellido1() + " " + alumnoClase.getApellido2() + ", " + alumnoClase.getNombre());


        if(alumnoClase.getAnotacion() != null){
            if(alumnoClase.getAnotacion().getFalta()!=null){
                Log.d("FALTA", alumnoClase.getAnotacion().getFalta()+ " " + alumnoClase.getNombre());
                holder.botonFalta.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.falta), PorterDuff.Mode.MULTIPLY);
                if(alumnoClase.getAnotacion().getFalta().equals("I")){
                    holder.botonFalta.setText("I");
                }
                else if(alumnoClase.getAnotacion().getFalta().equals("J")){
                    holder.botonFalta.setText("J");
                }
                else{
                    holder.botonFalta.setText("R");
                }
            }
            else{
                holder.botonFalta.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            }

            if(alumnoClase.getAnotacion().isTrabaja()){
                holder.botonTrabaja.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.trabaja), PorterDuff.Mode.MULTIPLY);
            }
            else{
                holder.botonTrabaja.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            }

            if(alumnoClase.getAnotacion().getPositivos()>0){
                holder.botonPositivo.setText(Integer.toString(alumnoClase.getAnotacion().getPositivos()) + "+");
                holder.botonPositivo.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.valoracion), PorterDuff.Mode.MULTIPLY);
            }
            else{
                holder.botonPositivo.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            }
            if(alumnoClase.getAnotacion().getNegativos()>0){
                holder.botonNegativo.setText(Integer.toString(alumnoClase.getAnotacion().getNegativos()) + "-");
                holder.botonNegativo.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.valoracion), PorterDuff.Mode.MULTIPLY);
            }
            else{
                holder.botonNegativo.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            }
        }
        else{
            holder.botonFalta.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            holder.botonTrabaja.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            holder.botonPositivo.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            holder.botonNegativo.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }


        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        String imageUri;
        if (alumnoClase.getFoto() != null){

            //Log.d("FOTO", alumnoClase.getNombre() + " " + alumnoClase.getFoto());
            imageUri = alumnoClase.getFoto();
        }
        else{
            imageUri="drawable://" + R.drawable.sinfoto;// from drawables (non-9patch images)
        }

        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .displayer(new CircleBitmapDisplayer())
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        imageLoader.displayImage(imageUri, holder.foto, options);
    }

    @Override
    public int getItemCount() {
        return this.alumnoClaseArrayList.size();
    }


    @Override
    public long getItemId(int posicion) {
        return posicion;
    }

    public int getItemPk(int posicion){ return this.alumnoClaseArrayList.get(posicion).getPk();}



    //Insertar anotacion
    private class PonerAnotacion extends AsyncTask<ParametrosValoracion, Void, Boolean> {

        HttpURLConnection urlConnection;

        public Boolean doInBackground(ParametrosValoracion... params) {

            String urlString = params[0].getUrl();
            String valoracion = params[0].getValoracion();
            int alumnoPk = params[0].getAlumnoPk();
            int posicion = params[0].getPosicion();
            ViewHolder viewHolder= (ViewHolder)params[0].getObject();

            try {
                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(contexto);
                String token = settings.getString("auth_token", "");
                // Creando la conexión
                String domain = contexto.getResources().getString(R.string.domain);
                URL url = new URL(domain + urlString);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "JWT " + token);
                //urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");

                //escritura de la anotacion
                JSONObject jsonObject = new JSONObject();
                switch (valoracion){
                    case "falta": jsonObject.put("falta", "I"); //la primera vez, la falta es injustificada
                        break;
                    case "trabaja": jsonObject.put("trabaja", true);
                        break;
                    case "positivo": jsonObject.put("positivos", 1);
                        break;
                    case "negativo": jsonObject.put("negativos", 1);
                        break;
                }

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
                    Anotacion anotacion = Anotacion.obtenerAnotacion(sb.toString());
                    anotacion.setAsignatura(idAsignatura);
                    anotacion.setAlumno(alumnoPk);
                    SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                    String fechaString = formato.format(new Date(fecha));
                    anotacion.setFecha(fechaString);

                    alumnoClaseArrayList.get(posicion).setAnotacion(anotacion);
                    onBindViewHolder(viewHolder, posicion);

                    Log.d("INSERTADO", Integer.toString(alumnoPk));

                    return true;
                } else {
                    Log.d("NOTINSERTED",urlConnection.getResponseMessage());
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

            if (result) {
                Toast.makeText(contexto, "Anotación insertada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(contexto, "Problemas al insertar la anotación", Toast.LENGTH_SHORT).show();
            }

        }

    }

    //Insertar profesor
    private class EditarAnotacion extends AsyncTask<ParametrosValoracion, Void, Boolean> {

        HttpURLConnection urlConnection;

        public Boolean doInBackground(ParametrosValoracion... params) {

            String urlString = params[0].getUrl();
            String valoracion = params[0].getValoracion();
            int alumnoPk = params[0].getAlumnoPk();
            int posicion = params[0].getPosicion();
            ViewHolder viewHolder= (ViewHolder)params[0].getObject();

            try {
                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(contexto);
                String token = settings.getString("auth_token", "");
                // Creando la conexión
                String domain = contexto.getResources().getString(R.string.domain);
                URL url = new URL(domain + urlString);

                urlConnection = (HttpURLConnection) url.openConnection();

                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "JWT " + token);
                //urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("PUT");

                //escritura de la anotacion
                Anotacion anotacion_old = alumnoClaseArrayList.get(posicion).getAnotacion();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("alumno", alumnoPk);
                jsonObject.put("asignatura", idAsignatura);
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                String fechaString = formato.format(new Date(fecha));
                jsonObject.put("fecha", fechaString);
                switch (valoracion){
                    case "falta": if (anotacion_old.getFalta()!=null){
                                        if(anotacion_old.getFalta().equals("I")){
                                            jsonObject.put("falta", "J");
                                        }
                                        else if(anotacion_old.getFalta().equals("J")){
                                            jsonObject.put("falta", "R");
                                        }
                                        else {
                                            jsonObject.put("falta", "");
                                        }
                                }else{
                                    jsonObject.put("falta", "I");
                                }
                        jsonObject.put("trabaja", anotacion_old.isTrabaja());
                        jsonObject.put("positivos", anotacion_old.getPositivos());
                        jsonObject.put("negativos", anotacion_old.getNegativos());
                        break;
                    case "trabaja": if (anotacion_old.isTrabaja()){
                                        jsonObject.put("trabaja", false);
                                    }else{
                                        jsonObject.put("trabaja", true);
                                    }
                        jsonObject.put("falta", anotacion_old.getFalta());
                        jsonObject.put("positivos", anotacion_old.getPositivos());
                        jsonObject.put("negativos", anotacion_old.getNegativos());
                        break;
                    case "positivo": if (anotacion_old.getPositivos() > 0 ){
                                        jsonObject.put("positivos", anotacion_old.getPositivos()+1);
                                    }else{
                                        jsonObject.put("positivos", 1);
                                    }
                        jsonObject.put("falta", anotacion_old.getFalta());
                        jsonObject.put("trabaja", anotacion_old.isTrabaja());
                        jsonObject.put("negativos", anotacion_old.getNegativos());
                        break;
                    case "negativo": if (anotacion_old.getNegativos() > 0 ){
                                        jsonObject.put("negativos", anotacion_old.getNegativos()+1);
                                    }else{
                                        jsonObject.put("negativos", 1);
                                    }
                        jsonObject.put("falta", anotacion_old.getFalta());
                        jsonObject.put("trabaja", anotacion_old.isTrabaja());
                        jsonObject.put("positivos", anotacion_old.getPositivos());
                        break;
                }

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
                    Anotacion anotacion_nueva = Anotacion.obtenerAnotacion(sb.toString());
                    //anotacion_nueva.setAsignatura(idAsignatura);
                    //anotacion_nueva.setAlumno(alumnoPk);
                    //SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    //String fechaString = formato.format(new Date(fecha));
                    //anotacion.setFecha(fechaString);

                    alumnoClaseArrayList.get(posicion).setAnotacion(anotacion_nueva);
                    onBindViewHolder(viewHolder, posicion);

                    Log.d("ACTUALIZADO", Integer.toString(alumnoPk));

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

            if (result) {
                Toast.makeText(contexto, "Anotación actualizada correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(contexto, "Problemas al actualizar la anotación", Toast.LENGTH_SHORT).show();
            }

        }


    }
}
