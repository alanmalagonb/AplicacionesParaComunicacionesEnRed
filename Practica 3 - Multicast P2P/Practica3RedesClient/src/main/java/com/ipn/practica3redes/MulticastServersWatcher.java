package com.ipn.practica3redes;

import java.util.List;

public class MulticastServersWatcher extends Thread{
    private final Files db;

    public MulticastServersWatcher(Files db){
        this.db = db;
    }

    /**
     * Comprobamos el temporizador inicializado en 6 segundos esperando un datagrama
     * si no se captura un datagrama borramos el servidor de la lista
     */
    public void run(){
        int i = 0;
        for(;;){
            try {
                List<DataFromServer> servers = db.getServers();
                if(!servers.isEmpty()){ // Verificar si la lista no está vacía

                    for (i = 0; i < servers.size(); i++){
                        /**Eliminar servidor si no se reporta*/
                        if(servers.get(i).getTemp() == 0) {
                            servers.remove(i);
                            i--; // Decrementar la posición del índice luego de remover un elemento
                        } else {
                            servers.get(i).setTemp(servers.get(i).getTemp()-1);
                        }
                    }

                    // Actualizar la lista en el archivo
                    db.setServers(servers);
                }
                Thread.sleep(1000);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }
}