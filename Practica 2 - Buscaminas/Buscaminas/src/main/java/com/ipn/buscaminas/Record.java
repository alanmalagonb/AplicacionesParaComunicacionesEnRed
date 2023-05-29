package com.ipn.buscaminas;

public class Record {
    private String jugador;
    private String direccion;
    private int puerto;
    private int nivel;
    private int puntos;
    private String tiempo;

    public Record(String jugador, String direccion, int puerto, int nivel, int puntos, String tiempo) {
        this.jugador = jugador;
        this.direccion = direccion;
        this.puerto = puerto;
        this.nivel = nivel;
        this.puntos = puntos;
        this.tiempo = tiempo;
    }

    public String getJugador() {
        return jugador;
    }

    public void setJugador(String jugador) {
        this.jugador = jugador;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getPuntos() {
        return puntos;
    }

    public void setPuntos(int puntos) {
        this.puntos = puntos;
    }

    public String getTiempo() {
        return tiempo;
    }

    public Record() {
    }

    public void setTiempo(String tiempo) {
        this.tiempo = tiempo;
    }
}
