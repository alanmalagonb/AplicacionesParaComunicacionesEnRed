package com.ipn.practica3redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class MulticastServer extends Thread{

    // Dirección multicast
    private InetAddress group = null;

    public void run(){
        try {
            group = InetAddress.getByName(Constants.MULTICAST_ADDRESS);
            while (true){
                send(   "--------------ANUNCIO--------------" + "\n" +
                        "MULTICAST ADDRESS: "+Constants.MULTICAST_ADDRESS+"\n" +
                        "MULTICAST PORT: "+Constants.MULTICAST_PORT+"\n" +
                        "DOWNLOAD PORT: "+Constants.DOWNLOAD_PORT+"\n" +
                        "SEARCH PORT: "+Constants.SEARCH_PORT+"\n"+
                        "----------------------------------");
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }catch (IOException e){
            System.exit(2);
        }
    }

    public Boolean send(String message){
        try {
            // Creamos el socket multicast
            MulticastSocket sendSocket = new MulticastSocket(Constants.MULTICAST_PORT);
            // Unimos el socket al grupo
            sendSocket.joinGroup(group);
            byte[] buffer = message.getBytes();
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, group, Constants.MULTICAST_PORT);
            // Enviamos el mensaje multicast
            sendSocket.send(packet);
            // Esperamos 5 segundos antes de enviar otro anuncio
            sendSocket.close();
            return true;
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
        try {
            MulticastServer multicastServer = new MulticastServer();
            multicastServer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
