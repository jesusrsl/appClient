package com.example.jesus.appcliente.clases;

import android.app.DownloadManager;
import android.content.Context;

/**
 * Created by jesus on 21/08/17.
 */

public class ParametrosAnotacion {
    private String url;
    private Anotacion anotacion;
    private int alumnoPk;
    private int posicion;
    private Object object;

    public ParametrosAnotacion(String url, Anotacion anotacion, int alumnoPk, int posicion, Object object) {
        this.url = url;
        this.anotacion = anotacion;
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

    public Anotacion getAnotacion() {
        return anotacion;
    }

    public void setAnotacion(Anotacion anotacion) {
        this.anotacion = anotacion;
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
