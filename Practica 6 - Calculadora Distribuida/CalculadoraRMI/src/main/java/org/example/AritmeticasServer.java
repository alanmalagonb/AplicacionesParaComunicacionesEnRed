package org.example;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class AritmeticasServer extends Thread implements AritmeticasInterface {

    public void run(){
        try {
            java.rmi.registry.LocateRegistry.createRegistry(Constants.RMIA_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //java.rmi.registry.LocateRegistry.createRegistry(Constants.RMI_PORT);
            AritmeticasServer obj = new AritmeticasServer();
            AritmeticasInterface stub = (AritmeticasInterface) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry(Constants.RMIA_PORT);
            registry.bind("Aritmeticas", stub);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public double suma(double a, double b) throws RemoteException {
        return a + b;
    }

    public double resta(double a, double b) throws RemoteException {
        return a - b;
    }

    public double multiplicacion(double a, double b) throws RemoteException {
        return a * b;
    }

    public double division(double a, double b) throws  RemoteException{
        return a / b;
    }

    @Override
    public double exponenciacion(double a, double b) throws RemoteException {
        return Math.pow(a,b);
    }

    @Override
    public double raiz(double a) throws RemoteException {
        return Math.sqrt(a);
    }
}