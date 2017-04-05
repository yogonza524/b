/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.protocol.b1.servidor;

import com.bv20.core.BV20;
import com.core.bingosimulador.Juego;
import com.google.gson.stream.MalformedJsonException;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.xsocket.MaxReadSizeExceededException;
import org.xsocket.connection.IConnectHandler;
import org.xsocket.connection.IDataHandler;
import org.xsocket.connection.IDisconnectHandler;
import org.xsocket.connection.INonBlockingConnection;
import org.xsocket.connection.IServer;
import org.xsocket.connection.Server;

/**
 *
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public class Servidor {
    
    // <editor-fold defaultstate="collapsed" desc="Atributos">
    private BV20 billetero;
    private final IServer server;
    private final XSocketDataHandler manejador;
    private Juego bingo;
    
    private Map<String,Object> parametros;
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public Servidor(int puerto, Juego bingo) throws IOException{
        parametros = new HashMap<>();
        manejador = new XSocketDataHandler();
        server = new Server(puerto,manejador);
        this.bingo = bingo;
    }
    
    public Servidor(String[] args) throws IOException{
        cargarConfiguracionPorDefecto();
        obtenerParametros(args);
        manejador = new XSocketDataHandler();
        server = new Server((int) parametros.get("puerto"),manejador);
        bingo = new Juego();
    }
    //</editor-fold>

    private void obtenerParametros(String[] args) {
        for(String param : args){
            String[] p = param.split(":");
            
            if (p != null && p.length > 0) {
                
                switch(p[0]){
                    case "-p" : 
                        if (p[1].matches("")) {
                            parametros.put("puerto", p[1]);
                        }
                        else{
                            parametros.put("puerto", 8890);
                        }
                        break;
                }
                
            }
        }
    }
    
    public void iniciar() throws IOException, InterruptedException{
        server.start();
        
        Thread.currentThread().join(); //Bucle infinito
    }

    private void cargarConfiguracionPorDefecto() {
        parametros = new HashMap<>();
        parametros.put("puerto", 8890);
    }

    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Metodos">

    //</editor-fold>
    
    private class XSocketDataHandler implements IDataHandler, IConnectHandler, IDisconnectHandler{

        private final Set<INonBlockingConnection> SESSIONS = Collections.synchronizedSet(new HashSet<INonBlockingConnection>());
        
        @Override
        public boolean onData(INonBlockingConnection nbc) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
            try {
            String data = nbc.readStringByDelimiter("\0"); //Lectura
            Paquete p = Paquete.deJSON(data); //Decodificación del paquete
            
            Paquete response = null;
            
            switch(p.getCodigo()){
                case 50 : response = this.jugar(); break;
                default: response = noImplementadoAun();
            }
            
            System.out.println("Paquete recibido");
            System.out.println(p.aJSON());
            
            enviar(response.aJSON());
            
        } catch(Exception ex){
            this.tratarDeEnviarExcepcion(ex);
        }
        
        return true;
        }

        @Override
        public boolean onConnect(INonBlockingConnection inbc) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
            
            return true;
        }

        @Override
        public boolean onDisconnect(INonBlockingConnection inbc) throws IOException {
            
            return true;
        }
        
        //Metodos especificos
        /**
         * Intenta enviar información sobre una excepción ocurrida 
         * en la lectura de datos del puerto, si no se puede
         * muestra dichos datos en el Logger
         * @param ex Excepción a enviar al puerto
         */
        private Paquete tratarDeEnviarExcepcion(Exception ex) {
            System.out.println("Excepcion: " + ex);
            try {
                Paquete respuesta = new Paquete.PaqueteBuilder()
                        .codigo(401)
                        .estado("error")
                        .dato("mensaje", getExcepcion(ex))
                        .dato("excepcion", ExceptionUtils.getMessage(ex))
                        .crear();
                enviar(respuesta.aJSON());
                return respuesta;
//                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex1) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex1);
            }
            return null;
        }
        
        /**
        * Obtiene el Mensaje de excepción para enviar en el paquete de error
        * el mismo contempla el tipo de excepción
        * @param ex Excepción lanzada al leer los datos
        * @return Cadena de carcteres, indica el mensaje a leer
        */
       private String getExcepcion(Exception ex) {
           String result = "";
           if (ex instanceof MalformedJsonException) {
               result = "Mensaje con formato incorrecto";
           }
           if (ex instanceof BufferUnderflowException) {
               result = "Delimitador no encontrado";
           }
           return result;
       }
       
       /**
        * Envia un mensaje al puerto, el mensaje corresponde
        * al formato JSON estandar
        * 
        * @param mensaje cadena de caracteres en formato JSON
        * @throws IOException Excepción de E/S al escribir en el puerto
        */
       public void enviar(String mensaje) throws IOException{
           synchronized(SESSIONS)
           {
               Iterator<INonBlockingConnection> iter = SESSIONS.iterator();

               while(iter.hasNext())
               {
                   INonBlockingConnection nbConn = (INonBlockingConnection) iter.next();

                   if(nbConn.isOpen())
                       nbConn.write(mensaje+"\0");
               }
           }
       }
       
       /**
         * Envia un mensaje de error al puerto, se dispara
         * unica y exclusivamente cuando el servidor recibe
         * una petición de ejecución de un método no soportado
         * 
         * @throws IOException Excepción de E/S al escribir en el puerto
         */
        private Paquete noImplementadoAun() throws IOException {
            Paquete respuesta = new Paquete.PaqueteBuilder()
                            .codigo(402)
                            .estado("error")
                            .dato("descripcion", "Mensaje no implementado aun")
                            .crear();
            enviar(respuesta.aJSON());
            return respuesta;
        }

        private Paquete jugar() throws IOException {
            bingo.jugar(true);
            
            Paquete p = new Paquete.PaqueteBuilder()
                    .codigo(50)
                    .estado("ok")
                    .dato("premios", bingo.getPremiosPagados())
                    .dato("cartones", bingo.getCartones())
                    .crear();
            
            enviar(p.aJSON());
            
            System.out.println(p.aJSON());
            
            return p;
        }
        
    }
}
