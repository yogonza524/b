/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.controladores;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author bingo
 */
public class JugadorController implements Initializable {

    @FXML private Accordion jugadorAccordion;
    @FXML private TitledPane paneJugadorDebil;
    @FXML private ComboBox configuracionJugadoresComboBox;
    @FXML private Button cerrarBtn;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initAccordions();
        initButtons();
        initComboBoxes();
    }    
    
    private void initAccordions() {
        jugadorAccordion.setExpandedPane(paneJugadorDebil);
    }

    private void initButtons() {
        cerrarBtn.setOnAction(e -> {
            // get a handle to the stage
            Stage stage = (Stage) cerrarBtn.getScene().getWindow();
            // do what you have to do
            stage.close();
        });
    }

    private void initComboBoxes() {
        configuracionJugadoresComboBox.getItems().clear();
        
        
    }
}
