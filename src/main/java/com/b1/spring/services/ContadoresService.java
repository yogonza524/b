/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.b1.spring.services;

import com.dao.Conexion;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Gonzalo H. Mendoza
 * @email yogonza524@gmail.com
 * 
 */
@Service
public class ContadoresService {
    
    @Autowired LogService logService;
    
    public void incrementarCantidadDeBilletesDe$1(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET \"cantidad_de_billetes_de_$1\" = \"cantidad_de_billetes_de_$1\" + 1");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void incrementarCantidadDeBilletesDe$2(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET \"cantidad_de_billetes_de_$2\" = \"cantidad_de_billetes_de_$2\" + 1");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void incrementarCantidadDeBilletesDe$5(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET \"cantidad_de_billetes_de_$5\" = \"cantidad_de_billetes_de_$5\" + 1");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void incrementarCantidadDeBilletesDe$10(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET \"cantidad_de_billetes_de_$10\" = \"cantidad_de_billetes_de_$10\" + 1");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void incrementarCantidadDeBilletesDe$20(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET \"cantidad_de_billetes_de_$20\" = \"cantidad_de_billetes_de_$20\" + 1");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void incrementarCantidadDeBilletesDe$50(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET \"cantidad_de_billetes_de_$50\" = \"cantidad_de_billetes_de_$50\" + 1");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void incrementarCantidadDeBilletesDe$100(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET \"cantidad_de_billetes_de_$100\" = \"cantidad_de_billetes_de_$100\" + 1");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void incrementarCantidadDeJuegosJugados(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET cantidad_de_juegos_jugados = cantidad_de_juegos_jugados + 1");
            
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void incrementarCantidadDeJuegosConAlgunaVictoria(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET cantidad_de_juegos_con_victorias = cantidad_de_juegos_con_victorias + 1");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void incrementarCantidadDeJuegosSinVictorias(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET cantidad_de_juegos_sin_victorias = cantidad_de_juegos_sin_victorias + 1");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void inicializarCantidadDeJuegosDesdeElEncendido(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET cantidad_de_juegos_desde_el_encendido =0");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }

    public void incrementarCantidadDeJuegosDesdeElEncendido(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET cantidad_de_juegos_desde_el_encendido = cantidad_de_juegos_desde_el_encendido + 1");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void incrementarCantidadDeJuegosDesdeQueSeAbrioOcerroLaPuertaPrincipal(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET cantidad_de_juegos_desde_apertura_o_cierre_de_puerta_principal = cantidad_de_juegos_desde_apertura_o_cierre_de_puerta_principal + 1");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void incrementarCantidadDeJuegosDesdeQueSeAbrioLaPuertaPrincipal(){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET cantidad_de_veces_que_se_abrio_la_puerta_principal = cantidad_de_veces_que_se_abrio_la_puerta_principal + 1");
        } catch (Exception e) {
            logService.log(e.getMessage());
            try {
                Conexion.getInstancia().actualizar("UPDATE contadores SET cantidad_de_veces_que_se_abrio_la_puerta_principal = 0");
            } catch (Exception ex) {
                logService.log(ex.getMessage());
            }
        }
    }
    
    //GETTERS
    public int getCantidadDeBilletesDe$1(){
        int result = 0;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT \"cantidad_de_billetes_de_$1\" as total FROM contadores");
            if (query != null && !query.isEmpty()) {
                result = Double.valueOf(query.get(0).get("total").toString()).intValue();
            }
        } catch (SQLException | NumberFormatException e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    public int getCantidadDeBilletesDe$2(){
        int result = 0;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT \"cantidad_de_billetes_de_$2\" as total FROM contadores");
            if (query != null && !query.isEmpty()) {
                result = Double.valueOf(query.get(0).get("total").toString()).intValue();
            }
        } catch (SQLException | NumberFormatException e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    public int getCantidadDeBilletesDe$5(){
        int result = 0;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT \"cantidad_de_billetes_de_$5\" as total FROM contadores");
            if (query != null && !query.isEmpty()) {
                result = Double.valueOf(query.get(0).get("total").toString()).intValue();
            }
        } catch (SQLException | NumberFormatException e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    public int getCantidadDeBilletesDe$10(){
        int result = 0;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT \"cantidad_de_billetes_de_$10\" as total FROM contadores");
            if (query != null && !query.isEmpty()) {
                result = Double.valueOf(query.get(0).get("total").toString()).intValue();
            }
        } catch (SQLException | NumberFormatException e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    public int getCantidadDeBilletesDe$20(){
        int result = 0;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT \"cantidad_de_billetes_de_$20\" as total FROM contadores");
            if (query != null && !query.isEmpty()) {
                result = Double.valueOf(query.get(0).get("total").toString()).intValue();
            }
        } catch (SQLException | NumberFormatException e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    public int getCantidadDeBilletesDe$50(){
        int result = 0;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT \"cantidad_de_billetes_de_$50\" as total FROM contadores");
            if (query != null && !query.isEmpty()) {
                result = Double.valueOf(query.get(0).get("total").toString()).intValue();
            }
        } catch (SQLException | NumberFormatException e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    public int getCantidadDeBilletesDe$100(){
        int result = 0;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT \"cantidad_de_billetes_de_$100\" as total FROM contadores");
            if (query != null && !query.isEmpty()) {
                result = Double.valueOf(query.get(0).get("total").toString()).intValue();
            }
        } catch (SQLException | NumberFormatException e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    public int getCantidadDeBilletesDe$200(){
        int result = 0;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT \"cantidad_de_billetes_de_$200\" as total FROM contadores");
            if (query != null && !query.isEmpty()) {
                result = Double.valueOf(query.get(0).get("total").toString()).intValue();
            }
        } catch (SQLException | NumberFormatException e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    public int getCantidadDeBilletesDe$500(){
        int result = 0;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT \"cantidad_de_billetes_de_$500\" as total FROM contadores");
            if (query != null && !query.isEmpty()) {
                result = Double.valueOf(query.get(0).get("total").toString()).intValue();
            }
        } catch (SQLException | NumberFormatException e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    public void inicializarContadores(){
        try {
            Conexion.getInstancia().borrar("TRUNCATE contadores CASCADE");
            Conexion.getInstancia().insertar("INSERT INTO contadores VALUES(0)");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
}
