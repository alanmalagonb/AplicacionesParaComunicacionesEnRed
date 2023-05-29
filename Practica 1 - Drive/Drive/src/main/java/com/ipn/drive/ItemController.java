/* Práctica 1 Aplicación Drive para almacenamiento de archivos
   Alumnos: Malagon Baeza Alan Adrian
            Martinez Chavez Jorge Alexis
   6CM1 Aplicaciones para Comunicaciones en Red
*/

package com.ipn.drive;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

// Controlador de Cards de archivo/carpetas
public class ItemController {

    @FXML
    private ImageView img;

    @FXML
    private Label nameLabel;

    @FXML
    private void click(MouseEvent mouseEvent) {
        myListener.onClickListener(directory);
        if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
            if (mouseEvent.getClickCount() == 2) {
                myListener.onDoubleClickListener(directory);
            }
        }
    }

    private Directory directory;
    private MyListener myListener;

    public void setData(Directory directory, MyListener myListener) {
        this.directory = directory;
        this.myListener = myListener;
        nameLabel.setText(directory.getName());
        Image image = new Image(getClass().getResourceAsStream(directory.getImgSrc()));
        img.setImage(image);
    }
}
