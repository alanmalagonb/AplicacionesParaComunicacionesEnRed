package com.ipn.practica3redes;

import javafx.scene.control.Alert;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class DownloadClient extends Thread {
    private ClientController app; // Controlador de la aplicación del cliente
    private Socket cl; // Socket del cliente para conectarse al servidor

    /**
     * Constructor de la clase que recibe el controlador de la aplicación
     * del cliente
     */
    public DownloadClient(ClientController app) {
        this.app = app;
    }

    /**
     * Método que establece la conexión con el servidor local en la dirección especificada
     */
    public void serverConnection(DataFromServer server) {
        try {
            // Crear un socket para conectarse al servidor en la dirección y puerto especificados
            cl = new Socket(server.getAddress(), server.getDport());
            // Imprimir un mensaje en la consola indicando que el cliente se ha conectado
            System.out.println(Constants.CLIENT_CONNECTED);
        } catch (Exception e) {
            // Imprimir cualquier excepción que se haya producido durante la conexión
            e.printStackTrace();
        }
    }

    /**
     * Método que descarga el archivo especificado del servidor
     */
    public void receiveFile(FoundFile file) {
        if (cl != null) {
            try {
                // Obtener el flujo de salida del socket para enviar el nombre del archivo a descargar
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream());
                // Enviar el nombre del archivo a descargar
                dos.writeUTF(file.getPath());
                dos.flush();

                // Esperar una respuesta del servidor indicando el nombre y tamaño del archivo a descargar
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                String name = dis.readUTF();
                long tam = dis.readLong();

                // Crear un flujo de salida para guardar el archivo en el disco
                DataOutputStream dosFile = new DataOutputStream(new FileOutputStream(String.format(Constants.FILES_PATH, name)));

                // Crear un buffer para leer el archivo del socket
                byte[] b = new byte[Constants.FILE_BUFFER_SIZE];
                long received = 0;
                int percentage;
                int n;
                // Leer el archivo del socket y guardarlo en disco
                while (received < tam) {
                    n = dis.read(b);
                    dosFile.write(b, 0, n);
                    dosFile.flush();
                    received += n;
                    percentage = (int) ((received * 100) / tam);
                    // Actualizar la barra de progreso en la interfaz de usuario
                    app.setProgressBar(percentage);
                }

                // Cerrar los flujos y el socket
                dos.close();
                dosFile.close();
                dis.close();
                cl.close();

                // Mostrar un mensaje de éxito en la interfaz de usuario
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText(null);
                alert.setContentText(Constants.DOWNLOADED_FILE);
                alert.showAndWait();

                // Restablecer la barra de progreso a cero
                app.setProgressBar(0);

            } catch (IOException e) {
                // Imprimir cualquier excepción que se haya producido durante la descarga
                e.printStackTrace();
            }
        }
    }

    /**
     * Método run() sobrescrito de la clase Thread. Este método se ejecuta cuando se
     * inicia el hilo utilizando el método start() de la clase Thread. Sin embargo,
     * este método no realiza ninguna tarea específica en esta clase.
     */
    public void run() {}
}