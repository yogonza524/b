/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.b1.spring.services;

import com.core.bingosimulador.Juego;
import com.dao.Conexion;
import com.google.gson.Gson;
import com.protocol.b1.servidor.Paquete;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 *
 * @author Gonzalo H. Mendoza
 * @email yogonza524@gmail.com
 * 
 */
@Service
public class HistorialB1Service {

    public boolean log(Paquete p, String accion, String metodo){
        boolean result = false;
        try {
            String query = "INSERT INTO historial_b1(paquete, accion, metodo) VALUES('" + p.aJSON() + "', '" + accion + "', '" + metodo + "')";
//            System.out.println(query);
            result = Conexion.getInstancia().insertar(query);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
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
                e.printStackTrace();
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
                e.printStackTrace();
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
                e.printStackTrace();
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
                e.printStackTrace();
            }
        }
        return result;
    }

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
                e.printStackTrace();
            }
        }
        return result;
    }
}
