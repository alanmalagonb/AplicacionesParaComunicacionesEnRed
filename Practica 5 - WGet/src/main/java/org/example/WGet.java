package org.example;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
class wget2 implements Runnable {

    static boolean isAddressResource(String s) {    //  Verificar link a descargar
        for(int i = s.length() - 1; i > 0; i--){
            if(s.charAt(i) == '/')
                return false;
            if(s.charAt(i) == '.')
                return true;
        }
        return false;
    }


    static String previousAddress(String s) {       //  Obtener direccion anterior
        for (int i = s.length() - 2; i > 0; i--)
            if (s.charAt(i) == '/')
                return s.substring(0, i + 1);
        return "";
    }

    static String newPath(String[] s) {             //  Construir nueva ruta para las descargas
        String aux = "";
        for (int i = 2; i < s.length; ++i) {
            aux += s[i] + "/";
            File file = new File(aux);
            file.mkdirs();
        }
        return aux;
    }

    static String makeLink(String s, String a){     //  Hacer un link nuevo auxiliar para ingresar
        if(s.contains(a) || s.contains("/" + a))    //  Posible error, complemento anterior
            return "";
        if(a.charAt(0) == '/')
            return s + a.substring(1);
        else
            return s + a;
    }

    static boolean linkViable(String s) { //  Verificar que se un link disponible para ir
        if(s.contains("//"))    return false;
        if(s.contains("@"))     return false;
        if(s.contains("#"))     return false;
        if(s.contains("\\"))    return false;
        if(s.charAt(0) == '?')
            return false;
        return true;
    }

    static boolean linkViable(String s, String a) { //  Verificar que se un link disponible para ir
        if(s.contains("//"))    return false;
        if(s.contains("@"))     return false;
        if(s.contains("#"))     return false;
        if(s.contains("\\"))    return false;
        if(a.contains(s) || a.contains("/" + s))
            return false;
        if(s.charAt(0) == '#' || s.charAt(0) == '?')
            return false;
        return true;
    }

    static boolean isResource(String s) {           //  Verificar que el nombre corresponda a un recurso
        if (s.contains("."))
            return true;
        return false;
    }

    static String onlyName(String s){               //  Obtener el nombre correctamente
        if(s.contains("/")){
            if(s.charAt(0) == '/' && s.charAt(s.length()-1) == '/')
                return s.substring(1, s.length()-1);
            if(s.charAt(0) == '/')
                return s.substring(1);
            if(s.charAt(s.length()-1) == '/')
                return s.substring(0, s.length()-1);
        }
        return s;
    }

    static int nivels(String s) {
        String[] aux = s.split("/");
        int count = 0;
        for(String a: aux){
            if(!a.equals(""))
                count++;
        }
        return count;
    }

    static boolean back_ahead(String s, String a) {
        if(a.contains(s) || a.contains("/" + s))
            return true;
        return false;
    }

    static String complementPath(int nivel){
        String replaced = "";
        for(int i = 0; i < nivel; ++i){
            replaced += "../";
        }
        return replaced;
    }

    static String path_downloadImg(String s, String img, int n){
        String[] aux = s.split("/");
        String[] aux1 = img.split("/");
        int count = nivels(s) - n;
        String filePath = "";

        for(String a: aux){
            if(!a.equals("") && count > 0){
                filePath += "/" + a;
                --count;
            }
        }

        for(String a: aux1){
            if(!a.equals(""))
                filePath += "/"+ a;
        }

        return filePath;
    }

    static void downloadImg(String s, String b){
        String[] aux = s.split("/");
        String path = "";
        int n = aux.length;

        for(int i = 0; i < n - 1; ++i){
            if(!aux[i].equals("")){
                File f = new File(path + "/" + aux[i]);
                path +=  aux[i] + "/";
                f.mkdirs();
            }
        }

        downloadResorce(b, path, aux[n - 1]);
    }

    static void downloadResorce(String url, String path, String filename) {
        System.out.println("Direccion: " + url);

        try {
            BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
            File f = new File(path + "/" + filename);
            FileOutputStream fos = new FileOutputStream(f);

            byte dataBuffer[] = new byte[1024];
            int bytesRead;

            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fos.write(dataBuffer, 0, bytesRead);
            }

            fos.close();
            System.out.println("Descargado: " + filename + "\n");
        } catch (MalformedURLException e) {
            System.out.println("Error, enlace no disponible para descarga");
        } catch (IOException e) {
            System.out.println("Error, enlace no disponible para descarga");
        }
    }

    static void download_write(String url, String urlserver, String path, int nivel) {
        try {
            // Descargar el contenido del index
            int c;
            String html = "";
            Set<String> links = new HashSet<>();
            InputStream in = new URL(url).openStream();
            while ((c = in.read()) != -1) {
                html += (char) c;
            }
            in.close();

            // Reescribir las direcciones en los links validos del servidor
            Pattern p = Pattern.compile("href=\"(.*?)\"");
            Matcher m = p.matcher(html);

            while (m.find()) {
                String href = m.group();
                int startIndex = href.indexOf("href=") + 6;
                String hrefTag = href.substring(startIndex, href.length() - 1);

                // Comprobar que el link no haya sea valido y reescribirlo
                if (linkViable(hrefTag) && !isResource(hrefTag) && !links.contains(hrefTag)) {
                    // Determinar si es un enlace para avanzar o retroceder en el sitio
                    if (back_ahead(hrefTag, url)) { // Retroceder
                        int aux = nivel - nivels(hrefTag);
                        String auxString = complementPath(aux);
                        html = html.replace(href, href.replace(hrefTag, auxString + "index.html"));
                    } else { // Avanzar
                        if (hrefTag.charAt(hrefTag.length() - 1) == '/') {
                            html = html.replace(href, "href=" + hrefTag.replace(hrefTag, hrefTag + "index.html"));
                        } else {
                            html = html.replace(href, "href=" + hrefTag.replace(hrefTag, hrefTag + "/index.html"));
                        }
                    }
                    links.add(hrefTag); // Marcar como visitado
                }

            }

            // Reescribir las direcciones y descargar imagenes
            p = Pattern.compile("<img [^>]*src=[\\\"']([^\\\"^']*)");
            m = p.matcher(html);

            while (m.find()) {
                String src = m.group();
                int startIndex = src.indexOf("src=") + 5;
                String srcTag = src.substring(startIndex, src.length());

                if (!links.contains(srcTag)) {
                    String auxString = complementPath(nivel);
                    String pathImg = path_downloadImg(path, srcTag, nivel);
                    downloadImg(pathImg, urlserver + srcTag);
                    if (srcTag.charAt(0) == '/') {
                        html = html.replace(srcTag, auxString.substring(0, (nivel * 3) - 1) + srcTag);
                    } else {
                        html = html.replace(srcTag, auxString + srcTag);
                    }
                    links.add(srcTag);
                }

            }

            FileWriter myWriter = new FileWriter(path + "/index.html");
            myWriter.write(html);
            myWriter.close();
        } catch (IOException e) {
            System.out.println("Error, enlace no disponible para descarga");
        } catch (Throwable err) {
            System.out.println("Error, enlace no disponible para descarga");
        }

    }

    // static void downloadResorces(Set<String> links, String nameHMTL, String
    // actual, String previousURL, String path, String space) {
    static void downloadResorces(Set<String> links, String servername, String actual, String previous, String path,
                                 int nivel, String space) {
        try {
            // Recursos necesario para recorrer el HTML
            final int n = nivel + 1;
            Reader r = null;
            URL pageURL = new URL(actual);
            InputStream in = pageURL.openStream();

            // Descargar el HTML del index
            download_write(actual, servername, path, nivel);
            // downloadResorce(actual, path, nameHMTL + ".html");
            // downloadResorce(actual, path, "index.html");

            r = new InputStreamReader(in);
            ParserDelegator hp = new ParserDelegator();

            hp.parse(r, new HTMLEditorKit.ParserCallback() {
                public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
                    if (t == HTML.Tag.A) {
                        Enumeration attrNames = a.getAttributeNames();

                        // Comprobar si quedan linkss por recorrer
                        while (attrNames.hasMoreElements()) {
                            Object key = attrNames.nextElement();
                            String actualName = (String) a.getAttribute(key);
                            String auxLink = makeLink(actual, actualName);

                            // Comprobar links
                            if ("href".equals(key.toString()) && linkViable(actualName, previous)
                                    && !links.contains(auxLink)) {
                                links.add(auxLink);
                                System.out.println(actualName);
                                System.out.println(space + auxLink);

                                // Descargar recurso o explorar un nuevo links
                                if (isResource(actualName)) {
                                    downloadResorce(auxLink, path, onlyName(actualName));
                                } else {
                                    System.out.println("Carpeta: " + actualName);
                                    File f = new File(path, actualName);
                                    f.mkdir();

                                    // downloadResorces(links, actualName, makeLink(actual, actualName), actual,
                                    // path + actualName, space + "----|");
                                    downloadResorces(links, servername, makeLink(actual, actualName), actual,
                                            path + actualName, n, space + "----|");

                                }
                            }
                        }
                    }
                }
            }, true);

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    static void getData_Download(String s, String[] parts) {
        String previous = previousAddress(s); // Direccion URL anterior
        String servername = parts[0] + "//" + parts[2] + "/"; // Direccion URL servidor
        String path = newPath(parts); // Nueva direccion de la descarga
        int nivel = nivels(path) - 1;

        System.out.println("--  --  --  --  --  --  --  --  --  --  --  --  --");
        System.out.println("Links en donde se descargaran recursos: " + s);
        System.out.println("Dirección anterior: " + previous);
        System.out.println("Dirección del servidor: " + servername);
        System.out.println("Dirección nueva: " + path);
        System.out.println("Profundidad : " + (nivel - 1));
        System.out.println("--  --  --  --  --  --  --  --  --  --  --  --  --");

        // Guardar datos actuales que ya no seran revisados
        Set<String> links = new HashSet<>();
        links.add(s);
        links.add(previous);
        links.add(servername);

        // downloadResorces(links, "index", s, previousURL, path, "----|");
        downloadResorces(links, servername, s, previous, path, nivel, "----|");

    }

    @Override
    public void run() {
        // Dirección URL para descargar
        //String dirname = "https://logos-marques.com/wp-content/uploads/2021/03/Marvel-Logo-2048x1158.png";
        //String dirname = "http://148.204.58.221/axel/aplicaciones/sockets/java/canales/";
        //String dirname = "http://148.204.58.221/axel/aplicaciones/22-2/";

        String dirname = "";
        boolean resource;
        System.out.println ("NOTA -r: modo recursivo");
        System.out.println ("Introduzca la URL: ");
        Scanner input = new Scanner(System.in);
        dirname = input.nextLine();
        resource = (dirname.substring(0, 2).equals("-r"));

        if (!resource) {
            // Descargar un solo recurso en la ubicación actual
            String[] partsURL = dirname.split("/");
            downloadResorce(dirname, System.getProperty("user.dir"), partsURL[partsURL.length - 1]);
        } else {
            // Descargar todos los recursos de un link
            String[] parts = dirname.split(" ");
            System.out.println(parts[0]);
            System.out.println(parts[1]);
            String[] partsURL = parts[1].split("/");
            getData_Download(parts[1], partsURL);
        }
    }

}

public class WGet {

    public static void main(String[] args) {
        int tamPool;
        Scanner input = new Scanner(System.in);
        System.out.println("Introduzca el tamaño del pool de hilos: ");
        tamPool = input.nextInt();
        Runnable g = new wget2();
        ExecutorService pool = Executors.newFixedThreadPool(tamPool);
        pool.execute(g);
        pool.shutdown();
    }
}
