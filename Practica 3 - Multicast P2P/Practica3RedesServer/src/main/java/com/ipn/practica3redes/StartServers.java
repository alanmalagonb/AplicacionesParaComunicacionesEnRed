package com.ipn.practica3redes;

// Se define una clase llamada StartServers que extiende de la clase Thread, indicando que esta clase será un hilo.
public class StartServers extends Thread{

    // Se crean tres objetos de los servidores: DownloadServer, SearchServer y MulticastServer.
    // Estos objetos se inicializan sin ningún parámetro.
    DownloadServer dServer = new DownloadServer();
    SearchServer sServer = new SearchServer();
    MulticastServer mServer = new MulticastServer();

    // Se define un constructor para la clase StartServers.
    // Dentro del constructor se inicia cada uno de los servidores mediante el método start(),
    // lo que iniciará la ejecución de los hilos correspondientes.
    public StartServers(){
        System.out.println(Constants.SERVER_INIT_MSG);
        mServer.start();
        sServer.start();
        dServer.start();
    }

    // Se define el método main() que es el punto de entrada del programa.
    // Dentro de este método, se crea una instancia de la clase StartServers y
    // se llama al método start() para iniciar la ejecución del hilo.
    // Si ocurre alguna excepción, se imprimirá el mensaje de error correspondiente mediante el método printStackTrace().
    public static void main(String[] args) {
        try {
            StartServers startServers = new StartServers();
            startServers.start();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}

