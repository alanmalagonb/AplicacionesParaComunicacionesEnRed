/* Práctica 1 Aplicación Drive para almacenamiento de archivos
   Alumnos: Malagon Baeza Alan Adrian
            Martinez Chavez Jorge Alexis
   6CM1 Aplicaciones para Comunicaciones en Red
*/

package com.ipn.drive;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Client {

    private static Options options;

    static {
        options = new Options();
    }

    public static void opciones() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Client.class.getResource("options.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.getIcons().add(new Image(Client.class.getResourceAsStream("img/settings.png")));
        stage.setTitle("Opciones");
        stage.setScene(scene);
        stage.show();
    }

    public static void crearCarpetaLocal(String directory,String nombre) throws IOException {
        File file = new File(directory + "\\" + nombre);
        if (!file.exists()) {
            file.mkdir();
        }
    }
    // Eliminar carpetas local y remotamente
    public static void eliminarArchivoLocal(String path, String target) throws IOException {
        // Obtenemos el directorio
        File f = new File("");
        String absoluta = f.getAbsolutePath();
        String ruta_archivos = absoluta + "\\" + path + "\\" + target;

        File f2 = new File(ruta_archivos);
        String output;
        if (f2.exists()) {
            if (f2.isDirectory()) {
                eliminarCarpeta(f2);
                f2.delete();
                output = "Se eliminó el directorio";
            } else {
                if (f2.delete()) {
                    output = "Se eliminó el archivo";
                } else {
                    output = "No se pudo eliminar el archivo";
                }
            }
        } else {
            output = "No se encontró el archivo a eliminar";
        }
    }

    // Eliminar contenidos de una carpeta
    private static void eliminarCarpeta(File f2) {
        File[] allContents = f2.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                if (file.isDirectory()) eliminarCarpeta(file);
                file.delete();
            }
        }
    }


    // Renomnbrar archivos/carpetas local o remotamente
    public static void renombrarArchivoLocal(String path, String rename) throws IOException {
        File f = new File("");
        String absoluta = f.getAbsolutePath();
        String ruta_nombre = absoluta + "\\" + path;
        String ruta_rename = absoluta + "\\" + rename;

        File f2 = new File(ruta_nombre);
        File f3 = new File(ruta_rename);

        boolean flag = f2.renameTo(f3);
        String output;
        if (flag) {
            output = "Archivo correctamente renombrado";
        } else {
            output = "No se pudo renombrar.";
        }
    }

    /* Visualizar carpetas (y su contenido), así como archivos almacenados
    local y remotamente
     */
    public static List<Directory> visualizarLocales(String dir){
        File f = new File("");
        String absoluta = f.getAbsolutePath();
        String ruta_archivos = absoluta + "\\" + dir;

        // Obtenemos los archivos en el directorio indicado
        File f2 = new File(ruta_archivos);
        File[] fileList = f2.listFiles();
        String name = "";
        List<Directory> directorios = new ArrayList<>();
        if (fileList != null) {
            Directory directory;
            for (int i = 0; i < fileList.length; i++) {
                directory = new Directory();
                name = fileList[i].getName();
                if (fileList[i].isDirectory()) {
                    name = name + "\\";
                    directory.setImgSrc("folder.png");
                    directory.setSize(0);
                } else {
                    directory.setImgSrc("file.png");
                    directory.setSize(fileList[i].length());
                }
                directory.setName(name);
                directorios.add(directory);
            }
        } else {
            System.out.println("Carpeta Vacía");
        }
        return directorios;
    }
    public static List<Directory> visualizarArchivos(String directory) {
        System.out.println(directory);
        List<Directory> directories = new ArrayList<>();
        try {
            Socket cl = new Socket(options.getHost(), options.getPort());
            cl.setReceiveBufferSize(options.getReceiveBufferSize());
            cl.setSendBufferSize(options.getSendBufferSize());
            cl.setSoTimeout(options.getTimeout());
            cl.setSoLinger(options.getLingerStatus(), options.getLingerLong());
            cl.setTcpNoDelay(options.isTcpNoDelay());

            DataOutputStream dos = new DataOutputStream(cl.getOutputStream());

            dos.writeInt(1); // Envio de bandera opcion
            dos.flush();
            dos.writeUTF(directory); // Envio directorio destino
            dos.flush();

            ObjectInputStream ois = new ObjectInputStream(cl.getInputStream()); // Flujo de entrada
            directories = (List<Directory>) ois.readObject(); // Lectura archivos/carpetas en formato

            ois.close();
            dos.close();
            cl.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return directories;
    }

    // Crear carpetas local y remotamente
    public static void crearCarpeta(String path, String nombre) {
        try {
            Socket cl = new Socket(options.getHost(), options.getPort());
            cl.setReceiveBufferSize(options.getReceiveBufferSize());
            cl.setSendBufferSize(options.getSendBufferSize());
            cl.setSoTimeout(options.getTimeout());
            cl.setSoLinger(options.getLingerStatus(), options.getLingerLong());

            DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); // Flujo de salida
            dos.writeInt(2);
            dos.flush();
            dos.writeUTF(nombre); //Envio nombre del archivo dos.flush();
            dos.flush();
            dos.writeUTF(path); // Sends the target directory
            dos.flush();

            dos.close();
            cl.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Eliminar carpetas local y remotamente
    public static void eliminarCarpeta(String path, String target) {
        try {
            Socket cl = new Socket(options.getHost(), options.getPort());
            cl.setReceiveBufferSize(options.getReceiveBufferSize());
            cl.setSendBufferSize(options.getSendBufferSize());
            cl.setSoTimeout(options.getTimeout());
            cl.setSoLinger(options.getLingerStatus(), options.getLingerLong());

            DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); // Flujo de salida
            dos.writeInt(3); // Envio bandera opcion
            dos.flush();
            dos.writeUTF(path); // Envio nombre del archivo dos.flush();
            dos.flush();
            dos.writeUTF(target); // Envio directorio destino

            DataInputStream dis = new DataInputStream(cl.getInputStream());
            String msg = dis.readUTF(); // Lectura mensaje de exito
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText(null);
            a.setContentText(msg);
            a.show();

            dis.close();
            dos.close();
            cl.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Subir archivos y carpetas a la carpeta remota
    public static void subirArchivo(File f, String directory) {
        try {
            Socket cl = new Socket(options.getHost(), options.getPort());
            cl.setReceiveBufferSize(options.getReceiveBufferSize());
            cl.setSendBufferSize(options.getSendBufferSize());
            cl.setSoTimeout(options.getTimeout());
            cl.setSoLinger(options.getLingerStatus(), options.getLingerLong());

            String nombre = f.getName();
            String path = f.getAbsolutePath();
            long tam = f.length();

            DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); // Flujo de salida
            dos.writeInt(0); // Envio bandera
            dos.flush();
            dos.writeUTF(nombre); //Envio nombre del archivo
            dos.flush();

            if (f.isFile()) {
                System.out.println("\nPreparandose para enviar archivo " + path + " de " + tam + " bytes\n");

                if (directory.isEmpty())
                    dos.writeUTF(options.getDirectory()); // Envio directorio destino
                else
                    dos.writeUTF(options.getDirectory() + "\\" + directory); // Envio directorio destino
                dos.flush();
                dos.writeLong(tam); // Envio tamaño del archivo
                dos.flush();
                dos.writeInt(1); // Envio flag tipo de archivo
                dos.flush();

                long enviados = 0;
                int l = 0, porcentaje = 0, buffer = options.getSendBufferSize();
                DataInputStream dis = new DataInputStream(new FileInputStream(path)); //Flujo de entrada
                while (enviados < tam) {
                    byte[] b = new byte[buffer];
                    l = dis.read(b);
                    dos.write(b, 0, l); // dos.write(b);
                    dos.flush();
                    enviados = enviados + l;
                    porcentaje = (int) ((enviados * 100) / tam);
                    System.out.println("Enviado el " + porcentaje + " % del archivo.Bytes enviados: " + enviados + "/" + tam);
                } // while
                System.out.println("Archivo " + f.getName() + " enviado...");
                dis.close();
            }//if
            else {
                if (directory.isEmpty())
                    dos.writeUTF(options.getDirectory());  // Envio directorio destino
                else
                    dos.writeUTF(options.getDirectory() + "\\" + directory); // Envio directorio destino
                dos.flush();
                dos.writeLong(tam); //Envio tamaño del archivo
                dos.flush();
                dos.writeInt(0); // Envio flag tipo de archivo
                dos.flush();

                File folder = new File(f.getAbsolutePath());
                File[] files = folder.listFiles();
                assert files != null;
                for (File fi : files) {
                    subirArchivo(fi,directory+"\\"+folder.getName());
                } // for
            } // else
            dos.close();
            cl.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // Descargar archivos y carpetas desde la carpeta remota
    public static void descargarArchivo(String nombre, String directory) {
        try{
            Socket cl = new Socket(options.getHost(), options.getPort());
            cl.setReceiveBufferSize(options.getReceiveBufferSize());
            cl.setSendBufferSize(options.getSendBufferSize());
            cl.setSoTimeout(options.getTimeout());
            cl.setSoLinger(options.getLingerStatus(), options.getLingerLong());

            DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); // Flujo de salida
            dos.writeInt(4); // Envio bandera opcion
            dos.flush();
            dos.writeUTF(nombre); // Envio nombre del archivo
            dos.flush();
            dos.writeUTF(directory); // Envio directorio destino
            dos.flush();

            DataInputStream dis = new DataInputStream(cl.getInputStream()); // Flujo de entrada
            String nombre_ = dis.readUTF(); // Lectura nombre del archivo
            String directory_ = dis.readUTF(); // Lectura directio destino
            long tam = dis.readLong(); // Lectura tamaño del archivo
            int flag = dis.readInt(); // Lectura flag tipo de archivo

            if (flag == 1) { // Es un archivo
                System.out.println("Comienza descarga del archivo " + nombre_ + " de " + tam + " bytes\n");

                // Creamos directorio
                File f = new File("");
                String absoluta = f.getAbsolutePath();
                String ruta_archivos = absoluta +"\\" + directory_  +"\\";

                File f2 = new File(ruta_archivos);
                f2.mkdirs();
                f2.setWritable(true);

                dos = new DataOutputStream(new FileOutputStream(ruta_archivos+"\\"+nombre_)); // Flujo de salida

                long recibidos = 0;
                int l = 0, porcentaje = 0, buffer = 1500;

                while (recibidos < tam) {
                    byte[] b = new byte[buffer];
                    l = dis.read(b);
                    dos.write(b, 0, l);
                    dos.flush();
                    recibidos = recibidos + l;
                    porcentaje = (int) ((recibidos * 100) / tam);
                    System.out.println("Recibido el " + porcentaje + " % del archivo. Bytes leídos: " + recibidos + "/" + tam);
                } // while
                System.out.println("Archivo " + nombre_ + " recibido..");
            } // if
            else { // Es una carpeta
                System.out.println("Comienza descarga del directorio " + nombre_ + " de " + tam + " bytes");

                File file = new File("local\\Descargas\\"+directory_+"\\"+ nombre_);
                if (!file.exists()) {
                    file.mkdir();
                }

                int nfiles = dis.readInt(); // Lectura numero archivos contenidos en la carpeta
                for (int i = 0; i < nfiles; i++) {
                    String nombreArchivo = dis.readUTF(); // Lectura nombre del archivo
                    String directoryName = dis.readUTF(); // Lectura directorio destino
                    String folderName = dis.readUTF(); // Lectura directorio actual
                    descargarArchivo(nombreArchivo,directoryName+"\\"+folderName);
                } // for
            } // else

            dos.close();
            dis.close();
            cl.close();
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    // Renombrar archivos/carpetas local o remotamente
    public static void renombrarArchivo(String path, String rename) {
        try {
            Socket cl = new Socket(options.getHost(), options.getPort());
            cl.setReceiveBufferSize(options.getReceiveBufferSize());
            cl.setSendBufferSize(options.getSendBufferSize());
            cl.setSoTimeout(options.getTimeout());
            cl.setSoLinger(options.getLingerStatus(), options.getLingerLong());

            DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); // Flujo de salida

            dos.writeInt(5); // Envio bandera opcion
            dos.flush();
            dos.writeUTF(path); // Envio nombre del archivo dos.flush();
            dos.flush();
            dos.writeUTF(rename); // Envio directorio destino
            dos.flush();

            DataInputStream dis = new DataInputStream(cl.getInputStream()); // Flujo de entrada
            String msg = dis.readUTF(); // Lectura mensaje de exito
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText(null);
            a.setContentText(msg);
            a.show();

            dis.close();
            dos.close();
            cl.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void guardarOpciones(String bEntrada, String bEscritura, String lLong, String lStatus,  String nagle,String temporizador ) {
        options.setSendBufferSize(Integer.parseInt(bEscritura));
        options.setReceiveBufferSize(Integer.parseInt(bEntrada));
        options.setTimeout(Integer.parseInt(temporizador));
        options.setLingerLong(Integer.parseInt(lLong));
        options.setLingerStatus(Boolean.parseBoolean(lStatus));
        options.setTcpNoDelay(Boolean.parseBoolean(nagle));
        System.out.println(options);
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText("Opciones guardadas..");
        a.show();
    }
}