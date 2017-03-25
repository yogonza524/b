/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bingo.perfilesJugador;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
public class Perfil {

    private int factorDeApuesta;
    private double probabilidadDeComprarBolasExtra;
    private int creditosMaximos;
    private String nombre;
    
    public Perfil(){}

    public int getFactorDeApuesta() {
        return factorDeApuesta;
    }

    public double getProbabilidadDeComprarBolasExtra() {
        return probabilidadDeComprarBolasExtra;
    }

    public void setFactorDeApuesta(int factorDeApuesta) {
        this.factorDeApuesta = factorDeApuesta;
    }

    public void setProbabilidadDeComprarBolasExtra(double probabilidadDeComprarBolasExtra) {
        this.probabilidadDeComprarBolasExtra = probabilidadDeComprarBolasExtra;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getNombre() {
        return nombre;
    }

    public int getCreditosMaximos() {
        return creditosMaximos;
    }

    public void setCreditosMaximos(int creditosMaximos) {
        this.creditosMaximos = creditosMaximos;
    }
    
    public final static Perfil[] perfiles(){
        Perfil[] result = new Perfil[3];
        for (int i = 0; i < 3; i++) {
            result[i] = new Perfil();
        }
        result[0].factorDeApuesta = 4;
        result[0].probabilidadDeComprarBolasExtra = .3;
        result[0].nombre = "Debil";
        result[0].creditosMaximos = 30;
        
        result[1].factorDeApuesta = 15;
        result[1].probabilidadDeComprarBolasExtra = .6;
        result[1].nombre = "Medio";
        result[1].creditosMaximos = 60;
        
        result[2].factorDeApuesta = 30;
        result[2].probabilidadDeComprarBolasExtra = .99;
        result[2].nombre = "Alto";
        result[2].creditosMaximos = 120;
        
        return result;
    }
}
