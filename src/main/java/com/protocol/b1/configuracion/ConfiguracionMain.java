/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.protocol.b1.configuracion;

/**
 *
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public class ConfiguracionMain {
    
    // <editor-fold defaultstate="collapsed" desc="Atributos">
    private static ConfiguracionMain instancia;
    
    private int idiomaCode;
    private int puertoBv20;
    private boolean beta;
    private boolean pad;
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    private ConfiguracionMain() {
        this.idiomaCode = 0; //0 = Español, por defecto
        this.puertoBv20 = 0; //Puerto serie 0 por defecto
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">
    public static ConfiguracionMain getInstancia(){
        if (instancia == null) {
            instancia = new ConfiguracionMain();
        }
        return instancia;
    }
    
    public int getIdiomaCode() {
        return idiomaCode;
    }

    public void setIdiomaCode(int idiomaCode) {
        this.idiomaCode = idiomaCode;
    }
    
    public int getPuertoBv20() {
        return puertoBv20;
    }

    public void setPuertoBv20(int puertoBv20) {
        this.puertoBv20 = puertoBv20;
    }
    
    public boolean isBeta() {
        return beta;
    }

    public void setBeta(boolean beta) {
        this.beta = beta;
    }
    
    public boolean isPad() {
        return pad;
    }

    public void setPad(boolean pad) {
        this.pad = pad;
    }
    
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Metodos">

    //</editor-fold>

    

    
    

    

    
}
