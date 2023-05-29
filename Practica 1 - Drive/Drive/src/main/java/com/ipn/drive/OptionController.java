/* Práctica 1 Aplicación Drive para almacenamiento de archivos
   Alumnos: Malagon Baeza Alan Adrian
            Martinez Chavez Jorge Alexis
   6CM1 Aplicaciones para Comunicaciones en Red
*/

package com.ipn.drive;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class OptionController implements Initializable {

    @FXML
    private TextField buferEntrada;

    @FXML
    private TextField buferEscritura;

    @FXML
    private TextField lingerLong;

    @FXML
    private TextField lingerStatus;

    @FXML
    private TextField nagle;

    @FXML
    private TextField temporizador;

    @FXML
    void aceptar(MouseEvent event) {
        Client.guardarOpciones(buferEntrada.getText(),buferEscritura.getText(),lingerLong.getText(),lingerStatus.getText(),nagle.getText(),temporizador.getText());
        Stage stage = (Stage) buferEscritura.getScene().getWindow();
        stage.close();
    }

    @FXML
    void cancelar(MouseEvent event) {
        Stage stage = (Stage) buferEscritura.getScene().getWindow();
        stage.close();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
