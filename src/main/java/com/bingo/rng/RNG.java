/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bingo.rng;

import java.security.SecureRandom;
import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;

/**
 * RNG (Random Number Generator)
 * Clase necesaria para obtener numeros pseudoaleatorios
 * capaces de pasar las pruebas estadisticas de:
 * 1. Medias
 * 2. Varianzas
 * 3. Chi-Cuadrado
 * 4. Kolmogorov-Smirnoff
 * 5. De independencia
 * 
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public class RNG {
    
    // <editor-fold defaultstate="collapsed" desc="Atributos">
    private double[] numeros;
    private final int totalAGenerar;
    private final double confianza;
    
    private static RNG instancia;
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    private RNG(int totalAGenerar, double confianza){
        this.totalAGenerar = totalAGenerar;
        this.confianza = confianza;
        this.numeros = this.generarNumeros(totalAGenerar);
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">
    /**
     * Obtiene la instancia por defecto del generador de números
     * pseudo-aleatorios con 100 numeros y una confianza del 90%
     * @return intancia singleton del generador RNG
     */
    public static RNG getInstance(){
        if (RNG.instancia == null) {
            instancia = new RNG(100, 0.9);
        }
        return instancia;
    }
    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Metodos">
    /**
     * Genera un conjunto de numeros pseudoaleatorios en funcion
     * del numero total de numeros a generar y la confianza del mismo
     * @param total cantidad de numeros a generar
     */
    private double[] generarNumeros(int total) {
        double[] result = new double[total];
        synchronized(RNG.class) {
            boolean seguirGenerando = true;
            int n = total;
            double r = 0.0;
            double alpha = 100 - confianza;
            
            while(seguirGenerando){
                Random random = new SecureRandom();
                double[] numbers = new double[n];
                for (int i = 0; i < n; i++) {
                    numbers[i] = random.nextDouble();
                    r += numbers[i];
                }
                r = r / n;
                
                boolean cumplemedias = cumplePruebaDeMedias(numbers, alpha);
                boolean cumpleVarianza = cumplePruebaDeVarianza(numbers);
                boolean cumpleChiCuadrado = cumplePruebaChiCuadrado(numbers);
                boolean cumpleCorridasArribaYabajo = cumplePruebaDeCorridasArribaYabajo(numbers);
                
                if (cumplemedias && cumpleVarianza && cumpleChiCuadrado
                        && cumpleCorridasArribaYabajo) {
                    result = numbers;
                    seguirGenerando = false;
                }
            }
        }
        return result;
    }
    
    /**
     * Verifica si el conjunto de numeros pseudoaleatorios cumple
     * con la prueba de medias
     * @param ri conjunto de numeros pseudo-aleatorios
     * @param alpha estadistico de confianza
     * @return TRUE si no se puede rechazar que ri tiene una media r = 1/2, FALSE en otro caso
     */
    private boolean cumplePruebaDeMedias(double[] ri, double alpha){
        boolean cumple = false;
        int n = ri.length;
        double r = 0.0;
        for (int i = 0; i < n; i++) {
            r += ri[i];
        }
        r = r / n;
        double min = mediaLI(n, alpha);
        double max = mediaLS(n, alpha);
        if (between(min, max, r)) {
            cumple = true;
        }
        return cumple;
    }
    
    /**
     * Verifica si el conjunto de numeros pseudo-aleatorios cumple
     * con la prueba de las varianzas. La misma establece que todo conjunto
     * de numeros pseudo-aleatorios es aceptable si no se puede rechazar
     * que su varianza es igual a 1/12
     * @param ri conjunto de numeros pseudo-aleatorios
     * @return TRUE si no se puede rechazar que el conjunto ri tiene una varianza de 1/12, FALSE en otro caso
     */
    private boolean cumplePruebaDeVarianza(double[] ri){
        boolean cumple = false;
        double prom = promedio(ri);
        double var = varianza(ri, prom);
        double li = varianzaLI(ri.length);
        double ls = varianzaLS(ri.length);
        
        if (between(li, ls, var)) {
            cumple = true;
        }
        
        return cumple;
    }
    
    /**
     * Prueba que establece si un conjunto de numeros pseudo-aleatorios
     * cumple con la prueba de Chi-Cuadrado
     * @param ri conjunto de numeros pseudo-aleatorios
     * @return TRUE si el conjunto cumple con la prueba, FALSE en otro caso
     */
    private boolean cumplePruebaChiCuadrado(double[] ri){
        int n = ri.length;
        int m = (int) Math.sqrt(n);
        int[] frecuenciaObservada = new int[m];
        int frecuenciaEsperada = n / m;
        double intervalLenght = 1 / (double)m;
        double delta;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                delta = j == 0 ? 0.0 : intervalLenght / 100.0;
                double min = j * intervalLenght + delta;
                double max = min + intervalLenght - delta;
                if (between(min, max, ri[i])) {
                    frecuenciaObservada[j] += 1;
                }
            }
        }
        double chi = 0.0;
        for (int i = 0; i < m; i++) {
            chi += Math.pow((double)frecuenciaObservada[i] - frecuenciaEsperada, 2) / frecuenciaEsperada;
        }
        double chiEstadistico = 0.0;
        if (n == 100) {
            chiEstadistico = 135.8069; //De la tabla de Chi cuadrado, v = 100 y alpha = 0.001
        }
        
        return chi < chiEstadistico;
    }
    
    /**
     * Prueba de independencia
     * @param ri conjunto de numeros pseudo-aleatorios
     * @return TRUE si cumple con la prueba, FALSE en otro caso
     */
    private static boolean cumplePruebaDeCorridasArribaYabajo(double[] ri){
        int c0 = 0;
        int n = ri.length;
        int[] s = new int[ri.length - 1];
        for (int i = 0; i < s.length; i++) {
            s[i] = ri[i] < ri[i+1]? 1 : 0;
        }
        for (int i = 0; i < s.length - 1; i++) {
            c0 += s[i] != s[i + 1] ? 1 : 0;
        }
        c0++;
        double mu = ((2.0 * n) - 1.0) / 3.0;
        double var = ((16.0 * n) - 29.0) / 90.0;
        double z0 = Math.abs((c0 - mu) / Math.sqrt(var));
        double z = 2.57; //Estadistico z(0,005) = z((1 - 0.99) / 2) sobrestimado
        
        
        return z0 < z;
    }
    
    /**
     * Selecciona un número pseudo-aleatorio (0-1) desde el
     * conjunto de numeros generados anteriormente, si no hay
     * numeros disponibles se generaran automaticamente
     * @return numero psudo-aleatorio del conjunto de numeros disponibles
     */
    public synchronized double pick(){
        synchronized(RNG.class){
            if (numeros.length == 0) {
                this.numeros = generarNumeros(totalAGenerar); //Si no quedan numeros en el arreglo fuerzo la generacion
            }     
            int total = numeros.length;
            double value = numeros[new Random().nextInt(total)];
            numeros = (double[])ArrayUtils.removeElement(numeros, value);   
            return value;
        }
    }
    
    /**
     * Obtiene un numero entero comprendido entre 0 y "n"
     * de manera pseudo-aleatoria utilizando la estrategia
     * de la funcion inversa con a = 0 y b = n
     * @param n numero total de numeros del intervalo a utilizar
     * @return numero entero pseudo-aleatorio perteneciente al intervalo [0 - n]
     */
    public synchronized int pickInt(int n){
        synchronized(RNG.class){
            if (numeros.length == 0) {
                this.numeros = generarNumeros(totalAGenerar); //Si no quedan numeros en el arreglo fuerzo la generacion
            }
            int total = numeros.length;
            double value = numeros[new Random().nextInt(total)];
            numeros = (double[])ArrayUtils.removeElement(numeros, value);   
            return (int) (value * n);
        }
    }
    
    /**
     * Obtiene un numero pseudo-aleatorio de tipo real comprendido entre 0 y "n",
     * Si no hay numeros disponibles en el arreglo se generan automaticamente
     * @param n numero entero positivo, si n es menor o igual que cero entonces la salida sera siempre 0.0
     * @return numero real positivo comprendido en el intervalo [0 -n], si n es menor o igual a cero siempre retorna 0.0
     */
    public synchronized double pickDouble(int n){
        synchronized(RNG.class){
            if (n > 0) {
                if (numeros.length == 0) {
                    this.numeros = generarNumeros(totalAGenerar); //Si no quedan numeros en el arreglo fuerzo la generacion
                }
                int total = numeros.length;
                double value = numeros[new Random().nextInt(total)];
                numeros = (double[])ArrayUtils.removeElement(numeros, value);   
                return value * n;
            }
            return 0.0;
        }
    }
    
    /**
     * Obtiene un arreglo de numeros pseudo-aleatorios, el numero total de numeros
     * esta dado por el valor de n, si n es menor o igual a cero entonces
     * retorna null
     * @param n numero total del arreglo de numeros pseudo-aleatorios
     * @return 
     */
    public synchronized double[] pickArray(int n){
        synchronized(RNG.class){
            double[] result = null;
            if (n > 0) {
                result = new double[n];
                if (numeros.length == 0) {
                    this.numeros = generarNumeros(totalAGenerar); //Si no quedan numeros en el arreglo fuerzo la generacion
                }
                int total = numeros.length;
                for (int i = 0; i < n; i++) {
                    result[i] = numeros[new Random().nextInt(total)];
                    numeros = (double[])ArrayUtils.removeElement(numeros, result[i]);
                }   
            }
            return result;
        }
    }
    
    /**
     * Genera valores enteros aleatorios únicos en un rango entre 1 y total
     * @param total numero entero positivo que especifica la cantidad de numeros a generar
     * @return arreglo de numeros aleatorios entre 1 y total
     */
    public int[] generarEnterosUnicos(int total){
        int[] result = new int[total];
        boolean[] check = new boolean[total];
        int amountFilled = 0;
        int trial;
        while (amountFilled < total) {
            trial = this.pickInt(total) + 1;
            if (!check[trial - 1]) {
                check[trial - 1] = true;
                result[amountFilled] = trial;
                amountFilled++;
            }
        }
        
        return result;
    }
    
    /**
     * Obtiene el valor del estadistico z en función del número "alpha"
     * Esto quiere decir que "alpha", el estadistico que establece
     * el grado de acceptación es el que nos permite obtener z
     * @param alpha numero real que representa el grado de aceptación de la distribución
     * @return numero real que representa el valor de z
     */
    private double zByAlpha(Double alpha){
        Double value = alpha;
        if (value >= 1) {
            value = value / 100;
        }
        value = value / 2;
        if (value.equals(new Double("0.001"))) {
            //Alpha = 0.1%, Confidence = 99.9%;
            return 3.09; 
        }
        if (value.equals(new Double("0.005"))) {
            //Alpha = 1%, Confidence = 99%;
            return 2.58; 
        }
        if (value.equals(new Double("0.01"))) {
            //Alpha = 2%, Confidence = 98%;
            return 2.33; 
        }
        if (value.equals(new Double("0.015"))) {
            //Alpha = 3%, Confidence = 97%;
            return 2.17; 
        }
        if (value.equals(new Double("0.02"))) {
            //Alpha = 4%, Confidence = 96%;
            return 2.06; 
        }
        if (value.equals(new Double("0.0250"))) {
            //Alpha = 5%, Confidence = 95%;
            return 1.96; 
        }
        if (value.equals(new Double("0.03"))) {
            //Alpha = 6%, Confidence = 94%;
            return 1.89; 
        }
        if (value.equals(new Double("0.035"))) {
            //Alpha = 7%, Confidence = 93%;
            return 1.82; 
        }
        if (value.equals(new Double("0.04"))) {
            //Alpha = 8%, Confidence = 92%;
            return 1.76; 
        }
        if (value.equals(new Double("0.045"))) {
            //Alpha = 9%, Confidence = 91%;
            return 1.70; 
        }
        if (value.equals(new Double("0.05"))) {
            //Alpha = 10%, Confidence = 90%;
            return 1.65; 
        }
        //Verifico que el estadistico para chi cuadrado
        if (value.equals(new Double("0.4975"))) {
            //Alpha = 99%, Confidence = 1%;
            return 0.01; 
        }
        if (value.equals(new Double("0.495"))) {
            //Alpha = 98%, Confidence = 2%;
            return 0.02; 
        }
        if (value.equals(new Double("0.4925"))) {
            //Alpha = 97%, Confidence = 3%;
            return 0.02; 
        }
        if (value.equals(new Double("0.49"))) {
            //Alpha = 96%, Confidence = 4%;
            return 0.03; 
        }
        if (value.equals(new Double("0.4875"))) {
            //Alpha = 95%, Confidence = 5%;
            return 0.04; 
        }
        if (value.equals(new Double("0.485"))) {
            //Alpha = 94%, Confidence = 6%;
            return 0.04; 
        }
        if (value.equals(new Double("0.4825"))) {
            //Alpha = 93%, Confidence = 7%;
            return 0.05; 
        }
        if (value.equals(new Double("0.48"))) {
            //Alpha = 92%, Confidence = 8%;
            return 0.06; 
        }
        if (value.equals(new Double("0.4775"))) {
            //Alpha = 91%, Confidence = 9%;
            return 0.06; 
        }
        if (value.equals(new Double("0.475"))) {
            //Alpha = 90%, Confidence = 10%;
            return 0.07; 
        }
        return value;
    }
    
    /**
     * Obtiene el valor de chi-cuadrado en función de alpha y los grados
     * de libertad, el mismo se obtiene desde una tabla
     * Ver tabla de Chi-Cuadrado
     * @param alpha numero real que representa el grado de aceptacion de la distribucion
     * @param freeDegrees numero real que representa los grados de libretad de la tabla de Chi-Cuadrado
     * @return numero real que representa el estadistico X (chi-cuadrado)
     */
    private double chi2ByAlpha(double alpha, int freeDegrees){
        double alfa = alpha;
        double result = 0.0;
        if (alfa >= 1) {
            alfa = alfa / 100;
        }
        if (freeDegrees > 100) { //Solo se calcula para grados de libertad > 100
            return 0.5 * (zByAlpha(alfa) + Math.pow(Math.sqrt((2 * freeDegrees) - 1.0), 2));
        }
        return result;
    }
    
    /**
     * Limite inferior para el cálculo de la prueba de medias
     * @param n numero de elementos del conjunto a evaluar
     * @param alpha grado de aceptación de la distribucion
     * @return numero real que representa el limite inferior de la media
     */
    private double mediaLI(int n, double alpha){
        return 0.5 - zByAlpha(alpha) * (1/(12 * Math.sqrt(n)));
    }
    
    /**
     * Obtiene el limite superior para el calculo de la prueba de medias
     * @param n numero entero positivo, representa el numero total de elementos en la muestra
     * @param alpha numero real positivo entre 0 y 1, representa el grado de libertad de la distribucion
     * @return numero real positivo entre 0 y 1, representa el limite superior
     */
    private double mediaLS(int n, double alpha){
        return 0.5 + zByAlpha(alpha) * (1/(12 * Math.sqrt(n)));
    }
    
    /**
     * Metodo accesorio, se utiliza para saber si un numero real se encuentra
     * comprendido en un intervalo, inclusive los extremos
     * @param min limite inferior del intervalo
     * @param max limite superior del intervalo
     * @param val numero real a verificar
     * @return TRUE si el numero val se encuentra en el intervalo, FALSE en otro caso
     */
    private boolean between(double min, double max, double val){
        return (val >= min && val <= max);
    }
    
    private double varianza(double[] ri, double rPromedio){
        double result = 0.0;
        int n = ri.length;
        for (int i = 0; i < ri.length; i++) {
            result += Math.pow(ri[i] - rPromedio, 2);
        }
        return (n > 1 ? result / (n -1) : Double.MIN_VALUE);
    }
    
    private double varianzaLI(int n){
        return (n > 1 ? 67.3275 / (12 * (n - 1)) : 0.0);
    }
    
    private double varianzaLS(int n){
        return (n > 1 ? 140.1697 / (12 * (n - 1)) : 0.0);
    }
    
    private double promedio(double[] ri){
        double r = 0.0;
        for (int i = 0; i < ri.length; i++) {
            r += ri[i];
        }
        return r / ri.length;
    }
    //</editor-fold>
}
