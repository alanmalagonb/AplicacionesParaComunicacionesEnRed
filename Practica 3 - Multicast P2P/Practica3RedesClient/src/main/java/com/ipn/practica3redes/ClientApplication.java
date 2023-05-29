package com.ipn.practica3redes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        // Cargar el archivo FXML de la interfaz de usuario
        FXMLLoader fxmlLoader = new FXMLLoader(ClientApplication.class.getResource("app.fxml"));
        // Crear una nueva escena utilizando el contenido del archivo FXML cargado
        Scene scene = new Scene(fxmlLoader.load());
        // Establecer el título de la ventana principal de la aplicación
        stage.setTitle(Constants.WINDOW_TITLE);
        // Establecer la escena recién creada en el Stage principal de la aplicación
        stage.setScene(scene);
        // Mostrar el Stage principal de la aplicación
        stage.show();
    }

    public static void main(String[] args) {
        // Iniciar la aplicación llamando al método launch() de la clase Application
        launch();
    }
}