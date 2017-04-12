/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.protocol.b1.servidor;

import com.bingo.enumeraciones.Denominacion;
import com.bv20.core.BV20;
import com.core.bingosimulador.Juego;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.MalformedJsonException;
import com.protocol.b1.configuracion.ConfiguracionMain;
import com.protocol.b1.enumeraciones.Idioma;
import com.protocol.dao.Conexion;
import java.io.IOException;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.lwjgl.LWJGLException;
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
        
        configurarJuego(bingo);
    }
    //</editor-fold>
    
    private void obtenerParametros(String[] args) {
        for(String param : args){
            String[] p = param.split(":");
            
            if (p != null && p.length > 0) {
                switch(p[0]){
                    case "-p" : 
                        if (p[1].matches("\\d+")) {
                            parametros.put("puerto", Integer.valueOf(p[1]));
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

    private void configurarJuego(Juego bingo) {
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT * FROM juego");
            
            if (query != null && !query.isEmpty()) {
                int creditos = Integer.valueOf(query.get(0).get("creditos").toString());
                int apuestaTotal = Integer.valueOf(query.get(0).get("apuesta_total").toString());
//                int apuestaEnBolasExtra = Integer.valueOf(query.get(0).get("apuesta_en_bolas_extra").toString());
                boolean carton1Habilitado = Boolean.valueOf(query.get(0).get("carton1_habilitado").toString());
                boolean carton2Habilitado = Boolean.valueOf(query.get(0).get("carton2_habilitado").toString());
                boolean carton3Habilitado = Boolean.valueOf(query.get(0).get("carton3_habilitado").toString());
                boolean carton4Habilitado = Boolean.valueOf(query.get(0).get("carton4_habilitado").toString());
                
                boolean[] habilitados = new boolean[]{carton1Habilitado,carton2Habilitado,carton3Habilitado,carton4Habilitado};
                
                bingo.setCreditos(creditos);
                bingo.setCartonesHabilitados(habilitados);
                
                //el metodo apostar depende de los cartones habilitados
                bingo.apostar(apuestaTotal);
            }
            
            System.out.println("Configuracion inicial");
            System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(bingo));
            System.out.println();
            
        } catch (SQLException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
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
                case 1: response = this.idMaquina(); break;
                case 3: response = this.idiomasSoportados(); break;
                case 4: response = this.idiomaActual(); break;
                case 6: response = this.cartonesActuales(); break;
                case 7: response = this.bolas(); break;
                case 10: response = this.configuracionJuego(); break;
                case 11: response = this.bolasVisibles(); break;
                case 14: response = this.aumentarApuesta(); break;
                case 15: response = this.disminuirApuesta(); break;
                case 16: response = this.cobrar(); break;
                case 17: response = this.cambiarCartones(); break;
                case 18: response = this.habilitarCarton(p); break;
                case 21: response = this.deshabilitarCarton(p); break;
                case 22: response = this.denominacionActual(p); break;
                case 24: response = this.generarBolillero(); break;
                case 25: response = this.colocarCreditos(p); break;
                case 50 : response = this.jugar(); break;
                default: response = noImplementadoAun(p);
            }
            
            System.out.println("Paquete recibido");
            System.out.println(p.aJSON());
            
            System.out.println("Paquete enviado");
            System.out.println(response.aJSON());
            
            enviar(response.aJSON());
            
        } catch(Exception ex){
            this.tratarDeEnviarExcepcion(ex);
        }
        
        return true;
        }

        @Override
        public boolean onConnect(INonBlockingConnection inbc) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
            boolean added = false;
        try {
            synchronized(SESSIONS){
                added = SESSIONS.add(inbc);
            }
            } catch (BufferUnderflowException e) {
                Logger.getLogger(XSocketDataHandler.class.getName()).log(Level.SEVERE, null, e);
            }
        return added;  
        }

        @Override
        public boolean onDisconnect(INonBlockingConnection inbc) throws IOException {
            System.out.println("Desconectado");
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
        private Paquete noImplementadoAun(Paquete p) throws IOException {
            Paquete respuesta = new Paquete.PaqueteBuilder()
                            .codigo(p.getCodigo())
                            .estado("error")
                            .dato("descripcion", "Mensaje no implementado aun")
                            .crear();
            enviar(respuesta.aJSON());
            return respuesta;
        }

        private Paquete jugar() throws IOException {
            
            try {
                Conexion.getInstancia().actualizar("UPDATE juego SET comenzo = '" + getCurrentTimeStamp() + "'");
            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            bingo.jugar(true);
            
            System.out.println("Ganado: " + bingo.ganancias());
            System.out.println("Apostado: " + ArrayUtils.toString(bingo.apuestas()));
            System.out.println("Premios");
            for (int i = 0; i < 4; i++) {
                System.out.println(ArrayUtils.toString(bingo.getPremiosPagados()[i]));
            }
            
            try {
                Conexion.getInstancia().actualizar("UPDATE juego SET creditos = " + bingo.getCreditos());
                Conexion.getInstancia().actualizar("UPDATE juego SET ganado = " + bingo.ganancias());
                Conexion.getInstancia().actualizar("UPDATE juego SET termino = '" + getCurrentTimeStamp().toString() + "'");
            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Paquete p = new Paquete.PaqueteBuilder()
                    .codigo(50)
                    .estado("ok")
                    .dato("bolasVisibles", bingo.getBolasVisibles())
                    .crear();
            
            enviar(p.aJSON());
            
            System.out.println(p.aJSON());
            return p;
        }
        
        private java.sql.Timestamp getCurrentTimeStamp() {

            java.util.Date today = new java.util.Date();
            return new java.sql.Timestamp(today.getTime());

        }

        private Paquete idMaquina() throws IOException, SQLException {
            //Buscar el ID en la Base de datos
            List<HashMap<String,Object>> result = Conexion.getInstancia().consultar("select numero_maquina from configuracion");
            
            int id = 0;
            if (result != null && !result.isEmpty()) {
                id = (int)result.get(0).get("numero_maquina");
            }
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(1)
                    .estado("ok")
                    .dato("ID", id)
                    .crear();
            
//            enviar(response.aJSON());
            return response;
        }

        private Paquete idiomasSoportados() throws SQLException {
            //Buscar los idiomas soportados
            List<HashMap<String,Object>> result = Conexion.getInstancia().consultar("SELECT * FROM idiomas");
            
            Map<String,String> idiomas = new HashMap<>();
            if (result != null && !result.isEmpty()) {
                for (int i = 0; i < result.size(); i++) {
                    idiomas.put(result.get(i).get("numero").toString(), result.get(i).get("nombre").toString());
                }
            }
            
            //Crear el paquete
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(3)
                    .estado("ok")
                    .dato("desc", "Idiomas soportados")
                    .dato("idiomas", idiomas)
                    .crear();
            //Enviar el paquete
            
            //Retornar
            return response;
        }

        private Paquete idiomaActual() throws SQLException {
            //Buscar los idiomas soportados
            List<HashMap<String,Object>> result = Conexion.getInstancia().consultar("SELECT idioma FROM configuracion");
            
            String idioma = "";
            if (result != null && !result.isEmpty()) {
                idioma = result.get(0).get("idioma").toString();
            }
            
            //Crear el paquete
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(4)
                    .estado("ok")
                    .dato("desc", "Idioma actual")
                    .dato("idioma", idioma)
                    .crear();
            //Enviar el paquete
            
            //Retornar
            return response;
        }

        private Paquete habilitarCarton(Paquete p) throws IOException {
            if (p != null && p.getDatos() != null 
                    && !p.getDatos().isEmpty() 
                    && p.getDatos().get("numero") != null) {
                
                int numero = Integer.valueOf(p.getDatos().get("numero").toString());
                
                
                
                boolean success = bingo.habilitar(numero - 1);
                
                if (success) {
                    try {
                        Conexion.getInstancia().actualizar("UPDATE juego SET carton" + numero + "_habilitado = true, apuesta_total = " + bingo.apuestaTotal());
                    } catch (SQLException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(18)
                    .estado("ok")
                    .dato("desc", success ? "Habilitar carton " + p.getDatos().get("numero") + " habilitado" : "No se pudo habilitar")
                    .dato("numero",Integer.valueOf(p.getDatos().get("numero").toString()))
                    .dato("apuestaTotal", bingo.apuestaTotal())
                    .dato("apuestaIndividual", bingo.apuestaIndividual())
                    .crear();

                return response;
            }
            return null;
        }

        private Paquete configuracionJuego() throws IOException, SQLException {
            
            boolean carton1_habilitado = true;
            boolean carton2_habilitado = false;
            boolean carton3_habilitado = false;
            boolean carton4_habilitado = false;
            
            //Obtener la informacion del juego
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT * FROM juego LIMIT 1");
            if (query != null && !query.isEmpty()) {
                int cred = Integer.valueOf(query.get(0).get("creditos").toString());
                int apuesta = Integer.valueOf(query.get(0).get("apuesta_total").toString());
                
                
                carton1_habilitado = Boolean.valueOf(query.get(0).get("carton1_habilitado").toString());
                carton2_habilitado = Boolean.valueOf(query.get(0).get("carton2_habilitado").toString());
                carton3_habilitado = Boolean.valueOf(query.get(0).get("carton3_habilitado").toString());
                carton4_habilitado = Boolean.valueOf(query.get(0).get("carton4_habilitado").toString());
                
                boolean[] habilitados = new boolean[]{carton1_habilitado,carton2_habilitado,carton3_habilitado,carton4_habilitado};
                
                int cartones_habilitados = 0;
                
                for(boolean val : habilitados){
                    cartones_habilitados += val? 1 : 0;
                }
                
                bingo.setCreditos(cred);
                int[] apuestas = new int[4];
                for (int i = 0; i < habilitados.length; i++) {
                    System.out.println("Carton " + (i+1) + ": " + habilitados[i]);
                    if (habilitados[i]) {
                        apuestas[i] = apuesta / cartones_habilitados;
                        System.out.println("Carton " + (i+1) + " apuesta: " + apuestas[i]);
                    }
                }
                bingo.setApostado(apuestas);
                
                bingo.setCartonesHabilitados(habilitados);
            }
            
            int[][][] cartones = bingo.getCartones();
            
            List<Map<String,Object>> c = new ArrayList<>();
            Map<String,Map<String,Object>> data = new HashMap<>();
            
            for (int i = 0; i < cartones.length; i++) {
                Integer[][] matriz = new Integer[3][5];
                Map<String,Object> cartonCasilla = new HashMap<>();
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 5; k++) {
                        matriz[j][k] = cartones[i][j][k];
                    }
                }
                cartonCasilla.put("casillas", matriz);
                c.add(cartonCasilla);
            }
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(10)
                    .estado("ok")
                    .dato("credito", bingo.getCreditos())
                    .dato("apostado", bingo.apuestaTotal())
                    .dato("apuestaIndividual", bingo.apuestaIndividual())
                    .dato("carton1_habilitado", carton1_habilitado)
                    .dato("carton2_habilitado", carton2_habilitado)
                    .dato("carton3_habilitado", carton3_habilitado)
                    .dato("carton4_habilitado", carton4_habilitado)
                    .dato("cartones", c)
                    .crear();
            
            return response;
        }

        private Paquete denominacionActual(Paquete p) throws SQLException {
            
            //Verificar si se deben colocar los datos del paquete
            if (p != null && p.getDatos() != null && p.getDatos().get("codigo") != null) {
                //Solicitud de colocacion de denominacion
                int codigo = Integer.valueOf(p.getDatos().get("codigo").toString());
                
                Denominacion actual = bingo.getDenominacion();
                
                switch(codigo){
                    case 10: 
                        bingo.setDenominacion(Denominacion.DIEZ_CENTAVOS);
                        if (actual.equals(Denominacion.CINCO_CENTAVOS)) {
                            //Cambiar de cinco a diez centavos
                            Conexion.getInstancia().actualizar("UPDATE juego SET denominacion_actual = 'DIEZ_CENTAVOS', denominacion_factor = 10, creditos = creditos / 2");
                        }
                        ; break; //10 centavos
                    case 20: ;
                        bingo.setDenominacion(Denominacion.CINCO_CENTAVOS);
                        if (actual.equals(Denominacion.DIEZ_CENTAVOS)) {
                            Conexion.getInstancia().actualizar("UPDATE juego SET denominacion_actual = 'CINCO_CENTAVOS', denominacion_factor = 20, creditos = creditos * 2");
                        }
                        ;break; //5 centavos
                }
            }
            
            //Obtener los datos
            List<HashMap<String,Object>> result = Conexion.getInstancia()
                    .consultar("SELECT juego.denominacion_actual, denominacion.valor FROM juego, denominacion WHERE denominacion.nombre = denominacion_actual");
            
            double valor = 0.0;
            String nombre = "";
            
            if (result != null && !result.isEmpty()) {
                valor = (double) result.get(0).get("valor");
                nombre = (String) result.get(0).get("denominacion_actual");
            }
            
            int creditos = Integer.valueOf(Conexion.getInstancia().consultar("SELECT creditos FROM juego LIMIT 1").get(0).get("creditos").toString());
            
            bingo.setCreditos(creditos);
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(22)
                    .estado("ok")
                    .dato("desc", "Denominacion actual: " + nombre)
                    .dato("valor", valor)
                    .dato("creditos", creditos)
                    .crear();
            
            return response;
        }

        private Paquete colocarCreditos(Paquete p) throws SQLException {
            
            if (p != null && p.getDatos() != null && p.getDatos().get("credito") != null) {
                
                int creditos = Integer.valueOf(p.getDatos().get("credito").toString());
                bingo.agregarCreditos(creditos);
                Conexion.getInstancia().actualizar("UPDATE juego SET creditos = creditos + " + creditos +
                        ", dinero = dinero + (creditos / denominacion_factor)");
                
                Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(25)
                    .estado("ok")
                    .dato("creditos", creditos)
                    .crear();
                
                return response;
            }
            
            return null;
        }

        private Paquete bolasVisibles() {
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(11)
                    .estado("ok")
                    .dato("bolasVisibles", bingo.getBolasVisibles())
                    .crear();
            
            return response;
        }

        private Paquete aumentarApuesta() {
            bingo.aumentarApuestas();
            
            try {
                Conexion.getInstancia().actualizar("UPDATE juego set apuesta_total = " + bingo.apuestaTotal());
            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(14)
                    .estado("ok")
                    .dato("apuestaTotal", bingo.apuestaTotal())
                    .dato("apuestaIndividual", bingo.apuestaIndividual())
                    .dato("desc", "Apuesta aumentada a "+ bingo.apuestaTotal())
                    .crear();
            
            return response;
        }

        private Paquete disminuirApuesta() {
            bingo.disminuirApuestas();
            
            try {
                //Persistir en la base de datos
                Conexion.getInstancia().actualizar("UPDATE juego SET apuesta_total = " + bingo.apuestaTotal());
            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(15)
                    .estado("ok")
                    .dato("apuestaTotal", bingo.apuestaTotal())
                    .dato("desc", "Apuesta disminuida a "+ bingo.apuestaTotal())
                    .crear();
            
            return response;
        }

        private Paquete cobrar() throws IOException {
            Paquete respuesta = new Paquete.PaqueteBuilder()
                        .codigo(16)
                        .estado("ok")
                        .dato("desc", "Pago manual activado, llamando al krupier")
                        .dato("credito", bingo.getCreditos())
                        .dato("pagar", bingo.getCreditos())
                        .crear();
            
            //Colocar los creditos en 0
            bingo.setCreditos(0);
            
            try {
                Conexion.getInstancia().actualizar("UPDATE juego set creditos = 0");
            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            return respuesta;
        }

        private Paquete cambiarCartones() {
            bingo.cambiarCartones();
            
            int[][][] cartones = bingo.getCartones();
            
            List<Map<String,Object>> c = new ArrayList<>();
            
            for (int i = 0; i < cartones.length; i++) {
                Integer[][] matriz = new Integer[3][5];
                Map<String,Object> cartonCasilla = new HashMap<>();
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 5; k++) {
                        matriz[j][k] = cartones[i][j][k];
                    }
                }
                cartonCasilla.put("casillas", matriz);
                c.add(cartonCasilla);
            }
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(17)
                    .estado("ok")
                    .dato("cartones", c)
                    .crear();
            
            return response;
        }

        private Paquete bolas() {
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(7)
                    .estado("ok")
                    .dato("bolas", ArrayUtils.addAll(bingo.getBolasVisibles(), bingo.getBolasExtra()))
                    .crear();
            
            return response;
        }

        private Paquete cartonesActuales() {
            int[][][] cartones = bingo.getCartones();
            
            List<Map<String,Object>> c = new ArrayList<>();
            
            for (int i = 0; i < cartones.length; i++) {
                Integer[][] matriz = new Integer[3][5];
                Map<String,Object> cartonCasilla = new HashMap<>();
                for (int j = 0; j < 3; j++) {
                    for (int k = 0; k < 5; k++) {
                        matriz[j][k] = cartones[i][j][k];
                    }
                }
                cartonCasilla.put("casillas", matriz);
                c.add(cartonCasilla);
            }
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(6)
                    .estado("ok")
                    .dato("cartones", c)
                    .crear();
            
            return response;
        }

        private Paquete generarBolillero() {
            bingo.generarBolillero();
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(24)
                    .estado("ok")
                    .dato("bolasVisibles", bingo.getBolasVisibles())
                    .dato("bolasExtra", bingo.getBolasExtra())
                    .dato("desc", "Bolillero nuevo generado")
                    .crear();
            
            return response;
        }

        private Paquete deshabilitarCarton(Paquete p) {
            
            if (p != null && p.getDatos() != null && p.getDatos().get("numero") != null) {
                
                int numero = Integer.valueOf(p.getDatos().get("numero").toString());
                
                boolean success = false;
                if (numero > 0 && numero < Juego.getCantidadDeCartones() + 1) {
                    success = bingo.deshabilitar(numero - 1);
                    System.out.println(bingo.habilitados());
                    System.out.println(ArrayUtils.toString(bingo.apuestas()));
                }
                
                try {
                    Conexion.getInstancia().actualizar("UPDATE juego set carton" + numero + "_habilitado = false, apuesta_total = " + bingo.apuestaTotal());
                } catch (SQLException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                Paquete response = null;
                if (success) {
                    response = new Paquete.PaqueteBuilder()
                    .codigo(21)
                    .estado("ok")
                    .dato("desc", "Carton " + p.getDatos().get("numero") + " deshabilitado")
                    .dato("numero", Integer.valueOf(p.getDatos().get("numero").toString()))
                    .dato("apuestaTotal", bingo.apuestaTotal())
                    .dato("apuestaIndividual", bingo.apuestaIndividual())
                    .crear();
                }
                else{
                    response = new Paquete.PaqueteBuilder()
                    .codigo(21)
                    .estado("fail")
                    .dato("desc", "Carton " + p.getDatos().get("numero") + " No se pudo deshabilitar")
                    .crear();
                }
            
                return response;
            }
            
            return faltanParametros(p);
        }

        private Paquete faltanParametros(Paquete p) {
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(p == null? 404 : p.getCodigo())
                    .estado("error")
                    .dato("Faltan parametros para la respuesta", p != null? p.aJSON() : "No se recibió ningun paquete")
                    .crear();
            
            return response;
        }
        
    }
}
