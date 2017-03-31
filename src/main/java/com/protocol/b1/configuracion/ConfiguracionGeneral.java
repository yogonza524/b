/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.protocol.b1.configuracion;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
public class ConfiguracionGeneral {

    private static ConfiguracionGeneral instancia;
    
    //Atributos
    private int creditosDeLaVista; //Solo utilizable cuando la vista implementa el juego
    
    private ConfiguracionGeneral(){}
    
    public static ConfiguracionGeneral getInstancia(){
        if (instancia == null) {
            instancia = new ConfiguracionGeneral();
        }
        return instancia;
    }

    public int getCreditosDeLaVista() {
        return creditosDeLaVista;
    }

    public void setCreditosDeLaVista(int creditosDeLaVista) {
        this.creditosDeLaVista = creditosDeLaVista;
    }
}
