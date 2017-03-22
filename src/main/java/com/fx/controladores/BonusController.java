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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
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

    @FXML private VBox bonusFijoPane;
    @FXML private VBox bonusVariablePane;
    @FXML private CheckBox utilizarPremiosFijosCheck;
    @FXML private CheckBox utilizarPremiosVariablesCheck;
    @FXML private TextField cantidadDePremiosTxt;
    @FXML private Button cerrarBtn;
    
    private boolean premiosFijos;
    private boolean premiosVariables;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initConfig();
        initCheckBox();
        initButtons();
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
                
                PrincipalController.setUtilizarPremiosFijosEnBonus(newValue);
                PrincipalController.setUtilizarPremiosVariablesEnBonus(!newValue);
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
                
                PrincipalController.setUtilizarPremiosFijosEnBonus(!newValue);
                PrincipalController.setUtilizarPremiosVariablesEnBonus(newValue);
            }
        });
    }

    private void initConfig() {
        premiosFijos = PrincipalController.isUtilizarPremiosFijosEnBonus();
        premiosVariables = PrincipalController.isUtilizarPremiosVariablesEnBonus();
        
        bonusFijoPane.setVisible(premiosFijos);
        bonusVariablePane.setVisible(premiosVariables);
        utilizarPremiosFijosCheck.setSelected(premiosFijos);
        utilizarPremiosVariablesCheck.setSelected(premiosVariables);
    }

    private void initButtons() {
        cerrarBtn.setOnAction(e -> {
            Stage stage = (Stage)cerrarBtn.getScene().getWindow();
            stage.close();
        });
    }
    
}
