/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.protocol.b1.interfaces;

import com.protocol.b1.servidor.Paquete;

/**
 * Interfaz de comportamiento de respuesta a mensajes de control
 * del juego BingoBot
 * 
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public interface ComportamientoJuegoB1 {
    
    /**
     * 
     * @return 
     */
    public Paquete getConfiguracion();
    
    
}
