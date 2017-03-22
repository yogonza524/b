/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.core.bingosimulador;

import com.bingo.entidades.FiguraPago;
import com.bingo.enumeraciones.Denominacion;
import com.bingo.fabricas.FiguraPagoFactoria;
import com.bingo.perfilesJugador.Perfil;
import com.bingo.rng.RNG;
import com.bingo.util.Matematica;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
public class Juego {

    //Configuracion
    private final static int LINEAS = 3;
    private final static int COLUMNAS = 5;
    private final static int CARTONES = 4;
    private final int CANTIDADDEBOLASVISIBLES = 30;
    private final int CANTIDADDEBOLASEXTRA = 10;
    private int cantidadDeFigurasDePago;
    
    private double porcentajeDelPremioMayorPorSalirParaBolaExtra = .1; //10% del mayor
    private int umbralParaLiberarBolasExtra = 10; //Lo define el usuario
    private double tournament;
    
    private int limiteInferiorParaBolaExtraGratis;
    private int limiteSuperiorParaBolaExtraGratis;
    
    //Estado del juego
    private boolean iniciado;
    private boolean modoSimulacion;
    private boolean modoTournament;
    
    //Denominacion
    private Denominacion denominacion;
    
    //Porcentajes
    private double porcentajeDeDescuentoParaTournament;
    
    //Perfiles de jugador
    private Perfil[] perfiles;
    private Perfil perfilActual;
    
    //Bolas extra
    private int[] bolasVisibles;
    private int[] bolasExtra;
    private boolean[] bolasExtraSeleccionadas;
    private int creditosInvertidosEnBolasExtra;
    
    //Creditos
    private int creditos;
    private double dinero;
    
    //Acumulado
    private double acumulado;
    
    //Cartones
    private int[][][] cartones;
    private boolean[] cartonesHabilitados;
    private int[] apostado;
    private int[] ganado;
    
    //Premios obtenidos
    private int[][] premiosPagados;
    private int[][] premiosPorSalir;
    private int[][] premiosPagadosEnCicloDeBolasExtra;
    
    //Premios
    private int[][][] figurasDePago;
    private int[] factoresDePago;
    private String[] nombresDeFiguras;
    private boolean[] figurasConBonus;
    
    //Bonus
    private int[] bonus; //Premios, se calculan en funcion de la apuesta
    private boolean[] premiosDelBonusSeleccionados; //Premios seleccionados
    private int totalGanadoEnBonus;
    private final int cantidadTotalDePremiosEnBonus = 16;
    private int cantidadDeBolasExtraSeleccionadas;

    /**
     * Constructor por defecto
     */
    public Juego() {
        inicializar();
    }
    
    /**
     * Inicializa todos los valores del juego
     */
    private void inicializar(){
        crearEstructuras();
        habilitarPrimerCarton();
        crearCartonesDeJuego();
        colocarApuestaInicial();
        colocarCreditosEnCero();
        crearFigurasDePago();
    }

    private void habilitarPrimerCarton() {
        //Habilita solo el primer carton
        for (int i = 0; i < cartonesHabilitados.length; i++) {
            cartonesHabilitados[i] = false;
        }
        cartonesHabilitados[0] = true;
    }

    private void crearEstructuras() {
        cantidadDeFigurasDePago = FiguraPagoFactoria.cartones().size();
        
        bolasExtraSeleccionadas = new boolean[CANTIDADDEBOLASEXTRA];
        
        perfiles = Perfil.perfiles();
        perfilActual = perfiles[0]; //Tomo el perfil debil
        
        //Porcentajes
        porcentajeDeDescuentoParaTournament = .01;
        
        //Denominacion por defecto
        denominacion = Denominacion.CINCO_CENTAVOS;
        
        cartones = new int[CARTONES][LINEAS][COLUMNAS];
        figurasDePago = new int[cantidadDeFigurasDePago][LINEAS][COLUMNAS];
        factoresDePago = new int[cantidadDeFigurasDePago];
        nombresDeFiguras = new String[cantidadDeFigurasDePago];
        figurasConBonus = new boolean[cantidadDeFigurasDePago];
        cartonesHabilitados = new boolean[CARTONES];
        apostado = new int[CARTONES];
        ganado = new int[CARTONES];
        premiosPagados = new int[CARTONES][cantidadDeFigurasDePago];
        premiosPorSalir = new int[CARTONES][cantidadDeFigurasDePago];
        premiosPagadosEnCicloDeBolasExtra = new int[CARTONES][cantidadDeFigurasDePago];
        creditosInvertidosEnBolasExtra = 0;
        iniciado = false;
        borrarSeleccionDeBolasExtra();
        
    }

    private void colocarApuestaInicial() {
        apostado[0] = 1; //Primer carton siempre inicia con una apuesta de 1 credito
    }

    public static int getLineas() {
        return LINEAS;
    }

    public static int getColumnas() {
        return COLUMNAS;
    }

    private void crearFigurasDePago() {
        List<FiguraPago> figuras = FiguraPagoFactoria.cartones();
        
        int i = 0;
        for(FiguraPago figura : figuras){
            figurasDePago[i] = figura.getCasillas();
            factoresDePago[i] = figura.getFactorGanancia();
            nombresDeFiguras[i] = figura.getNombre();
            figurasConBonus[i] = figura.isEsBonus();
            i++;
        }
    }
    
    public void cargarFigurasDePago(List<FiguraPago> figuras) {
        
        int i = 0;
        for(FiguraPago figura : figuras){
            figurasDePago[i] = figura.getCasillas();
            factoresDePago[i] = figura.getFactorGanancia();
            nombresDeFiguras[i] = figura.getNombre();
            figurasConBonus[i] = figura.isEsBonus();
            i++;
        }
    }
    
    /**
     * Intercambia las casillas de los cartones de juego
     */
    public void cambiarCartones(){
        int[] numeros = RNG.getInstance().generarEnterosUnicos(LINEAS * COLUMNAS * CARTONES);
        
        //Genero los valores de cada carton
        int indice = 0;
        
        for (int i = 0; i < CARTONES; i++) {
            //Generar casillas con valores aleatorios
            int[][] casillas = new int[LINEAS][COLUMNAS];
            //Cargo las casillas
            for (int j = 0; j < LINEAS; j++) {
                for (int k = 0; k < COLUMNAS; k++) {
                    casillas[j][k] = numeros[indice];
                    indice++;
                }
            }
            
            cartones[i] = casillas;
            
            //Ordeno ascendentemente
            cartones[i] = ordenarAsendentemente(cartones[i]);
        }
        
        
    }
    
    /**
     * Obtiene el total de apuestas
     * @return 
     */
    public int apuestaTotal(){
        int result = 0;
        for (int i = 0; i < apostado.length; i++) {
            if (cartonesHabilitados[i]) {
                //El carton i esta habilitado, computar apuesta
                result += apostado[i];
            }
        }
        return result;
    }
    
    /**
     * Obtiene el total de creditos actuales
     * @return numero entero mayor o igual a cero, representa
     * la cantidad total de creditos que introdujo el jugador
     */
    public int creditos(){
        return this.creditos;
    }
    
    public void aumentarApuestas(){
        if (apuestaTotal() + habilitados() <= creditos) {
            for (int i = 0; i < apostado.length; i++) {
                if (cartonesHabilitados[i]) {
                    apostado[i] += 1;
                }
            }
        }
    }
    
    public void disminuirApuestas(){
        if (apuestaTotal() - habilitados() > 0) {
            for (int i = 0; i < apostado.length; i++) {
                if (cartonesHabilitados[i]) {
                    apostado[i] -= 1;
                }
            }
        }
    }
    
    public int habilitados(){
        int result = 0;
        for (int i = 0; i < cartonesHabilitados.length; i++) {
            result += cartonesHabilitados[i] ? 1 : 0;
        }
        return result;
    }
    
    public double acumulado(){
        return this.acumulado;
    }
    
    public int[] apuestas(){
        return this.apostado;
    }

    public Denominacion getDenominacion() {
        return denominacion;
    }

    public void setDenominacion(Denominacion denominacion) {
        this.denominacion = denominacion;
    }

    public double getPorcentajeDeDescuentoParaTournament() {
        return porcentajeDeDescuentoParaTournament;
    }

    public void setPorcentajeDeDescuentoParaTournament(double porcentajeDeDescuentoParaTournament) {
        this.porcentajeDeDescuentoParaTournament = porcentajeDeDescuentoParaTournament;
    }
    
    public int[] factoresDePago(){
        return this.factoresDePago;
    }
    
    public int[] ganado(){
        return this.ganado;
    }

    public int getCantidadDeBolasExtraSeleccionadas() {
        return cantidadDeBolasExtraSeleccionadas;
    }

    public double getDinero() {
        return dinero;
    }

    public void setDinero(double dinero) {
        this.dinero = dinero;
    }

    public void setCantidadDeBolasExtraSeleccionadas(int cantidadDeBolasExtraSeleccionadas) {
        this.cantidadDeBolasExtraSeleccionadas = cantidadDeBolasExtraSeleccionadas;
    }
    
    public int[][][] cartones(){
        return this.cartones;
    }
    
    public int[][][] figurasDePago(){
        return this.figurasDePago;
    }
    
    public void agregarCreditos(int creditos){
        if (creditos > 0) {
            this.creditos += creditos;
        }
    }

    public void mostrarConfiguracion() {
        System.out.println("Acumulado: " + this.acumulado());
        System.out.println("Ganado(por carton): " + ArrayUtils.toString(this.ganado()));
        System.out.println("Ganado(total): " + ganancias());
        System.out.println("Apuesta total: " + this.apuestaTotal());
        System.out.println("Apuesta individual: " + ArrayUtils.toString(apostado));
        System.out.println("Creditos: " + this.creditos());
        System.out.println("Cartones Habilitados: " + this.habilitados());
        System.out.println("Cartones Habilitados(individual): " + ArrayUtils.toString(this.cartonesHabilitados));
    }

    public int ganancias() {
        int result = 0;
        for (int i = 0; i < ganado.length; i++) {
            result += ganado[i];
        }
        return result;
    }
    
    public int[][] premiosPagados(){
        return this.premiosPagados;
    }
    
    public int[][] premiosPorSalir(){
        return this.premiosPorSalir;
    }
    
    public int[][] carton(int i){
        int[][] result = null;
        if (i >= 0 && i < CARTONES) {
            return cartones[i];
        }
        return result;
    }
    
    public boolean habilitar(int i){
        boolean result = false;
        if (habilitados() == 0) {
            cartonesHabilitados[0] = true;
        }
        if (i >= 0 && i < CARTONES && !cartonesHabilitados[i] && 
                apuestaTotal() + (apuestaTotal() / habilitados()) <= creditos) {
            apostado[i] = apuestaTotal() / habilitados();
            cartonesHabilitados[i] = true;
        }
        return result;
    }
    
    public boolean deshabilitar(int i){
        boolean result = false;
        if (i >= 0 && i < CARTONES && cartonesHabilitados[i] &&
                apuestaTotal() - (apuestaTotal() / habilitados()) > 0) {
            apostado[i] = 0;
            cartonesHabilitados[i] = false;
        }
        return result;
    }
    
    public void setCreditos(int creditos){
        if (creditos >= 0) {
            this.creditos = creditos;
        }
    }

    public int getCreditos() {
        return creditos;
    }

    private void colocarCreditosEnCero() {
        this.creditos = 0;
    }

    private void crearCartonesDeJuego() {
        this.cambiarCartones();
    }
    
    private void borrarPremiosPorSalir(){
        for (int i = 0; i < premiosPorSalir.length; i++) {
            premiosPorSalir[i] = new int[cantidadDeFigurasDePago];
        }
    }
    
    private void borrarGanancias(){
        for (int i = 0; i < ganado.length; i++) {
            ganado[i] = 0;
        }
    }
    
    private void borrarBolasExtraSeleccionadas(){
        bolasExtraSeleccionadas = new boolean[CANTIDADDEBOLASEXTRA];
    }
    
    public void generarBolillero() {
        
        int[] bolas = getBolilleroAleatorio();
        this.bolasVisibles = new int[CANTIDADDEBOLASVISIBLES];
        this.bolasExtra = new int[CANTIDADDEBOLASEXTRA];
        
        System.arraycopy(bolas, 0, this.bolasVisibles, 0, CANTIDADDEBOLASVISIBLES);
        
        int indice = 0;
        for (int i = CANTIDADDEBOLASVISIBLES; i < CANTIDADDEBOLASEXTRA + CANTIDADDEBOLASVISIBLES; i++) {
            this.bolasExtra[indice] = bolas[i];
            indice++;
        }
        
    }
    
    /**
     * Genera un conjunto de bolas aleatoriamente
     * @return 
     */
    private int[] getBolilleroAleatorio(){
        int totalBolillas = CANTIDADDEBOLASVISIBLES + CANTIDADDEBOLASEXTRA;
        
        int[] bolas = new int[totalBolillas];

        int bolilla;
        
        for(int i=0; i< totalBolillas; i++) {
          bolilla = (int)(RNG.getInstance().pick() * LINEAS * COLUMNAS * CARTONES) + 1; 

          if (!Matematica.contiene(bolas, bolilla)){
              // Si el número aun no había salido, lo agrego y sigo
              bolas[i] = bolilla;
          } else {
              // Si ya había salido el número, no avanzamos a la próxima bolilla
              i = i - 1;
          }
        } 
        return bolas;
     }
    
    public void mostrarBolillero(){
        System.out.println("Bolas visibles: " + ArrayUtils.toString(this.bolasVisibles) + "(" + this.bolasVisibles.length + ")");
        System.out.println("Bolas extra: " + ArrayUtils.toString(this.bolasExtra) + "(" + this.bolasExtra.length + ")");
    }
    
    public void mostrarBolasExtraSeleccionadas(){
        System.out.println("Bolas extra seleccionadas: " + ArrayUtils.toString(bolasExtraSeleccionadas));
    }
    
    public int[] premiosSegunApostado(){
        int[] result = new int[cantidadDeFigurasDePago];
        for (int i = 0; i < cantidadDeFigurasDePago; i++) {
            result[i] = this.factoresDePago[i] * this.apuestaIndividual();
        }
        return result;
    }

    private int apuestaIndividual() {
        if (habilitados() > 0) {
            return this.apuestaTotal() / habilitados();
        }
        System.out.println("No hay cartones habilitados, no se puede computar la apuesta individual");
        return -1;
    }
    
    public void mostrarPremiosSegunApostado(){
        System.out.println("Premios segun apuestas: " + ArrayUtils.toString(this.premiosSegunApostado()));
        System.out.println("Nombres: " + ArrayUtils.toString(nombresDeFiguras));
    }
    
    public boolean liberarBolasExtra(int umbral){
        boolean result = false;
        for (int i = 0; i < premiosPorSalir.length; i++) {
            for (int j = 0; j < premiosPorSalir[0].length; j++) {
                if (premiosPorSalir[i][j] > 0 && factoresDePago[j] >= umbral) {
                    result = true;
                    System.out.println("Se deben liberar las bolas extra!");
                    break;
                }
            }
        }
        return result;
    }
    
    public int costoBolaExtra(){
        int factorDeGananciaDelPremioMayor = premioMayorPorSalir();
        
        if (factorDeGananciaDelPremioMayor >= limiteInferiorParaBolaExtraGratis 
                && factorDeGananciaDelPremioMayor <= limiteSuperiorParaBolaExtraGratis) {
            return 0;
        }
        return (int)(factorDeGananciaDelPremioMayor * porcentajeDelPremioMayorPorSalirParaBolaExtra);
    }
    
    //Falta terminar
    public void buscarPremiosPorSalir(){
        //Inicializo los premios por salir
        this.premiosPorSalir = new int[CARTONES][cantidadDeFigurasDePago];
        //Recorro los cartones
        for (int i = 0; i < CARTONES; i++) {
            //Recorro las figuras
            for (int j = 0; j < figurasDePago.length; j++) {
                int[][] casillasBinarias = Matematica.figuraBinaria(bolasVisibles, cartones[i]);
                
                //Verifico que le falte una casilla y ademas sea mayor al umbral
                if (cartonesHabilitados[i] && Matematica.leFaltaUno(casillasBinarias, figurasDePago[j]) 
                        && factoresDePago[j] >= umbralParaLiberarBolasExtra) {
                    premiosPorSalir[i][j] = premiosSegunApostado()[j];
                    
                    System.out.println("Premio por salir: " + nombresDeFiguras[j] + "(" + factoresDePago[j] + "). Umbral: " + umbralParaLiberarBolasExtra);
                }
            }
        }
    }
    
    private void buscarPremios(int[] bolilleroVisible, int[][][] cartones){
        for (int i = 0; i < cartones.length; i++) {
            int[][] casillasConPremios = casillasGanadoras(cartones[i], bolilleroVisible);
            for (int j = 0; j < figurasDePago.length; j++) {
                if (cartonesHabilitados[i] && Matematica.estaContenido(figurasDePago[j],casillasConPremios)) {
                    //Premio encontrado
                    this.premiosPagados[i][j] = factoresDePago[j] * apuestaIndividual();
                }
            }
        }
        //Borrar los premios contenidos en otros de menor jerarquia segun los factores de pago
        //La norma es: dar solo el premio mayor
        int indiceDelMayor;
        for (int i = 0; i < premiosPagados.length; i++) {
            for (int j = 0; j < premiosPagados[0].length; j++) {
                indiceDelMayor = premiosPagados[i][j] > 0 ? j : -1;
                if (indiceDelMayor > -1 && indiceDelMayor < premiosPagados[0].length - 1) {
                    //Busco los premios contenidos y los borro
                    for (int k = indiceDelMayor; k < premiosPagados[0].length; k++) {
                        if (k != indiceDelMayor && premiosPagados[i][k] > 0 
                                && Matematica.estaContenido(figurasDePago[k], figurasDePago[indiceDelMayor])) {
                            premiosPagados[i][k] = 0;
//                            System.out.println("Se borra el premio " + nombresDeFiguras[k]);
//                            System.out.println("Esta contenido en " + nombresDeFiguras[indiceDelMayor]);
                        }
                    }
                }
            }
        }
    }
    
    public void computarGanancias(){
        this.ganado = new int[CARTONES];
        for (int i = 0; i < premiosPagados.length; i++) {
            for (int j = 0; j < premiosPagados[0].length; j++) {
                ganado[i] += premiosPagados[i][j];
            }
            creditos += ganado[i];
        }
    }
    
//    public boolean figuraPremiadaAnteriormenteContenida(int indiceCarton, int[][] contenido){
//        boolean result = false;
//        for (int i = 0; i < this.premiosPagados[indiceCarton].length; i++) {
//            if (premiosPagados[indiceCarton][i] > 0 && Matematica.estaContenido(figurasDePago[i], contenido)) {
//                result = true;
//                System.out.println("Figura premiada anteriormente: " + this.nombresDeFiguras[i]);
//                System.out.println("No se debe pagar, casillas comparadas");
//                for (int j = 0; j < contenido.length; j++) {
//                    System.out.println(ArrayUtils.toString(contenido[j]));
//                }
//                break;
//            }
//        }
//        return result;
//    }
    
    public void buscarPremios(){
        this.buscarPremios(bolasVisibles, cartones);
    }
    
    private int[][] casillasGanadoras(int[][] casillasDelCarton, int[] bolasVisibles){
        int[][] result = new int[LINEAS][COLUMNAS];
        for (int i = 0; i < bolasVisibles.length; i++) {
            for (int j = 0; j < casillasDelCarton.length; j++) {
                for (int k = 0; k < casillasDelCarton[0].length; k++) {
                    if (casillasDelCarton[j][k] == bolasVisibles[i]) {
                        result[j][k] = 1;
                    }
                }
            }
        }
        return result;
    }

    public void mostrarPremiosObtenidos() {
        int[][] premios = this.premiosPagados();
        System.out.println("Premios obtenidos: ");
        for (int i = 0; i < premios.length; i++) {
            System.out.println("Carton " + (i + 1) + ": " + ArrayUtils.toString(premios[i]));
        }
    }
    
    public void mostrarCartones(){
        System.out.println("Cartones actuales");
        for (int i = 0; i < cartones.length; i++) {
            for (int j = 0; j < cartones[0].length; j++) {
                System.out.println(ArrayUtils.toString(cartones[i][j]));
            }
            System.out.println("-----------------");
        }
    }

    public void mostrarGanado() {
        System.out.println("Ganancias totales: " + ganancias());
        System.out.println("Ganancias parciales: " + ArrayUtils.toString(this.ganado));
    }

    public void mostrarCreditos() {
        System.out.println("Creditos: " + this.creditos);
    }

    public void mostrarApuestas() {
        System.out.println("Apuesta total: " + this.apuestaTotal());
        System.out.println("Apuestas individuales: " + ArrayUtils.toString(this.apostado));
    }

    private int premioMayorPorSalir() {
        int premio = -1;
        for (int i = 0; i < CARTONES; i++) {
            for (int j = 0; j < premiosPorSalir[i].length; j++) {
                if (premiosPorSalir[i][j] > premio) {
                    premio = premiosPorSalir[i][j]; 
                }
            }
        }
        return premio;
    }

    public double getPorcentajeDelPremioMayorPorSalirParaBolaExtra() {
        return porcentajeDelPremioMayorPorSalirParaBolaExtra;
    }

    public void setPorcentajeDelPremioMayorPorSalirParaBolaExtra(double porcentajeDelPremioMayorPorSalirParaBolaExtra) {
        this.porcentajeDelPremioMayorPorSalirParaBolaExtra = porcentajeDelPremioMayorPorSalirParaBolaExtra;
    }

    public int getCantidadDeFigurasDePago() {
        return cantidadDeFigurasDePago;
    }

    public void setCantidadDeFigurasDePago(int cantidadDeFigurasDePago) {
        this.cantidadDeFigurasDePago = cantidadDeFigurasDePago;
    }

    public int getUmbralParaLiberarBolasExtra() {
        return umbralParaLiberarBolasExtra;
    }

    public void setUmbralParaLiberarBolasExtra(int umbralParaLiberarBolasExtra) {
        this.umbralParaLiberarBolasExtra = umbralParaLiberarBolasExtra;
    }

    public int[] getBolasVisibles() {
        return bolasVisibles;
    }

    public void setBolasVisibles(int[] bolasVisibles) {
        this.bolasVisibles = bolasVisibles;
    }

    public int[] getBolasExtra() {
        return bolasExtra;
    }

    public void setBolasExtra(int[] bolasExtra) {
        this.bolasExtra = bolasExtra;
    }

    public boolean[] getBolasExtraSeleccionadas() {
        return bolasExtraSeleccionadas;
    }

    public int getLimiteInferiorParaBolaExtraGratis() {
        return limiteInferiorParaBolaExtraGratis;
    }

    public void setLimiteInferiorParaBolaExtraGratis(int limiteInferiorParaBolaExtraGratis) {
        this.limiteInferiorParaBolaExtraGratis = limiteInferiorParaBolaExtraGratis;
    }

    public int getLimiteSuperiorParaBolaExtraGratis() {
        return limiteSuperiorParaBolaExtraGratis;
    }

    public void setLimiteSuperiorParaBolaExtraGratis(int limiteSuperiorParaBolaExtraGratis) {
        this.limiteSuperiorParaBolaExtraGratis = limiteSuperiorParaBolaExtraGratis;
    }

    public void setBolasExtraSeleccionadas(boolean[] bolasExtraSeleccionadas) {
        this.bolasExtraSeleccionadas = bolasExtraSeleccionadas;
    }

    public double getAcumulado() {
        return acumulado;
    }

    public void setAcumulado(double acumulado) {
        this.acumulado = acumulado;
    }

    public int[][][] getCartones() {
        return cartones;
    }

    public void setCartones(int[][][] cartones) {
        this.cartones = cartones;
    }

    public boolean[] getCartonesHabilitados() {
        return cartonesHabilitados;
    }

    public void setCartonesHabilitados(boolean[] cartonesHabilitados) {
        this.cartonesHabilitados = cartonesHabilitados;
    }

    public int[] getApostado() {
        return apostado;
    }

    public void setApostado(int[] apostado) {
        this.apostado = apostado;
    }

    public boolean isModoSimulacion() {
        return modoSimulacion;
    }

    public void setModoSimulacion(boolean modoSimulacion) {
        this.modoSimulacion = modoSimulacion;
    }

    public int[] getGanado() {
        return ganado;
    }

    public void setGanado(int[] ganado) {
        this.ganado = ganado;
    }

    public int[][] getPremiosPagados() {
        return premiosPagados;
    }

    public void setPremiosPagados(int[][] premiosPagados) {
        this.premiosPagados = premiosPagados;
    }

    public int[][] getPremiosPorSalir() {
        return premiosPorSalir;
    }

    public boolean isModoTournament() {
        return modoTournament;
    }

    public void setModoTournament(boolean modoTournament) {
        this.modoTournament = modoTournament;
    }

    public Perfil getPerfilActual() {
        return perfilActual;
    }

    public void setPerfilActual(Perfil perfilActual) {
        this.perfilActual = perfilActual;
    }

    public void setPremiosPorSalir(int[][] premiosPorSalir) {
        this.premiosPorSalir = premiosPorSalir;
    }

    public int[][][] getFigurasDePago() {
        return figurasDePago;
    }

    public void setFigurasDePago(int[][][] figurasDePago) {
        this.figurasDePago = figurasDePago;
    }

    public int[] getFactoresDePago() {
        return factoresDePago;
    }

    public void setFactoresDePago(int[] factoresDePago) {
        this.factoresDePago = factoresDePago;
    }

    public String[] getNombresDeFiguras() {
        return nombresDeFiguras;
    }

    public void setNombresDeFiguras(String[] nombresDeFiguras) {
        this.nombresDeFiguras = nombresDeFiguras;
    }

    public int[] getBonus() {
        return bonus;
    }

    public void setBonus(int[] bonus) {
        this.bonus = bonus;
    }

    public boolean[] getPremiosDelBonusSeleccionados() {
        return premiosDelBonusSeleccionados;
    }

    public int getCreditosInvertidosEnBolasExtra() {
        return creditosInvertidosEnBolasExtra;
    }

    public void setCreditosInvertidosEnBolasExtra(int creditosInvertidosEnBolasExtra) {
        this.creditosInvertidosEnBolasExtra = creditosInvertidosEnBolasExtra;
    }

    public int[][] getPremiosPagadosEnCicloDeBolasExtra() {
        return premiosPagadosEnCicloDeBolasExtra;
    }

    public void setPremiosPagadosEnCicloDeBolasExtra(int[][] premiosPagadosEnCicloDeBolasExtra) {
        this.premiosPagadosEnCicloDeBolasExtra = premiosPagadosEnCicloDeBolasExtra;
    }

    public boolean[] getFigurasConBonus() {
        return figurasConBonus;
    }

    public void setFigurasConBonus(boolean[] figurasConBonus) {
        this.figurasConBonus = figurasConBonus;
    }

    public void setPremiosDelBonusSeleccionados(boolean[] premiosDelBonusSeleccionados) {
        this.premiosDelBonusSeleccionados = premiosDelBonusSeleccionados;
    }

    public int getTotalGanadoEnBonus() {
        return totalGanadoEnBonus;
    }

    public void setTotalGanadoEnBonus(int totalGanadoEnBonus) {
        this.totalGanadoEnBonus = totalGanadoEnBonus;
    }

    public double getTournament() {
        return tournament;
    }

    public void setTournament(double tournament) {
        this.tournament = tournament;
    }
    
    public void jugar(boolean generarNuevoBolillero){
        if (generarNuevoBolillero) {
            this.generarBolillero();
        }
        
        if (this.apuestaTotal() > this.creditos) {
            System.out.println("La apuesta total de " + this.apuestaTotal() + " supera los "
                    + "creditos disponibles, " + creditos);
            return;
        }
        
        borrarJuegoAnterior();
        
        if (cartonesDeJuegoVacios()) {
            System.out.println("Los cartones de juego no fueron colocados, juego abortado...");
            return;
        }
        
        iniciado = true; //Comenzó el juego
        
        this.descontarApuestas();
        
        this.buscarPremios(); //Busco premios comunmente
        this.buscarPremiosPorSalir(); //Busco premios por salir
        
        if (modoSimulacion) {
            if (huboBonus()) {
                //Iniciar ciclo de bonus
                cicloBonus();

                //Computar la ganancia del bonus
                creditos += totalGanadoEnBonus;
            }

            if (liberarBolasExtra(umbralParaLiberarBolasExtra)) {
                //Iniciar ciclo de bolas extra
                cicloDeBolasExtra();
            }
        }
        
        computarGanancias();
        
        //Si ya no tengo credito disponible, partir y deshabilitar los cartones
        deshabilitarCartonesSiNoHayCreditoSuficiente();
        
        //Verificar si se debe pagar el tournament
        if (pagarTournament()) {
            
        }
    }

    private boolean cartonesDeJuegoVacios() {
        return this.cartones == null;
    }

    private boolean huboBonus() {
        for (int i = 0; i < premiosPagados.length; i++) {
            for (int j = 0; j < premiosPagados[i].length; j++) {
                if (premiosPagados[i][j] > 0 && figurasConBonus[j]) {
                    return true;
                }
            }
        }
        return false;
    }

    private void borrarPremiosObtenidos() {
        this.premiosPagados = new int[CARTONES][this.cantidadDeFigurasDePago];
    }

    private void borrarPremiosObtenidosEnCicloDeBolasExtra() {
        this.premiosPagadosEnCicloDeBolasExtra = new int[CARTONES][this.cantidadDeFigurasDePago];
    }

    private void borrarJuegoAnterior() {
        borrarBolasExtraSeleccionadas();
        borrarGanancias();
        borrarPremiosPorSalir();
        borrarPremiosObtenidos();
        borrarPremiosObtenidosEnCicloDeBolasExtra();
        
        //Borro el totalizador de bolas extra
        borrarSeleccionDeBolasExtra();
        
        creditosInvertidosEnBolasExtra = 0;
        totalGanadoEnBonus = 0;
    }

    private void borrarSeleccionDeBolasExtra() {
        this.bolasExtraSeleccionadas = new boolean[CANTIDADDEBOLASEXTRA];
    }

    public void cicloDeBolasExtra() {
        if (perfilActual == null) {
            System.out.println("No se ha cargado un perfil, saliendo del ciclo de bolas extra....");
            return;
        }
        if (cantidadDeBolasExtraPorComprar() == 0) {
            System.out.println("No se pueden comprar bolas extra, ya no hay ninguna, saliendo del ciclo de bolas extra...");
            return;
        }
        while (cantidadDeBolasExtraPorComprar() > 0 && 
                RNG.getInstance().pick() >= perfilActual.getProbabilidadDeComprarBolasExtra()) {
            
            int indiceDeBolaExtraAComprar = seleccionarAleatoriamenteBolaExtraDisponible();
            int costoBolaSeleccionada = costoBolaExtra();
            
            System.out.println("Indice de bola extra elegida: " + indiceDeBolaExtraAComprar);
            System.out.println("Costo de la bola elegida: " + costoBolaSeleccionada);
            System.out.println("Numero de bola elegida: " + bolasExtra[indiceDeBolaExtraAComprar]);
            
            //Verifico que tenga creditos disponibles para comprar
            if (creditos - costoBolaSeleccionada >= 0) {
                //Tiene creditos, descontar 
                System.out.println("Puede comprar la bola extra! Descontando el costo anterior del credito");
                bolasExtraSeleccionadas[indiceDeBolaExtraAComprar] = true; //Marco como seleccionada
                creditos -= costoBolaSeleccionada; //Descuento
                
                System.out.println("Se desconó creditos por bolas extra, credito actual: " + creditos);
                
                //Aumento el contador de credito gastado en bolas extra
                creditosInvertidosEnBolasExtra += costoBolaSeleccionada;
                
                //Aumento el contador de veces que se compraron bolas extra
                cantidadDeBolasExtraSeleccionadas++;
                
                bolasVisibles  = Arrays.copyOf(bolasVisibles, bolasVisibles.length + 1); //Genero una nueva ranura para la bola extra en las visibles
                bolasVisibles[bolasVisibles.length - 1] = bolasExtra[indiceDeBolaExtraAComprar]; //agrego el numero de la bola extra nueva
                buscarPremios();
                
                System.out.println("Compró la bola: " + indiceDeBolaExtraAComprar);
                System.out.println("Costo: " + costoBolaSeleccionada);
            }
            else{
                //No tiene creditos disponibles, salir del ciclo
                break;
            }
        }
    }
    
    public void buscarPremiosConBolasExtra(){
        
    }
    
    private int cantidadDeBolasExtraPorComprar(){
        int result = 0;
        for (int i = 0; i < bolasExtraSeleccionadas.length; i++) {
            result += !bolasExtraSeleccionadas[i] ? 1 : 0;
        }
        return result;
    }

    private int seleccionarAleatoriamenteBolaExtraDisponible() {
        int indice = RNG.getInstance().pickInt(CANTIDADDEBOLASEXTRA);
        int cantidadDeBolasDisponibles = cantidadDeBolasExtraPorComprar();
        while(cantidadDeBolasDisponibles > 0 && bolasExtraSeleccionadas[indice]){
            indice = RNG.getInstance().pickInt(CANTIDADDEBOLASEXTRA);
        }
        return indice;
    }

    private void descontarApuestas() {
        this.creditos -= apuestaTotal();
        
        System.out.println("Creditos luego de descontar: " + this.creditos);
        
        //Si el juego es comunitario sumar al pozo
        if (modoTournament) {
            acumulado += apuestaTotal() * porcentajeDeDescuentoParaTournament;
//            dinero -= apuestaTotal() * porcentajeDeDescuentoParaTournament;
            this.creditos -= apuestaTotal() * porcentajeDeDescuentoParaTournament;
            
            System.out.println("Creditos luego de descontar el tournament: " + this.creditos);
        }
    }
    
    /**
     * El jugador decide cobrar su dinero ganado
     * @return TRUE si se cobró correctamente
     */
    public double cobrar(){
        
        System.out.println("Creditos a cobrar: " + creditos);
        System.out.println("Dinero antes del calculo: " + dinero);
        
        //Sumo el dinero (si esta en modo Tournament el dinero tiene un valor
        //Negativo, debido al descuento de la apuesta del jugador)
        //Con los creditos en la denominacion elegida
        double dineroCobrado = dinero + creditos * denominacion.getValue();
        
        System.out.println("Dinero a cobrar: " + dineroCobrado);
        
        dineroCobrado = Matematica.redondear(dineroCobrado, 2);
        dinero = 0.0;
        
        System.out.println("Dinero a cobrar(redondeado): " + dineroCobrado);
        
        return dineroCobrado;
    }
    
    /**
     * Coloca los limites de factores de ganancia de los cartones cuyas bolas extra seran gratis
     * @param inferior
     * @param superior 
     */
    public void setLimitesBolaExtraGratis(int inferior, int superior){
        this.limiteInferiorParaBolaExtraGratis = inferior;
        this.limiteSuperiorParaBolaExtraGratis = superior;
    }

    private void cicloBonus() {
        //Inicializo los valores
        totalGanadoEnBonus = 0;
        bonus = new int[cantidadTotalDePremiosEnBonus];
        premiosDelBonusSeleccionados = new boolean[cantidadTotalDePremiosEnBonus];
        
        System.out.println("Comienzo del ciclo del bonus!");
        
        //Genero los premios en funcion de lo apostado
        int apuestaIndividual = this.apuestaIndividual();
        
        //Genero factores de pago, al seleccionar el 0 se termina el ciclo
        int[] factoresDePremioBonus = new int[]{
            10,20,0,0
        };
        
        //Cargo los valores aleatorios
        for (int i = 0; i < cantidadTotalDePremiosEnBonus; i++) {
            bonus[i] = apuestaIndividual * factoresDePremioBonus[RNG.getInstance().pickInt(factoresDePremioBonus.length)];
        }
        
        System.out.println("Premios del bonus: " + ArrayUtils.toString(bonus));
        
        //Comienza la seleccion aleatoria
        int indice = RNG.getInstance().pickInt(bonus.length);
        while(quedanPremiosDelBonusDisponibles() && bonus[indice] != 0 && !premiosDelBonusSeleccionados[indice]){
            totalGanadoEnBonus += bonus[indice];
            premiosDelBonusSeleccionados[indice] = true;
            
            System.out.println("Seleccionado: " + indice);
            System.out.println("Premio bonus: " + bonus[indice]);
            
            indice = RNG.getInstance().pickInt(bonus.length);
        }
        
        System.out.println("Finalizó el bonus, indice = " + indice + ". Ganancia total: " + totalGanadoEnBonus);
    }
    
    public boolean quedanPremiosDelBonusDisponibles(){
        for (int i = 0; i < premiosDelBonusSeleccionados.length; i++) {
            if (!premiosDelBonusSeleccionados[i]) {
                return true;
            }
        }
        return false;
    }
    
    public boolean huboGanancias(){
        return this.ganancias() > 0;
    }
    
    public boolean huboBolasExtraElegidas(){
        return this.bolasVisibles.length > CANTIDADDEBOLASVISIBLES;
    }
    
    public boolean huboBolasExtraLiberadas(){
        return iniciado && this.liberarBolasExtra(umbralParaLiberarBolasExtra);
    }
    
    public boolean huboPremiosPorSalir(){
        return premioMayorPorSalir() > 0;
    }

    private void deshabilitarCartonesSiNoHayCreditoSuficiente() {
        int apuestasNuevas = apuestaIndividual();
        int apuestaTotal = apuestaTotal();
        if (apuestaTotal > creditos) {
            
            System.out.println("No hay creditos suficientes, se deshabilitaran los cartonas superiores");
            
            apuestaTotal = apuestasNuevas;
            
            for (int i = 0; i < CARTONES; i++) {
                if (apuestasNuevas <= creditos && apuestaTotal <= creditos) {
                    cartonesHabilitados[i] = true;
                    apuestaTotal += apuestasNuevas;
                }
                else{
                    for (int j = i; j < CARTONES; j++) {
                        cartonesHabilitados[j] = false;
                        apostado[j] = 0;
                    }
                    break;
                }
            }

            habilitar(0); //Primer carton siempre habilitado
            
            if (apuestaTotal() == 0) {
                apostado[0] = 1;
            }
            
            System.out.println("Cartones luego de deshabilitar: " + ArrayUtils.toString(cartonesHabilitados));
            System.out.println("Apuestas luego de deshabilitar: ");
            mostrarApuestas();
        }
    }

    private int[][] ordenarAsendentemente(int[][] casillas) {
        Integer[][] lista = new Integer[casillas.length][casillas[0].length];
        
        //Instancio las casillas nuevas
        int[][] resultado = new int[casillas.length][casillas[0].length];
        
        //Cargo las lista de objetos
        for (int i = 0; i < casillas.length; i++) {
            lista[i] = ArrayUtils.toObject(casillas[i]);
            
            //Ordeno ascendentemente
            Arrays.sort(lista[i], new Comparator<Integer>()
            {
                @Override
                public int compare(Integer x, Integer y)
                {
                    return x - y;
                }
            });
            
            resultado[i] = ArrayUtils.toPrimitive(lista[i]);
        }
        
        return resultado;
    }

    //Condicion de verificacion del pago del tournament
    private boolean pagarTournament() {
        boolean result = false;
        
        return result;
    }
}
