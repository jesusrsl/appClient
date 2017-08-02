package com.example.jesus.appcliente.interfaces;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.Alumno;
import com.example.jesus.appcliente.clases.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class AlumnoDetailFragment extends Fragment {

    private TextView nombre, apellido1, apellido2, fecha_nacimiento, email, grupo;
    private ImageView foto;
    private ListView asignaturas;
    private Alumno alumno;
    private int idAlumno;
    private Bundle parametros;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.layout_alumno_detail, container, false);
        this.apellido1 = (TextView) view.findViewById(R.id.textViewAp1AlumnoDetail);
        this.apellido2 = (TextView) view.findViewById(R.id.textViewAp2AlumnoDetail);
        this.nombre = (TextView) view.findViewById(R.id.textViewNombreAlumnoDetail);
        this.grupo = (TextView) view.findViewById(R.id.textViewGrupoAlumnoDetail);
        this.fecha_nacimiento = (TextView) view.findViewById(R.id.textViewNacAlumnoDetail);
        this.email = (TextView) view.findViewById(R.id.textViewEmailAlumnoDetail);
        this.foto = (ImageView) view.findViewById(R.id.imageViewAlumnoDetail);
        this.asignaturas = (ListView) view.findViewById(R.id.listViewAsignaturasMatriculadas);
        parametros = getActivity().getIntent().getExtras();
        this.idAlumno = parametros.getInt("idAlumno");
        return view;
    }


    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        new AlumnoDetailFragment.GetAlumno().execute();

    }


    //Get profesores
    private class GetAlumno extends AsyncTask<Void, Void, Alumno> {

        HttpURLConnection urlConnection;

        public Alumno doInBackground(Void... var1){

            StringBuilder result = new StringBuilder();

            try{

                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);

                //Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/alumno/" + Integer.toString(idAlumno) + "/");
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
                //JSONObject jsonObject = new JSONObject(result.toString());

                alumno = Alumno.obtenerAlumno(result.toString());
                return alumno;

            }
            catch (java.net.MalformedURLException e){
                e.printStackTrace();
                return null;
            }
            catch(java.io.IOException e){
                e.printStackTrace();
                return null;
            }
            catch(Exception e){
                e.printStackTrace();
                return null;
            }
            finally {
                urlConnection.disconnect();
            }

        }

        public void onPostExecute(Alumno alumno){
            super.onPostExecute(alumno);

            if(alumno != null){
                apellido1.setText(alumno.getApellido1());
                apellido2.setText(alumno.getApellido2());
                nombre.setText(alumno.getNombre());
                grupo.setText("Grupo: " + alumno.getGrupo());
                fecha_nacimiento.setText("Fecha de nacimiento: " + alumno.getFecha_nacimiento());
                if(alumno.getEmail().isEmpty()){
                    email.setText("Email: ---");
                }else{ email.setText("Email: " + alumno.getEmail()); }

                ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
                String imageUri;
                if (alumno.getFoto() != null){
                    imageUri = alumno.getFoto();
                }
                else{
                    imageUri="drawable://" + R.drawable.sinfoto;// from drawables (non-9patch images)
                }
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .displayer(new CircleBitmapDisplayer())
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .build();
                imageLoader.displayImage(imageUri, foto, options);


                if (!alumno.getAsignaturas().isEmpty()){
                    asignaturas.setAdapter(new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, alumno.getAsignaturas()));
                }
            }
            else{
                Toast.makeText(getActivity(),"Error al cargar el alumno", Toast.LENGTH_LONG).show();
            }
        }

    }


}
