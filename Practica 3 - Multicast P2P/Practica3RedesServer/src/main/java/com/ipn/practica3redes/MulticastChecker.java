package com.ipn.practica3redes;

import java.net.MulticastSocket;

public class MulticastChecker {
    public static void main(String[] args) {
        try {
            MulticastSocket socket = new MulticastSocket();
            System.out.println("Multicast es compatible en esta máquina");
        } catch (Exception e) {
            System.out.println("Multicast no es compatible en esta máquina");
        }
    }
}
