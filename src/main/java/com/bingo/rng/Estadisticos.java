/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bingo.rng;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 * 
 * Clase necesaria para implementar el comportamiento de la tabla de distribucion
 * normal para pruebas estadisticas. Ref: https://www.uam.es/personal_pdi/ciencias/gallardo/Tablas-normal-chi-t-F.pdf
 */
public class Estadisticos {
    
    // <editor-fold defaultstate="collapsed" desc="Atributos">
    private static Map<Double,Double> z;
    
    static{
        z = new HashMap<>();
    }
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Metodos">
    public static void cargarValoresZ(){
        z.put(0.00, 0.5000);
        z.put(0.01, 0.4960);
        z.put(0.02, 0.4920);
        z.put(0.03, 0.4880);
        z.put(0.04, 0.4840);
        z.put(0.05, 0.4801);
        z.put(0.06, 0.4761);
        z.put(0.07, 0.4721);
        z.put(0.08, 0.4681);
        z.put(0.09, 0.4641);
        
        z.put(0.10, 0.4602);
        z.put(0.11, 0.4562);
        z.put(0.12, 0.4522);
        z.put(0.13, 0.4483);
        z.put(0.14, 0.4443);
        z.put(0.15, 0.4404);
        z.put(0.16, 0.4364);
        z.put(0.17, 0.4325);
        z.put(0.18, 0.4286);
        z.put(0.19, 0.4641);
        
        z.put(0.20,0.4207);
        z.put(0.21,0.4168);
        z.put(0.22,0.4129);
        z.put(0.23,0.4090);
        z.put(0.24,0.4052);
        z.put(0.25,0.4013);
        z.put(0.26,0.3974);
        z.put(0.27,0.3936);
        z.put(0.28,0.3897);
        z.put(0.29,0.3859);
        
        z.put(0.30,0.3821);
        z.put(0.31,0.3783);
        z.put(0.32,0.3745);
        z.put(0.33,0.3707);
        z.put(0.34,0.3669);
        z.put(0.35,0.3632);
        z.put(0.36,0.3594);
        z.put(0.37,0.3557);
        z.put(0.38,0.3520);
        z.put(0.39,0.3483);
    }
    
    
    public static double zByAlpha(Double alpha){
        Double value = alpha;
        if (value >= 1) {
            value = value / 100;
        }
        value = value / 2;
        if (value.equals(new Double("0.001"))) {
            return 3.09; //Alpha = 0.1%, Confidence = 99.9%;
        }
        if (value.equals(new Double("0.005"))) {
            return 2.58; //Alpha = 1%, Confidence = 99%;
        }
        if (value.equals(new Double("0.01"))) {
            return 2.33; //Alpha = 2%, Confidence = 98%;
        }
        if (value.equals(new Double("0.015"))) {
            return 2.17; //Alpha = 3%, Confidence = 97%;
        }
        if (value.equals(new Double("0.02"))) {
            return 2.06; //Alpha = 4%, Confidence = 96%;
        }
        if (value.equals(new Double("0.0250"))) {
            return 1.96; //Alpha = 5%, Confidence = 95%;
        }
        if (value.equals(new Double("0.03"))) {
            return 1.89; //Alpha = 6%, Confidence = 94%;
        }
        if (value.equals(new Double("0.035"))) {
            return 1.82; //Alpha = 7%, Confidence = 93%;
        }
        if (value.equals(new Double("0.04"))) {
            return 1.76; //Alpha = 8%, Confidence = 92%;
        }
        if (value.equals(new Double("0.045"))) {
            return 1.70; //Alpha = 9%, Confidence = 91%;
        }
        if (value.equals(new Double("0.05"))) {
            return 1.65; //Alpha = 10%, Confidence = 90%;
        }
        //Verifico que el estadistico para chi cuadrado
        if (value.equals(new Double("0.4975"))) {
            return 0.01; //Alpha = 99%, Confidence = 1%;
        }
        if (value.equals(new Double("0.495"))) {
            return 0.02; //Alpha = 98%, Confidence = 2%;
        }
        if (value.equals(new Double("0.4925"))) {
            return 0.02; //Alpha = 97%, Confidence = 3%;
        }
        if (value.equals(new Double("0.49"))) {
            return 0.03; //Alpha = 96%, Confidence = 4%;
        }
        if (value.equals(new Double("0.4875"))) {
            return 0.04; //Alpha = 95%, Confidence = 5%;
        }
        if (value.equals(new Double("0.485"))) {
            return 0.04; //Alpha = 94%, Confidence = 6%;
        }
        if (value.equals(new Double("0.4825"))) {
            return 0.05; //Alpha = 93%, Confidence = 7%;
        }
        if (value.equals(new Double("0.48"))) {
            return 0.06; //Alpha = 92%, Confidence = 8%;
        }
        if (value.equals(new Double("0.4775"))) {
            return 0.06; //Alpha = 91%, Confidence = 9%;
        }
        if (value.equals(new Double("0.475"))) {
            return 0.07; //Alpha = 90%, Confidence = 10%;
        }
        return value;
    }
    
    public static double chi2ByAlpha(double alpha, int freeDegrees){
        double result = 0.0;
        if (alpha >= 1) {
            alpha = alpha / 100;
        }
        if (freeDegrees > 100) { //Solo se calcula para grados de libertad > 100
            return 0.5 * (zByAlpha(alpha) + Math.pow(Math.sqrt((2 * freeDegrees) - 1), 2));
        }
        return result;
    }
    
    public static double MediaLI(int n, double alpha){
        return 0.5 - zByAlpha(alpha) * (1/(12 * Math.sqrt(n)));
    }
    
    public static double MediaLS(int n, double alpha){
        return 0.5 + zByAlpha(alpha) * (1/(12 * Math.sqrt(n)));
    }
    
    public static boolean between(double min, double max, double val){
        return (val >= min && val <= max);
    }
    
    public static double varianza(double[] ri, double rPromedio){
        double result = 0.0;
        int n = ri.length;
        for (int i = 0; i < ri.length; i++) {
            result += Math.pow(ri[i] - rPromedio, 2);
        }
        return (n > 1 ? result / (n -1) : Double.MIN_VALUE);
    }
    
    public static double VarianzaLI(int n){
        return (n > 1 ? 67.3275 / (12 * (n - 1)) : 0.0);
    }
    
    public static double VarianzaLS(int n){
        return (n > 1 ? 140.1697 / (12 * (n - 1)) : 0.0);
    }
    
    public static double promedio(double[] ri){
        double r = 0.0;
        for (int i = 0; i < ri.length; i++) {
            r += ri[i];
        }
        return r / ri.length;
    }
    //</editor-fold>
}
