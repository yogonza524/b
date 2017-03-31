/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.protocol.b1.repositorios;

import com.bingo.persistencia.Conexion;
import com.protocol.b1.enumeraciones.Idioma;
import com.protocol.b1.enumeraciones.Pais;
import com.protocol.b1.interfaces.RepositorioB1;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public class RepositorioB1EnPostgres implements RepositorioB1{

    @Override
    public int getNumeroDeMaquina() {
        int response = -1;
        try {
            List<HashMap<String, Object>> result =Conexion.getInstancia()
                    .consultar("SELECT id_maquina as id FROM b1 WHERE b1.id = 0");
            if (result != null && result.size() > 0) {
                response = Integer.valueOf(result.get(0).get("id").toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }   
        return response;
    }

    @Override
    public void setNumeroDeMaquina(int numeroDeMaquina) {
        try {
            Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET id_maquina = " + numeroDeMaquina
                            + " WHERE b1.id = 0");
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void setIdiomasSoportados(List<Idioma> idiomas) {
        
    }

    @Override
    public List<Idioma> getIdiomasSoportados() {
        List<Idioma> response = null;
        try {
            List<HashMap<String, Object>> result = Conexion.getInstancia()
                    .consultar("SELECT * FROM idiomas");
            if (result != null && result.size() > 0) {
                response = new ArrayList<>();
                for (int i = 0; i < result.size(); i++) {
                    for(Map.Entry<String,Object> idioma : result.get(i).entrySet()){
                        Idioma soportado = Idioma.valueOf(idioma.getValue().toString());
                        response.add(soportado);
                    }
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public void setIdiomaActual(Idioma idioma) {
        try {
            Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET idioma_seleccionado = '" + idioma.name()
                            + "' WHERE b1.id = 0");
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public Idioma getIdiomaActual() {
        try {
            List<HashMap<String, Object>> result = Conexion.getInstancia()
                    .consultar("SELECT idioma_seleccionado as idioma FROM b1 WHERE b1.id = 0");
            if (result != null && result.size() > 0) {
                return Idioma.valueOf(result.get(0).get("idioma").toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$1() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET billetes_de_$1 = "
                            + "billetes_de_$1 + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$2() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET billetes_de_$2 = "
                            + "billetes_de_$2 + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$5() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET billetes_de_$5 = "
                            + "billetes_de_$5 + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$10() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET billetes_de_$10 = "
                            + "billetes_de_$10 + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$20() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET billetes_de_$20 = "
                            + "billetes_de_$20 + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$50() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET billetes_de_$50 = "
                            + "billetes_de_$50 + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$100() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET billetes_de_$100 = "
                            + "billetes_de_$100 + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$200() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET billetes_de_$200 = "
                            + "billetes_de_$200 + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$500() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET billetes_de_$500 = "
                            + "billetes_de_$500 + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarIngresoDeBilletesDe$1000() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET billetes_de_$1000 = "
                            + "billetes_de_$1000 + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarCantidadDeJuegosJugados() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET cantidad_de_juegos = "
                            + "cantidad_de_juegos + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarCantidadDeJuegosGanados() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET cantidad_de_juegos_ganados = "
                            + "cantidad_de_juegos_ganados + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarCantidadDeJuegosPerdidos() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET cantidad_de_juegos_perdidos = "
                            + "cantidad_de_juegos_perdidos + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarCantidadDeJuegosDesdeElEncendido() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET cantidad_de_juegos_desde_el_encendido = "
                            + "cantidad_de_juegos_desde_el_encendido + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean incrementarCantidadDeJuegosDesdeQueSeCerroLaPuertaPrincipal() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET cantidad_de_juegos_desde_que_se_cerro_la_puerta_principal = "
                            + "cantidad_de_juegos_desde_que_se_cerro_la_puerta_principal + 1 "
                            + "WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public void colocarCodigoDePais(Pais pais) {
        
    }

    @Override
    public Pais codigoDePais() {
        Pais response = null;
        try {
            List<HashMap<String, Object>> result = Conexion.getInstancia()
                    .consultar("SELECT pais "
                            + "FROM b1 WHERE b1.id = 0" );
            if (result != null && result.size() > 0) {
                response = Pais.valueOf(result.get(0).get("pais").toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean entrarEnModoMantenimiento() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET modo_mantenimiento = TRUE WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean salirDeModoMantenimiento() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET modo_mantenimiento = FALSE WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean obtenerModoMantenimiento() {
        boolean response = false;
        try {
            List<HashMap<String, Object>> result = Conexion.getInstancia()
                    .consultar("SELECT modo_mantenimiento FROM b1 WHERE b1.id = 0");
            if (result != null && result.size() > 0) {
                response = Boolean.valueOf(result.get(0).get("modo_mantenimiento").toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean deshabilitarSonido() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET sonido = FALSE WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean habilitarSonido() {
        boolean response = false;
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET sonido = TRUE WHERE b1.id = 0");
            response = filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean sonidoHabilitado() {
        boolean response = false;
        try {
            List<HashMap<String, Object>> result = Conexion.getInstancia()
                    .consultar("SELECT sonido FROM b1 WHERE b1.id = 0");
            if (result != null && result.size() > 0) {
                response = Boolean.valueOf(result.get(0).get("sonido").toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public int cantidadDeJuegosDesdeElEncendido() {
        int response = -1;
        try {
            List<HashMap<String, Object>> result = Conexion.getInstancia()
                    .consultar("SELECT cantidad_de_juegos_desde_el_encendido "
                            + "as cantidad FROM b1 WHERE b1.id = 0" );
            if (result != null && result.size() > 0) {
                response = Integer.valueOf(result.get(0).get("cantidad").toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public int cantidadDeJuegosDesdeQueSeCerroLaPuertaPrincipal() {
        int response = -1;
        try {
            List<HashMap<String, Object>> result = Conexion.getInstancia()
                    .consultar("SELECT cantidad_de_juegos_desde_que_se_cerro_la_puerta_principal "
                            + "as cantidad FROM b1 WHERE b1.id = 0" );
            if (result != null && result.size() > 0) {
                response = Integer.valueOf(result.get(0).get("cantidad").toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public boolean bloquearMaquina() {
        int filasAfectadas = 0;
        try {
            filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET maquina_bloqueada = true WHERE b1.id = 0");
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return filasAfectadas > 0;
    }

    @Override
    public boolean desbloquearmaquina() {
        try {
            int filasAfectadas = Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET maquina_bloqueada = false WHERE b1.id = 0");
            return filasAfectadas > 0;
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    @Override
    public boolean apagarMaquina() {
        System.exit(0);
        return true;
    }

    @Override
    public void colocarPorcentajeDeRetribucion(int porcentaje) {
        try {
            Conexion.getInstancia()
                    .actualizar("UPDATE b1 SET porcentaje_de_retribucion = " + porcentaje
                            + " WHERE b1.id = 0");
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public int porcentajeDeRetribucion() {
        int response = -1;
        try {
            List<HashMap<String, Object>> result = Conexion.getInstancia()
                    .consultar("SELECT porcentaje_de_retribucion as porcentaje FROM b1 WHERE b1.id = 0");
            if (result != null && result.size() > 0) {
                response = Integer.valueOf(result.get(0).get("porcentaje").toString());
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }

    @Override
    public void colocarNumeroDeSerieDeLaMaquina(String serial) {
        if (serial != null && !serial.isEmpty()) {
            try {
                Conexion.getInstancia()
                        .actualizar("UPDATE b1 SET numero_de_serie_de_la_maquina = '" + serial
                                + "' WHERE b1.id = 0");
                
            } catch (SQLException ex) {
                Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public String numeroDeSerieDeLaMaquina() {
        String response = "";
        try {
            List<HashMap<String, Object>> result = Conexion.getInstancia()
                    .consultar("SELECT numero_de_serie_de_la_maquina as serial FROM b1 LIMIT 1");
            if (result != null && result.size() > 0) {
                response = result.get(0).get("serial").toString();
            }
        } catch (SQLException ex) {
            Logger.getLogger(RepositorioB1EnPostgres.class.getName()).log(Level.SEVERE, null, ex);
        }
        return response;
    }
    
    
}
