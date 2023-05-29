package org.ipn;

import java.net.*;
import java.io.*;
import java.util.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class Manejador extends Thread {

    protected Socket cl;
    protected DataOutputStream dos;
    protected Mime mime;
    protected DataInputStream dis;

    public Manejador(Socket cl) throws Exception {
        this.cl = cl;
        this.dos = new DataOutputStream(this.cl.getOutputStream());
        this.mime = new Mime();
        this.dis = new DataInputStream(this.cl.getInputStream());
    }

    public void sendResource(String arg, int flag) {
        try {
            File f = new File(arg);
            String sb = "HTTP/1.1 200 OK\n";

            if (!f.exists()) {
                arg = "404.html"; // Recurso no encontrado
                sb = "HTTP/1.1 404 Not Found\n";
            } else if (f.isDirectory()) {
                arg = "403.html"; // Recurso privado
                sb = "HTTP/1.1 403 Acces Forbidden\n";
            }

            DataInputStream dis2 = new DataInputStream(new FileInputStream(arg));
            int tam = dis2.available();

            // Get extension
            int pos = arg.indexOf(".");
            String extension = arg.substring(pos + 1, arg.length());

            // Responses headers HTTP
            /***** HEAD *****/
            String metodo = "HEAD";
            sb = sb + "Date: " + new Date() + " \n"
                    + "Server: NetworkServer Server/1.0 \n"
                    + //Distintos tipos MIME para distintos tipos de archivos
                    "Content-Type: " + mime.get(extension) + " \n"
                    + "Content-Length: " + tam + "\n\n";

            dos.write(sb.getBytes());
            dos.flush();

            if (flag == 1) {
                metodo = "GET";
                // Respuesta GET, enviamos el archivo solicitado
                byte[] b = new byte[1024];
                long enviados = 0;
                int n = 0;

                while (enviados < tam) {
                    n = dis2.read(b);
                    dos.write(b, 0, n);
                    dos.flush();
                    enviados += n;
                }
            }
            System.out.println("Respuesta " + metodo + ": \n" + sb);
            dis2.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void deleteResource(String arg, String headers) {
        try {
            System.out.println(arg);
            File f = new File(arg);

            if (f.exists()) {
                if (f.delete()) {
                    System.out.println("..:: Archivo " + arg + " eliminado exitosamente ::..\n");

                    String deleteOK = headers
                            + "<html><head><meta charset='UTF-8'><title>202 OK Deleted Resource</title></head>"
                            + "<body><h1>202 Deleted Resource "+ arg +"</h1>"
                            + "</body></html>";

                    dos.write(deleteOK.getBytes());
                    dos.flush();
                    System.out.println("Respuesta DELETE: \n" + deleteOK);
                } else {
                    System.out.println("El archivo " + arg + " no pudo ser borrado\n");

                    String error404 = "HTTP/1.1 404 Not Found\n"
                            + "Date: " + new Date() + " \n"
                            + "Server: NetworkServer Server/1.0 \n"
                            + "Content-Type: text/html \n\n"
                            + "<html><head><meta charset='UTF-8'><title>404 Not found</title></head>"
                            + "<body><h1>404 Not found</h1>"
                            + "<p>Archivo " + arg + " no encontrado.</p>"
                            + "</body></html>";

                    dos.write(error404.getBytes());
                    dos.flush();
                    System.out.println("Respuesta DELETE - ERROR 404: \n" + error404);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }



    public String getResourceName(String line) {
        // Resource name
        int i = line.indexOf("/");
        int f = line.indexOf(" ", i);
        String resourceName = line.substring(i + 1, f);

        // Empty then show the index
        if (resourceName.compareTo("") == 0) {
            resourceName = "index.html";
        }

        return resourceName;
    }

    public String getParameters(String line, String headers, int flag) {
        String method = "POST";
        String request2 = line;

        if (flag== 0) {
            method = "GET";
            // Line: GET /?Nombre=&Direccion=&Telefono=&Comentarios= HTTP/1.1
            System.out.println(line);
            StringTokenizer tokens = new StringTokenizer(line, "?");
            String request = tokens.nextToken();
            request = tokens.nextToken();

            // Separate the parameters of "HTTP/1.1"
            StringTokenizer tokens2 = new StringTokenizer(request, " ");
            request2 = tokens2.nextToken();
        }

        System.out.println(request2);
        StringTokenizer paramsTokens = new StringTokenizer(request2, "&");

        String html = headers
                + "<html><head><meta charset='UTF-8'><title>Metodo " + method + "\n"
                + "</title></head><body style=\"background-color: #00a0fdd9\"\"><center><h2>Parametros obtenidos por medio de " + method + "</h2><br>\n"
                + "<table border='2'><tr><th>Parametro</th><th>Valor</th></tr>";

        // Loop through all parameters
        while (paramsTokens.hasMoreTokens()) {
            String parametros = paramsTokens.nextToken();

            StringTokenizer paramValue = new StringTokenizer(parametros, "=");
            String param = ""; //paramater name
            String value = ""; //parameter value

            // Check if there are empty parameters
            if (paramValue.hasMoreTokens()) {
                param = paramValue.nextToken();
            }

            if (paramValue.hasMoreTokens()) {
                value = paramValue.nextToken();
            }

            html = html + "<tr><td><b>" + param + "</b></td><td>" + value + "</td></tr>\n";
        }
        html = html + "</table></center></body></html>";
        return html;
    }

    @Override
    public void run() {

        // Cabeceras de respuestas HTTP
        String headers = "HTTP/1.1 200 OK\n"
                + "Date: " + new Date() + " \n"
                + "Server: Server/1.0 \n"
                + "Content-Type: text/html \n\n";

        try {
            String line = dis.readLine(); // Lee primera linea DEPRECIADO !!!!

            // Empty line
            if (line == null) {
                String vacia = "<html><head><title>Servidor WEB</title><body bgcolor='#AACCFF'>Linea Vacia</body></html>";
                dos.write(vacia.getBytes());
                dos.flush();
            }

            else{
                System.out.println("\n..:: Cliente Conectado desde " + cl.getInetAddress() + " ::..");
                System.out.println("Desde el puerto: " + cl.getPort());
                System.out.println("Datos: " + line + "\r\n\r\n");

                /***** GET *****/
                if (line.toUpperCase().startsWith("GET")) {
                    if (line.indexOf("?") == -1) {
                        // Request a file
                        String fileName = getResourceName(line);
                        // Flag HEAD = 0, GET = 1
                        sendResource(fileName, 1);
                    } else {
                        // Requesta a Form
                        // Bandera GET = 0, POST = 1
                        String respuesta = getParameters(line, headers, 0);
                        // GET response
                        dos.write(respuesta.getBytes());
                        dos.flush();
                        System.out.println("Respuesta GET: \n" + respuesta);
                    }
                }

                /***** HEAD *****/
                else if (line.toUpperCase().startsWith("HEAD")) {
                    if (line.indexOf("?") == -1) {
                        //  Request a file, only mime type and length is sent
                        String fileName = getResourceName(line);
                        // Flag HEAD = 0, GET = 1
                        sendResource(fileName, 0);
                    } else {
                        // HEAD response, HTTP headers
                        dos.write(headers.getBytes());
                        dos.flush();
                        System.out.println("Respuesta HEAD: \n" + headers);
                    }
                }

                /***** POST *****/
                else if (line.toUpperCase().startsWith("POST")) {
                    // Input stream
                    int tam = dis.available();
                    byte[] b = new byte[tam];

                    dis.read(b);
                    //Create a string with the bytes reads
                    String request = new String(b, 0, tam);

                    // Separate the parameters from the rest of the HTTP headers
                    String[] reqLineas = request.split("\n");
                    int ult = reqLineas.length - 1;

                    // Falg GET = 0, POST = 1
                    String respuesta = getParameters(reqLineas[ult], headers, 1);

                    // POST response
                    dos.write(respuesta.getBytes());
                    dos.flush();
                    System.out.println("Respuesta POST: \n" + respuesta);
                }

                /***** DELETE *****/
                else if (line.toUpperCase().startsWith("DELETE")) {
                    String fileName = getResourceName(line);
                    deleteResource(fileName, headers);
                }

                /***** PUT *****/
                else if (line.toUpperCase().startsWith("PUT")) {
                    String fileName = getResourceName(line);
                    String ruta = fileName;

                    try {
                        String contenido = "MÉTODO PUT : ACTUALIZAR O CREAR EL RECURSO";
                        File file = new File(ruta);
                        // The file is created if it does not exist
                        System.out.println("Respuesta PUT: \n" + headers);

                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        else{
                            headers = "HTTP/1.1 204 OK\n"
                                    + "Date: " + new Date() + " \n"
                                    + "Server: NetServer Server/1.0 \n"
                                    + "Content-Type: text/html \n\n";
                        }
                        FileWriter fw = new FileWriter(file);
                        BufferedWriter bw = new BufferedWriter(fw);
                        bw.write(contenido);
                        bw.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dos.write(headers.getBytes());
                    dos.flush();
                    System.out.println("Respuesta PUT: \n" + headers);
                }

                else {
                    //Methods not implemented on the server
                    String error501 = "HTTP/1.1 501 Not Implemented\n" +
                            "Date: " + new Date() + " \n" +
                            "Server: NetworkServer Server/1.0 \n" +
                            "Content-Type: text/html \n\n" +
                            "<html><head><meta charset='UTF-8'><title>Error 501</title></head>" +
                            "<body><h1>Error 501: No implementado.</h1>" +
                            "<p>El método HTTP o funcionalidad solicitada no está implementada en el servidor.</p>" +
                            "</body></html>";

                    dos.write(error501.getBytes());
                    dos.flush();
                    System.out.println("Respuesta ERROR 501: \n" + error501);
                }
            }
            dis.close();
            dos.close();
            cl.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
    class Mime {
        public static HashMap<String, String> mimeTypes;

        public Mime() {
            mimeTypes = new HashMap<>();
            mimeTypes.put("doc", "application/msword");
            mimeTypes.put("pdf", "application/pdf");
            mimeTypes.put("xls", "application/vnd.ms-excel");
            mimeTypes.put("ppt", "application/vnd.ms-powerpoint");
            mimeTypes.put("rar", "application/x-rar-compressed");
            mimeTypes.put("mp3", "audio/mpeg");
            mimeTypes.put("wav", "audio/x-wav");
            mimeTypes.put("gif", "image/gif");
            mimeTypes.put("jpg", "image/jpeg");
            mimeTypes.put("jpeg", "image/jpeg");
            mimeTypes.put("png", "image/png");
            mimeTypes.put("svg", "image/svg+xml");
            mimeTypes.put("html", "text/html");
            mimeTypes.put("htm", "text/html");
            mimeTypes.put("c", "text/plain");
            mimeTypes.put("txt", "text/plain");
            mimeTypes.put("java", "text/plain");
            mimeTypes.put("mp4", "video/mp4");
        }

        public String get(String extension) {
            if(mimeTypes.containsKey(extension))
                return mimeTypes.get(extension);
            else
                return "application/octet-stream";
        }

    }
}


