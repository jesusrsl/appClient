package com.example.jesus.appcliente.interfaces;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.ClickListener;
import com.example.jesus.appcliente.clases.DetalleAsignatura;
import com.example.jesus.appcliente.clases.AlumnoClase;
import com.example.jesus.appcliente.clases.AlumnoClaseAdapter;
import com.example.jesus.appcliente.clases.DialogoSelectorFecha;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class DetalleAsignaturaFragment extends Fragment implements DatePickerDialog.OnDateSetListener, ClickListener{

    private TextView textViewAsignatura, textViewGrupo, textViewFecha;
    private int idAsignatura;
    private String nombreAsignatura, nombreGrupo;
    private long fecha;

    private RecyclerView recyclerViewDetalleAsignatura;
    private RecyclerView.LayoutManager layoutManager;
    private AlumnoClaseAdapter adaptador;
    private Bundle parametros;
    private ArrayList<Integer> alumnosSeleccionados;
    private FloatingActionButton fab;

    private ActionModeCallback actionModeCallback = new ActionModeCallback();
    private ActionMode actionMode;


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
        this.fab = (FloatingActionButton)view.findViewById(R.id.fab);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        textViewAsignatura.setText(nombreAsignatura);
        textViewGrupo.setText(nombreGrupo);

        ArrayList<AlumnoClase> alumnos = new ArrayList<AlumnoClase>();
        adaptador = new AlumnoClaseAdapter(getContext(), alumnos, idAsignatura, fecha, this);
        recyclerViewDetalleAsignatura.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(getContext());
        recyclerViewDetalleAsignatura.setLayoutManager(layoutManager);
        recyclerViewDetalleAsignatura.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        /*adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicion = (int) recyclerViewDetalleAsignatura.getChildAdapterPosition(v);
                Log.d("POSICION", Integer.toString(posicion));
            }
        });*/


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alumnosSeleccionados = new ArrayList<Integer>();
                for (int i=0; i < adaptador.getSelectedItems().size();i++){
                    alumnosSeleccionados.add(adaptador.getItemPk(adaptador.getSelectedItems().get(i)));
                }
                Log.d("SELECCIÓN", adaptador.getSelectedItems().toString());
                Log.d("ALUMNADO", alumnosSeleccionados.toString());
            }
        });


        //se borran la selección que se haya hecho previamente
        if (actionMode != null) {
            actionMode.finish();
        }

        new DetalleAsignaturaFragment.GetAlumnado().execute();
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
        new DetalleAsignaturaFragment.GetAlumnado().execute();
        //se almacena la fecha seleccionada por si se rota la pantalla
        SharedPreferences settings = PreferenceManager
                .getDefaultSharedPreferences(getActivity());
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("fecha_seleccionada", Long.toString(fecha));
        editor.apply();
    }


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
                urlConnection.disconnect();
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
}
