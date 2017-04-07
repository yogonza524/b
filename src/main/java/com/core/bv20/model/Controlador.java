/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.core.bv20.model;

/**
 *
 * @author gonza
 */
public interface Controlador {
    
    /**
     * Inhibe el canal indicado en "numeroDeCanal"
     * Metodo de respuesta, al lanzar este metodo el canal
     * con numero "numeroDeCanal" se halla inhibido
     * @param numeroDeCanal entero positivo entre 1 y 16
     */
    public void canalInhibido(int numeroDeCanal);

    /**
     * Desinhibe el canal indicado por "numeroDeCanal" 
     * Al lanzar este metodo ya se haya deshinibido el canal
     * con numero "numeroDeCanal"
     * @param numeroDeCanal entero positivo entre 1 y 16
     */
    public void canalDesinhibido(int numeroDeCanal);

    /**
     * Metodo respuesta al evento de aceptación correcta
     * en el canal "numeroDeCanal"
     * @param numeroDeCanal entero positivo entre 1 y 16
     */
    public void aceptadoEnCanal(int numeroDeCanal);
    
    /**
     * Metodo de respuesta ante el evento de rechazo de un billete
     */
    public void billeteNoReconocido();

    /**
     * Metodo de respuesta ante el evento de funcionamiento lento
     */
    public void billeteroFuncionandoLento();
    
    /**
     * Metodo de respuesta ante el evento de validador ocupado
     */
    public void validadorOcupado();

    /**
     * Metodo de respuesta ante el evento de validador disponible
     */
    public void validadorDisponible();

    /**
     * Metodo de respuesta ante el evento de billete rechazado
     * por ser falso
     */
    public void billeteRechazadoFalso();

    /**
     * Metodo de respuesta ante el evento de billetero
     * lleno o billete atascado
     */
    public void billeteroLlenoOatascado();

    /**
     * Metodo de respuesta ante el evento de operacion
     * abortada durante el ingreso del billete
     */
    public void operacionAbortadaDuranteIngreso();

    /**
     * Metodo de respuesta ante el evento de reinicio 
     * del billetero
     */
    public void billeteroReiniciado();

    /**
     * Metodo de respuesta ante el evento
     * habilitar deposito, al ejecutar este metodo
     * el deposito se encuentra habilitado
     */
    public void depositoHabilitado();

    /**
     * Metodo de respuesta ante el evento de deshabilitacion
     * de ingresos de billetes
     */
    public void ingresoDeshabilitado();

    /**
     * Metodo de respuesta ante el evento de deshabilitacion
     * del deposito
     */
    public void depositoDeshabilitado();

    /**
     * Metodo de respuesta ante el evento de deshabilitacion
     * de todos los canales
     */
    public void todosLosCanalesHabilitados();

    /**
     * Metodo de respuesta ante un comando enviado al 
     * billetero no reconocido por el mismo
     */
    public void errorDeComando();

    /**
     * Metodo de respuesta para manejar paquetes de longitud
     * 2 enviados por el billetero
     * @param datos arreglo de bytes, paquete enviado por el billetero
     */
    public void manejadorPorDefectoPaquetesLongitud2(byte[] datos);

    /**
     * Metodo de respuesta enviado por el billetero 
     * con un paquete de longitud 1
     * @param dato packete enviado por el billetero, longitud 1
     */
    public void manejadorPorDefectoPaquetesLongitud1(byte dato);

    /**
     * Metodo de respuesta ante una consulta del estado del billetero
     * al lanzarse este metodo se hallan cargados los datos del estado
     * del deposito, canales (bajo y alto)
     * @param e Estado del billetero
     */
    public void manejadorEstado(Estado e);
    
    /**
     * Obtiene el numero de firmware actual del billetero
     * @param number numero entero que especifica la version del firmware
     */
    public void firmware(int number);
    
    /**
     * Conjunto de datos de la version de firmware del billetero
     * @param dataset conjunto de caracteres que indica el juego de datos del firmware
     */
    public void dataSet(String dataset);
}
