package com.example.lab5_20210795;


import java.io.Serializable;

public class Habit implements Serializable {
    private int idUnico;
    private String nombre;
    private String categoria;
    private int frecuenciaHoras;
    private long fechaInicioMillis;

    public Habit(int idUnico, String nombre, String categoria, int frecuenciaHoras, long fechaInicioMillis) {
        this.idUnico = idUnico;
        this.nombre = nombre;
        this.categoria = categoria;
        this.frecuenciaHoras = frecuenciaHoras;
        this.fechaInicioMillis = fechaInicioMillis;
    }

    public int getIdUnico() {
        return idUnico;
    }

    public void setIdUnico(int idUnico) {
        this.idUnico = idUnico;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public int getFrecuenciaHoras() {
        return frecuenciaHoras;
    }

    public void setFrecuenciaHoras(int frecuenciaHoras) {
        this.frecuenciaHoras = frecuenciaHoras;
    }

    public long getFechaInicioMillis() {
        return fechaInicioMillis;
    }

    public void setFechaInicioMillis(long fechaInicioMillis) {
        this.fechaInicioMillis = fechaInicioMillis;
    }
}
