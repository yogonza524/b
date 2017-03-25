/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author gonza
 */
public class TournamentController implements Initializable {

    @FXML private Button cerrarBtn;
    @FXML private AnchorPane base;
    @FXML private ComboBox<Double> comboTorunament;
    @FXML private CheckBox usarTournament;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initButtons();
        initTextFields();
        initComboBoxes();
        initConfig();
    }    

    private void initButtons() {
        cerrarBtn.setOnAction(e -> {
            cerrar();
        });
    }

    private void initTextFields() {
        
    }

    private void initConfig() {
        base.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    cerrar();
                }
            }
        });
    }
    
    private void cerrar() {
        Stage stage = (Stage)cerrarBtn.getScene().getWindow();
            stage.close();
    }

    private void initComboBoxes() {
        comboTorunament.setConverter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return object.intValue() + "% de lo apostado";
            }

            @Override
            public Double fromString(String string) {
                return Double.valueOf(string.split("%")[0]);
            }
        });
        
        comboTorunament.valueProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                
            }
        });
    }
    
}
