/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bv20.core;

import com.core.bv20.model.Controlador;
import com.core.bv20.model.Estado;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author gonza
 * Github: github.com/yogonza524
 * Web: http://idsoft.com.ar
 * mail: yogonza524@gmail.com
 */
public class BV20 {

    private SerialPort puerto;
    private final Controlador controlador;
    private final Oyente oyente;
    private Response respuesta;
    /**
     * Constructor por defecto
     * @param controlador Interfaz de control de mensajes desde y hacia el billetero BV20
     */
    public BV20(Controlador controlador) {
        this.controlador = controlador;
        this.oyente = new Oyente();
        this.respuesta = new Response();
    }

    /**
     * Configura el puerto serie, este metodo
     * solo puede invocarse luego de que se haya inicializado el billetero
     * (metodo abrirPuerto) ya que de otra forma se carga la configuracion
     * por defecto. Ver metodo iniciar()
     * @param baudios Cantidad de datos por segundos del puerto
     * @param dataBits Longitud de los paquetes del puerto. 5,7,8
     * @param stopBits Longitud de bits de parada. 1 o 2
     * @param paridad Paridad de verificación de paquetes. ODD, EVEN, MARK, SPACE, NONE
     */
    public void configurarPuerto(int baudios, int dataBits, int stopBits, int paridad){
        if (this.puerto != null) {
            this.puerto.setComPortParameters(baudios, dataBits, stopBits, paridad);
        }
    }
    
    /**
     * Selecciona un puerto RS232 en función del indice en la lista de puertos fisicos
     * Si el puerto esta dentro del rango, entonces el puerto serie se cargara en el objeto interno
     * del BV20
     * @param indice
     * @return TRUE si el puerto se seleccionó correctamente, FALSE en otro caso
     */
    private boolean seleccionarPuerto(int indice){
        if (indice > -1 && indice < SerialPort.getCommPorts().length) {
            this.puerto = SerialPort.getCommPorts()[indice];
            return true;
        }
        return false;
    }
    
    /**
     * Abre el puerto interno del BV20, previamente se debe seleccionar y configurar el puerto
     * @param indice
     * @return TRUE si se pudo concretar la apertura, FALSE en otro caso
     */
    public boolean abrirPuerto(int indice){
        if (seleccionarPuerto(indice) && puerto != null && !puerto.isOpen()) {
            iniciar();
            return puerto.openPort();
        }
        return false;
    }
    
    /**
     * Cierra el puerto interno del BV20, si esta configurado y abierto
     * Previamente elimina todos los oyentes para evitar que existan mas
     * de un oyente.
     * @return TRUE si se pudo cerrar, FALSE en otro caso
     */
    public boolean cerrarPuerto(){
        if (puerto != null && puerto.isOpen()) {
            puerto.removeDataListener();
            return puerto.closePort();
        }
        return false;
    }
    
    /**
     * Metodo de configuracion del billetero
     */
    private void iniciar(){
        if (puerto != null) {
            puerto.setComPortParameters(9600, 8, 1, SerialPort.NO_PARITY);
            puerto.addDataListener(oyente);
        }
    }
    
    /**
     * Envia el comando 182 preguntando el estado del billetero
     */
    public synchronized void estado(){
        if (puerto != null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{(byte)182}, 1);
        }
    }

    public Response getRespuesta() {
        return respuesta;
    }
    
    /**
     * Inhibe el canal dado por "i"
     * @param i numero entero positivo entre 1 y 16
     */
    public synchronized void inhibirCanal(int i){
        if (i > 0 && i < 17 && puerto != null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{(byte)(130 + i)}, 1);
        }
    }
    
    /**
     * Deshinibe el canal dado por "i"
     * @param i numero entero positivo entre 1 y 16
     */
    public synchronized void deshinhibirCanal(int i){
        if (i > 0 && i < 17 && puerto != null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{(byte)(150 + i)}, 1);
        }
    }
    
    /**
     * Habilita el deposito de billetes
     */
    public synchronized void habilitarDeposito(){
        if (puerto!= null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{(byte)170}, 1);
        }
    }
    
    /**
     * Deshabilita el deposito de billetes
     */
    public synchronized void deshabilitarDeposito(){
        if (puerto!= null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{(byte)171}, 1);
        }
    }
    
    /**
     * Acepta el ingreso de billetes al deposito
     */
    public synchronized void aceptarDeposito(){
        if (puerto!= null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{(byte)172}, 1);
        }
    }
    
    /**
     * Rechaza el ingreso de billetes al deposito
     */
    public synchronized void rechazarDeposito(){
        if (puerto!= null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{(byte)173}, 1);
        }
    }
    
    /**
     * TODO: Habilita todo
     */
    public synchronized void habilitarTodo(){
        if (puerto!= null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{(byte)184}, 1);
        }
    }
    
    /**
     * TODO: Deshabilita todo
     */
    public synchronized void deshabilitarTodo(){
        if (puerto!= null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{(byte)185}, 1);
        }
    }
    
    /**
     * Deshabilita el tiempo de espera del deposito del billetero
     */
    public synchronized void deshabilitarTiempoDeEsperaDelDeposito(){
        if (puerto!= null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{(byte)190}, 1);
        }
    }
    
    /**
     * Habilita el tiempo de espera para el deposito
     * del billetero
     */
    public synchronized void habilitarTiempoDeEsperaDelDeposito(){
        if (puerto!= null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{(byte)191}, 1);
        }
    }
    
    /**
     * Consulta el estado del puerto (Abierto o cerrado)
     * @return TRUE si el puerto esta abierto, FALSE en otro caso
     */
    public boolean puertoAbierto(){
        if (puerto != null) {
            return puerto.isOpen();
        }
        return false;
    }
    
    /**
     * Envia un camando simple (byte) al billetero
     * @param comando paquete de longitud 1 del tipo byte
     */
    public void enviarComando(byte comando){
        if (puerto != null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{comando}, 1);
        }
    }
    
    /**
     * Busca los puertos físicos en el Host (PC)
     * @return arreglo de puertos serie disponibles en el Host
     */
    public SerialPort[] puertosDisponibles(){
        return SerialPort.getCommPorts();
    }
    
    /**
     * Solicita al billetero el numero de firmware del mismo
     */
    public void firmware(){
        if (puerto != null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{(byte)192}, 1);
        }
    }
    
    /**
     * Solicita al billetero el juego de datos instalado
     */
    public void dataSet(){
        if (puerto != null && puerto.isOpen()) {
            puerto.writeBytes(new byte[]{(byte)193}, 1);
        }
    }
    
    private class Oyente implements SerialPortDataListener{

        @Override
        public int getListeningEvents() {
            return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
        }

        @Override
        public void serialEvent(SerialPortEvent spe) {
            if(spe.getEventType() == getListeningEvents()){
                try {
                    esperar(puerto.bytesAvailable() > 0 ? puerto.bytesAvailable() : 1000);
                    byte[] datos = new byte[puerto.bytesAvailable()];
                    puerto.readBytes(datos, datos.length);
                    //Reiniciar la respuesta
                    respuesta = new Response();
                    
                    int longitud = datos.length;
                    
                    if (longitud == 1) {
                        //Paquete de longitud 1, ver tabla en manual BV20
                        controlarEventos(datos[0]);
                        return;
                    }
                    if (longitud == 2 && datos[0] == 121) {
                        aceptarBillete(datos[1]);
                        return;
                    }
                    if (longitud == 2 && datos[0] != 121) {
                        controlador.manejadorPorDefectoPaquetesLongitud2(datos);
                        respuesta.values.put("paqueteLongitud2", datos);
                        return;
                    }
                    if (longitud == 4 && (datos[0] & 0xff) == 182) {                    
                        cargarEstado(datos);
                        return;
                    }
                    if (longitud > 0 && datos[0] == (byte)192) {
                        datos = ArrayUtils.removeElement(datos, datos[0]);
                        int number = Integer.valueOf(new String(datos, StandardCharsets.UTF_8));
                        controlador.firmware(number);
                        respuesta.values.put("firmware", number);
                        return;
                    }
                    if (longitud > 0 && datos[0] == (byte)193) {
                        datos = ArrayUtils.removeElement(datos, datos[0]);
                        controlador.dataSet(new String(datos));
                        respuesta.values.put("dataSet", new String(datos));
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(BV20.class.getName()).log(Level.SEVERE, null, ex);
                    Thread.currentThread().interrupt();  // set interrupt flag
                }
            }
        }
        
        private String completarConCeros(String inhibit) {
            StringBuilder sb = new StringBuilder(inhibit);
            sb = sb.reverse();
            if (inhibit.length() < 8) {
                for (int i = inhibit.length(); i < 8; i++) {
                    sb.append("0");
                }
            }
            return sb.reverse().toString();
        }

        private void cargarEstado(byte[] datos) {
            String lowInhibit = completarConCeros(Integer.toBinaryString(datos[2] & 0xff));
            String highInhibit = completarConCeros(Integer.toBinaryString(datos[1] & 0xff));
            int enableScrew = datos[3];

            Estado e = new Estado();

            if (enableScrew == 0) {
                e.setDepositoHabilitado(true);
            }
            else{
                e.setDepositoHabilitado(false);
            }

            String bajo = new StringBuilder(lowInhibit).reverse().toString();
            String alto = new StringBuilder(highInhibit).reverse().toString();

            for (int i = 0; i < 8; i++) {
                e.setCanalInhibido(i, bajo.charAt(i) == '1');
            }

            for (int i = 8; i < 16; i++) {
                e.setCanalInhibido(i, alto.charAt(i - 8) == '1');
            }

            controlador.manejadorEstado(e);
            respuesta.values.put("estado", e);
        }

        private void aceptarBillete(byte canal) {
            controlador.aceptadoEnCanal(canal & 0xff);
            respuesta.values.put("aceptarBillete", canal & 0xff);
        }

        private void controlarEventos(byte dato) {
            if (dato == 120) {
                controlador.validadorOcupado();
                respuesta.values.put("validadorOcupado", true);
                return;
            }
            if (dato == 121) {
                controlador.validadorDisponible();
                respuesta.values.put("validadorDisponible", true);
                return;
            }
            if (dato > (byte)130 && dato < (byte)147) {
                controlarInhibidos(dato);
                return;
            }
            if (dato > (byte)150 && dato < (byte)167) {
                controlarDeshinibidos(dato);
                return;
            }
            switch(dato){
                case 20: controlador.billeteNoReconocido();
                    respuesta.values.put("billeteNoReconocido", true);
                    break;
                case 30: controlador.billeteroFuncionandoLento(); 
                    respuesta.values.put("billeteroFuncionandoLento", true);
                    break;
                case 50: controlador.billeteRechazadoFalso(); 
                    respuesta.values.put("billeteRechazadoFalso", true);
                    break;
                case 60: controlador.billeteroLlenoOatascado(); 
                    respuesta.values.put("billeteroLlenoOatascado", true);
                    break; 
                case 70: controlador.operacionAbortadaDuranteIngreso(); 
                    respuesta.values.put("operacionAbrotadaDuranteIngreso", true);
                    break;
                case 80: controlador.billeteroReiniciado();
                    respuesta.values.put("billeteroReiniciado", true);
                    break;
                case (byte)170: controlador.depositoHabilitado(); 
                    respuesta.values.put("depositoHabilitado", true);
                    break;
                case (byte)171: controlador.depositoDeshabilitado(); 
                    respuesta.values.put("depositoDeshabilitado", true);
                    break;
                case (byte)184: controlador.todosLosCanalesHabilitados(); 
                    respuesta.values.put("todosLosCanalesHabilitados", true);
                    break;
                case (byte)255: controlador.errorDeComando(); 
                    respuesta.values.put("errorDeComando", true);
                    break;
                default: controlador.manejadorPorDefectoPaquetesLongitud1(dato);
                    respuesta.values.put("paqueteDeLongirud1", dato);
                    break;
                }
        }

        private void esperar(int bytesAvailable) throws InterruptedException {
            Thread.sleep((long)6 * bytesAvailable);
        }

        private void controlarInhibidos(byte dato) {
            if (dato == (byte)131) {
                controlador.canalInhibido(1);
                respuesta.values.put("canalInhibido", 1);
                return;
            }
            if (dato == (byte)132) {
                controlador.canalInhibido(2);
                respuesta.values.put("canalInhibido", 2);
                return;
            }
            if (dato == (byte)133) {
                controlador.canalInhibido(3);
                respuesta.values.put("canalInhibido", 3);
                return;
            }
            if (dato == (byte)134) {
                controlador.canalInhibido(4);
                respuesta.values.put("canalInhibido", 4);
                return;
            }
            if (dato == (byte)135) {
                controlador.canalInhibido(5);
                respuesta.values.put("canalInhibido", 5);
                return;
            }
            if (dato == (byte)136) {
                controlador.canalInhibido(6);
                respuesta.values.put("canalInhibido", 6);
                return;
            }
            switch(dato){
                case (byte)137: controlador.canalInhibido(7);
                    respuesta.values.put("canalInhibido", 6);
                    break;
                case (byte)138: controlador.canalInhibido(8);
                    respuesta.values.put("canalInhibido", 8);
                    break;
                case (byte)139: controlador.canalInhibido(9);
                    respuesta.values.put("canalInhibido", 9);
                    break;
                case (byte)140: controlador.canalInhibido(10);
                    respuesta.values.put("canalInhibido", 10);
                    break;
                case (byte)141: controlador.canalInhibido(11);
                    respuesta.values.put("canalInhibido", 11);
                    break;
                case (byte)142: controlador.canalInhibido(12);
                    respuesta.values.put("canalInhibido", 12);
                    break;
                case (byte)143: controlador.canalInhibido(13);
                    respuesta.values.put("canalInhibido", 13);
                    break;
                case (byte)144: controlador.canalInhibido(14);
                    respuesta.values.put("canalInhibido", 14);
                    break;
                case (byte)145: controlador.canalInhibido(15);
                    respuesta.values.put("canalInhibido", 15);
                    break;
                default: controlador.canalInhibido(16);
                    respuesta.values.put("canalInhibido", 16);
                    break;
            }
        }

        private void controlarDeshinibidos(byte dato) {
            switch(dato){
                case (byte)151: controlador.canalDesinhibido(1);
                    respuesta.values.put("canalDesinhibido", 1);
                    break;
                case (byte)152: controlador.canalDesinhibido(2);
                    respuesta.values.put("canalDesinhibido", 2);
                    break;
                case (byte)153: controlador.canalDesinhibido(3);
                    respuesta.values.put("canalDesinhibido", 3);
                    break;
                case (byte)154: controlador.canalDesinhibido(4);
                    respuesta.values.put("canalDesinhibido", 4);
                    break;
                case (byte)155: controlador.canalDesinhibido(5);
                    respuesta.values.put("canalDesinhibido", 5);
                    break;
                case (byte)156: controlador.canalDesinhibido(6);
                    respuesta.values.put("canalDesinhibido", 6);
                    break;
                case (byte)157: controlador.canalDesinhibido(7);
                    respuesta.values.put("canalDesinhibido", 7);
                    break;
                case (byte)158: controlador.canalDesinhibido(8);
                    respuesta.values.put("canalDesinhibido", 8);
                    break;
                case (byte)159: controlador.canalDesinhibido(9);
                    respuesta.values.put("canalDesinhibido", 9);
                    break;
                case (byte)160: controlador.canalDesinhibido(10);
                    respuesta.values.put("canalDesinhibido", 10);
                    break;
                case (byte)161: controlador.canalDesinhibido(11);
                    respuesta.values.put("canalDesinhibido", 11);
                    break;
                case (byte)162: controlador.canalDesinhibido(12);   
                    respuesta.values.put("canalDesinhibido", 12);
                    break;
                case (byte)163: controlador.canalDesinhibido(13);
                    respuesta.values.put("canalDesinhibido", 13);
                    break;
                case (byte)164: controlador.canalDesinhibido(14);
                    respuesta.values.put("canalDesinhibido", 14);
                    break;
                case (byte)165: controlador.canalDesinhibido(15);
                    respuesta.values.put("canalDesinhibido", 15);
                    break;
                default: controlador.canalDesinhibido(16);
                    respuesta.values.put("canalDesinhibido", 16);
                    break;
        }
        }
        
    }
    
    public static final class Response{
        
        private Map<String,Object> values;

        private Response() {
            this.values = new HashMap<>();
        }

        public Map<String, Object> getValues() {
            return values;
        }

        public void setValues(Map<String, Object> values) {
            this.values = values;
        }
        
        
    }
}
