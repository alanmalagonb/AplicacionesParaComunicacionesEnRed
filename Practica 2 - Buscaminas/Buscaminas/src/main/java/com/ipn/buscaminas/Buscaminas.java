package com.ipn.buscaminas;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Buscaminas extends Application {

    static String nickName;
    static String nivel = "Principiante";

    static int niveln = 1;
    static int casillasW =  9;
    static int casillasH =  9;
    static int minas = 10;

    static String[] configuracion;
    static int points = 0;
    static int finalPoints = 0;


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Buscaminas.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Inicio");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static void iniciarJuego() {
        final String host = "127.0.0.1";
        final int port = 8009;


        try{
            InetAddress dst = InetAddress.getByName(host);
            DatagramSocket cl = new DatagramSocket();
            System.out.println("Cliente iniciado "+host+" : "+port);

            Objeto obj = new Objeto(nickName);
            ByteArrayOutputStream baos= new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(obj);
            oos.flush();

            byte[] b = baos.toByteArray();
            DatagramPacket p = new DatagramPacket(b,b.length,dst,port);
            cl.send(p);
            System.out.println("Nickname del jugador enviado: "+obj.getStr());

            System.out.println("Esperando recibir nivel...");
            DatagramPacket p1 = new DatagramPacket(new byte[65535],65535);
            cl.receive(p1);
            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(p1.getData()));
            Objeto o1 = (Objeto)ois.readObject();
            System.out.println(o1.getStr());

            int nvl = 1;
            niveln = nvl;
            if(nivel.equalsIgnoreCase("principiante")) {
                nvl = 1;
            }
            else if(nivel.equalsIgnoreCase("intermedio")) {
                nvl = 2;
            }
            else if (nivel.equalsIgnoreCase("experto")) {
                nvl = 3;
            }
            Objeto obj1 = new Objeto(nvl+"");
            oos.writeObject(obj1);
            oos.flush();
            b = baos.toByteArray();
            DatagramPacket p2 = new DatagramPacket(b,b.length,dst,port);
            cl.send(p2);
            System.out.println("Nivel enviado: "+nivel);

            cl.receive(p1);
            configuracion = ((Objeto)ois.readObject()).getStr().split(",");
            System.out.println(configuracion);
            casillasW = Integer.parseInt(configuracion[0]);
            casillasH = Integer.parseInt(configuracion[1]);
            minas = Integer.parseInt(configuracion[2]);

            cl.receive(p1);
            Objeto obj2 = (Objeto)ois.readObject();
            System.out.println(obj2.getStr());

            long startTime = System.currentTimeMillis();
                    FXMLLoader fxmlLoader = new FXMLLoader(Buscaminas.class.getResource("buscaminas.fxml"));
                    Stage stage = new Stage();
                    Scene scene = new Scene(fxmlLoader.load());
                    stage.setTitle("Buscaminas");
                    stage.setScene(scene);
                    stage.showAndWait();
            long stopTime = System.currentTimeMillis();
            double elapsedTime = (stopTime - startTime)/1000.0;
            System.out.println("Marca de tiempo: " + elapsedTime+ " seg");

            Objeto obj4 = new Objeto(Buscaminas.points+"");
            oos.writeObject(obj4);
            oos.flush();
            b = baos.toByteArray();
            DatagramPacket p4 = new DatagramPacket(b,b.length,dst,port);
            cl.send(p4);
            System.out.println("Puntaje enviada al servidor");

            Objeto obj3 = new Objeto(elapsedTime+"");
            oos.writeObject(obj3);
            oos.flush();
            b = baos.toByteArray();
            DatagramPacket p3 = new DatagramPacket(b,b.length,dst,port);
            cl.send(p3);
            System.out.println("Marca de tiempo enviada al servidor");

            Objeto obj5 = new Objeto(Boolean.toString(points==finalPoints));
            oos.writeObject(obj5);
            oos.flush();
            b = baos.toByteArray();
            DatagramPacket p5 = new DatagramPacket(b,b.length,dst,port);
            cl.send(p5);
            System.out.println("Si es ganador enviado");

            cl.close();
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }
}