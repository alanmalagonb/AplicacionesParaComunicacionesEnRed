package org.ipn;

import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WebServer {

    public static void main(String[] args) {
        int pto, tamPool;

        try {
            Scanner sc = new Scanner(System.in);
            System.out.print("Indique el puerto: ");
            pto = sc.nextInt();
            System.out.print("Indique el tamaño del pool de conexiones: ");
            tamPool = sc.nextInt();


            ExecutorService pool = Executors.newFixedThreadPool(tamPool);
            System.out.println("\n\n..:: Iniciando Servidor ::.. \nPool de Conexiones = " + tamPool);

            ServerSocket s = new ServerSocket(pto);
            System.out.println("Servidor iniciado: http://localhost:" + pto + "/ --- OK");
            System.out.println("Esperando cliente....");

            for( ; ; ) {
                Socket cl = s.accept();
                Manejador manejador = new Manejador(cl);
                pool.execute(manejador);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
