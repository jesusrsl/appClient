package com.example.jesus.appcliente.clases;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jesus on 10/07/17.
 */

public class AlumnadoGrupo implements Serializable{

    private int pk;
    private String grupo;
    private String tutor;
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

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }


    public String getTutor() {
        return tutor;
    }

    public void setTutor(String tutor) {
        this.tutor = tutor;
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


    public static AlumnadoGrupo obtenerAlumnadoGrupo(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<AlumnadoGrupo>(){}.getType();
        return gson.fromJson(json, type);
    }
}
