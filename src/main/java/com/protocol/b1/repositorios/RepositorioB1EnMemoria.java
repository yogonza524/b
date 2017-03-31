/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.protocol.b1.repositorios;

import com.protocol.b1.enumeraciones.Idioma;
import com.protocol.b1.enumeraciones.Pais;
import com.protocol.b1.interfaces.RepositorioB1;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Repositorio de datos en memoria, solo utilizarlo en modo
 * Testing o para fines demostrativos, no válido para 
 * su utilización en producción (información volátil)
 *
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public class RepositorioB1EnMemoria implements RepositorioB1{

    private int numeroDeMaquina;
    private List<Idioma> idiomas;
    private Idioma idioma;
    private boolean sonidos;
    private BigInteger cantidadDeBilletesDe$1;
    private BigInteger cantidadDeBilletesDe$2;
    private BigInteger cantidadDeBilletesDe$5;
    private BigInteger cantidadDeBilletesDe$10;
    private BigInteger cantidadDeBilletesDe$20;
    private BigInteger cantidadDeBilletesDe$50;
    private BigInteger cantidadDeBilletesDe$100;
    private BigInteger cantidadDeBilletesDe$200;
    private BigInteger cantidadDeBilletesDe$500;
    private BigInteger cantidadDeBilletesDe$1000;
    private BigInteger cantidadDeJuegosJugados;
    private BigInteger cantidadDeJuegosGanados;
    private BigInteger cantidadDeJuegosPerdidos;
    private BigInteger cantidadDeJuegosDesdeElEncendido;
    private BigInteger cantidadDeJuegosDesdeQueSeCerroLaPuertaPrincipal;
    private int porcentajeDeRetribucion;
    private Pais pais;
    private String numeroDeSerie;
    private boolean modoMantenimiento;
    private boolean maquinaBloqueada;
    private boolean maquinaEncendida;

    /**
     * Constructor por defecto
     */
    public RepositorioB1EnMemoria() {
        this.numeroDeMaquina = 1;
        this.idiomas = new ArrayList<>();
        this.idiomas.addAll(Arrays.asList(Idioma.values()));
        this.idioma = Idioma.ENGLISH;
        this.cantidadDeBilletesDe$1 = BigInteger.ZERO;
        this.cantidadDeBilletesDe$2 = BigInteger.ZERO;
        this.cantidadDeBilletesDe$5 = BigInteger.ZERO;
        this.cantidadDeBilletesDe$10 = BigInteger.ZERO;
        this.cantidadDeBilletesDe$20 = BigInteger.ZERO;
        this.cantidadDeBilletesDe$50 = BigInteger.ZERO;
        this.cantidadDeBilletesDe$100 = BigInteger.ZERO;
        this.cantidadDeBilletesDe$200 = BigInteger.ZERO;
        this.cantidadDeBilletesDe$500 = BigInteger.ZERO;
        this.cantidadDeBilletesDe$1000 = BigInteger.ZERO;
        this.cantidadDeJuegosJugados = BigInteger.ZERO;
        this.cantidadDeJuegosGanados = BigInteger.ZERO;
        this.cantidadDeJuegosPerdidos = BigInteger.ZERO;
        this.cantidadDeJuegosDesdeElEncendido = BigInteger.ZERO;
        this.cantidadDeJuegosDesdeQueSeCerroLaPuertaPrincipal = BigInteger.ZERO;
        this.pais = Pais.ARGENTINA;
        this.numeroDeSerie = "ESSENTIT-B1";
        this.modoMantenimiento = false;
        this.maquinaBloqueada = false;
        this.maquinaEncendida = true;
        this.porcentajeDeRetribucion = 90;
    }
    
    @Override
    public int getNumeroDeMaquina() {
        return this.numeroDeMaquina;
    }
    
    // <editor-fold defaultstate="collapsed" desc="Atributos">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Metodos">

    //</editor-fold>

    @Override
    public void setNumeroDeMaquina(int numeroDeMaquina) {
        this.numeroDeMaquina = numeroDeMaquina;
    }

    @Override
    public void setIdiomasSoportados(List<Idioma> idiomas) {
        this.idiomas = idiomas;
    }

    @Override
    public List<Idioma> getIdiomasSoportados() {
        return this.idiomas;
    }

    @Override
    public void setIdiomaActual(Idioma idioma) {
        this.idioma = idioma;
    }

    @Override
    public Idioma getIdiomaActual() {
        return this.idioma;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$1() {
        this.cantidadDeBilletesDe$1 = this.cantidadDeBilletesDe$1.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$2() {
        this.cantidadDeBilletesDe$2 = this.cantidadDeBilletesDe$2.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$5() {
        this.cantidadDeBilletesDe$5 = this.cantidadDeBilletesDe$5.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$10() {
        this.cantidadDeBilletesDe$10 = this.cantidadDeBilletesDe$10.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$20() {
        this.cantidadDeBilletesDe$20 = this.cantidadDeBilletesDe$20.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$50() {
        this.cantidadDeBilletesDe$50 = this.cantidadDeBilletesDe$50.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$100() {
        this.cantidadDeBilletesDe$100 = this.cantidadDeBilletesDe$100.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$200() {
        this.cantidadDeBilletesDe$200 = this.cantidadDeBilletesDe$200.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$500() {
        this.cantidadDeBilletesDe$500 = this.cantidadDeBilletesDe$500.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$1000() {
        this.cantidadDeBilletesDe$1000 = this.cantidadDeBilletesDe$1000.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarCantidadDeJuegosJugados() {
        this.cantidadDeJuegosJugados = this.cantidadDeJuegosJugados.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarCantidadDeJuegosGanados() {
        this.cantidadDeJuegosGanados = this.cantidadDeJuegosGanados.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarCantidadDeJuegosPerdidos() {
        this.cantidadDeJuegosPerdidos = this.cantidadDeJuegosPerdidos.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarCantidadDeJuegosDesdeElEncendido() {
        this.cantidadDeJuegosDesdeElEncendido = this.cantidadDeJuegosDesdeElEncendido.add(BigInteger.ONE);
        return true;
    }

    @Override
    public boolean incrementarCantidadDeJuegosDesdeQueSeCerroLaPuertaPrincipal() {
        this.cantidadDeJuegosDesdeQueSeCerroLaPuertaPrincipal = this.cantidadDeJuegosDesdeQueSeCerroLaPuertaPrincipal.add(BigInteger.ONE);
        return true;
    }

    @Override
    public void colocarCodigoDePais(Pais pais) {
        this.pais = pais;
    }

    @Override
    public Pais codigoDePais() {
        return this.pais;
    }

    @Override
    public boolean entrarEnModoMantenimiento() {
        this.modoMantenimiento = true;
        return true;
    }

    @Override
    public boolean salirDeModoMantenimiento() {
        this.modoMantenimiento = false;
        return true;
    }

    @Override
    public boolean obtenerModoMantenimiento() {
        return this.modoMantenimiento;
    }

    @Override
    public boolean deshabilitarSonido() {
        this.sonidos = false;
        return true;
    }

    @Override
    public boolean habilitarSonido() {
        this.sonidos = true;
        return true;
    }

    @Override
    public boolean sonidoHabilitado() {
        return this.sonidos;
    }

    @Override
    public int cantidadDeJuegosDesdeElEncendido() {
        return this.cantidadDeJuegosDesdeElEncendido.intValue();
    }

    @Override
    public int cantidadDeJuegosDesdeQueSeCerroLaPuertaPrincipal() {
        return this.cantidadDeJuegosDesdeQueSeCerroLaPuertaPrincipal.intValue();
    }

    @Override
    public boolean bloquearMaquina() {
        this.maquinaBloqueada = true;
        return true;
    }

    @Override
    public boolean desbloquearmaquina() {
        this.maquinaBloqueada = false;
        return true;
    }

    @Override
    public boolean apagarMaquina() {
        this.maquinaEncendida = false;
        return true;
    }

    @Override
    public void colocarPorcentajeDeRetribucion(int porcentaje) {
        this.porcentajeDeRetribucion = porcentaje;
    }

    @Override
    public int porcentajeDeRetribucion() {
        return this.porcentajeDeRetribucion;
    }

    @Override
    public void colocarNumeroDeSerieDeLaMaquina(String serial) {
        this.numeroDeSerie = serial;
    }

    @Override
    public String numeroDeSerieDeLaMaquina() {
        return this.numeroDeSerie;
    }
}
