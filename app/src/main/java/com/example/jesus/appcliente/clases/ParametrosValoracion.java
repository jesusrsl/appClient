package com.example.jesus.appcliente.clases;

import android.app.DownloadManager;
import android.content.Context;

/**
 * Created by jesus on 21/08/17.
 */

public class ParametrosValoracion {
    private String url;
    private String valoracion;
    private int alumnoPk;
    private int posicion;
    private Object object;

    public ParametrosValoracion(String url, String valoracion, int alumnoPk, int posicion, Object object) {
        this.url = url;
        this.valoracion = valoracion;
        this.alumnoPk = alumnoPk;
        this.posicion = posicion;
        this.object = object;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getValoracion() {
        return valoracion;
    }

    public void setValoracion(String valoracion) {
        this.valoracion = valoracion;
    }

    public int getAlumnoPk() {
        return alumnoPk;
    }

    public void setAlumnoPk(int alumnoPk) {
        this.alumnoPk = alumnoPk;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }
}
