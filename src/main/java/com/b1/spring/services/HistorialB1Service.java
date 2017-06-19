/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.b1.spring.services;

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
}
