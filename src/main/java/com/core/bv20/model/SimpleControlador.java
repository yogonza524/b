/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.core.bv20.model;

/**
 *
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public class SimpleControlador implements Controlador{
    
    // <editor-fold defaultstate="collapsed" desc="Atributos">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Metodos">

    //</editor-fold>
    
    @Override
     public void canalInhibido(int numeroDeCanal) {
         System.out.println("Canal " + numeroDeCanal + " inhibido");
     }

     @Override
     public void canalDesinhibido(int numeroDeCanal) {
         System.out.println("Canal " + numeroDeCanal + " desinhibido");
     }

     @Override
     public void aceptadoEnCanal(int numeroDeCanal) {
         System.out.println("Billete aceptado en canal " + numeroDeCanal);
     }

     @Override
     public void billeteNoReconocido() {
         System.out.println("Billete no reconocido");
     }

     @Override
     public void billeteroFuncionandoLento() {
         System.out.println("Billetero funcionando lento");
     }

     @Override
     public void validadorOcupado() {
         System.out.println("Validando...");
     }

     @Override
     public void validadorDisponible() {
         System.out.println("Validado correctamente");
     }

     @Override
     public void billeteRechazadoFalso() {
         System.out.println("Billete rechazado, falso");
     }

     @Override
     public void billeteroLlenoOatascado() {
         System.out.println("Billetero lleno o atascado");
     }

     @Override
     public void operacionAbortadaDuranteIngreso() {
         System.out.println("Operacion abortada, reiniciando...");
     }

     @Override
     public void billeteroReiniciado() {
         System.out.println("Billetero reiniciado");
     }

     @Override
     public void depositoHabilitado() {
         System.out.println("Deposito habilitado");
     }

     @Override
     public void ingresoDeshabilitado() {
         System.out.println("Ingreso deshabilitado");
     }

     @Override
     public void depositoDeshabilitado() {
         System.out.println("Deposito deshabilitado");
     }

     @Override
     public void todosLosCanalesHabilitados() {
         System.out.println("Todos los canales habilitados");
     }

     @Override
     public void errorDeComando() {

     }

     @Override
     public void manejadorPorDefectoPaquetesLongitud2(byte[] datos) {

     }

     @Override
     public void manejadorPorDefectoPaquetesLongitud1(byte dato) {

     }

     @Override
     public void manejadorEstado(Estado e) {
         System.out.println("Deposito " + (e.depositoHabilitado() ? "Habilitado" : "Deshabilitado"));
     }

     @Override
     public void firmware(int number) {
         System.out.println("Firmware " + number);
     }

     @Override
     public void dataSet(String dataset) {
         System.out.println("Data set: " + dataset);
     }
}
