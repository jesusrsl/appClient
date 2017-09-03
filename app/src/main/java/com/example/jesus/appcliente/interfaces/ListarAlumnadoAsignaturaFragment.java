package com.example.jesus.appcliente.interfaces;

import android.Manifest;
import android.app.DownloadManager;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.AlumnadoAsignatura;
import com.example.jesus.appcliente.clases.Alumno;
import com.example.jesus.appcliente.clases.AlumnoAdapter;
import com.example.jesus.appcliente.clases.DownloadPDFTask;
import com.example.jesus.appcliente.clases.ParametrosPDF;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ListarAlumnadoAsignaturaFragment extends Fragment {

    private TextView textViewAsignatura;
    private TextView textViewGrupo;
    private int idAsignatura;
    private String nombreAsignatura;
    private Button botonPDF;
    final static int SOLICITUD_PERMISO_WRITE_EXTERNAL_STORAGE = 1;
    private DownloadManager manager;

    private RecyclerView recyclerViewAlumnadoAsignatura;
    private RecyclerView.LayoutManager layoutManager;
    private AlumnoAdapter adaptador;
    private Bundle parametros;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.listar_alumnado_asignatura, container, false);
        this.recyclerViewAlumnadoAsignatura = (RecyclerView) view.findViewById(R.id.recycler_view_alumnado_asignatura);
        this.textViewAsignatura = (TextView) view.findViewById(R.id.textViewAsignatura);
        this.textViewGrupo = (TextView) view.findViewById(R.id.textViewGrupo);
        parametros = getActivity().getIntent().getExtras();
        this.idAsignatura = parametros.getInt("idAsignatura");

        botonPDF = (Button)view.findViewById(R.id.btnAsignaturaPDF);
        botonPDF.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view) {
                btn_asignatura_PDF(view);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        ArrayList<Alumno> alumnos = new ArrayList<Alumno>();
        adaptador = new AlumnoAdapter(getContext(), alumnos);
        recyclerViewAlumnadoAsignatura.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(getContext());
        recyclerViewAlumnadoAsignatura.setLayoutManager(layoutManager);
        recyclerViewAlumnadoAsignatura.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        new ListarAlumnadoAsignaturaFragment.GetAlumnado().execute();

        manager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
    }

    public void btn_asignatura_PDF(View view){

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            String url = "anota/asignatura/" + Integer.toString(idAsignatura) + "/PDF/";
            String nombre = "asignatura-" + nombreAsignatura +".pdf";
            String descripcion = "Listado del alumnado de la asignatura " + nombreAsignatura;

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
                btn_asignatura_PDF(null);
            }
            else {

                Snackbar.make(getView(), "Sin el permiso, no se pueden almacenar archivos",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }



    //Get profesores
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
                URL url = new URL(domain + "api/alumnado/asignaturas/" + Integer.toString(idAsignatura));
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
                AlumnadoAsignatura alumnado = AlumnadoAsignatura.obtenerAlumnadoAsignatura(result);

                nombreAsignatura = alumnado.getNombre();
                textViewAsignatura.setText(alumnado.getNombre());
                textViewGrupo.setText(alumnado.getGrupoText());

                if(!alumnado.getAlumnos().isEmpty()) {
                    adaptador.actualizar(alumnado.getAlumnos());
                    recyclerViewAlumnadoAsignatura.getAdapter().notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getActivity(),"No se generaron resultados", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}
