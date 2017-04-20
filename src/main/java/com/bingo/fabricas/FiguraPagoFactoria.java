/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bingo.fabricas;

import com.bingo.entidades.FiguraPago;
import com.core.bingosimulador.Juego;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fabrica de cartones de tabla de pagos, no instanciable
 * 
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public class FiguraPagoFactoria {

    private static FiguraPago diamante(int factorGanancia) {
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        
        FiguraPago diamante = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name + "1")
                .casilla(0, 2)
                .casilla(1, 1)
                .casilla(1, 3)
                .casilla(2, 2)
                .crear();
        return diamante;
    }
    
    private FiguraPagoFactoria(){} //Constructor private. Singleton parcial
    
    /**
     * Obtiene un carton ganador con la configuracion de casillas
     * en la cual la primer fila posee 1 en cada casilla, cero en las demas
     * @param factorGanancia valor entero positivo, representa el pago de creditos
     * otorgado al apostador al salir esta figura en el carton de juego
     * @return Carton de tablero de pagos con figura en forma de linea en fila 1
     */
    public static FiguraPago[] linea(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago[] result;
        
        //Creo la primer linea
        FiguraPago linea1 = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name + "1")
                .casilla(0, 0)
                .casilla(0, 1)
                .casilla(0, 2)
                .casilla(0, 3)
                .casilla(0, 4)
                .crear();
        
        //Creo la primer linea
        FiguraPago linea2 = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name + "2")
                .casilla(1, 0)
                .casilla(1, 1)
                .casilla(1, 2)
                .casilla(1, 3)
                .casilla(1, 4)
                .crear();
        
        //Creo la primer linea
        FiguraPago linea3 = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name + "3")
                .casilla(2, 0)
                .casilla(2, 1)
                .casilla(2, 2)
                .casilla(2, 3)
                .casilla(2, 4)
                .crear();
        
        result = new FiguraPago[]{linea1,linea2,linea3};
        
        return result;
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago[] dosLineas(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago[] result;
        
        FiguraPago configuracion1 = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name + "1")
                .casilla(0, 0)
                .casilla(0, 1)
                .casilla(0, 2)
                .casilla(0, 3)
                .casilla(0, 4)
                .casilla(1, 0)
                .casilla(1, 1)
                .casilla(1, 2)
                .casilla(1, 3)
                .casilla(1, 4)
                .crear();
        
        FiguraPago configuracion2 = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name + "2")
                .casilla(0, 0)
                .casilla(0, 1)
                .casilla(0, 2)
                .casilla(0, 3)
                .casilla(0, 4)
                .casilla(2, 0)
                .casilla(2, 1)
                .casilla(2, 2)
                .casilla(2, 3)
                .casilla(2, 4)
                .crear();
        
        FiguraPago configuracion3 = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name + "3")
                .casilla(1, 0)
                .casilla(1, 1)
                .casilla(1, 2)
                .casilla(1, 3)
                .casilla(1, 4)
                .casilla(2, 0)
                .casilla(2, 1)
                .casilla(2, 2)
                .casilla(2, 3)
                .casilla(2, 4)
                .crear();
        
        result = new FiguraPago[]{configuracion1, configuracion2,configuracion3};
        
        return result;
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago consuelo1(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        
        
        FiguraPago consuelo1 = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name)
                .casilla(0, 2)
                .casilla(1, 3)
                .casilla(2, 4)
                .crear();
        
        return consuelo1;
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago consuelo2(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        
        FiguraPago consuelo1 = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name)
                .casilla(0, 2)
                .casilla(1, 1)
                .casilla(2, 0)
                .crear();
        
        return consuelo1;
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago tresLineas(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago.FiguraPagoBuilder cb = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name)
                ;
        
        int lineas = Juego.getLineas();
        int columnas = Juego.getColumnas();
        
        for (int i = 0; i < lineas; i++) {
            for (int j = 0; j < columnas; j++) {
                cb.casilla(i, j);
            }
        }

        return cb.crear();
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago cuadrado(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago.FiguraPagoBuilder cb = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name)
                .esBonus()
                .casilla(0, 0)
                .casilla(0, 1)
                .casilla(0, 2)
                .casilla(0, 3)
                .casilla(0, 4)
                .casilla(1, 0)
                .casilla(1, 4)
                .casilla(2, 0)
                .casilla(2, 1)
                .casilla(2, 2)
                .casilla(2, 3)
                .casilla(2, 4)
                ;
        return cb.crear();
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago h(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago.FiguraPagoBuilder cb = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name)
                .casilla(0, 0)
                .casilla(0, 4)
                .casilla(2, 0)
                .casilla(2, 4)
                .casilla(1, 0)
                .casilla(1, 1)
                .casilla(1, 2)
                .casilla(1, 3)
                .casilla(1, 4)
                ;
        return cb.crear();
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago gamma(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago.FiguraPagoBuilder cb = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name)
                .casilla(0, 0)
                .casilla(0, 1)
                .casilla(0, 2)
                .casilla(0, 3)
                .casilla(0, 4)
                .casilla(1, 1)
                .casilla(1, 3)
                .casilla(2, 1)
                .casilla(2, 2)
                .casilla(2, 3)
                ;
        return cb.crear();
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago tt(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago.FiguraPagoBuilder cb = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name)
                .casilla(0, 0)
                .casilla(0, 1)
                .casilla(0, 2)
                .casilla(0, 3)
                .casilla(0, 4)
                .casilla(1, 1)
                .casilla(1, 3)
                .casilla(2, 1)
                .casilla(2, 3)
                ;
        return cb.crear();
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago grilla(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago.FiguraPagoBuilder cb = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name);
        
        int lineas = Juego.getLineas();
        int columnas = Juego.getColumnas();
        
        for (int i = 0; i < lineas; i++) {
            for (int j = 0; j < columnas; j++) {
                if ((i + j) % 2 == 0) {
                    cb.casilla(i, j);
                }
            }
        }
                
        return cb.crear();
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago grillaInvertida(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago.FiguraPagoBuilder cb = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name);
        
        int lineas = Juego.getLineas();
        int columnas = Juego.getColumnas();
        
        for (int i = 0; i < lineas; i++) {
            for (int j = 0; j < columnas; j++) {
                if ((i + j) % 2 != 0) {
                    cb.casilla(i, j);
                }
            }
        }
                
        return cb.crear();
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago y(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago.FiguraPagoBuilder cb = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name)
                .casilla(0, 0)
                .casilla(0, 4)
                .casilla(1, 0)
                .casilla(1, 1)
                .casilla(1, 2)
                .casilla(1, 3)
                .casilla(1, 4)
                .casilla(2, 2)
                ;
        return cb.crear();
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago piramide(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago.FiguraPagoBuilder cb = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name)
                .casilla(0, 2)
                .casilla(1, 1)
                .casilla(1, 2)
                .casilla(1, 3)
                .casilla(2, 0)
                .casilla(2, 1)
                .casilla(2, 2)
                .casilla(2, 3)
                .casilla(2, 4)
                ;
        return cb.crear();
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago piramideComun(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago.FiguraPagoBuilder cb = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name)
                .casilla(0, 2)
                .casilla(1, 1)
                .casilla(1, 3)
                .casilla(2, 0)
                .casilla(2, 1)
                .casilla(2, 2)
                .casilla(2, 3)
                .casilla(2, 4)
                ;
        return cb.crear();
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago v(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago.FiguraPagoBuilder cb = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name)
                .casilla(0, 0)
                .casilla(0, 4)
                .casilla(1, 1)
                .casilla(1, 3)
                .casilla(2, 2)
                ;
        return cb.crear();
    }
    
    /**
     *
     * @param factorGanancia
     * @return
     */
    public static FiguraPago vInvertida(int factorGanancia){
        String name = new Object(){}.getClass().getEnclosingMethod().getName();
        FiguraPago.FiguraPagoBuilder cb = new FiguraPago.FiguraPagoBuilder()
                .factorDeGanancia(factorGanancia)
                .nombre(name)
                .casilla(0, 2)
                .casilla(1, 1)
                .casilla(1, 3)
                .casilla(2, 0)
                .casilla(2, 4)
                ;
        return cb.crear();
    }
    
    /**
     *
     * @return
     */
    public static List<FiguraPago> cartones(){
        FiguraPagoFactoria.TableroBuilder cartones = new FiguraPagoFactoria.TableroBuilder()
                .agregar(FiguraPagoFactoria.tresLineas(1500))
                .agregar(FiguraPagoFactoria.cuadrado(500))
                .agregar(FiguraPagoFactoria.h(300))
                .agregar(FiguraPagoFactoria.gamma(100))
                .agregar(FiguraPagoFactoria.tt(100))
                .agregar(FiguraPagoFactoria.grilla(40))
                .agregar(FiguraPagoFactoria.y(8))
                .agregar(FiguraPagoFactoria.piramideComun(8))
                .agregar(FiguraPagoFactoria.vInvertida(3))
                .agregar(FiguraPagoFactoria.v(3))
                .agregar(FiguraPagoFactoria.diamante(1))
//                .agregar(FiguraPagoFactoria.consuelo1(1))
//                .agregar(FiguraPagoFactoria.consuelo2(1))
                ;
        
        //Cargar los cartones de 1 linea
        FiguraPago[] unaLinea = FiguraPagoFactoria.linea(2);
        for(FiguraPago carton : unaLinea){
            cartones.agregar(carton);
        }
        
        //Cargar los cartones de 2 linea
        FiguraPago[] dosLineas = FiguraPagoFactoria.dosLineas(50);
        for(FiguraPago carton : dosLineas){
            cartones.agregar(carton);
        }
        
        //Ordeno de manera descendente, esto es obligatorio a la hora de 
        //buscar los premios ya que un premio de menor valor
        //puede estar dentro de uno de mayor valor, solo se debe pagar el mayor
        List<FiguraPago> salida = cartones.listar();
        Collections.sort(salida, (FiguraPago o1, FiguraPago o2) -> o2.getFactorGanancia() - o1.getFactorGanancia());
        
        //Coloco los números a los cartones
        for (int i = 0; i < salida.size(); i++) {
            salida.get(i).setNumero(i + 1);
        }
        //Devuelvo la lista ordenada descendentemente
        return salida;
    }
    
    /**
     *
     */
    public static class TableroBuilder{
        List<FiguraPago> cartones;
        
        /**
         *
         */
        public TableroBuilder(){
            this.cartones = new ArrayList<>();
        }
        
        /**
         *
         * @param carton
         * @return
         */
        public TableroBuilder agregar(FiguraPago carton){
            this.cartones.add(carton);
            return this;
        }
        
        /**
         *
         * @return
         */
        public List<FiguraPago> listar(){
            //Orden descendente, de mayor a menor
            Collections.sort(cartones, (FiguraPago c1, FiguraPago c2) -> c1.getFactorGanancia() - c2.getFactorGanancia());
            return this.cartones;
        }
    }
}
