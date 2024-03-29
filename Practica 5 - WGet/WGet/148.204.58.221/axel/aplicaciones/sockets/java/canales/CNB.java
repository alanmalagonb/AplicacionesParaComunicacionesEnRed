import java.nio.channels.*;
import java.nio.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.util.Iterator;

/**
 *
 * @author axel
 */


public class CNB {
   public static void main(String[] args){
       try{
           SocketChannel cl = SocketChannel.open();
           cl.configureBlocking(false);
           Selector sel = Selector.open();
           cl.register(sel, SelectionKey.OP_CONNECT);
           cl.connect(new InetSocketAddress("::1",9999));
           while(true){
               sel.select();
               Iterator<SelectionKey> it = sel.selectedKeys().iterator();
               while(it.hasNext()){
                   SelectionKey k = (SelectionKey)it.next();
                   it.remove();
                   if(k.isConnectable()){
                       SocketChannel ch = (SocketChannel)k.channel();
                       if(ch.isConnectionPending()){
                           try{
                               ch.finishConnect();
                           }catch(Exception e){
                               e.printStackTrace();
                           }//catch
                       }//if
                       System.out.println("Conexion establecida..");
                      // ch.configureBlocking(false);
                       ch.register(sel, SelectionKey.OP_READ|SelectionKey.OP_WRITE);
                       continue;
                   }//if
                   if (k.isWritable()){
                       Objeto o1 = new Objeto("Esta es una cadena",5.3f,1);
                       ByteArrayOutputStream baos = new ByteArrayOutputStream();
                       ObjectOutputStream oos = new ObjectOutputStream(baos);
                       oos.writeObject(o1);
                       oos.flush();
                       ByteBuffer b = ByteBuffer.wrap(baos.toByteArray());
                       SocketChannel sc = (SocketChannel)k.channel();
                       sc.write(b);
                       continue;

                   } else if(k.isReadable()){
                       ByteBuffer b = ByteBuffer.allocate(2000);
                       b.clear();
                       SocketChannel ch = (SocketChannel)k.channel();
                       ch.read(b);
                       b.flip();
                       if(b.hasArray()){
                           ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(b.array()));
                           Objeto o = (Objeto)ois.readObject();
                           System.out.println("Objeto recibido..");
                           System.out.println("x:"+o.x+" y:"+o.y+" z:"+o.z);
                           k.cancel();
                           System.exit(0);
                       }//if
                   }//else
               }//while
           }//while

       }catch(Exception e){
           e.printStackTrace();
       }//catch
   }//main
}
