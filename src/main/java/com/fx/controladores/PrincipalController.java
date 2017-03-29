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
import com.bingo.perfilesJugador.Perfil;
import com.bingo.rng.RNG;
import com.bingo.util.Matematica;
import com.core.bingosimulador.Juego;
import com.fx.util.Dialog;
import com.google.common.eventbus.Subscribe;
import com.guava.core.EventBusManager;
import com.guava.core.Evento;
import com.persistencia.ConfiguracionPersistencia;
import com.persistencia.PersistenciaJson;
import dnl.utils.text.table.TextTable;
import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeDesign;
import eu.hansolo.medusa.TickLabelOrientation;
import eu.hansolo.medusa.skins.ModernSkin;
import eu.hansolo.medusa.skins.QuarterSkin;
import eu.hansolo.medusa.skins.SlimSkin;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TreeMap;
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
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
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
import org.apache.commons.lang3.ArrayUtils;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
public class PrincipalController implements Initializable{
    
    //Juego
    private static Juego bingo;
    private Integer[] creditosMaximosPorPerfil;
    private Double[] probabilidadDeApostarPorPerfil;
    private Double[] probabilidadDeComprarBolasExtra;
    private int indiceConfiguracionJugadores;
    
    //Bonus
    private Map<Integer,Integer> premiosFijosBonus;
    private Map<Integer,Integer> premiosVariablesBonus;
    private boolean utilizarPremiosFijosBonus;
    private boolean utilizarPremiosVariablesBonus;
    private boolean utilizarBolasExtraGratis;
    private int cantidadDePremiosBonusFijo;
    private int cantidadDePremiosBonusVariable;
    private boolean modoDebug;
    
    //Cómputo de resultados
    private BigInteger cantidadDeJuegosGanados;
    private BigInteger pagado;
    private BigInteger apostado;
    private BigInteger juegosConBolasExtra;
    private boolean tournament;
    private double porcentajeParaTournament;
    private boolean usarUmbralParaLiberarBolasExtra;
    private int umbralParaLiberarBolasExtra;
    private double porcentajeDeCostoDeBolaExtraSegunPremioMayor;
    private int limiteMinimoGratis;
    private int limiteMaximoGratis;
    private double[] retribuciones; //Retribuciones parciales, para cada juego
    private Integer[] frecuenciaDePremiosObtenidosPorFigura;
    private double[] retribucionesParcialesAcumuladas;
    private BigInteger cantidadDeJuegosConBonus;
    private BigInteger cantidadGanadoEnBonus;
    
    private BigInteger[] pagadoSegunPerfil;
    private BigInteger[] apostadoSegunPerfil;
    private Integer[] frecuenciaDeFigurasObtenidas;
    private Integer[] creditosApostadosPorFigura;
    
    //Bolas extra
    private Integer creditosGanadosGraciasABolasExtra;
    private Integer creditosApostadosEnBolasExtra;
    private Integer cantidadDeBolasExtraSeleccionadas;
    
    //Computo de graficos
    private BigInteger[] ganaciasPorFigura;
    
    //Simular boton
    @FXML private ProgressBar progress;
    
    
    
    //Sensores
    private Gauge porcentajeDeRetribucionGauge;
    private Gauge porcentajeDeJuegosGanados;
    private Gauge porcentajeDeJuegosConBolasExtra;
    private Gauge porcentajeDeBonusEnJuegosTotales;
    
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
    
    //Resultados
    @FXML private TextArea resultadosTxt;
    
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
    
    @FXML private BarChart frecuenciaDeTablero;
    
    private static boolean utilizarPremiosFijosEnBonus;
    private static boolean utilizarPremiosVariablesEnBonus;
    private Integer cantidadDeSimulaciones;
    private int cantidadDePremiosBonus;
    private int indiceConfiguracionCostoBolaExtra;
    
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
        try {
            initConfig();
        } catch (IOException ex) {
            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
        }
        initComputos();
        
    }
    
    @Subscribe
    private void persistirTablaDePagos(Evento e){
        if (e.getCodigo() == 101 && comboTablaPagos.getSelectionModel().getSelectedIndex() > -1
                && comboFigurasPago.getSelectionModel().getSelectedIndex() > -1) {
            try {
                if (comboFigurasPago.getSelectionModel().getSelectedIndex() < 
                        comboTablaPagos.getSelectionModel().getSelectedItem().getFiguras().size()) {
                    tabla.get(comboTablaPagos.getSelectionModel().getSelectedIndex())
                        .getFiguras().get(comboFigurasPago.getSelectionModel().getSelectedIndex())
                        .setCasillas(casillas);
                }
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
            
//            mostrarPorPantalla("Cargar la tabla de la base de datos");
            
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
    
    @Subscribe
    private void cargarPerfiles(Evento e) throws IOException{
        if (e.getCodigo() == CodigoEvento.CARGARPERFILES.getValue()) {
            Map<String,Object> parametros = e.getParametros();
            if (parametros != null && parametros.get("creditosMaximosPorPerfil") != null){
                Object[] aux = (Object[]) e.getParametros().get("creditosMaximosPorPerfil");
                this.creditosMaximosPorPerfil = Arrays.copyOf(aux, aux.length, Integer[].class);  
            }
            if (parametros != null && parametros.get("probabilidadDeApostarPorPerfil") != null){
                Object[] aux = (Object[]) e.getParametros().get("probabilidadDeApostarPorPerfil");
                this.probabilidadDeApostarPorPerfil = Arrays.copyOf(aux, aux.length, Double[].class);  
            }
            if (parametros != null && parametros.get("probabilidadDeComprarBolasExtra") != null){
                Object[] aux = (Object[]) e.getParametros().get("probabilidadDeComprarBolasExtra");
                this.probabilidadDeComprarBolasExtra = Arrays.copyOf(aux, aux.length, Double[].class);  
            }
            if (parametros != null && parametros.get("indiceConfiguracionJugadores") != null){
                this.indiceConfiguracionJugadores = Integer.valueOf(parametros.get("indiceConfiguracionJugadores").toString());  
            }
            
            ConfiguracionPersistencia config = PersistenciaJson.getInstancia().getConfiguracion();
            
            config.setProbabilidadDeApostarPorPerfil(probabilidadDeApostarPorPerfil);
            config.setProbabilidadDeComprarBolasExtra(probabilidadDeComprarBolasExtra);
            config.setCreditosMaximosPorPerfil(creditosMaximosPorPerfil);
            config.setIndiceConfiguracionJugadores(indiceConfiguracionJugadores);
            
            PersistenciaJson.getInstancia().persistirConfiguracion();
        }
    }
    
    @Subscribe
    private void configurarBolasExtra(Evento e) throws IOException{
        if (e.getCodigo() == CodigoEvento.BOLASEXTRA.getValue()) {
            if (e.getParametros() != null && e.getParametros().get("utilizarUmbral") != null) {
                this.usarUmbralParaLiberarBolasExtra = Boolean.valueOf(e.getParametros().get("utilizarUmbral").toString());
            }
            
            if (e.getParametros() != null && e.getParametros().get("umbralParaLiberarBolasExtra") != null) {
                this.umbralParaLiberarBolasExtra = Integer.valueOf(e.getParametros().get("umbralParaLiberarBolasExtra").toString());
            }
            
            if (e.getParametros() != null && e.getParametros().get("porcentajeDeCostoDeBolaExtraSegunPremioMayor") != null) {
                this.porcentajeDeCostoDeBolaExtraSegunPremioMayor = Double.valueOf(e.getParametros().get("porcentajeDeCostoDeBolaExtraSegunPremioMayor").toString());
            }
            
            if (e.getParametros() != null && e.getParametros().get("limiteMinimoGratis") != null) {
                this.limiteMinimoGratis = Integer.valueOf(e.getParametros().get("limiteMinimoGratis").toString());
            }
            
            if (e.getParametros() != null && e.getParametros().get("limiteMaximoGratis") != null) {
                this.limiteMaximoGratis = Integer.valueOf(e.getParametros().get("limiteMaximoGratis").toString());
            }
            if (e.getParametros() != null && e.getParametros().get("indiceConfiguracionCostoBolaExtra") != null) {
                this.indiceConfiguracionCostoBolaExtra = Integer.valueOf(e.getParametros().get("indiceConfiguracionCostoBolaExtra").toString());
            }
            if (e.getParametros() != null && e.getParametros().get("utilizarBolasExtraGratis") != null) {
                this.utilizarBolasExtraGratis = Boolean.valueOf(e.getParametros().get("utilizarBolasExtraGratis").toString());
            }
            
//            System.out.println("Utilizaaa bolas extra gratis: " + utilizarBolasExtraGratis);
            
            //Cargar los datos a la configuracion
            ConfiguracionPersistencia config = PersistenciaJson.getInstancia().getConfiguracion();
            config.setUtilizarUmbral(usarUmbralParaLiberarBolasExtra);
            config.setUtilizarBolasExtraGratis(utilizarBolasExtraGratis);
            config.setUmbralParaLiberarBolasExtra(umbralParaLiberarBolasExtra);
            config.setLimiteMinimoGratis(limiteMinimoGratis);
            config.setLimiteMaximoGratis(limiteMaximoGratis);
            config.setFactorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor(porcentajeDeCostoDeBolaExtraSegunPremioMayor);
            config.setIndiceConfiguracionCostoBolaExtra(indiceConfiguracionCostoBolaExtra);
            //Persistir
            PersistenciaJson.getInstancia().persistirConfiguracion();
            
            //Debug
//            mostrarPorPantalla("Metodo suscripto a bolas extra");
//            mostrarPorPantalla("utilziar Umbral de bolas extra: " + usarUmbralParaLiberarBolasExtra);
//            mostrarPorPantalla("Umbral de bolas extra: " + umbralParaLiberarBolasExtra);
//            mostrarPorPantalla("Limite minimo gratis: " + limiteMinimoGratis);
//            mostrarPorPantalla("Limite maximo gratis: " + limiteMaximoGratis);
//            mostrarPorPantalla("Porcentaje del premio mayor para el costo: " + porcentajeDeCostoDeBolaExtraSegunPremioMayor);
            
        }
    }
    
    @Subscribe
    private void persistirTournament(Evento e) throws IOException{
        if (e != null && e.getCodigo() == CodigoEvento.TOURNAMENT.getValue()) {
            int indicePorcentajeTournamentCombo = 0;
            
            if (e.getParametros() != null && e.getParametros().get("tournament") != null) {
                this.tournament = Boolean.valueOf(e.getParametros().get("tournament").toString());
            }
            if (e.getParametros() != null && e.getParametros().get("porcentajeParaTournament") != null) {
                this.porcentajeParaTournament = Double.valueOf(e.getParametros().get("porcentajeParaTournament").toString()) ;
            }
            if (e.getParametros() != null && e.getParametros().get("indicePorcentajeTournamentCombo") != null) {
                indicePorcentajeTournamentCombo = Integer.valueOf(e.getParametros().get("indicePorcentajeTournamentCombo").toString());
            }
            
            ConfiguracionPersistencia config = PersistenciaJson.getInstancia().getConfiguracion();
            config.setTournament(tournament);
            config.setPorcentajeParaTournament(porcentajeParaTournament);
            config.setIndicePorcentajeTournamentCombo(indicePorcentajeTournamentCombo);
            
            PersistenciaJson.getInstancia().persistirConfiguracion();
        }
    }
    
    @Subscribe
    private void persistirBonus(Evento e) throws IOException{
        if (e != null && e.getCodigo() == CodigoEvento.BONUS.getValue()) {
            if (e.getParametros() != null && e.getParametros().get("premiosFijos") != null) {
                this.premiosFijosBonus =  (Map<Integer, Integer>) e.getParametros().get("premiosFijos");
            }
            if (e.getParametros() != null && e.getParametros().get("premiosVariables") != null) {
                this.premiosVariablesBonus = (Map<Integer, Integer>) e.getParametros().get("premiosVariables");  
            }
            if (e.getParametros() != null && e.getParametros().get("utilizarPremiosVariables") != null) {
                this.utilizarPremiosVariablesBonus = Boolean.valueOf(e.getParametros().get("utilizarPremiosVariables").toString());
            }
            
            if (e.getParametros() != null && e.getParametros().get("utilizarPremiosFijos") != null) {
                this.utilizarPremiosFijosBonus = Boolean.valueOf(e.getParametros().get("utilizarPremiosFijos").toString());
            }
            
            if (e.getParametros() != null && e.getParametros().get("cantidadDePremiosBonus") != null) {
                this.cantidadDePremiosBonus = Integer.valueOf(e.getParametros().get("cantidadDePremiosBonus").toString());
            }
            
            //Debug
//            mostrarPorPantalla("---------Bonus-----------");
//            mostrarPorPantalla("Utilizar premios fijos: " + utilizarPremiosFijosBonus);
//            mostrarPorPantalla("Utilizar premios variables: " + utilizarPremiosVariablesBonus);
//            mostrarPorPantalla("Premios fijos: " + ArrayUtils.toString(premiosFijosBonus));
//            mostrarPorPantalla("Premios variables: " + ArrayUtils.toString(premiosVariablesBonus));
            
            ConfiguracionPersistencia config = PersistenciaJson.getInstancia().getConfiguracion();
            config.setUtilizarPremiosFijosBonus(utilizarPremiosFijosBonus);
            config.setUtilizarPremiosVariablesBonus(utilizarPremiosVariablesBonus);
            config.setPremiosFijosBonus(premiosFijosBonus);
            config.setPremiosVariablesBonus(premiosVariablesBonus);
            config.setCantidadDePremiosEnBonus(cantidadDePremiosBonus);
            
            PersistenciaJson.getInstancia().persistirConfiguracion();
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
            
            if (indiceTablero > -1) {
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
                
                this.cantidadDeSimulaciones = Integer.valueOf(simulacionesTxt.getText());
                this.retribuciones = new double[cantidadDeSimulaciones];
                
                this.pagado = BigInteger.ZERO;
                this.apostado = BigInteger.ZERO;
                
                Task<Void> task = new Task<Void>() {
                    @Override protected Void call() throws Exception {
                        
                        //Comenzar la simulacion
                        
                        mostrarPorPantalla("Tablero elegido: " + comboTablaPagos.getSelectionModel().getSelectedIndex() + "\n");
                        
                        resultadosTxt.setText("");
                        
                        mostrarResultados("Tablero: " + tableroElegido(comboTablaPagos.getSelectionModel().getSelectedItem()) + "\n" );
                        
                        //Inhabilitar el boton de simulacion
                        simularBtn.setDisable(true);
                        
                        simular(cantidadDeSimulaciones);
                        
                        return null;
                    }

                    private String tableroElegido(TablaDePago tablero) {
                        StringBuilder result = new StringBuilder();
                        result.append("[");
                        for(FiguraPago figura : tablero.getFiguras()){
                            result.append(figura.getNombre()).append("(").append(figura.getFactorGanancia())
                                    .append("),");
                        }
                        return result.substring(0, result.length() -1) + "]";
                    }
                };
                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    
                    @Override
                    public void handle(WorkerStateEvent t) {
                        //Terminó el proceso de simulación
                        
                        progress.setVisible(false); //Ocultar la barra de progreso
                        simularBtn.setDisable(false); //Habilito el boton de simular
                        
                        
                        //Mostrar resultados
                        BigDecimal porcentajeRetribucion = Matematica.porcentaje(pagado.intValueExact(), apostado.intValueExact());
                        if (porcentajeRetribucion != null) {
                            double retribuido = porcentajeRetribucion.doubleValue();
                            //Mostrar
                            if (retribuido > 100.0) {
                                porcentajeDeRetribucionGauge.setMaxValue(retribuido);
                            }
                            else{
                                porcentajeDeRetribucionGauge.setMaxValue(100); 
                            }
                            
                            porcentajeDeRetribucionGauge.setValue(retribuido);
                        }
                        
                        int cantidadGanados = cantidadDeJuegosGanados.intValueExact();
                        BigDecimal porcentajeDeGanados = Matematica.porcentaje(cantidadGanados, cantidadDeSimulaciones);
                        if (porcentajeDeGanados != null) {
                            double ganado = porcentajeDeGanados.doubleValue();
                            //Mostrar
                            porcentajeDeJuegosGanados.setValue(ganado);
                        }
                        
                        int cantidadConBolasExtra = juegosConBolasExtra.intValueExact();
                        BigDecimal porcentajeDeJuegosConBolasExtraLiberadas = Matematica.porcentaje(cantidadConBolasExtra, cantidadDeSimulaciones);
                        if (porcentajeDeJuegosConBolasExtraLiberadas != null) {
                            double bolasExtra = porcentajeDeJuegosConBolasExtraLiberadas.doubleValue();
                            //Mostrar
                            porcentajeDeJuegosConBolasExtra.setValue(bolasExtra);
                        }
                        
                        double porcentajeDeJuegosConBonus = Matematica.porcentaje(cantidadDeJuegosConBonus, BigInteger.valueOf(cantidadDeSimulaciones)).doubleValue();
                        porcentajeDeBonusEnJuegosTotales.setValue(Matematica.redondear(porcentajeDeJuegosConBonus, 2));
                        
                        //Mostrar porcentajes de retribucion segun perfil
                        mostrarResultados("------------------------------------------" + "\n");
                        mostrarResultados("Porcentajes de retribucion según perfiles " + "\n");
                        try {
                            mostrarResultados("Débil: " + (!apostadoSegunPerfil[0].equals(BigInteger.ZERO) ? Matematica.redondear(Matematica.porcentaje(pagadoSegunPerfil[0], apostadoSegunPerfil[0]).doubleValue(), 2) : BigInteger.ZERO.doubleValue()) + "%\n");
                            mostrarResultados("medio: " + (!apostadoSegunPerfil[1].equals(BigInteger.ZERO) ? Matematica.redondear(Matematica.porcentaje(pagadoSegunPerfil[1], apostadoSegunPerfil[1]).doubleValue(), 2) : BigInteger.ZERO.doubleValue()) + "%\n");
                            mostrarResultados("Fuerte: " + (!apostadoSegunPerfil[2].equals(BigInteger.ZERO) ? Matematica.redondear(Matematica.porcentaje(pagadoSegunPerfil[2], apostadoSegunPerfil[2]).doubleValue(), 2) : BigInteger.ZERO.doubleValue()) + "%\n");
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        
                        //Mostrar ganancias
                        mostrarResultados("------------------------------------------" + "\n");
                        mostrarResultados("Apostado: " + apostado + " creditos (Apuesta básica: " + (apostado.intValue() - creditosApostadosEnBolasExtra) + " - Apuesta en compra de bolas extra:" + creditosApostadosEnBolasExtra + ")\n");
                        mostrarResultados("Ganado: " + pagado + " creditos\n");
                        
                        //Mostrar datos del bonus
                        mostrarResultados("------------------------------------------" + "\n");
                        mostrarResultados("Cantidad de juegos con bonus: " + cantidadDeJuegosConBonus.intValue() + "\n");
                        mostrarResultados("Ganancia total en bonus: " + cantidadGanadoEnBonus.intValue() + "\n");
                        
                        if (pagado.intValue() > 0) {
                            mostrarResultados("Porcentaje de lo ganado en Bonus con respecto a la ganancia total: " + Matematica.porcentaje(cantidadGanadoEnBonus, pagado) + "%\n");
                        }
                        
                        
                        //Mostrar datos de las bolas extra
                        mostrarResultados("------------------------------------------" + "\n");
                        mostrarResultados("Cantidad de juegos con bolas extras: " + cantidadConBolasExtra + "\n");
                        mostrarResultados("Cantidad de bolas extras seleccionadas: " + cantidadDeBolasExtraSeleccionadas + "\n");
                        mostrarResultados("Ganado en ciclos de bolas extras: " + creditosGanadosGraciasABolasExtra + "\n");
                        mostrarResultados("Apostado en ciclos de bolas extras: " + creditosApostadosEnBolasExtra + "\n");
                        if (creditosApostadosEnBolasExtra > 0) {
                            mostrarResultados("Porcentaje de retribución de bolas extra: " + Matematica.porcentaje(creditosGanadosGraciasABolasExtra, creditosApostadosEnBolasExtra) + "%\n");
                        }
                        else{
                            if (creditosGanadosGraciasABolasExtra > 0 && creditosApostadosEnBolasExtra == 0) {
                                mostrarResultados("Porcentaje de retribución de bolas extra: Infinito\n");
                            }
                            if (creditosApostadosEnBolasExtra == 0) {
                                mostrarResultados("Porcentaje de retribución de bolas extra: 0%\n");
                            }
                        }
                        
                        
                        //Mostrar frecuencia de premios
                        mostrarResultados("------------------------------------------" + "\n");
                        mostrarResultados("Frecuencia de premios obtenidos" + "\n");
                        
                        BigInteger cantidadDePremiosObtenidos = BigInteger.ZERO;
                        BigInteger ganado = BigInteger.ZERO;
                        for (int i = 0; i < frecuenciaDeFigurasObtenidas.length; i++) {
                            cantidadDePremiosObtenidos = cantidadDePremiosObtenidos.add(BigInteger.valueOf(frecuenciaDeFigurasObtenidas[i]));
                            ganado = ganado.add(BigInteger.valueOf(frecuenciaDePremiosObtenidosPorFigura[i]));
                        }
                        
                        Object[][] data = new Object[frecuenciaDeFigurasObtenidas.length][5];
                        
                        for (int i = 0; i < frecuenciaDeFigurasObtenidas.length; i++) {
//                            mostrarResultados(String.format("%-25s %-10s\n",comboFigurasPago.getItems().get(i).getNombre(),Matematica.porcentaje(frecuenciaDeFigurasObtenidas[i], cantidadDePremiosObtenidos)));
                            if (!cantidadDePremiosObtenidos.equals(BigInteger.ZERO)) {
                                data[i][0] = comboFigurasPago.getItems().get(i).getNombre();
                                data[i][1] = Matematica.porcentaje(BigInteger.valueOf(frecuenciaDePremiosObtenidosPorFigura[i]), ganado) + "%";
                                data[i][2] = frecuenciaDeFigurasObtenidas[i] + " ocurrencias";
                                data[i][3] = frecuenciaDePremiosObtenidosPorFigura[i] + " creditos obtenidos";
                                data[i][4] = creditosApostadosPorFigura[i] + " creditos apostados";
                            }
                        }
                        
                        TextTable tt = new TextTable(new String[]{"Nombre", "Porcentaje de retribución (%)", "Ocurrencia", "Creditos obtenidos", "Creditos apostados"}, data);
                        try {
                            tt.printTable(new PrintStream("frecuencia.txt"), 0);
                            mostrarResultados(read("frecuencia.txt"));
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        //Graficar retribuciones parciales
                        graficarPorcentajesDeRetribucionParciales(retribucionesParcialesAcumuladas);
                        graficarFrecuenciasDeFiguras(frecuenciaDePremiosObtenidosPorFigura);
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

    private String read(String fileName) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        try {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String result = sb.toString();
            return result;
        } finally {
            br.close();
        }
    }
    
    private void initMenu() {
        
        jugadorMenuItem.setOnAction(e -> {
            ventana("/fxml/Jugador.fxml", "Perfiles de Jugador");
        });
        
        bonusMenuItem.setOnAction(e -> {
            ventana("/fxml/Bonus1.fxml", "Bonus");
        });
        
        bolasExtraMenuItem.setOnAction(e -> {
            ventana("/fxml/BolasExtra.fxml", "Bolas Extra");
        });
        
        tournamentMenuItem.setOnAction(e -> {
            ventana("/fxml/Tournament.fxml", "Jackpot");
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
        temperatura.setPrefWidth(panel != null? panel.getPrefWidth() : 70.0);
        temperatura.setPrefHeight(panel != null? panel.getPrefHeight(): 100.0);
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

    private void initConfig() throws IOException {
        progress.setVisible(false);
        
        //Tournament
        tournament = false;
        porcentajeParaTournament = .01;
        
        ConfiguracionPersistencia config = PersistenciaJson.getInstancia().getConfiguracion();
        
        creditosMaximosPorPerfil = config.getCreditosMaximosPorPerfil();
        probabilidadDeApostarPorPerfil = config.getProbabilidadDeApostarPorPerfil();
        probabilidadDeComprarBolasExtra = config.getProbabilidadDeComprarBolasExtra();
        
        crearTermometro(porcentajeDeRetribucionGauge = new Gauge(), porcentajeRetribucionPane, "Retribución", "%");
        crearTermometro(porcentajeDeJuegosGanados = new Gauge(), porcentajeGanadosPane, "Ganados", "%");
        crearTermometro(porcentajeDeJuegosConBolasExtra = new Gauge(), porcentajeJuegosBolasExtraPane, "Con bolas extra", "%");
        crearTermometro(porcentajeDeBonusEnJuegosTotales = new Gauge(), PorcentajeDeJuegosEnCiclosDe5sinGanarPane, "Bonus obtenidos", "%");
        
//        cargarBarChartTablero(panelVertical, frecuenciaDeTablero, null);
        
        this.usarUmbralParaLiberarBolasExtra = config.isUtilizarUmbral();
        this.umbralParaLiberarBolasExtra = config.getUmbralParaLiberarBolasExtra();
        this.limiteMinimoGratis = config.getLimiteMinimoGratis();
        this.limiteMaximoGratis = config.getLimiteMaximoGratis();
        this.porcentajeDeCostoDeBolaExtraSegunPremioMayor = config.getFactorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor();
        this.tournament = config.isTournament();
        this.porcentajeParaTournament = config.getPorcentajeParaTournament();
        //Bonus
        this.premiosFijosBonus = config.getPremiosFijosBonus();
        this.premiosVariablesBonus = config.getPremiosVariablesBonus();
        this.utilizarPremiosFijosBonus = config.isUtilizarPremiosFijosBonus();
        this.utilizarBolasExtraGratis = config.isUtilizarBolasExtraGratis();
        this.utilizarPremiosVariablesBonus = config.isUtilizarPremiosVariablesBonus();
        this.cantidadDePremiosBonus = config.getCantidadDePremiosEnBonus();
        
        //Totalizador de perfiles
        this.pagadoSegunPerfil = new BigInteger[3];
        this.apostadoSegunPerfil = new BigInteger[3];
        
//        System.out.println("Indice de configuracion de perfiles: " + config.getIndiceConfiguracionJugadores());
//        mostrarPorPantalla("Porcentaje del premio mayor (initConfig): " + this.porcentajeDeCostoDeBolaExtraSegunPremioMayor);
    }
    
    
    private void simular(int n) throws IOException{
        
        //Ambiente controlado
        int[][][] figuras = obtenerFiguras(comboTablaPagos.getSelectionModel().getSelectedItem().getFiguras());
        int acumulado = 10000;
        boolean generarNuevoBolillero = true;
        this.cantidadDeJuegosGanados = BigInteger.ZERO; //Coloco en cero el contador de juegos ganados
        this.juegosConBolasExtra = BigInteger.ZERO; //Coloco en cero el contador de juegos ganados
        this.cantidadDeJuegosConBonus = BigInteger.ZERO;
        this.frecuenciaDeFigurasObtenidas = new Integer[figuras.length];
        this.apostado = BigInteger.ZERO;
        this.pagado = BigInteger.ZERO;
        this.cantidadGanadoEnBonus = BigInteger.ZERO;
        //Frecuencia de premios obtenidos por figura
        frecuenciaDePremiosObtenidosPorFigura = new Integer[figuras.length];
        
        creditosApostadosPorFigura = new Integer[figuras.length];
        
        //Inicializar frecuencia de figuras obtenidas y los creditos apostados
        for (int i = 0; i < figuras.length; i++) {
            this.frecuenciaDeFigurasObtenidas[i] = 0;
            this.creditosApostadosPorFigura[i] = 0;
            this.frecuenciaDePremiosObtenidosPorFigura[i] = 0;
        }
        
        ConfiguracionPersistencia config = PersistenciaJson.getInstancia().getConfiguracion();
        
        porcentajeDeCostoDeBolaExtraSegunPremioMayor = config.getFactorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor();
        cantidadDePremiosBonus = config.getCantidadDePremiosEnBonus();
        
        //Totalizadores de bolas extra
        creditosGanadosGraciasABolasExtra = 0;
        creditosApostadosEnBolasExtra = 0;
        cantidadDeBolasExtraSeleccionadas = 0;
    
        //Debug
//        mostrarPorPantalla("Utilizar umbral: " + usarUmbralParaLiberarBolasExtra);
//        mostrarPorPantalla("Umbral: " + this.umbralParaLiberarBolasExtra);
//        mostrarPorPantalla("Limite inferior: " + limiteMinimoGratis);
//        mostrarPorPantalla("Limite superior: " + limiteMaximoGratis);
//        mostrarPorPantalla("Porcentaje del premio mayor para bola extra: " + porcentajeDeCostoDeBolaExtraSegunPremioMayor);
//        mostrarPorPantalla("Tournament: " + tournament);
//        mostrarPorPantalla("Porcentaje para tournament: " + config.getPorcentajeParaTournament());
//        mostrarPorPantalla("Indice de configuracion de jugadores: " + config.getIndiceConfiguracionJugadores());
//        mostrarPorPantalla("Creditos maximos por perfil: " + ArrayUtils.toString(config.getCreditosMaximosPorPerfil()));
//        mostrarPorPantalla("Probabilidad de apuestas por perfil: " + ArrayUtils.toString(config.getProbabilidadDeApostarPorPerfil()));
//        mostrarPorPantalla("Probabilidad de comprar bolas extra por perfil: " + ArrayUtils.toString(config.getProbabilidadDeComprarBolasExtra()));
        
        this.porcentajeParaTournament = config.getPorcentajeParaTournament();
        
//        mostrarPorPantalla("Porcentaje de tournament: " + porcentajeParaTournament);
        
        //Creo los perfiles segun la configuracion
        Perfil[] perfiles = crearPerfiles(config);
        
        //Inicializo los acumuladores de total de apuestas y ganado por perfil
        for (int i = 0; i < 3; i++) {
            this.pagadoSegunPerfil[i] = BigInteger.ZERO;
            this.apostadoSegunPerfil[i] = BigInteger.ZERO;
        }
        
        //Indice de muestra de resultados
        int l = 1;
        
        int cantidadDeMediciones = 10;
        int total = n;
        int avance = total / cantidadDeMediciones;
        
        retribucionesParcialesAcumuladas = new double[cantidadDeMediciones];
        
        this.utilizarBolasExtraGratis = config.isUtilizarBolasExtraGratis();
        
        for (int i = 0; i < n; i++) { 
            
            
            bingo = new Juego();
            bingo.setCrearFigurasDePago(false);
            bingo.setFigurasDePago(figuras);
            bingo.setModoDebug(false);
            
            bingo.inicializar(comboTablaPagos.getSelectionModel().getSelectedItem().getFiguras());
            
            bingo.setAcumulado(acumulado);
            bingo.setUtilizarUmbralParaLiberarBolasExtra(usarUmbralParaLiberarBolasExtra);
            bingo.setUtilizarBolasExtraGratis(utilizarBolasExtraGratis);
            bingo.setUmbralParaLiberarBolasExtra(umbralParaLiberarBolasExtra);
            bingo.setModoTournament(tournament);
            bingo.setPorcentajeDeDescuentoParaTournament(porcentajeParaTournament);
            bingo.setModoSimulacion(true);
            bingo.setLimiteInferiorParaBolaExtraGratis(limiteMinimoGratis);
            bingo.setLimiteSuperiorParaBolaExtraGratis(limiteMaximoGratis);
            bingo.setPorcentajeDelPremioMayorPorSalirParaBolaExtra(porcentajeDeCostoDeBolaExtraSegunPremioMayor);
            bingo.setModoBonus(true);
            bingo.setPametrosBonus(this.utilizarPremiosFijosBonus, 
                    this.utilizarPremiosVariablesBonus, 
                    this.premiosFijosBonus, 
                    this.premiosVariablesBonus, 
                    this.cantidadDePremiosBonus);
            bingo.setPerfilActual(seleccionarPerfil(config.getIndiceConfiguracionJugadores(), i, cantidadDeSimulaciones, perfiles));
            bingo.setCreditos(bingo.getPerfilActual().getCreditosMaximos());
            int factorDeApuesta = bingo.getPerfilActual().getFactorDeApuesta();
            int creditosMaximos = bingo.getPerfilActual().getCreditosMaximos();
//            System.out.println("Factor de apuesta " + factorDeApuesta);
//            System.out.println("Creditos maximos " + creditosMaximos);
            bingo.apostarEquitativamente(RNG.getInstance().pickInt(factorDeApuesta), 4);
            bingo.habilitarTodos();
            
            //Jugar
            bingo.jugar(generarNuevoBolillero);
            
            //Computar resultados;
            int apuestasBasicas = bingo.apuestaTotal();
            int invertidoEnBolasExtra = bingo.getCreditosInvertidosEnBolasExtra();
            int apuestaTotal = apuestasBasicas + invertidoEnBolasExtra;
            int gano = bingo.ganancias();
            int conBolasExtra = bingo.isSeLiberaronBolasExtra() ? 1 : 0;
            cantidadDeJuegosConBonus = cantidadDeJuegosConBonus.add(BigInteger.valueOf(bingo.getCantidadDeVecesQueSeObtuvoElBonus()));
            cantidadGanadoEnBonus = cantidadGanadoEnBonus.add(BigInteger.valueOf(bingo.getTotalGanadoEnBonus()));
            
            //Bolas extra
            creditosGanadosGraciasABolasExtra += bingo.getGanadoEnBolasExtra();
            creditosApostadosEnBolasExtra += invertidoEnBolasExtra;
            cantidadDeBolasExtraSeleccionadas += bingo.getCantidadDeBolasExtraSeleccionadas();
            
            if (invertidoEnBolasExtra > 0) {
                System.out.println(invertidoEnBolasExtra);
            }
            
//            if (invertidoEnBolasExtra > 0) {
//                mostrarPorPantalla("Invirtió en bolas extra: " + invertidoEnBolasExtra);
//            }
            
            apostado = apostado.add(BigInteger.valueOf(apuestaTotal));
            pagado = pagado.add(BigInteger.valueOf(gano));
            juegosConBolasExtra = juegosConBolasExtra.add(BigInteger.valueOf(conBolasExtra));
            
            this.retribuciones[i] = bingo.apuestaTotal() > 0 ? Matematica.porcentaje(gano, apuestaTotal).doubleValue() : 0.0;
            this.cantidadDeJuegosGanados = gano > 0 ? this.cantidadDeJuegosGanados.add(BigInteger.valueOf(1)) : this.cantidadDeJuegosGanados;
            
            //Contabilizar totales por perfil
            int indicePerfilActual = 0;
            String nombrePerfil = bingo.getPerfilActual().getNombre();
            switch(nombrePerfil){
                case "Medio": indicePerfilActual = 1; break;
                case "Fuerte": indicePerfilActual = 2; break;
            }
            
            this.pagadoSegunPerfil[indicePerfilActual] = this.pagadoSegunPerfil[indicePerfilActual].add(BigInteger.valueOf(gano));
            this.apostadoSegunPerfil[indicePerfilActual] = this.apostadoSegunPerfil[indicePerfilActual].add(BigInteger.valueOf(apuestaTotal));
            
            //Porcentaje de retribucion parcial hasta el momento
            BigDecimal retribuidoParcial = Matematica.porcentaje(pagado, apostado);
            int apuestaInd = bingo.apuestaIndividual();
            
            //Computar la frecuencia de premios por cada figura
            for (int j = 0; j < Juego.getCantidadDeCartones(); j++) {
                for (int k = 0; k < figuras.length; k++) {
                    
                    frecuenciaDePremiosObtenidosPorFigura[k] += bingo.getPremiosPagados()[j][k];
                    
//                    System.out.println("Frecuencia de figura: " + frecuenciaDePremiosObtenidosPorFigura[k]);
//                    System.out.println("Apuesta individual " + bingo.apuestaIndividual());
//                    System.out.println("Premio: " + bingo.getNombresDeFiguras()[k]);
//                    
//                    for (int h = 0; h < Juego.getCantidadDeCartones(); h++) {
//                        System.out.println(ArrayUtils.toString(bingo.getPremiosPagados()[h]));
//                    }
                    int premio = bingo.getPremiosPagados()[j][k];
                    
                    int factor = bingo.getFactoresDePago()[k];
                    int ocurrencia =  premio / (apuestaInd * factor);
                    
//                    System.out.println("Apuesta individual: " + apuestaInd);
//                    if (ocurrencia == 1) { 
//                        System.out.println("\nAparecio una vez: " + bingo.getNombresDeFiguras()[k]);
//                    }
//                    if (ocurrencia > 1) {
//                        System.out.println("Aparecio: " + ocurrencia + "el " + bingo.getNombresDeFiguras()[k]);
//                    }
//                    System.out.println("Ocurrencia: " + ocurrencia);
                    if (ocurrencia > 0) {
                        this.frecuenciaDeFigurasObtenidas[k] += 1;
                        creditosApostadosPorFigura[k] += apuestaInd;
                        //Mostrar
//                        System.out.println("Ocurrencias: " + ArrayUtils.toString(frecuenciaDeFigurasObtenidas));
//                        System.out.println("Creditos ganados por figura: " + ArrayUtils.toString(frecuenciaDePremiosObtenidosPorFigura));
//                        System.out.println("Apostado por figura: " + ArrayUtils.toString(creditosApostadosPorFigura));
                        
                        
//                        System.out.println();
                    }
                }
            }
            
            if (i == (avance * l)) {
                mostrarResultados("Simulación " + i + ". Porcentaje de retribución: " + Matematica.redondear(retribuidoParcial.doubleValue(), 2) +"%\n");
                retribucionesParcialesAcumuladas[l] = Matematica.redondear(retribuidoParcial.doubleValue(), 2);
                l++;
            }
            
            if (i + 1 == n) {
                mostrarResultados("Simulación " + (i + 1) + ". Porcentaje de retribución: " + Matematica.redondear(retribuidoParcial.doubleValue(), 2) +"%\n");
            }
            
            //mostrarResultados(bingo.getResultados());
        }
    }

    private void mostrarPorPantalla(Object o){
        if (modoDebug) {
            System.out.println(o);
        }
    }
    
    private int[][][] obtenerFiguras(List<FiguraPago> figuras) {
        
        //Ordeno las figuras de mayor a menor, si no estan ordenadas el 
        //comportamiento del simulador es erratico
        Collections.sort(figuras, new Comparator<FiguraPago>() {
            @Override
            public int compare(FiguraPago o1, FiguraPago o2) {
                return o2.getFactorGanancia() - o1.getFactorGanancia();
            }
        });
        
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
        juegosConBolasExtra = BigInteger.ZERO;
    }

    private Perfil seleccionarPerfil(int indiceConfiguracion, int iteracionActual, Integer totalDeSimulaciones, Perfil[] perfiles) {
        Perfil p = null;
        try {
            //Selecciono en funcion de la configuracion y el indice elegido
            ConfiguracionPersistencia config = PersistenciaJson.getInstancia().getConfiguracion();
            
            if (indiceConfiguracion == 0) {
                double porcentajeDeAvance = iteracionActual / (double)totalDeSimulaciones;
//                mostrarPorPantalla("Configuracion 0 de jugadores!");
//                mostrarPorPantalla("Porcentaje de avance: " + porcentajeDeAvance);
                if (porcentajeDeAvance <= .3) {
                    return perfiles[0]; //Debil
                }
                else{
                    if (porcentajeDeAvance > .3 && porcentajeDeAvance <= .6) {
                        return perfiles[1]; //Medio
                    }
                    else{
                        return perfiles[2]; //Fuerte
                    }
                }
            }
            
            if (indiceConfiguracion == 1) {
                double porcentajeDeAvance = iteracionActual / (double)totalDeSimulaciones;
//                mostrarPorPantalla("Configuracion 1 de jugadores!");
//                mostrarPorPantalla("Porcentaje de avance: " + porcentajeDeAvance);
                if (porcentajeDeAvance <= .4) {
                    return perfiles[0]; //Debil
                }
                else{
                    if (porcentajeDeAvance > .4 && porcentajeDeAvance <= .7) {
                        return perfiles[1]; //Medio
                    }
                    else{
                        return perfiles[2]; //Fuerte
                    }
                }
            }
            
            if (indiceConfiguracion == 2) {
                double porcentajeDeAvance = iteracionActual / (double)totalDeSimulaciones;
//                mostrarPorPantalla("Configuracion 2 de jugadores!");
//                mostrarPorPantalla("Porcentaje de avance: " + porcentajeDeAvance);
                if (porcentajeDeAvance <= .1) {
                    return perfiles[0]; //Debil
                }
                else{
                    if (porcentajeDeAvance > .1 && porcentajeDeAvance <= .55) {
                        return perfiles[1]; //Medio
                    }
                    else{
                        return perfiles[2]; //Fuerte
                    }
                }
            }
            
            if (indiceConfiguracion == 3) {
                //Debil
                return perfiles[0];
            }
            
            if (indiceConfiguracion == 4) {
                return perfiles[1];
            }
            
            if (indiceConfiguracion == 5) {
                return perfiles[2];
            }
            
        } catch (IOException ex) {
            Logger.getLogger(PrincipalController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return p; 
    }
    
    private void graficarPorcentajesDeRetribucionParciales(double[] retribucionesParciales){
        linearChart.getData().clear();
        
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Cantidad de simulaciones");
        xAxis.setAnimated(false);

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("%");
        yAxis.setAnimated(false);
        
        XYChart.Series dataSeries1 = new XYChart.Series();
        dataSeries1.setName("Porcentaje de retribucion");
        
        for (int i = 0; i < retribucionesParciales.length; i++) {
            dataSeries1.getData().add(new XYChart.Data(i + "", (int)retribucionesParciales[i]));
        }
        
        linearChart.getData().add(dataSeries1);
    }
    
    private void cargarBarChartTablero(VBox panelVertical, BarChart frecuenciaTablero, int[] frecuenciaDeFiguras){
//        if (panelVertical.getChildren().contains(frecuenciaTablero)) {
//            panelVertical.getChildren().remove(frecuenciaTablero);
//        }

        

        CategoryAxis xAxis    = new CategoryAxis();
        xAxis.setLabel("Figuras");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Frecuencia");
        
        frecuenciaTablero = new BarChart(xAxis, yAxis);
        frecuenciaTablero.setPrefHeight(200);
        
        //Cargo las frecuencias
        if (frecuenciaDeFiguras != null) {
            //TODO: rellenar con datos de las frecuencias por figura,
            //frecuenciaDeFiguras.length = cantidad de figuras del tablero
            XYChart.Series dataSeries1 = new XYChart.Series();
            dataSeries1.setName("Frecuencia de figuras");
            
            for (int i = 0; i < frecuenciaDeFiguras.length; i++) {
                dataSeries1.getData().add(new XYChart.Data((i + 1) + "", (int)frecuenciaDeFiguras[i]));
            }

            frecuenciaTablero.getData().add(dataSeries1);
        }
        
//        panelVertical.getChildren().add(frecuenciaTablero);
    }

    private void graficarFrecuenciasDeFiguras(BigInteger[] frecuenciaDeFiguras){
        //Cargo las frecuencias
        if (frecuenciaDeFiguras != null) {
            //TODO: rellenar con datos de las frecuencias por figura,
            //frecuenciaDeFiguras.length = cantidad de figuras del tablero
            
            
            if (frecuenciaDeTablero == null) {
                CategoryAxis xAxis    = new CategoryAxis();
                xAxis.setLabel("Figuras");

                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Frecuencia");

                frecuenciaDeTablero = new BarChart(xAxis, yAxis);
            }
            
            frecuenciaDeTablero.getData().clear();
            
            XYChart.Series dataSeries1 = new XYChart.Series();
            dataSeries1.setName("Porcentaje de creditos ganados según figuras");
            
            //Calculo el total de creditos obtenidos
            BigInteger totalCreditosGanados = BigInteger.ZERO;
            
            for (int i = 0; i < frecuenciaDeFiguras.length; i++) {
                totalCreditosGanados = totalCreditosGanados.add(frecuenciaDeFiguras[i]);
            }
            
            for (int i = 0; i < frecuenciaDeFiguras.length; i++) {
                String nombreFigura = comboFigurasPago.getItems().get(i).getNombre();
//                mostrarPorPantalla(nombreFigura);
                try {
                    BigDecimal porcentaje = Matematica.porcentaje(frecuenciaDeFiguras[i], totalCreditosGanados);
                    dataSeries1.getData().add(new XYChart.Data(nombreFigura + "(" + Matematica.redondear(porcentaje.doubleValue(), 2) + "%)", porcentaje));
                } catch (Exception e) {
                }
            }
            
            
            frecuenciaDeTablero.getData().add(dataSeries1);
            frecuenciaDeTablero.setLegendSide(Side.TOP);
        }
        
        
    }
    
    private void graficarFrecuenciasDeFiguras(Integer[] frecuenciaDeFiguras){
        //Cargo las frecuencias
        if (frecuenciaDeFiguras != null) {
            //TODO: rellenar con datos de las frecuencias por figura,
            //frecuenciaDeFiguras.length = cantidad de figuras del tablero
            
            
            if (frecuenciaDeTablero == null) {
                CategoryAxis xAxis    = new CategoryAxis();
                xAxis.setLabel("Figuras");

                NumberAxis yAxis = new NumberAxis();
                yAxis.setLabel("Frecuencia");

                frecuenciaDeTablero = new BarChart(xAxis, yAxis);
            }
            
            frecuenciaDeTablero.getData().clear();
            
            XYChart.Series dataSeries1 = new XYChart.Series();
            dataSeries1.setName("Porcentaje de creditos ganados según figuras");
            
            //Calculo el total de creditos obtenidos
            BigInteger totalCreditosGanados = BigInteger.ZERO;
            
            for (int i = 0; i < frecuenciaDeFiguras.length; i++) {
                totalCreditosGanados = totalCreditosGanados.add(BigInteger.valueOf(frecuenciaDeFiguras[i]));
            }
            
            for (int i = 0; i < frecuenciaDeFiguras.length; i++) {
                String nombreFigura = comboFigurasPago.getItems().get(i).getNombre();
//                mostrarPorPantalla(nombreFigura);
                try {
                    BigDecimal porcentaje = Matematica.porcentaje(BigInteger.valueOf(frecuenciaDeFiguras[i]), totalCreditosGanados);
                    dataSeries1.getData().add(new XYChart.Data(nombreFigura + "(" + Matematica.redondear(porcentaje.doubleValue(), 2) + "%)", porcentaje));
                } catch (Exception e) {
                }
            }
            
            
            frecuenciaDeTablero.getData().add(dataSeries1);
            frecuenciaDeTablero.setLegendSide(Side.TOP);
        }
        
        
    }
    
    private void mostrarResultados(String val) {
        Platform.runLater(() -> {
            resultadosTxt.appendText(val);
        });
    }

    private Perfil[] crearPerfiles(ConfiguracionPersistencia config) {
        Perfil[] result = new Perfil[3];
        
        //Debil
        result[0] = new Perfil();
        result[0].setCreditosMaximos(config.getCreditosMaximosPorPerfil()[0]);
        result[0].setProbabilidadDeComprarBolasExtra(config.getProbabilidadDeComprarBolasExtra()[0]);
        result[0].setNombre("Debil");
        result[0].setFactorDeApuesta((int)(config.getProbabilidadDeApostarPorPerfil()[0] * config.getCreditosMaximosPorPerfil()[0]));
        
        if (result[0].getCreditosMaximos() <= 0) {
            result[0].setCreditosMaximos(50);
        }
        
        if (result[0].getFactorDeApuesta() * 4 > result[0].getCreditosMaximos()) {
            result[0].setFactorDeApuesta((int)(result[0].getCreditosMaximos() / 4));
        }
        
        //Medio
        result[1] = new Perfil();
        result[1].setCreditosMaximos(config.getCreditosMaximosPorPerfil()[1]);
        result[1].setProbabilidadDeComprarBolasExtra(config.getProbabilidadDeComprarBolasExtra()[1]);
        result[1].setNombre("Medio");
        result[1].setFactorDeApuesta((int)(config.getProbabilidadDeApostarPorPerfil()[1] * config.getCreditosMaximosPorPerfil()[1]));
        
        if (result[1].getCreditosMaximos() <= 0) {
            result[1].setCreditosMaximos(100);
        }
        
        if (result[1].getFactorDeApuesta() * 4 > result[1].getCreditosMaximos()) {
            result[1].setFactorDeApuesta((int)(result[1].getCreditosMaximos() / 4));
        }
        
        //Fuerte
        result[2] = new Perfil();
        result[2].setCreditosMaximos(config.getCreditosMaximosPorPerfil()[2]);
        result[2].setProbabilidadDeComprarBolasExtra(config.getProbabilidadDeComprarBolasExtra()[2]);
        result[2].setNombre("Fuerte");
        result[2].setFactorDeApuesta((int)(config.getProbabilidadDeApostarPorPerfil()[2] * config.getCreditosMaximosPorPerfil()[2]));
        
        if (result[2].getCreditosMaximos() <= 0) {
            result[2].setCreditosMaximos(200);
        }
        
        //Si la la apuesta (por ejemplo 7, creditos maximos 20) al habilitar todos los cartones
        //supera al credito (28 > 20) entonces coloco todos los creditos equitativamente 
        //en cada carton
        if (result[2].getFactorDeApuesta() * 4 > result[2].getCreditosMaximos()) {
            result[2].setFactorDeApuesta((int)(result[2].getCreditosMaximos() / 4));
        }
        
        return result;
    }
}
