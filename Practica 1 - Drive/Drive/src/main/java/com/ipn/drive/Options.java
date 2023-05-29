/* Práctica 1 Aplicación Drive para almacenamiento de archivos
   Alumnos: Malagon Baeza Alan Adrian
            Martinez Chavez Jorge Alexis
   6CM1 Aplicaciones para Comunicaciones en Red
*/

package com.ipn.drive;

import java.io.*;

// Opciones
public class Options implements Serializable {

    private String host, directory;

    private int port, receiveBufferSize, sendBufferSize, timeout, lingerLong;
    private boolean lingerStatus,tcpNoDelay;

    public boolean isLingerStatus() {
        return lingerStatus;
    }

    public boolean isTcpNoDelay() {
        return tcpNoDelay;
    }

    public void setTcpNoDelay(boolean tcpNoDelay) {
        this.tcpNoDelay = tcpNoDelay;
    }



    // Constructor
    public Options() {
        // 192.168.43.1
        this.host = "192.168.43.73";
        //this.host = "localhost";
        this.directory = "remoto";
        this.port = 1201;
        this.receiveBufferSize = 1500;
        this.sendBufferSize = 1500;
        this.timeout = 300;
        this.lingerLong = 30;
        this.lingerStatus = true;
    }

    // Getters & Setters
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getReceiveBufferSize() {
        return receiveBufferSize;
    }

    public void setReceiveBufferSize(int receiveBufferSize) {
        this.receiveBufferSize = receiveBufferSize;
    }

    public int getSendBufferSize() {
        return sendBufferSize;
    }

    public void setSendBufferSize(int sendBufferSize) {
        this.sendBufferSize = sendBufferSize;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getLingerLong() {
        return lingerLong;
    }

    public void setLingerLong(int lingerLong) {
        this.lingerLong = lingerLong;
    }

    public boolean getLingerStatus() {
        return lingerStatus;
    }

    public void setLingerStatus(boolean lingerStatus) {
        this.lingerStatus = lingerStatus;
    }



}
