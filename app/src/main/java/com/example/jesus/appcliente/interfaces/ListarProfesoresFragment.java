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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.DownloadPDFTask;
import com.example.jesus.appcliente.clases.ParametrosPDF;
import com.example.jesus.appcliente.clases.ProfesorUserAdapter;
import com.example.jesus.appcliente.clases.ProfesorUser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ListarProfesoresFragment extends Fragment {

    private Button botonPDF;
    private RecyclerView recyclerViewProfesores;
    private RecyclerView.LayoutManager layoutManager;
    private ProfesorUserAdapter adaptador;
    final static int SOLICITUD_PERMISO_WRITE_EXTERNAL_STORAGE = 1;
    private DownloadManager manager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.listar_profesores, container, false);
        this.recyclerViewProfesores = (RecyclerView) view.findViewById(R.id.recycler_view_profesores);

        botonPDF = (Button)view.findViewById(R.id.btnProfesoradoPDF);
        botonPDF.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view) {
                btn_profesorado_PDF(view);
            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        ArrayList<ProfesorUser> profesores = new ArrayList<ProfesorUser>();
        adaptador = new ProfesorUserAdapter(getContext(), profesores);
        recyclerViewProfesores.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(getContext());
        recyclerViewProfesores.setLayoutManager(layoutManager);
        recyclerViewProfesores.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getActivity().getIntent();
                int posicion = (int) recyclerViewProfesores.getChildAdapterPosition(v);
                intent.putExtra("idProfesor", adaptador.getItemPk(posicion));

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                ProfesorDetailFragment fragmentProfesorDetail = new ProfesorDetailFragment();
                fragmentProfesorDetail.setArguments(intent.getExtras());
                transaction.replace(R.id.container, fragmentProfesorDetail);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null).commit();
                getActivity().getSupportFragmentManager().executePendingTransactions();

            }
        });

        new ListarProfesoresFragment.GetProfesores().execute();

        manager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);

    }


    public void btn_profesorado_PDF(View view){

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            ParametrosPDF parametros = new ParametrosPDF("anota/profesores/PDF/", "profesorado.pdf", "Listado del profesorado", getContext(), manager);
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
                btn_profesorado_PDF(null);
            }
            else {

                Snackbar.make(getView(), "Sin el permiso, no se pueden almacenar archivos",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }


    //Get profesores
    private class GetProfesores extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(Void... var1){

            StringBuilder result = new StringBuilder();

            try{
                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);


                //Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/profesores/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestProperty("Authorization", "JWT " + token);

                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
                in.close();
                reader.close();
                urlConnection.disconnect();
                Log.d("JSON", result.toString());
            }
            catch (java.net.MalformedURLException e){
                e.printStackTrace();
                return "";
            }
            catch(java.io.IOException e){
                e.printStackTrace();
                return "";
            }
            catch(Exception e){
                e.printStackTrace();
                return "";
            }
            finally {
                urlConnection.disconnect();
            }

            return result.toString();

        }

        public void onPostExecute(String result){

            if(result.isEmpty()){
                Toast.makeText(getActivity(),"No se generaron resultados", Toast.LENGTH_LONG).show();

            }
            else{
                ArrayList<ProfesorUser> profesores = ProfesorUser.obtenerProfesores(result);

                if(profesores.size() != 0){

                    adaptador.actualizar(profesores);
                    recyclerViewProfesores.getAdapter().notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getActivity(),"No se generaron resultados", Toast.LENGTH_LONG).show();
                }

            }
        }


    }
}
