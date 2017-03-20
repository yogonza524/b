/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.fx.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.joda.time.DateTime;

/**
 *
 * @author bingo
 */
public class Util {
    
    private static Stage principal;

    public static Stage getPrincipal() {
        return principal;
    }

    public static void setPrincipal(Stage principal) {
        Util.principal = principal;
    }
    
    public static void showWindow(String title,Class clazz, String fxml, Node n, boolean isModal, boolean isClosable, boolean isMaximized) throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader(clazz.getResource(fxml));
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            if (isModal) {
                stage.initModality(Modality.APPLICATION_MODAL);
            }
            if (isClosable) {
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent t) {
                        System.exit(0);
                    }
                });
            }
            if (isMaximized) {
                maximizar(stage);
            }
            stage.setTitle(title);
            stage.setScene(new Scene(root1));
            
            stage.show();
    }

    private static void maximizar(Stage stage) {
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
    }
    
    public static String now(){
        DateTime n = new DateTime(new Date());
        return n.getDayOfMonth() + "/" + n.getMonthOfYear() + "/"
                + n.getYear() + " " + (n.getHourOfDay() < 10 ? "0" + n.getHourOfDay() : n.getHourOfDay())
                + ":" + (n.getMinuteOfHour() < 10? "0" + n.getMinuteOfHour(): n.getMinuteOfHour());
    }
    
    public static String dia(Date d){
        String result = "Desconocido";
        if (d != null) {
            DateTime date = new DateTime(d);
            switch(date.getDayOfWeek()){
                case 7: result = "Domingo"; break;
                case 1: result = "Lunes"; break;
                case 2: result = "Martes"; break;
                case 3: result = "Miercoles"; break;
                case 4: result = "Jueves"; break;
                case 5: result = "Viernes"; break;
                case 6: result = "Sabado"; break;
            }
        }
        return result;
    }
    
    public static void showWindow(String title, String fxml, Class clazz, boolean isModal){
        Stage s = new Stage();
            Parent root;
            try {
                FXMLLoader loader = new FXMLLoader(clazz.getResource(fxml));
                root = (Parent) loader.load();
                s.setScene(new Scene(root));
                s.setTitle(title);
                s.setResizable(false);
                if (isModal) {
                    s.initModality(Modality.APPLICATION_MODAL);
                    s.initStyle(StageStyle.UTILITY);
                }
                else{
                    //Al no ser una ventana Modal, al cerrarla se cierra la aplicacion
                    s.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            System.exit(0);
                        }
                    });
                }
                s.getIcons().add(new Image("/img/icon.png"));
                s.show();
            } catch (IOException ex) {
                Logger.getLogger(clazz.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public static void showWindow(String title, String fxml, Class clazz, boolean isModal, boolean isClosable){
        Stage s = new Stage();
            Parent root;
            try {
                FXMLLoader loader = new FXMLLoader(clazz.getResource(fxml));
                root = (Parent) loader.load();
                s.setScene(new Scene(root));
                s.setTitle(title);
                s.setResizable(false);
                if (isModal) {
                    s.initModality(Modality.APPLICATION_MODAL);
                    s.initStyle(StageStyle.UTILITY);
                }
                else{
//                    s.initModality(Modality.NONE);
                    s.initStyle(StageStyle.DECORATED);
                }
                if (isClosable) {
                    //Al no ser una ventana Modal, al cerrarla se cierra la aplicacion
                    s.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            System.exit(0);
                        }
                    });
                }
                s.getIcons().add(new Image("/img/icon.png"));
                s.show();
            } catch (IOException ex) {
                Logger.getLogger(clazz.getName()).log(Level.SEVERE, null, ex);
            }
    }

    public static void showWindow(String title, String fxml, Class clazz, boolean isModal, boolean isClosable, boolean isResizable){
        Stage s = new Stage();
            Parent root;
            try {
                FXMLLoader loader = new FXMLLoader(clazz.getResource(fxml));
                root = (Parent) loader.load();
                s.setScene(new Scene(root));
                s.setTitle(title);
                s.setResizable(isResizable);
                if (isModal) {
                    s.initModality(Modality.APPLICATION_MODAL);
                    s.initStyle(StageStyle.UTILITY);
                }
                else{
//                    s.initModality(Modality.NONE);
                    s.initStyle(StageStyle.DECORATED);
                }
                if (isClosable) {
                    //Al no ser una ventana Modal, al cerrarla se cierra la aplicacion
                    s.setOnCloseRequest(new EventHandler<WindowEvent>() {
                        @Override
                        public void handle(WindowEvent event) {
                            System.exit(0);
                        }
                    });
                }
                s.getIcons().add(new Image("/img/icon.png"));
                s.show();
            } catch (IOException ex) {
                Logger.getLogger(clazz.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public static String fecha(Date fechaAdquisicion) {
        String result = "Sin especificar";
        if (fechaAdquisicion != null) {
            DateTime d = new DateTime(fechaAdquisicion);
            result = d.getDayOfMonth() + "/" + d.getMonthOfYear() + "/" + d.getYear();
        }
        return result;
    }
    
    public static String fechaLarga(Date fecha) {
        String result = "Sin especificar";
        if (fecha != null) {
            DateTime d = new DateTime(fecha);
            result = d.getDayOfMonth() + "/" + d.getMonthOfYear() + "/" + d.getYear()
                    + " " + (d.getHourOfDay() < 10 ? "0" : "") + d.getHourOfDay() + ":" + (d.getMinuteOfHour() < 10 ? "0" : "" ) + 
                    d.getMinuteOfHour() + ":" + (d.getSecondOfMinute() < 10 ? "0" : "") +
                    d.getSecondOfMinute()
                    ;
        }
        return result;
    }
    
    public static void closeWindow(Node c){
        Stage stage = (Stage) c.getScene().getWindow();
        stage.close();
    }
    
    public static String fechaYhora(Date d){
        DateTime n = new DateTime(d);
        return n.getDayOfMonth() + "/" + n.getMonthOfYear() + "/"
                + n.getYear() + " " + (n.getHourOfDay() < 10 ? "0" + n.getHourOfDay() : n.getHourOfDay())
                + ":" + (n.getMinuteOfHour() < 10? "0" + n.getMinuteOfHour(): n.getMinuteOfHour());
    }
    
    public static Map<String,BigDecimal> sort(Map<String,BigDecimal> map){
        Map<String, BigDecimal> treeMap = new TreeMap<>(
                new Comparator<String>() {

                    @Override
                    public int compare(String o1, String o2) {
                        return o1.compareTo(o2);
                    }

                });
        treeMap.putAll(map);
        return treeMap;
    }
    
    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    public static BigDecimal percentage(BigDecimal base, BigDecimal pct){
        return base.multiply(ONE_HUNDRED).divide(pct,2,RoundingMode.HALF_UP);
    }
    
    // Implementing Fisher–Yates shuffle
    public static void shuffleArray(Object[] ar)
    {
      // If running on Java 6 or older, use `new Random()` on RHS here
      Random rnd = ThreadLocalRandom.current();
      for (int i = ar.length - 1; i > 0; i--)
      {
        int index = rnd.nextInt(i + 1);
        // Simple swap
        Object a = ar[index];
        ar[index] = ar[i];
        ar[i] = a;
      }
    }
    
    public static <K, V extends Comparable<? super V>> Map<K, V> 
        sortByValueAsc( Map<K, V> map )
        {
            List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );
            Collections.sort( list, new Comparator<Map.Entry<K, V>>()
            {
                public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
                {
                    return (o1.getValue()).compareTo( o2.getValue() );
                }
            } );

            Map<K, V> result = new LinkedHashMap<K, V>();
            for (Map.Entry<K, V> entry : list)
            {
                result.put( entry.getKey(), entry.getValue() );
            }
            return result;
        }
        
    public static <K, V extends Comparable<? super V>> Map<K, V> 
        sortByValueDesc( Map<K, V> map )
        {
            List<Map.Entry<K, V>> list =
                new LinkedList<Map.Entry<K, V>>( map.entrySet() );
            Collections.sort( list, new Comparator<Map.Entry<K, V>>()
            {
                public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
                {
                    return (o2.getValue()).compareTo( o1.getValue() );
                }
            } );

            Map<K, V> result = new LinkedHashMap<K, V>();
            for (Map.Entry<K, V> entry : list)
            {
                result.put( entry.getKey(), entry.getValue() );
            }
            return result;
        }
        
    public static double redondear(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        
        return (double) tmp / factor;
    }
    
    public static double percentage(double value, int total){
        return redondear( 100.0 * value / (double)total, 2);
    }
    
    public static boolean between(double min, double max, double val){
        return (val >= min && val <= max);
    }
}
