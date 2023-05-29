package com.ipn.buscaminas;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.*;
import java.util.List;

public class Server {
    public static void main(String args[]) {
        try {
            String host = "127.0.0.1";
            int port = 8009;
            //Creacion del socket
            DatagramSocket s = new DatagramSocket(port);
            InetAddress address = InetAddress.getByName(host);
            s.setReuseAddress(true);
            s.setBroadcast(true);
            System.out.println("Servidor iniciado, esperando..");

            while (true) {
                String nickName = "";
                String time = "";

                DatagramPacket p = new DatagramPacket(new byte[65535], 65535);
                s.receive(p);
                s.connect(p.getAddress(), p.getPort());
                System.out.println("\nInet address " + s.getInetAddress());
                ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(p.getData()));
                Objeto o = (Objeto) ois.readObject();
                nickName = o.getStr();
                System.out.println("Nickname cliente: " + o.getStr());

                Objeto o1 = new Objeto("Seleccione un nivel \n Principiante\n Intermedio\n Experto");
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(o1);
                oos.flush();
                byte[] b = baos.toByteArray();
                DatagramPacket p2 = new DatagramPacket(b, b.length, p.getAddress(), p.getPort());
                s.send(p2);
                System.out.println("Niveles disponibles enviados.");

                s.receive(p);
                Objeto o2 = (Objeto) ois.readObject();
                int nivel = Integer.parseInt(o2.getStr());
                int casillasW =  9;
                int casillasH =  9;
                int minas = 10;
                Objeto o5 = null;
                switch (nivel) {
                    case 1:
                        casillasW = 9;
                        casillasH = 9;
                        minas = 10;
                        break;
                    case 2:
                        casillasW = 16;
                        casillasH = 16;
                        minas = 40;
                        break;
                    case 3:
                        casillasW = 16;
                        casillasH = 30;
                        minas = 99;
                        break;
                }
                o5 = new Objeto(casillasW+","+casillasH+","+minas);

                System.out.println("Configuración de juego enviada: \n" +
                        "Casillas Ancho: " + casillasW +"\n" +
                        "Casillas Alto: "+ casillasH + "\n" +
                        "Minas: " + casillasH);
                oos.writeObject(o5);
                oos.flush();
                b = baos.toByteArray();
                DatagramPacket p5 = new DatagramPacket(b, b.length, p.getAddress(), p.getPort());
                s.send(p5);

                Objeto o3 = new Objeto("Inicio del buscaminas...");
                oos.writeObject(o3);
                oos.flush();
                b = baos.toByteArray();
                DatagramPacket p3 = new DatagramPacket(b, b.length, p.getAddress(), p.getPort());
                s.send(p3);
                System.out.println("¡Inicio del juego!");

                s.receive(p);
                Objeto o8 = (Objeto) ois.readObject();
                String points = o8.getStr();
                System.out.println("Puntaje: " + points);

                s.receive(p);
                Objeto o4 = (Objeto) ois.readObject();
                time = o4.getStr();
                System.out.println("Tiempo: " + time + " seg");

                s.receive(p);
                Objeto o6 = (Objeto) ois.readObject();
                boolean gano = Boolean.parseBoolean(o6.getStr());
                System.out.println(" gano "+gano);

                BufferedWriter bw = null;
                FileWriter fw = null;
                File file = new File("Puntajes.txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                fw = new FileWriter(file.getAbsoluteFile(), true);
                bw = new BufferedWriter(fw);


                String data = "+ Jugador: " + nickName + "    Direccion:  " + p.getAddress() + "   Puerto: " + p.getPort()
                        + "    Nivel:  " + nivel + "    Puntos:  " + points
                        + "    Tiempo:   " + time + " seg\n";

                bw.write(data);
                System.out.println("Información agregada: " + file.getAbsolutePath());
                bw.close();
                fw.close();

                if(gano) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    List<Record> records = objectMapper.readValue(new File("records.json"), new TypeReference<List<Record>>() {
                    });
                    Record record = new Record(nickName, p.getAddress().toString(), p.getPort(), nivel, Integer.parseInt(points), time);
                    records.add(record);
                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("records.json"), records);
                }

                s.disconnect();
            }//while
        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }
}
