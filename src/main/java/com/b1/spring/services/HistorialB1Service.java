/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.b1.spring.services;

import com.core.bingosimulador.Juego;
import com.dao.Conexion;
import com.google.gson.Gson;
import com.protocol.b1.main.MainApp;
import com.protocol.b1.servidor.Paquete;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gonzalo H. Mendoza
 * @email yogonza524@gmail.com
 * @pattern Microservicio. Spring Framework.
 * 
 */
//@Service
public class HistorialB1Service {

    private final LogService logService;

    public HistorialB1Service() {
        logService = MainApp.getLogService();
    }
    
    /**
     * Registra un mensaje del protocolo B1 (Paquete p) en la base de datos
     * @param p Paquete del protocolo B1
     * @param accion SOLICITUD | RESPUESTA
     * @param metodo nombre del metodo del protocolo B1
     * @return true si se persistió correctamente, false en caso contrario
     */
    public boolean log(Paquete p, String accion, String metodo){
        boolean result = false;
        try {
            String query = "INSERT INTO historial_b1(paquete, accion, metodo) VALUES('" + p.aJSON() + "', '" + accion + "', '" + metodo + "')";
            result = Conexion.getInstancia().insertar(query);
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    /**
     * Obtiene una lista de Paquetes (protocolo B1) cuyo metodo es "JUGAR"
     * @param cantidad numero entero positivo, representa la contidad de paquetes 
     * B1 a devolver en la lista
     * @return lista de paquetes B1 cuyos metodos son "JUGAR", ordenados
     * descendentemente desde la ultima fecha en la cual se generaron 
     */
    public List<Paquete> historialDeJuegos(int cantidad){
        List<Paquete> result = null;
        if (cantidad > 0) {
            try {
                List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT * FROM historial_b1 h WHERE h.metodo = 'JUGAR' AND h.accion = 'RESPUESTA' ORDER BY h.fecha DESC LIMIT " + cantidad);
                if (query != null && !query.isEmpty()) {
                    result = new ArrayList<>();
                    for(HashMap<String,Object> o : query){
                        if (o.get("paquete") != null) {
                            Paquete paket = new Gson()
                                    .fromJson(o.get("paquete").toString(), Paquete.class);
                            paket.getDatos().put("fecha", o.get("fecha").toString());
                            result.add(paket);
                        }
                    }
                }
            } catch (Exception e) {
                logService.log(e.getMessage());
            }
        }
        return result;
    }
    
    public boolean actualizarJuego(Juego bingo){
        boolean result = false;
        if (bingo != null) {
            try {
                List<HashMap<String,Object>> historial = com.protocol.dao.Conexion.getInstancia().consultar(""
                            + "SELECT id,paquete FROM historial_b1 h WHERE h.paquete -> 'datos'->> 'id' = '" + bingo.getUid() + "'");
                    
                    if (historial != null && !historial.isEmpty()) {
                        Paquete hPacket = new Gson().fromJson(historial.get(0).get("paquete").toString(), Paquete.class);
                        
                        if (hPacket != null) {
                            hPacket.getDatos().put("cantidadDeBolasExtraSeleccionadas", Double.valueOf(hPacket.getDatos().get("cantidadDeBolasExtraSeleccionadas").toString()).intValue() + 1);
                            hPacket.getDatos().put("creditos", bingo.getCreditos());
                            hPacket.getDatos().put("denominacion", bingo.getDenominacion().getValue());
                            hPacket.getDatos().put("ganado", bingo.ganancias());
                            hPacket.getDatos().put("apostadoEnCicloDeBolasExtra", bingo.getApostadoEnCicloDeBolasExtra());
                        
                            Conexion.getInstancia().actualizar("UPDATE historial_b1 h SET paquete = '" + hPacket.aJSON() + "' WHERE h.id = '" + historial.get(0).get("id") + "'");
                            
                            result = true;
                        }
                    }
            } catch (Exception e) {
                logService.log(e.getMessage());
            }
        }
        return result;
    }
    
    public boolean aumentarBolasExtraSeleccionadas(Juego bingo){
        boolean result = false;
        if (bingo != null) {
            try {
                List<HashMap<String,Object>> historial = com.protocol.dao.Conexion.getInstancia().consultar(""
                            + "SELECT id,paquete FROM historial_b1 h WHERE h.paquete -> 'datos'->> 'id' = '" + bingo.getUid() + "'");
                if (historial != null && !historial.isEmpty()) {
                        Paquete hPacket = new Gson().fromJson(historial.get(0).get("paquete").toString(), Paquete.class);
                        
                        if (hPacket != null) {
                            hPacket.getDatos().put("cantidadDeBolasExtraSeleccionadas", Double.valueOf(hPacket.getDatos().get("cantidadDeBolasExtraSeleccionadas").toString()).intValue() + 1);
                            hPacket.getDatos().put("apostadoEnCicloDeBolasExtra", bingo.getApostadoEnCicloDeBolasExtra());
                            hPacket.getDatos().put("creditos", bingo.getCreditos());
                            hPacket.getDatos().put("ganado", bingo.ganancias());
                            
                            Conexion.getInstancia().actualizar("UPDATE historial_b1 h SET paquete = '" + hPacket.aJSON() + "' WHERE h.id = '" + historial.get(0).get("id") + "'");
                            
                            result = true;
                        }
                    }
            } catch (Exception e) {
                logService.log(e.getMessage());
            }
        }
        return result;
    }
    
    public boolean actualizarJuegoEnCicloBonus(Juego bingo){
        boolean result = false;
        if (bingo != null) {
            try {
                List<HashMap<String,Object>> historial = com.protocol.dao.Conexion.getInstancia().consultar(""
                            + "SELECT id,paquete FROM historial_b1 h WHERE h.paquete -> 'datos'->> 'id' = '" + bingo.getUid() + "'");
                    
                    if (historial != null && !historial.isEmpty()) {
                        Paquete hPacket = new Gson().fromJson(historial.get(0).get("paquete").toString(), Paquete.class);
                        
                        if (hPacket != null) {
                            hPacket.getDatos().put("creditos", bingo.getCreditos());
                            hPacket.getDatos().put("ganadoEnBonus", bingo.premioTotalDelBonus());
                            hPacket.getDatos().put("ganado", bingo.ganancias());
                            hPacket.getDatos().put("huboBonus", true);
                        
                            Conexion.getInstancia().actualizar("UPDATE historial_b1 h SET paquete = '" + hPacket.aJSON() + "' WHERE h.id = '" + historial.get(0).get("id") + "'");
                            
                            result = true;
                        }
                    }
            } catch (Exception e) {
                logService.log(e.getMessage());
            }
        }
        return result;
    }

    /**
     * Actualiza la denominacion de un juego dado por su id (uuid) en la 
     * base de datos (tabla historial_b1)
     * @param bingo Objeto Juego. Contiene un uuid y denominacion
     * @return true si se actualizó correctamente la base de datos, false
     * en otro caso.
     */
    public boolean actualizarDenominacion(Juego bingo) {
        boolean result = false;
        if (bingo != null) {
            try {
                List<HashMap<String,Object>> historial = com.protocol.dao.Conexion.getInstancia().consultar(""
                            + "SELECT id,paquete FROM historial_b1 h WHERE h.paquete -> 'datos'->> 'id' = '" + bingo.getUid() + "'");
                    
                    if (historial != null && !historial.isEmpty()) {
                        Paquete hPacket = new Gson().fromJson(historial.get(0).get("paquete").toString(), Paquete.class);
                        
                        if (hPacket != null) {
                            hPacket.getDatos().put("creditos", bingo.getCreditos());
                            hPacket.getDatos().put("creditos", bingo.getCreditos());
                            hPacket.getDatos().put("denominacion", bingo.getDenominacion().getValue());
                        
                            Conexion.getInstancia().actualizar("UPDATE historial_b1 h SET paquete = '" + hPacket.aJSON() + "' WHERE h.id = '" + historial.get(0).get("id") + "'");
                            
                            result = true;
                        }
                    }
            } catch (Exception e) {
                logService.log(e.getMessage());
            }
        }
        return result;
    }
    
    public double porcentajeDeRetribucionHistorico(){
        double result = 0.0;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar(
                    "SELECT 100 * SUM(CAST(h.paquete -> 'datos' ->> 'ganado' AS real)) / SUM(CAST(h.paquete -> 'datos' ->> 'apostado' AS real) + \n" +
                    "CAST(h.paquete -> 'datos' ->> 'apostadoEnCicloDeBolasExtra' as real)) \n" +
                    "as retribuido FROM historial_b1 h WHERE h.metodo = 'JUGAR'"
            );
            if (query != null && !query.isEmpty()) {
                result = Double.valueOf(query.get(0).get("retribuido").toString());
            }
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
        return result;
    }

    /**
     * Obtiene el numero total de juegos registrados desde el inicio de 
     * los tiempos (en la base de datos)
     * @return numero entero mayor o igual a cero.
     */
    public int cantidadDeJuegos() {
        int result = 0;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT COUNT(*) as total FROM historial_b1 h WHERE  h.metodo = 'JUGAR' AND h.accion = 'SOLICITUD'");
            if (query != null && !query.isEmpty()) {
                result = Double.valueOf(query.get(0).get("total").toString()).intValue();
            }
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    public int recaudadoHistorico(){
        int result = 0;
        try {
            
            //RECAUDADO EN CREDITOS
//            String r = ""
//                    + "SELECT SUM(CAST(h.paquete -> 'datos' ->> 'apostado' AS real) + \n" +
//                    "CAST(h.paquete -> 'datos' ->> 'apostadoEnCicloDeBolasExtra' as real)) - SUM(CAST(h.paquete -> 'datos' ->> 'ganado' AS real))\n" +
//                    "as recaudado FROM historial_b1 h WHERE h.metodo = 'JUGAR'";

            String r = "SELECT SUM(\n" +
                "	(\n" +
                "		CAST(h.paquete -> 'datos' ->> 'apostado' AS real) + \n" +
                "		CAST(h.paquete -> 'datos' ->> 'apostadoEnCicloDeBolasExtra' as real) - \n" +
                "		CAST(h.paquete -> 'datos' ->> 'ganado' AS real)\n" +
                "	) * CAST(h.paquete -> 'datos' ->> 'denominacion' AS real)\n" +
                ") \n" +
                "as recaudado FROM historial_b1 h WHERE h.metodo = 'JUGAR'";

            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar(r);
            if (query != null && !query.isEmpty()) {
                result = Double.valueOf(query.get(0).get("recaudado").toString()).intValue();
            }
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
        return result;
    }
}
