/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.controladores;

import com.bingo.enumeraciones.CodigoEvento;
import com.guava.core.EventBusManager;
import com.guava.core.Evento;
import com.persistencia.ConfiguracionPersistencia;
import com.persistencia.PersistenciaJson;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    @FXML private CheckBox usarTournamentCheck;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        initButtons();
        initCheckBoxes();
        initComboBoxes();
        try {
            initConfig();
        } catch (IOException ex) {
            Logger.getLogger(TournamentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    private void initButtons() {
        cerrarBtn.setOnAction(e -> {
            cerrar();
        });
    }

    private void initCheckBoxes() {
        usarTournamentCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                comboTorunament.setDisable(!newValue);
                
                if (comboTorunament.getSelectionModel().getSelectedIndex() < 0) {
                    comboTorunament.getSelectionModel().selectFirst();
                }
                
                Double porcentajeParaTournament = comboTorunament.getSelectionModel().getSelectedItem();
                
                Map<String,Object> param = new  HashMap<>();
                param.put("porcentajeParaTournament", porcentajeParaTournament);
                param.put("tournament", newValue);
                
                EventBusManager.getInstancia().getBus()
                        .post(new Evento(CodigoEvento.TOURNAMENT.getValue(), param));
            }
            
        });
    }

    private void initConfig() throws IOException {
        base.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    cerrar();
                }
            }
        });
        
        ConfiguracionPersistencia config = PersistenciaJson.getInstancia().getConfiguracion();
        
        Boolean tournament = config.isTournament();
        Integer indiceTournament = config.getIndicePorcentajeTournamentCombo();
        
        usarTournamentCheck.setSelected(tournament);
        comboTorunament.getSelectionModel().select(indiceTournament);
    }
    
    private void cerrar() {
        Stage stage = (Stage)cerrarBtn.getScene().getWindow();
            stage.close();
    }

    private void initComboBoxes() {
        comboTorunament.setConverter(new StringConverter<Double>() {
            @Override
            public String toString(Double object) {
                return (object.intValue() + 1) + "% de lo apostado";
            }

            @Override
            public Double fromString(String string) {
                return ((Double.valueOf(string.split("%")[0])) * 0.01);
            }
        });
        
        for (int i = 0; i < 100; i++) {
            comboTorunament.getItems().add(i * 1.0);
        }
        
        comboTorunament.valueProperty().addListener(new ChangeListener<Double>() {
            @Override
            public void changed(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
                Double porcentajeParaTournament = (comboTorunament.getSelectionModel().getSelectedItem() + 1) * 0.01;
                int indicePorcentajeTournamentCombo = comboTorunament.getSelectionModel().getSelectedIndex();
                
                Map<String,Object> param = new  HashMap<>();
                param.put("porcentajeParaTournament", porcentajeParaTournament);
                param.put("indicePorcentajeTournamentCombo", indicePorcentajeTournamentCombo);
                
                EventBusManager.getInstancia().getBus()
                        .post(new Evento(CodigoEvento.TOURNAMENT.getValue(), param));
                
                //Debug
                System.out.println("Porcentaje elegido: " + porcentajeParaTournament);
            }
        });
    }
    
}
