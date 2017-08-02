package com.example.jesus.appcliente.clases;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jesus on 10/07/17.
 */

public class Alumno implements Serializable{

    private int pk;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private String fecha_nacimiento;
    private String email;
    private String foto;
    private String grupo;
    private ArrayList<String> asignaturas;

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

    public String getFecha_nacimiento() {
        return fecha_nacimiento;
    }

    public void setFecha_nacimiento(String fecha_nacimiento) {
        this.fecha_nacimiento = fecha_nacimiento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    public ArrayList<String> getAsignaturas() {
        return asignaturas;
    }

    public void setAsignaturas(ArrayList<String> asignaturas) {
        this.asignaturas = asignaturas;
    }

    public static Alumno obtenerAlumno(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<Alumno>(){}.getType();
        return gson.fromJson(json, type);
    }

    public static ArrayList<Alumno> obtenerAlumnos(String json){
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Alumno>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
