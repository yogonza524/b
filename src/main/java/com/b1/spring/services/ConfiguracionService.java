/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.b1.spring.services;

import com.dao.Conexion;
import com.protocol.b1.main.MainApp;
import com.protocol.b1.servidor.Servidor;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gonzalo H. Mendoza
 * @email yogonza524
 * @pattern Microservicio. Spring Framework. Inyección de dependencias
 */
//@Service
//@Configurable
public class ConfiguracionService {
    
    //@Autowired LogService logService; //Dependencia creada en com.protocol.b1.main
    private final LogService logService;

    public ConfiguracionService() {
        logService = MainApp.getLogService();
    }
    
    /**
     * Obtiene el umbral de creditos de la figura de pago por salir 
     * necesaria para cumplir la condicion de liberar las bolas extra
     * @return numero entero positivo, representa los creditos minimos
     * que deben cumplir las figuras de pago por salir para liberar las bolas
     * extra
     */
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
    
    /**
     * Obtiene la bandera (desde la base de datos) necesaria para decidir si 
     * se debe utilizar un umbral de creditos de las figuras de pago por salir
     * para liberar las bolas extra
     * @return booleano
     */
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
    
    /**
     * Actualiza la ultima fecha y hora del pago manual, el mismo sirve para
     * conocer cuando debe comenzar la nueva contabilización de metricas
     */
    public void asentarUltimaFechaDePagoManual(){
        try {
            Conexion.getInstancia().actualizar("UPDATE configuracion SET ultimo_pago_manual_realizado = now()");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    /**
     * Acumula el monto pagado en el pago manual al acumulado historico
     * @param montoApagar valor real, representa el monto en dinero a acumular
     */
    public void acumularPagoManualAlGeneral(double montoApagar){
        try {
            Conexion.getInstancia().actualizar("UPDATE contadores SET pagado_general = pagado_general + " + montoApagar);
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    /**
     * Acumula el monto especificado por Dinero en la base de datos
     * Este metodo solo debe ser llamado por el servidor de Jackpot
     * el cliente de jackpot debe informar al servidor via socket
     * a traves del puerto especificado (por defecto 8895)
     * @param dinero 
     */
    public void aumularParaElJackpot(double dinero){
        try {
            com.protocol.dao.Conexion.getInstancia().actualizar("UPDATE configuracion SET acumulado = acumulado + " + dinero);
        } catch (SQLException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            logService.log(ex.getMessage());
        }
    }
    
    /**
     * Obtiene el total acumulado para el Jackpot, el mismo 
     * se encuentra en la tabla bingo -> configuracion
     * @return valor total acumulado por los distintos clientes
     * conectados al servidor de Jackpot
     */
    public double getAcumulado(){
        double result = 0.0;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT acumulado FROM configuracion LIMIT 1");
            if (query != null && ! query.isEmpty()) {
                result = Double.valueOf(query.get(0).get("acumulado").toString());
            }
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    public boolean esServidor(){
        boolean result = false;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT servidor FROM configuracion LIMIT 1");
            if (query != null && !query.isEmpty()) {
                result = Boolean.valueOf(query.get(0).get("servidor").toString());
            }
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
        return result;
    }
}
