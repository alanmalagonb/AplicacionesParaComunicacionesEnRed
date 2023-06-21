package org.example;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AritmeticasInterface extends Remote {

    double suma(double a, double b) throws RemoteException;

    double resta(double a, double b) throws RemoteException;

    double multiplicacion(double a, double b) throws RemoteException;

    double division(double a, double b) throws RemoteException;

    double exponenciacion(double a, double b) throws RemoteException;
    double raiz(double a) throws RemoteException;
}