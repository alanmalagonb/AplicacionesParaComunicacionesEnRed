package org.example;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class TrigonometricasServer extends Thread implements TrigonometricasInterface {

    public void run(){
        try {
            java.rmi.registry.LocateRegistry.createRegistry(Constants.RMIT_PORT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //java.rmi.registry.LocateRegistry.createRegistry(Constants.RMI_PORT);
            TrigonometricasServer obj = new TrigonometricasServer();
            TrigonometricasInterface stub = (TrigonometricasInterface) UnicastRemoteObject.exportObject(obj, 0);
            Registry registry = LocateRegistry.getRegistry(Constants.RMIT_PORT);
            registry.bind("Trigonometricas", stub);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public double cos(double angle) throws RemoteException {
        return Math.cos(Math.toRadians(angle));
    }

    public double sin(double angle) throws RemoteException {
        return Math.sin(Math.toRadians(angle));
    }

    public double tan(double angle) throws RemoteException {
        return Math.tan(Math.toRadians(angle));
    }

    @Override
    public double cot(double angle) throws RemoteException {
        return 1/Math.tan(Math.toRadians(angle));
    }

    @Override
    public double csc(double angle) throws RemoteException {
        return 1/Math.sin(Math.toRadians(angle));
    }

    @Override
    public double sec(double angle) throws RemoteException {
        return 1/Math.cos(Math.toRadians(angle));
    }

    @Override
    public double log(double angle) throws RemoteException {
        return Math.log(angle);
    }
}