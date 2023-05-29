package com.ipn.practica3redes;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;


public class SearchClient extends Thread{

    private ClientController app; // Controlador de la aplicación cliente
    private Socket cl; // Socket para la conexión con el servidor de búsqueda
    private final Files db; // Base de datos de los servidores y archivos encontrados
    private String address;

    public SearchClient(Files db, ClientController controller) {
        this.app = controller;
        this.db = db;
    }

    /**
     * Realiza la conexión con el primer servidor de búsqueda encontrado en la lista de servidores
     */
    public void serverConnection(DataFromServer server){
        try {
            this.address = server.getAddress();
            System.out.println(server.getAddress()+" " +server.getPort());
            cl = new Socket(address, server.getSport());
            System.out.println(Constants.CLIENT_CONNECTED);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Método que busca el archivo especificado en todos los servidores registrados en la base de datos
     * @param fileName Nombre del archivo a buscar
     */
    public void receiveFile(String fileName){
        if(cl != null){
            try {
                // Mandamos el archivo a buscar al servidor de búsqueda
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                dos.writeUTF(fileName);
                dos.flush();


                try {

                    FileServer fs = null;

                            // Esperamos por la respuesta del servidor
                            ObjectInputStream ois = new ObjectInputStream(cl.getInputStream());
                            // Recibimos la busqueda
                            FoundFile response = (FoundFile) ois.readObject();
                            if(!response.getFileName().equals("unknown")){
                                // Si el archivo es encontrado, lo agregamos a la lista de archivos y servidores
                                fs = new FileServer(address,response);
                                db.addFile(fs);
                            }
                            ois.close();


                    // Actualizamos la vista de la aplicación con los archivos encontrados
                    app.search(fs);
                }catch (Exception e){
                    e.printStackTrace();
                }
                dos.close();
                cl.close();

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void run(){}
}
