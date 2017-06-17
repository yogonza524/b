/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.b1.spring.services;

import com.dao.Conexion;
import com.protocol.b1.servidor.Paquete;
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
}
