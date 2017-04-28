/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.core.bingosimulador;

import com.bingo.entidades.FiguraPago;
import com.bingo.enumeraciones.Denominacion;
import com.bingo.enumeraciones.FaseDeBusqueda;
import com.bingo.fabricas.FiguraPagoFactoria;
import com.bingo.perfilesJugador.Perfil;
import com.bingo.rng.RNG;
import com.bingo.util.Matematica;
import com.persistencia.ConfiguracionPersistencia;
import com.persistencia.PersistenciaJson;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
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

    public static int getCantidadDeCartones() {
        return CARTONES;
    }
    private final int CANTIDADDEBOLASVISIBLES = 30;
    private final int CANTIDADDEBOLASEXTRA = 10;
    private int cantidadDeFigurasDePago;
    
    //Registro de resultados
    private String resultados;
    
    private double porcentajeDelPremioMayorPorSalirParaBolaExtra = .1; //10% del mayor
    private int umbralParaLiberarBolasExtra = 10; //Lo define el usuario
    private double tournament;
    
    private int limiteInferiorParaBolaExtraGratis;
    private int limiteSuperiorParaBolaExtraGratis;
    
    //Estado del juego
    private boolean iniciado;
    private boolean modoSimulacion;
    private boolean modoTournament;
    private boolean modoBonus;
    private boolean utilizarUmbralParaLiberarBolasExtra;
    private boolean modoDeshabilitarPorFaltaDeCredito;
    private boolean crearFigurasDePago;
    
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
    private boolean utilizarBolasExtraGratis;
    private int creditosInvertidosEnBolasExtra;
    private int ganadoEnBolasExtra;
    private int apuestaCompleta;
    
    //Variable auxiliar para elegir el indice de la bola extra
    private int indiceBolaExtra;
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
    private int[] frecuenciaFiguras; 
    private int[] frecuenciaFigurasPrimeraFase;
    private int[] frecuenciaFigurasSegundaFase;
    
    //Premios
    private int[][][] figurasDePago;
    private int[] factoresDePago;
    private String[] nombresDeFiguras;
    private boolean[] figurasConBonus;
    
    //Bonus
    private Integer[] bonus; //Premios, se calculan en funcion de la apuesta
    private boolean[] premiosDelBonusSeleccionados; //Premios seleccionados
    private int totalGanadoEnBonus;
    private int cantidadTotalDePremiosEnBonus = 16;
    private int cantidadDeBolasExtraSeleccionadas;
    private boolean seLiberaronBolasExtra;
    private boolean modoDebug;
    
    private Map<Integer,Integer> premiosFijosBonus;
    private Map<Integer,Integer> premiosVariablesBonus;
    private boolean utilizarPremiosFijosBonus;
    private boolean utilizarPremiosVariablesBonus;
    private int cantidadDePremiosBonusFijo;
    private int cantidadDePremiosBonusVariable;
    private boolean salioElBonusAlInicioDelJuego;
    private boolean inicioDelCicloDeJuego;
    private int cantidadDeVecesQueSeObtuvoElBonus;

    /**
     * Constructor por defecto
     */
    public Juego() {
        try {
            inicializar(null);
        } catch (IOException ex) {
            Logger.getLogger(Juego.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Inicializa todos los valores del juego
     * @param figuras
     */
    public void inicializar(List<FiguraPago> figuras) throws IOException{
        crearEstructuras();
        habilitarPrimerCarton();
        crearCartonesDeJuego();
        colocarApuestaInicial();
        colocarCreditosEnCero();
        crearFigurasDePago(figuras);
    }

    private void habilitarPrimerCarton() {
        //Habilita solo el primer carton
        for (int i = 0; i < cartonesHabilitados.length; i++) {
            cartonesHabilitados[i] = false;
        }
        cartonesHabilitados[0] = true;
    }

    private void crearEstructuras() throws IOException {
        ConfiguracionPersistencia config = PersistenciaJson.getInstancia().getConfiguracion();
        
        cantidadDeFigurasDePago = this.figurasDePago != null? this.figurasDePago.length : FiguraPagoFactoria.cartones().size() ;
        
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
        frecuenciaFiguras = new int[cantidadDeFigurasDePago];
        frecuenciaFigurasPrimeraFase = new int[cantidadDeFigurasDePago];
        frecuenciaFigurasSegundaFase = new int[cantidadDeFigurasDePago];
        
        cartonesHabilitados = new boolean[CARTONES];
        apostado = new int[CARTONES];
        ganado = new int[CARTONES];
        premiosPagados = new int[CARTONES][cantidadDeFigurasDePago];
        premiosPorSalir = new int[CARTONES][cantidadDeFigurasDePago];
        premiosPagadosEnCicloDeBolasExtra = new int[CARTONES][cantidadDeFigurasDePago];
        creditosInvertidosEnBolasExtra = 0;
        iniciado = false;
        utilizarBolasExtraGratis = false;
        ganadoEnBolasExtra = 0;
        borrarSeleccionDeBolasExtra();
        apuestaCompleta = 0;
        
        //Bonus
        utilizarPremiosFijosBonus = true;
        utilizarPremiosVariablesBonus = false;
        premiosFijosBonus = config.getPremiosFijosBonus();
        premiosVariablesBonus = config.getPremiosVariablesBonus();
        totalGanadoEnBonus = 0;
        
        //Log de resultados 
        resultados = "";
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

    private void crearFigurasDePago(List<FiguraPago> figuras) {
        if (figuras == null) {
            figuras = FiguraPagoFactoria.cartones();
        }
        
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

    public int getGanadoEnBolasExtra() {
        return ganadoEnBolasExtra;
    }

    public void setGanadoEnBolasExtra(int ganadoEnBolasExtra) {
        this.ganadoEnBolasExtra = ganadoEnBolasExtra;
    }

    public boolean isUtilizarBolasExtraGratis() {
        return utilizarBolasExtraGratis;
    }

    public boolean isUtilizarPremiosFijosBonus() {
        return utilizarPremiosFijosBonus;
    }

    public void setUtilizarPremiosFijosBonus(boolean utilizarPremiosFijosBonus) {
        this.utilizarPremiosFijosBonus = utilizarPremiosFijosBonus;
    }

    public boolean isUtilizarPremiosVariablesBonus() {
        return utilizarPremiosVariablesBonus;
    }

    public void setUtilizarPremiosVariablesBonus(boolean utilizarPremiosVariablesBonus) {
        this.utilizarPremiosVariablesBonus = utilizarPremiosVariablesBonus;
    }

    public void setUtilizarBolasExtraGratis(boolean utilizarBolasExtraGratis) {
        this.utilizarBolasExtraGratis = utilizarBolasExtraGratis;
    }
    
    public int[] factoresDePago(){
        return this.factoresDePago;
    }

    public boolean isCrearFigurasDePago() {
        return crearFigurasDePago;
    }

    public void setCrearFigurasDePago(boolean crearFigurasDePago) {
        this.crearFigurasDePago = crearFigurasDePago;
    }

    public boolean isModoDebug() {
        return modoDebug;
    }

    public void setModoDebug(boolean modoDebug) {
        this.modoDebug = modoDebug;
    }
    
    public int[] ganado(){
        return this.ganado;
    }

    public int getCantidadDeBolasExtraSeleccionadas() {
        return cantidadDeBolasExtraSeleccionadas;
    }

    public boolean isSeLiberaronBolasExtra() {
        return seLiberaronBolasExtra;
    }

    public void setSeLiberaronBolasExtra(boolean seLiberaronBolasExtra) {
        this.seLiberaronBolasExtra = seLiberaronBolasExtra;
    }

    public String getResultados() {
        return resultados;
    }

    public void setResultados(String resultados) {
        this.resultados = resultados;
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

    private void mostrarPorPantalla(Object val){
        System.out.println(val);
    }
    
    public void mostrarConfiguracion() {
        //log(("Acumulado: " + this.acumulado());
        //log(("Ganado(por carton): " + ArrayUtils.toString(this.ganado()));
        //log(("Ganado(total): " + ganancias());
        //log(("Apuesta total: " + this.apuestaTotal());
        //log(("Apuesta individual: " + ArrayUtils.toString(apostado));
        //log(("Creditos: " + this.creditos());
        //log(("Cartones Habilitados: " + this.habilitados());
        //log(("Cartones Habilitados(individual): " + ArrayUtils.toString(this.cartonesHabilitados));
    }
    
    public void mostrarParametros(){
        //log(("Limite inferior de bola extra gratis: " + this.limiteInferiorParaBolaExtraGratis);
        //log(("Limite superior de bola extra gratis: " + this.limiteSuperiorParaBolaExtraGratis);
        //log(("Umbral: " + this.umbralParaLiberarBolasExtra);
        //log(("Utilizar umbral: " + this.utilizarUmbralParaLiberarBolasExtra);
        //log(("Tournament: " + (this.modoTournament? "Si" : "No" ));
        //log(("Porcentaje de descuento de apuestas para tournament: " + (int)(this.porcentajeDeDescuentoParaTournament * 100) + "%");
    }

    public int ganancias() {
        int result = 0;
        for (int i = 0; i < ganado.length; i++) {
            result += ganado[i];
        }
        result += totalGanadoEnBonus;
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
            result = true;
        }
        else{
            System.out.println("No se pudo habilitar");
            System.out.println("Apostado: " + ArrayUtils.toString(apostado));
            System.out.println("Habilitados: " + habilitados());
            System.out.println("Creditos: " + creditos);
            System.out.println("Cartones habilitados: " + ArrayUtils.toString(cartonesHabilitados));
        }
        return result;
    }
    
    public boolean deshabilitar(int i){
        boolean result = false;
        if (i >= 0 && i < CARTONES && cartonesHabilitados[i] &&
                apuestaTotal() - (apuestaTotal() / habilitados()) > 0) {
            apostado[i] = 0;
            cartonesHabilitados[i] = false;
            result = true;
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
        //log(("Bolas visibles: " + ArrayUtils.toString(this.bolasVisibles) + "(" + this.bolasVisibles.length + ")");
        //log(("Bolas extra: " + ArrayUtils.toString(this.bolasExtra) + "(" + this.bolasExtra.length + ")");
    }
    
    public void mostrarBolasExtraSeleccionadas(){
        //log(("Bolas extra seleccionadas: " + ArrayUtils.toString(bolasExtraSeleccionadas));
    }
    
    public int[] premiosSegunApostado(){
        int[] result = new int[cantidadDeFigurasDePago];
        for (int i = 0; i < cantidadDeFigurasDePago; i++) {
            result[i] = this.factoresDePago[i] * this.apuestaIndividual();
        }
        return result;
    }

    public int apuestaIndividual() {
        if (habilitados() > 0) {
            return this.apuestaTotal() / habilitados();
        }
        //log(("No hay cartones habilitados, no se puede computar la apuesta individual");
        return -1;
    }
    
    public void mostrarPremiosSegunApostado(){
        //log(("Premios segun apuestas: " + ArrayUtils.toString(this.premiosSegunApostado()));
        //log(("Nombres: " + ArrayUtils.toString(nombresDeFiguras));
    }
    
    public boolean liberarBolasExtra(int umbral){
        boolean result = false;
        for (int i = 0; i < premiosPorSalir.length; i++) {
            for (int j = 0; j < premiosPorSalir[0].length; j++) {
                if (premiosPorSalir[i][j] > 0 && factoresDePago[j] >= umbral) {
                    result = true;
                    this.seLiberaronBolasExtra = true;
                    //log(("Se deben liberar las bolas extra!");
                    break;
                }
            }
        }
        return result;
    }
    
    public boolean liberarBolasExtra(){
        boolean result = false;
        for (int i = 0; i < premiosPorSalir.length; i++) {
            for (int j = 0; j < premiosPorSalir[0].length; j++) {
                int creditosDelPremioPorSalir = premiosPorSalir[i][j] / this.apuestaIndividual();
                if (creditosDelPremioPorSalir > 0) {
                    if (!utilizarUmbralParaLiberarBolasExtra) {
                        result = true;
                        this.seLiberaronBolasExtra = true;
                        //log(("Se deben liberar las bolas extra!");
                        break;
                    }
                    else{
                        //Tener en cuenta el umbral
                        if (creditosDelPremioPorSalir > 0 && creditosDelPremioPorSalir > umbralParaLiberarBolasExtra) {
                            result = true;
                            this.seLiberaronBolasExtra = true;
                            //log(("Se deben liberar las bolas extra!");
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public int costoBolaExtra(){
        int factorDeGananciaDelPremioMayor = premioMayorPorSalir();
        
        if (utilizarBolasExtraGratis && factorDeGananciaDelPremioMayor >= limiteInferiorParaBolaExtraGratis 
                && factorDeGananciaDelPremioMayor <= limiteSuperiorParaBolaExtraGratis) {
            return 0;
        }
        int apuestaInd = this.apuestaIndividual();
        int costo = (int)(factorDeGananciaDelPremioMayor * apuestaInd * porcentajeDelPremioMayorPorSalirParaBolaExtra);
        //log(("Costo de la bola extra: " + costo);
//        System.out.println("Apuesta individual: " + apuestaInd + 
//                ". Porcentaje de descuento: " + porcentajeDelPremioMayorPorSalirParaBolaExtra + ". Costo: " + costo);
//        System.out.println("------------------------------------");
        return costo;
    }
    
    //Falta terminar
    public void buscarPremiosPorSalir(boolean utilizarUmbral){
        //Inicializo los premios por salir
        this.premiosPorSalir = new int[CARTONES][cantidadDeFigurasDePago];
        //Recorro los cartones
        for (int i = 0; i < CARTONES; i++) {
            //Recorro las figuras
            for (int j = 0; j < figurasDePago.length; j++) {
                int[][] casillasBinarias = Matematica.figuraBinaria(bolasVisibles, cartones[i]);
                
                //Verifico que le falte una casilla y ademas sea mayor al umbral
                //y ademas el premio que vaya a salir sea mayor a uno que ya salió
                if (cartonesHabilitados[i] && Matematica.leFaltaUno(casillasBinarias, figurasDePago[j])
                        && elPremioPorSalirEsMayorQueCualquierPremioQueYaSalio(this.premiosSegunApostado()[j], i)
                        ) {
                    
                    //Utilizar umbral?
                    if (utilizarUmbral) { //Si, utilizar el umbral
                        if (factoresDePago[j] >= umbralParaLiberarBolasExtra) { //El premio por salir supera el umbral?
                            premiosPorSalir[i][j] = premiosSegunApostado()[j]; //Si, contabilizar el premio
                        }
                    }
                    else{
                        premiosPorSalir[i][j] = premiosSegunApostado()[j]; //No, contabilizar ya que no importa el umbral
                    }
                    
                    //log("Premio por salir: " + nombresDeFiguras[j] + "(" + factoresDePago[j] + "). Umbral: " + umbralParaLiberarBolasExtra
                    //+ "(utilzarUmbral = " + utilizarUmbral + ")");
                }
            }
        }
        
        //Borrar aquellos premios por salir menores al mayor
        for (int i = 0; i < Juego.CARTONES; i++) {
            for (int j = 0; j < this.premiosPorSalir[0].length; j++) {
                if (premiosPorSalir[i][j] > 0) {
                    for (int k = j + 1; k < this.premiosPorSalir[0].length; k++) {
                        this.premiosPorSalir[i][k] = 0;
                    }
                }
            }
        }
        System.out.println("Resultado de la busqueda de premios por salir");
        
        for (int i = 0; i < Juego.CARTONES; i++) {
            System.out.println(i + ": " + ArrayUtils.toString(premiosPorSalir[i]));
        }
        
        System.out.println("Bolillero: " + ArrayUtils.toString(this.bolasVisibles));
    }
    
    private void buscarPremios(int[] bolilleroVisible, int[][][] cartones, FaseDeBusqueda fase){
        for (int i = 0; i < cartones.length; i++) {
            int[][] casillasConPremios = casillasGanadoras(cartones[i], bolilleroVisible);
            for (int j = 0; j < figurasDePago.length; j++) {
                if (cartonesHabilitados[i] && Matematica.estaContenido(figurasDePago[j],casillasConPremios)) {
                    //Premio encontrado
                    this.premiosPagados[i][j] = factoresDePago[j] * apuestaIndividual();
                    
                    if (fase.equals(FaseDeBusqueda.BOLAEXTRA)) {
                        
                        boolean premioGraciasABolaExtra = premiosPorSalir[i][j] > 0 && premiosPagados[i][j] > 0;
                        if (premioGraciasABolaExtra) {
                            this.ganadoEnBolasExtra += factoresDePago[j] * apuestaIndividual();
                        }
                    }
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
                            premiosPagados[i][k] -= factoresDePago[k] * apuestaIndividual();
                            if (fase.equals(FaseDeBusqueda.BOLAEXTRA)) {
                                int creditosPagadosAEliminar = factoresDePago[k] * apuestaIndividual();
                                if (this.ganadoEnBolasExtra > 0 && this.ganadoEnBolasExtra - creditosPagadosAEliminar > 0) {
                                    this.ganadoEnBolasExtra -= factoresDePago[k] * apuestaIndividual();
                                }
                            }
                        }
                    }
                }
            }
        }
        
        //Borrar premios a partir del mayor hacia el menor
        //Esto es necesario para igualar el criterio de la vista con el backend
        //Este procedimiento pisa el anterior criterio
        for (int i = 0; i < premiosPagados.length; i++) {
            for (int j = 0; j < premiosPagados[0].length; j++) {
                if (premiosPagados[i][j] > 0) {
                    for (int k = j + 1; k < premiosPagados[0].length; k++) {
                        premiosPagados[i][k] = 0;
                    }
                }
            }
        }
    }
    
    private void computarFrecuencias(){
        
        for (int i = 0; i < CARTONES; i++) {
            ArrayUtils.toString(this.premiosPagados[i]);
        }
        
        for (int i = 0; i < figurasDePago.length; i++) {
            int mayor = frecuenciaFigurasPrimeraFase[i] > frecuenciaFigurasSegundaFase[i] ? frecuenciaFigurasPrimeraFase[i] : frecuenciaFigurasSegundaFase[i];
            frecuenciaFiguras[i] = mayor;
        }
        
        System.out.println();
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
//                //log(("Figura premiada anteriormente: " + this.nombresDeFiguras[i]);
//                //log(("No se debe pagar, casillas comparadas");
//                for (int j = 0; j < contenido.length; j++) {
//                    //log((ArrayUtils.toString(contenido[j]));
//                }
//                break;
//            }
//        }
//        return result;
//    }
    
    public void buscarPremios(FaseDeBusqueda fase){
        this.buscarPremios(bolasVisibles, cartones, fase);
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
        //log(("Premios obtenidos: ");
        for (int i = 0; i < premios.length; i++) {
            //log(("Carton " + (i + 1) + ": " + ArrayUtils.toString(premios[i]));
        }
    }
    
    public void mostrarCartones(){
        //log(("Cartones actuales");
        for (int i = 0; i < cartones.length; i++) {
            for (int j = 0; j < cartones[0].length; j++) {
                //log((ArrayUtils.toString(cartones[i][j]));
            }
            //log(("-----------------");
        }
    }

    public void mostrarGanado() {
        //log(("Ganancias totales: " + ganancias());
        //log(("Ganancias parciales: " + ArrayUtils.toString(this.ganado));
        //log(("Ganancias en bonus: " + totalGanadoEnBonus);
    }

    public void mostrarCreditos() {
        //log(("Creditos: " + this.creditos);
    }

    public void mostrarApuestas() {
        int apuestaBasica = this.apuestaTotal();
        
        //log(("Apuesta básica: " + apuestaBasica);
        //log(("Creditos apostados en bolas extra: " + this.creditosInvertidosEnBolasExtra);
        //log(("Apuesta total: " + (apuestaBasica + this.creditosInvertidosEnBolasExtra));
        //log(("Apuestas individuales: " + ArrayUtils.toString(this.apostado));
    }

    private int premioMayorPorSalir() {
        int premio = 0;
        String nombrePremio = "";
        for (int i = 0; i < CARTONES; i++) {
            for (int j = 0; j < premiosPorSalir[i].length; j++) {
                if (cartonesHabilitados[i] && premiosPorSalir[i][j] / apuestaIndividual() > premio) {
                    premio = premiosPorSalir[i][j] / apuestaIndividual();
                    nombrePremio = nombresDeFiguras[j];
//                    System.out.println("Mayor actual: " + nombrePremio + "(" + premio + ")");
                }
            }
        }
//        System.out.println("Premio mayor por salir: " + nombrePremio + "(" + premio + "). Apuesta: " + this.apostado[0]);
//        //log(("Premio mayor por salir: " + nombrePremio + "("+ premio + "), premiosPorSalir: " + ArrayUtils.toString(premiosPorSalir));
//        for (int i = 0; i < CARTONES; i++) {
//            System.out.println(ArrayUtils.toString(premiosPorSalir[i]));
//        }
        
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

    public boolean isModoDeshabilitarPorFaltaDeCredito() {
        return modoDeshabilitarPorFaltaDeCredito;
    }

    public void setModoDeshabilitarPorFaltaDeCredito(boolean modoDeshabilitarPorFaltaDeCredito) {
        this.modoDeshabilitarPorFaltaDeCredito = modoDeshabilitarPorFaltaDeCredito;
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

    public Integer[] getBonus() {
        return bonus;
    }

    public void setBonus(Integer[] bonus) {
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

    public boolean isModoBonus() {
        return modoBonus;
    }

    public void setModoBonus(boolean modoBonus) {
        this.modoBonus = modoBonus;
    }

    public boolean[] getFigurasConBonus() {
        return figurasConBonus;
    }

    public boolean isUtilizarUmbralParaLiberarBolasExtra() {
        return utilizarUmbralParaLiberarBolasExtra;
    }

    public void setUtilizarUmbralParaLiberarBolasExtra(boolean utilizarUmbralParaLiberarBolasExtra) {
        this.utilizarUmbralParaLiberarBolasExtra = utilizarUmbralParaLiberarBolasExtra;
    }

    public void setFigurasConBonus(boolean[] figurasConBonus) {
        this.figurasConBonus = figurasConBonus;
    }

    public void setPremiosDelBonusSeleccionados(boolean[] premiosDelBonusSeleccionados) {
        this.premiosDelBonusSeleccionados = premiosDelBonusSeleccionados;
    }

    public int getCantidadTotalDePremiosEnBonus() {
        return cantidadTotalDePremiosEnBonus;
    }

    public void setCantidadTotalDePremiosEnBonus(int cantidadTotalDePremiosEnBonus) {
        this.cantidadTotalDePremiosEnBonus = cantidadTotalDePremiosEnBonus;
    }

    public int getCantidadDePremiosBonusFijo() {
        return cantidadDePremiosBonusFijo;
    }

    public void setCantidadDePremiosBonusFijo(int cantidadDePremiosBonusFijo) {
        this.cantidadDePremiosBonusFijo = cantidadDePremiosBonusFijo;
    }

    public int getCantidadDePremiosBonusVariable() {
        return cantidadDePremiosBonusVariable;
    }

    public void setCantidadDePremiosBonusVariable(int cantidadDePremiosBonusVariable) {
        this.cantidadDePremiosBonusVariable = cantidadDePremiosBonusVariable;
    }

    public int getTotalGanadoEnBonus() {
        return totalGanadoEnBonus;
    }

    public Map<Integer, Integer> getPremiosFijosBonus() {
        return premiosFijosBonus;
    }

    public void setPremiosFijosBonus(Map<Integer, Integer> premiosFijosBonus) {
        this.premiosFijosBonus = premiosFijosBonus;
    }

    public Map<Integer, Integer> getPremiosVariablesBonus() {
        return premiosVariablesBonus;
    }

    public void setPremiosVariablesBonus(Map<Integer, Integer> premiosVariablesBonus) {
        this.premiosVariablesBonus = premiosVariablesBonus;
    }

    public int getCantidadDeVecesQueSeObtuvoElBonus() {
        return cantidadDeVecesQueSeObtuvoElBonus;
    }

    public void setCantidadDeVecesQueSeObtuvoElBonus(int cantidadDeVecesQueSeObtuvoElBonus) {
        this.cantidadDeVecesQueSeObtuvoElBonus = cantidadDeVecesQueSeObtuvoElBonus;
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
    
    public int ganadoEnCarton(int numero){
        int result = 0;
        for (int i = 0; i < this.premiosPagados[numero - 1].length; i++) {
            result += this.premiosPagados[numero - 1][i];
        }
        return result;
    }
    
    public void jugar(boolean generarNuevoBolillero){
        
        if (generarNuevoBolillero) {
            this.generarBolillero();
        }
        
        //Genero un nuevo Bonus
        if (modoSimulacion) {
            this.generarBonus();
        }
        
        if (this.apuestaTotal() > this.creditos) {
            log("La apuesta total de " + this.apuestaTotal() + " supera los "
                    + "creditos disponibles, " + creditos);
            return;
        }
        
        borrarJuegoAnterior();
        
//        mostrarBolillero();
        
        if (cartonesDeJuegoVacios()) {
            //log(("Los cartones de juego no fueron colocados, juego abortado...");
            return;
        }
        
        iniciado = true; //Comenzó el juego
        inicioDelCicloDeJuego = true;
        
        this.descontarApuestas();
        
        this.buscarPremios(FaseDeBusqueda.PRIMERA); //Busco premios comunmente
        this.buscarPremiosPorSalir(utilizarUmbralParaLiberarBolasExtra); //Busco premios por salir
        
        if (modoSimulacion) {
            if (modoBonus && huboBonus()) { //Si el bonus esta habilitado y ademas hay premios con bonus
                //Iniciar ciclo de bonus
                cicloBonus();
            }

            if (liberarBolasExtra()) {

                //Iniciar ciclo de bolas extra
                cicloDeBolasExtra();
            }
        }
        
//        computarFrecuencias();
        
        computarGanancias();
        
        mostrarPremiosSegunApostado();
        mostrarPremiosObtenidos();
        mostrarGanado();
        mostrarApuestas();
        
        //Si ya no tengo credito disponible, partir y deshabilitar los cartones
        if (modoDeshabilitarPorFaltaDeCredito) {
            deshabilitarCartonesSiNoHayCreditoSuficiente();
        }
        
        //Verificar si se debe pagar el tournament
        if (pagarTournament()) {
            
        }
    }

    private boolean cartonesDeJuegoVacios() {
        return this.cartones == null;
    }

    public boolean huboBonus() {
        for (int i = 0; i < premiosPagados.length; i++) {
            for (int j = 0; j < premiosPagados[i].length; j++) {
                if (premiosPagados[i][j] > 0 && figurasConBonus[j]) {
//                    System.out.println("Bonus en figura: " + nombresDeFiguras[j]);
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
        cantidadDeVecesQueSeObtuvoElBonus = 0;
        salioElBonusAlInicioDelJuego = false;
        inicioDelCicloDeJuego = false;
        indiceBolaExtra = 0;
    }

    private void borrarSeleccionDeBolasExtra() {
        this.bolasExtraSeleccionadas = new boolean[CANTIDADDEBOLASEXTRA];
    }

    public void cicloDeBolasExtra() {
        if (perfilActual == null) {
            //log(("No se ha cargado un perfil, saliendo del ciclo de bolas extra....");
            return;
        }
        if (cantidadDeBolasExtraPorComprar() == 0) {
            //log(("No se pueden comprar bolas extra, ya no hay ninguna, saliendo del ciclo de bolas extra...");
            return;
        }
        int costoBolaSeleccionada = costoBolaExtra();
        boolean puedeComprar = cantidadDeBolasExtraPorComprar() > 0 || costoBolaSeleccionada == 0;
        while (cantidadDeBolasExtraPorComprar() > 0 && 
                RNG.getInstance().pick() <= perfilActual.getProbabilidadDeComprarBolasExtra()
                && puedeComprar) {
            
            int indiceDeBolaExtraAComprar = seleccionarAleatoriamenteBolaExtraDisponible();
            costoBolaSeleccionada = costoBolaExtra();
            
            //log(("Indice de bola extra elegida: " + indiceDeBolaExtraAComprar);
            //log(("Costo de la bola elegida: " + costoBolaSeleccionada);
            //log(("Numero de bola: " + bolasExtra[indiceDeBolaExtraAComprar]);
            
            //Verifico que tenga creditos disponibles para comprar
            if (creditos - costoBolaSeleccionada >= 0) {
                //Tiene creditos, descontar 
                //log(("Puede comprar la bola extra! Descontando el costo anterior del credito");
                bolasExtraSeleccionadas[indiceDeBolaExtraAComprar] = true; //Marco como seleccionada
                creditos -= costoBolaSeleccionada; //Descuento
                
                //log(("Se descontaron creditos por la bolas extra, credito actual: " + creditos);
                
                //Aumento el contador de credito gastado en bolas extra
//                creditosInvertidosEnBolasExtra += costoBolaSeleccionada; //Solo computa lo invertido en bolas extra, no la apuesta basica
                creditosInvertidosEnBolasExtra += costoBolaSeleccionada;
                apuestaCompleta += costoBolaSeleccionada;
                
                //Aumento el contador de veces que se compraron bolas extra
                cantidadDeBolasExtraSeleccionadas++;
                
                bolasVisibles  = Arrays.copyOf(bolasVisibles, bolasVisibles.length + 1); //Genero una nueva ranura para la bola extra en las visibles
                bolasVisibles[bolasVisibles.length - 1] = bolasExtra[indiceDeBolaExtraAComprar]; //agrego el numero de la bola extra nueva
                buscarPremios(FaseDeBusqueda.BOLAEXTRA);
                
                //log(("Compró la bola: " + indiceDeBolaExtraAComprar);
                //log(("Costo: " + costoBolaSeleccionada);
                
                //Verificar si se debe lanzar el bonus
                if (salioUnNuevoBonus()) {
                    //log(("Comenzó el ciclo de bonus gracias a la bola extra");
                    
                    cicloBonus();
                }
                
                //Una vez que compró la bola extra se debe volver a buscar las nuevas figuras
                //por salir, ya que la introducción de una bola nueva genera nuevas figuras por salir
                //log(("Se buscaran nuevos premios por salir, ya que compró una bola extra nueva");
                buscarPremiosPorSalir(utilizarUmbralParaLiberarBolasExtra);
                
            }
            else{
                //No tiene creditos disponibles, salir del ciclo
                //log(("No tiene creditos disponibles para comprar la bola extra!");
                break;
            }
        }
        //Le sumo la apuesta total ya que me interesa saber cuantos creditos
        //Se inviertieron desde que se inició el ciclo (con la apuesta basica)
        //mas lo invertido comprando bolas extra
//        apuestaCompleta += apuestaTotal();
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
        
        //log(("Creditos luego de descontar: " + this.creditos);
        
        //Si el juego es comunitario sumar al pozo y descontar un porcentaje de la apuesta
        if (modoTournament) {
            acumulado += apuestaTotal() * porcentajeDeDescuentoParaTournament;
//            dinero -= apuestaTotal() * porcentajeDeDescuentoParaTournament;
            this.creditos -= apuestaTotal() * porcentajeDeDescuentoParaTournament;//Descuento el porcentaje de tournament al credito
            
            if (creditos < 0) {
                creditos = 0;
            }
            
            //log(("Creditos luego de descontar el tournament: " + this.creditos);
        }
    }
    
    /**
     * El jugador decide cobrar su dinero ganado
     * @return TRUE si se cobró correctamente
     */
    public double cobrar(){
        
        //log(("Creditos a cobrar: " + creditos);
        //log(("Dinero antes del calculo: " + dinero);
        
        //Sumo el dinero (si esta en modo Tournament el dinero tiene un valor
        //Negativo, debido al descuento de la apuesta del jugador)
        //Con los creditos en la denominacion elegida
        double dineroCobrado = dinero + creditos * denominacion.getValue();
        
        //log(("Dinero a cobrar: " + dineroCobrado);
        
        dineroCobrado = Matematica.redondear(dineroCobrado, 2);
        dinero = 0.0;
        
        //log(("Dinero a cobrar(redondeado): " + dineroCobrado);
        
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

    public void generarBonus(){
        this.bonus = new Integer[utilizarPremiosFijosBonus? this.cantidadDePremiosBonusFijo : this.cantidadDePremiosBonusVariable];
        
        //Cargo los valores aleatorios
        if (utilizarPremiosVariablesBonus) {
            //Genero los premios en funcion de lo apostado
            int apuesta = this.apuestaTotal();
            
            Integer[] variables = Matematica.crearArregloAleatorioConCeros(premiosVariablesBonus, cantidadTotalDePremiosEnBonus);
            for (int i = 0; i < cantidadTotalDePremiosEnBonus; i++) {
                bonus[i] = apuesta * variables[i];   
            }
//            System.out.println("Premios del bonus: " + ArrayUtils.toString(bonus));
        }
        else{
            if (utilizarPremiosFijosBonus) {
                bonus = Matematica.crearArregloAleatorioConCeros(premiosFijosBonus, cantidadTotalDePremiosEnBonus);
//                System.out.println("Premios del bonus: " + ArrayUtils.toString(bonus));
            }
            else{
                //Por defecto cargo el bonus fijo
                bonus = Matematica.crearArregloAleatorioConCeros(premiosFijosBonus, cantidadTotalDePremiosEnBonus);
//                System.out.println("Premios del bonus: " + ArrayUtils.toString(bonus));
            }
        }
    }
    
    public void generarBonusB1(){
        this.bonus = new Integer[utilizarPremiosFijosBonus? this.cantidadDePremiosBonusFijo : this.cantidadDePremiosBonusVariable];
        
        //Cargo los valores aleatorios
        if (utilizarPremiosVariablesBonus) {
            //Genero los premios en funcion de lo apostado
            int apuesta = this.apuestaTotal();
            
            List<Integer> variables = new ArrayList<>();
            for(Map.Entry<Integer,Integer> entry : this.premiosVariablesBonus.entrySet()){
                for (int i = 0; i < entry.getKey(); i++) {
                    variables.add(entry.getValue());
                }
            }
            
            //Mover aleatoriamente los elementos
            Integer[] valores = Arrays.copyOfRange(variables.toArray(), 0, variables.toArray().length, Integer[].class);
            Matematica.revolver(valores);
            
            this.bonus = new Integer[variables.size() - RNG.getInstance().pickInt(variables.size() - 1)];
            
            for (int i = 0; i < bonus.length; i++) {
                bonus[i] = apuesta * valores[i];   
            }
//            System.out.println("Premios del bonus: " + ArrayUtils.toString(bonus));
        }
        else{
            if (utilizarPremiosFijosBonus) {
                bonus = Matematica.crearArregloAleatorioConCeros(premiosFijosBonus, cantidadTotalDePremiosEnBonus);
//                System.out.println("Premios del bonus: " + ArrayUtils.toString(bonus));
            }
            else{
                //Por defecto cargo el bonus fijo
                bonus = Matematica.crearArregloAleatorioConCeros(premiosFijosBonus, cantidadTotalDePremiosEnBonus);
//                System.out.println("Premios del bonus: " + ArrayUtils.toString(bonus));
            }
        }
    }
    
    private void cicloBonus() {
        //Inicializo los valores
        totalGanadoEnBonus = 0;
        bonus = new Integer[cantidadTotalDePremiosEnBonus];
        premiosDelBonusSeleccionados = new boolean[cantidadTotalDePremiosEnBonus];
        
        //Computar la cantidad de veces que se ingresó al bonus
        this.cantidadDeVecesQueSeObtuvoElBonus++;
        
        //log(("Comienzo del ciclo del bonus!");
        
        //Genero los premios en funcion de lo apostado
        int apuesta = this.apuestaTotal();
        
        //Cargo los valores aleatorios
        if (utilizarPremiosVariablesBonus) {
            Integer[] variables = Matematica.crearArregloAleatorioConCeros(premiosVariablesBonus, cantidadTotalDePremiosEnBonus);
            for (int i = 0; i < cantidadTotalDePremiosEnBonus; i++) {
                bonus[i] = apuesta * variables[i];   
            }
//            System.out.println("Premios del bonus: " + ArrayUtils.toString(bonus));
        }
        else{
            if (utilizarPremiosFijosBonus) {
                bonus = Matematica.crearArregloAleatorioConCeros(premiosFijosBonus, cantidadTotalDePremiosEnBonus);
//                System.out.println("Premios del bonus: " + ArrayUtils.toString(bonus));
            }
            else{
                //Por defecto cargo el bonus fijo
                bonus = Matematica.crearArregloAleatorioConCeros(premiosFijosBonus, cantidadTotalDePremiosEnBonus);
//                System.out.println("Premios del bonus: " + ArrayUtils.toString(bonus));
            }
        }
        
        
        //log(("Premios del bonus: " + ArrayUtils.toString(bonus));
        
        //Comienza la seleccion aleatoria
        int indice = RNG.getInstance().pickInt(bonus.length);
        while(quedanPremiosDelBonusDisponibles() && bonus[indice] != 0 && !premiosDelBonusSeleccionados[indice]){
            totalGanadoEnBonus += bonus[indice];
            premiosDelBonusSeleccionados[indice] = true;
            
            //log(("Seleccionado: " + indice);
            //log(("Premio bonus: " + bonus[indice]);
            
            while(premiosDelBonusSeleccionados[indice] && quedanPremiosDelBonusDisponibles()){
                indice = RNG.getInstance().pickInt(bonus.length);
            }
        }
        
        //log(("Finalizó el bonus, indice = " + indice + ". Ganancia total: " + totalGanadoEnBonus);
        //log(("Creditos: " + creditos);
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

    public int[] getFrecuenciaFiguras() {
        return frecuenciaFiguras;
    }

    public void setFrecuenciaFiguras(int[] frecuenciaFiguras) {
        this.frecuenciaFiguras = frecuenciaFiguras;
    }
    
    public boolean huboBolasExtraLiberadas(){
        return iniciado && this.liberarBolasExtra(umbralParaLiberarBolasExtra);
    }

    public int getApuestaCompleta() {
        return this.apuestaTotal() + this.creditosInvertidosEnBolasExtra;
    }

    public void setApuestaCompleta(int apuestaCompleta) {
        this.apuestaCompleta = apuestaCompleta;
    }
    
    public boolean huboPremiosPorSalir(){
        return premioMayorPorSalir() > 0;
    }

    private void deshabilitarCartonesSiNoHayCreditoSuficiente() {
        int apuestasNuevas = apuestaIndividual();
        int apuestaTotal = apuestaTotal();
        if (apuestaTotal > creditos) {
            
            //log(("No hay creditos suficientes, se deshabilitaran los cartonas superiores");
            
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
                apostado[0] = creditos > 0 ? creditos : apuestasNuevas > 0 ? apuestasNuevas - 1 : 1;
            }
            
            //log(("Cartones luego de deshabilitar: " + ArrayUtils.toString(cartonesHabilitados));
            //log(("Apuestas luego de deshabilitar: ");
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

    public void habilitarTodos() {
        for (int i = 0; i < 4; i++) {
            this.habilitar(i);
        }
    }

    public void apostar(int apuestaTotal) {
        int habilitados = habilitados();
        int apuestaIndividual = habilitados > 0 ? apuestaTotal : 0;
        
        if (apuestaIndividual == 0) {
            apuestaIndividual = 1;
        }
        
        if (apuestaIndividual > 30) {
            apuestaIndividual = 30;
        }
        
        for (int i = 0; i < habilitados; i++) {
            this.apostado[i] = apuestaIndividual;
        }
    }
    
    public void apostarEquitativamente(int apuesta, int cantidadDeCartonesHabilitados) {
        int habilitados = cantidadDeCartonesHabilitados > 0 ? cantidadDeCartonesHabilitados : 1;
        int apuestaIndividual = habilitados > 0 ? apuesta : 0;
        
        if (apuestaIndividual == 0) {
            apuestaIndividual = 1;
        }
        
        if (apuestaIndividual > 30) {
            apuestaIndividual = 30;
        }
        
        for (int i = 0; i < habilitados; i++) {
            this.apostado[i] = apuestaIndividual;
        }
    }
    
    private void log(Object val){
        if (modoDebug) {
            resultados += val.toString() + "\n";
        }
    }

    public void setPametrosBonus(boolean utilizarPremiosFijosBonus, boolean utilizarPremiosVariablesBonus, Map<Integer,Integer> premiosFijosBonus, Map<Integer,Integer> premiosVariablesBonus, int cantidadDePremiosBonus) {
        this.utilizarPremiosFijosBonus = utilizarPremiosFijosBonus;
        this.utilizarPremiosVariablesBonus = utilizarPremiosVariablesBonus;
        this.premiosFijosBonus = premiosFijosBonus;
        this.premiosVariablesBonus = premiosVariablesBonus;
        this.cantidadTotalDePremiosEnBonus = cantidadDePremiosBonus;
    }

    private boolean salioUnNuevoBonus() {
        boolean result = false;
        //Recorrer los premios nuevos, si la interseccion entre los premios
        //obtenidos y los premios por salir es mayor a cero entonces
        //la bola extra comprada produjo un nuevo premio, verificar si este 
        //nuevo premio es bonus
        for (int i = 0; i < Juego.CARTONES; i++) {
            for (int j = 0; j < premiosPagados[i].length; j++) {
                if (premiosPagados[i][j] > 0 && premiosPorSalir[i][j] > 0) {
                    //El premio por salir efectivamente salió
                    //Verificar si es bonus
                    if (figurasConBonus[j]) {
                        //Es bonus
                        result = true;
//                        System.out.println("Salio un nuevo bonus: " + nombresDeFiguras[j]);
//                        this.cantidadDeVecesQueSeObtuvoElBonus--;
                        break;
                    }
                }
            }
        }
        return result;
    }
    
//    public Map<Integer, List<Integer>> verPremiosPorCarton(){
//        Map
//    }

    public String seleccionarBolaExtra() {
        return seleccionarBolaExtra(indiceBolaExtra++);
    }
    
    public String seleccionarBolaExtra(int indice) {
        String result = "";
        if (indice > -1 && indice < 10) {
            System.out.println("Metodo seleccionarBolaExtra");
            
            int costoBolaExtra = this.costoBolaExtra();
                
            System.out.println("Costo bola extra: " + costoBolaExtra);
            
            //Indice valido, seleccionar la bola extra y buscar los nuevos premios
            if (!bolasExtraSeleccionadas[indice] && (creditos - costoBolaExtra > 0)) {
                
                //Cambio la bandera de la bola extra seleccionada
                bolasExtraSeleccionadas[indice] = true;
                
                System.out.println("Indice: " + indice);
                
                //Correcto, realizar la busqueda de nuevos premios y premios por salir, 
                //descontar el costo de la bola extra actual
                
                //Agregar la bola extra a la lista de bolas visibles
                this.bolasVisibles = (int[])ArrayUtils.add(this.bolasVisibles, this.bolasExtra[indice]);
                
                System.out.println("Bolas visibles: " + ArrayUtils.toString(this.bolasVisibles));
                
                //Coloco en 0 el valor de la bola extra
                this.bolasExtra[indice] = 0;
                
                System.out.println("Bolas extra: " + ArrayUtils.toString(this.bolasExtra));
                
                //Descuento el valor de la bola extra del credito actual
                this.creditos -= costoBolaExtra;
                
                System.out.println("Creditos: " + creditos);
                
                //Almaceno una copia de los premios pagados para verificar
                //luego si debo computar premios nuevos
                int[][] premiosObtenidosEnFaseCero = clonar(this.premiosPagados);
                
                //Busco los nuevos premios
                buscarPremios(FaseDeBusqueda.PRIMERA);
                
                //Muestro los premios actuales
                System.out.println("**************************************");
                
                System.out.println("Premios en fase 1");
                
                for (int i = 0; i < Juego.CARTONES; i++) {
                    System.out.println(i + ": " + ArrayUtils.toString(this.premiosPagados[i]));
                }
                
                System.out.println("**************************************");
                //Verificar si se obtuvo un nuevo premio
                //La condicion de obtencion de nuevo premio gracias a una bola
                //extra es que luego de buscar los premios con la bola extra
                //en el conjunto de bolas visibles
                //el vector de premios y premios por salir tendran un elemento
                //en comun, esto indica que el premio por salir salió y se encuentra
                //computado en el vector de premios, se debe sumar al credito ganado
                //y luego buscar los nuevos premios por salir
                result = buscarNuevosPremios(result, premiosObtenidosEnFaseCero);
                
                //Busco los premios por salir
                buscarPremiosPorSalir(utilizarUmbralParaLiberarBolasExtra);
                
                if (result.isEmpty()) {
                    result = "No se obtuvieron premios al elegir la bola " + bolasVisibles[bolasVisibles.length - 1];
                }
            }
            else{
                result = "Indice ya seleccionado, no se realizaran operaciones. Abortado";
            }
        }
        else{
            result = "Indice fuera de rango, valores validos: de 0 a 9";
        }
        return result;
    }

    public int[] getBolas() {
        return ArrayUtils.addAll(this.bolasVisibles, this.bolasExtra);
    }

    private boolean elPremioPorSalirEsMayorQueCualquierPremioQueYaSalio(int creditosQuePagaElCartonPorSalir, int numeroDeCarton) {
        boolean result = true;
        for (int i = 0; i < premiosPagados[numeroDeCarton].length; i++) {
            if (premiosPagados[numeroDeCarton][i] > creditosQuePagaElCartonPorSalir) {
                result = false;
                break;
            }
        }
        return result;
    }

    private String buscarNuevosPremios(String result, int[][] premiosObtenidosEnFaseCero) {
        
        
        
        System.out.println("---------------------------------------------");
        
        System.out.println("Premios obtenidos en fase 0");
        
        for (int i = 0; i < Juego.CARTONES; i++) {
            System.out.println(i + ": " + ArrayUtils.toString(premiosObtenidosEnFaseCero[i]));
        }
        
        System.out.println("---------------------------------------------");
        
        //Recorro los nuevos premios y comparo si debo pagar un premio nuevo
        for (int i = 0; i < Juego.CARTONES; i++) {
            for (int j = 0; j < premiosPagados[0].length; j++) {
                if (premiosPagados[i][j] > 0 && premiosPagados[i][j] > premiosObtenidosEnFaseCero[i][j]) {
                    creditos += premiosPagados[i][j];
                    
                    System.out.println("*****************************************");
                    
                    System.out.println("Premio Obtenido en " + i + "," + j);
                    
                    System.out.println("*****************************************");
                    
                    for (int k = j + 1; k < premiosPagados[0].length; k++) {
                        creditos -= premiosObtenidosEnFaseCero[i][k];
                    }
                }
            }
        }
        
        for (int i = 0; i < Juego.CARTONES; i++) {
                    for (int j = 0; j < premiosPagados.length; j++) {
                        if (premiosPagados[i][j] > 0 && 
                            premiosPorSalir[i][j] > 0 &&
                            premiosPagados[i][j] == premiosPorSalir[i][j]) {
                            //Salio el premio gracias a la bola extra comprada
                            creditos += premiosPagados[i][j];
                            
                            //Coloco el mensaje correspondiente
                            result = "Premio obtenido gracias a la bola extra " + bolasVisibles[bolasVisibles.length - 1];
                            
                            System.out.println("Premio obtenido gracias a la bola extra: " + this.nombresDeFiguras[j]
                                + " en carton: " + (i + 1)
                                    );
                            
                            System.out.println("Premios actuales");
                            
                            for (int k = 0; k < this.premiosPagados.length; k++) {
                                System.out.println(k + ": " + ArrayUtils.toString(this.premiosPagados[k]));
                            }
                            
                            //Parar la iteracion, solo un premio puede formarse
                            //ya que las bolas son unicas e irrepetibles
                            break;
                        }
                    }
                }
        return result;
    }

    private int[][] clonar(int[][] premiosPagados) {
        int[][] result = new int[premiosPagados.length][premiosPagados[0].length];
        
        for (int i = 0; i < premiosPagados.length; i++) {
            for (int j = 0; j < premiosPagados[0].length; j++) {
                result[i][j] = premiosPagados[i][j];
            }
        }
        return result;
    }
    
}
