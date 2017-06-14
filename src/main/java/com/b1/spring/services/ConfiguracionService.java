/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.b1.spring.services;

import com.dao.Conexion;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.stereotype.Service;

/**
 *
 * @author Gonzalo
 */
@Service
@Configurable
public class ConfiguracionService {
    
    public int umbralParaLiberarBolasExtra(){
        int result = 9;
        
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT umbral_minimo_para_liberar_bolas_extra FROM configuracion LIMIT 1");
            if (query != null) {
                result = Double.valueOf(query.get(0).get("umbral_minimo_para_liberar_bolas_extra").toString()).intValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public boolean utilizarUmbralParaLiberarBolasExtra(){
        boolean result = false;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT utilizar_umbral_para_liberar_bolas_extra FROM configuracion LIMIT 1");
            if (query != null) {
                result = Boolean.valueOf(query.get(0).get("utilizar_umbral_para_liberar_bolas_extra").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public String rutaBingo(){
        String result = "bingo.swf";
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT ruta_bingobot FROM configuracion LIMIT 1");
            if (query != null && !query.isEmpty() && query.get(0) != null && query.get(0).get("ruta_bingobot") != null) {
                result = query.get(0).get("ruta_bingobot").toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public int puertoBilletero(){
        int result = 0;
        
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT puerto_billetero FROM configuracion LIMIT 1");
            if (query != null) {
                result = Double.valueOf(query.get(0).get("puerto_billetero").toString()).intValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return result;
    }
    
    public double costoDeLaBolaExtraEnPorcentajeDelMayorPorSalir(){
        double result = 0.04;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT porcentaje_del_premio_mayor_por_salir FROM configuracion LIMIT 1");
            
            if (query != null && !query.isEmpty() && query.get(0) != null && query.get(0).get("porcentaje_del_premio_mayor_por_salir") != null) {
                result = Double.valueOf(query.get(0).get("porcentaje_del_premio_mayor_por_salir").toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
}
