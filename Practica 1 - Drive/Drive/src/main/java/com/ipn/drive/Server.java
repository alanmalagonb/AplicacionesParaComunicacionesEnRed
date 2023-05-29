/* Práctica 1 Aplicación Drive para almacenamiento de archivos
   Alumnos: Malagon Baeza Alan Adrian
            Martinez Chavez Jorge Alexis
   6CM1 Aplicaciones para Comunicaciones en Red
*/

package com.ipn.drive;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static final int HOST_PORT = 1201;

    private static String folder = "remoto";

    public static void main(String[] args) {
        // Establecemos una conexión
        try {
            // Crea un socket de servidor, vinculado al puerto especificado.
            ServerSocket s = new ServerSocket(HOST_PORT);

            // Habilita/deshabilita la opción de socket SO_REUSEADDR.
            s.setReuseAddress(true);

            System.out.println("Servidor iniciado esperando archivos..");

            for (; ; ) {
                // Escucha la conexión que se realizará con este socket y la acepta.
                Socket cl = s.accept();

                // Imprimimos la direccion y el numero de puerto remoto
                // al que esta conectado este socket
                System.out.println("\n\nCliente conectado desde " + cl.getInetAddress() + ":" + cl.getPort());

                // Crea un flujo de entrada que usa el flujo de entrada subyacente especificado.
                // #getInputStream : Devuelve un flujo de entrada para este socket.
                DataInputStream dis = new DataInputStream(cl.getInputStream());

                // Bandera
                // #readInt : Lee cuatro bytes de entrada y devuelve un valor int.
                int flag = dis.readInt();

                // Opciones
                switch (flag) {
                    // 0 : Recibir archivos/carpeta
                    case 0 -> descargarArchivo(dis);
                    // 1 : Ver carpeta/archivos
                    case 1 -> {
                        folder = dis.readUTF();
                        listarArchivos(dis, cl);
                    }
                    // 2 : Crear carpeta
                    case 2 -> crearCarpeta(dis);
                    // 3 : Eliminar archivos/carpeta
                    case 3 -> eliminarArchivo(dis, cl);
                    // 4 : Enviar archivos/carpeta
                    case 4 -> {
                        String nombre = dis.readUTF();
                        String directory = dis.readUTF();
                        File f = new File(nombre);
                        enviarArchivo(dis, cl, f, directory);
                    }
                    // 5 : Renombrar archivos/carpeta
                    case 5 -> renombrarArchivo(dis, cl);
                } // switch
                cl.close(); // Cierra este socket.
            } // for
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Subir archivos y carpetas a la carpeta local
    private static void enviarArchivo(DataInputStream dis, Socket cl, File f, String directory) {
        try {
            String nombre = f.getName();
            String path = f.getAbsolutePath();
            long tam = f.length();

            if (f.isFile()) {
                System.out.println("\nPreparandose para enviar archivo " + path + " de " + tam + " bytes\n");

                DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); // Flujo de salida
                dos.writeUTF(nombre); // Envio nombre del archivo
                dos.flush();
                if (directory.isEmpty())
                    dos.writeUTF("local" + "\\Descargas"); // Sends the target directory
                else
                    dos.writeUTF("local" + "\\Descargas\\" + directory);
                dos.flush();
                dos.writeLong(tam); // Envio tamaño del archivo
                dos.flush();
                dos.writeInt(1); // Envio flag tipo de archivo
                dos.flush();

                long enviados = 0;
                int l = 0, porcentaje = 0, buffer = 1500;
                dis = new DataInputStream(new FileInputStream(path)); //Flujo de entrada
                while (enviados < tam) {
                    byte[] b = new byte[buffer];
                    l = dis.read(b);
                    dos.write(b, 0, l);// dos.write(b);
                    dos.flush();
                    enviados = enviados + l;
                    porcentaje = (int) ((enviados * 100) / tam);
                    System.out.println("Enviado el " + porcentaje + " % del archivo.Bytes enviados: " + enviados + "/" + tam);
                } // while

                System.out.println("Archivo " + f.getName() + " enviado...");

                dis.close();
                dos.close();
                cl.close();
            }//if
            else {
                DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); // Flujo de salida
                dos.writeUTF(nombre); // Envio nombre del archivo
                dos.flush();
                if (directory.isEmpty())
                    dos.writeUTF("local"); // Envio directorio destino
                else
                    dos.writeUTF("local" + "\\" + directory); // Envio directorio destino
                dos.flush();
                dos.writeLong(0); // Envio tamaño directorio
                dos.flush();
                dos.writeInt(0); // Envio flag  tipo de archivo
                dos.flush();

                File folder = new File(f.getAbsolutePath());
                File[] files = folder.listFiles();
                if (files != null) {
                    dos.writeInt(files.length); // Envio numero de archivos en el directorio
                    dos.flush();

                    for (File fi : files) {
                        dos.writeUTF(fi.getAbsolutePath()); // Envio directorio destino
                        dos.flush();
                        dos.writeUTF(directory); // Envio directorio padre
                        dos.flush();
                        dos.writeUTF(folder.getName()); // Envio directorio actual
                        dos.flush();
                    }
                } // if
                else {
                    dos.writeInt(0); // Envio numero de archivos en el directorio
                    dos.flush();
                } // else
                dis.close();
                dos.close();
                cl.close();
            } // else
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Descargar archivos y carpetas desde la carpeta local
    private static void descargarArchivo(DataInputStream dis) throws IOException {
        String nombre = dis.readUTF(); // Lectura nombre del archivo
        String directory = dis.readUTF(); // Obtenemos el directorio de destino
        long tam = dis.readLong(); // Lectura tamaño del archivo
        int flag = dis.readInt(); // Lectura flag opcion

        if (flag == 1) { // Es un archivo
            System.out.println("Comienza descarga del archivo " + nombre + " de " + tam + " bytes\n");

            // Creamos directorio
            File f = new File("");
            String absoluta = f.getAbsolutePath();
            String ruta_archivos = absoluta + "\\" + directory + "\\";

            File f2 = new File(ruta_archivos);
            f2.mkdirs();
            f2.setWritable(true);

            DataOutputStream dos = new DataOutputStream(new FileOutputStream(ruta_archivos + nombre)); // Flujo de salida
            long recibidos = 0;
            int l = 0, porcentaje = 0, buffer = 1500;
            while (recibidos < tam) {
                byte[] b = new byte[buffer];
                l = dis.read(b);
                dos.write(b, 0, l); // dos.write(b)
                dos.flush();
                recibidos = recibidos + l;
                porcentaje = (int) ((recibidos * 100) / tam);
                System.out.println("Recibido el " + porcentaje + " % del archivo. Bytes leídos: " + recibidos + "/" + tam);
            } // while
            System.out.println("Archivo " + nombre + " recibido..");
            dos.close();
        } // if
        else { // Es una carpeta
            System.out.println("Comienza descarga del directorio " + nombre + " de " + tam + " bytes");
            File file = new File(directory + "\\" + nombre);
            if (!file.exists()) {
                file.mkdir();
            }
        }
        dis.close();
    }


    /* Visualizar carpetas (y su contenido), así como archivos almacenados
    local y remotamente
     */
    public static void listarArchivos(DataInputStream dis, Socket cl) throws IOException {
        // Obtenemos el directorio
        File f = new File("");
        String absoluta = f.getAbsolutePath();
        String ruta_archivos = absoluta + "\\" + folder;

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

        // Crea un nuevo flujo de salida de datos para escribir datos en el flujo de salida subyacente especificado.
        ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
        // Escribe un objeto en el flujo de salida subyacente
        oos.writeObject(directorios);
        // Cierra este flujo de salida/entrada y libera cualquier recurso del sistema asociado con el flujo.
        oos.flush();

        oos.close();
        dis.close();
    }

    // Crear carpetas local y remotamente
    public static void crearCarpeta(DataInputStream dis) throws IOException {
        String nombre = dis.readUTF(); // Lectura nombre del directorio
        String directory = dis.readUTF(); // Lectura directorio destino

        File file = new File(directory + "\\" + nombre);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    // Eliminar carpetas local y remotamente
    public static void eliminarArchivo(DataInputStream dis, Socket cl) throws IOException {
        // Obtenemos el directorio
        File f = new File("");
        String absoluta = f.getAbsolutePath();
        String path = dis.readUTF(); // Lectura directorio padre
        String target = dis.readUTF(); // Lectura directirio destino
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
        DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); // Flujo de salida
        dos.writeUTF(output); // Envio mensaje de exito
        dos.flush();
        dos.close();
        dis.close();
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
    public static void renombrarArchivo(DataInputStream dis, Socket cl) throws IOException {
        File f = new File("");
        String absoluta = f.getAbsolutePath();
        String path = dis.readUTF(); // Lectura directorio a cambiar
        String rename = dis.readUTF(); // Lectura directorio nombre nuevo
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
        DataOutputStream dos = new DataOutputStream(cl.getOutputStream()); // Flujo de salida
        dos.writeUTF(output); // Envio mensaje de exito
        dos.flush();
        dos.close();
        dis.close();
    }
}
