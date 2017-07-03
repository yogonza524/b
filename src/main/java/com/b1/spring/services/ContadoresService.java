/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.b1.spring.services;

import com.dao.Conexion;
import java.io.Serializable;
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
    
    public Contadores contadores(){
        Contadores result = null;
        try {
            List<HashMap<String,Object>> query = com.protocol.dao.Conexion.getInstancia().consultar("SELECT * FROM contadores LIMIT 1");
            if (query != null && !query.isEmpty()) {
                result = new Contadores();
                
                result.setCantidadDeBilletesDe$1(Double.valueOf(query.get(0).get("cantidad_de_billetes_de_$1").toString()).intValue());
                result.setCantidadDeBilletesDe$2(Double.valueOf(query.get(0).get("cantidad_de_billetes_de_$2").toString()).intValue());
                result.setCantidadDeBilletesDe$5(Double.valueOf(query.get(0).get("cantidad_de_billetes_de_$5").toString()).intValue());
                result.setCantidadDeBilletesDe$10(Double.valueOf(query.get(0).get("cantidad_de_billetes_de_$10").toString()).intValue());
                result.setCantidadDeBilletesDe$20(Double.valueOf(query.get(0).get("cantidad_de_billetes_de_$20").toString()).intValue());
                result.setCantidadDeBilletesDe$50(Double.valueOf(query.get(0).get("cantidad_de_billetes_de_$50").toString()).intValue());
                result.setCantidadDeBilletesDe$100(Double.valueOf(query.get(0).get("cantidad_de_billetes_de_$100").toString()).intValue());
                result.setCantidadDeBilletesDe$200(Double.valueOf(query.get(0).get("cantidad_de_billetes_de_$200").toString()).intValue());
                result.setCantidadDeBilletesDe$500(Double.valueOf(query.get(0).get("cantidad_de_billetes_de_$500").toString()).intValue());
                
                result.setCantidadDeJugadas(Double.valueOf(query.get(0).get("cantidad_de_juegos_jugados").toString()).intValue());
                result.setCantidadDeJugadasConAlgunaVictoria(Double.valueOf(query.get(0).get("cantidad_de_juegos_con_victorias").toString()).intValue());
                result.setCantidadDeJugadasSinVictorias(Double.valueOf(query.get(0).get("cantidad_de_juegos_sin_victorias").toString()).intValue());
                result.setCantidadDeJugadasDesdeElEncendido(Double.valueOf(query.get(0).get("cantidad_de_juegos_desde_el_encendido").toString()).intValue());
                result.setCantidadDeJugadasDesdeLaAperturaOcierreDeLaPuertaPrincipal(Double.valueOf(query.get(0).get("cantidad_de_juegos_desde_apertura_o_cierre_de_puerta_principal").toString()).intValue());
                result.setCantidadDeJugadasDesdeLaAperturaDeLaPuertaPrincipal(Double.valueOf(query.get(0).get("cantidad_de_veces_que_se_abrio_la_puerta_principal").toString()).intValue());
            }
        } catch (Exception e) {
            logService.log(e.getMessage());
        }
        return result;
    }
    
    public class Contadores implements Serializable{
        private int cantidadDeBilletesDe$1;
        private int cantidadDeBilletesDe$2;
        private int cantidadDeBilletesDe$5;
        private int cantidadDeBilletesDe$10;
        private int cantidadDeBilletesDe$20;
        private int cantidadDeBilletesDe$50;
        private int cantidadDeBilletesDe$100;
        private int cantidadDeBilletesDe$200;
        private int cantidadDeBilletesDe$500;
        
        private int cantidadDeJugadas;
        private int cantidadDeJugadasSinVictorias;
        private int cantidadDeJugadasConAlgunaVictoria;
        private int cantidadDeJugadasDesdeElEncendido;
        private int cantidadDeJugadasDesdeLaAperturaOcierreDeLaPuertaPrincipal;
        private int cantidadDeJugadasDesdeLaAperturaDeLaPuertaPrincipal;

        public int getCantidadDeBilletesDe$1() {
            return cantidadDeBilletesDe$1;
        }

        public void setCantidadDeBilletesDe$1(int cantidadDeBilletesDe$1) {
            this.cantidadDeBilletesDe$1 = cantidadDeBilletesDe$1;
        }

        public int getCantidadDeBilletesDe$2() {
            return cantidadDeBilletesDe$2;
        }

        public void setCantidadDeBilletesDe$2(int cantidadDeBilletesDe$2) {
            this.cantidadDeBilletesDe$2 = cantidadDeBilletesDe$2;
        }

        public int getCantidadDeBilletesDe$5() {
            return cantidadDeBilletesDe$5;
        }

        public void setCantidadDeBilletesDe$5(int cantidadDeBilletesDe$5) {
            this.cantidadDeBilletesDe$5 = cantidadDeBilletesDe$5;
        }

        public int getCantidadDeBilletesDe$10() {
            return cantidadDeBilletesDe$10;
        }

        public void setCantidadDeBilletesDe$10(int cantidadDeBilletesDe$10) {
            this.cantidadDeBilletesDe$10 = cantidadDeBilletesDe$10;
        }

        public int getCantidadDeBilletesDe$20() {
            return cantidadDeBilletesDe$20;
        }

        public void setCantidadDeBilletesDe$20(int cantidadDeBilletesDe$20) {
            this.cantidadDeBilletesDe$20 = cantidadDeBilletesDe$20;
        }

        public int getCantidadDeBilletesDe$50() {
            return cantidadDeBilletesDe$50;
        }

        public void setCantidadDeBilletesDe$50(int cantidadDeBilletesDe$50) {
            this.cantidadDeBilletesDe$50 = cantidadDeBilletesDe$50;
        }

        public int getCantidadDeBilletesDe$100() {
            return cantidadDeBilletesDe$100;
        }

        public void setCantidadDeBilletesDe$100(int cantidadDeBilletesDe$100) {
            this.cantidadDeBilletesDe$100 = cantidadDeBilletesDe$100;
        }

        public int getCantidadDeBilletesDe$200() {
            return cantidadDeBilletesDe$200;
        }

        public void setCantidadDeBilletesDe$200(int cantidadDeBilletesDe$200) {
            this.cantidadDeBilletesDe$200 = cantidadDeBilletesDe$200;
        }

        public int getCantidadDeBilletesDe$500() {
            return cantidadDeBilletesDe$500;
        }

        public void setCantidadDeBilletesDe$500(int cantidadDeBilletesDe$500) {
            this.cantidadDeBilletesDe$500 = cantidadDeBilletesDe$500;
        }

        public int getCantidadDeJugadas() {
            return cantidadDeJugadas;
        }

        public void setCantidadDeJugadas(int cantidadDeJugadas) {
            this.cantidadDeJugadas = cantidadDeJugadas;
        }

        public int getCantidadDeJugadasSinVictorias() {
            return cantidadDeJugadasSinVictorias;
        }

        public void setCantidadDeJugadasSinVictorias(int cantidadDeJugadasSinVictorias) {
            this.cantidadDeJugadasSinVictorias = cantidadDeJugadasSinVictorias;
        }

        public int getCantidadDeJugadasConAlgunaVictoria() {
            return cantidadDeJugadasConAlgunaVictoria;
        }

        public void setCantidadDeJugadasConAlgunaVictoria(int cantidadDeJugadasConAlgunaVictoria) {
            this.cantidadDeJugadasConAlgunaVictoria = cantidadDeJugadasConAlgunaVictoria;
        }

        public int getCantidadDeJugadasDesdeElEncendido() {
            return cantidadDeJugadasDesdeElEncendido;
        }

        public void setCantidadDeJugadasDesdeElEncendido(int cantidadDeJugadasDesdeElEncendido) {
            this.cantidadDeJugadasDesdeElEncendido = cantidadDeJugadasDesdeElEncendido;
        }

        public int getCantidadDeJugadasDesdeLaAperturaOcierreDeLaPuertaPrincipal() {
            return cantidadDeJugadasDesdeLaAperturaOcierreDeLaPuertaPrincipal;
        }

        public void setCantidadDeJugadasDesdeLaAperturaOcierreDeLaPuertaPrincipal(int cantidadDeJugadasDesdeLaAperturaOcierreDeLaPuertaPrincipal) {
            this.cantidadDeJugadasDesdeLaAperturaOcierreDeLaPuertaPrincipal = cantidadDeJugadasDesdeLaAperturaOcierreDeLaPuertaPrincipal;
        }

        public int getCantidadDeJugadasDesdeLaAperturaDeLaPuertaPrincipal() {
            return cantidadDeJugadasDesdeLaAperturaDeLaPuertaPrincipal;
        }

        public void setCantidadDeJugadasDesdeLaAperturaDeLaPuertaPrincipal(int cantidadDeJugadasDesdeLaAperturaDeLaPuertaPrincipal) {
            this.cantidadDeJugadasDesdeLaAperturaDeLaPuertaPrincipal = cantidadDeJugadasDesdeLaAperturaDeLaPuertaPrincipal;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + this.cantidadDeBilletesDe$1;
            hash = 67 * hash + this.cantidadDeBilletesDe$2;
            hash = 67 * hash + this.cantidadDeBilletesDe$5;
            hash = 67 * hash + this.cantidadDeBilletesDe$10;
            hash = 67 * hash + this.cantidadDeBilletesDe$20;
            hash = 67 * hash + this.cantidadDeBilletesDe$50;
            hash = 67 * hash + this.cantidadDeBilletesDe$100;
            hash = 67 * hash + this.cantidadDeBilletesDe$200;
            hash = 67 * hash + this.cantidadDeBilletesDe$500;
            return hash;
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
            final Contadores other = (Contadores) obj;
            if (this.cantidadDeBilletesDe$1 != other.cantidadDeBilletesDe$1) {
                return false;
            }
            if (this.cantidadDeBilletesDe$2 != other.cantidadDeBilletesDe$2) {
                return false;
            }
            if (this.cantidadDeBilletesDe$5 != other.cantidadDeBilletesDe$5) {
                return false;
            }
            if (this.cantidadDeBilletesDe$10 != other.cantidadDeBilletesDe$10) {
                return false;
            }
            if (this.cantidadDeBilletesDe$20 != other.cantidadDeBilletesDe$20) {
                return false;
            }
            if (this.cantidadDeBilletesDe$50 != other.cantidadDeBilletesDe$50) {
                return false;
            }
            if (this.cantidadDeBilletesDe$100 != other.cantidadDeBilletesDe$100) {
                return false;
            }
            if (this.cantidadDeBilletesDe$200 != other.cantidadDeBilletesDe$200) {
                return false;
            }
            if (this.cantidadDeBilletesDe$500 != other.cantidadDeBilletesDe$500) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Contadores{" + "cantidadDeBilletesDe$1=" + cantidadDeBilletesDe$1 + ", cantidadDeBilletesDe$2=" + cantidadDeBilletesDe$2 + ", cantidadDeBilletesDe$5=" + cantidadDeBilletesDe$5 + ", cantidadDeBilletesDe$10=" + cantidadDeBilletesDe$10 + ", cantidadDeBilletesDe$20=" + cantidadDeBilletesDe$20 + ", cantidadDeBilletesDe$50=" + cantidadDeBilletesDe$50 + ", cantidadDeBilletesDe$100=" + cantidadDeBilletesDe$100 + ", cantidadDeBilletesDe$200=" + cantidadDeBilletesDe$200 + ", cantidadDeBilletesDe$500=" + cantidadDeBilletesDe$500 + '}';
        }
    }
}
