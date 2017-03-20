/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fx.controladores;

import com.fx.main.MainApp;
import com.fx.util.Util;
import com.guava.core.EventBusManager;
import com.guava.core.Evento;
import java.net.URL;
import java.util.ResourceBundle;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
public class SplashController  implements Initializable {

    @FXML private AnchorPane base;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new SplashScreen().start();
    }
    
    class SplashScreen extends Thread{

        @Override
        public void run() {
            try {
                Thread.sleep(MainApp.getTiempoDeVidaSplash());
                
                Platform.runLater(() -> {
//                    base.getScene().getWindow().hide();
                    MainApp.getStage().hide();
                    EventBusManager.getInstancia().getBus().post(new Evento(2,null));
                });
                
            } catch (InterruptedException ex) {
                System.exit(0);
            }
        }
    }
}
