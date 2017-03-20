/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fx.main;

import com.fx.util.Util;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.guava.core.EventBusManager;
import com.guava.core.Evento;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
public class MainApp extends Application{
    
    private final static int TIEMPODEVIDASPLASH = 2000;
    private static Stage stage;

    public static Stage getStage() {
        return stage;
    }
    
    public static int getTiempoDeVidaSplash() {
        return TIEMPODEVIDASPLASH;
    }
    @Override
    public void start(Stage stage) throws Exception {
        
        MainApp.stage = stage;
        
        EventBusManager.getInstancia().getBus().register(this);
        EventBusManager.getInstancia().getBus().post(new Evento(1,null));
    }

    public static void main(String[] args) {
        launch(args);
    }
    
    private void mostrarSplash(String titulo) throws IOException, InterruptedException{
//        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Splash.fxml"));
        Platform.setImplicitExit(false);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Splash.fxml"));
        Parent root = (Parent) loader.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle(titulo);
//        icono(stage);
//        stage.getIcons().add(new Image("/img/icon.png"));
        stage.setScene(scene);
        stage.show();
    }
    
    @Subscribe
    private void splash(Evento e) throws IOException, InterruptedException{
        if (e.getCodigo() == 1) {
            mostrarSplash("Bingo Simulador 1.0");
        }
    }
    
    @Subscribe
    private void principal(Evento e) throws IOException{
        if (e.getCodigo() == 2) {
            mostrarPrincipal("BingoBot Simulador 1.0");
        }
    }

    private void mostrarPrincipal(String titulo) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Principal.fxml"));
        Parent root = (Parent) loader.load();
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/principal.css");
        
        Stage main = new Stage();
        main.initStyle(StageStyle.DECORATED);
        main.setTitle(titulo);
//        icono(stage);
        main.getIcons().add(new Image("/img/icon.png"));
        main.setScene(scene);
        main.setMaximized(true);
        
        main.setOnCloseRequest(e -> {
            System.exit(0);
        });
        
        main.show();
        
        stage = main;
    }
}
