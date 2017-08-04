package com.example.jesus.appcliente.clases;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jesus on 10/07/17.
 */

public class DetalleAsignatura implements Serializable{

    private int pk;
    private String nombre;
    private String grupo;
    private int distribucion;
    private ArrayList<AlumnoClase> alumnos;


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

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public int getDistribucion() {
        return distribucion;
    }

    public void setDistribucion(int distribucion) {
        this.distribucion = distribucion;
    }

    public ArrayList<AlumnoClase> getAlumnos() {
        return alumnos;
    }

    public void setAlumnos(ArrayList<AlumnoClase> alumnos) {
        this.alumnos = alumnos;
    }

    public static DetalleAsignatura obtenerAlumnadoAsignatura(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<DetalleAsignatura>(){}.getType();
        return gson.fromJson(json, type);
    }
}
