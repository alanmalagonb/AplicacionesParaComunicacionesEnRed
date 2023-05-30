package com.ipn.practica3redes;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class SearchServer extends Thread{

    File directory = new File(Constants.FILES_PATH);
    private ServerSocket s;
    private Socket cl;

    public void run(){
        startServer();
        searchFile();
    }

    public void startServer() {
        try{
            s = new ServerSocket(Constants.SEARCH_PORT);
            s.setReuseAddress(true);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void searchFile() {
        while (true){
            try {
                cl=s.accept();
                System.out.println(String.format(Constants.CLIENT_CONNECTION,cl.getInetAddress(),cl.getPort()));

                // Leemos la entrada
                DataInputStream dis = new DataInputStream(cl.getInputStream());
                String name = dis.readUTF();
                File f = new File(name);
                String fileName = f.getName();



                    FoundFile result = new FoundFile();
                    result.setFileName("unknown");

                    File dir = directory;
                    File[] files = dir.listFiles();
                    for(File file : files)
                        if(file.isFile())
                            if(file.getName().equals(fileName) ){
                                result.setFileName(file.getName());
                                result.setPath(file.getAbsolutePath());
                                result.setMd5(getMD5(file.getAbsolutePath()));
                            }

                    ObjectOutputStream oos = new ObjectOutputStream(cl.getOutputStream());
                    oos.writeObject(result);
                    oos.flush();
                    oos.close();


                dis.close();
                cl.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public String getMD5(String path){
        try{
            MD5Checksum md5 = new MD5Checksum();
            return md5.getMD5Checksum(path);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        try {
            SearchServer sServer = new SearchServer();
            sServer.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
