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
    @FXML private TextField frecuenciaFijosTxt;
    @FXML private TextField frecuenciaVariablesTxt;
    @FXML private Button cerrarBtn;
    @FXML private Button eliminarFijo;
    @FXML private Button eliminarVariable;
    @FXML private Button agregarFijoBtn;
    @FXML private Button agregarVariableBtn;
    @FXML private ListView listaFija;
    @FXML private ListView listaVariable;
    
    private boolean premiosFijos;
    private boolean premiosVariables;
    
    private Map<Integer,Integer> premiosFijosMap;
    private Map<Integer,Integer> premiosVariablesMap;
    
    
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
        
        premiosFijosMap = config.getPremiosFijosBonus();
        premiosVariablesMap = config.getPremiosVariablesBonus();
        
        for(Map.Entry<Integer,Integer> premio : premiosFijosMap.entrySet()){
            listaFija.getItems().add(premio.getKey() + "(frecuencia = " + premio.getValue() + ")");
        }
        
        for(Map.Entry<Integer,Integer> premio : premiosVariablesMap.entrySet()){
            listaVariable.getItems().add(premio.getKey() + "(frecuencia = " + premio.getValue() + ")");
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
            
            if (frecuenciaFijosTxt.getText().isEmpty()) {
                return;
            }
            
            int totalPremios = Integer.valueOf(cantidadDePremiosTxt.getText());
            
            //Verificar si se puede agregar
            int frecuenciaTotal = 0;
            for(Map.Entry<Integer,Integer> entry : premiosFijosMap.entrySet()){
                frecuenciaTotal += entry.getValue();
            }
            
            int frecuencia = Integer.valueOf(frecuenciaFijosTxt.getText());
            
            if (frecuenciaTotal + frecuencia >= totalPremios) { 
                if (frecuenciaTotal > totalPremios) {
                    Dialog.error("Error al agregar", "La cantidad total de premios no puede ser\n mayor a " + frecuenciaTotal, "Se requiere al menos un premio \ncon valor cero para salir del bonus");
                }
                else{
                    Dialog.error("Error al agregar", "La cantidad total de premios no puede ser\n mayor a " + (totalPremios - 1), "Se requiere al menos un premio \ncon valor cero para salir del bonus");
                }
                return;
            }
            
            int creditos = Integer.valueOf(creditosFijosTxt.getText());
            
            premiosFijosMap.put(creditos, frecuencia);
            
            Map<String,Object> params = new HashMap<>();
            params.put("premiosFijos", premiosFijosMap);

            EventBusManager.getInstancia().getBus()
                    .post(new Evento(CodigoEvento.BONUS.getValue(), params));
            
            listaFija.getItems().clear();
            
            for(Map.Entry<Integer,Integer> entry : premiosFijosMap.entrySet()){
                listaFija.getItems().add(entry.getKey() + "(frecuencia = " + entry.getValue() + ")");
            }
            
            
            creditosFijosTxt.setText("");
            frecuenciaFijosTxt.setText("");
            
        });
        
        agregarVariableBtn.setOnAction(e -> {
            if (creditosVariablesTxt.getText().isEmpty()) {
                return;
            }
            
            if (frecuenciaVariablesTxt.getText().isEmpty()) {
                return;
            }
            
            int totalPremios = Integer.valueOf(cantidadDePremiosTxt.getText());
            //Verificar si se puede agregar
            int frecuenciaTotal = 0;
            for(Map.Entry<Integer,Integer> entry : premiosVariablesMap.entrySet()){
                frecuenciaTotal += entry.getValue();
            }
            
            int frecuencia = Integer.valueOf(frecuenciaVariablesTxt.getText());
            
            if (frecuenciaTotal + frecuencia >= totalPremios) { 
                if (frecuenciaTotal > totalPremios) {
                    Dialog.error("Error al agregar", "La cantidad total de factores no puede ser\n mayor a " + frecuenciaTotal, "Se requiere al menos un premio \ncon valor cero para salir del bonus");
                }
                else{
                    Dialog.error("Error al agregar", "La cantidad total de factores no puede ser\n mayor a " + (totalPremios - 1), "Se requiere al menos un premio \ncon valor cero para salir del bonus");
                }
                return;
            }
            
            int creditos = Integer.valueOf(creditosVariablesTxt.getText());
            
            premiosVariablesMap.put(creditos, frecuencia);
            
            Map<String,Object> params = new HashMap<>();
            params.put("premiosFijos", premiosVariablesMap);

            EventBusManager.getInstancia().getBus()
                    .post(new Evento(CodigoEvento.BONUS.getValue(), params));
            
            listaVariable.getItems().clear();
            
            for(Map.Entry<Integer,Integer> entry : premiosVariablesMap.entrySet()){
                listaVariable.getItems().add(entry.getKey() + "(frecuencia = " + entry.getValue() + ")");
            }
            
            creditosVariablesTxt.setText("");
            frecuenciaVariablesTxt.setText("");
            
        });
        
        eliminarFijo.setOnAction(e -> {
            if (listaFija.getSelectionModel().getSelectedIndex() > -1) {
                 
                Integer value = Integer.valueOf(listaFija.getSelectionModel().getSelectedItem().toString().split("\\(")[0]);
                premiosFijosMap.remove(value);
                listaFija.getItems().remove(listaFija.getSelectionModel().getSelectedIndex());
                
                Map<String,Object> params = new HashMap<>();
                params.put("premiosFijos", premiosFijosMap);
                
                EventBusManager.getInstancia().getBus()
                        .post(new Evento(CodigoEvento.BONUS.getValue(), params));
            }
        });
        
        eliminarVariable.setOnAction(e -> {
            if (listaVariable.getSelectionModel().getSelectedIndex() > -1) {  
                Integer value = Integer.valueOf(listaVariable.getSelectionModel().getSelectedItem().toString().split("\\(")[0]);
                premiosVariablesMap.remove(value);
                listaVariable.getItems().remove(listaVariable.getSelectionModel().getSelectedIndex());
                
                Map<String,Object> params = new HashMap<>();
                params.put("premiosVariables", premiosVariablesMap);
                
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
                    
                    int total = cantidadDePremiosTxt.getText().isEmpty() ? 0 : Integer.valueOf(cantidadDePremiosTxt.getText());
                    int cantidadDeFijos = 0;
                    int cantidadDeVariables = 0;
                    
                    for(Map.Entry<Integer,Integer> entry : premiosFijosMap.entrySet()){
                        cantidadDeFijos += entry.getValue();
                    }
                    
                    for(Map.Entry<Integer,Integer> entry : premiosVariablesMap.entrySet()){
                        cantidadDeVariables += entry.getValue();
                    }
                    
                    int mayor = cantidadDeFijos > cantidadDeVariables ? cantidadDeFijos : cantidadDeVariables;
                    
                    if (total < mayor) {
                        total = mayor + 1;
                    }
                    
                    
                    Map<String,Object> params = new HashMap<>();
                    params.put("cantidadDePremiosBonus", cantidadDePremiosTxt.getText().isEmpty() ? mayor : total);

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
        
        frecuenciaFijosTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (frecuenciaFijosTxt.isFocused()) {
                    if (!newValue.matches("\\d*")) {
                        frecuenciaFijosTxt.setText(newValue.replaceAll("[^\\d]", ""));
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
        
        frecuenciaVariablesTxt.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (frecuenciaVariablesTxt.isFocused()) {
                    if (!newValue.matches("\\d*")) {
                        frecuenciaVariablesTxt.setText(newValue.replaceAll("[^\\d]", ""));
                        return;
                    }
                }
            }
        });
    }
    
}
