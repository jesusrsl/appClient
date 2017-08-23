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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.Asignatura;
import com.example.jesus.appcliente.clases.AsignaturaAdapter;
import com.example.jesus.appcliente.clases.DownloadPDFTask;
import com.example.jesus.appcliente.clases.ParametrosPDF;
import com.example.jesus.appcliente.clases.NombreGrupo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static android.content.Context.DOWNLOAD_SERVICE;

public class ListarAsignaturasFragment extends Fragment {

    private RecyclerView recyclerViewAsignaturas;
    private RecyclerView.LayoutManager layoutManager;
    private AsignaturaAdapter adaptador;
    private Button botonPDF, botonBuscar;
    private Spinner spinner;
    private ArrayList<NombreGrupo> nombreGrupos;
    private Bundle parametros;
    private int idGrupo;
    private String nombreGrupo;
    final static int SOLICITUD_PERMISO_WRITE_EXTERNAL_STORAGE = 1;
    private DownloadManager manager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.listar_asignaturas, container, false);
        this.recyclerViewAsignaturas = (RecyclerView) view.findViewById(R.id.recycler_view_asignaturas);
        this.spinner = (Spinner) view.findViewById(R.id.spinnerAsignatura);
        parametros = getActivity().getIntent().getExtras();

        botonPDF = (Button)view.findViewById(R.id.btnAsignaturasPDF);
        botonPDF.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view) {
                btn_asignaturas_PDF(view);
            }
        });

        botonBuscar = (Button)view.findViewById(R.id.btnBuscarAsignaturas);
        botonBuscar.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view){
                btn_buscarAsignaturas(view);
            }

        });

        return view;
    }


    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        ArrayList<Asignatura> asignaturas = new ArrayList<Asignatura>();
        adaptador = new AsignaturaAdapter(getContext(), asignaturas);
        recyclerViewAsignaturas.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(getContext());

        if (parametros != null){
            idGrupo = parametros.getInt("grupo");
        }
        else{
            idGrupo = 0;
        }

        //inicialización del spinner
        new ListarAsignaturasFragment.GetSpinnerGrupos().execute();


        recyclerViewAsignaturas.setLayoutManager(layoutManager);
        recyclerViewAsignaturas.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getActivity().getIntent();
                int posicion = (int) recyclerViewAsignaturas.getChildAdapterPosition(v);
                intent.putExtra("idAsignatura", adaptador.getItemPk(posicion));

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                ListarAlumnadoAsignaturaFragment fragmentAlumnadoAsignatura = new ListarAlumnadoAsignaturaFragment();
                fragmentAlumnadoAsignatura.setArguments(intent.getExtras());
                transaction.replace(R.id.container, fragmentAlumnadoAsignatura);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null).commit();
                getActivity().getSupportFragmentManager().executePendingTransactions();
            }
        });

        String url;
        if(idGrupo == 0){
            url = "api/lista/asignaturas/";
        }
        else{
            url = "api/grupo/" + Integer.toString(idGrupo) + "/asignaturas/";
        }

        new ListarAsignaturasFragment.GetAsignaturas().execute(url);

        manager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
    }

    public void btn_buscarAsignaturas(View view){
        Intent intent = getActivity().getIntent();
        if (spinner.getSelectedItemId() != 0 && nombreGrupos != null){  //un grupo seleccionado
            idGrupo = nombreGrupos.get((int)spinner.getSelectedItemId()-1).getPk();
        }
        else{   //se listan todos los grupos
            idGrupo = 0;
        }
        intent.putExtra("grupo", idGrupo);
        Log.d("ITEM", spinner.getSelectedItemId() + " " +Integer.toString(idGrupo));

        //se carga el mismo fragment, con el grupo seleccionado en el spinner
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ListarAsignaturasFragment fragmentAsignaturas = new ListarAsignaturasFragment();
        fragmentAsignaturas.setArguments(intent.getExtras());
        transaction.replace(R.id.container, fragmentAsignaturas);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null).commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }

    public void btn_asignaturas_PDF(View view){

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            String url;
            String nombre;
            String descripcion;
            if (idGrupo == 0){
                url = "instituto/asignaturas/PDF/";
                nombre = "asignaturas.pdf";
                descripcion="Listado de asignaturas";
            }
            else{
                url = "instituto/asignaturas/"+ Integer.toString(idGrupo) + "/PDF/";
                nombre = "asignaturas-"+ nombreGrupo.replace(" ", "-") +".pdf";
                descripcion="Listado de asignaturas de " + nombreGrupo;
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
                btn_asignaturas_PDF(null);
            }
            else {

                Snackbar.make(getView(), "Sin el permiso, no se pueden almacenar archivos",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }


    //Get asignaturas
    private class GetAsignaturas extends AsyncTask<String, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(String... var1){

            StringBuilder result = new StringBuilder();

            try{

                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);

                //Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + var1[0]);
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
                ArrayList<Asignatura> asignaturas = Asignatura.obtenerAsignaturas(result);

                if(asignaturas.size() != 0){

                    adaptador.actualizar(asignaturas);
                    recyclerViewAsignaturas.getAdapter().notifyDataSetChanged();


                }
                else{
                    Toast.makeText(getActivity(),"No se generaron resultados", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

    //Get asignaturas
    private class GetSpinnerGrupos extends AsyncTask<Void, Void, String> {

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
                URL url = new URL(domain + "api/spinner/grupos/");
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
                Toast.makeText(getActivity(),"El spinner no puede ser inicializado", Toast.LENGTH_LONG).show();

            }
            else{

                nombreGrupos = NombreGrupo.obtenerNombreGrupos(result);

                if(nombreGrupos.size() != 0){

                    ArrayList<String> spinnerGrupos = new ArrayList<String>();
                    //primr valor
                    spinnerGrupos.add("Todos");
                    for(int i=0; i<nombreGrupos.size();i++){
                        spinnerGrupos.add(nombreGrupos.get(i).getGrupo());
                    }

                    final ArrayAdapter<String> gruposAdapter =
                            new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, spinnerGrupos);
                    gruposAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    gruposAdapter.notifyDataSetChanged();

                    spinner.setAdapter(gruposAdapter);

                    if (idGrupo != 0)   //hay un grupo seleccionado
                    {
                        int position = 0;
                        for(int i=0;i<nombreGrupos.size();i++){
                            if (nombreGrupos.get(i).getPk() == idGrupo){
                                position = i;
                                nombreGrupo = nombreGrupos.get(i).getGrupo();
                                break;
                            }
                        }
                        spinner.setSelection(position+1);
                    }
                    else{
                        spinner.setSelection(0);    //1ª opcion: todos los grupos
                    }


                }
                else{
                    Toast.makeText(getActivity(),"El spinner no puede ser inicializado", Toast.LENGTH_LONG).show();
                }
            }
        }

    }
}

