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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.ProfesorUserAdapter;
import com.example.jesus.appcliente.clases.ProfesorUser;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class ListarProfesoresFragment extends Fragment {

    private Spinner spinnerParametro;
    private EditText dato;
    private Button boton;
    private RecyclerView recyclerViewProfesores;
    private RecyclerView.LayoutManager layoutManager;
    private ProfesorUserAdapter adaptador;
    private Bundle parametros;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_listar_profesores, container, false);
        this.recyclerViewProfesores = (RecyclerView) view.findViewById(R.id.recycler_view_profesores);
        this.spinnerParametro = (Spinner) view.findViewById(R.id.spinnerProfesorParametros);
        this.dato = (EditText) view.findViewById(R.id.editTextDato);
        parametros = getActivity().getIntent().getExtras();

        boton = (Button)view.findViewById(R.id.button3);
        boton.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view){
                btn_buscarProfe(view);
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

                /*Intent i = new Intent(getActivity(), ProfesorFormulario.class);
                i.putExtra("operacion", "actualizar");
                int posicion = (int) recyclerViewProfesores.getChildAdapterPosition(v);
                i.putExtra("idProfesor", adaptador.getItemPk(posicion));
                startActivity(i);*/
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



        if (parametros != null){
            this.spinnerParametro.setSelection((int)parametros.getLong("spinner"));
            this.dato.setText(parametros.getString("dato"));

        }

        new ListarProfesoresFragment.GetProfesores().execute();
    }

    public void btn_buscarProfe(View view){
        Intent intent = getActivity().getIntent();
        intent.putExtra("spinner", spinnerParametro.getSelectedItemId());
        intent.putExtra("dato", dato.getText().toString().trim());

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ListarProfesoresFragment fragmentProfesores = new ListarProfesoresFragment();
        fragmentProfesores.setArguments(intent.getExtras());
        transaction.replace(R.id.container, fragmentProfesores);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.addToBackStack(null).commit();
        getActivity().getSupportFragmentManager().executePendingTransactions();
    }

    //Get profesores
    private class GetProfesores extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection;

        public String doInBackground(Void... var1){
            /*try{
                return HttpRequest.get(var1[0]).accept("application/json").body();
            }
            catch(Exception e){
                return "";
            }*/

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
                ArrayList<ProfesorUser> profesores = ProfesorUser.obtenerProfesores(result);


                ArrayList<ProfesorUser> profesores_aux = new ArrayList<ProfesorUser>();

                if(spinnerParametro.getSelectedItem().toString().equals("Listar todo")){
                    profesores_aux = profesores;
                }
                else {
                    for (int i = 0; i < profesores.size(); i++){
                        switch(spinnerParametro.getSelectedItem().toString()){
                            case "Nombre":
                                if (profesores.get(i).getFirst_name().equals(dato.getText().toString().trim())){
                                    profesores_aux.add(profesores.get(i));
                                }
                                break;
                            case "Apellidos":
                                if (profesores.get(i).getLast_name().equals(dato.getText().toString().trim())){
                                    profesores_aux.add(profesores.get(i));
                                }
                                break;
                            case "Nombre de usuario":
                                if (profesores.get(i).getUsername().equals(dato.getText().toString().trim())){
                                    profesores_aux.add(profesores.get(i));
                                }
                                break;
                            case "E-mail":
                                if (profesores.get(i).getEmail().equals(dato.getText().toString().trim())){
                                    profesores_aux.add(profesores.get(i));
                                }
                                break;
                        }
                    }
                }

                if(profesores_aux.size() != 0){
                    /*ProfesorUserAdapter adapter = new ProfesorUserAdapter(ListarProfesores.this, profesores_aux);
                    listviewProfesor.setAdapter(adapter);
                    listviewProfesor.setOnItemClickListener( new AdapterView.OnItemClickListener()
                    {
                        @Override
                        public void onItemClick(AdapterView<?> parent , View view , int position ,long arg3)
                        {
                            Intent i = new Intent(ListarProfesores.this, ProfesorFormulario.class);
                            i.putExtra("operacion", "actualizar");
                            i.putExtra("idProfesor", ((ProfesorUser) parent.getAdapter().getItem(position)).getPk());
                            startActivity(i);
                        }
                    });*/

                    adaptador.actualizar(profesores_aux);
                    recyclerViewProfesores.getAdapter().notifyDataSetChanged();
                }
                else{
                    Toast.makeText(getActivity(),"No se generaron resultados", Toast.LENGTH_LONG).show();
                }

            }
        }


    }

}
