package com.ipn.buscaminas;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

public class RecordController implements Initializable {
    @FXML
    private Label playersLbl;

    @FXML
    private Label timeLbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        playersLbl.setText("");
        timeLbl.setText("");
        ObjectMapper objectMapper = new ObjectMapper();
        List<Record> records = null;
        try {
            records = objectMapper.readValue(new File("records.json"), new TypeReference<List<Record>>() {});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(records);
        records.removeIf(r -> r.getNivel() != Buscaminas.niveln);
        Collections.sort(records, Comparator.comparing(Record::getTiempo));

        int i = 0;
        for (Record r : records) {
            if(i==6) return;
            playersLbl.setText(playersLbl.getText()+r.getJugador()+"\n");
            timeLbl.setText(timeLbl.getText()+r.getTiempo()+"\n");
            i++;
        }
    }
}
