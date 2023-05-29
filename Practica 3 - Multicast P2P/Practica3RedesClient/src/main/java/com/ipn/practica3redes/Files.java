package com.ipn.practica3redes;

import java.util.ArrayList;
import java.util.List;

// Clase que simulara una base de datos
public class Files {

    private List<DataFromServer> servers = new ArrayList<>();

    private List<FileServer> files = new ArrayList<>();

    public List<DataFromServer> getServers() {
        return servers;
    }
    public List<FileServer> getFiles(){return files;}
    public void setServers(List<DataFromServer> servers) {
        this.servers = servers;
    }
    public void addServer(DataFromServer dfs){
        servers.add(dfs);
    }
    public void setFiles(List<FileServer> files){this.files = files;}
    public void addFile(FileServer fs){files.add(fs);}
    public void clearFiles(){files.clear();}

}

