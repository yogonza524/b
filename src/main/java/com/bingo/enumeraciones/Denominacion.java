/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bingo.enumeraciones;

/**
 *
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public enum Denominacion {
    
    UN_CENTAVO(0.01f),
    CINCO_CENTAVOS(0.05f),
    DIEZ_CENTAVOS(0.10f),
    VEINTICINCO_CENTAVOS(0.25f),
    CINCUENTA_CENTAVOS(0.5f),
    UN_PESO(1.0f)
    ;
    
    Denominacion(Float value){
        this.value = value;
    }
    
    private final Float value;
    
    public Float getValue(){
        return this.value;
    }

    @Override
    public String toString() {
        return "Denominacion = $" + value;
    }
}
