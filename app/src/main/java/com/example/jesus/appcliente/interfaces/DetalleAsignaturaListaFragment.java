package com.example.jesus.appcliente.interfaces;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.Anotacion;
import com.example.jesus.appcliente.clases.ClickListener;
import com.example.jesus.appcliente.clases.DetalleAsignatura;
import com.example.jesus.appcliente.clases.AlumnoClase;
import com.example.jesus.appcliente.clases.AlumnoClaseListaAdapter;
import com.example.jesus.appcliente.clases.DialogoSelectorFecha;
import com.github.fafaldo.fabtoolbar.widget.FABToolbarLayout;

import org.json.JSONArray;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DetalleAsignaturaListaFragment extends Fragment implements DatePickerDialog.OnDateSetListener, ClickListener{

    private TextView textViewAsignatura, textViewGrupo, textViewFecha;
    private int idAsignatura;
    private String nombreAsignatura, nombreGrupo;
    private long fecha;

    private RecyclerView recyclerViewDetalleAsignatura;
    private RecyclerView.LayoutManager layoutManager;
    private AlumnoClaseListaAdapter adaptador;
    private Bundle parametros;
    private ArrayList<Integer> alumnosSeleccionados;

    private FABToolbarLayout fabToolbarLayout;
    private ImageView btnFalta, btnTrabaja, btnPositivo, btnNegativo, btnCancelar;
    private FloatingActionButton fab;

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;

    MyReceiver r;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.detalle_asignatura, container, false);

        parametros = getActivity().getIntent().getExtras();
        this.idAsignatura = parametros.getInt("idAsignatura");
        this.nombreAsignatura = parametros.getString("nombreAsignatura");
        this.nombreGrupo = parametros.getString("nombreGrupo");

        //obtención del token
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        String fecha_almacenada = settings.getString("fecha_seleccionada", ""/*default value*/);
        if(fecha_almacenada.isEmpty()){
            this.fecha = parametros.getLong("fecha");   //se coge la fecha actual
        }
        else{
            this.fecha = Long.parseLong(fecha_almacenada);
        }


        this.textViewFecha = (TextView) view.findViewById(R.id.textViewFecha);
        textViewFecha.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cambiarFecha();
            }
        });
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        textViewFecha.setText(formato.format(new Date(fecha)));

        this.recyclerViewDetalleAsignatura = (RecyclerView) view.findViewById(R.id.recycler_view_detalle_asignatura);
        this.textViewAsignatura = (TextView) view.findViewById(R.id.textViewAsignatura);
        this.textViewGrupo = (TextView) view.findViewById(R.id.textViewGrupo);


        this.fabToolbarLayout = (FABToolbarLayout) view.findViewById(R.id.fabtoolbar);
        this.btnFalta = view.findViewById(R.id.imageViewFalta);
        this.btnTrabaja = view.findViewById(R.id.imageViewTrabaja);
        this.btnPositivo = view.findViewById(R.id.imageViewPositivo);
        this.btnNegativo = view.findViewById(R.id.imageViewNegativo);
        this.btnCancelar = view.findViewById(R.id.imageViewCancelar);

        this.fab = (FloatingActionButton)view.findViewById(R.id.fabtoolbar_fab);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        textViewAsignatura.setText(nombreAsignatura);
        textViewGrupo.setText(nombreGrupo);

        ArrayList<AlumnoClase> alumnos = new ArrayList<AlumnoClase>();
        adaptador = new AlumnoClaseListaAdapter(getContext(), alumnos, idAsignatura, fecha, this);
        recyclerViewDetalleAsignatura.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(getContext());
        recyclerViewDetalleAsignatura.setLayoutManager(layoutManager);
        recyclerViewDetalleAsignatura.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        btnFalta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarAlumnado();
                if(alumnosSeleccionados.isEmpty()){
                    Toast.makeText(getActivity(), "Debe seleccionar algún alumno", Toast.LENGTH_SHORT).show();
                }
                else{
                    new DetalleAsignaturaListaFragment.PonerFalta().execute("falta");
                }
            }
        });

        btnTrabaja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarAlumnado();
                if(alumnosSeleccionados.isEmpty()){
                    Toast.makeText(getActivity(), "Debe seleccionar algún alumno", Toast.LENGTH_SHORT).show();
                }
                else{
                    new DetalleAsignaturaListaFragment.PonerFalta().execute("trabaja");
                }
            }
        });

        btnPositivo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarAlumnado();
                if(alumnosSeleccionados.isEmpty()){
                    Toast.makeText(getActivity(), "Debe seleccionar algún alumno", Toast.LENGTH_SHORT).show();
                }
                else{
                    new DetalleAsignaturaListaFragment.PonerFalta().execute("positivo");
                }
            }
        });

        btnNegativo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seleccionarAlumnado();
                if(alumnosSeleccionados.isEmpty()){
                    Toast.makeText(getActivity(), "Debe seleccionar algún alumno", Toast.LENGTH_SHORT).show();
                }
                else{
                    new DetalleAsignaturaListaFragment.PonerFalta().execute("negativo");
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fabToolbarLayout.hide();
                recyclerViewDetalleAsignatura.setPadding(0,0,0,0);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //se aumenta el padding para poder ver el último elemento
                recyclerViewDetalleAsignatura.setPadding(0,0,0,100);
                fabToolbarLayout.show();
            }
        });


        //se borran la selección que se haya hecho previamente
        if (actionMode != null) {
            actionMode.finish();
        }
        //se oculta el FAB hasta que no haya una selección múltiple
        //fab.setVisibility(View.GONE);

        new DetalleAsignaturaListaFragment.GetAlumnado().execute();
    }

    public void seleccionarAlumnado(){
        alumnosSeleccionados = new ArrayList<Integer>();
        for (int i=0; i < adaptador.getSelectedItems().size();i++){
            alumnosSeleccionados.add(adaptador.getItemPk(adaptador.getSelectedItems().get(i)));
        }
        Log.d("SELECCIÓN", adaptador.getSelectedItems().toString());
        Log.d("ALUMNADO", alumnosSeleccionados.toString());
    }

    public void refresh() {
        //your code in refresh.
        Log.i("Refresh", "YES");
        //se recargan las anotaciones que se pudieran haber hecho desde otro fragment
        new DetalleAsignaturaListaFragment.GetAlumnado().execute();
    }

    public void onPause() {
        super.onPause();
        Log.d("ONPAUSE", "onpause");
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(r);
    }

    public void onResume() {
        super.onResume();
        Log.d("ONRESUME", "onresume");
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(r,
                new IntentFilter("TAG_REFRESH_1"));
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("ONRECEIVE", "onreceive");
            DetalleAsignaturaListaFragment.this.refresh();
        }
    }


    public void cambiarFecha() {
        DialogoSelectorFecha dialogoFecha = new DialogoSelectorFecha();
        dialogoFecha.setOnDateSetListener(this);
        Bundle args = new Bundle();
        args.putLong("fecha", fecha);
        dialogoFecha.setArguments(args);
        dialogoFecha.show(getActivity().getSupportFragmentManager(), "selectorFecha");
    }

    @Override
    public void onDateSet(DatePicker vista, int anyo, int mes, int dia) {
        Calendar calendario = Calendar.getInstance();
        calendario.setTimeInMillis(fecha);
        calendario.set(Calendar.YEAR, anyo);
        calendario.set(Calendar.MONTH, mes);
        calendario.set(Calendar.DAY_OF_MONTH, dia);
        fecha = calendario.getTimeInMillis();

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        //DateFormat formato =  DateFormat.getDateInstance();
        textViewFecha.setText(formato.format(new Date(fecha)));
        //se actualiza las anotaciones del alumnado en la fecha indicada
        new DetalleAsignaturaListaFragment.GetAlumnado().execute();
        //se almacena la fecha seleccionada por si se rota la pantalla
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("fecha_seleccionada", Long.toString(fecha));
        editor.apply();
    }

    /*@Override
    public void onBackPressed() {
        fabToolbarLayout.hide();
    }*/


    @Override
    public void onItemClicked(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (actionMode == null) {   //se activa la selección múltiple
            actionMode =
                    ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);

            //se visualiza el FAB
            //fab.setVisibility(View.VISIBLE);
        }

        toggleSelection(position);

        return true;
    }

    /**
     * Toggle the selection state of an item.
     *
     * If the item was the last one in the selection and is unselected, the selection is stopped.
     * Note that the selection must already be started (actionMode must not be null).
     *
     * @param position Position of the item to toggle the selection state
     */
    private void toggleSelection(int position) {
        adaptador.toggleSelection(position);
        int count = adaptador.getSelectedItemCount();

        if (count == 0) {
            actionMode.finish();
            //se oculta el FAB
            //fab.setVisibility(View.GONE);
        } else {
            if(count == 1){actionMode.setTitle(String.valueOf(count) + " alumno/a");}
            else{actionMode.setTitle(String.valueOf(count) + " alumnos/as");}
            actionMode.invalidate();
        }
    }

    /**
     * Select all the items
     *
     */
    private void seleccionarTodos() {
        for(int i=0; i<adaptador.getItemCount(); i++){
            adaptador.doSelection(i);
        }
        actionMode.setTitle("Todos");
        actionMode.invalidate();
    }

    /**
     * Unselect all the items
     *
     */
    private void borrarSeleccion() {
       adaptador.clearSelection();
        actionMode.setTitle("Ninguno");
        actionMode.invalidate();
    }


    /**
     * Toggle the selection of all the items
     *
     */
    private void invertirSeleccion() {
        for(int i=0; i<adaptador.getItemCount(); i++){
            adaptador.toggleSelection(i);
        }
        int count = adaptador.getSelectedItemCount();

        if(count == 1){actionMode.setTitle(String.valueOf(count) + " alumno/a");}
        else{actionMode.setTitle(String.valueOf(count) + " alumnos/as");}

        actionMode.invalidate();

    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.selected_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.menu_todos:
                    seleccionarTodos();
                    return true;
                case R.id.menu_ninguno:
                    borrarSeleccion();
                    return true;
                case R.id.menu_invertir:
                    invertirSeleccion();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            adaptador.clearSelection();
            actionMode = null;
        }
    }



    //Get alumado
    private class GetAlumnado extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(Void... var1){

            StringBuilder result = new StringBuilder();

            try{
                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);

                Date fechaConsulta = new Date(fecha);
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String fechaParameter = formatter.format(fechaConsulta);

                // Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/asignatura/" + Integer.toString(idAsignatura) + "/" + fechaParameter + "/detalle/");
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
                if(urlConnection!=null){
                    urlConnection.disconnect();
                }

            }

            return result.toString();

        }

        public void onPostExecute(String result){

            if(result.isEmpty()){
                Toast.makeText(getActivity(),"No hay alumnado matriculado", Toast.LENGTH_LONG).show();

            }
            else{
                DetalleAsignatura alumnado = DetalleAsignatura.obtenerAlumnadoAsignatura(result);

                if(!alumnado.getAlumnos().isEmpty()) {
                    adaptador.actualizar(alumnado.getAlumnos(), idAsignatura, fecha);
                    recyclerViewDetalleAsignatura.getAdapter().notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getActivity(),"No se generaron resultados", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    //Insertar anotacion
    private class PonerFalta extends AsyncTask<String, Void, Boolean> {

        HttpURLConnection urlConnection;

        public Boolean doInBackground(String... params) {

            try {
                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", "");
                // Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/anotacion/falta/");

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
                jsonObject.put("valoracion", params[0]);    //valoracion a poner (falta, trabaja, positivo, negativo)
                jsonObject.put("idAsignatura", idAsignatura);
                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                jsonObject.put("fecha", formato.format(new Date(fecha)));
                JSONArray jsonArray = new JSONArray();
                for(int i=0;i<alumnosSeleccionados.size();i++){
                    jsonArray.put(alumnosSeleccionados.get(i));
                }
                jsonObject.put("alumnos", jsonArray);


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
                    Log.d("ANOTATED","" + sb.toString());
                    ArrayList<Anotacion> anotaciones = Anotacion.obtenerAnotaciones(sb.toString());

                    //ACTUALIZAR CAMBIOS
                    for (int i=0; i<adaptador.getSelectedItems().size();i++){
                        int posicion=adaptador.getSelectedItems().get(i);
                        adaptador.actualizarAnotacion(anotaciones.get(i),posicion);
                        adaptador.notifyItemChanged(posicion);
                    }


                    return true;
                } else {
                    Log.d("NOTANOTATED",urlConnection.getResponseMessage());
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
                Toast.makeText(getActivity(), "Problemas al valorar al alumnado", Toast.LENGTH_SHORT).show();
            }

        }

    }
}
