package com.example.jesus.appcliente.clases;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jesus on 12/07/17.
 */

public class AlumnoClaseListaAdapter extends SelectableAdapter<AlumnoClaseListaAdapter.ViewHolder>{

    protected ArrayList<AlumnoClase> alumnoClaseArrayList;
    protected int idAsignatura;
    protected long fecha;
    protected LayoutInflater inflador;
    protected Context contexto;
    protected View.OnClickListener onClickListener;
    private ClickListener clickListener;

    public AlumnoClaseListaAdapter(Context contexto, ArrayList<AlumnoClase> alumnoClaseArrayList, int idAsignatura, long fecha, ClickListener clickListener) {
        this.contexto = contexto;
        this.alumnoClaseArrayList = alumnoClaseArrayList;
        this.idAsignatura = idAsignatura;
        this.fecha = fecha;
        this.clickListener = clickListener;
        inflador = (LayoutInflater) contexto
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void actualizar(ArrayList<AlumnoClase> alumnoClaseArrayList, int idAsignatura, long fecha){
        this.alumnoClaseArrayList = alumnoClaseArrayList;
        this.idAsignatura = idAsignatura;
        this.fecha = fecha;
    }

    public void actualizarAnotacion(Anotacion anotacion, int position){
        this.alumnoClaseArrayList.get(position).setAnotacion(anotacion);
    }

    public void setOnItemClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    //Creamos nuestro ViewHolder, con los tipos de elementos a modificar
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener{
        public TextView nombreAlumno;
        public ImageView foto;
        public Button botonFalta, botonTrabaja, botonPositivo, botonNegativo, botonEditar;
        public RelativeLayout lt;
        private ClickListener listener;

        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);

            this.listener = listener;

            nombreAlumno = (TextView) itemView.findViewById(R.id.textViewNombreAlumno);
            foto = (ImageView) itemView.findViewById(R.id.imageViewAlumno);
            botonFalta = (Button) itemView.findViewById(R.id.buttonFalta);
            botonTrabaja = (Button) itemView.findViewById(R.id.buttonTrabaja);
            botonPositivo = (Button) itemView.findViewById(R.id.buttonPositivo);
            botonNegativo = (Button) itemView.findViewById(R.id.buttonNegativo);
            botonEditar = (Button) itemView.findViewById(R.id.buttonEdit);
            lt = (RelativeLayout) itemView.findViewById(R.id.layout);

            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);
            botonFalta.setOnClickListener(this);
            botonTrabaja.setOnClickListener(this);
            botonPositivo.setOnClickListener(this);
            botonNegativo.setOnClickListener(this);
            botonEditar.setOnClickListener(this);

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
                    ParametrosAnotacion parametros = new ParametrosAnotacion(urlEditar, anotacionModificada(anotacion,"falta"), pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosAnotacion, Void, Boolean> task = new EditarAnotacion();
                    task.execute(parametros);
                }
                else{
                    ParametrosAnotacion parametros = new ParametrosAnotacion(urlPoner, nuevaAnotacion("falta"), pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosAnotacion, Void, Boolean> task = new PonerAnotacion();
                    task.execute(parametros);
                }
            }
            else if (v.getId() == botonTrabaja.getId()){
                if (anotacion != null){
                    ParametrosAnotacion parametros = new ParametrosAnotacion(urlEditar, anotacionModificada(anotacion,"trabaja"), pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosAnotacion, Void, Boolean> task = new EditarAnotacion();
                    task.execute(parametros);
                }
                else{
                    ParametrosAnotacion parametros = new ParametrosAnotacion(urlPoner, nuevaAnotacion("trabaja"), pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosAnotacion, Void, Boolean> task = new PonerAnotacion();
                    task.execute(parametros);
                }
            }
            else if (v.getId() == botonPositivo.getId()) {
                if (anotacion != null) {
                    ParametrosAnotacion parametros = new ParametrosAnotacion(urlEditar, anotacionModificada(anotacion,"positivo"), pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosAnotacion, Void, Boolean> task = new EditarAnotacion();
                    task.execute(parametros);
                }
                else{
                    ParametrosAnotacion parametros = new ParametrosAnotacion(urlPoner, nuevaAnotacion("positivo"), pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosAnotacion, Void, Boolean> task = new PonerAnotacion();
                    task.execute(parametros);
                }
            }
            else if (v.getId() == botonNegativo.getId()){
                if (anotacion != null){
                    ParametrosAnotacion parametros = new ParametrosAnotacion(urlEditar, anotacionModificada(anotacion,"negativo"), pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosAnotacion, Void, Boolean> task = new EditarAnotacion();
                    task.execute(parametros);
                }
                else{
                    ParametrosAnotacion parametros = new ParametrosAnotacion(urlPoner, nuevaAnotacion("negativo"), pk, getAdapterPosition(), this);
                    AsyncTask<ParametrosAnotacion, Void, Boolean> task = new PonerAnotacion();
                    task.execute(parametros);
                }
            }
            else if (v.getId() == botonEditar.getId()){

                if(anotacion!=null){
                    ParametrosAnotacion parametros = new ParametrosAnotacion(urlEditar, null, pk, getAdapterPosition(), this);
                    mostrarDialogAnotaciones(v, anotacion, parametros);
                }
                else{
                    ParametrosAnotacion parametros = new ParametrosAnotacion(urlPoner, null, pk, getAdapterPosition(), this);
                    mostrarDialogAnotaciones(v, anotacion, parametros);
                }

            }
            else {
                if (listener != null) {
                    listener.onItemClicked(getAdapterPosition());
                }
            }
        }


        public void mostrarDialogAnotaciones(View v, final Anotacion anotacion, final ParametrosAnotacion parametros) {

            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(v.getContext());
            View dialogView = inflador.inflate(R.layout.dialogo_anotaciones, null);
            final Spinner falta = (Spinner) dialogView.findViewById(R.id.spinnerFalta);
            final Switch trabaja = (Switch) dialogView.findViewById(R.id.switchTrabaja);
            final EditText positivos = (EditText) dialogView.findViewById(R.id.editTextPositivos);
            final EditText negativos = (EditText) dialogView.findViewById(R.id.editTextNegativos);

            if(anotacion != null){
                if (anotacion.getFalta()!=null){
                    if (anotacion.getFalta().equals("I")){
                        falta.setSelection(1);
                    }
                    else if (anotacion.getFalta().equals("J")){
                        falta.setSelection(2);
                    }
                    else if (anotacion.getFalta().equals("R")){
                        falta.setSelection(3);
                    }
                    else{
                        falta.setSelection(0);
                    }
                }
                else{
                    falta.setSelection(0);
                }
                if (anotacion.isTrabaja()){trabaja.setChecked(true);}else{trabaja.setChecked(false);}
                if (anotacion.getPositivos()>0){
                    positivos.setText(Integer.toString(anotacion.getPositivos()));
                }
                if (anotacion.getNegativos()>0){
                    negativos.setText(Integer.toString(anotacion.getNegativos()));
                }
            }

            dialogBuilder.setView(dialogView);
            dialogBuilder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    /*if (anotacion != null) {
                    }
                    else {
                    }*/
                    //se guardan los valores introducidos
                    Anotacion nuevaAnotacion = new Anotacion();
                    switch (falta.getSelectedItemPosition()) {
                        case 0: nuevaAnotacion.setFalta("");
                            break;
                        case 1: nuevaAnotacion.setFalta("I");
                            break;
                        case 2: nuevaAnotacion.setFalta("J");
                            break;
                        case 3: nuevaAnotacion.setFalta("R");
                            break;
                    }

                    nuevaAnotacion.setTrabaja(trabaja.isChecked());
                    if (!positivos.getText().toString().isEmpty()){
                        nuevaAnotacion.setPositivos(Integer.parseInt(positivos.getText().toString().trim()));
                    }
                    if (!negativos.getText().toString().isEmpty()){
                        nuevaAnotacion.setNegativos(Integer.parseInt(negativos.getText().toString().trim()));
                    }
                    //se actualiza la anotacion contenida en los parámetros
                    parametros.setAnotacion(nuevaAnotacion);

                    Log.d("ANOTACION", nuevaAnotacion.getFalta() + " " + nuevaAnotacion.isTrabaja() + " " + nuevaAnotacion.getPositivos() + " " + nuevaAnotacion.getNegativos());

                    if (anotacion != null){
                        AsyncTask<ParametrosAnotacion, Void, Boolean> task = new EditarAnotacion();
                        task.execute(parametros);
                    }
                    else{
                        AsyncTask<ParametrosAnotacion, Void, Boolean> task = new PonerAnotacion();
                        task.execute(parametros);
                    }

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

        //onLongClickListener for view --> para comenzar la selección múltiple
        @Override
        public boolean onLongClick(View v) {

            if (listener != null) {
                return listener.onItemLongClicked(getAdapterPosition());
            }

            return false;

        }

    }

    /////////////////////////////////////////////////////////////////////////////

    // Creamos el ViewHolder con la vista de un elemento sin personalizar
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflamos la vista desde el xml
        View v = inflador.inflate(R.layout.layout_alumno_clase_lista, parent, false);
        //v.setOnClickListener(onClickListener);
        return new ViewHolder(v, clickListener);
    }

    // Usando como base el ViewHolder y lo personalizamos
    @Override
    public void onBindViewHolder(ViewHolder holder, int posicion) {
        AlumnoClase alumnoClase = this.alumnoClaseArrayList.get(posicion);
        personalizaVista(holder, alumnoClase, posicion);
    }

    // Personalizamos un ViewHolder a partir de un lugar
    private void personalizaVista(ViewHolder holder, AlumnoClase alumnoClase, int posicion) {
        holder.nombreAlumno.setText(alumnoClase.getNombre() + " " + alumnoClase.getApellido1() + " " + alumnoClase.getApellido2());
        holder.nombreAlumno.setTextColor(ContextCompat.getColor(contexto, R.color.negro));

        if(alumnoClase.getAnotacion() != null){
            if(alumnoClase.getAnotacion().getFalta()!=null){
                if(alumnoClase.getAnotacion().getFalta().equals("I")){
                    holder.botonFalta.setText("I");
                    holder.botonFalta.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.falta), PorterDuff.Mode.MULTIPLY);
                }
                else if(alumnoClase.getAnotacion().getFalta().equals("J")){
                    holder.botonFalta.setText("J");
                    holder.botonFalta.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.falta), PorterDuff.Mode.MULTIPLY);
                }
                else if(alumnoClase.getAnotacion().getFalta().equals("R")){
                    holder.botonFalta.setText("R");
                    holder.botonFalta.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.falta), PorterDuff.Mode.MULTIPLY);
                }
                else{
                    holder.botonFalta.setText("F");
                    holder.botonFalta.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
                }
            }
            else{
                holder.botonFalta.setText("F");
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
                holder.botonPositivo.setText("+");
                holder.botonPositivo.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            }
            if(alumnoClase.getAnotacion().getNegativos()>0){
                holder.botonNegativo.setText(Integer.toString(alumnoClase.getAnotacion().getNegativos()) + "-");
                holder.botonNegativo.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.valoracion), PorterDuff.Mode.MULTIPLY);
            }
            else{
                holder.botonNegativo.setText("-");
                holder.botonNegativo.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            }

            if(alumnoClase.getAnotacion().getFalta() != null) {
                //aviso por si tiene falta, y alguna otra valoración. En caso contrario, el fondo será blanco
                if (alumnoClase.getAnotacion().getFalta().equals("I") || alumnoClase.getAnotacion().getFalta().equals("J")) {
                    if (alumnoClase.getAnotacion().isTrabaja() || alumnoClase.getAnotacion().getPositivos() > 0 || alumnoClase.getAnotacion().getNegativos() > 0) {
                        Log.d("AVISO", "en rojo");
                        holder.nombreAlumno.setTextColor(ContextCompat.getColor(contexto, R.color.aviso_anotacion));
                    }
                }
            }
        }
        else{
            holder.botonFalta.setText("F");
            holder.botonFalta.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            holder.botonTrabaja.setText("T");
            holder.botonTrabaja.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            holder.botonPositivo.setText("+");
            holder.botonPositivo.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
            holder.botonNegativo.setText("-");
            holder.botonNegativo.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        }


        //Se determina el color de fondo: si está seleccionado en gris
        if (isSelected(posicion)){
            holder.lt.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.selected_overlay), PorterDuff.Mode.MULTIPLY);
        }
        else{
            holder.lt.getBackground().setColorFilter(ContextCompat.getColor(contexto, R.color.blanco), PorterDuff.Mode.MULTIPLY);
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

    public Anotacion nuevaAnotacion(String valoracion){
        Anotacion anotacion = new Anotacion();
        switch (valoracion){
            case "falta": anotacion.setFalta("I"); //la primera vez, la falta es injustificada
                break;
            case "trabaja": anotacion.setTrabaja(true);
                break;
            case "positivo": anotacion.setPositivos(1);
                break;
            case "negativo": anotacion.setNegativos(1);
                break;
        }
        return anotacion;
    }

    public Anotacion anotacionModificada(Anotacion anotacionOld, String valoracion){
        Anotacion anotacion = new Anotacion();
        switch (valoracion){
            case "falta": if (anotacionOld.getFalta()!=null){
                                if(anotacionOld.getFalta().equals("I")){
                                    anotacion.setFalta("J");
                                }
                                else if(anotacionOld.getFalta().equals("J")){
                                    anotacion.setFalta("R");
                                }
                                else if(anotacionOld.getFalta().equals("R")){
                                    anotacion.setFalta("");
                                }
                                else{
                                    anotacion.setFalta("I");
                                }
                        }else{
                            anotacion.setFalta("I");
                        }
                        anotacion.setTrabaja(anotacionOld.isTrabaja());
                        anotacion.setPositivos(anotacionOld.getPositivos());
                        anotacion.setNegativos(anotacionOld.getNegativos());
                        break;
            case "trabaja": if (anotacionOld.isTrabaja()){
                                anotacion.setTrabaja(false);
                            }else{
                                anotacion.setTrabaja(true);
                            }
                        anotacion.setFalta(anotacionOld.getFalta());
                        anotacion.setPositivos(anotacionOld.getPositivos());
                        anotacion.setNegativos(anotacionOld.getNegativos());
                        break;
            case "positivo": if (anotacionOld.getPositivos() > 0 ){
                                anotacion.setPositivos(anotacionOld.getPositivos()+1);
                            }else{
                                anotacion.setPositivos(1);
                            }
                        anotacion.setFalta(anotacionOld.getFalta());
                        anotacion.setTrabaja(anotacionOld.isTrabaja());
                        anotacion.setNegativos(anotacionOld.getNegativos());
                        break;
            case "negativo": if (anotacionOld.getNegativos() > 0 ){
                                anotacion.setNegativos(anotacionOld.getNegativos()+1);
                            }else{
                                anotacion.setNegativos(1);
                            }
                        anotacion.setFalta(anotacionOld.getFalta());
                        anotacion.setTrabaja(anotacionOld.isTrabaja());
                        anotacion.setPositivos(anotacionOld.getPositivos());
                        break;
            }
        return anotacion;
    }

    //Insertar anotacion
    private class PonerAnotacion extends AsyncTask<ParametrosAnotacion, Void, Boolean> {

        HttpURLConnection urlConnection;

        public Boolean doInBackground(ParametrosAnotacion... params) {

            String urlString = params[0].getUrl();
            Anotacion anotacion = params[0].getAnotacion();
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
                jsonObject.put("falta", anotacion.getFalta());
                jsonObject.put("trabaja", anotacion.isTrabaja());
                jsonObject.put("positivos", anotacion.getPositivos());
                jsonObject.put("negativos", anotacion.getNegativos());

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
                    Anotacion anotacionDevuelta = Anotacion.obtenerAnotacion(sb.toString());
                    anotacionDevuelta.setAsignatura(idAsignatura);
                    anotacionDevuelta.setAlumno(alumnoPk);
                    SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                    String fechaString = formato.format(new Date(fecha));
                    anotacionDevuelta.setFecha(fechaString);

                    alumnoClaseArrayList.get(posicion).setAnotacion(anotacionDevuelta);
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

            if (!result) {
                Toast.makeText(contexto, "Problemas al insertar la anotación", Toast.LENGTH_SHORT).show();
            }

        }

    }

    //Editar una determinada anotacion
    private class EditarAnotacion extends AsyncTask<ParametrosAnotacion, Void, Boolean> {

        HttpURLConnection urlConnection;

        public Boolean doInBackground(ParametrosAnotacion... params) {

            String urlString = params[0].getUrl();
            Anotacion anotacion = params[0].getAnotacion();
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
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("alumno", alumnoPk);
                jsonObject.put("asignatura", idAsignatura);
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
                String fechaString = formato.format(new Date(fecha));
                jsonObject.put("fecha", fechaString);
                jsonObject.put("falta", anotacion.getFalta());
                jsonObject.put("trabaja", anotacion.isTrabaja());
                jsonObject.put("positivos", anotacion.getPositivos());
                jsonObject.put("negativos", anotacion.getNegativos());


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
                    Anotacion anotacionDevuelta = Anotacion.obtenerAnotacion(sb.toString());
                    //anotacion_nueva.setAsignatura(idAsignatura);
                    //anotacion_nueva.setAlumno(alumnoPk);
                    //SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                    //String fechaString = formato.format(new Date(fecha));
                    //anotacion.setFecha(fechaString);

                    alumnoClaseArrayList.get(posicion).setAnotacion(anotacionDevuelta);
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

            if (!result) {
                Toast.makeText(contexto, "Problemas al actualizar la anotación", Toast.LENGTH_SHORT).show();
            }

        }


    }
}
