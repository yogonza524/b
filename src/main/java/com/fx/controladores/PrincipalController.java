/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fx.controladores;

import com.bingo.entidades.FiguraPago;
import com.bingo.entidades.TablaDePago;
import com.core.bingosimulador.Juego;
import com.google.common.eventbus.Subscribe;
import com.guava.core.EventBusManager;
import com.guava.core.Evento;
import com.persistencia.PersistenciaJson;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import javafx.util.converter.FormatStringConverter;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
public class PrincipalController implements Initializable{
    
    //Figura de pago, barra lateral derecha
    private Pane[][] paneles;
    private int[][] casillas;
    
    //Tabla de pagos
    private String rutaTablaDePagosEnDisco;
    private List<TablaDePago> tabla;
    
    @FXML private ComboBox<TablaDePago> comboTablaPagos;
    @FXML private ComboBox<FiguraPago> comboFigurasPago;
    
    //Figura de pago seleccionada
    @FXML private Label nombreFiguraLabel;
    @FXML private TextField factorPagoFiguraTxt;
    @FXML private CheckBox bonusCheckFigura;
    
    @FXML private Pane p1;
    @FXML private Pane p2;
    @FXML private Pane p3;
    @FXML private Pane p4;
    @FXML private Pane p5;
    @FXML private Pane p6;
    @FXML private Pane p7;
    @FXML private Pane p8;
    @FXML private Pane p9;
    @FXML private Pane p10;
    @FXML private Pane p11;
    @FXML private Pane p12;
    @FXML private Pane p13;
    @FXML private Pane p14;
    @FXML private Pane p15;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventBusManager.getInstancia().getBus().register(this);
        EventBusManager.getInstancia().getBus().post(new Evento(100,null));
        EventBusManager.getInstancia().getBus().post(new Evento(102,null));
        
        initFiguraDePago();
        initComboBoxes();
        initCheckBoxes();
    }
    
    @Subscribe
    private void persistirTablaDePagos(Evento e){
        if (e.getCodigo() == 101 && comboTablaPagos.getSelectionModel().getSelectedIndex() > -1
                && comboFigurasPago.getSelectionModel().getSelectedIndex() > -1) {
            try {
                tabla.get(comboTablaPagos.getSelectionModel().getSelectedIndex())
                        .getFiguras().get(comboFigurasPago.getSelectionModel().getSelectedIndex())
                        .setCasillas(casillas);
                PersistenciaJson.getInstancia()
                        .persistir(tabla);
            } catch (IOException ex) {
                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Subscribe
    private void cargarTablaDePagos(Evento e){
        if (e.getCodigo() == 102) {
            comboTablaPagos.getItems().clear();
            
            comboTablaPagos.setConverter(new StringConverter<TablaDePago>() {
                @Override
                public String toString(TablaDePago object) {
                    return object.getNumero() + "";
                }

                @Override
                public TablaDePago fromString(String string) {
                    for (int i = 0; i < tabla.size(); i++) {
                        if ((tabla.get(i).getNumero() + "").equals(string)) {
                            return tabla.get(i);
                        }
                    }
                    return null;
                }
            });
            
            comboFigurasPago.setConverter(new StringConverter<FiguraPago>() {
                @Override
                public String toString(FiguraPago object) {
                    return object.getNombre();
                }

                @Override
                public FiguraPago fromString(String string) {
                    return comboFigurasPago.getSelectionModel().getSelectedItem();
                }
            });
            
            try {
                tabla = PersistenciaJson.getInstancia().tablasDePago();
            } catch (FileNotFoundException ex) {
                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            tabla.stream().forEach(tablero -> {
                comboTablaPagos.getItems().add(tablero);
            });
            
            //Selecciono el primer tablero y lo muestro
            comboTablaPagos.getSelectionModel().selectFirst();
            
            //Borro el combo de las figuras
            comboFigurasPago.getItems().clear();
            
            comboTablaPagos.getSelectionModel().getSelectedItem().getFiguras().stream()
                    .forEach(figura -> {
                        comboFigurasPago.getItems().add(figura);
                    });
        }
    }
    
    private void initPaneles() {
        
        paneles = new Pane[3][5];
        casillas = new int[3][5];
        
        paneles[0][0] = p1;
        paneles[0][1] = p2;
        paneles[0][2] = p3;
        paneles[0][3] = p4;
        paneles[0][4] = p5;
        paneles[1][0] = p6;
        paneles[1][1] = p7;
        paneles[1][2] = p8;
        paneles[1][3] = p9;
        paneles[1][4] = p10;
        paneles[2][0] = p11;
        paneles[2][1] = p12;
        paneles[2][2] = p13;
        paneles[2][3] = p14;
        paneles[2][4] = p15;
        
        for (int i = 0; i < paneles.length; i++) {
            for (int j = 0; j < paneles[0].length; j++) {
                cambiarCasilla(paneles[i][j],i,j);
            }
        }
    }
    
    private void tooglePane(final Pane p, final int i, final int j) {
        p.setStyle("-fx-background-color: white;");
    }
    
    private void borrarPaneles(Pane[][] paneles){
        for (int i = 0; i < paneles.length; i++) {
            for (int j = 0; j < paneles[0].length; j++) {
                paneles[i][j].setStyle("-fx-background-color: #CCC; -fx-border-width: 1;");
            }
        }
    }
    
    private void mostrarCartonTablero(int[][] figuraPago){
        this.casillas = figuraPago;
        
        for (int i = 0; i < paneles.length; i++) {
            for (int j = 0; j < paneles[0].length; j++) {
                paneles[i][j].setStyle("-fx-border-color:white;-fx-border-width: 1;-fx-background-color: #CCC;");
            }
        }
        
//        paneles[0][0].setStyle("-fx-border-color:white;-fx-border-width: 2;");
    }

    private void initFiguraDePago() {
        initPaneles();
        mostrarCartonTablero(casillas);
    }

    private void initComboBoxes() {
        comboTablaPagos.setOnAction(e -> {
            comboFigurasPago.getItems().clear();
            
            comboFigurasPago.getItems().addAll(comboTablaPagos.getSelectionModel().getSelectedItem().getFiguras());
        });
        
        comboFigurasPago.setOnAction(e -> {
            FiguraPago figura = comboFigurasPago.getSelectionModel().getSelectedItem();
            this.casillas = figura.getCasillas();
            borrarPaneles(paneles);
            //Pinto los casilleros
            for (int i = 0; i < paneles.length; i++) {
                for (int j = 0; j < paneles[0].length; j++) {
                    if (casillas[i][j] == 1) {
                        paneles[i][j].setStyle("-fx-border-color:white;-fx-border-width: 1; -fx-background-color: black;");
                    }
                    else{
                        paneles[i][j].setStyle("-fx-border-color:white;-fx-border-width: 1; -fx-background-color: #CCC;");
                    }
                }
            }
            
            nombreFiguraLabel.setText(figura.getNombre());
            factorPagoFiguraTxt.setText(figura.getFactorGanancia() + "");
            bonusCheckFigura.setSelected(figura.isEsBonus());
        });
    }
    
    private void cambiarCasilla(final Pane p, final int i, final int j) {
        p.setStyle("-fx-background-color: white;");
        p.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
            @Override
            public void handle(javafx.scene.input.MouseEvent t) {
                if (casillas[i][j] == 0) {
                    p.setStyle("-fx-border-color:white;-fx-border-width: 1;-fx-background-color: black;");
                    casillas[i][j] = 1;
                }
                else{
                    p.setStyle("-fx-background-color: #CCC;-fx-border-color:white;-fx-border-width: 1;");
                    casillas[i][j] = 0;
                }
                EventBusManager.getInstancia()
                        .getBus().post(new Evento(101,null));
            }
        });
    }

    private void initCheckBoxes() {
        bonusCheckFigura.setOnAction(e -> {
            int indiceTabla = comboTablaPagos.getSelectionModel().getSelectedIndex();
            int indiceFigura = comboFigurasPago.getSelectionModel().getSelectedIndex();
            if (indiceTabla > -1 && indiceFigura > -1) {
                tabla.get(indiceTabla).getFiguras().get(indiceFigura).setEsBonus(bonusCheckFigura.isSelected());
                EventBusManager.getInstancia().getBus().post(new Evento(101,null));
            }
        });
    }
}
