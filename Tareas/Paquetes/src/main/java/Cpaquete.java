import java.net.*;
import java.io.*;
import java.util.Arrays;

public class Cpaquete {
    public static void main(String[] args) {
        try {
            //Inicializamos puerto, ip, tamaño, buffer para leer y el socket datagrama
            int pto = 6666;
            String dir = "127.0.0.1";
            InetAddress dst = InetAddress.getByName(dir);
            int tam = 10;
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            DatagramSocket cl = new DatagramSocket();
            while (true) {
                System.out.println("Ingresa el mensaje,  \"-\" para terminar");
                String msj = br.readLine();
                if (msj.compareToIgnoreCase("-") == 0) {
                    System.out.println("BYE");
                    br.close();
                    cl.close();
                    System.exit(0);
                } else {
                    //Si el arreglo de bytes es mayor al tamaño establecido, entonces...
                    int numPaquete = 0;
                    byte[] b = msj.getBytes();
                    if (b.length > tam) {
                        byte[] b_eco = new byte[b.length]; //Creamos un nuesvo arreglo de bytes de la misma longitud que el original
                        System.out.println("No bytes: " + b_eco.length); //Imprimimos el nuevo arreglo con la cantidad de Bytes totales
                        int tp = (int) (b.length / tam); // Cuantas veces el tamaño definido cabe en el tamaño del arreglo de bytes
                        //                  if(b.length%tam>0)
                        //                      tp=tp+1;
                        for (int j = 0; j < tp; j++) { //Desde el nuevo paquete 0 hasta acabar con los que sean (tp)
                            //byte[] tmp = new byte[tam];
                            byte[] tmp = Arrays.copyOfRange(b, j * tam, ((j * tam) + (tam))); //Copiamos el contenido a un array de bytes auxiliar 
                            System.out.println("Tam " + tmp.length); //Imprimos el tamaño del nuevo arreglo  
                            byte[] bFinal = (numPaquete +  new String(tmp)).getBytes(); //Nuevo arreglo = numpaquete + bytes del pedazo seleccionado
                            DatagramPacket p = new DatagramPacket(bFinal, bFinal.length, dst, pto);  //Declaramos un paquete de datagramas
                            cl.send(p);  // enviamos mediante el socket de datagramas el paquete de datagramas
                            
                            //Imprimimos el fragmento que se va a enviar de cuantos y su contenido
                            System.out.println("Enviando fragmento " + (j + 1) + " de " + tp + "\ndesde:" + (j * tam) + " hasta " + ((j * tam) + (tam)));
                            
                            DatagramPacket p1 = new DatagramPacket(new byte[tam], tam);
                            cl.receive(p1); //Recibimos el paquete de datagramas 
                            byte[] bp1 = p1.getData(); //Metemos el contenido en un array de bytes
                            for (int i = 0; i < tam; i++) { //verificamos que sean la cantidad de elementos correctos
                                System.out.println((j * tam) + i + "~ " + i);
                                b_eco[(j * tam) + i] = bp1[i]; //Almacenamos en el lugar indicado los bytes
                            }//for
                            numPaquete++;  //Incrementamos el numero de paquete
                        }//for
                        if (b.length % tam > 0) { //bytes sobrantes
                            //tp=tp+1;
                            int sobrantes = b.length % tam;
                            System.out.println("sobrantes:" + sobrantes);
                            System.out.println("b:" + b.length + "\nUltimo pedazo desde " + tp * tam + " hasta " + ((tp * tam) + sobrantes));
                            byte[] tmp = Arrays.copyOfRange(b, tp * tam, ((tp * tam) + sobrantes));
                            System.out.println("Tam " + tmp.length);
                            byte[] bFinal = (numPaquete + new String(tmp)).getBytes();
                            DatagramPacket p = new DatagramPacket(bFinal, bFinal.length, dst, pto);
                            cl.send(p);
                            DatagramPacket p1 = new DatagramPacket(new byte[tam], tam);
                            cl.receive(p1);
                            byte[] bp1 = p1.getData();
                            for (int i = 0; i < sobrantes; i++) {
                                System.out.println((tp * tam) + i + "->" + i);
                                b_eco[(tp * tam) + i] = bp1[i];
                            }//for
                        }//if

                        String eco = new String(b_eco);
                        System.out.println("Eco recibido: " + eco);
                    } else {
                        byte[] bFinal = (numPaquete + msj).getBytes();
                        DatagramPacket p = new DatagramPacket(bFinal, bFinal.length, dst, pto);
                        cl.send(p);
                        DatagramPacket p1 = new DatagramPacket(new byte[65535], 65535);
                        cl.receive(p1);
                        String eco = new String(p1.getData(), 0, p1.getLength());
                        System.out.println("Eco recibido: " + eco);
                    }//else
                }//else
            }//while
        } catch (Exception e) {
            e.printStackTrace();
        }//catch
    }//main
}
