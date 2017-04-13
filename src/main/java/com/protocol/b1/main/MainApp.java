/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.protocol.b1.main;

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
public class MainApp {
    
    // <editor-fold defaultstate="collapsed" desc="Atributos">
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">

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
}