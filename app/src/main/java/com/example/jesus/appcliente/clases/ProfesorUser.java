package com.example.jesus.appcliente.clases;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by jesus on 10/07/17.
 */

public class ProfesorUser implements Serializable{

    private int pk;
    private String first_name;
    private String last_name;
    private String username;
    private String email;

    public int getId() {
        return pk;
    }

    public String getIdToString() {
        return Integer.toString(pk);
    }

    public void setId(int id) {
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static ArrayList<ProfesorUser> obtenerProfesores(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ProfesorUser>>(){}.getType();
        /*ArrayList<ProfesorUser> lista = gson.fromJson(json,type);
        return lista;*/
        return gson.fromJson(json, type);
    }
}