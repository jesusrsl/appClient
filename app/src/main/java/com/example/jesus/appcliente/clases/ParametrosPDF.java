package com.example.jesus.appcliente.clases;

import android.app.DownloadManager;
import android.content.Context;

/**
 * Created by jesus on 21/08/17.
 */

public class ParametrosPDF {
    private String url;
    private String nombre;
    private String descripcion;
    private Context contexto;
    private DownloadManager downloadManager;

    public ParametrosPDF(String url, String nombre, String descripcion, Context contexto, DownloadManager downloadManager) {
        this.url = url;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.contexto = contexto;
        this.downloadManager = downloadManager;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Context getContexto() {
        return contexto;
    }

    public void setContexto(Context contexto) {
        this.contexto = contexto;
    }

    public DownloadManager getDownloadManager() {
        return downloadManager;
    }

    public void setDownloadManager(DownloadManager downloadManager) {
        this.downloadManager = downloadManager;
    }
}
