package com.example.jesus.appcliente.interfaces;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.AsignaturaProfesor;
import com.example.jesus.appcliente.clases.AsignaturaProfesorAdapter;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class MainFragment extends Fragment {

    private RecyclerView recyclerViewAsignaturasProfesor;
    private RecyclerView.LayoutManager layoutManager;
    private AsignaturaProfesorAdapter adaptador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=true;
        MainActivity.isOtherFragmentShown=false;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        this.recyclerViewAsignaturasProfesor = (RecyclerView) view.findViewById(R.id.recycler_view_misasignaturas);
        return view;
    }


    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        ArrayList<AsignaturaProfesor> asignaturas = new ArrayList<AsignaturaProfesor>();
        adaptador = new AsignaturaProfesorAdapter(getContext(), asignaturas);
        recyclerViewAsignaturasProfesor.setAdapter(adaptador);
        this.layoutManager = new LinearLayoutManager(getContext());
        recyclerViewAsignaturasProfesor.setLayoutManager(layoutManager);
        recyclerViewAsignaturasProfesor.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        adaptador.setOnItemClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = getActivity().getIntent();
                int posicion = (int) recyclerViewAsignaturasProfesor.getChildAdapterPosition(v);
                intent.putExtra("idAsignatura", adaptador.getItemPk(posicion));
                intent.putExtra("fecha", System.currentTimeMillis());

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                DetalleAsignaturaFragment fragmentDetalleAsignatura = new DetalleAsignaturaFragment();
                fragmentDetalleAsignatura.setArguments(intent.getExtras());
                transaction.replace(R.id.container, fragmentDetalleAsignatura);
                transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                transaction.addToBackStack(null).commit();
                getActivity().getSupportFragmentManager().executePendingTransactions();
            }
        });

        new MainFragment.GetAsignaturasProfesor().execute();

    }




    //Get asignaturas
    private class GetAsignaturasProfesor extends AsyncTask<Void, Void, String> {

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
                URL url = new URL(domain + "api/asignaturas/");
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
                Toast.makeText(getActivity(),"No se generaron resultados", Toast.LENGTH_LONG).show();

            }
            else{
                ArrayList<AsignaturaProfesor> asignaturas = AsignaturaProfesor.obtenerAsignaturas(result);

                if(asignaturas.size() != 0){

                    adaptador.actualizar(asignaturas);
                    recyclerViewAsignaturasProfesor.getAdapter().notifyDataSetChanged();


                }
                else{
                    Toast.makeText(getActivity(),"No se generaron resultados", Toast.LENGTH_LONG).show();
                }
            }
        }

    }

}
