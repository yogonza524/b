/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bingo.util;

import com.bingo.rng.RNG;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public class Matematica {
    
    // <editor-fold defaultstate="collapsed" desc="Atributos">
    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);
    //</editor-fold>

    public static Integer[] crearArregloAleatorioConCeros(Integer[] val, int cantidadDePremiosBonusFijo) {
        Integer[] result = null;
        if (cantidadDePremiosBonusFijo > 0) {
            result = new Integer[cantidadDePremiosBonusFijo];
            for (int i = 0; i < result.length; i++) {
                if (RNG.getInstance().pick() > .5) {
                    result[i] = val[RNG.getInstance().pickInt(val.length)];
                }
                else{
                    result[i] = 0;
                }
            }
        }
        return result;
    }

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor por defecto, no instanciable
     */
    private Matematica(){
        
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Metodos">
    /**
     * Genera valores aleatorios únicos en un rango entre 1 y total
     * @param total numero entero positivo que especifica la cantidad de numeros a generar
     * @return arreglo de numeros aleatorios entre 1 y total
     */
    public static int[] generarValoresAleatoriosUnicos(int total){
        SecureRandom random = new SecureRandom();
        int[] numeros = new int[total];
        boolean[] check = new boolean[total];
        int amountFilled = 0;
        int trial;
        while (amountFilled < total) {
            trial = random.nextInt(total) + 1;
            if (!check[trial - 1]) {
                check[trial - 1] = true;
                numeros[amountFilled] = trial;
                amountFilled++;
            }
        }
        
        return numeros;
    }

     
    /**
     * Implementing Fisher–Yates shuffle
     * @param ar arreglo de objetos a revolver
     */
    public static void revolver(Object[] ar)
    {
      // If running on Java 6 or older, use `new Random()` on RHS here
      Random rnd = ThreadLocalRandom.current();
      for (int i = ar.length - 1; i > 0; i--)
      {
        int index = rnd.nextInt(i + 1);
        // Simple swap
        Object a = ar[index];
        ar[index] = ar[i];
        ar[i] = a;
      }
    }
    
    /**
     * Implementing Fisher–Yates shuffle
     * @param ar arreglo de objetos a revolver
     */
    public static void revolver(int[] ar)
    {
      // If running on Java 6 or older, use `new Random()` on RHS here
      Random rnd = ThreadLocalRandom.current();
      for (int i = ar.length - 1; i > 0; i--)
      {
        int index = rnd.nextInt(i + 1);
        // Simple swap
        int a = ar[index];
        ar[index] = ar[i];
        ar[i] = a;
      }
    }
    
    /**
     * Obtiene el porcentaje de un numero en funcion de otro
     * aplicando la relacion (base * 100) / pct
     * @param base numero total, mayor que 0 y que pct
     * @param pct razon de total, mayor que 0 y menor que pct
     * @return calculo del porcentaje entre base y pct
     */
    public static BigDecimal porcentaje(BigDecimal base, BigDecimal pct){
        return base.multiply(ONE_HUNDRED).divide(pct,2, RoundingMode.HALF_UP);
    }
    
    /**
     * Obtiene el porcentaje de un numero en funcion de otro
     * aplicando la relacion (base * 100) / pct
     * @param base numero total, mayor que 0 y que pct
     * @param pct razon de total, mayor que 0 y menor que pct
     * @return calculo del porcentaje entre base y pct
     */
    public static BigDecimal porcentaje(Integer base, Integer pct){
        return BigDecimal.valueOf(base).multiply(ONE_HUNDRED).divide(BigDecimal.valueOf(pct),2, RoundingMode.HALF_UP);
    }
    
    
    /**
     * Cuenta el numero de elementos de una matriz cuyo elemento[i][j] es mayor a cero
     * @param matrix matriz de numeros enteros
     * @return conteo total de elementos que cumplen la condicion de ser mayores a cero
     */
    public static int contarLosPositivos(int[][] matrix){
        int total = 0;
        for (int[] matrix1 : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                total += matrix1[j] > 0 ? 1 : 0;
            }
        }
        return total;
    }
    
    /**
     * Verifica si un numero entero se encuentra dentro de un arreglo de enteros
     * @param array arreglo de numeros enteros
     * @param key numero entero a evaluar
     * @return TRUE si key esta presente en el conjunto array, FALSE en otro caso
     */
    public static boolean contiene(final int[] array, final int key) {
        for (final int i : array) {
            if (i == key) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Verifica si una matriz se encuentra contenida dentro de otra, ambas
     * deben ser de la misma dimension
     * @param contenido matriz a evaluar contra el contenedor
     * @param contenedor matriz base de la evaluacion
     * @return TRUE si cada elemento igual a 1 en contenido es igual a 1 en contenedor
     */
    public static boolean estaContenido(final int[][] contenido, final int[][] contenedor){
        for (int i = 0; i < contenido.length; i++) {
            for (int j = 0; j < contenido[0].length; j++) {
                //Comparar los valores iguales a 1 en el contenido
                if (contenido[i][j] == 1) {
                    //bit encendido, comparar la posicion del mismo en el contenedor
                    if (contenido[i][j] != contenedor[i][j]) {
                        //Valores distintos, no seguir buscando
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    public static boolean estaContenido(final int[][] contenido, final Integer[][] contenedor){
        for (int i = 0; i < contenido.length; i++) {
            for (int j = 0; j < contenido[0].length; j++) {
                //Comparar los valores iguales a 1 en el contenido
                if (contenido[i][j] == 1) {
                    //bit encendido, comparar la posicion del mismo en el contenedor
                    if (contenido[i][j] != contenedor[i][j]) {
                        //Valores distintos, no seguir buscando
                        return false;
                    }
                }
            }
        }
        return true;
    }
    
    /**
     * Verifica si una matriz de enteros llamada menorJerarquia se encuentra
     * contenida en otra llamada contenedorMayor, para el calculo se procede
     * a crear una matriz auxiliar binaria en la cual se cargaran con 1
     * las posiciones en las cuales menorJerarquia y contenedorMayor tengan
     * el mismo valor, cero en caso contrario
     * @param menorJerarquia
     * @param contenedorMayor
     * @return TRUE si menorJerarquia esta contenido parcialmente en contenedorMayor, FALSE en otro caso
     */
    public static boolean contenidoParcialMente(final int[][] menorJerarquia, final int[][] contenedorMayor){
        int filas = contenedorMayor.length;
        int columnas = contenedorMayor[0].length;
        int[][] casillas = new int[filas][columnas];
        int posicionesIguales = 0;
        for (int i = 0; i < contenedorMayor.length; i++) {
            for (int j = 0; j < contenedorMayor[0].length; j++) {
                casillas[i][j] = menorJerarquia[i][j] == contenedorMayor[i][j]? contenedorMayor[i][j] : 0;
                posicionesIguales += casillas[i][j];
            }
        }
        return !Arrays.deepEquals(contenedorMayor,casillas) && posicionesIguales > 0;
    }
    
    /**
     * Obtiene la matriz binaria de ocurrencias de un arreglo de numeros 
     * enteros (bolillero) en una matriz de numeros enteros (carton)
     * @param bolillero arreglo de numeros enteros positivos
     * @param carton matriz de numeros enteros positivos
     * @return matriz de numeros enteros binarios, cada 1 representa la existencia de un numero de bolillero en alguna posicion del carton
     */
    public static int[][] figuraBinaria(int[] bolillero, int[][] carton){
        int[][] salida = new int[carton.length][carton[0].length];
        for (int i = 0; i < carton.length; i++) {
            for (int j = 0; j < carton[0].length; j++) {
                if (contiene(bolillero, carton[i][j])) {
                    salida[i][j] = 1;
                }
            }
        }
        return salida;
    }
    
    public static Integer[][] figuraBinaria(int[] bolillero, Integer[][] carton){
        Integer[][] salida = new Integer[carton.length][carton[0].length];
        for (int i = 0; i < carton.length; i++) {
            for (int j = 0; j < carton[0].length; j++) {
                if (contiene(bolillero, carton[i][j])) {
                    salida[i][j] = 1;
                }
            }
        }
        return salida;
    }
    
    /**
     * Redondea un numero real en n lugares despues de la coma
     * @param value numero a redondear
     * @param places numero entero que representa los lugares despues de la coma
     * @return numero real redondeado despues de la coma
     */
    public static double redondear(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        double v = value * factor;
        long tmp = Math.round(v);
        
        return (double) tmp / factor;
    }
    
    /**
     * Evalua si un numero real se encuentra dentro de un intervalo real
     * @param min extremo inferior del intervalo a evaluar
     * @param max extremo superior del intervalo a evaluar
     * @param val valor a buscar dentro del intervalo
     * @return TRUE si val se encuentra entre min  y max (inclusive extremos), FALSE en otro caso
     */
    public static boolean entre(double min, double max, double val){
        return val >= min && val < max;
    }
    
    /**
     * Muestra una matriz de enteros por la salida estandar
     * @param matriz matriz de numeros enteros primitivos
     */
    public static void mostrarMatriz(int[][] matriz){
        System.out.println("Matriz(" + matriz.length + ", " + matriz[0].length +")");
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[0].length; j++) {
                System.out.print("(" + i + ", " + j + "): " + matriz[i][j] + "\t");
            }
            System.out.println("");
        }
    }
    
    /**
     * Convierte una matriz de enteros (arreglo de arreglos) en una matriz
     * de enteros primitivos
     * @param matriz cadena de caracteres del tipo {{1,2,3},{4,5,6},{7,8,9}}
     * @return matriz de enteros primitivos
     */
    public static int[][] toIntMatrix2D(String matriz){
        
        matriz = matriz.substring(0, matriz.length() - 1).replaceFirst("\\{", "");
         
         String[] split = matriz.split("\\},\\{");
         int[][] result = new int[split.length][5];
         int i = 0;
         for(String s : split){
             String numeros = s.replaceAll("\\{", "").replaceAll("\\}", "");
             numeros = "{" + numeros + "}";
             result[i] = toIntArray(numeros);
             i++;
         }
         return result;
    }
    
    public static int[] toIntArray(String array){
        return Arrays.stream(array.substring(1, array.length()-1).split(","))
                                .map(String::trim).mapToInt(Integer::parseInt).toArray();
    }
    
    public static boolean leFaltaUno(int[][] figuraBinariaSinUno, int[][] figuraBinariaCompleta){
        int iguales = 0;
        int totalCasillasFiguraCompleta = contarLosPositivos(figuraBinariaCompleta);
        for (int i = 0; i < figuraBinariaSinUno.length; i++) {
            for (int j = 0; j < figuraBinariaSinUno[0].length; j++) {
                if (figuraBinariaSinUno[i][j] == figuraBinariaCompleta[i][j] 
                        && figuraBinariaSinUno[i][j] == 1) {
                    iguales++;
                }
            }
        }
        return iguales == totalCasillasFiguraCompleta - 1;
    }
    
    public static boolean leFaltaUno(Integer[][] figuraBinariaSinUno, int[][] figuraBinariaCompleta){
        int iguales = 0;
        int totalCasillasFiguraCompleta = contarLosPositivos(figuraBinariaCompleta);
        for (int i = 0; i < figuraBinariaSinUno.length; i++) {
            for (int j = 0; j < figuraBinariaSinUno[0].length; j++) {
                if (figuraBinariaSinUno[i][j] == figuraBinariaCompleta[i][j] 
                        && figuraBinariaSinUno[i][j] == 1) {
                    iguales++;
                }
            }
        }
        return iguales == totalCasillasFiguraCompleta - 1;
    }
    //</editor-fold>
}
