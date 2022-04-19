package com.example.konyvtarasztali;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class KonyvtarController {
    @FXML
    private TableColumn<Konyv, Integer> publish_year;
    @FXML
    private TableColumn<Konyv, String> author;
    @FXML
    private TableColumn<Konyv, String> title;
    @FXML
    private TableColumn<Konyv, Integer> page_count;
    @FXML
    private TableView<Konyv> konyvek;
    private KonyvtarDB db;

    public void initialize(){
        title.setCellValueFactory(new PropertyValueFactory<>("title")); //getTitle()
        author.setCellValueFactory(new PropertyValueFactory<>("author"));
        publish_year.setCellValueFactory(new PropertyValueFactory<>("publish_year"));
        page_count.setCellValueFactory(new PropertyValueFactory<>("page_count"));
        try {
            db = new KonyvtarDB();
            listaz();
        } catch (SQLException e) {
            Platform.runLater(() ->{
                hibaKiir(e);
                Stage stage = (Stage) konyvek.getScene().getWindow();
                stage.close();
            });

        }
    }

    private void listaz() throws SQLException {
        List<Konyv> konyvList = db.getKonyvek();
        konyvek.getItems().clear();
        konyvek.getItems().addAll(konyvList);
    }

    private void hibaKiir(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(e.getClass().toString());
        alert.setHeaderText(e.getMessage());
        alert.setHeaderText(e.getMessage());
        alert.setContentText(e.getLocalizedMessage());
        alert.showAndWait();
    }


    @FXML
    public void torlesClick(ActionEvent actionEvent) {
        if (konyvek.getSelectionModel().getSelectedIndex() < 0){
            alert("Törléshez elöbb válasszon ki egy könyvet!");
            return;
        }
        Konyv torlendo = konyvek.getSelectionModel().getSelectedItem();
        if (!confirm("Biztos, hogy törölni szeretnéd a kiválasztott könyvet?")){
            return;
        }
        try {
            if (db.deleteKonyv(torlendo)){
                alert("Sikeres törlés!");
            }else{
                alert("Ismeretlen hiba történt a törlés során.");
            }
            listaz();
        } catch (SQLException e) {
            hibaKiir(e);
        }
    }

    private boolean confirm(String s) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Biztos?");
        alert.setHeaderText(s);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get().equals(ButtonType.OK);
    }

    private void alert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Figyelem!");
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}