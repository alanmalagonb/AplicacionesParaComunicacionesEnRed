/* Práctica 1 Aplicación Drive para almacenamiento de archivos
   Alumnos: Malagon Baeza Alan Adrian
            Martinez Chavez Jorge Alexis
   6CM1 Aplicaciones para Comunicaciones en Red
*/

package com.ipn.drive;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

// Interfaz principal
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("drive.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Drive");
        stage.getIcons().add(new Image(Main.class.getResourceAsStream("img/cloud.png")));
        stage.setScene(scene);
        stage.show();
    }
}
