/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.fx.controladores;

import com.bingo.entidades.FiguraPago;
import com.bingo.entidades.TablaDePago;
import com.bingo.enumeraciones.CodigoEvento;
import com.bingo.fabricas.FiguraPagoFactoria;
import com.core.bingosimulador.Juego;
import com.fx.util.Dialog;
import com.google.common.eventbus.Subscribe;
import com.guava.core.EventBusManager;
import com.guava.core.Evento;
import com.persistencia.PersistenciaJson;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeDesign;
import eu.hansolo.medusa.TickLabelOrientation;
import eu.hansolo.medusa.skins.ModernSkin;
import eu.hansolo.medusa.skins.QuarterSkin;
import eu.hansolo.medusa.skins.SlimSkin;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import javafx.util.StringConverter;
import javafx.util.converter.FormatStringConverter;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
public class PrincipalController implements Initializable{
    
    //Juego
    private static Juego bingo;
    
    //Cómputo de resultados
    private BigInteger cantidadDeJuegosGanados;
    private BigInteger pagado;
    private BigInteger apostado;
    
    //Simular boton
    @FXML private ProgressBar progress;
    
    //Sensores
    private Gauge porcentajeDeRetribucionGauge;
    private Gauge porcentajeDeJuegosGanados;
    private Gauge porcentajeDeJuegosConBolasExtra;
    private Gauge PorcentajeDeJuegosEnCiclosDe5sinGanar;
    
    //Banderas
    
    //Figura de pago, barra lateral derecha
    private Pane[][] paneles;
    private int[][] casillas;
    
    //Tabla de pagos
    private String rutaTablaDePagosEnDisco;
    private List<TablaDePago> tabla;
    
    @FXML private ComboBox<TablaDePago> comboTablaPagos;
    @FXML private ComboBox<FiguraPago> comboFigurasPago;
    
    //Figura de pago seleccionada
    @FXML private TextField nombreFiguraTxt;
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
    
    @FXML private Pane porcentajeRetribucionPane;
    @FXML private Pane porcentajeGanadosPane;
    @FXML private Pane porcentajeJuegosBolasExtraPane;
    @FXML private Pane PorcentajeDeJuegosEnCiclosDe5sinGanarPane;
    @FXML private VBox panelVertical;
    
    @FXML private Hyperlink nuevoPremioLink;
    @FXML private Hyperlink eliminarLink;
    
    @FXML private MenuItem jugadorMenuItem;
    @FXML private MenuItem bonusMenuItem;
    @FXML private MenuItem bolasExtraMenuItem;
    @FXML private MenuItem tournamentMenuItem;
    
    @FXML private TextField simulacionesTxt;
    @FXML private Button simularBtn;
    
    //Graficos
    @FXML private LineChart linearChart;
    
    private BarChart frecuenciaDeTablero;
    
    private static boolean utilizarPremiosFijosEnBonus;
    private static boolean utilizarPremiosVariablesEnBonus;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventBusManager.getInstancia().getBus().register(this);
        EventBusManager.getInstancia().getBus().post(new Evento(100,null));
        EventBusManager.getInstancia().getBus().post(new Evento(102,null));
        
        initFiguraDePago();
        initComboBoxes();
        initCheckBoxes();
        initTextFields();
        initButtons();
        initMenu();
        initConfig();
        initComputos();
        
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
            
            System.out.println("Cargar la tabla de la base de datos");
            
            comboTablaPagos.getItems().clear();
            
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
            if (comboTablaPagos.getSelectionModel().getSelectedIndex() < 0) {
                comboTablaPagos.getSelectionModel().selectFirst();
            }
            
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

    public static boolean isUtilizarPremiosFijosEnBonus() {
        return utilizarPremiosFijosEnBonus;
    }

    public static void setUtilizarPremiosFijosEnBonus(boolean utilizarPremiosFijosEnBonus) {
        PrincipalController.utilizarPremiosFijosEnBonus = utilizarPremiosFijosEnBonus;
    }

    public static boolean isUtilizarPremiosVariablesEnBonus() {
        return utilizarPremiosVariablesEnBonus;
    }

    public static void setUtilizarPremiosVariablesEnBonus(boolean utilizarPremiosVariablesEnBonus) {
        PrincipalController.utilizarPremiosVariablesEnBonus = utilizarPremiosVariablesEnBonus;
    }
    
    private void tooglePane(final Pane p, final int i, final int j) {
        p.setStyle("-fx-background-color: white;");
    }
    
    private void borrarPaneles(Pane[][] paneles){
        for (int i = 0; i < paneles.length; i++) {
            for (int j = 0; j < paneles[0].length; j++) {
                paneles[i][j].setStyle("-fx-border-color:white;-fx-border-width: 1; -fx-background-color: #CCC;");
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
    }

    private void initFiguraDePago() {
        initPaneles();
        mostrarCartonTablero(casillas);
    }

    private void initComboBoxes() {
        
        comboTablaPagos.setConverter(new StringConverter<TablaDePago>() {
                @Override
                public String toString(TablaDePago object) {
                    return "Tablero " + object.getNumero();
                }

                @Override
                public TablaDePago fromString(String string) {
                    return comboTablaPagos.getSelectionModel().getSelectedItem();
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

            
        comboTablaPagos.valueProperty().addListener(new ChangeListener<TablaDePago>() {
            @Override public void changed(ObservableValue ov, TablaDePago t, TablaDePago t1) {
                
                comboFigurasPago.getItems().clear();
            
                if (comboTablaPagos.getSelectionModel().getSelectedIndex() > -1) {
                    comboFigurasPago.getItems().addAll(tabla.get(comboTablaPagos.getSelectionModel().getSelectedIndex()).getFiguras());
                
                    nombreFiguraTxt.setText("");
                    bonusCheckFigura.setSelected(false);
                    factorPagoFiguraTxt.setText("");
                }
                
                
                
                
            }    
        });
        
        comboFigurasPago.valueProperty().addListener(new ChangeListener<FiguraPago>() {
            @Override
            public void changed(ObservableValue<? extends FiguraPago> observable, FiguraPago oldValue, FiguraPago newValue) {
                
                if (comboFigurasPago.getSelectionModel().getSelectedIndex() > -1) {
                    FiguraPago figura = comboFigurasPago.getSelectionModel().getSelectedItem();
                    casillas = figura.getCasillas();
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

                    nombreFiguraTxt.setText(figura.getNombre());
                    factorPagoFiguraTxt.setText(figura.getFactorGanancia() + "");
                    bonusCheckFigura.setSelected(figura.isEsBonus());
                }
                else{
                    borrarPaneles(paneles);
                }
                
            }
            
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
                EventBusManager.getInstancia().getBus().post(new Evento(CodigoEvento.PERSISTIR.getValue(),null));
            }
        });
        
        //Bonus
        utilizarPremiosFijosEnBonus = true;
        utilizarPremiosVariablesEnBonus = false;
    }

    private void initTextFields() {
        nombreFiguraTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (nombreFiguraTxt.isFocused()) {
                int indiceTabla = comboTablaPagos.getSelectionModel().getSelectedIndex();
                int indiceFigura = comboFigurasPago.getSelectionModel().getSelectedIndex();

                if (indiceTabla > -1 && indiceFigura > -1 && !newValue.isEmpty()) {
                    PauseTransition pause = new PauseTransition(Duration.seconds(1));

                    pause.setOnFinished(event -> {
                        tabla.get(indiceTabla).getFiguras().get(indiceFigura)
                                .setNombre(newValue);

                        EventBusManager.getInstancia().getBus()
                                .post(new Evento(CodigoEvento.PERSISTIR.getValue(),null));

                        FiguraPago actual = comboFigurasPago.getValue();
                        actual.setNombre(newValue);
                        comboFigurasPago.getItems().set(indiceFigura, actual);

                    });
                    pause.playFromStart();
                }
            }
        });
        
        simulacionesTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                factorPagoFiguraTxt.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
        
        factorPagoFiguraTxt.textProperty().addListener((observable, oldValue, newValue) -> {
            if (factorPagoFiguraTxt.isFocused()) {
                if (!newValue.matches("\\d*")) {
                    factorPagoFiguraTxt.setText(newValue.replaceAll("[^\\d]", ""));
                    return;
                }

                int indiceTablero = comboTablaPagos.getSelectionModel().getSelectedIndex();
                int indiceFigura = comboFigurasPago.getSelectionModel().getSelectedIndex();

                if (indiceTablero > -1 && indiceFigura > -1) {
                    PauseTransition pause = new PauseTransition(Duration.seconds(1));

                    pause.setOnFinished(event -> {
                        tabla.get(indiceTablero).getFiguras().get(indiceFigura)
                                .setFactorGanancia(Integer.valueOf(factorPagoFiguraTxt.getText()));
                        EventBusManager.getInstancia().getBus()
                                .post(new Evento(CodigoEvento.PERSISTIR.getValue(),null));
                    });
                    pause.playFromStart();

                }
            }
        });
    }

    private void initButtons() {
        eliminarLink.setOnAction(e -> {
            int indiceTablero = comboTablaPagos.getSelectionModel().getSelectedIndex();
            int indiceFigura = comboFigurasPago.getSelectionModel().getSelectedIndex();
            
            if (indiceFigura > -1 && indiceTablero > -1) {
                String nombreFigura = comboFigurasPago.getSelectionModel().getSelectedItem().getNombre();
                if (Dialog.preguntaSiNo("Eliminar la figura " + nombreFigura, "Se eliminará permanentemente la figura", "¿Seguro desea continuar?")) {
                    tabla.get(indiceTablero).getFiguras().remove(indiceFigura);
                    comboTablaPagos.getSelectionModel().select(indiceTablero);
                    EventBusManager.getInstancia().getBus()
                            .post(new Evento(CodigoEvento.PERSISTIR.getValue(),null));
//                    EventBusManager.getInstancia().getBus()
//                            .post(new Evento(CodigoEvento.CARGAR.getValue(),null));
                    comboFigurasPago.getItems().remove(indiceFigura);
                }
            }
            else{
                Dialog.error("Seleccione un tablero y una figura", "Para proceder debe seleccionar alguna", "La eliminacion no puede continuar, retrocediendo....");
            }
        });
        
        nuevoPremioLink.setOnAction(e -> {
            int indiceTablero = comboTablaPagos.getSelectionModel().getSelectedIndex();
            int indiceFigura = comboFigurasPago.getSelectionModel().getSelectedIndex();
            
            if (indiceFigura > -1 && indiceTablero > -1) {
                FiguraPago nueva = new FiguraPago.FiguraPagoBuilder()
                        .nombre("figura-" + comboFigurasPago.getItems().size())
                        .numero(mayorNumero(tabla.get(indiceTablero)))
                        .crear();
                tabla.get(indiceTablero).getFiguras().add(nueva);
                comboFigurasPago.getItems().add(nueva);
                comboFigurasPago.getSelectionModel().select(nueva);
                
                //Persistir
                EventBusManager.getInstancia().getBus()
                        .post(new Evento(CodigoEvento.PERSISTIR.getValue(),null));
            }
            else{
                Dialog.error("Seleccione un tablero", "Para agregar una nueva figura se necesita un tablero", "Proceso cancelado, retrocediendo...");
            }
        });
        
        //Correr simulacion
        simularBtn.setOnAction(e -> {
            if (simulacionesTxt.getText() != null && !simulacionesTxt.getText().isEmpty()) {
                
                progress.setVisible(true);
                
                Task<Void> task = new Task<Void>() {
                    @Override protected Void call() throws Exception {
                        return null;
                    }
                };
                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent t) {
                        progress.setVisible(false);
                    }
                });

                progress.progressProperty().unbind();
                progress.progressProperty().bind(task.progressProperty());

                task.messageProperty().addListener(new ChangeListener<String>() {
                    public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                        progress.setProgress(Double.valueOf(newValue));
                    }
                });

                Thread th = new Thread(task);
                th.start(); 
            }
        });
    }

    private void initMenu() {
        
        jugadorMenuItem.setOnAction(e -> {
            ventana("/fxml/Jugador.fxml", "Perfiles de Jugador");
        });
        
        bonusMenuItem.setOnAction(e -> {
            ventana("/fxml/Bonus.fxml", "Bonus");
        });
        
        bolasExtraMenuItem.setOnAction(e -> {
            ventana("/fxml/BolasExtra.fxml", "Bolas Extra");
        });
        
        tournamentMenuItem.setOnAction(e -> {
            ventana("/fxml/Tournament.fxml", "Tournament");
        });
    }

    
    private void ventana(String fxml, String titulo){
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            Parent root = null;
            try {
                root = (Parent) loader.load();
            } catch (IOException ex) {
                Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
            }

            Scene scene = new Scene(root);
            scene.getStylesheets().add("/styles/principal.css");

            Stage main = new Stage();
            main.initStyle(StageStyle.DECORATED);
            main.setTitle(titulo);
    //        icono(stage);
            main.getIcons().add(new Image("/img/icon.png"));
            main.initOwner(bonusCheckFigura.getScene().getWindow());
            main.initModality(Modality.APPLICATION_MODAL); 
            main.setResizable(false);
            main.setScene(scene);

            main.show();
        }

    public static Juego getBingo() {
        return bingo;
    }

    public static void setBingo(Juego bingo) {
        PrincipalController.bingo = bingo;
    }   

    private int mayorNumero(TablaDePago tabla) {
        int mayor = tabla.getFiguras().size() > 0 ? tabla.getFiguras().size() : -1;
        for(FiguraPago figura : tabla.getFiguras()){
            if (figura.getNumero() > mayor) {
                mayor = figura.getNumero();
            }
        }
        return mayor;
    }
    
    private void crearTermometro(Gauge temperatura, Pane panel, String titulo, String unidades){
        if (panel != null && panel.getChildren().contains(temperatura)) {
            panel.getChildren().remove(temperatura);
        }
          
//        temperatura.setSkin(new eu.hansolo.medusa.skins.ModernSkin(temperatura)); 
//        temperatura.setSkin(new SlimSkin(temperatura));
        temperatura.setSkin(new QuarterSkin(temperatura));
        temperatura.setTitle(titulo);  
        temperatura.setUnit(unidades);  
        temperatura.setDecimals(0); 
        temperatura.setPrefWidth(panel != null? panel.getPrefWidth() : 200.0);
        temperatura.setPrefHeight(panel != null? panel.getPrefHeight(): 200.0);
        temperatura.setValueColor(Color.BLACK);  
        temperatura.setTitleColor(Color.BLACK);  
        temperatura.setUnitColor(Color.BLACK);
        temperatura.setSubTitleColor(Color.WHITE);  
        temperatura.setBarColor(Color.rgb(0, 214, 215));  
        temperatura.setNeedleColor(Color.WHITE);  
        temperatura.setThresholdColor(Color.rgb(204, 0, 0));  
        temperatura.setTickLabelColor(Color.rgb(151, 151, 151));  
        temperatura.setTickMarkColor(Color.GREY);  
        temperatura.setTickLabelOrientation(TickLabelOrientation.ORTHOGONAL);

        if (panel != null) {
            panel.getChildren().add(temperatura);
        }
    }

    private void initConfig() {
        progress.setVisible(false);
        
        crearTermometro(porcentajeDeRetribucionGauge = new Gauge(), porcentajeRetribucionPane, "Retribución", "%");
        crearTermometro(porcentajeDeJuegosGanados = new Gauge(), porcentajeGanadosPane, "Ganados", "%");
        crearTermometro(porcentajeDeJuegosConBolasExtra = new Gauge(), porcentajeJuegosBolasExtraPane, "Con bolas extra", "%");
        crearTermometro(PorcentajeDeJuegosEnCiclosDe5sinGanar = new Gauge(), PorcentajeDeJuegosEnCiclosDe5sinGanarPane, "5 juegos sin ganar", "%");
        
        cargarBarChartTablero(panelVertical, frecuenciaDeTablero);
    }
    
    private void cargarBarChartTablero(VBox panelVertical, BarChart frecuenciaTablero){
        if (panelVertical.getChildren().contains(frecuenciaTablero)) {
            panelVertical.getChildren().remove(frecuenciaTablero);
        }
        
        CategoryAxis xAxis    = new CategoryAxis();
        xAxis.setLabel("Figuras");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Frecuencia");
        
        frecuenciaTablero = new BarChart(xAxis, yAxis);
        frecuenciaTablero.setPrefHeight(200);
        
        panelVertical.getChildren().add(frecuenciaTablero);
    }
    
    private void simular(int n){
        
        int[][][] figuras = obtenerFiguras(tabla.get(comboTablaPagos.getSelectionModel().getSelectedIndex()).getFiguras());
        int acumulado = 10000;
        boolean generarNuevoBolillero = true;
        
        for (int i = 0; i < n; i++) {
            bingo = new Juego();
            bingo.setFigurasDePago(figuras);
            bingo.setAcumulado(acumulado);
            
            
            //Jugar
            bingo.jugar(generarNuevoBolillero);
            
            //Computar resultados;
            apostado = apostado.add(BigInteger.valueOf(bingo.apuestaTotal()));
        }
    }

    private int[][][] obtenerFiguras(List<FiguraPago> figuras) {
        int[][][] result = new int[figuras.size()][Juego.getLineas()][Juego.getColumnas()];
        for (int i = 0; i < figuras.size(); i++) {
            result[i] = figuras.get(i).getCasillas();
        }
        return result;
    }

    private void initComputos() {
        apostado = BigInteger.ZERO;
        pagado = BigInteger.ZERO;
        cantidadDeJuegosGanados = BigInteger.ZERO;
    }
}
