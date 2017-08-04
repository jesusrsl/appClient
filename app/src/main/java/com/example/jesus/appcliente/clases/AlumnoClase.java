package com.example.jesus.appcliente.clases;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jesus on 10/07/17.
 */

public class AlumnoClase implements Serializable{

    private int pk;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String foto;
    private Anotacion anotacion;

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

    public String getApellido1() {
        return apellido1;
    }

    public void setApellido1(String apellido1) {
        this.apellido1 = apellido1;
    }

    public String getApellido2() {
        return apellido2;
    }

    public void setApellido2(String apellido2) {
        this.apellido2 = apellido2;
    }


    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public Anotacion getAnotacion() {
        return anotacion;
    }

    public void setAnotacion(Anotacion anotacion) {
        this.anotacion = anotacion;
    }

    public static AlumnoClase obtenerAlumnoClase(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<AlumnoClase>(){}.getType();
        return gson.fromJson(json, type);
    }

    public static ArrayList<AlumnoClase> obtenerAlumnosClase(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<AlumnoClase>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
