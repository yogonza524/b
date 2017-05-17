/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.controladores;

import com.bingo.persistencia.Conexion;
import com.core.bingosimulador.Juego;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.guava.core.EventBusManager;
import com.protocol.b1.main.MainApp;
import com.protocol.b1.servidor.Servidor;
import static com.protocol.b1.servidor.Servidor.B1FX;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

/**
 * FXML Controller class
 *
 * @author Gonzalo
 */
public class B1Controller implements Initializable {

    @FXML private Label creditos;
    @FXML private Label apuestaTotal;
    @FXML private Label apuestaIndividual;
    @FXML private Label cartonesHabilitados;
    @FXML private Label ganancia;
    @FXML private Button ejecutarB1;
    @FXML private Button detenerB1;
    
    @FXML private GridPane carton1;
    @FXML private GridPane carton2;
    @FXML private GridPane carton3;
    @FXML private GridPane carton4;
    
    private Servidor serverGame;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initConfig();
        initButtons();
        initComboBoxes();
        initTexts();
        initOthers();
    }    

    private void initConfig() {
        //Registro el oyente de eventos
        EventBusManager.getInstancia().getBus().register(this);
        
        //Habilito el modo B1 en FX
        B1FX = true;
    }

    private void initButtons() {
        ejecutarB1.setOnAction(e -> {
            
            Platform.runLater(() -> {
                try {
                    serverGame = new Servidor(null);
                    Servidor.B1FX = true; //Habilito la bandera para los eventos FX

                    serverGame.iniciar();

                    ejecutarB1.setDisable(true);
                } catch (IOException ex) {
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        });
    }

    private void initComboBoxes() {
        
    }

    private void initTexts() {
        
    }

    private void initOthers() {
        
    }
    
    @Subscribe
    private void mostrarCreditos(MostrarCreditos evento){
        Platform.runLater(() -> {
            try {
                List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT creditos FROM juego LIMIT 1");
                if (query != null && !query.isEmpty()) {
                    String cred = query.get(0).get("creditos").toString();
                    creditos.setText(cred);
                }
            } catch (SQLException ex) {
                Logger.getLogger(B1Controller.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }
    
    @Subscribe
    private void actualizarDatosLuegoDeJugar(EventoJugar evento){
        mostrarCreditos(null);
    }
    
    @Subscribe
    private void configurar(EventoConfigurar evento){
        Platform.runLater(() -> {
            Juego bingo = evento.getBingo();
            
            creditos.setText(bingo.getCreditos() + "");
            apuestaTotal.setText(bingo.apuestaTotal() + "");
            apuestaIndividual.setText(bingo.apuestaIndividual() + "");
            cartonesHabilitados.setText(bingo.habilitados() + "");
            ganancia.setText(bingo.ganancias() + "");
            
            //Cargar los cartones
            cargarCarton(bingo.getCartones()[0], carton1);
            cargarCarton(bingo.getCartones()[1], carton2);
            cargarCarton(bingo.getCartones()[2], carton3);
            cargarCarton(bingo.getCartones()[3], carton4);
        });
    }

    private void cargarCarton(int[][] cartonJuego, GridPane carton) {
        carton.getChildren().clear();
        
        //Carga
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 5; j++) {
                Label l = new Label(cartonJuego[i][j] + "");
                l.setContentDisplay(ContentDisplay.TOP);
                l.setAlignment(Pos.CENTER);
                l.setWrapText(true);
                l.setStyle("-fx-border-color: white;");
                carton.add(l, j, i);
            }
        }
    }
    
    @Subscribe
    private void cambiarCartones(EventoCambiarCartones evento){
        if (evento != null) {
            Platform.runLater(() -> {
                cargarCarton(evento.cartones[0], carton1);
                cargarCarton(evento.cartones[1], carton2);
                cargarCarton(evento.cartones[2], carton3);
                cargarCarton(evento.cartones[3], carton4);
            });
        }
    }
    
    //Clases estaticas para el evento de oyente
    public static class MostrarCreditos{}
    public static class ApuestaTotal{}
    public static class CartonesHabilitados{}
    public static class IdiomaActual{}
    public static class DenominacionActual{}
    public static class Cartones{}
    public static class TablaDePagos{}
    public static class Acumulado{}
    public static class HayJackpot{}
    public static class Bonus{}
    
    //Eventos
    public static class EventoJugar{}
    public static class EventoConfigurar{
        
        private Juego bingo;
        
        public EventoConfigurar(Juego bingo){
            this.bingo = bingo;
        }
        
        public Juego getBingo(){return this.bingo;}
    }
    
    public static class PaqueteRecibido{}

    public static class EventoCambiarCartones {

        private int[][][] cartones;
        
        public EventoCambiarCartones(int[][][] cartones) {
            this.cartones = cartones;
        }
        
        public int[][][] getCartones(){return this.cartones;}
    }
}
