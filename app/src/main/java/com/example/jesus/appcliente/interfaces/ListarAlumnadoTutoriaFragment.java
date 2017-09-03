package com.example.jesus.appcliente.interfaces;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.example.jesus.appcliente.clases.AlumnadoGrupo;
import com.example.jesus.appcliente.clases.Alumno;
import com.example.jesus.appcliente.clases.AlumnoAdapter;
import com.example.jesus.appcliente.clases.DownloadPDFTask;
import com.example.jesus.appcliente.clases.ParametrosPDF;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ListarAlumnadoTutoriaFragment extends Fragment {

    private TextView textViewGrupo;
    private Button botonPDF;
    private int idGrupo;
    private String nombreGrupo;
    private int distribucionGrupo;
    final static int SOLICITUD_PERMISO_WRITE_EXTERNAL_STORAGE = 1;
    private DownloadManager manager;
    private RecyclerView recyclerViewAlumnadoTutoria;
    private RecyclerView.LayoutManager layoutManager;
    private AlumnoAdapter adaptador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.listar_alumnado_tutoria, container, false);
        this.recyclerViewAlumnadoTutoria = (RecyclerView) view.findViewById(R.id.recycler_view_alumnado_tutoria);
        this.textViewGrupo = (TextView) view.findViewById(R.id.textViewGrupo);

        botonPDF = (Button)view.findViewById(R.id.btnTutoriaPDF);
        botonPDF.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view) {
                btn_tutoria_PDF(view);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        ArrayList<Alumno> alumnos = new ArrayList<Alumno>();
        adaptador = new AlumnoAdapter(getContext(), alumnos);
        recyclerViewAlumnadoTutoria.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(getContext());
        recyclerViewAlumnadoTutoria.setLayoutManager(layoutManager);
        recyclerViewAlumnadoTutoria.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

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

        new ListarAlumnadoTutoriaFragment.GetAlumnado().execute();

        manager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
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
                URL url = new URL(domain + "api/tutoria/");
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
                textViewGrupo.setText(alumnado.getGrupo());

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
}
