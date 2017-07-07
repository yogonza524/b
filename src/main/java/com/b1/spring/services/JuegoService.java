/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.b1.spring.services;

import com.bingo.persistencia.Conexion;
import com.protocol.b1.main.MainApp;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Gonzalo H. Mendoza
 * @email yogonza524@gmail.com
 * @pattern Microservicio. Spring Framework. Inyección de dependencias
 * 
 */
//@Service
public class JuegoService {

    //@Autowired LogService logService; //dependencia creda en com.protocol.b1.MainApp
    private final LogService logService;

    public JuegoService() {
        logService = MainApp.getLogService();
    }
    
    
    /**
     * Coloca en 0.0 el monto total recaudado por la maquina desde que se inicio
     * el juego, el mismo se persiste en la base de datos, (configuracion -> recaudado_desde_el_encendido)
     */
    public void inicializarRecaudadoDesdeElEncendido(){
        try {
            Conexion.getInstancia().actualizar("UPDATE juego SET recaudado_desde_el_encendido = 0");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    /**
     * Cocola en 0 el recaudado total desde el inicio de los tiempos,
     * el mismo se persiste en la base de datos (configuracion -> recaudado)
     */
    public void inicializarRecaudado(){
        try {
            Conexion.getInstancia().actualizar("UPDATE juego SET recaudado = 0");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    /**
     * Coloca en 0 los creditos del juego (Singleton), el mismo se persiste 
     * en la base de datos (juego -> creditos)
     */
    public void inicializarCreditos(){
        try {
            Conexion.getInstancia().actualizar("UPDATE juego SET creditos = 0");
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    /**
     * Deshabilita el carton dado por "numero" (1...4) y actualiza la apuesta 
     * total (apuesta basica). El mismo se persiste en la base de datos
     * (juego -> cartonX_habilitado, juego -> apuesta_total)
     * @param numero entero positivo de 1 a 4
     * @param apuestaTotal entero positivo, representa los creditos de la apuesta basica
     */
    public void deshabilitarCarton(int numero, int apuestaTotal){
        try {
            com.protocol.dao.Conexion.getInstancia().actualizar("UPDATE juego set carton" + numero + "_habilitado = false, apuesta_total = " + apuestaTotal);
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
    }
    
    public void setApuestaTotal(int apuestaTotal){
        try {
            com.protocol.dao.Conexion.getInstancia().actualizar("UPDATE juego SET apuesta_total = " + apuestaTotal);
        } catch (Exception e) {
            logService.equals(e.getMessage());
        }
    }
    
    public EstadoDelJuegoActual estadoActual(){
        EstadoDelJuegoActual result = null;
        try {
            List<HashMap<String,Object>> query = Conexion.getInstancia()
                    .consultar("SELECT * FROM juego LIMIT 1");
            
            if (query != null && !query.isEmpty()) {
                result = new EstadoDelJuegoActual();
                
                result.setGanado(Double.valueOf(query.get(0).get("ganado").toString()).intValue());
                result.setCreditos(Double.valueOf(query.get(0).get("creditos").toString()).intValue());
                result.setApuestaTotal(Double.valueOf(query.get(0).get("apuesta_total").toString()).intValue());
                result.setApuestaEnBolasExtra(Double.valueOf(query.get(0).get("apuesta_en_bolas_extra").toString()).intValue());
                result.setCarton1Habilitado(true);
                result.setCarton2Habilitado(Boolean.valueOf(query.get(0).get("carton2_habilitado").toString()));
                result.setCarton3Habilitado(Boolean.valueOf(query.get(0).get("carton3_habilitado").toString()));
                result.setCarton4Habilitado(Boolean.valueOf(query.get(0).get("carton4_habilitado").toString()));
                
                result.calcularCantidadDeCartonesHabilitados(); //Cuenta la cantidad de cartones habilitados
                result.calcularApuestaIndividual();
            }
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    public class EstadoDelJuegoActual implements Serializable{
        private int creditos;
        private int apuestaTotal;
        private int apuestaIndividual;
        private int apuestaEnBolasExtra;
        private boolean carton1Habilitado;
        private boolean carton2Habilitado;
        private boolean carton3Habilitado;
        private boolean carton4Habilitado;
        private int ganado;
        private int cantidadDeCartonesHabilitados;

        public void calcularApuestaIndividual(){
            this.calcularCantidadDeCartonesHabilitados();
            this.apuestaIndividual = this.apuestaTotal / this.cantidadDeCartonesHabilitados;
        }
        
        public int getCreditos() {
            return creditos;
        }

        public void setCreditos(int creditos) {
            this.creditos = creditos;
        }

        public int getApuestaTotal() {
            return apuestaTotal;
        }

        public void setApuestaTotal(int apuestaTotal) {
            this.apuestaTotal = apuestaTotal;
        }

        public int getApuestaEnBolasExtra() {
            return apuestaEnBolasExtra;
        }

        public void setApuestaEnBolasExtra(int apuestaEnBolasExtra) {
            this.apuestaEnBolasExtra = apuestaEnBolasExtra;
        }

        public int getGanado() {
            return ganado;
        }

        public int getApuestaIndividual() {
            return apuestaIndividual;
        }

        public void setApuestaIndividual(int apuestaIndividual) {
            this.apuestaIndividual = apuestaIndividual;
        }

        public int getCantidadDeCartonesHabilitados() {
            return cantidadDeCartonesHabilitados;
        }

        public void calcularCantidadDeCartonesHabilitados(){
            this.cantidadDeCartonesHabilitados = 1 + (this.carton2Habilitado ? 1 : 0) + (this.carton3Habilitado? 1:0)
                    + (this.carton4Habilitado? 1:0);
        }

        public boolean isCarton1Habilitado() {
            return carton1Habilitado;
        }

        public void setCarton1Habilitado(boolean carton1Habilitado) {
            this.carton1Habilitado = carton1Habilitado;
        }

        public boolean isCarton2Habilitado() {
            return carton2Habilitado;
        }

        public void setCarton2Habilitado(boolean carton2Habilitado) {
            this.carton2Habilitado = carton2Habilitado;
        }

        public boolean isCarton3Habilitado() {
            return carton3Habilitado;
        }

        public void setCarton3Habilitado(boolean carton3Habilitado) {
            this.carton3Habilitado = carton3Habilitado;
        }

        public boolean isCarton4Habilitado() {
            return carton4Habilitado;
        }

        public void setCarton4Habilitado(boolean carton4Habilitado) {
            this.carton4Habilitado = carton4Habilitado;
        }

        public void setGanado(int ganado) {
            this.ganado = ganado;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final EstadoDelJuegoActual other = (EstadoDelJuegoActual) obj;
            if (this.creditos != other.creditos) {
                return false;
            }
            if (this.apuestaTotal != other.apuestaTotal) {
                return false;
            }
            if (this.apuestaEnBolasExtra != other.apuestaEnBolasExtra) {
                return false;
            }
            if (this.carton1Habilitado != other.carton1Habilitado) {
                return false;
            }
            if (this.carton2Habilitado != other.carton2Habilitado) {
                return false;
            }
            if (this.carton3Habilitado != other.carton3Habilitado) {
                return false;
            }
            if (this.carton4Habilitado != other.carton4Habilitado) {
                return false;
            }
            if (this.ganado != other.ganado) {
                return false;
            }
            return true;
        }
        
        
    }
}
