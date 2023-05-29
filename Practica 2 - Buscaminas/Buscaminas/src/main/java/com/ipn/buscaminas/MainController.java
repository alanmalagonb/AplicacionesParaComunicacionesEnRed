package com.ipn.buscaminas;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private TextField nickTxt;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    void jugar(MouseEvent event) {
        String nick = nickTxt.getText();
        if(nick.isEmpty() || nick.isBlank()){
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText(null);
            a.setContentText("Ingresa un nick");
            a.show();
            return;
        } else if (nickTxt.getLength() > 12){
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText(null);
            a.setContentText("El nick debe tener 12 caracteres o menos");
            a.show();
            return;
        }
        Buscaminas.nickName = nick;

        Stage stage = (Stage) nickTxt.getScene().getWindow();
        stage.close();

        Buscaminas.iniciarJuego();


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        comboBox.getItems().addAll("Principiante","Intermedio","Experto");
        comboBox.setOnAction((ActionEvent ev) -> {
            Buscaminas.nivel =  comboBox.getSelectionModel().getSelectedItem().toString();
        });
    }
}
