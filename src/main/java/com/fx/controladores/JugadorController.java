/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.controladores;

import com.bingo.enumeraciones.CodigoEvento;
import com.guava.core.EventBusManager;
import com.guava.core.Evento;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author bingo
 */
public class JugadorController implements Initializable {

    //Jugadores
    private int[] creditosMaximosPorPerfil;
    private double[] probabilidadDeApostarPorPerfil;
    private double[] probabilidadDeComprarBolasExtra;
    
    @FXML private Accordion jugadorAccordion;
    @FXML private TitledPane paneJugadorDebil;
    @FXML private ComboBox configuracionJugadoresComboBox;
    @FXML private Button cerrarBtn;
    
    //Entradas
    @FXML private TextField apuestaDebil;
    @FXML private TextField comprarBolasExtraDebil;
    @FXML private TextField creditosMaximosDebil;
    @FXML private TextField apuestaMedio;
    @FXML private TextField bolasExtraMedio;
    @FXML private TextField creditosMaximosMedio;
    @FXML private TextField apuestaFuerte;
    @FXML private TextField bolasExtraFuerte;
    @FXML private TextField creditosMaximosFuerte;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initAccordions();
        initButtons();
        initComboBoxes();
        initConfig();
        initTextFields();
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

    private void initConfig() {
        creditosMaximosPorPerfil = new int[3];
        probabilidadDeApostarPorPerfil = new double[3];
        probabilidadDeComprarBolasExtra = new double[3];
    }

    private void initTextFields() {
        apuestaDebil.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                apuestaDebil.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            this.probabilidadDeApostarPorPerfil[0] = Double.valueOf(apuestaDebil.getText());
            cargarPerfiles();
        });
        
        comprarBolasExtraDebil.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                comprarBolasExtraDebil.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            this.probabilidadDeComprarBolasExtra[0] = Double.valueOf(comprarBolasExtraDebil.getText());
            cargarPerfiles();
        });
        creditosMaximosDebil.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                creditosMaximosDebil.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            this.creditosMaximosPorPerfil[1] = Integer.valueOf(creditosMaximosDebil.getText());
            cargarPerfiles();
        });
        apuestaMedio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                apuestaMedio.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            this.probabilidadDeApostarPorPerfil[1] = Double.valueOf(apuestaMedio.getText());
            cargarPerfiles();
        });
        bolasExtraMedio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                bolasExtraMedio.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            this.probabilidadDeComprarBolasExtra[1] = Double.valueOf(bolasExtraMedio.getText());
            cargarPerfiles();
        });
        creditosMaximosMedio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                creditosMaximosMedio.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            this.creditosMaximosPorPerfil[1] = Integer.valueOf(creditosMaximosMedio.getText());
            cargarPerfiles();
        });
        apuestaFuerte.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                apuestaFuerte.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            this.probabilidadDeApostarPorPerfil[2] = Double.valueOf(apuestaFuerte.getText());
            cargarPerfiles();
        });
        bolasExtraFuerte.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                bolasExtraFuerte.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            this.probabilidadDeComprarBolasExtra[2] = Double.valueOf(bolasExtraFuerte.getText());
            cargarPerfiles();
        });
        creditosMaximosFuerte.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                creditosMaximosFuerte.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            this.creditosMaximosPorPerfil[2] = Integer.valueOf(creditosMaximosFuerte.getText());
            cargarPerfiles();
        });
    }
    
    private void cargarPerfiles(){
        
        Map<String,Object> parametros = new HashMap<>();
        
        parametros.put("creditosMaximosPorPerfil", creditosMaximosPorPerfil);
        parametros.put("probabilidadDeApostarPorPerfil", probabilidadDeApostarPorPerfil);
        parametros.put("probabilidadDeComprarBolasExtra", probabilidadDeComprarBolasExtra);
        
        EventBusManager.getInstancia().getBus()
                .post(new Evento(CodigoEvento.CARGARPERFILES.getValue(),parametros));
    }
}
