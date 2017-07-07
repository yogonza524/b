/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.b1.spring.services;

import com.bingo.persistencia.Conexion;

/**
 *
 * @author Gonzalo H. Mendoza
 * @email yogonza524@gmail.com
 * 
 */
//@Service
public class LogService {

    public void log(String message){
        try {
            Conexion.getInstancia().insertar("INSERT INTO general_log(id,\"desc\",fecha) VALUES(uuid(),'" + message + "',now());");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
