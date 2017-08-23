package com.example.jesus.appcliente.clases;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jesus on 10/07/17.
 */

public class Grupo implements Serializable{

    private int pk;
    private String curso;
    private String unidad;
    private String tutor;
    private int distribucion;
    private int num_alumnos;

    public Grupo(int pk, String curso, String unidad, String tutor, int distribucion, int num_alumnos) {
        this.pk = pk;
        this.curso = curso;
        this.unidad = unidad;
        this.tutor = tutor;
        this.distribucion = distribucion;
        this.num_alumnos = num_alumnos;
    }

    public int getPk() {
        return pk;
    }

    public void setPk(int pk) {
        this.pk = pk;
    }

    public String getCurso() {
        return curso;
    }

    public void setCurso(String curso) {
        this.curso = curso;
    }

    public String getUnidad() {
        return unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
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

    public int getNum_alumnos() {
        return num_alumnos;
    }

    public void setNum_alumnos(int num_alumnos) {
        this.num_alumnos = num_alumnos;
    }

    public static ArrayList<Grupo> obtenerGrupos(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Grupo>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
