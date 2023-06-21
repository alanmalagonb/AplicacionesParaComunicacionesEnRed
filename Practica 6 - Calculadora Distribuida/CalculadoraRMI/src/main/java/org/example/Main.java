package org.example;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        RMIClient rmiClient = new RMIClient();
        rmiClient.start();

        Scanner scanner = new Scanner(System.in);
        String expresion;

        while (true) {
            System.out.print("Ingrese una expresión (o escriba 'salir' para finalizar): ");
            expresion = scanner.nextLine();

            if (expresion.equalsIgnoreCase("salir")) {
                break;
            }

            rmiClient.calcular(expresion);
        }

        scanner.close();
    }

    // (2^3)*(sin(90))
}
