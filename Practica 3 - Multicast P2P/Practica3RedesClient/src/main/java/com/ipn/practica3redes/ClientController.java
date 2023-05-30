package com.ipn.practica3redes;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ClientController implements Initializable {

    Files db = new Files();

    DownloadClient dClient = new DownloadClient(this);
    MulticastClient mClient = new MulticastClient(db);
    MulticastServersWatcher msw = new MulticastServersWatcher(db);
    SearchClient sClient = new SearchClient(db,this);

    @FXML
    private Button downloadBtn;

    @FXML
    private ProgressBar downloadPB;

    @FXML
    private ListView<String> filesLV;

    @FXML
    private Label md5Lbl;

    @FXML
    private Label nameLbl;

    @FXML
    private TextField nameTxt;

    @FXML
    private Label ruteLbl;

    @FXML
    private Label serverLbl;

    @FXML
    void onDownloadButtonClick(ActionEvent event) {
        if(selectedLV != -1){
            downloadPB.setVisible(true);
            FileServer fs = db.getFiles().get(selectedLV);

            DataFromServer serverTo = null;
            for (DataFromServer server:
                 db.getServers()) {
                if(fs.getFileInServer().equals(server.getAddress()))
                    serverTo = server;
            }

            dClient.serverConnection(serverTo);
            dClient.receiveFile(fs.getFoundFile());
        }
    }

    @FXML
    void onSearchButtonClick(ActionEvent event) {
        if(nameTxt.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText(Constants.EMPTY_FILE_NAME);
            alert.show();
            return;
        }
        List<DataFromServer> servers = db.getServers();
        db.clearFiles();
        filesLV.getItems().clear();

        if(!servers.isEmpty()){
            System.out.println(servers.size());
            int i;
            for (i = 0; i < servers.size(); i++) {
                sClient.serverConnection(servers.get(i));
                sClient.receiveFile(nameTxt.getText());
            }

        } else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setHeaderText(null);
            alert.setContentText(Constants.EMPTY_SERVERS_MESSAGE);
            alert.show();
        }
    }

    public void search(FileServer fs){
        String fileName = fs.getFoundFile().getFileName();

        filesLV.getItems().addAll(fileName);

        if(db.getFiles().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText(Constants.FILE_NOT_FOUND);
            alert.show();
        }else{
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setContentText(Constants.FOUND_FILE);
            alert.show();
        }
    }

    private int selectedLV = -1;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        clientsStart();
        filesLV.setOnMouseClicked(mouseEvent -> {
            selectedLV = filesLV.getSelectionModel().getSelectedIndex();
            if(selectedLV < 0) return;
            FileServer fs = db.getFiles().get(selectedLV);
            nameLbl.setText(fs.getFoundFile().getFileName());
            int searchPort =0000;
            for(DataFromServer server: db.getServers()){
                if(server.getAddress() == fs.getFileInServer()){
                    searchPort = server.getSport();
                }
            }
            serverLbl.setText(fs.getFileInServer() +":"+searchPort);
            ruteLbl.setText(fs.getFoundFile().getPath());
            md5Lbl.setText(fs.getFoundFile().getMd5());
            downloadBtn.setVisible(true);
            downloadPB.setVisible(true);
        });
    }

    public void clientsStart(){
        mClient.start();
        //sw.start();
        sClient.start();
        dClient.start();
        md5Lbl.setVisible(false);
        downloadBtn.setVisible(false);
        downloadPB.setVisible(false);
    }

    public void setProgressBar(int percentage) {
        downloadPB.setProgress(percentage);
    }
}