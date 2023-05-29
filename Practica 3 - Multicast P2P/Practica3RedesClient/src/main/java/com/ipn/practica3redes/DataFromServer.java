package com.ipn.practica3redes;

import java.io.Serializable;

// Clase que tiene la información del server
public class DataFromServer implements Serializable {
    private String address;
    private int temp;
    private int port;

    private int sport;

    private int dport;

    public DataFromServer(String address, int temp, int port, int sport, int dport) {
        this.address = address;
        this.temp = temp;
        this.port = port;
        this.sport = sport;
        this.dport = dport;
    }

    public DataFromServer(){}

    public String getAddress() {
        return address;
    }
    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getSport() {
        return sport;
    }

    public void setSport(int sport) {
        this.sport = sport;
    }

    public int getDport() {
        return dport;
    }

    public void setDport(int dport) {
        this.dport = dport;
    }
}

