/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.protocol.b1.main;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Gonzalo
 */
//@SpringBootApplication(exclude = {JmxAutoConfiguration.class})
public class MainFx extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        iniciarApp();
    }
    
    public static void main(String[] args) {
        launch(args);
    }

    private void iniciarApp() throws IOException {
        
        String titulo = "B1 Protocol 1.0.0 for BingoBot Slot Game System";
        
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/B1.fxml"));
        Parent root = (Parent) loader.load();
        
        Scene scene = new Scene(root);
        
        Stage main = new Stage();
        main.initStyle(StageStyle.DECORATED);
        main.setTitle(titulo);
        main.setScene(scene);
        main.setMaximized(true);
        
        main.setOnCloseRequest(e -> {
            System.exit(0);
        });
        
        main.show();
    }
    
}
