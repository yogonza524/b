/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.protocol.b1.interfaces;

import com.protocol.b1.enumeraciones.Idioma;
import com.protocol.b1.enumeraciones.Pais;
import java.util.List;

/**
 * Repositorio de datos del protocolo B1
 * 
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public interface RepositorioB1 {
    
    /**
     * Obtiene el número de identificación de esta máquina de juegos
     * @return numero entero positivo entre 1 y 127
     */
    public int getNumeroDeMaquina();
    
    /**
     * Coloca el número identificatorio de esta máquina de juegos
     * @param numeroDeMaquina numero entero positivo, entre 1 y 127
     */
    public void setNumeroDeMaquina(int numeroDeMaquina);
    
    /**
     * Coloca la lista de idiomas soportados por el juego BingoBot
     * @param idiomas lista de idiomas del juego
     */
    public void setIdiomasSoportados(List<Idioma> idiomas);
    
    /**
     * Obtiene la lista de idiomas soportados por el juego BingoBot
     * @return Lista de idiomas del juego
     */
    public List<Idioma> getIdiomasSoportados();
    
    /**
     * Coloca el idioma actual que utiliza el juego BingoBot
     * @param idioma Idioma seleccionado
     */
    public void setIdiomaActual(Idioma idioma);
    
    /**
     * Obtiene el idioma actual cargado para el juego BingoBot
     * @return idioma actual en uso por el juego
     */
    public Idioma getIdiomaActual();
    
    public boolean incrementarIngresoDeBilletesDe$1();
    
    public boolean incrementarIngresoDeBilletesDe$2();
    
    public boolean incrementarIngresoDeBilletesDe$5();
    
    public boolean incrementarIngresoDeBilletesDe$10();
    
    public boolean incrementarIngresoDeBilletesDe$20();
    
    public boolean incrementarIngresoDeBilletesDe$50();
    
    public boolean incrementarIngresoDeBilletesDe$100();
    
    public boolean incrementarIngresoDeBilletesDe$200();
    
    public boolean incrementarIngresoDeBilletesDe$500();
    
    public boolean incrementarIngresoDeBilletesDe$1000();
    
    public boolean incrementarCantidadDeJuegosJugados();
    
    public boolean incrementarCantidadDeJuegosGanados();
    
    public boolean incrementarCantidadDeJuegosPerdidos();
    
    public boolean incrementarCantidadDeJuegosDesdeElEncendido();
    
    public boolean incrementarCantidadDeJuegosDesdeQueSeCerroLaPuertaPrincipal();
    
    public void colocarCodigoDePais(Pais pais);
    
    public Pais codigoDePais();
    
    public boolean entrarEnModoMantenimiento();
    
    public boolean salirDeModoMantenimiento();
    
    public boolean obtenerModoMantenimiento();
    
    public boolean deshabilitarSonido();
    
    public boolean habilitarSonido();
    
    public boolean sonidoHabilitado();
    
    public int cantidadDeJuegosDesdeElEncendido();
    
    public int cantidadDeJuegosDesdeQueSeCerroLaPuertaPrincipal();
    
    public boolean bloquearMaquina();
    
    public boolean desbloquearmaquina();
    
    public boolean apagarMaquina();
    
    public void colocarPorcentajeDeRetribucion(int porcentaje);
    
    public int porcentajeDeRetribucion();
    
    public void colocarNumeroDeSerieDeLaMaquina(String serial);
    
    public String numeroDeSerieDeLaMaquina();
    
}
