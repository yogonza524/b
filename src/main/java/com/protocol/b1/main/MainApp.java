/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.protocol.b1.main;

import com.b1.batch.ProcessB1;
import com.b1.spring.services.ConfiguracionService;
import com.b1.spring.services.ContadoresService;
import com.b1.spring.services.HistorialB1Service;
import com.b1.spring.services.JuegoService;
import com.b1.spring.services.LogService;
import com.protocol.b1.servidor.Servidor;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
//@Configuration
//@ComponentScan

//@EnableAutoConfiguration
//@SpringBootApplication(exclude = {JmxAutoConfiguration.class})
public class MainApp {
    
    // <editor-fold defaultstate="collapsed" desc="Atributos">
    private static LogService logService;
    private static ContadoresService contadoresService;
    private static HistorialB1Service historialB1Service;
    private static JuegoService juegoService;
    private static ConfiguracionService configuracionService;
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Metodos">
    public static void main(String[] args) {
  
//        SpringApplication sa = new SpringApplication(MainApp.class);
//        sa.setBannerMode(Banner.Mode.OFF);
//        sa.setLogStartupInfo(false);
        
        Servidor server;
        try {
            server = new Servidor(args);
            server.iniciar();
            
        } catch (IOException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainApp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    //</editor-fold>

    public static LogService getLogService() {
        if (logService == null) {
            logService = new LogService();
        }
        return logService;
    }

    public static ContadoresService getContadoresService() {
        if (contadoresService == null) {
            contadoresService = new ContadoresService();
        }
        return contadoresService;
    }

    public static HistorialB1Service getHistorialB1Service() {
        if (historialB1Service == null) {
            historialB1Service = new HistorialB1Service();
        }
        return historialB1Service;
    }

    public static JuegoService getJuegoService() {
        if (juegoService == null) {
            juegoService = new JuegoService();
        }
        return juegoService;
    }

    public static ConfiguracionService getConfiguracionService() {
        if (configuracionService == null) {
            configuracionService = new ConfiguracionService();
        }
        return configuracionService;
    }
    
    /**
     * Metodos estaticos para servicios
     */
    
    
}
