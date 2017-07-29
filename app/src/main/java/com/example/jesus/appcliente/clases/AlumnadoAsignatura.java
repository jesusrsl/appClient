package com.example.jesus.appcliente.clases;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jesus on 10/07/17.
 */

public class AlumnadoAsignatura implements Serializable{

    private int pk;
    private String nombre;
    private String grupoText;
    private int distribucion;
    private ArrayList<Alumno> alumnos;


    public int getPk() {
        return pk;
    }

    public String getIdToString() {
        return Integer.toString(pk);
    }


    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getGrupoText() {
        return grupoText;
    }

    public void setGrupoText(String grupoText) {
        this.grupoText = grupoText;
    }

    public int getDistribucion() {
        return distribucion;
    }

    public void setDistribucion(int distribucion) {
        this.distribucion = distribucion;
    }

    public ArrayList<Alumno> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(ArrayList<Alumno> alumnos) {
        this.alumnos = alumnos;
    }


    public static AlumnadoAsignatura obtenerAlumnadoAsignatura(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<AlumnadoAsignatura>(){}.getType();
        return gson.fromJson(json, type);
    }
}
