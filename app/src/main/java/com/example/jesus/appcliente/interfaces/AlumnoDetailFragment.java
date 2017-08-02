package com.example.jesus.appcliente.interfaces;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jesus.appcliente.BuildConfig;
import com.example.jesus.appcliente.R;
import com.example.jesus.appcliente.clases.Alumno;
import com.example.jesus.appcliente.clases.CircleBitmapDisplayer;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class AlumnoDetailFragment extends Fragment {

    private TextView nombre, apellido1, apellido2, fecha_nacimiento, email, grupo;
    private ImageView foto;
    private ListView asignaturas;
    private Alumno alumno;
    private int idAlumno;
    private Bundle parametros;
    private Uri uriFoto;
    private String imagepath=null;
    final static int RESULTADO_EDITAR= 1;
    final static int RESULTADO_GALERIA= 2;
    final static int RESULTADO_FOTO= 3;
    final static int SOLICITUD_PERMISO_READ_EXTERNAL_STORAGE = 0;
    final static int SOLICITUD_PERMISO_WRITE_EXTERNAL_STORAGE = 1;

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

        ImageView camara = (ImageView) view.findViewById(R.id.camara);
        camara.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                tomarFoto(null);
            }
        });
        ImageView galeria = (ImageView) view.findViewById(R.id.gallery);
        galeria.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                galeria(null);
            }
        });
        ImageView delFoto = (ImageView) view.findViewById(R.id.delFoto);
        delFoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                eliminarFoto(null);
            }
        });

        return view;
    }


    @Override
    public void onActivityCreated(Bundle state) {
        super.onActivityCreated(state);

        new AlumnoDetailFragment.GetAlumno().execute();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.alumno_detail, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.accion_compartir:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, alumno.getNombre() + "  " + alumno.getApellido1() + " " + alumno.getApellido2());
                startActivity(intent);
                return true;

            case R.id.accion_editar:
                /*Intent i = new Intent(getActivity(), EdicionLugarActivity.class);
                i.putExtra("id", id);
                startActivityForResult(i, RESULTADO_EDITAR);*/
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RESULTADO_EDITAR) {
            //new AlumnoDetailFragment.GetAlumno().execute();

        }
        else if (requestCode == RESULTADO_GALERIA && resultCode == Activity.RESULT_OK) {
            alumno.setFoto(data.getDataString());
            //new AlumnoDetailFragment.ActualizarAlumno().execute();
            ponerFoto(foto, data.getDataString());

            Uri selectedImageUri = data.getData();
            imagepath = getPath(selectedImageUri);
            //Bitmap bitmap=BitmapFactory.decodeFile(imagepath);
            //imageview.setImageBitmap(bitmap);
            //messageText.setText("Uploading file path:" +imagepath);


            new AlumnoDetailFragment.UploadFile().execute();
        }
        else if(requestCode == RESULTADO_FOTO && resultCode == Activity.RESULT_OK
                 && uriFoto!=null) {
            alumno.setFoto(uriFoto.toString());
            //new AlumnoDetailFragment.ActualizarAlumno().execute();
            ponerFoto(foto, uriFoto.toString());
        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    public void galeria(View view) {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED){

            Intent intent = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULTADO_GALERIA);
        }
        else{
            solicitarPermisoLeerAlmacenamiento();
        }
    }

    public void tomarFoto(View view) {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            /*uriFoto = Uri.fromFile(new File(Environment.
                    getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    + File.separator + "img_" + (System.currentTimeMillis() / 1000) + ".jpg"));*/

            uriFoto = FileProvider.getUriForFile(getActivity(),
                    BuildConfig.APPLICATION_ID + ".provider",
                    new File(Environment.
                            getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                            + File.separator + "img_" + (System.currentTimeMillis() / 1000) + ".jpg"));

            /* También es posible con:
            uriFoto = FileProvider.getUriForFile(VistaLugarActivity.this,
                    BuildConfig.APPLICATION_ID + ".provider",  createImageFile());
             */

            intent.putExtra(MediaStore.EXTRA_OUTPUT, uriFoto);
            startActivityForResult(intent, RESULTADO_FOTO);
        }
        else{
            solicitarPermisoEscribirAlmacenamiento();
        }
    }

    protected void ponerFoto(ImageView imageView, String uri) {
        ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
        String imageUri;
        if (uri != null && !uri.isEmpty()){
            imageUri = uri;
        }
        else{
            imageUri="drawable://" + R.drawable.sinfoto;// from drawables (non-9patch images)
        }
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                //.displayer(new CircleBitmapDisplayer())
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();
        imageLoader.displayImage(imageUri, imageView, options);

    }



    public void eliminarFoto(View view) {
        alumno.setFoto(null);
        new AlumnoDetailFragment.ActualizarAlumno().execute();
        ponerFoto(foto, null);
    }

    void solicitarPermisoLeerAlmacenamiento() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Snackbar.make(getView(), "Sin el permiso de almacenamiento"
                    +" no se puede acceder a las imágenes del dispositivo.", Snackbar.LENGTH_INDEFINITE)
                    .setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            requestPermissions(
                                    new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE},
                                    SOLICITUD_PERMISO_READ_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {

            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    SOLICITUD_PERMISO_READ_EXTERNAL_STORAGE);
        }
    }

    void solicitarPermisoEscribirAlmacenamiento() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Snackbar.make(getView(), "Sin el permiso de almacenamiento"
                    +" no se pueden almacenar fotografías.", Snackbar.LENGTH_INDEFINITE)
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

        if (requestCode == SOLICITUD_PERMISO_READ_EXTERNAL_STORAGE) {
            if (grantResults.length== 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                galeria(null);
            }
            else {

                Snackbar.make(getView(), "Sin el permiso, no se puede acceder a las imágenes del dispositivo",
                        Snackbar.LENGTH_LONG).show();
            }
        }
        else if (requestCode == SOLICITUD_PERMISO_WRITE_EXTERNAL_STORAGE) {

            if (grantResults.length== 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                tomarFoto(null);
            }
            else {

                Snackbar.make(getView(), "Sin el permiso, no se pueden almacenar las fotografías",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }




    private class UploadFile extends AsyncTask<Void, Void, Boolean> {

        private Exception exception;

        protected Boolean doInBackground(Void...var1) {

            String fileName = imagepath;

            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            File sourceFile = new File(imagepath);

            if (!sourceFile.isFile()) {

                //dialog.dismiss();

                Log.e("uploadFile", "Source File not exist :" + imagepath);
                return false;

            }
            else
            {
                try {

                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    //URL url = new URL(upLoadServerUri);

                    //obtención del token
                    SharedPreferences settings = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    String token = settings.getString("auth_token", ""/*default value*/);

                    //Creando la conexión
                    String domain = getResources().getString(R.string.domain);
                    URL url = new URL(domain + "api/alumno/" + Integer.toString(idAlumno) + "/");



                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("PUT");
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("Authorization", "JWT " + token);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());


                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"nombre\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(alumno.getNombre());
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"apellido1\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(alumno.getApellido1()+"prueba");
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"apellido2\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(alumno.getApellido2());
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"fecha_nacimiento\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(alumno.getFecha_nacimiento());
                    dos.writeBytes(lineEnd);

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"foto\";filename=\""
                            + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);



                    // Responses from the server (code and message)
                    int serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadFile", "HTTP Response is : "
                            + serverResponseMessage + ": " + serverResponseCode);

                    if(serverResponseCode == 200){

                        //Toast.makeText(getActivity(), "File Upload Complete.", Toast.LENGTH_SHORT).show();

                    }

                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();

                } catch (MalformedURLException ex) {

                    //dialog.dismiss();
                    ex.printStackTrace();

                    //Toast.makeText(getActivity(), "MalformedURLException", Toast.LENGTH_SHORT).show();


                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {

                    //dialog.dismiss();
                    e.printStackTrace();

                    //Toast.makeText(getActivity(), "Got Exception : see logcat ", Toast.LENGTH_SHORT).show();

                    Log.e("Upload file to server", "Exception : "  + e.getMessage(), e);
                }
                //dialog.dismiss();
                return true;

            } // End else block

        }

        protected void onPostExecute(Boolean arg) {

        }
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
                grupo.setText(alumno.getGrupo());
                fecha_nacimiento.setText(alumno.getFecha_nacimiento());
                if(alumno.getEmail().isEmpty()){
                    email.setText("---");
                }else{ email.setText(alumno.getEmail()); }

                /*ImageLoader imageLoader = ImageLoader.getInstance(); // Get singleton instance
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
                imageLoader.displayImage(imageUri, foto, options);*/
                ponerFoto(foto, alumno.getFoto());

                if (!alumno.getAsignaturas().isEmpty()){
                    asignaturas.setAdapter(new ArrayAdapter(getActivity(),android.R.layout.test_list_item, alumno.getAsignaturas()));
                }
            }
            else{
                Toast.makeText(getActivity(),"Error al cargar el alumno", Toast.LENGTH_LONG).show();
            }
        }

    }

    private class ActualizarAlumno extends AsyncTask<Void, Void, Boolean> {

        HttpURLConnection urlConnection;

        public Boolean doInBackground(Void... var1) {
            try {

                //obtención del token
                SharedPreferences settings = PreferenceManager
                        .getDefaultSharedPreferences(getActivity());
                String token = settings.getString("auth_token", ""/*default value*/);

                //Creando la conexión
                String domain = getResources().getString(R.string.domain);
                URL url = new URL(domain + "api/alumno/" + Integer.toString(idAlumno) + "/");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("PUT");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Authorization", "JWT " + token);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("nombre", alumno.getNombre());
                jsonObject.put("apellido1", alumno.getApellido1());
                jsonObject.put("apellido2", alumno.getApellido2());
                jsonObject.put("fecha_nacimiento", alumno.getFecha_nacimiento());
                jsonObject.put("email", alumno.getEmail());
                //jsonObject.put("foto", alumno.getFoto());
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
                mensaje = "Alumno actualizado correctamente";
            }
            else{
                mensaje = "Problemas al actualizar";
            }
            Toast.makeText(getActivity(), mensaje, Toast.LENGTH_LONG).show();
        }
    }


}
