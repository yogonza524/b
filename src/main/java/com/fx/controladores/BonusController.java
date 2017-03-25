/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fx.controladores;

import com.bingo.enumeraciones.CodigoEvento;
import com.fx.util.Dialog;
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
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
public class BonusController implements Initializable{

    @FXML private AnchorPane bonus_pane;
    @FXML private VBox bonusFijoPane;
    @FXML private VBox bonusVariablePane;
    @FXML private CheckBox utilizarPremiosFijosCheck;
    @FXML private CheckBox utilizarPremiosVariablesCheck;
    @FXML private TextField cantidadDePremiosTxt;
    @FXML private TextField creditosFijosTxt;
    @FXML private TextField creditosVariablesTxt;
    @FXML private Button cerrarBtn;
    @FXML private Button eliminarFijo;
    @FXML private Button eliminarVariable;
    @FXML private Button agregarFijoBtn;
    @FXML private Button agregarVariableBtn;
    @FXML private ListView listaFija;
    @FXML private ListView listaVariable;
    
    private boolean premiosFijos;
    private boolean premiosVariables;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            initConfig();
        } catch (IOException ex) {
            Logger.getLogger(BonusController.class.getName()).log(Level.SEVERE, null, ex);
        }
        initCheckBox();
        initButtons();
        initTextFields();
    }

    private void initCheckBox() {
        
//        bonusFijoPane.setManaged(false);
//        bonusVariablePane.setManaged(false);
        
        utilizarPremiosFijosCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                bonusFijoPane.setVisible(newValue);
                bonusVariablePane.setVisible(!newValue);
                utilizarPremiosVariablesCheck.setSelected(!newValue);
                bonusFijoPane.setManaged(true);
                bonusVariablePane.setManaged(false);
                
                Map<String,Object> params = new HashMap<>();
                params.put("utilizarPremiosFijos", newValue);
                params.put("utilizarPremiosVariables", !newValue);
                
                EventBusManager.getInstancia().getBus()
                        .post(new Evento(CodigoEvento.BONUS.getValue(), params));
            }
        });
        
        utilizarPremiosVariablesCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                bonusFijoPane.setVisible(!newValue);
                bonusVariablePane.setVisible(newValue);
                
                utilizarPremiosFijosCheck.setSelected(!newValue);
                bonusVariablePane.setManaged(true);
                bonusFijoPane.setManaged(false);
                
                Map<String,Object> params = new HashMap<>();
                params.put("utilizarPremiosVariables", newValue);
                
                EventBusManager.getInstancia().getBus()
                        .post(new Evento(CodigoEvento.BONUS.getValue(), params));
            }
        });
    }

    private void initConfig() throws IOException {
        ConfiguracionPersistencia config = PersistenciaJson.getInstancia().getConfiguracion();
        
        premiosFijos = config.isUtilizarPremiosFijosBonus();
        premiosVariables = config.isUtilizarPremiosVariablesBonus();
        
        bonusFijoPane.setVisible(premiosFijos);
        bonusVariablePane.setVisible(premiosVariables);
        utilizarPremiosFijosCheck.setSelected(premiosFijos);
        utilizarPremiosVariablesCheck.setSelected(premiosVariables);
        
        cantidadDePremiosTxt.setText(config.getCantidadDePremiosEnBonus() + "");
        
        //Cargar las listas
        listaFija.getItems().clear();
        listaVariable.getItems().clear();
        
        Integer[] fijos = config.getPremiosFijosBonus();
        Integer[] variables = config.getPremiosVariablesBonus();
        
        for (int i = 0; i < fijos.length; i++) {
            listaFija.getItems().add(fijos[i]);
        }
        
        for (int i = 0; i < variables.length; i++) {
            listaVariable.getItems().add(variables[i]);
        }
        
        bonus_pane.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ESCAPE) {
                    Stage stage = (Stage)cerrarBtn.getScene().getWindow();
                    stage.close();
                }
            }
        });
    }

    private void initButtons() {
        cerrarBtn.setOnAction(e -> {
            Stage stage = (Stage)cerrarBtn.getScene().getWindow();
            stage.close();
        });
        
        agregarFijoBtn.setOnAction(e -> {
            if (creditosFijosTxt.getText().isEmpty()) {
                return;
            }
            
            int totalPremios = Integer.valueOf(cantidadDePremiosTxt.getText());
            
            if (listaFija.getItems().size() >= totalPremios - 1 ) {
                Dialog.error("No se puede agregar", "La cantidad de premios no puede exceder el total de premios menos uno (condicion de salida: premio con valor 0)", "Elimine premios e intentelo de nuevo");
                return;
            }
            
            int creditos = Integer.valueOf(creditosVariablesTxt.getText());
            boolean existe = false;
            
            for (int i = 0; i < listaFija.getItems().size(); i++) {
                if (creditos == Integer.valueOf(listaFija.getItems().get(i).toString())) {
                    existe = true;
                    break;
                }
            }
            
            if (!existe) {
                listaFija.getItems().add(creditos);
                creditosFijosTxt.setText("");
                
                Map<String,Object> params = new HashMap<>();
                params.put("premiosFijos", listaFija.getItems().toArray());
                
                EventBusManager.getInstancia().getBus()
                        .post(new Evento(CodigoEvento.BONUS.getValue(), params));
            }
            
        });
        
        agregarVariableBtn.setOnAction(e -> {
            if (creditosVariablesTxt.getText().isEmpty()) {
                return;
            }
            
            int totalPremios = Integer.valueOf(cantidadDePremiosTxt.getText());
            
            if (listaVariable.getItems().size() >= totalPremios - 1 ) {
                Dialog.error("No se puede agregar", "La cantidad de factores no puede exceder el \ntotal de premios menos uno (condicion de salida: premio con valor 0)", "Elimine premios e intentelo de nuevo");
                return; 
            }
            
            int creditos = Integer.valueOf(creditosVariablesTxt.getText());
            boolean existe = false;
            
            for (int i = 0; i < listaVariable.getItems().size(); i++) {
                if (creditos == Integer.valueOf(listaVariable.getItems().get(i).toString())) {
                    existe = true;
                    break;
                }
            }
            
            if (!existe) {
                listaVariable.getItems().add(creditos);
                creditosVariablesTxt.setText("");
                
                Map<String,Object> params = new HashMap<>();
                params.put("premiosVariables", listaVariable.getItems().toArray());
                
                EventBusManager.getInstancia().getBus()
                        .post(new Evento(CodigoEvento.BONUS.getValue(), params));
            }
            
            
            
        });
        
        eliminarFijo.setOnAction(e -> {
            if (listaFija.getSelectionModel().getSelectedIndex() > -1) {
                listaFija.getItems().remove(listaFija.getSelectionModel().getSelectedIndex());
                
                Map<String,Object> params = new HashMap<>();
                params.put("premiosFijos", listaFija.getItems().toArray());
                
                EventBusManager.getInstancia().getBus()
                        .post(new Evento(CodigoEvento.BONUS.getValue(), params));
            }
        });
        
        eliminarVariable.setOnAction(e -> {
            if (listaVariable.getSelectionModel().getSelectedIndex() > -1) {
                listaVariable.getItems().remove(listaVariable.getSelectionModel().getSelectedIndex());
                
                Map<String,Object> params = new HashMap<>();
                params.put("premiosVariables", listaVariable.getItems().toArray());
                
                EventBusManager.getInstancia().getBus()
                        .post(new Evento(CodigoEvento.BONUS.getValue(), params));
            }
        });
    }

    private void initTextFields() {
        cantidadDePremiosTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (cantidadDePremiosTxt.isFocused()) {
                    if (!newValue.matches("\\d*")) {
                        cantidadDePremiosTxt.setText(newValue.replaceAll("[^\\d]", ""));
                        return;
                    }
                    
                    if (cantidadDePremiosTxt.getText().isEmpty()) {
                        cantidadDePremiosTxt.setText("16");
                    }
                    
                    Map<String,Object> params = new HashMap<>();
                    params.put("cantidadDePremiosBonus", Integer.valueOf(cantidadDePremiosTxt.getText()));

                    EventBusManager.getInstancia().getBus()
                            .post(new Evento(CodigoEvento.BONUS.getValue(), params));
                }
            }
        });
        
        creditosFijosTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (creditosFijosTxt.isFocused()) {
                    if (!newValue.matches("\\d*")) {
                        creditosFijosTxt.setText(newValue.replaceAll("[^\\d]", ""));
                        return;
                    }
                }
            }
        });
        
        creditosVariablesTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (creditosVariablesTxt.isFocused()) {
                    if (!newValue.matches("\\d*")) {
                        creditosVariablesTxt.setText(newValue.replaceAll("[^\\d]", ""));
                        return;
                    }
                }
            }
        });
    }
    
}
