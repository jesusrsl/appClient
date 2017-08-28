package com.example.jesus.appcliente.clases;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jesus on 3/08/17.
 */

public class Anotacion {

    private int pk;
    private int alumno;
    private int asignatura;
    private String fecha;
    private String falta;
    private boolean trabaja;
    private int positivos;
    private int negativos;

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public int getAlumno() {
        return alumno;
    }

    public void setAlumno(int alumno) {
        this.alumno = alumno;
    }

    public int getAsignatura() {
        return asignatura;
    }

    public void setAsignatura(int asignatura) {
        this.asignatura = asignatura;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFalta() {
        return falta;
    }

    public void setFalta(String falta) {
        this.falta = falta;
    }

    public boolean isTrabaja() {
        return trabaja;
    }

    public void setTrabaja(boolean trabaja) {
        this.trabaja = trabaja;
    }

    public int getPositivos() {
        return positivos;
    }

    public void setPositivos(int positivos) {
        this.positivos = positivos;
    }

    public int getNegativos() {
        return negativos;
    }

    public void setNegativos(int negativos) {
        this.negativos = negativos;
    }

    public static Anotacion obtenerAnotacion(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<Anotacion>(){}.getType();
        return gson.fromJson(json, type);
    }

    public static ArrayList<Anotacion> obtenerAnotaciones(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Anotacion>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
