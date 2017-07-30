package com.example.jesus.appcliente.clases;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jesus on 10/07/17.
 */

public class ProfesorDetail implements Serializable{

    private int pk;
    private String first_name;
    private String last_name;
    private ArrayList<String> asignatura_set;

    public int getPk() {
        return pk;
    }

    public String getIdToString() {
        return Integer.toString(pk);
    }

    public void setPk(int id) {
        this.pk = id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }


    public ArrayList<String> getAsignatura_set() {
        return asignatura_set;
    }

    public void setAsignatura_set(ArrayList<String> asignatura_set) {
        this.asignatura_set = asignatura_set;
    }

    public static ProfesorDetail obtenerProfesor(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<ProfesorDetail>(){}.getType();
        return gson.fromJson(json, type);
    }
}
