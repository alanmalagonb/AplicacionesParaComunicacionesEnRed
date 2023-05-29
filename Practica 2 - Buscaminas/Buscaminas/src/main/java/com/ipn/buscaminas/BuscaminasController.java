package com.ipn.buscaminas;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class BuscaminasController implements Initializable {
    @FXML
    private HBox content;

    @FXML
    private Label flagsLbl;

    @FXML
    private AnchorPane ap;

    private int i1, j1;
    static int casillasW =  Buscaminas.casillasW;
    static int casillasH =  Buscaminas.casillasH;
    static int minas = Buscaminas.minas;
    private final int[][] board = new int[casillasW][casillasH];
    private final Button[][] boardButtons = new Button[casillasW][casillasH];
    private int points = 0;
    private int finalPoints = 0;
    private static String greenColor="#A3D14A";
    private static String dirtColor="#E5C29F";
    private static String mudColor="#D7B899";
    private static String limeColor="#A9D751";

    @FXML
    void logros(MouseEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Buscaminas.class.getResource("records.fxml"));
        Stage stage = new Stage();
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Logros");
        stage.setScene(scene);
        stage.show();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        crearBuscaminas();

        flagsLbl.setText(minas+"");

        VBox rows = new VBox();
        for (int i = 0; i < board.length; i++) {
            HBox columns = new HBox();
            for (int j = 0; j < board[i].length; j++) {
                boardButtons[i][j] = new Button();
                if(i%2 == 0){
                    if(j%2==0){
                        boardButtons[i][j].setStyle("-fx-background-color: " + greenColor + ";"+
                                "-fx-background-radius: 0;");
                    }else{
                        boardButtons[i][j].setStyle("-fx-background-color: " + limeColor + ";"+
                                "-fx-background-radius: 0;");
                    }
                }else{
                    if(j%2==0){
                        boardButtons[i][j].setStyle("-fx-background-color: " + limeColor + ";"+
                                "-fx-background-radius: 0;");
                    }else{
                        boardButtons[i][j].setStyle("-fx-background-color: " + greenColor + ";"+
                                "-fx-background-radius: 0;");
                    }
                }
                boardButtons[i][j].setFont(Font.font(14));
                boardButtons[i][j].setTextFill(Color.BLACK);
                boardButtons[i][j].setPrefSize(32, 32);
                boardButtons[i][j].setUserData(new ButtonInfo(i, j));
                boardButtons[i][j].setOnMouseClicked(buttonClick);
                boardButtons[i][j].setMaxWidth(32);
                boardButtons[i][j].setMinWidth(32);
                boardButtons[i][j].setMaxHeight(32);
                boardButtons[i][j].setMinHeight(32);
                columns.getChildren().add(boardButtons[i][j]);
            }
            rows.getChildren().add(columns);
        }
        content.getChildren().add(0, rows);
        content.setMaxHeight(Control.USE_COMPUTED_SIZE);

        Platform.runLater(()-> {
                Stage stage = (Stage) ap.getScene().getWindow();
                double topBarSize = 89;
                double w = boardButtons[0][0].getPrefWidth();
                double finalW = (w * casillasH)+w/2;
                stage.setWidth(finalW);
                double h = boardButtons[0][0].getPrefHeight();
                double finalH = (h * casillasW)+topBarSize;
                stage.setHeight(finalH);
            }
        );
        for (int[] x : board)
        {
            for (int y : x)
            {
                System.out.print(y + " ");
            }
            System.out.println();
        }
    }

    private void crearBuscaminas() {
        colocarMinas(minas);
        finalPoints += minas*9;
        for (int i=0;i<casillasW;i++){
            for (int j=0;j<casillasH;j++){
                if (board[i][j] == 0){
                    int minas = checarMina(i+1,j)+
                    checarMina(i-1,j)+
                    checarMina(i,j+1)+
                    checarMina(i,j-1)+
                    checarMina(i-1,j+1)+
                    checarMina(i+1,j+1)+
                    checarMina(i-1,j-1)+
                    checarMina(i+1,j-1);
                    board[i][j] = minas;
                    finalPoints+=minas;
                }
            }
        }
        Buscaminas.finalPoints = finalPoints;
    }

    private List<ButtonInfo> botonesMinas = new ArrayList<>();
    private void colocarMinas(int minas){
        if(minas==0) return;
        int im = (int) Math.floor(Math.random() * (casillasW));
        int hm = (int) Math.floor(Math.random() * (casillasH));
        if(board[im][hm] == -1)
            colocarMinas(minas);
        else{
            int minasN = checarMina(im+1,hm)+
                    checarMina(im-1,hm)+
                    checarMina(im,hm+1)+
                    checarMina(im,hm-1)+
                    checarMina(im-1,hm+1)+
                    checarMina(im+1,hm+1)+
                    checarMina(im-1,hm-1)+
                    checarMina(im+1,hm-1);
            if(minasN<4) {
                board[im][hm] = -1;
                ButtonInfo buttonInfo = new ButtonInfo(im,hm);
                botonesMinas.add(buttonInfo);
                colocarMinas(minas - 1);
            }else
                colocarMinas(minas);

        }
    }

    private void revelarCasilla(int i, int j){

        int n = board[i][j];
        if(n == -1){
            styleButton(i,j,"mine",false);
            revelarMinas();
            Buscaminas.points = points;
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("¡Perdiste!");
            alert.showAndWait();
            Platform.exit();
            return;
        }
        points+=n;
        styleButton(i,j,n+"",false);

        if(n == 0)
            revelarCasillasAlrededor(i,j);
    }

    private void revelarMinas() {
        Collections.shuffle(botonesMinas);
        botonesMinas.forEach(mina -> {
            int i = mina.getI();
            int j = mina.getJ();
            boardButtons[i][j].setGraphic(null);
            boardButtons[i][j].setText("⬤");
            Random random = new Random();
            boardButtons[i][j].setStyle("-fx-background-color: " + String.format("#%06x", random.nextInt(0xffffff + 1)) + ";"+
                    "-fx-background-radius: 0;");
        });
    }
    private void revelarCasillasAlrededor(int i,int j){
        if (i > 0 && estaBloqueada(i - 1, j)) revelarCasilla(i - 1, j);
        if (i < casillasW-1 && estaBloqueada(i + 1, j)) revelarCasilla(i + 1, j);
        if (j > 0 && estaBloqueada(i, j - 1)) revelarCasilla(i, j - 1);
        if (j < casillasW-1 && estaBloqueada(i, j + 1)) revelarCasilla(i, j + 1);
        if (i > 0 && j > 0 && estaBloqueada(i - 1, j - 1)) revelarCasilla(i - 1, j - 1);
        if (i < casillasW-1 && j < casillasH-1 && estaBloqueada(i + 1, j + 1)) revelarCasilla(i + 1, j + 1);
        if (i > 0 && j < casillasH-1 && estaBloqueada(i - 1, j + 1)) revelarCasilla(i - 1, j + 1);
        if (i < casillasW-1 && j > 0 && estaBloqueada(i + 1, j - 1)) revelarCasilla(i + 1, j - 1);
    }


    private boolean estaBloqueada(int i,int j){
        if(boardButtons[i][j].getGraphic() == null && board[i][j] != -1 && board[i][j] !=9)
            return true;
        return false;
    }

    private int checarMina(int i, int j){
        if(i < 0 || i > casillasW-1 || j < 0 || j > casillasH-1) return 0;
        if(board[i][j]==-1) return(1);
        return 0;
    }

    private void styleButton( int i, int j,String url,boolean isGreen) {
        String light;
        String dark;
        if(isGreen){
            light=limeColor;
            dark=greenColor;
        }else{
            light=dirtColor;
            dark=mudColor;
        }
        if(i%2 == 0){
            if(j%2==0){
                boardButtons[i][j].setStyle("-fx-background-color: " + dark + ";" +
                        "-fx-background-radius: 0;");
            }else{
                boardButtons[i][j].setStyle("-fx-background-color: " + light + ";"+
                        "-fx-background-radius: 0;");
            }
        }else{
            if(j%2==0){
                boardButtons[i][j].setStyle("-fx-background-color: " + light + ";"+
                        "-fx-background-radius: 0;");
            }else{
                boardButtons[i][j].setStyle("-fx-background-color: " + dark + ";"+
                        "-fx-background-radius: 0;");
            }
        }
        if(url.equals("")) return;
        Button button = boardButtons[i][j];
        Image image = new Image(getClass().getResourceAsStream(url+".png"));

        ImageView imageView = new ImageView(image);
        imageView.setPreserveRatio(true);
        imageView.fitWidthProperty().bind(button.widthProperty());
        imageView.fitHeightProperty().bind(button.heightProperty());

        button.setGraphic(imageView);
    }


    private final EventHandler<MouseEvent> buttonClick = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent mouseEvent) {
            Button button = (Button) mouseEvent.getTarget();
            button.setText("");
            ButtonInfo buttonInfo = (ButtonInfo) button.getUserData();
            i1 = buttonInfo.getI();
            j1 = buttonInfo.getJ();
            int casillaSeleccionada = board[i1][j1];

            if(mouseEvent.getButton() == MouseButton.PRIMARY){
                if(casillaSeleccionada != 9){
                    revelarCasilla(i1,j1);

                }
            } else if(mouseEvent.getButton() == MouseButton.SECONDARY) {
                if(casillaSeleccionada != 9 && boardButtons[i1][j1].getGraphic() == null){
                    if(casillaSeleccionada == -1) points+=9;
                    styleButton(i1,j1,"flag_icon",true);

                    board[i1][j1]=9;
                    flagsLbl.setText(Integer.parseInt(flagsLbl.getText())-1+"");
                }
                else if(casillaSeleccionada == 9){
                    points-=9;
                    flagsLbl.setText(Integer.parseInt(flagsLbl.getText())+1+"");
                    button.setGraphic(null);
                    board[i1][j1]=0;
                }
            }

            if(points==finalPoints){
                Buscaminas.points=points;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setHeaderText("¡Ganaste!");
                alert.showAndWait();
                Platform.exit();
            }
        }
    };
}
