/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.b1.spring.services;

import com.bingo.persistencia.Conexion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Gonzalo H. Mendoza
 * @email yogonza524@gmail.com
 * 
 */
@Service
public class JuegoService {

    @Autowired LogService logService;
    
    public void inicializarRecaudadoDesdeElEncendido(){
        try {
            Conexion.getInstancia().actualizar("UPDATE juego SET recaudado_desde_el_encendido = 0");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void inicializarRecaudado(){
        try {
            Conexion.getInstancia().actualizar("UPDATE juego SET recaudado = 0");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
}
