package com.ipn.practica3redes;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**Stream de server que se usará para enviar los archivos solicitados*/
public class DownloadServer extends Thread{
    private ServerSocket s;
    private Socket cl;

    public void run(){
        startServer();
        sendFile();
    }

    public void startServer(){
        try {
            s = new ServerSocket(Constants.DOWNLOAD_PORT);
            s.setReuseAddress(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void sendFile(){
        while(true){
            try {
                cl = s.accept();
                System.out.println(String.format(Constants.CLIENT_CONNECTION,cl.getInetAddress(), cl.getPort()));

                /**Leyendo entrada*/
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                String name = dis.readUTF();
                File f = new File(name);
                String fileName = f.getName();
                long tam = f.length();
                String path = f.getAbsolutePath();
                System.out.println(Constants.SENDING_FILE);
                System.out.println(String.format(Constants.FILE_SIZE,fileName, tam));
                DataOutputStream dos = new DataOutputStream (cl.getOutputStream());

                DataInputStream disFile = new DataInputStream(new FileInputStream(path));

                /**Enviando archivo*/
                dos.writeUTF(fileName);
                dos.flush();
                dos.writeLong(tam);
                byte[] b = new byte[Constants.FILE_BUFFER_SIZE];
                long sent = 0;
                int n;
                while(sent  <tam){
                    n = disFile.read(b);
                    dos.write(b, 0, n);
                    dos.flush();
                    sent += n;
                }
                disFile.close();
                dos.close();
                cl.close();
                System.out.println(Constants.FILE_SENT_SUCCESSFULLY);
                dis.close();
                cl.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            DownloadServer dServer = new DownloadServer();
            dServer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
