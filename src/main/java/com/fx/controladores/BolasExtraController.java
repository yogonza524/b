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
import javafx.scene.control.TextField;
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
public class BolasExtraController implements Initializable {

    private boolean utilizarUmbral;
    private int umbralParaLiberarBolasExtra;
    private int factorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor;
    private int limiteMinimoGratis;
    private int limiteMaximoGratis;
    
    //Anchor pane base
    @FXML private AnchorPane base;
    
    @FXML private Button cerrarBtn;
    @FXML private TextField limiteMaximoBolaGratis;
    @FXML private TextField limiteMinimoBolaGratis;
    @FXML private TextField umbralText;
    @FXML private ComboBox<Integer> comboCosto;
    @FXML private CheckBox bolaGratisCheck;
    @FXML private CheckBox utilizarUmbralCheck;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Registro el objeto para escuchar los mensajes de la tuberia de mensajes
        EventBusManager.getInstancia().getBus()
                .register(this);
        
        try {
            initConfig();
        } catch (IOException ex) {
            Logger.getLogger(BolasExtraController.class.getName()).log(Level.SEVERE, null, ex);
        }
        initButtons();
        initTextFields();
        initComboBoxes();
        initCheckBoxes();
    }    

    private void initConfig() throws IOException {
        
        ConfiguracionPersistencia config = PersistenciaJson.getInstancia().configuracion();
        bolaGratisCheck.setSelected(config.isUtilizarBolasExtraGratis());
        utilizarUmbral = config.isUtilizarUmbral();
        umbralParaLiberarBolasExtra = config.getUmbralParaLiberarBolasExtra();
        limiteMinimoGratis = config.getLimiteMinimoGratis();
        limiteMaximoGratis = config.getLimiteMaximoGratis();
        
        utilizarUmbralCheck.setSelected(utilizarUmbral);
        
        umbralText.setText(umbralParaLiberarBolasExtra + "");
        limiteMinimoBolaGratis.setText(limiteMinimoGratis + "");
        limiteMaximoBolaGratis.setText(limiteMaximoGratis + "");
    }

    private void initButtons() {
        cerrarBtn.setOnAction(e -> {
            Stage stage = (Stage) cerrarBtn.getScene().getWindow();
            stage.close();
        });
        
        
        base.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    Stage stage = (Stage)cerrarBtn.getScene().getWindow();
                    stage.close();
                }
            }
        });
    }

    private void initTextFields() {
        umbralText.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (umbralText.isFocused()) {
                    if (!newValue.matches("\\d*")) {
                        umbralText.setText(newValue.replaceAll("[^\\d]", ""));
                        return;
                    }
                    
                    if (umbralText.getText().isEmpty()) {
                        umbralText.setText("0");
                    }
                    
                    umbralParaLiberarBolasExtra = Integer.valueOf(umbralText.getText());
                    
                    Map<String,Object> parametros = new HashMap<>();
                    parametros.put("umbralParaLiberarBolasExtra", umbralParaLiberarBolasExtra);
                    
                    //Avisar al oyente principal
                    EventBusManager.getInstancia().getBus()
                            .post(new Evento(CodigoEvento.BOLASEXTRA.getValue(),parametros));
                }
            }
        });
        
        limiteMinimoBolaGratis.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (limiteMinimoBolaGratis.isFocused()) {
                    if (!newValue.matches("\\d*")) {
                        limiteMinimoBolaGratis.setText(newValue.replaceAll("[^\\d]", ""));
                        return;
                    }
                    
                    if (limiteMinimoBolaGratis.getText().isEmpty()) {
                        limiteMinimoBolaGratis.setText("0");
                    }
                    
                    limiteMinimoGratis = Integer.valueOf(limiteMinimoBolaGratis.getText());
                    
                    Map<String,Object> parametros = new HashMap<>();
                    parametros.put("limiteMinimoGratis", limiteMinimoGratis);
                    
                    //Avisar al oyente principal
                    EventBusManager.getInstancia().getBus()
                            .post(new Evento(CodigoEvento.BOLASEXTRA.getValue(),parametros));
                }
            }
            
        });
        
        limiteMaximoBolaGratis.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (limiteMaximoBolaGratis.isFocused()) {
                    if (!newValue.matches("\\d*")) {
                        limiteMaximoBolaGratis.setText(newValue.replaceAll("[^\\d]", ""));
                        return;
                    }
                    
                    if (limiteMaximoBolaGratis.getText().isEmpty()) {
                        limiteMaximoBolaGratis.setText("0");
                    }
                    
                    limiteMaximoGratis = Integer.valueOf(limiteMaximoBolaGratis.getText());
                    
                    Map<String,Object> parametros = new HashMap<>();
                    parametros.put("limiteMaximoGratis", limiteMaximoGratis);
                    
                    //Avisar al oyente principal
                    EventBusManager.getInstancia().getBus()
                            .post(new Evento(CodigoEvento.BOLASEXTRA.getValue(),parametros));
                }
            }
            
        });
    }

    private void initComboBoxes() {
        comboCosto.getItems().clear();
        comboCosto.setConverter(new StringConverter<Integer>() {
            @Override
            public String toString(Integer object) {
                return object + "% del premio mayor";
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string.split("%")[0]);
            }
        });
        
        for (int i = 0; i < 9; i++) {
            comboCosto.getItems().add((i + 1) * 10);
        }
        
        comboCosto.setOnAction(e -> {
            if (comboCosto.getSelectionModel().getSelectedIndex() > -1) {
                Map<String,Object> parametros = new HashMap<>();
                parametros.put("porcentajeDeCostoDeBolaExtraSegunPremioMayor",  comboCosto.getSelectionModel().getSelectedItem() * 0.01);
                parametros.put("indiceConfiguracionCostoBolaExtra", comboCosto.getSelectionModel().getSelectedIndex());
                //Avisar al oyente
                EventBusManager.getInstancia().getBus()
                        .post(new Evento(CodigoEvento.BOLASEXTRA.getValue(), parametros));
            }
        });
        
        try {
            comboCosto.getSelectionModel().select(PersistenciaJson.getInstancia().getConfiguracion().getIndiceConfiguracionCostoBolaExtra());
        } catch (IOException ex) {
            Logger.getLogger(BolasExtraController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initCheckBoxes() {
       utilizarUmbralCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                utilizarUmbral = newValue;
                umbralText.setDisable(!newValue);
                
                //Avisar al oyente principal de este cambio
                Map<String,Object> parametros = new HashMap<>();
                parametros.put("utilizarUmbral",newValue);
                parametros.put("umbralParaLiberarBolasExtra",umbralParaLiberarBolasExtra);
                
                EventBusManager.getInstancia().getBus()
                        .post(new Evento(CodigoEvento.BOLASEXTRA.getValue(), parametros));
            }
        });
       
       bolaGratisCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                //Cambiar el estado de los textfields limites
                limiteMinimoBolaGratis.setDisable(!newValue);
                limiteMaximoBolaGratis.setDisable(!newValue);
                
                if (newValue) {
                    
                    if (limiteMinimoBolaGratis.getText().isEmpty()) {
                       limiteMinimoGratis = 0; 
                    }
                    else{
                        limiteMinimoGratis = Integer.valueOf(limiteMinimoBolaGratis.getText());
                    }
                    if (limiteMaximoBolaGratis.getText().isEmpty()) {
                        limiteMaximoGratis = 0;
                    }
                    else{
                        limiteMaximoGratis = Integer.valueOf(limiteMaximoBolaGratis.getText());
                    }
                }
                
                //Avisar al oyente principal
                Map<String,Object> parametros = new HashMap<>();
                parametros.put("limiteMinimoGratis", limiteMinimoGratis);
                parametros.put("limiteMaximoGratis", limiteMaximoGratis);
                parametros.put("utilizarBolasExtraGratis", bolaGratisCheck.isSelected());
                
                EventBusManager.getInstancia().getBus()
                        .post(new Evento(CodigoEvento.BOLASEXTRA.getValue(),parametros));
                
                //Debug
//                System.out.println("Minimo: " + limiteMinimoGratis);
//                System.out.println("Maximo: " + limiteMaximoGratis);
            }
            
        });
    }
    
}
