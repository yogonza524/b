/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.guava.core;

import com.google.common.eventbus.EventBus;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
public class EventBusManager {
    
    private static EventBusManager instancia;
    
    private final EventBus bus;
    
    private EventBusManager(){
        bus = new EventBus("EventBusManager");
    }

    public static EventBusManager getInstancia() {
        if (instancia == null) {
            instancia = new EventBusManager();
        }
        return instancia;
    }

    public EventBus getBus() {
        return bus;
    }
    
}
