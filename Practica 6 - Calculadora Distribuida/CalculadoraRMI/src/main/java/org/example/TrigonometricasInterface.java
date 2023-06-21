package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TrigonometricasInterface extends Remote {

    double cos(double angle) throws RemoteException;

    double sin(double angle) throws RemoteException;

    double tan(double angle) throws RemoteException;
    double cot(double angle) throws RemoteException;
    double csc(double angle) throws RemoteException;
    double sec(double angle) throws RemoteException;
    double log(double angle) throws RemoteException;
}
