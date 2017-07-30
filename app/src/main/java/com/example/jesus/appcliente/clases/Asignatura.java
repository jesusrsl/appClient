package com.example.jesus.appcliente.clases;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jesus on 10/07/17.
 */

public class Asignatura implements Serializable{

    private int pk;
    private String nombre;
    private String profesorText;
    private String grupoText;
    private int distribucion;

    public Asignatura(int pk, String nombre, String profesorText, String grupoText, int distribucion) {
        this.pk = pk;
        this.nombre = nombre;
        this.profesorText = profesorText;
        this.grupoText = grupoText;
        this.distribucion = distribucion;
    }

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

    public String getProfesorText() {
        return profesorText;
    }

    public void setProfesorText(String profesorText) {
        this.profesorText = profesorText;
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

    public static ArrayList<Asignatura> obtenerAsignaturas(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Asignatura>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
