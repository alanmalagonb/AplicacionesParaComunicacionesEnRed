import java.net.*;
import java.io.*;

public class Spaquete {
    public static void main(String[] args){
        try{
            int pto=6666;
            String msj="";
            DatagramSocket s = new DatagramSocket(pto);
            s.setReuseAddress(true);
            // s.setBroadcast(true);
            System.out.println("Servidor iniciado... esperando datagramas..");
            for(;;){
                byte[] b = new byte[65535];
                DatagramPacket p = new DatagramPacket(b,b.length);
                s.receive(p);
                msj = new String(p.getData(),0,p.getLength());
                System.out.println("Se ha recibido datagrama desde "+p.getAddress()+":"+p.getPort()+" con el mensaje:"+msj.substring(1, msj.length())+
                        "\nPaquete número: " + msj.substring(0,1)+
                        "\tTamaño total: " + msj.length() + ", \tTamaño del mensaje: "+(msj.length()-1));
                DatagramPacket nuevoP = new DatagramPacket(msj.substring(1, msj.length()).getBytes(),msj.length()-1,p.getAddress(),p.getPort());
                s.send(nuevoP);
            }//for

        }catch(Exception e){
            e.printStackTrace();
        }//catch

    }//main
}
