package com.ipn.practica3redes;

public class Constants {
    public static final int BUFFER_SIZE = 1024;

    public static final String WINDOW_TITLE = "Práctica 3";

    /**MULTICAST SERVER CONSTANTS*/
    public static final String MULTICAST_ADDRESS = "228.1.1.1";
    public static final int MULTICAST_PORT = 9014;

    /**SEARCH SERVER CONSTANTS*/
    public static final int SEARCH_PORT = 1207;

    /**DOWNLOAD SERVER CONSTANTS*/
    public static final String FILES_PATH = "downloads/%s";
    public static final int FILE_BUFFER_SIZE = 1500;
    public static final int DOWNLOAD_PORT = 1208;

    /** MESSAGES*/
    public static final String CLIENT_CONNECTED = "Cliente conectado.";
    public static final String EMPTY_SERVERS_MESSAGE = "No hay servidores.";
    public static final String EMPTY_FILE_NAME = "Escribe un nombre.";
    public static final String FOUND_FILE = "Archivo encontrado.";
    public static final String DOWNLOADED_FILE = "Archivo descargado.";
    public static final String FILE_NOT_FOUND = "No se encontró ningún archivo.";

}
