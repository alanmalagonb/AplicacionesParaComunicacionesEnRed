/* Práctica 1 Aplicación Drive para almacenamiento de archivos
   Alumnos: Malagon Baeza Alan Adrian
            Martinez Chavez Jorge Alexis
   6CM1 Aplicaciones para Comunicaciones en Red
*/

package com.ipn.drive;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


// Controlador interfaz principal
public class DriveController implements Initializable {

    // Folder actual seleccionado
    private String folder = "remoto";

    private Image image;
    private MyListener myListener;
    @FXML
    private VBox chosenFileCard;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button downloadBtn;

    @FXML
    private ImageView fileImg;

    @FXML
    private Label fileNameLbl;

    @FXML
    private Label fileSizeLbl;

    @FXML
    private GridPane grid;

    @FXML
    private Button localBtn;

    @FXML
    private TextField nameTxt;

    @FXML
    private Button remotoBtn;

    @FXML
    private Button renameBtn;

    @FXML
    private ScrollPane scroll;

    @FXML
    private void carpetaNueva(MouseEvent mouseEvent) {
        TextInputDialog tid = new TextInputDialog("Carpeta Sin Título");
        tid.setHeaderText("Carpeta nueva");
        tid.showAndWait().ifPresent(nombre -> {
            if(folder.contains("local")) {
                try {
                    Client.crearCarpetaLocal(folder,nombre);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else Client.crearCarpeta(folder, nombre);
            
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText(null);
            a.setContentText("Carpeta creada..");
            a.show();

            if(folder.contains("local")) actualizarGrid(Client.visualizarLocales("local"));
            else actualizarGrid(Client.visualizarArchivos(folder));
        });
    }

    @FXML
    private void subirArchivo(MouseEvent mouseEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Buscar archivo");
        List<File> file = fileChooser.showOpenMultipleDialog(null);
        if (file == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setHeaderText(null);
            a.setContentText("Elige un archivo..");
            a.show();
            return;
        }
        
        for (File f : file){
            Client.subirArchivo(f, "");
        }
        
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText("Archivo recibido..");
        a.show();
        
        actualizarGrid(Client.visualizarArchivos("remoto"));
    }

    @FXML
    private void subirCarpeta(MouseEvent mouseEvent) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setMultiSelectionEnabled(true); // Seleccionar multiples archivos
        jFileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int r = jFileChooser.showOpenDialog(null);

        if(r == JFileChooser.APPROVE_OPTION){
            File[] files = jFileChooser.getSelectedFiles();
            for(File file : files){
                Client.subirArchivo(file,"");
            }
        }else{
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setHeaderText(null);
            a.setContentText("Elige una carpeta..");
            a.show();
            return;
        }

        // El DirectoryChooser de JavaFX no permite seleccion de multiples carpetas
        /*
        DirectoryChooser fileChooser = new DirectoryChooser();
        fileChooser.setTitle("Buscar carpeta");
        File file = fileChooser.showDialog(null);
        if (file == null) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setHeaderText(null);
            a.setContentText("Elige una carpeta..");
            a.show();
            return;
        }
        Client.subirArchivo(file, "");
        */
        
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText("Carpeta recibida..");
        a.show();
        
        actualizarGrid(Client.visualizarArchivos("remoto"));
    }

    @FXML
    private void miUnidad(MouseEvent mouseEvent) throws IOException {

        actualizarGrid(Client.visualizarArchivos("remoto"));
        
        folder = "remoto";
        remotoBtn.setStyle("-fx-background-color: #A9A9A9;\n" +
                "    -fx-background-radius: 30;");
        localBtn.setStyle("-fx-background-color: #d2d2d2;\n" +
                "    -fx-background-radius: 30;");
        downloadBtn.setText("Descargar");
    }

    @FXML
    private void computadora(MouseEvent mouseEvent) throws IOException {
        actualizarGrid(Client.visualizarLocales("local"));
        
        folder = "local";
        remotoBtn.setStyle("-fx-background-color: #d2d2d2;\n" +
                "    -fx-background-radius: 30;");
        localBtn.setStyle("-fx-background-color: #A9A9A9;\n" +
                "    -fx-background-radius: 30;");
        downloadBtn.setText("Subir");
    }


    @FXML
    private void renombrar() throws IOException {
        if (nameTxt.getText().trim().isEmpty()) {
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setHeaderText(null);
            a.setContentText("Escribe un nombre...");
            a.show();
            return;
        }
        
        String regex = "[:<>|/\\?/*]";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(nameTxt.getText());
        if(m.find() || nameTxt.getText().contains("\\")){
            Alert a = new Alert(Alert.AlertType.WARNING);
            a.setHeaderText(null);
            a.setContentText("Los nombres de archivo no pueden contener ninguno de los siguientes caracteres:"+
                    " \\/:*?\"<>|");
            a.show();
            return;
        }
        
        String path = folder+"\\" +fileNameLbl.getText();
        String rename = folder +"\\"+ nameTxt.getText();
        if(folder.contains("local")) Client.renombrarArchivoLocal(path,rename);
        else Client.renombrarArchivo(path,rename);
        
        nameTxt.setText("");
        if(folder.contains("local")) actualizarGrid(Client.visualizarLocales(folder));
        else actualizarGrid(Client.visualizarArchivos(folder));
    }

    @FXML
    private void descargar() {
        if(downloadBtn.getText().trim().equals("Descargar")) {
            String nombre = fileNameLbl.getText();
            if(nombre.contains("/"))
                nombre=fileNameLbl.getText().substring(0,fileNameLbl.getText().length()-1);
            
            Client.descargarArchivo(folder+"\\"+nombre, "");

            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setHeaderText(null);
            a.setContentText("Archivo descargado..");
            a.show();

            if(folder.contains("local")) actualizarGrid(Client.visualizarLocales("local"));
            else actualizarGrid(Client.visualizarArchivos(folder));
        }else{
            String nombre = fileNameLbl.getText().trim();
            File file = new File(folder+"\\"+nombre);
            
            Client.subirArchivo(file, "");
            
            if(nombre.contains("\\"))
            {//Es carpeta
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setHeaderText(null);
                a.setContentText("Carpeta recibida..");
                a.show();
            }
            else //Es archivo
            {
                Alert a = new Alert(Alert.AlertType.INFORMATION);
                a.setHeaderText(null);
                a.setContentText("Archivo recibido..");
                a.show();
            }
        }
    }

    @FXML
    private void eliminar() throws IOException {
        String target = fileNameLbl.getText();
        String path = folder;

        if(folder.contains("local")) Client.eliminarArchivoLocal(path,target);
        else    Client.eliminarCarpeta(path, target);


        if(folder.contains("local")) actualizarGrid(Client.visualizarLocales("local"));
        else actualizarGrid(Client.visualizarArchivos(folder));
    }

    @FXML
    private void opciones() throws IOException {
        System.out.println("Opciones");
        Client.opciones();
    }

    private void establecerArchivoElegido(Directory directory) {
        String nombre = directory.getName();
        if(nombre.contains("\\"))
            nombre = nombre.substring(0,nombre.length()-1);
        nameTxt.setText(nombre);
        fileNameLbl.setText(directory.getName());
        String size = "-";
        if (directory.getSize() != 0) size = directory.getSize() + " Bytes";
        fileSizeLbl.setText(size);
        image = new Image(getClass().getResourceAsStream(directory.getImgSrc()));
        fileImg.setImage(image);
    }

    private void abrirCarpeta(Directory directory) {
        if (directory.getName().contains(".")) return;
        if(folder.contains("local")) actualizarGrid(Client.visualizarLocales(folder + "\\" + directory.getName()));
        else actualizarGrid(Client.visualizarArchivos(folder + "\\" + directory.getName()));
        folder += "\\"+directory.getName();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        actualizarGrid(Client.visualizarArchivos("remoto"));
        remotoBtn.setStyle("-fx-background-color: #A9A9A9;\n" +
                "    -fx-background-radius: 30;");
    }

    public void actualizarGrid(List<Directory> files) {
        grid.getChildren().clear();
        if (files.isEmpty()){
            chosenFileCard.setVisible(false);
            return;
        }

        chosenFileCard.setVisible(true);
        if (files.size() > 0) {
            establecerArchivoElegido(files.get(0));
            myListener = new MyListener() {
                @Override
                public void onClickListener(Directory directory) {
                    establecerArchivoElegido(directory);
                }

                @Override
                public void onDoubleClickListener(Directory directory) {
                    abrirCarpeta(directory);
                }
            };
        }

        int column = 0;
        int row = 1;
        try {
            for (Directory file : files) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("item.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemController itemController = fxmlLoader.getController();
                itemController.setData(file, myListener);

                if (column == 3) {
                    column = 0;
                    row++;
                }

                grid.add(anchorPane, column++, row); // (child,colum,row)
                // establecer el ancho de la cuadrícula
                grid.setMinWidth(Region.USE_COMPUTED_SIZE);
                grid.setPrefWidth(Region.USE_COMPUTED_SIZE);
                grid.setMaxWidth(Region.USE_PREF_SIZE);

                // establecer la altura de la cuadrícula
                grid.setMinHeight(Region.USE_COMPUTED_SIZE);
                grid.setPrefHeight(Region.USE_COMPUTED_SIZE);
                grid.setMaxHeight(Region.USE_PREF_SIZE);

                GridPane.setMargin(anchorPane, new Insets(10));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}