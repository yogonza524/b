/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.protocol.b1.servidor;

import com.b1.batch.ProcessB1;
import com.b1.services.ConfiguracionService;
import com.b1.services.ContadoresService;
import com.b1.services.ContadoresService.Contadores;
import com.b1.services.HistorialB1Service;
import com.b1.services.JuegoService;
import com.b1.services.LogService;
import com.b1.services.UtilidadesService;
import com.bingo.enumeraciones.Denominacion;
import com.bingo.enumeraciones.FaseDeBusqueda;
import com.bingo.enumeraciones.MetodoB1;
import com.bingo.rng.RNG;
import com.bingo.util.Matematica;
import com.bv20.core.BV20;
import com.core.bingosimulador.Juego;
import com.core.bv20.model.Controlador;
import com.core.bv20.model.Estado;
import com.fx.controladores.B1Controller;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.MalformedJsonException;
import com.guava.core.EventBusManager;
import com.protocol.b1.configuracion.ConfiguracionMain;
import com.protocol.b1.enumeraciones.Idioma;
import com.protocol.b1.main.MainApp;
import com.protocol.b1.servidor.Paquete.PaqueteBuilder;
import com.protocol.dao.Conexion;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.BufferUnderflowException;
import java.nio.channels.ClosedChannelException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    private final IServer serverGame;
    private final XSocketDataHandler manejadorGame;
    
    private IServer serverJackpot;
    private XSocketDataHandlerServerJackpot manejadorServerJackpot;
    
    public static boolean B1FX = false;
    
    //Si la maquina no es servidor de Jackpot entonces los siguientes 2(dos)
    //objetos seran cargados para comunicarse con el servidor de Jackpot
    private IServer clientJackpot;
    private XSocketDataHandlerClientJackpot manejadorClientJackpot;
    
    private Juego bingo;
    
    //Atributos auxiliares
    private int puertoBilletero;
    
    private Map<String,Object> parametros;
    
    //Servicios Singleton desde la clase Main
    private ConfiguracionService configService = 
            MainApp.getConfiguracionService();
    
    private HistorialB1Service historialB1service = 
            MainApp.getHistorialB1Service();
    
    private LogService logService = 
            MainApp.getLogService();
    
    private ContadoresService contadoresService = 
            MainApp.getContadoresService();
    
    private JuegoService juegoService = 
            MainApp.getJuegoService();
    
    private UtilidadesService utilidadesService = 
            MainApp.getUtilidadesService();
    
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    public Servidor(int puerto, Juego bingo) throws IOException{
        
        try {
            //Verificar el Servicio de PostgreSQL
            verificarServicioPostgreSQL();
        } catch (InterruptedException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        parametros = new HashMap<>();
        manejadorGame = new XSocketDataHandler();
        
        //Instanciar el correspondiente servidor de jackpot
        if (configService.esServidor()) {
            //Es cliente, debe informar al servidor
            manejadorClientJackpot = new XSocketDataHandlerClientJackpot();
            serverJackpot = new Server(8895, manejadorClientJackpot);
        }
        else{
            //Es servidor, debe informar a los demas clientes
            manejadorServerJackpot = new XSocketDataHandlerServerJackpot();
            clientJackpot = new Server(8895, manejadorServerJackpot);
        }
        
        serverGame = new Server(puerto,manejadorGame);
        
        this.bingo = bingo;
        
        inicializarJackpot();
        
        //Oyente de evento cerrar aplicacion
        Runtime.getRuntime().addShutdownHook(new Thread()
        {
            @Override
            public void run()
            {
                try {
                    manejadorGame.enviar(new Paquete.PaqueteBuilder().codigo(300).estado("ok").dato("desc","Cerrar").crear().aJSON());
                } catch (IOException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
    
    public Servidor(String[] args) throws IOException{
        
//        try {
//            //Verificar el Servicio de PostgreSQL
//            verificarServicioPostgreSQL();
//        } catch (InterruptedException ex) {
//            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        bingo = new Juego();
        configurarJuego(bingo);
        cargarConfiguracionPorDefecto();
        obtenerParametros(args); //Prioridad alta, se ejecuta luego de la lectura de la configuracion
        manejadorGame = new XSocketDataHandler();
        serverGame = new Server((int) parametros.get("puerto"),manejadorGame);
        
        //Inicializar Jackpot
        inicializarJackpot();
        
    }
    //</editor-fold>
    
    private void obtenerParametros(String[] args) {
        if (args != null && args.length > 0) {
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
                        case "-bv20":
                            //Coloco el puerto para abrirlo luego
                            puertoBilletero = Integer.valueOf(p[1]);
                            billetero = crearBilletero();
                            break;
                        case "-servidor":
                            //Colocar el modo servidor de jackpot
                            try {
                                boolean servidor = Integer.valueOf(p[1]) == 1 ? true : false;

                                Conexion.getInstancia().actualizar("UPDATE configuracion SET servidor = " + servidor);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case "-servidorIp":
                            //Coloco el ip del servidor de jackpot

                            try{
                                Conexion.getInstancia().actualizar("UPDATE configuracion SET ip_servidor = " + p[1]);
                            } catch (SQLException ex) {
                                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                                logService.log(ex.getMessage());
                            }

                            break;
                    }

                }
            }
        }
    }
    
    public void iniciar() throws IOException, InterruptedException{
        
        iniciarBilletero();
        
        serverGame.start();
        
        if (configService.esServidor()) {
            clientJackpot.start();
        }
        else{
            serverJackpot.start();
        }
        
        //Lanzar el BingoBot
        if (configService.lanzarVista()) {
            //Comentar el siguiente Hilo si se quiere lanzar el Bingo de manera manual
            //(abrir manualmente el archivo bingo.swf)
            new Thread(() -> {
                //"\"C:\\Documents and Settings\\Gonzalo\\Desktop\\bingoBot\\PantallaPrincipalCorregida\\bingo.swf\""
                String ruta = configService.rutaBingo();

                try {
                    new ProcessB1().run(new String[]{"cmd"}, ruta , "No se pudo iniciar el juego", true);
                } catch (IOException | InterruptedException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    logService.log(ex.getMessage());
                }
            }).start();
        }
        
        if (!B1FX) {
            Thread.currentThread().join(); //Bucle infinito
        }
    }

    private void cargarConfiguracionPorDefecto() {
        parametros = new HashMap<>();
        parametros.put("puerto", 8890);
    }

    private void configurarJuego(Juego bingo) {
        
        //Deshabilitar cartones si no hay suficiente credito, calcular apuestas
        bingo.setModoDeshabilitarPorFaltaDeCredito(true);
        
        //Colocar el umbral 
        bingo.setUtilizarUmbralParaLiberarBolasExtra(configService.utilizarUmbralParaLiberarBolasExtra());
        bingo.setUmbralParaLiberarBolasExtra(configService.umbralParaLiberarBolasExtra());
        
        //Colocar el costo de la bola extra
        bingo.setPorcentajeDelPremioMayorPorSalirParaBolaExtra(configService.costoDeLaBolaExtraEnPorcentajeDelMayorPorSalir() / 100.0);
        
        //Bonus variable
        bingo.setUtilizarPremiosVariablesBonus(true);
        
        //Utilizar 16 items en el bonus
        bingo.setCantidadDePremiosBonusFijo(16);
        bingo.setCantidadDePremiosBonusVariable(16);
        bingo.setCantidadTotalDePremiosEnBonus(4);
        
        //Colocar los premios variables
        Map<Integer,Integer> premiosBonusVariable = new HashMap<>();
        premiosBonusVariable.put(4, 2);
        premiosBonusVariable.put(3, 1);
        premiosBonusVariable.put(1, 3);
        
        bingo.setPremiosVariablesBonus(premiosBonusVariable);
        
        juegoService.inicializarRecaudadoDesdeElEncendido();
        contadoresService.inicializarCantidadDeJuegosDesdeElEncendido();
        configService.setUltimaFechaDeEncendido();
        
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
                
                //Verificar si los creditos son suficientes
                if (creditos < apuestaTotal) {
                    
                    //coloco la apuesta total igual a los creditos disponibles
                    apuestaTotal = creditos;
                    
                    //Persisto la apuesta total en la base de datos
                    juegoService.setApuestaTotal(apuestaTotal);
                }
                
                //Verificar si se puede habilitar dependiendo de la cantidad de creditos
                if (creditos == 0) {
                    Conexion.getInstancia().actualizar("UPDATE juego SET carton2_habilitado = false, carton3_habilitado = false, carton4_habilitado = false, apuesta_total = 1");
                    habilitados[1] = false;
                    habilitados[2] = false;
                    habilitados[3] = false;
                    apuestaTotal = 1;
                }
                
                //La habilitacion siempre se debe realizar antes que la apuesta
                bingo.setCartonesHabilitados(habilitados);
                
                //el metodo apostar depende de los cartones habilitados
                bingo.apostar(apuestaTotal);
                
                //Colocar la denominacion actual
                int denominacion = Double.valueOf(query.get(0).get("denominacion_factor").toString()).intValue();
                switch(denominacion){
                    case 20: 
                        //Cinco centavos
                        bingo.setDenominacion(Denominacion.CINCO_CENTAVOS); break;
                    case 10:
                        //Diez centavos
                        bingo.setDenominacion(Denominacion.DIEZ_CENTAVOS); break;
                }
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            logService.log(ex.getMessage());
        }
    }

    private BV20 crearBilletero() {
        BV20 b = new BV20(new Controlador() {
            @Override
            public void canalInhibido(int numeroDeCanal) {
                System.out.println("Canal inhibido para aceptar billetes: " + numeroDeCanal);
                logService.log("Canal inhibido para aceptar billetes: " + numeroDeCanal);
            }

            @Override
            public void canalDesinhibido(int numeroDeCanal) {
                System.out.println("Canal desinhibido para aceptar billetes: " + numeroDeCanal);
                logService.log("Canal desinhibido para aceptar billetes: " + numeroDeCanal);
            }

            @Override
            public void aceptadoEnCanal(int numeroDeCanal) {
                System.out.println("Aceptado en canal " + numeroDeCanal);
                logService.log("Billete Aceptado en canal " + numeroDeCanal);
                boolean aceptado = false;
                switch(numeroDeCanal){
                    case 0: 
                    {
                        try {
                            //Verificar que la conexion este abierta
                            if (serverGame != null && serverGame.isOpen()) {
                                //Billete de $1 aceptado
                                float creditos = 1 / bingo.getDenominacion().getValue();
                                
                                System.out.println("Billete recibido de $2");
                                
                                //Aumentar los creditos del juego
                                bingo.setCreditos((int) (bingo.getCreditos() + creditos));
                                
                                //Persistir el cambio en la base de datos
                                Conexion.getInstancia().actualizar("UPDATE juego SET creditos = " + bingo.getCreditos());
                                
                                contadoresService.incrementarCantidadDeBilletesDe$1();
                                
                                manejadorGame.enviar(new Paquete.PaqueteBuilder()
                                        .codigo(30)
                                        .estado("ok")
                                        .dato("creditos", bingo.getCreditos())
                                        .crear()
                                        .aJSON()
                                );
                                //Billete aceptado
                                aceptado = true;
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
break;
                    case 1: 
                    {
                        try {
                            //Verificar que la conexion este abierta
                            if (serverGame != null && serverGame.isOpen()) {
                                //Billete de $2 aceptado
                                float creditos = 2 / bingo.getDenominacion().getValue();
                                
                                System.out.println("Billete recibido de $2");
                                
                                //Aumentar los creditos del juego
                                bingo.setCreditos((int) (bingo.getCreditos() + creditos));
                                
                                //Persistir el cambio en la base de datos
                                Conexion.getInstancia().actualizar("UPDATE juego SET creditos = " + bingo.getCreditos());
                                
                                contadoresService.incrementarCantidadDeBilletesDe$2();
                                
                                manejadorGame.enviar(new Paquete.PaqueteBuilder()
                                        .codigo(30)
                                        .estado("ok")
                                        .dato("creditos", bingo.getCreditos())
                                        .crear()
                                        .aJSON()
                                );
                                //Billete aceptado
                                aceptado = true;
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
                    break;
                    case 2: 
                    {
                        try {
                            //Verificar que la conexion este abierta
                            if (serverGame != null && serverGame.isOpen()) {
                                //Billete de $5 aceptado
                                float creditos = 5 / bingo.getDenominacion().getValue();
                                
                                System.out.println("Billete recibido de $5");
                                
                                //Aumentar los creditos del juego
                                bingo.setCreditos((int) (bingo.getCreditos() + creditos));
                                
                                //Persistir el cambio en la base de datos
                                Conexion.getInstancia().actualizar("UPDATE juego SET creditos = " + bingo.getCreditos());
                                
                                contadoresService.incrementarCantidadDeBilletesDe$5();
                                
                                manejadorGame.enviar(new Paquete.PaqueteBuilder()
                                        .codigo(30)
                                        .estado("ok")
                                        .dato("creditos", bingo.getCreditos())
                                        .crear()
                                        .aJSON()
                                );
                                //Billete aceptado
                                aceptado = true;
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
                    break;
                    case 3: 
                    {
                        try {
                            //Verificar que la conexion este abierta
                            if (serverGame != null && serverGame.isOpen()) {
                                //Billete de $10 aceptado
                                float creditos = 10 / bingo.getDenominacion().getValue();
                                
                                System.out.println("Billete recibido de $10");
                                
                                //Aumentar los creditos del juego
                                bingo.setCreditos((int) (bingo.getCreditos() + creditos));
                                
                                //Persistir el cambio en la base de datos
                                Conexion.getInstancia().actualizar("UPDATE juego SET creditos = " + bingo.getCreditos());
                                
                                Conexion.getInstancia().actualizar("UPDATE contadores SET cantidad_de_billetes_de_$10 = cantidad_de_billetes_de_$10 + 1");
                                
                                manejadorGame.enviar(new Paquete.PaqueteBuilder()
                                        .codigo(30)
                                        .estado("ok")
                                        .dato("creditos", bingo.getCreditos())
                                        .crear()
                                        .aJSON()
                                );
                                //Billete aceptado
                                aceptado = true;
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                            logService.log(ex.getMessage());
                        } catch (IOException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                        logService.log(ex.getMessage());
                    }
                    }
                    break;
                    case 4: 
                    {
                        try {
                            //Verificar que la conexion este abierta
                            if (serverGame != null && serverGame.isOpen()) {
                                //Billete de $20 aceptado
                                float creditos = 20 / bingo.getDenominacion().getValue();
                                
                                System.out.println("Billete recibido de $20");
                                
                                //Aumentar los creditos del juego
                                bingo.setCreditos((int) (bingo.getCreditos() + creditos));
                                
                                //Persistir el cambio en la base de datos
                                Conexion.getInstancia().actualizar("UPDATE juego SET creditos = " + bingo.getCreditos());
                                
                                Conexion.getInstancia().actualizar("UPDATE contadores SET cantidad_de_billetes_de_$20 = cantidad_de_billetes_de_$20 + 1");
                                
                                manejadorGame.enviar(new Paquete.PaqueteBuilder()
                                        .codigo(30)
                                        .estado("ok")
                                        .dato("creditos", bingo.getCreditos())
                                        .crear()
                                        .aJSON()
                                );
                                //Billete aceptado
                                aceptado = true;
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
                    break;
                    case 5: 
                    {
                        try {
                            //Verificar que la conexion este abierta
                            if (serverGame != null && serverGame.isOpen()) {
                                //Billete de $50 aceptado
                                float creditos = 50 / bingo.getDenominacion().getValue();
                                
                                System.out.println("Billete recibido de $50");
                                
                                //Aumentar los creditos del juego
                                bingo.setCreditos((int) (bingo.getCreditos() + creditos));
                                
                                //Persistir el cambio en la base de datos
                                Conexion.getInstancia().actualizar("UPDATE juego SET creditos = " + bingo.getCreditos());
                                
                                Conexion.getInstancia().actualizar("UPDATE contadores SET cantidad_de_billetes_de_$50 = cantidad_de_billetes_de_$50 + 1");
                                
                                manejadorGame.enviar(new Paquete.PaqueteBuilder()
                                        .codigo(30)
                                        .estado("ok")
                                        .dato("creditos", bingo.getCreditos())
                                        .crear()
                                        .aJSON()
                                );
                                //Billete aceptado
                                aceptado = true;
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
                    break;
                    case 6: 
                    {
                        try {
                            //Verificar que la conexion este abierta
                            if (serverGame != null && serverGame.isOpen()) {
                                //Billete de $100 aceptado
                                float creditos = 100 / bingo.getDenominacion().getValue();
                                
                                System.out.println("Billete recibido de $100");
                                
                                //Aumentar los creditos del juego
                                bingo.setCreditos((int) (bingo.getCreditos() + creditos));
                                
                                //Persistir el cambio en la base de datos
                                Conexion.getInstancia().actualizar("UPDATE juego SET creditos = " + bingo.getCreditos());
                                
                                Conexion.getInstancia().actualizar("UPDATE contadores SET cantidad_de_billetes_de_$100 = cantidad_de_billetes_de_$100 + 1");
                                
                                manejadorGame.enviar(new Paquete.PaqueteBuilder()
                                        .codigo(30)
                                        .estado("ok")
                                        .dato("creditos", bingo.getCreditos())
                                        .crear()
                                        .aJSON()
                                );
                                //Billete aceptado
                                aceptado = true;
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    }
                    break;
                }
                if (aceptado) {
                    if (billetero != null) {
                        billetero.aceptarDeposito();
                    }
                }
                else{
                    if (billetero != null) {
                        billetero.rechazarDeposito();
                    }
                }
            }

            @Override
            public void billeteNoReconocido() {
                System.out.println("Billete no reconocido");
            }

            @Override
            public void billeteroFuncionandoLento() {
                
            }

            @Override
            public void validadorOcupado() {
                System.out.println("Validando...");
            }

            @Override
            public void validadorDisponible() {
                System.out.println("Billetero disponible");
            }

            @Override
            public void billeteRechazadoFalso() {
                
            }

            @Override
            public void billeteroLlenoOatascado() {
                
            }

            @Override
            public void operacionAbortadaDuranteIngreso() {
                
            }

            @Override
            public void billeteroReiniciado() {
                
            }

            @Override
            public void depositoHabilitado() {
                System.out.println("Habilitado para ingreso de billetes");
            }

            @Override
            public void ingresoDeshabilitado() {
                System.out.println("Deshabilitado para ingresar billetes");
            }

            @Override
            public void depositoDeshabilitado() {
                System.out.println("Deposito deshabilitado");
            }

            @Override
            public void todosLosCanalesHabilitados() {
                
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
                
            }

            @Override
            public void firmware(int number) {
                System.out.println("Firmware: " + number);
                
                try {
                    Conexion.getInstancia().actualizar("UPDATE billetero SET firmware = " + number);
                } catch (SQLException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            @Override
            public void dataSet(String dataset) {
                System.out.println("Dataset: " + dataset);
                
                try {
                    Conexion.getInstancia().actualizar("UPDATE billetero SET dataset = " + dataset);
                } catch (SQLException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        
        return b;
    }

    private void inicializarJackpot() {
        
        try {
            //Verificar si esta maquina es servidor de Jackpot
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT servidor, puerto_jackpot FROM configuracion");
            
            if (query != null && !query.isEmpty() && query.get(0).get("servidor") != null && Boolean.valueOf(query.get(0).get("servidor").toString())) {
                
                //Esta maquina es el servidor de Jackpot, verificar si puedo instanciar
                System.out.println("Maquina configurada como servidor de Jackpot");
                
                if (query.get(0).get("puerto_jackpot") != null) {
                    //Instanciar el cliente de Jackpot
                    manejadorClientJackpot = new XSocketDataHandlerClientJackpot();
                    clientJackpot = new Server(Integer.valueOf(query.get(0).get("puerto_jackpot").toString()), manejadorServerJackpot);
                }
                else{
                    System.err.println("No se ha colocado el puerto para el servidor de jackpot, no se puede instanciar el servidor");
                }
            }
            else{
                //Verificar si no es el servidor
                if (query != null && !query.isEmpty() && query.get(0).get("servidor") != null && !Boolean.valueOf(query.get(0).get("servidor").toString())) {
                    //La maquina es un cliente, no es el servidor, instanciar el servidor cliente de Jackpot
                    if (query.get(0).get("puerto_jackpot") != null) {
                        
                        //Instanciar el servidor de Jackpot
                        manejadorServerJackpot = new XSocketDataHandlerServerJackpot();
                        serverJackpot = new Server(Integer.valueOf(query.get(0).get("puerto_jackpot").toString()), manejadorServerJackpot);
                    }
                    else{
                        System.err.println("No se ha colocado el puerto para el cliente de jackpot, no se puede instanciar el cliente");
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void verificarServicioPostgreSQL() throws IOException, InterruptedException {
        int esperar = 20; //Segundos a esperar la conexion de PostgreSQL
        int cont = 0; //Contador de segundos
        boolean postgreSQL = false;
        while(cont < esperar && !postgreSQL){
        
            postgreSQL = new ProcessB1().isRunning("postgresql-9.4", new String[]{"cmd"});
            
            cont++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (!postgreSQL) {
            System.out.println("Error: No se encontró el servicio PostgreSQL, no se puede continuar");
            System.exit(1);
        }
        
    }

    private void iniciarBilletero() {
        
        if (billetero == null) {
            billetero = crearBilletero();
            puertoBilletero = configService.puertoBilletero();
        }
        
        //Verificar si esta conectado el billetero
        if (billetero != null && puertoBilletero > -1) {
            if (billetero.abrirPuerto(puertoBilletero)) {
                System.out.println("Billetero abierto y escuchando en el puerto " + puertoBilletero);
            }
            else{
                System.out.println("Error al abrir el puerto del billetero");
            }
        }
        else{
            System.out.println("Error al abrir el billetero: " + billetero == null);
            System.out.println("Puerto: " + puertoBilletero);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">

    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Metodos">

    //</editor-fold>
    
    private class XSocketDataHandler implements IDataHandler, IConnectHandler, IDisconnectHandler{

        private final Set<INonBlockingConnection> SESSIONS = Collections.synchronizedSet(new HashSet<INonBlockingConnection>());
        private final Set<INonBlockingConnection> SESSIONS_NOT_VALID = Collections.synchronizedSet(new HashSet<INonBlockingConnection>());
        private boolean hayJackpot;
        
        @Override
        public boolean onData(INonBlockingConnection nbc) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
            if (SESSIONS.contains(nbc)) {
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
                        case 26: response = this.pagar(); break;
                        case 27: response = this.generarBonus(); break;
                        case 50: response = this.jugar(p); break;
                        case 51: response = this.cargarCreditos(p); break;
//                        case 52: response = this.colocarApuestas(p); break;
                        case 56: response = this.estadoDelJuegoActual(); break;
                        case 60: response = this.bolasExtraSeleccionadas(); break;
                        case 61: response = this.seleccionarBolaExtra(p); break;
                        case 62: response = this.costoBolaExtra(); break;
                        case 63: response = this.gananciasYCreditos(); break;
                        case 64: response = this.historialDeJuegos(p); break;
                        case 65: response = this.contadores(); break;
                        case 120: response = this.enviarCreditosActuales(); break;
                        case 121: response = this.bonus(); break;
                        case 122: response = this.premioObtenidoEnBonus(p); break;
                        case 123: response = this.informarGananciaEnBonus(p); break;
                        case 124: response = this.creditosActualizados(); break;
                        case 125: response = this.actualizarEstadoDesdeLaVista(p); break;
                        case 130: response = this.bloquearBilletero(); break;
                        case 131: response = this.desbloquearBilletero(); break;
                        case 200: response = this.acumularParaElJackpot(); break;
                        case 201: response = this.otorgarJackpot(p); break;
                        case 202: response = this.obtenerTotalAcumuladoEnJackpot(); break;
                        case 203: response = this.reiniciarAcumulado(); break;
                        case 210: response = this.habilitarJackpot(); break;
                        default: response = noImplementadoAun(p);
                    }

                        if (p.getCodigo() != 202) {
                            System.out.println("Paquete recibido");
                            System.out.println(p.aJSON());
        
                            System.out.println("Paquete enviado");
                            System.out.println(response != null? response.aJSON() : "Mensaje nulo");
                        }

                    if (response != null) {
                        enviar(response.aJSON());
                    }
                    
                    //Guardar el historial del mensaje
                    if (p.getCodigo() != 202) {
                        
                        historialB1service.log(p, "SOLICITUD", MetodoB1.porCodigo(p.getCodigo()));
                        historialB1service.log(response, "RESPUESTA", MetodoB1.porCodigo(response.getCodigo()));
                    }

                } catch(IOException | SQLException ex){
                    logService.log(ExceptionUtils.getMessage(ex));
                    this.tratarDeEnviarExcepcion(ex);
                }
            }
        
        return true;
        }

        @Override
        public boolean onConnect(INonBlockingConnection inbc) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
            boolean added = false;
            try {
                synchronized(SESSIONS){
                    if (SESSIONS != null && !SESSIONS.isEmpty()) {
                        //Forbidden: Only one instance of BingoBot can be connected at same time
                        System.out.println("No se permiten conexiones");
                        SESSIONS_NOT_VALID.add(inbc);
                    }
                    else{
                        //First session connection to BingoBot
                        System.out.println("Conexion admitida: " + inbc.getRemoteAddress().getHostAddress());
                        added = SESSIONS.add(inbc);
                        bingo = new Juego();
                        configurarJuego(bingo);
                    }
                }
                } catch (BufferUnderflowException e) {
                    Logger.getLogger(XSocketDataHandler.class.getName()).log(Level.SEVERE, null, e);
                }
            return added;  
        }

        @Override
        public boolean onDisconnect(INonBlockingConnection inbc) throws IOException {
            if (SESSIONS.contains(inbc)) {
                System.out.println("Desconectado");
                if (billetero != null) {
                    billetero.cerrarPuerto();
                }
                //Cerrar la vista
                enviar(new Paquete.PaqueteBuilder().codigo(300).estado("ok").crear().aJSON());
                
                System.exit(0);
            }
            if (SESSIONS_NOT_VALID.contains(inbc)) {
                SESSIONS_NOT_VALID.remove(inbc);
                System.out.println("Conexion no valida removida");
            }
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
//            System.out.println("Excepcion: " + ex);
            try {
                Paquete respuesta = new Paquete.PaqueteBuilder()
                        .codigo(401)
                        .estado("error")
                        .dato("mensaje", getExcepcion(ex))
                        .dato("excepcion", ExceptionUtils.getMessage(ex))
                        .crear();
                enviar(respuesta.aJSON());
                logService.log(ExceptionUtils.getMessage(ex));
                
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

        private Paquete jugar(Paquete p) throws IOException {
            
            bingo.jugar(false);
            
            try {
                String query = "UPDATE juego SET comenzo = '" + getCurrentTimeStamp() + "'"
                                + ", ganado_en_bonus = 0, ganado = 0, bolas_visibles = '" + ArrayUtils.toString(bingo.getBolasVisibles()) + "'"
                        + ", bolas_extras = '" + ArrayUtils.toString(bingo.getBolasExtra()) + "',"
                        + " recaudado = recaudado + " + (bingo.ganancias() - bingo.apuestaTotal() * bingo.getDenominacion().getValue()) + ","
                        + " recaudado_desde_el_encendido = recaudado_desde_el_encendido + " + (bingo.ganancias() - bingo.apuestaTotal() * bingo.getDenominacion().getValue()) ;
//                System.out.println(query);
                Conexion.getInstancia().actualizar(query);
            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            //Actualizo los datos de la base de datos
            try {
                Conexion.getInstancia().actualizar("UPDATE juego SET creditos = " + bingo.getCreditos() + 
                        ", liberar_bolas_extra = " + bingo.liberarBolasExtra()
                        + ", ganado_carton1 = " + bingo.ganadoEnCarton(1) + 
                        ", ganado_carton2 = " + bingo.ganadoEnCarton(2) + 
                        ", ganado_carton3 = " + bingo.ganadoEnCarton(3) + 
                        ", ganado_carton4 = " + bingo.ganadoEnCarton(4) +
                        ", apuesta_total = " + bingo.apuestaTotal() + 
                        ", carton2_habilitado = " + bingo.getCartonesHabilitados()[1] +
                        ", carton3_habilitado = " + bingo.getCartonesHabilitados()[2] +
                        ", carton4_habilitado = " + bingo.getCartonesHabilitados()[3] +
                        ", ganado = " + bingo.ganancias() + 
                        ", termino = '" + getCurrentTimeStamp().toString() + "'"
                );
                
                //Acumulo los contadores
                contadoresService.incrementarCantidadDeJuegosJugados();
                contadoresService.incrementarCantidadDeJuegosDesdeElEncendido();
                
                if (bingo.ganancias() > 0) {
                    contadoresService.incrementarCantidadDeJuegosConAlgunaVictoria();
                }
                else{
                    contadoresService.incrementarCantidadDeJuegosSinVictorias();
                }
                
                //Acumular para el Jackpot
                double factorParaJackpot;
                boolean servidor;
                List<HashMap<String,Object>> query = null;

                try {
                    //Obtengo el tipo de maquina de juego: servidor o cliente
                    query = Conexion.getInstancia().consultar("SELECT * FROM configuracion");
                    } catch (SQLException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    if (query != null && !query.isEmpty()) {

                        //Obtengo el tipo de maquina, servidor(true) o cliente(false)
                        servidor = Boolean.valueOf(query.get(0).get("servidor").toString());

                        if (servidor) {
                            //La maquina es servidor, debe descontar para el Jackpot
                            //Obtengo el factor de descuento para Jackpot, actualmente
                            //se encuentra en el 1% de lo recaudado (0.01)
                            factorParaJackpot = Double.valueOf(query.get(0).get("factor_para_jackpot").toString());

                            //Calculo el total de dinero destinado al jackpot, es un porcentaje de la apuesta total
                            double paraJackpot = bingo.apuestaTotal() * bingo.getDenominacion().getValue() * factorParaJackpot;

                            //Persisto en la base de datos (solo si soy servidor)
                            configService.aumularParaElJackpot(paraJackpot);
                            
                            //Notifico a todas las maquinas conectadas al servidor
                            //Que el jackpot acaba de cambiar
                            manejadorClientJackpot.enviar(
                                new Paquete.PaqueteBuilder()
                                    .estado("ok")
                                    .codigo(202)
                                    .dato("acumulado", configService.getAcumulado())
                                    .crear()
                            );
                        }
                        else{
                            //La maquina no es servidor, enviar el mensaje al servidor
                            System.out.println("No es el servidor, enviar al ip");
                            
                            Object ipServidor = query.get(0).get("ip_servidor");
                            
                            if (ipServidor != null) {
                                String ipDelServidor = ipServidor.toString();
                                
                                //Logica del Socket
                                Socket s = new Socket(ipDelServidor, parametros.get("puerto") != null? Integer.valueOf(parametros.get("puerto").toString()) : 8890);
                                
                                DataOutputStream os = new DataOutputStream(s.getOutputStream());
                                
                                os.writeUTF(new Paquete.PaqueteBuilder().codigo(200).estado("pedido").crear().aJSON() + "\0");
                                
                                os.flush();
                                s.close();
                            }
                            else{
                                System.out.println("No hay ninguna IP configurada como servidor de Jackpot");
                            }
                        }
                    }
                
            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(50)
                    .estado("ok")
                    .dato("id", bingo.getUid())
                    .dato("bolasVisibles", bingo.getBolasVisibles())
                    .dato("bolasExtra", bingo.getBolasExtra())
                    .dato("bolas", bingo.getBolas())
                    .dato("ganado", bingo.ganancias())
                    .dato("ganadoEnBonus",0)
                    .dato("cantidadDeBolasExtraSeleccionadas",0)
                    .dato("premios", bingo.getPremiosPagados())
                    .dato("premiosPorSalir", bingo.getPremiosPorSalir())
                    .dato("apostado", bingo.apuestaTotal())
                    .dato("apostadoEnCicloDeBolasExtra", bingo.getApostadoEnCicloDeBolasExtra())
                    .dato("apostadoEnBolasExtra",0)
                    .dato("apuestas", bingo.getApostado())
                    .dato("creditos",bingo.getCreditos())
                    .dato("denominacion", bingo.getDenominacion().getValue())
                    .dato("cartonesHabilitados", bingo.getCartonesHabilitados())
                    .dato("habilitados", bingo.habilitados())
                    .dato("bonus", bingo.getBonus())
                    .dato("actualizarJuegoEnCicloBonus", bingo.huboBonus())
                    .dato("liberarBolasExtra", bingo.liberarBolasExtra())
                    .dato("hayJackpot", this.hayJackpot)
                    .dato("carton1", bingo.getCartones()[0])
                    .dato("carton2", bingo.getCartones()[1])
                    .dato("carton3", bingo.getCartones()[2])
                    .dato("carton4", bingo.getCartones()[3])
                    .dato("tablaDePagos", bingo.getTablaDePagos())
                    .crear();
            
            //Si hubo Jackpot, cambiar la bandera a false y resetear el acumulado
            if (hayJackpot) {
                hayJackpot = false;
            }
            
            //B1 en modo gráfico?
            if (B1FX) {
                //Avisar al manejador de eventos sobre este acontecimiento
                EventBusManager.getInstancia().getBus().post(new B1Controller.EventoJugar());
            }
            
              log(p);
            
//            System.out.println(p.aJSON());
            return response;
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
                
                try {
                    //Coloco en 0 las ganancias, esto refleja lo que hace la vista
                    Conexion.getInstancia().actualizar("UPDATE juego SET ganado = 0");
                } catch (SQLException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                
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
                    .estado(success? "ok" : "error")
                    .dato("desc", success ? "Habilitar carton " + p.getDatos().get("numero") + " habilitado" : "No se pudo habilitar")
                    .dato("numero",Integer.valueOf(p.getDatos().get("numero").toString()))
                    .dato("apuestaTotal", bingo.apuestaTotal())
                    .dato("apuestaIndividual", bingo.apuestaIndividual())
                    .dato("creditos", bingo.getCreditos())
                    .dato("cartonesHabilitados", bingo.getCartonesHabilitados())
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
                        apuestas[i] = ((apuesta / cartones_habilitados) > 0 ? apuesta / cartones_habilitados : 1 );
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
                    .dato("b1Habilitado", true) //Necesario para decirle a la vista que escuche
                    .dato("credito", bingo.getCreditos())
                    .dato("apostado", bingo.apuestaTotal())
                    .dato("apuestaIndividual", bingo.apuestaIndividual())
                    .dato("carton1_habilitado", carton1_habilitado)
                    .dato("carton2_habilitado", carton2_habilitado)
                    .dato("carton3_habilitado", carton3_habilitado)
                    .dato("carton4_habilitado", carton4_habilitado)
                    .dato("denominacion", bingo.getDenominacion().name())
                    .dato("figuras", bingo.getFigurasDePago())
                    .dato("factoresDePago", bingo.factoresDePago())
                    .dato("ganado", bingo.ganancias())
                    .dato("cartones", c)
                    .crear();
            
            //Si esta corriendo B1 en modo FX alertar al oyente
            if (B1FX) {
                EventBusManager.getInstancia().getBus().post(new B1Controller.EventoConfigurar(bingo));
            }
            
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
            
            //Salvar el historico de denominacion
            historialB1service.actualizarDenominacion(bingo);
            
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
                
                int creditos = Double.valueOf(p.getDatos().get("credito").toString()).intValue();
                bingo.agregarCreditos(creditos);
                Conexion.getInstancia().actualizar("UPDATE juego SET creditos = creditos + " + creditos);
                
                Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(25)
                    .estado("ok")
                    .dato("creditos", bingo.getCreditos())
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
                    .dato("bolasExtra", bingo.getBolasExtra())
                    .dato("liberarBolasExtra", bingo.liberarBolasExtra())
                    .crear();
            
            return response;
        }

        /**
         * Aumenta la apuesta total (apuesta basica) en tantas unidades como
         * cartones habilitados existan, aumenta la apuesta de cada carton en
         * una unidad. Persiste en la base de datos
         * @return Paquete B1
         */
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

        /**
         * Disminuye las apuestas totales (apuesta basica) en tantas unidades
         * como cartones habilitados existan, disminuye la apuesta en cada carton
         * habilitado en una unidad. Persiste el estado en la Base de datos
         * @return Paquete B1
         */
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
                    .dato("apuestaIndividual", bingo.apuestaIndividual())
                    .dato("desc", "Apuesta disminuida a "+ bingo.apuestaTotal())
                    .crear();
            
            return response;
        }

        private Paquete cobrar() throws IOException {
            
            String leyenda = "";
            
            try {
                //Mandar la leyenda de contadores
                
                List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT * from contadores");
                
                if (query != null && !query.isEmpty()) {
                    leyenda = "TOTAL $1: " + query.get(0).get("cantidad_de_billetes_de_$1") + "($" + 
                            sumar(1, Integer.valueOf(query.get(0).get("cantidad_de_billetes_de_$1").toString())) + ")\n";
                    
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //Colocar los creditos en 0
            bingo.setCreditos(0);
            
            //Habilitar el billetero
            if (billetero != null) {
                billetero.habilitarTodo();
            }
            
            //INICIALIZAR CREDITOS EN LA TABLA juegos
            juegoService.inicializarCreditos();
            
            //ACUMULO EL MONTO A PAGAR EN EL ACUMULADO HISTORICO DE PAGOS
            configService.acumularPagoManualAlGeneral(bingo.getCreditos() * bingo.getDenominacion().getValue());
            
            //ACTUALIZO LA ULTIMA FECHA DE COBRO MANUAL
            configService.asentarUltimaFechaDePagoManual();
            
            Paquete respuesta = new Paquete.PaqueteBuilder()
                        .codigo(16)
                        .estado("ok")
                        .dato("desc", "Pago manual activado, llamando al krupier")
                        .dato("credito", bingo.getCreditos())
                        .dato("leyendaPagoManual", leyenda)
                        .dato("pagar", bingo.getCreditos())
                        .crear();
            
            return respuesta;
        }

        /**
         * Genera un conjunto de numeros unicos para los distintos cartones
         * de juego, dichos numeros cumplen con el requisito de G.L.I
         * (prueba de medias, prueba de independencia, prueba de chi-cuadrada)
         * @return Paquete B1
         */
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
            
            //Si la aplicacion es BX avisar al oyente
            if (B1FX) {
                EventBusManager.getInstancia().getBus().post(new B1Controller.EventoCambiarCartones(bingo.getCartones()));
            }
            
            return response;
        }

        /**
         * Genera un paquete del protocolo B1 con código = 7, estado = ok y 
         * un campo "bolas" con el arreglo de bolas del bolillero actual,
         * las primeras 30 bolas son las bolas visibles (se visualizan al 
         * presionar el boton "JUGAR" y las siguientes 10 son las bolas extra
         * (se visualizan solo si se cumple la condicion para liberar tales bolas)
         * @return objeto com.protocol.b1.servidor.Paquete, entidad serializada
         * encargada de encapsular los datos de comunicación entre la vista y el 
         * controlador
         */
        private Paquete bolas() {
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(7)
                    .estado("ok")
                    .dato("bolas", ArrayUtils.addAll(bingo.getBolasVisibles(), bingo.getBolasExtra()))
                    .crear();
            
            return response;
        }

        /**
         * Genera un paquete del protocolo B1 encargado de encapsular
         * una matriz de matrices de 3X5 con los cartones actuales,
         * los mismos se obtienen del objeto "bingo" (clase Juego)
         * @return Paquete de comunicacion B1 con los cartones actuales
         */
        private Paquete cartonesActuales() {
            int[][][] cartones = bingo.getCartones();
            
            List<Map<String,Object>> c = new ArrayList<>();
            
            for (int i = 0; i < cartones.length; i++) {
                Integer[][] matriz = new Integer[Juego.getLineas()][Juego.getColumnas()];
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

        /**
         * Genera un nuevo bolillero (y tambien un nuevo Bonus) 
         * @return Paquete de comunicacion B1
         */
        private Paquete generarBolillero() {
            
            if (hayJackpot) {
                int[] bolillero = generarJackpotDirigido();
                
                int[] visibles = Arrays.copyOf(bolillero, 30);
                int[] extra = Arrays.copyOfRange(bolillero, 30, 40);
                
                //Revolver
                Matematica.revolver(visibles);
                Matematica.revolver(extra);
                
                bingo.setBolasVisibles(visibles);
                bingo.setBolasExtra(extra);
            }
            else{
                bingo.generarBolillero();
            }
            
            //Acciones por defecto
            bingo.generarBonusB1();
            bingo.buscarPremios(FaseDeBusqueda.PRIMERA);
            bingo.buscarPremiosPorSalir(bingo.isUtilizarUmbralParaLiberarBolasExtra());
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(24)
                    .estado("ok")
                    .dato("bolasVisibles", bingo.getBolasVisibles())
                    .dato("bolasExtra", bingo.getBolasExtra())
                    .dato("bolas", bingo.getBolas())
                    .dato("liberarBolasExtra", bingo.liberarBolasExtra())
                    .dato("bonus", bingo.getBonus())
                    .dato("desc", "Bolillero nuevo generado")
                    .crear();
            
            return response;
        }

        /**
         * Deshabilita un carton seleccionado a partir del dato "numero"
         * encapsulado en el paquete "p" pasado como parametro
         * @param p Paquete recibido desde la vista, debe contener el dato "numero" (int)
         * @return Paquete B1
         */
        private Paquete deshabilitarCarton(Paquete p) {
            
            if (p != null && p.getDatos() != null && p.getDatos().get("numero") != null) {
                
                int numero = Integer.valueOf(p.getDatos().get("numero").toString());
                
                boolean success = false;
                if (numero > 0 && numero < Juego.getCantidadDeCartones() + 1) {
                    success = bingo.deshabilitar(numero - 1);
                }
                
                //DESHABILITAR EL CARTON Y ACTUALIZAR APUESTA EN LA BD
                juegoService.deshabilitarCarton(numero, bingo.apuestaTotal());
                
                Paquete response;
                
                if (success) {
                    response = new Paquete.PaqueteBuilder()
                    .codigo(21)
                    .estado("ok")
                    .dato("desc", "Carton " + p.getDatos().get("numero") + " deshabilitado")
                    .dato("numero", Integer.valueOf(p.getDatos().get("numero").toString()))
                    .dato("apuestaTotal", bingo.apuestaTotal())
                    .dato("apuestaIndividual", bingo.apuestaIndividual())
                    .dato("habilitados", bingo.habilitados())
                    .crear();
                }
                else{
                    response = new Paquete.PaqueteBuilder()
                    .codigo(21)
                    .estado("error")
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

        /**
         * Genera un paquete de comunicacion B1 con el arreglo de premios
         * otorgados en el ciclo de Bonus
         * @return Paquete B1, codigo 121
         */
        private Paquete bonus() {
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(121)
                    .estado("ok")
                    .dato("bonus", bingo.getBonus())
                    .crear();
            
            return response;
        }
        
        /**
         * Genera un arreglo de premios (fijos o variables, depende de la configuracion
         * previa) para el ciclo de Bonus. A la fecha (4/7/2017) se estableció
         * con el cliente que el bonus sera variable (premios multiplos de la apuesta
         * basica)
         * @return Paquete B1, codigo 27 
         */
        private Paquete generarBonus() {
            
            bingo.generarBonusB1();
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(27)
                    .estado("ok")
                    .dato("bonus", bingo.getBonus())
                    .dato("desc", "Nuevo bonus generado")
                    .crear();
            
            return response;
        }

        private Paquete premioObtenidoEnBonus(Paquete p) {
            if (p != null && p.getDatos() != null && p.getDatos().get("creditosDePremio") != null) {
                
                //Recupero el premio (numero entero en creditos)
                int premio = Integer.valueOf(p.getDatos().get("premio").toString());
                
                //Aumentar las ganancias 
                bingo.setTotalGanadoEnBonus(bingo.getTotalGanadoEnBonus() + premio);

                try {
                    //Actualizar los datos de la base de datos
                    Conexion.getInstancia().actualizar(("UPDATE juego set ganado_en_bonus = ganado_en_bonus +" + premio));
                } catch (SQLException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(122)
                    .estado("ok")
                    .dato("ganado", bingo.ganancias())
                    .dato("desc", "Aumentando las ganancias gracias al bonus")
                    .crear();
            
                return response;
            }
            return null;
        }

        private Paquete pagar() {
            
            double pagado = 0;
            String leyendaPagoManual = "";
            
            //Deshabilitar para evitar problemas
            if (billetero != null) {
                billetero.deshabilitarTodo();
            }
            
            try {
                List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT dinero FROM juego");
                
                if (query != null && !query.isEmpty()) {
                    pagado = Double.parseDouble(query.get(0).get("dinero").toString());
                    
                    //Colocar en cero los acumuladores
                    Conexion.getInstancia().actualizar("UPDATE juego SET creditos = 0, ganado = 0, liberar_bolas_extra = FALSE");
                }
                
                //Mandar la leyenda de contadores
                
                
                query = Conexion.getInstancia().consultar("SELECT * from contadores");
                
                if (query != null && !query.isEmpty()) {
                    leyendaPagoManual = "TOTAL $1: " + query.get(0).get("cantidad_de_billetes_de_$1") + "($" + 
                            sumar(1, Integer.valueOf(query.get(0).get("cantidad_de_billetes_de_$1").toString())) + ")\n"
                            + "TOTAL $2: "  + query.get(0).get("cantidad_de_billetes_de_$2") + "($" + 
                            sumar(2, Integer.valueOf(query.get(0).get("cantidad_de_billetes_de_$2").toString())) + ")\n"
                            + "TOTAL $5: "  + query.get(0).get("cantidad_de_billetes_de_$5") + "($" + 
                            sumar(5, Integer.valueOf(query.get(0).get("cantidad_de_billetes_de_$5").toString())) + ")\n"
                            + "TOTAL $10: "  + query.get(0).get("cantidad_de_billetes_de_$10") + "($" + 
                            sumar(10, Integer.valueOf(query.get(0).get("cantidad_de_billetes_de_$10").toString())) + ")\n"
                            + "TOTAL $20: "  + query.get(0).get("cantidad_de_billetes_de_$20") + "($" + 
                            sumar(20, Integer.valueOf(query.get(0).get("cantidad_de_billetes_de_$20").toString())) + ")\n"
                            + "TOTAL $50: "  + query.get(0).get("cantidad_de_billetes_de_$50") + "($" + 
                            sumar(50, Integer.valueOf(query.get(0).get("cantidad_de_billetes_de_$50").toString())) + ")\n"
                            + "TOTAL $100: "  + query.get(0).get("cantidad_de_billetes_de_$100") + "($" + 
                            sumar(100, Integer.valueOf(query.get(0).get("cantidad_de_billetes_de_$100").toString())) + ")\n"
                            ;
                    
                    leyendaPagoManual += "Pagar: $" + pagado;
                }
                
            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(26)
                    .estado("ok")
                    .dato("pagado", pagado)
                    .dato("leyendaPagoManual", leyendaPagoManual)
                    .crear();
            
            return response;
        }

        /**
         * Obtiene el numero de bolas extra seleccionadas por el jugador,
         * tener en cuenta que el arreglo de bolas extra se implementó (heredado)
         * como una pila, con lo cual si el numero de bolas extra seleccioandas 
         * es igual a 3 eso indica que las bolas extra descubiertas fueron las 
         * de la posicion 0,1 y 2 en el arreglo de bolas extra
         * @return Paquete B1, codigo = 60
         */
        private Paquete bolasExtraSeleccionadas() {
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(60)
                    .estado("ok")
                    .dato("bolasExtraSeleccionadas", bingo.getBolasExtraSeleccionadas())
                    .crear();
            return response;
        }

        private Paquete seleccionarBolaExtra(Paquete p) {
            String leyenda = bingo.seleccionarBolaExtra();
                
                try {
                    Conexion.getInstancia().actualizar("UPDATE juego SET "
                            + "creditos = " + bingo.getCreditos() + ","
                            + "ganado = " + bingo.ganancias() + ","
                            + "carton2_habilitado = " + bingo.getCartonesHabilitados()[1] + ","
                            + "carton3_habilitado = " + bingo.getCartonesHabilitados()[2] + ","
                            + "carton4_habilitado = " + bingo.getCartonesHabilitados()[3] + ","
                            + "ganado_carton1 = " + bingo.getGanado()[0] + ","
                            + "ganado_carton2 = " + bingo.getGanado()[1] + ","
                            + "ganado_carton3 = " + bingo.getGanado()[2] + ","
                            + "ganado_carton4 = " + bingo.getGanado()[3]
                    );
                
                    //Actualizar la cantidad de bolas extra seleccionadas en la tabla de historial
                    historialB1service.aumentarBolasExtraSeleccionadas(bingo);
                    
                
                } catch (SQLException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(61)
                    .estado("ok")
                    .dato("costoBolaExtra", bingo.costoBolaExtra())
                    .dato("bolaExtra", bingo.getBolasVisibles()[bingo.getBolasVisibles().length - 1])
                    .dato("leyenda", leyenda)
                    .crear();
                return response;
        }

        /**
         * Obtiene el costo (en creditos) de la bola extra siguiente, la misma
         * se calcula dependiendo de la proxima figura por salir: una figura
         * por salir es tal si se encuentra dicha figura de pago en algun
         * carton habilitado y le falta una casilla (cualquiera) para ser
         * formada. Tambien genera un arreglo de premios de bonus, esto es
         * necesario para que no salgan dos bonus iguales en el mismo juego
         * @return Paquete B1 
         */
        private Paquete costoBolaExtra() {
            
            bingo.generarBonusB1();
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(62)
                    .estado("ok")
                    .dato("costoBolaExtra", bingo.costoBolaExtra())
                    .dato("bonus", bingo.getBonus())
                    .crear();
            
            return response;
        }

        /**
         * Metodo accesorio, realiza la multiplicacion a la vieja escuela
         * (reemplazar en cualquier momento ;) )
         * @param valor
         * @param frecuencia
         * @return 
         */
        private String sumar(int valor, Integer frecuencia) {
            int cont = 0;
            for (int i = 0; i < frecuencia; i++) {
                cont += valor;
            }
            return cont + "";
        }

        private Paquete informarGananciaEnBonus(Paquete p) {
            boolean recibido = p!= null && p.getDatos() != null && p.getDatos().get("ganadoEnBonus") != null;
            System.out.println("Premios del bonus" + ArrayUtils.toString(bingo.getBonus()));
            
            if (recibido) {
                //El juego envió correctamente las ganancias del Bonus, persistirlas y actualizar los creditos
                Integer ganadoEnBonus = Integer.valueOf(p.getDatos().get("ganadoEnBonus").toString());
                
                try {
                    List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT ganado_en_bonus FROM juego");
                    
                    if (query != null && !query.isEmpty()) {
                        int ganadoAnterior = Integer.valueOf(query.get(0).get("ganado_en_bonus").toString());
                        
                        //Se resta siempre por que pueden haber mas de 1 bonus por juego y 
                        //el front end envia la suma de los bonus anteriores
                        ganadoEnBonus -= ganadoAnterior;
                    }
                } catch (SQLException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                bingo.setCreditos(bingo.getCreditos() + ganadoEnBonus);
                bingo.setTotalGanadoEnBonus(bingo.getTotalGanadoEnBonus() + ganadoEnBonus);
                
                try {
                    Conexion.getInstancia().actualizar("UPDATE juego SET creditos = "  + bingo.getCreditos() +
                            ", ganado = ganado + " + ganadoEnBonus + ", ganado_en_bonus = " + ganadoEnBonus
                    );
                } catch (SQLException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                //Actualizar el historial
                historialB1service.actualizarJuegoEnCicloBonus(bingo);
            }
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(123)
                    .estado(recibido? "ok" : "error")
                    .dato("todasLasBolasExtraFueronLiberadas", bingo.getCantidadDeBolasExtraSeleccionadas() == 10)
                    .dato("noHuboBolasExtra", bingo.getCantidadDeBolasExtraSeleccionadas() == 0)
                    .dato("creditosActuales", bingo.getCreditos())
                    .dato("desc", recibido ? "Recibido en bonus "  + p.getDatos().get("ganadoEnBonus") + " creditos"  : "Faltan parametros, abortando")
                    .crear();
            
            return response;
        }

        private Paquete colocarApuestas(Paquete p) {
            if (p != null && p.getDatos() != null && p.getDatos().get("totalApostado") != null) {
                
                int apuestas = Double.valueOf(p.getDatos().get("totalApostado").toString()).intValue();
                bingo.apostar(apuestas);
                try {
                    System.out.println("Actualizando las apuestas totales, metodo colocarApuestas: " + apuestas);
                    Conexion.getInstancia().actualizar("UPDATE juego SET apuesta_total = " + apuestas);
                } catch (SQLException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(52)
                    .estado("ok")
                    .dato("totalApostado", apuestas)
                    .crear();
                
                return response;
            }
            
            return null;

        }

        private Paquete gananciasYCreditos() {
            Paquete result = new Paquete.PaqueteBuilder()
                    .codigo(63)
                    .estado("ok")
                    .dato("creditos", bingo.getCreditos())
                    .dato("ganado", bingo.ganancias())
                    .crear();
            
            return result;
        }

        private Paquete cargarCreditos(Paquete p) {
            
            return new Paquete.PaqueteBuilder()
                    .codigo(51)
                    .estado("ok")
                    .dato("creditos", bingo.getCreditos())
                    .crear();
        }

        private Paquete creditosActualizados() {
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(124)
                    .estado("ok")
                    .dato("creditosActualizados", bingo.getCreditos())
                    .crear();
            return response;
        }

        private Paquete enviarCreditosActuales() {
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(120)
                    .estado("ok")
                    .dato("creditosActuales", bingo.getCreditos())
                    .crear();
            return response;
        }

        private Paquete actualizarEstadoDesdeLaVista(Paquete p) {
            if (p != null && p.getDatos() != null && p.getDatos().get("cartonesActivos") != null 
                    && p.getDatos().get("apuestaTotal") != null
                    && p.getDatos().get("creditos") != null) {
                int cartonesActivos = Double.valueOf(p.getDatos().get("cartonesActivos").toString()).intValue();
                int apuestaTotal = Double.valueOf(p.getDatos().get("apuestaTotal").toString()).intValue();
                int creditos = Double.valueOf(p.getDatos().get("creditos").toString()).intValue();
                
                for (int i = 0; i < cartonesActivos; i++) {
                    bingo.getCartonesHabilitados()[i] = true;
                }
                
                for (int i = cartonesActivos; i < 4; i++) {
                    bingo.getCartonesHabilitados()[i] = false;
                }
                
                bingo.apostar(apuestaTotal);
                bingo.setCreditos(creditos);
                
                try {
                    Conexion.getInstancia().actualizar("UPDATE juego SET apuesta_total = " + apuestaTotal +
                            ", creditos = " + creditos + 
                            ", carton1_habilitado = " + bingo.getCartonesHabilitados()[0] +
                            ", carton2_habilitado = " + bingo.getCartonesHabilitados()[1] +
                            ", carton3_habilitado = " + bingo.getCartonesHabilitados()[2] +
                            ", carton4_habilitado = " + bingo.getCartonesHabilitados()[3]);
                    
                    return new Paquete.PaqueteBuilder()
                            .codigo(125)
                            .estado("ok")
                            .dato("desc", "Estado interno cambiado correctamente")
                            .crear();
                } catch (SQLException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return new Paquete.PaqueteBuilder()
                    .codigo(125)
                    .estado("error")
                    .dato("desc", "Faltan parametros")
                    .crear();
        }

        private Paquete acumularParaElJackpot() {
            Paquete.PaqueteBuilder response = new Paquete.PaqueteBuilder()
                    .codigo(200)
                    .estado("error");
            
            double factorParaJackpot;
            boolean servidor;
            List<HashMap<String,Object>> query = null;
            
            try {
                //Obtengo el tipo de maquina de juego: servidor o cliente
                query = Conexion.getInstancia().consultar("SELECT * FROM configuracion");
            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                response.dato("desc", "Problemas con la consulta a la base de datos");
            }
            
            if (query != null && !query.isEmpty()) {
                
                //Obtengo el tipo de maquina, servidor(true) o cliente(false)
                servidor = Boolean.valueOf(query.get(0).get("servidor").toString());
                
                if (servidor) {
                    //La maquina es servidor, debe descontar para el Jackpot
                    //Obtengo el factor de descuento para Jackpot, actualmente
                    //se encuentra en el 1% de lo recaudado (0.01)
                    factorParaJackpot = Double.valueOf(query.get(0).get("factor_para_jackpot").toString());
                    
                    double paraJackpot = bingo.apuestaTotal() * bingo.getDenominacion().getValue() * factorParaJackpot;
                    
                    try {
                        Conexion.getInstancia().actualizar("UPDATE configuracion SET acumulado = acumulado + " + paraJackpot);
                    } catch (SQLException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                        response.dato("desc","Problemas actualizando el acumulado (Mensaje 200)");
                    }
                }
                else{
                    response.dato("desc", "Esta máquina no es servidor, es cliente");
                }
            }
            
            return response.crear();
        }

        private Paquete otorgarJackpot(Paquete p) {
            Paquete.PaqueteBuilder response = new Paquete.PaqueteBuilder()
                    .estado("error")
                    .codigo(201);
            
            //Verificar si esta maquina es Servidor
            List<HashMap<String,Object>> query = null;
                    
            try {
                
                query = Conexion.getInstancia().consultar("SELECT * FROM configuracion");
                
            } catch (SQLException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                response.dato("desc", "Excepcion SQL: " + ex.getMessage());
            }
            
            if (query == null) {
                return response.dato("desc", "No se encontró la configuracion en la base de datos").crear();
            }
            
            boolean servidor = Boolean.valueOf(query.get(0).get("servidor").toString());
            
            //Verifico que la maquina sea servidor, caso contrario informar que es cliente
            if (servidor) {
                //Maquina servidor, verificar la condicion y si se cumple enviar el Jackpot
                double acumulado = Double.valueOf(query.get(0).get("acumulado").toString());
                
                //La condicion varia en funcion de lo que pida el cliente
                boolean condicion = acumulado > 12000.0 && acumulado < 25000.0 && RNG.getInstance().pickInt(10) > 5;
                
                if (condicion) {
                    
                    try {
                        //Colocar en 0 el valor del acumulado en la base de datos
                        Conexion.getInstancia().actualizar("UPDATE configuracion SET acumulado = 0.0");
                        
                        //Otorgar el Jackpot
                        response.estado("ok").dato("acumulado", acumulado);
                    } catch (SQLException ex) {
                        Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                        response.dato("desc", "Excepcion SQL, no se pudo actualizar el acumulado en tabla configuracion")
                                .dato("excepcion", ex.getMessage());
                    }
                }
                else{
                    return response.dato("desc", "Servidor responde: aun no se ha cumplido la condicion para otorgar el Jackpot").crear();
                }
            }
            else{
                return response.dato("desc", "Metodo invalido: la maquina no es servidor").crear();
            }
            
            return response.crear();
                    
        }
        
        private boolean log(Paquete p){
            boolean result = false;
            if (p != null && p.getDatos() != null && p.getDatos().get("apuestaTotal") != null
                    && p.getDatos().get("creditos") != null) {
                int apuestaTotal = Double.valueOf(p.getDatos().get("apuestaTotal").toString()).intValue();
                int creditos = Double.valueOf(p.getDatos().get("creditos").toString()).intValue();
                
                try {
                    Conexion.getInstancia().insertar("INSERT INTO logs(creditos_backend, creditos_frontend"
                            + ",apuesta_total_backend, apuesta_total_frontend) VALUES(" + bingo.getCreditos() +
                            "," + (creditos -  apuestaTotal) + "," + bingo.apuestaTotal() + "," + apuestaTotal + ")");
                    result = true;
                    System.out.println("Log correcto");
                } catch (SQLException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else{
                System.out.println("Faltan parametros para el log");
            }
            return result;
        }

        private Paquete obtenerTotalAcumuladoEnJackpot() {
            
            /*
            Si esta instancia de B1 es servidor de Jackpot enviar el acumulado,
            caso contrario no hacer nada
            */
            
            if (configService.esServidor()) {
                return new Paquete.PaqueteBuilder().codigo(202).estado("ok").dato("desc", "Bote").dato("acumulado", configService.getAcumulado()).crear();
            }
            
            Paquete response = new Paquete.PaqueteBuilder()
                    .codigo(202)
                    .estado("ok")
                    
                    .crear();
            
            return response;
        }

        private Paquete habilitarJackpot() {
            this.hayJackpot = true;
            
            return new Paquete.PaqueteBuilder()
                    .codigo(210)
                    .estado("ok")
                    .dato("hayJackpot", hayJackpot)
                    .dato("desc", "Se coloco en true el flag de Jackpot")
                    .crear();
        }

        private int[] generarJackpotDirigido() {
            //generar el bolillero del Bingo

            int cont = 0;
            int max = 60;
            
            int[] bolilleroDirigido = new int[max];
            
            for (int i = 0; i < Juego.getCantidadDeCartones(); i++) {
                for (int j = 0; j < Juego.getLineas(); j++) {
                    for (int k = 0; k < Juego.getColumnas(); k++) {
                        bolilleroDirigido[cont++] = bingo.getCartones()[i][j][k];
                    }
                }
            }
            int[] salida = Arrays.copyOf(bolilleroDirigido, 15);
            int[] cola = new int[15];
            int incremento = 0;
            
            boolean seguir = true;
            
            while(seguir){
                int indice = 15 + RNG.getInstance().pickInt(max - 15);
                
                if (bolilleroDirigido[indice] != 0) {
                    cola[incremento++] = bolilleroDirigido[indice];
                    bolilleroDirigido[indice] = 0;
                    seguir = incremento < 15;
                }
            }
            
            salida = ArrayUtils.addAll(salida, cola);
            
            Matematica.revolver(salida);
            
            return salida;
        }

        private Paquete reiniciarAcumulado() {
            configService.reiniciarAcumulado();
            return new Paquete.PaqueteBuilder().codigo(203).estado("ok").dato("desc", "Acumulado reiniciado a 0").crear();
        }

        private Paquete bloquearBilletero() {
            PaqueteBuilder response = new Paquete.PaqueteBuilder()
                    .codigo(130);
            
            if (billetero != null && billetero.puertoAbierto()) {
                billetero.deshabilitarTodo();
                
                response.estado("ok");
                response.dato("desc", "Billetero deshabilitado para el ingreso de billetes");
                return response.crear();
            }
            return response.estado("error").dato("desc", "Billetero no instanciado").crear();
        }

        private Paquete desbloquearBilletero() {
            PaqueteBuilder response = new Paquete.PaqueteBuilder()
                    .codigo(131);
            
            if (billetero != null && billetero.puertoAbierto()) {
                billetero.habilitarTodo();
                
                response.estado("ok");
                response.dato("desc", "Billetero habilitado para el ingreso de billetes");
                return response.crear();
            }
            return response.estado("error").dato("desc", "Billetero no instanciado").crear();
        }

        private Paquete historialDeJuegos(Paquete p) {
            PaqueteBuilder result = new PaqueteBuilder().codigo(64).estado("error").dato("desc", "Historial de juegos");
            if (p != null && p.getDatos() != null && p.getDatos().get("juegos") != null) {
                List<Paquete> juegos = historialB1service.historialDeJuegos(Double.valueOf(p.getDatos().get("juegos").toString()).intValue());
                
                if (juegos != null && !juegos.isEmpty()) {
                    result.estado("ok");
                    result
                            .dato("juegos", juegos)
                            .dato("total", juegos.size())
                            .dato("retHistorico", historialB1service.porcentajeDeRetribucionHistorico())
                            .dato("totalJuegos", historialB1service.cantidadDeJuegos())
                            .dato("recaudado", historialB1service.recaudadoHistorico())
                            ;
                }
                else{
                    result.dato("total", 0);
                }
            }
            return result.crear();
        }

        private Paquete contadores() {
            PaqueteBuilder pb = new PaqueteBuilder().codigo(65).estado("error");
            
            //CONSULTAR LOS CONTADORES
            Contadores cont = contadoresService.contadores();
            
            if (cont != null) {
                pb.estado("ok")
                        .dato("contadores", cont)
                        .dato("recaudadoDesdeElEncendido", historialB1service.recaudadoDesdeElUltimoEncendido())
                        ;
            }
            else{
                pb.dato("desc", "No se encontró el registro de contadores");
            }
            
            return pb.crear();
        }

        private Paquete estadoDelJuegoActual() {
            PaqueteBuilder result = new Paquete.PaqueteBuilder().codigo(56).estado("error");
            JuegoService.EstadoDelJuegoActual estado = juegoService.estadoActual();
            if (estado != null) {
                result.estado("ok").dato("estado", estado);
            }
            return result.crear();
        }
    }
    
    private class XSocketDataHandlerServerJackpot implements IDataHandler, IConnectHandler, IDisconnectHandler{

        private final Set<INonBlockingConnection> SESSIONS_SERVER = Collections.synchronizedSet(new HashSet<INonBlockingConnection>());
        
        @Override
        public boolean onData(INonBlockingConnection nbc) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
            
            /**
             * Logica necesaria para recibir los datos desde el puerto por defecto (8895) desde
             * el servidor, tener cuidado de que siempre sea el servidor el que envia
             * dicha informacion ya que de otra forma se puede corromper la integridad
             * de los datos almacenados en la base de datos y/o en el juego
             * 
             * Lo que el servidor envia siempre es el valor actual del acumulado
             * para el Jackpot
             */
            try {
                String data = nbc.readStringByDelimiter("\0"); //Lectura
                Paquete p = Paquete.deJSON(data); //Decodificación del paquete

                Paquete response = null;

                switch(p.getCodigo()){
                    case 202: response = colocarAcumuladoEinformar(p); break;
                }
                
                if (response != null) {
                    //Informar a la vista
                    manejadorGame.enviar(response.aJSON());
                }
                
            } catch (Exception e) {
                logService.log(e.getMessage());
            }
            
            return true;
        }

        @Override
        public boolean onConnect(INonBlockingConnection inbc) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
            System.out.println("Conexion admitida: " + inbc.getRemoteAddress().getHostAddress());
            
            return SESSIONS_SERVER.add(inbc);
        }

        @Override
        public boolean onDisconnect(INonBlockingConnection inbc) throws IOException {
            System.out.println("Desconectado: " + inbc.getRemoteAddress().getHostAddress());
            
            return SESSIONS_SERVER.remove(inbc);
        }
        
        //Metodos propios del servidor de Jackpot
        /**
        * Envia un mensaje al puerto, el mensaje corresponde
        * al formato JSON estandar
        * 
        * @param mensaje cadena de caracteres en formato JSON
        * @throws IOException Excepción de E/S al escribir en el puerto
        */
       public void enviar(String mensaje) throws IOException{
           synchronized(SESSIONS_SERVER)
           {
               Iterator<INonBlockingConnection> iter = SESSIONS_SERVER.iterator();

               while(iter.hasNext())
               {
                   INonBlockingConnection nbConn = (INonBlockingConnection) iter.next();

                   if(nbConn.isOpen())
                       nbConn.write(mensaje+"\0");
               }
           }
       }
       
       /**
        * Este metodo coloca el valor del acumulado (p.datos.acumulado)
        * informado por el servidor de Jackpot (no es esta maquina)
        * lo persiste en la tabla "configuracion" campo "acumulado"
        * y envia esta informacion a la vista (modulo Flash) para actualizar
        * el valor mostrado al jugador a traves del mensaje con codigo = 202
        * @param p Paquete B1 con el campo acumulado = informado por el servidor
        * @return Paquete B1, respuesta a enviar a la vista (campo acumulado seteado)
        */
       private Paquete colocarAcumuladoEinformar(Paquete p) {
            PaqueteBuilder result = new Paquete.PaqueteBuilder().codigo(202).estado("error");
            if (p != null && p.getDatos() != null && p.getDatos().get("acumulado") != null) {
                double acumulado = Double.valueOf(p.getDatos().get("acumulado").toString());
                configService.setAcumulado(acumulado); //Registrar el valor en la base de datos
                result.estado("ok").dato("acumulado", acumulado); //Crear el paquete para informar a la vista
            }
            return result.crear();
        }
    }
    
    private class XSocketDataHandlerClientJackpot implements IDataHandler, IConnectHandler, IDisconnectHandler{

        private final Set<INonBlockingConnection> SESSIONS_CLIENTS = Collections.synchronizedSet(new HashSet<INonBlockingConnection>());
        
        @Override
        public boolean onData(INonBlockingConnection inbc) throws IOException, BufferUnderflowException, ClosedChannelException, MaxReadSizeExceededException {
            
            /*
            Logica de oyente del servidor desde los clientes conectados
            */
            
            return true;
        }

        @Override
        public boolean onConnect(INonBlockingConnection inbc) throws IOException, BufferUnderflowException, MaxReadSizeExceededException {
            boolean added = false;
            try {
                synchronized(SESSIONS_CLIENTS){
                    added = SESSIONS_CLIENTS.add(inbc);
                }
            } catch (Exception e) {
                logService.log(e.getMessage());
            }
            
            return added;
        }

        @Override
        public boolean onDisconnect(INonBlockingConnection inbc) throws IOException {
            /**
             * Remuevo un cliente de la lista de sesiones de Jackpot
             * una vez que el mismo haya cerrado la conexion o se haya perdido
             * la conexion por algun motivo desconocido
             */
            
            boolean removed = false;
            try {
                if (SESSIONS_CLIENTS.contains(inbc)) {
                    removed = SESSIONS_CLIENTS.remove(inbc);
                }
            } catch (Exception e) {
                logService.log(e.getMessage());
            }
            return removed;
        }
        
        /**
         * Envia un paquete B1 a los clientes conectados al servidor
         * @param p Objeto Paquete 
         * @throws IOException Excepcion de entrada y salida, se puede producir
         * por algun inconveniente en la conexion de socket entre cliente y servidor
         */
        public void enviar(Paquete p) throws IOException{
            synchronized(SESSIONS_CLIENTS)
           {
               Iterator<INonBlockingConnection> iter = SESSIONS_CLIENTS.iterator();

               while(iter.hasNext())
               {
                   INonBlockingConnection nbConn = (INonBlockingConnection) iter.next();

                   if(nbConn.isOpen())
                       nbConn.write(p.aJSON() +"\0");
               }
           }
        }
        
        /**
         * Envia un paquete B1 (formato String) a los clientes conectados al servidor
         * @param paquete Objeto String con formato JSON y estructura segun especificacion B1
         * @throws IOException IOException Excepcion de entrada y salida, se puede producir
         * por algun inconveniente en la conexion de socket entre cliente y servidor
         */
        public void enviar(String paquete) throws IOException{
            synchronized(SESSIONS_CLIENTS)
           {
               Iterator<INonBlockingConnection> iter = SESSIONS_CLIENTS.iterator();

               while(iter.hasNext())
               {
                   INonBlockingConnection nbConn = (INonBlockingConnection) iter.next();

                   if(nbConn.isOpen())
                       nbConn.write(paquete +"\0");
               }
           }
        }
    }
}
