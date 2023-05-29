package com.ipn.practica3redes;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Nos unimos al grupo multicast leyendo todos los datagramas
 * generar una lista de servidores que se actualiza cada segundo
 */
public class MulticastClient extends Thread{

    private List<DataFromServer> servers = new ArrayList<>(); // Lista de servidores disponibles

    private InetAddress group = null; // Direccion del grupo multicast
    private final Files db; // Base de datos local

    // Constructor
    public MulticastClient(Files db){
        this.db = db;
        try {
            group = InetAddress.getByName(Constants.MULTICAST_ADDRESS);
        }catch (UnknownHostException e){
            e.printStackTrace();
            System.exit(1);
        }
    }

    // Método principal del hilo
    public void run(){
        try {
            // Crea un socket multicast
            MulticastSocket socket = new MulticastSocket(Constants.MULTICAST_PORT);
            // Se une al grupo multicast
            socket.joinGroup(group);
            // Ciclo infinito para recibir mensajes del grupo
            for(;;){
                byte []buffer = new byte[Constants.BUFFER_SIZE];
                DatagramPacket recv = new DatagramPacket(buffer, buffer.length);
                socket.receive(recv);
                byte []data = recv.getData();
                String msg = new String(data);
                msg = msg.trim();

                System.out.println(msg);

                // Regular expression patterns to match the ports
                String multicastPortPattern = "MULTICAST PORT: (\\d+)";
                String downloadPortPattern = "DOWNLOAD PORT: (\\d+)";
                String searchPortPattern = "SEARCH PORT: (\\d+)";

                // Create Pattern objects and compile the patterns
                Pattern multicastPortRegex = Pattern.compile(multicastPortPattern);
                Pattern downloadPortRegex = Pattern.compile(downloadPortPattern);
                Pattern searchPortRegex = Pattern.compile(searchPortPattern);

                // Create Matcher objects and match against the announcement string
                Matcher multicastPortMatcher = multicastPortRegex.matcher(msg);
                Matcher downloadPortMatcher = downloadPortRegex.matcher(msg);
                Matcher searchPortMatcher = searchPortRegex.matcher(msg);

                // Find the first occurrence of each pattern and extract the ports
                int multicastPort = extractPort(multicastPortMatcher);
                int downloadPort = extractPort(downloadPortMatcher);
                int searchPort = extractPort(searchPortMatcher);

                //System.out.println("Multicast Port: " + multicastPort);
                //System.out.println("Download Port: " + downloadPort);
                //System.out.println("Search Port: " + searchPort);

                // Verifica si el servidor ya está guardado
                DataFromServer curServer = new DataFromServer(recv.getAddress().toString().substring(1), 6, recv.getPort(),searchPort,downloadPort);
                // Agrega el servidor si no está en la lista
                servers = db.getServers();
                int pos = isInList(servers, curServer);
                if(pos == -1){
                    db.addServer(curServer);
                }else{
                    // Actualiza la temporalidad del servidor si ya está en la lista
                    servers.get(pos).setTemp(6);
                    db.setServers(servers);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
            System.exit(2);
        }
    }

    private int extractPort(Matcher matcher) {
        if (matcher.find()) {
            String portString = matcher.group(1);
            return Integer.parseInt(portString);
        }
        return 0;
    }

    // Verifica si un servidor ya está en la lista de servidores
    public int isInList(List<DataFromServer>list, DataFromServer dfs){
        int i;
        for(i = 0; i < list.size(); i++){
            if(list.get(i).getAddress().equals(dfs.getAddress())){
                return i;
            }
        }
        return -1;
    }

}

