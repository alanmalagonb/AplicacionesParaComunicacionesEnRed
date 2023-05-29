package com.ipn.practica3redes;

public class Constants {
    public static final int BUFFER_SIZE = 1024;

    public static final String SERVER_INIT_MSG = "Servidor iniciado";

    /**MULTICAST SERVER CONSTANTS*/

    // Dirrecion Multicast
    public static final String MULTICAST_ADDRESS = "228.1.1.1";
    // Puerto Multicast
    public static final int MULTICAST_PORT = 9014;

    /**SEARCH SERVER CONSTANTS*/
    public static final int SEARCH_PORT = 1210;
    public static final String FILES_PATH = "files";
    /**DOWNLOAD SERVER CONSTANTS*/
    public static final int DOWNLOAD_PORT = 1209;
    public static final int FILE_BUFFER_SIZE = 1500;

    /**MESSAGES*/
    public static final String CLIENT_CONNECTION = "Cliente conectado desde: %s: desde el puerto: %d";
    public static final String SENDING_FILE = "Mandando el archivo";
    public static final String FILE_SIZE = "Archivo: %s con tamaño de %s";
    public static final String FILE_SENT_SUCCESSFULLY = "Archivo enviado exitosamente";
}
