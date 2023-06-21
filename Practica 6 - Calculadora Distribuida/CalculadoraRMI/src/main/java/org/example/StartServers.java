package org.example;

public class StartServers extends Thread{

    AritmeticasServer aritmeticasServer = new AritmeticasServer();
    TrigonometricasServer trigonometricasServer = new TrigonometricasServer();
    public StartServers(){
        System.out.println(Constants.SERVER_INIT_MSG);
        aritmeticasServer.start();
        trigonometricasServer.start();
    }

    public static void main(String[] args) {
        try {
            StartServers startServers = new StartServers();
            startServers.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
