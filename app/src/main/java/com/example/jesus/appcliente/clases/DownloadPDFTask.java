package com.example.jesus.appcliente.clases;

import android.app.DownloadManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.example.jesus.appcliente.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jesus on 21/08/17.
 */

public class DownloadPDFTask extends AsyncTask<ParametrosPDF, Void, File> {

    String urlString;
    String nombre;
    String descripcion;
    Context contexto;
    DownloadManager downloadManager;

    @Override
    protected File doInBackground(ParametrosPDF... params) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        urlString = params[0].getUrl();
        nombre = params[0].getNombre();
        descripcion = params[0].getDescripcion();
        contexto = params[0].getContexto();
        downloadManager = params[0].getDownloadManager();

        try {
            //Creando la conexión
            String domain = contexto.getResources().getString(R.string.domain);
            URL url = new URL(domain + urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e("RESPONSE", "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage());
                return  null;
            }


            // download the file
            input = connection.getInputStream();
            String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + File.separator + nombre;
            File file = new File(path);
            int fileNum = 0;
            while(file.exists() && !file.isDirectory()) {
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
            connection.disconnect();

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
            }
            catch (IOException e) {
                e.printStackTrace();
            }

            if (connection != null)
                connection.disconnect();
        }
        //return null;
    }

    @Override
    protected void onPostExecute(File result) {

        if (result == null) {
            Toast.makeText(contexto, "Error en la descarga", Toast.LENGTH_LONG).show();
        }
        else{

            Log.d("FILE", result.getPath());
            Log.d("LENGTH", Long.toString(result.length()));
            long id = downloadManager.addCompletedDownload(nombre, descripcion, true, "application/pdf", result.getPath(), result.length(), true);

            Toast.makeText(contexto, "Archivo descargado con éxito", Toast.LENGTH_LONG).show();

                /*Snackbar.make(getView(), "Archivo descargado con éxito",
                        Snackbar.LENGTH_LONG)
                        .setAction("ABRIR DESCARGAS", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
                            }
                        })
                        .show();*/

        }

    }
}
