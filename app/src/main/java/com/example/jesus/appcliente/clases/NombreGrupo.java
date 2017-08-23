package com.example.jesus.appcliente.clases;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jesus on 10/07/17.
 */

public class NombreGrupo implements Serializable{

    private int pk;
    private String grupo;

    public NombreGrupo(int pk, String grupo) {
        this.pk = pk;
        this.grupo = grupo;
    }

    public int getPk() {
        return pk;
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

    public static ArrayList<NombreGrupo> obtenerNombreGrupos(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<NombreGrupo>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
