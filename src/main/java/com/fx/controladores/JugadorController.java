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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.stage.Stage;
import org.apache.commons.lang3.ArrayUtils;

/**
 * FXML Controller class
 *
 * @author bingo
 */
public class JugadorController implements Initializable {

    //Jugadores
    private Integer[] creditosMaximosPorPerfil;
    private Double[] probabilidadDeApostarPorPerfil;
    private Double[] probabilidadDeComprarBolasExtra;
    
    private int indiceConfiguracion;
    private String[] configuraciones;
    
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
        try {
            initConfig();
        } catch (IOException ex) {
            Logger.getLogger(JugadorController.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    private void initConfig() throws IOException {
        ConfiguracionPersistencia config = PersistenciaJson.getInstancia().getConfiguracion();
        
        creditosMaximosPorPerfil = config.getCreditosMaximosPorPerfil();
        probabilidadDeApostarPorPerfil = config.getProbabilidadDeApostarPorPerfil();
        probabilidadDeComprarBolasExtra = config.getProbabilidadDeComprarBolasExtra();
        indiceConfiguracion = config.getIndiceConfiguracionJugadores();
        
        //Debil
        apuestaDebil.setText((int)(probabilidadDeApostarPorPerfil[0] * 100) + "");
        comprarBolasExtraDebil.setText((int)(probabilidadDeComprarBolasExtra[0] * 100) + "");
        creditosMaximosDebil.setText(creditosMaximosPorPerfil[0] + "");
        
        //Medio
        apuestaMedio.setText((int)(probabilidadDeApostarPorPerfil[1] * 100) + "");
        bolasExtraMedio.setText((int)(probabilidadDeComprarBolasExtra[1] * 100) + "");
        creditosMaximosMedio.setText(creditosMaximosPorPerfil[1] + "");
        
        //Fuerte
        apuestaFuerte.setText((int)(probabilidadDeApostarPorPerfil[2] * 100) + "");
        bolasExtraFuerte.setText((int)(probabilidadDeComprarBolasExtra[2] * 100) + "");
        creditosMaximosFuerte.setText(creditosMaximosPorPerfil[2] + "");
        
        //Cargar el combo de configuracion
        configuracionJugadoresComboBox.getItems().clear();
        
        configuraciones = new String[]{
            "Debil[30%] - Medio[30%] - Fuerte[40%]",
            "Debil[40%] - Medio[30%] - Fuerte[30%]",
            "Debil[10%] - Medio[45%] - Fuerte[45%]",
            "Debil[100%]",
            "Medio[100%]",
            "Fuerte[100%]",
        };
        
        for(String con : configuraciones){
            configuracionJugadoresComboBox.getItems().add(con);
        }
        
        configuracionJugadoresComboBox.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                cargarPerfiles();
            }
        });
        
        configuracionJugadoresComboBox.getSelectionModel().select(config.getIndiceConfiguracionJugadores());
    }

    private void initTextFields() {
        apuestaDebil.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                apuestaDebil.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            try {
                this.probabilidadDeApostarPorPerfil[0] = Integer.valueOf(apuestaDebil.getText()) * 0.01;
            } catch (Exception e) {
            }
            cargarPerfiles();
        });
        
        comprarBolasExtraDebil.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                comprarBolasExtraDebil.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            try {
                this.probabilidadDeComprarBolasExtra[0] = Integer.valueOf(comprarBolasExtraDebil.getText()) * 0.01;
            } catch (Exception e) {
            }
            cargarPerfiles();
        });
        creditosMaximosDebil.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                creditosMaximosDebil.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            try {
                this.creditosMaximosPorPerfil[0] = Integer.valueOf(creditosMaximosDebil.getText());
            } catch (Exception e) {
            }
            cargarPerfiles();
        });
        apuestaMedio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                apuestaMedio.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            this.probabilidadDeApostarPorPerfil[1] = Integer.valueOf(apuestaMedio.getText()) * 0.01;
            cargarPerfiles();
        });
        bolasExtraMedio.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                bolasExtraMedio.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            this.probabilidadDeComprarBolasExtra[1] = Integer.valueOf(bolasExtraMedio.getText()) * 0.01;
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
            this.probabilidadDeApostarPorPerfil[2] = Integer.valueOf(apuestaFuerte.getText()) * 0.01;
            cargarPerfiles();
        });
        bolasExtraFuerte.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                bolasExtraFuerte.setText(newValue.replaceAll("[^\\d]", ""));
                return;
            }
            this.probabilidadDeComprarBolasExtra[2] = Integer.valueOf(bolasExtraFuerte.getText()) * 0.01;
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
        
        if (configuracionJugadoresComboBox.getSelectionModel().getSelectedIndex() < 0) {
            parametros.put("indiceConfiguracionJugadores", 0);
        }
        else{
            parametros.put("indiceConfiguracionJugadores", configuracionJugadoresComboBox.getSelectionModel().getSelectedIndex());
        }
        
        EventBusManager.getInstancia().getBus()
                .post(new Evento(CodigoEvento.CARGARPERFILES.getValue(),parametros));
    }
}
