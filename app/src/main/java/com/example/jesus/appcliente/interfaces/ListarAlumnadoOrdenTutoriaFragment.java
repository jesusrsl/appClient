package com.example.jesus.appcliente.interfaces;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.AlumnadoGrupo;
import com.example.jesus.appcliente.clases.Alumno;
import com.example.jesus.appcliente.clases.AlumnoFotoOrdenAdapter;
import com.example.jesus.appcliente.clases.DownloadPDFTask;
import com.example.jesus.appcliente.clases.ParametrosPDF;
import com.example.jesus.appcliente.helper.OnStartDragListener;
import com.example.jesus.appcliente.helper.SimpleItemTouchHelperCallback;
import com.yqritc.recyclerviewflexibledivider.HorizontalDividerItemDecoration;
import com.yqritc.recyclerviewflexibledivider.VerticalDividerItemDecoration;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ListarAlumnadoOrdenTutoriaFragment extends Fragment implements OnStartDragListener {

    private TextView textViewGrupo;
    private Spinner spinner;
    private Button botonPDF, botonGuardar, botonDistribucion;
    private int idGrupo;
    private String nombreGrupo;
    private int distribucionGrupo, nuevaDistribucion;
    private ArrayList<Integer> alumnos;
    final static int SOLICITUD_PERMISO_WRITE_EXTERNAL_STORAGE = 1;
    private DownloadManager manager;
    private RecyclerView recyclerViewAlumnadoTutoria;
    private RecyclerView.LayoutManager layoutManager;
    //private AlumnoAdapter adaptador;
    private AlumnoFotoOrdenAdapter adaptador;
    private ItemTouchHelper mItemTouchHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.listar_alumnado_tutoria_orden, container, false);
        this.recyclerViewAlumnadoTutoria = (RecyclerView) view.findViewById(R.id.recycler_view_alumnado_tutoria);
        this.textViewGrupo = (TextView) view.findViewById(R.id.textViewGrupo);
        this.spinner = (Spinner) view.findViewById(R.id.spinner);


        this.botonPDF = (Button)view.findViewById(R.id.btnTutoriaPDF);
        this.botonPDF.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view) {
                btn_tutoria_PDF(view);
            }
        });

        this.botonGuardar = (Button)view.findViewById(R.id.btnTutoriaGuardar);
        this.botonGuardar.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view) {
                btn_tutoria_guardar(view);
            }
        });

        this.botonDistribucion = (Button)view.findViewById(R.id.btnDistribucion);
        this.botonDistribucion.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view) {
                btn_distribucion(view);
            }
        });

        return view;

    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        //simple_spinner_item Specify the spinner TextView
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.distribucion, android.R.layout.simple_spinner_item);
        //simple_spinner_dropdown_item Specify the dropdown item TextView if not set , and the same as simple_spinner_item
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        ArrayList<Alumno> alumnos = new ArrayList<Alumno>();
        //adaptador = new AlumnoAdapter(getContext(), alumnos);
        adaptador = new AlumnoFotoOrdenAdapter(getContext(), alumnos, this);
        recyclerViewAlumnadoTutoria.setAdapter(adaptador);
        //this.layoutManager = new LinearLayoutManager(getContext());
        //recyclerViewAlumnadoTutoria.setLayoutManager(layoutManager);
        //recyclerViewAlumnadoTutoria.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));


        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getActivity().getIntent();
                int posicion = (int) recyclerViewAlumnadoTutoria.getChildAdapterPosition(v);
                intent.putExtra("idAlumno", adaptador.getItemPk(posicion));

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                AlumnoDetailFragment fragmentAlumnoDetail = new AlumnoDetailFragment();
                fragmentAlumnoDetail.setArguments(intent.getExtras());
                transaction.replace(R.id.container, fragmentAlumnoDetail);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null).commit();
                getActivity().getSupportFragmentManager().executePendingTransactions();
            }
        });

        new ListarAlumnadoOrdenTutoriaFragment.GetAlumnado().execute();

        manager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adaptador);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recyclerViewAlumnadoTutoria);
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    public void btn_tutoria_guardar(View view){

        alumnos = new ArrayList<Integer>();
        for (int i=0; i <recyclerViewAlumnadoTutoria.getAdapter().getItemCount(); i++){

            alumnos.add(adaptador.getItemPk(i));
        }

        Log.d("ARRAY", alumnos.toString());
        new UpdateDisposicion().execute();
    }

    public void btn_distribucion(View view){
        nuevaDistribucion = Integer.parseInt(spinner.getSelectedItem().toString());
        new ListarAlumnadoOrdenTutoriaFragment.UpdateDistribucion().execute();

    }

    public void btn_tutoria_PDF(View view){

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            String url;
            String nombre;
            String descripcion;
            if (idGrupo > 0 && nombreGrupo != null) {
                url = "anota/grupo/" + Integer.toString(idGrupo) + "/PDF/";
                nombre = "tutoría-" + nombreGrupo.replace(" ", "-") + ".pdf";
                descripcion = "Listado del alumnado de tutoría de " + nombreGrupo;
            }else{
                url = "anota/grupos/PDF/";
                nombre = "grupos.pdf";
                descripcion = "Listado de grupos";
            }

            ParametrosPDF parametros = new ParametrosPDF(url, nombre, descripcion, getContext(), manager);
            AsyncTask<ParametrosPDF, Void, File> task =new DownloadPDFTask();
            task.execute(parametros);
        }
        else{
            solicitarPermisoEscribirAlmacenamiento();
        }
    }


    void solicitarPermisoEscribirAlmacenamiento() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Snackbar.make(getView(), "Sin el permiso de almacenamiento"
                    +" no se pueden descargar archivos.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(
                                    new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE},
                                    SOLICITUD_PERMISO_WRITE_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    SOLICITUD_PERMISO_WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        if (requestCode == SOLICITUD_PERMISO_WRITE_EXTERNAL_STORAGE) {

            if (grantResults.length== 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                btn_tutoria_PDF(null);
            }
            else {

                Snackbar.make(getView(), "Sin el permiso, no se pueden almacenar archivos",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }


    //Get alumnado
    private class GetAlumnado extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(Void... var1){

            StringBuilder result = new StringBuilder();

            try{
                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);

                // Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/orden/tutoria/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Authorization", "JWT " + token);

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                JSONArray jsonArray = new JSONArray(result.toString());
                Log.d("JSON", jsonArray.getJSONObject(0).toString());
                return jsonArray.getJSONObject(0).toString();
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

            //return result.toString();

        }

        public void onPostExecute(String result){

            if(result.isEmpty()){
                textViewGrupo.setText("---");
                Toast.makeText(getActivity(),"Actualmente no tutoriza ningún grupo", Toast.LENGTH_LONG).show();
                botonPDF.setVisibility(View.GONE);
            }
            else{
                AlumnadoGrupo alumnado = AlumnadoGrupo.obtenerAlumnadoGrupo(result);

                idGrupo = alumnado.getPk();
                nombreGrupo = alumnado.getGrupo();
                botonPDF.setVisibility(View.VISIBLE);
                distribucionGrupo = alumnado.getDistribucion();
                int position = distribucionGrupo - 4;
                if(position < 0){ position = 0;}
                spinner.setSelection(position);
                textViewGrupo.setText(alumnado.getGrupo());

                layoutManager = new GridLayoutManager(getContext(), distribucionGrupo);
                recyclerViewAlumnadoTutoria.setLayoutManager(layoutManager);
                recyclerViewAlumnadoTutoria.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                        .color(Color.WHITE)
                        .build());
                recyclerViewAlumnadoTutoria.addItemDecoration(new VerticalDividerItemDecoration.Builder(getContext())
                        .color(Color.WHITE)
                        .build());

                if(!alumnado.getAlumnos().isEmpty()) {

                    adaptador.actualizar(alumnado.getAlumnos());
                    recyclerViewAlumnadoTutoria.getAdapter().notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getActivity(),"No existe alumnado matriculado en el grupo", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    private class UpdateDistribucion extends AsyncTask<Void, Void, Boolean> {

        HttpURLConnection urlConnection;

        public Boolean doInBackground(Void... var1) {
            try {

                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);

                //Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/grupo/" + Integer.toString(idGrupo) + "/distribucion/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "JWT " + token);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("distribucion", nuevaDistribucion);
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
                mensaje = "Distribución actualizada correctamente";

                //se actualiza el layout
                layoutManager = new GridLayoutManager(getContext(), nuevaDistribucion);
                recyclerViewAlumnadoTutoria.setLayoutManager(layoutManager);
                recyclerViewAlumnadoTutoria.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getContext())
                        .color(Color.WHITE)
                        .build());
                recyclerViewAlumnadoTutoria.addItemDecoration(new VerticalDividerItemDecoration.Builder(getContext())
                        .color(Color.WHITE)
                        .build());

            }
            else{
                mensaje = "Problemas al actualizar la distribución";
            }
            Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();
        }
    }

    private class UpdateDisposicion extends AsyncTask<Void, Void, Boolean> {

        HttpURLConnection urlConnection;

        public Boolean doInBackground(Void... var1) {
            try {

                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", "");

                //Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/grupo/" + Integer.toString(idGrupo) + "/disposicion/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "JWT " + token);

                JSONObject jsonObject = new JSONObject();
                JSONArray jsonArray = new JSONArray();
                for(int i=0;i<alumnos.size();i++){
                    jsonArray.put(alumnos.get(i));
                }
                jsonObject.put("alumnos", jsonArray);
                //urlConnection.setFixedLengthSt1reamingMode(jsonObject.toString().length());

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
                mensaje = "Disposición del alumnado actualizada correctamente";
            }
            else{
                mensaje = "Problemas al actualizar la disposición del alumnado";
            }
            Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();
        }
    }
}
