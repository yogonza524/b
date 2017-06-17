/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.protocol.b1.main;

import com.b1.batch.ProcessB1;
import com.b1.spring.services.ConfiguracionService;
import com.b1.spring.services.HistorialB1Service;
import com.protocol.b1.servidor.Servidor;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
@Configuration
@ComponentScan
@EnableAutoConfiguration
public class MainApp {
    
    // <editor-fold defaultstate="collapsed" desc="Atributos">
    private static final ApplicationContext CONTEXT = new 
            AnnotationConfigApplicationContext(MainApp.class);
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">
    public static ApplicationContext springContext(){
        return CONTEXT;
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Metodos">
    public static void main(String[] args) {
  
        
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
    
    @Bean
    ConfiguracionService configService(){
        return new ConfiguracionService();
    }
    
    @Bean
    HistorialB1Service historialB1service(){
        return new HistorialB1Service();
    }
}
