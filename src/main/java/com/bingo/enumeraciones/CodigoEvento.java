/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bingo.enumeraciones;

/**
 *
 * @author gonza
 */
public enum CodigoEvento {
    PERSISTIR(101),
    CARGAR(102),
    CARGARPERFILES(103)
    
    ;
    
    private final int value;
    
    CodigoEvento(int value){
        this.value = value;
    }
    
    public int getValue(){
        return this.value;
    }
}
