package com.example.jesus.appcliente.interfaces;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.AlumnoClase;
import com.example.jesus.appcliente.clases.AlumnoClaseAdapter;
import com.example.jesus.appcliente.clases.ClickListener;
import com.example.jesus.appcliente.clases.DetalleAsignatura;
import com.example.jesus.appcliente.clases.DialogoSelectorFecha;
import com.example.jesus.appcliente.clases.DownloadPDFTask;
import com.example.jesus.appcliente.clases.ParametrosPDF;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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

import static android.content.Context.DOWNLOAD_SERVICE;

public class VerAnotacionesFragment extends Fragment {

    private TextView textViewFechaInicio, textViewFechaFin;
    private Button botonAnotaciones;
    private int idAsignatura;
    private String nombreAsignatura;
    private long fecha, fechaInicio, fechaFin;

    private Bundle parametros;
    final static int SOLICITUD_PERMISO_WRITE_EXTERNAL_STORAGE = 1;
    private DownloadManager manager;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //visibilidad de los fragments
        MainActivity.isMainShown=false;
        MainActivity.isOtherFragmentShown=true;

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.ver_anotaciones, container, false);

        parametros = getActivity().getIntent().getExtras();
        this.idAsignatura = parametros.getInt("idAsignatura");
        this.nombreAsignatura = parametros.getString("nombreAsignatura");
        this.fecha = parametros.getLong("fecha");   //fecha actual
        this.fechaInicio = fecha;
        this.fechaFin = fecha;

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

        this.textViewFechaInicio = (TextView) view.findViewById(R.id.textViewFechaInicio);
        textViewFechaInicio.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cambiarFechaInicio();
            }
        });
        textViewFechaInicio.setText(formato.format(new Date(fecha)));

        this.textViewFechaFin = (TextView) view.findViewById(R.id.textViewFechaFin);
        textViewFechaFin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                cambiarFechaFin();
            }
        });
        textViewFechaFin.setText(formato.format(new Date(fecha)));

        botonAnotaciones = (Button)view.findViewById(R.id.btnVerAnotaciones);
        botonAnotaciones.setOnClickListener( new View.OnClickListener() {

            public void onClick(View view) {
                btn_ver_Anotaciones(view);
            }
        });

        return view;
    }

    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        manager = (DownloadManager) getActivity().getSystemService(DOWNLOAD_SERVICE);
    }


    public void cambiarFechaInicio() {
        DialogoSelectorFecha dialogoFechaInicio = new DialogoSelectorFecha();
        dialogoFechaInicio.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anyo, int mes, int dia) {
                Calendar calendario = Calendar.getInstance();
                calendario.setTimeInMillis(fecha);
                calendario.set(Calendar.YEAR, anyo);
                calendario.set(Calendar.MONTH, mes);
                calendario.set(Calendar.DAY_OF_MONTH, dia);
                fechaInicio = calendario.getTimeInMillis();

                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                textViewFechaInicio.setText(formato.format(new Date(fechaInicio)));


            }
        });
        Bundle args = new Bundle();
        args.putLong("fecha", fecha);
        dialogoFechaInicio.setArguments(args);
        dialogoFechaInicio.show(getActivity().getSupportFragmentManager(), "selectorFechaInicio");
    }

    public void cambiarFechaFin() {
        DialogoSelectorFecha dialogoFechaFin = new DialogoSelectorFecha();
        dialogoFechaFin.setOnDateSetListener(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int anyo, int mes, int dia) {
                Calendar calendario = Calendar.getInstance();
                calendario.setTimeInMillis(fecha);
                calendario.set(Calendar.YEAR, anyo);
                calendario.set(Calendar.MONTH, mes);
                calendario.set(Calendar.DAY_OF_MONTH, dia);
                fechaFin = calendario.getTimeInMillis();

                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                textViewFechaFin.setText(formato.format(new Date(fechaFin)));


            }
        });
        Bundle args = new Bundle();
        args.putLong("fecha", fecha);
        dialogoFechaFin.setArguments(args);
        dialogoFechaFin.show(getActivity().getSupportFragmentManager(), "selectorFechaFin");
    }


    public void btn_ver_Anotaciones(View view){

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {


            String url = "api/anotaciones/PDF/";
            String nombre = "anotaciones-" + nombreAsignatura.replace(" ","-") +".pdf";
            String descripcion = "Anotaciones de " + nombreAsignatura;

            ParametrosPDF parametros = new ParametrosPDF(url, nombre, descripcion, getContext(), manager);
            AsyncTask<ParametrosPDF, Void, File> task =new VerAnotaciones();
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
                btn_ver_Anotaciones(null);
            }
            else {

                Snackbar.make(getView(), "Sin el permiso, no se pueden almacenar archivos",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }


    //Ver anotaciones
    public class VerAnotaciones extends AsyncTask<ParametrosPDF, Void, File> {

        String urlString;
        String nombre;
        String descripcion;
        Context contexto;
        DownloadManager downloadManager;

        @Override
        protected File doInBackground(ParametrosPDF... params) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection urlConnection = null;
            urlString = params[0].getUrl();
            nombre = params[0].getNombre();
            descripcion = params[0].getDescripcion();
            contexto = params[0].getContexto();
            downloadManager = params[0].getDownloadManager();

            try {
                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);

                //Creando la conexión
                String domain = contexto.getResources().getString(R.string.domain);
                URL url = new URL(domain + urlString);
                urlConnection = (HttpURLConnection) url.openConnection();
                //connection.connect();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "JWT " + token);

                //se escriben los datos para obtener el PDF
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("idAsignatura", idAsignatura);
                jsonObject.put("inicio", formatter.format(new Date(fechaInicio)));
                jsonObject.put("fin", formatter.format(new Date(fechaFin)));


                Log.d("JSON", jsonObject.toString());
                OutputStream os = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());
                writer.flush();
                writer.close();
                os.close();


                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (urlConnection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e("RESPONSE", "Server returned HTTP " + urlConnection.getResponseCode()
                            + " " + urlConnection.getResponseMessage());
                    return null;
                }


                // download the file
                input = urlConnection.getInputStream();
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + nombre;
                File file = new File(path);
                int fileNum = 0;
                while (file.exists() && !file.isDirectory()) {
                    fileNum++;
                    String newName = path.replaceAll(".pdf", "(" + fileNum + ").pdf");
                    file = new File(newName);
                }

                output = new FileOutputStream(file);
                Log.d("PATH", file.getPath());

                byte data[] = new byte[4096];

                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }
                //downloaded file
                output.close();
                input.close();
                urlConnection.disconnect();

                return file;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (urlConnection != null)
                    urlConnection.disconnect();
            }
            //return null;
        }

        @Override
        protected void onPostExecute(File result) {

            if (result == null) {
                Toast.makeText(contexto, "No existen anotaciones en las fechas indicadas. Por favor, inténtelo de nuevo", Toast.LENGTH_LONG).show();
            } else {

                Log.d("FILE", result.getPath());
                Log.d("LENGTH", Long.toString(result.length()));
                long id = downloadManager.addCompletedDownload(nombre, descripcion, true, "application/pdf", result.getPath(), result.length(), true);

                Toast.makeText(contexto, "Archivo descargado con éxito", Toast.LENGTH_LONG).show();
            }
        }
    }
}
