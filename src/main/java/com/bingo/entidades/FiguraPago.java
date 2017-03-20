/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bingo.entidades;

import com.bingo.util.Matematica;
import com.core.bingosimulador.Juego;
import com.google.gson.Gson;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Carton ubicado en la región superior izquierda del juego BingoBot,
 * es utilizado para rellenar la tabla de pagos, cada carton 
 * especifica la figura premiada y el valor de la misma,
 * tambien se puede saber si dicho carton habilita el bonus o no
 * 
 * Internamente cada carton tiene un numero identificatorio,
 * un conjunto de casillas dado por la clase Configuracion, el factor
 * de ganancia (representa el premio en creditos a pagar al apostador) y
 * un nombre para identificarlo internamente
 * 
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public class FiguraPago implements Serializable{
    
    private int numero;
    private int[][] casillas;
    private int factorGanancia;
    private int apostado;
    private String nombre;
    private boolean esBonus;
    
    private static final Logger LOG = Logger.getLogger(FiguraPago.class.getName());
    
    
    private FiguraPago(){
        this.casillas = new int[Juego.getLineas()][Juego.getColumnas()];
        this.apostado = 0;
        this.numero = 1;
    }

    /**
     * Obtiene las casillas de este carton
     * @return matriz de enteros positivos
     */
    public int[][] getCasillas() {
        return casillas;
    }

    /**
     *
     * @param casillas
     */
    public void setCasillas(int[][] casillas) {
        this.casillas = casillas;
    }

    /**
     *
     * @return
     */
    public int getFactorGanancia() {
        return factorGanancia;
    }

    /**
     *
     * @param factorGanancia
     */
    public void setFactorGanancia(int factorGanancia) {
        this.factorGanancia = factorGanancia;
    }

    /**
     *
     * @return
     */
    public int getApostado() {
        return apostado;
    }

    /**
     *
     * @param apostado
     */
    public void setApostado(int apostado) {
        this.apostado = apostado;
    }

    /**
     *
     * @return
     */
    public String getNombre() {
        return nombre;
    }

    /**
     *
     * @param nombre
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     *
     * @return
     */
    public boolean isEsBonus() {
        return esBonus;
    }

    /**
     *
     * @param esBonus
     */
    public void setEsBonus(boolean esBonus) {
        this.esBonus = esBonus;
    }
    
    /**
     * Muestra la configuracion del tablero con sus datos respectivos
     */
    public void mostrarTablero(){
        Matematica.mostrarMatriz(casillas);
    }

    /**
     *
     * @return
     */
    public int getNumero() {
        return numero;
    }

    /**
     *
     * @param numero
     */
    public void setNumero(int numero) {
        this.numero = numero;
    }
    
    /**
     * Clase encargada de generar un carton de la tabla de pagos
     * aplicando el patron Builder
     */
    public static class FiguraPagoBuilder{
        
        private final FiguraPago carton;
        private static final Logger LOG = Logger.getLogger(FiguraPagoBuilder.class.getName());
        
        /**
         *
         */
        public FiguraPagoBuilder(){
            this.carton = new FiguraPago();
            this.carton.setNombre("");
            this.carton.setEsBonus(false);
        }
        
        /**
         *
         * @param factor
         * @return
         */
        public FiguraPagoBuilder factorDeGanancia(int factor){
            this.carton.setFactorGanancia(factor);
            return this;
        }
        
        /**
         *
         * @param nombre
         * @return
         */
        public FiguraPagoBuilder nombre(String nombre){
            this.carton.setNombre(nombre);
            return this;
        }
        
        /**
         *
         * @return
         */
        public FiguraPagoBuilder esBonus(){
            this.carton.setEsBonus(true);
            return this;
        }
        
        /**
         *
         * @param numero
         * @return
         */
        public FiguraPagoBuilder numero(int numero){
            this.carton.setNumero(numero);
            return this;
        }
        
        /**
         *
         * @param x
         * @param y
         * @return
         */
        public FiguraPagoBuilder casilla(int x, int y){
            if (x < Juego.getLineas() && y < Juego.getColumnas()) {
                carton.getCasillas()[x][y] = 1;
            }
            else{
                LOG.log(Level.WARNING, "Cuidado: no se ha cargado la casilla ({0}, {1}). Razon: fuera de rango del tablero", new Object[]{x, y});
            }
            return this;
        }
        
        /**
         *
         * @param apuesta
         * @return
         */
        public FiguraPagoBuilder apostar(int apuesta){
            this.carton.setApostado(apuesta);
            return this;
        }
        
        /**
         *
         * @return
         */
        public FiguraPago crear(){
            return this.carton;
        }
        
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Arrays.deepHashCode(this.casillas);
        hash = 67 * hash + this.factorGanancia;
        hash = 67 * hash + this.apostado;
        hash = 67 * hash + Objects.hashCode(this.nombre);
        hash = 67 * hash + (this.esBonus ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FiguraPago other = (FiguraPago) obj;
        if (this.numero != other.numero) {
            return false;
        }
        if (!Objects.equals(this.nombre, other.nombre)) {
            return false;
        }
        return true;
    }


    /**
     * Obtiene el numero total de casillas ganadoras, es decir todas
     * aquellas casillas que esten cargadas con 1 se computaran
     * 
     * <p>Por ejemplo: Si la figura es una linea horizontal (en cualquiera
     * de sus filas) la cantidad de casillas ganadoras sera igual a la 
     * cantidad de columnas, es decir: 5.
     * 
     * </p>
     * @return numero total de casillas de este tablero marcadas con 1
     */
    public int cantidadCasillasGanadoras(){
        int result = 0;
        for (int[] casilla : this.casillas) {
            for (int j = 0; j < this.casillas[0].length; j++) {
                result += casilla[j];
            }
        }
        return result;
    }
    
    /**
     * Verifica si el carton actual contiene a otro llamado premio,
     * para el calculo del mismo se tiene en cuenta las posiciones 
     * de los numeros distribuidos en las casillas de cada carton
     * @param premio CartonTablero a verificar (contenido)
     * @return TRUE si el carton actual contiene a premio, FALSE en otro caso
     */
    public boolean contiene(FiguraPago premio){
        boolean response = false;
        if (premio != null && premio.getCasillas() != null) {
            return Matematica.estaContenido(premio.getCasillas(), casillas);
        }
        return response;
    }
    
    /**
     * Verifica si un carton de juego esta contenido en un carton de tablero
     * @param carton
     * @param b
     * @return 
     */
//    public boolean contiene(CartonJuego carton, Bolillero b){
//        boolean response = false;
//        if (carton != null && carton.getCasillas() != null) {
//            return Matematica.estaContenido(casillas, carton.casillasGanadoras(b.getBolasVisibles()));
//        }
//        return response;
//    }
    
    /**
     * Verifica si el carton actual contiene parcialmente a otro llamado premio,
     * esto quiere decir que el carton contenido tiene al menos una casilla
     * en comun con las casillas del carton contenido y ademas el numero
     * de casillas en comun no es igual al numero de casillas distintas de cero
     * @param premio
     * @return 
     */
    public boolean contenidoParcialmenteEn(FiguraPago premio){
        boolean response = false;
        if (premio != null && premio.getCasillas() != null) {
            return Matematica.contenidoParcialMente(casillas, premio.getCasillas());
        }
        return response;
    }
}
