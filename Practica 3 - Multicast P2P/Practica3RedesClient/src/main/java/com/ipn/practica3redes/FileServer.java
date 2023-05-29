package com.ipn.practica3redes;

// Clase que guarda la informacion de un archivo y el servidor de origen
public class FileServer {
    private String fileInServer = new String();
    private FoundFile foundFile = new FoundFile();

    public FileServer(String fileInServer, FoundFile foundFile) {
        this.fileInServer = fileInServer;
        this.foundFile = foundFile;
    }

    public FileServer() {
    }

    public String getFileInServer() {
        return fileInServer;
    }

    public void setFileInServer(String fileInServer) {
        this.fileInServer = fileInServer;
    }

    public FoundFile getFoundFile() {
        return foundFile;
    }

    public void setFoundFile(FoundFile foundFile) {
        this.foundFile = foundFile;
    }
}
