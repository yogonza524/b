/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.b1.configuracion;

import com.bingo.entidades.TablaDePago;
import com.bingo.persistencia.Conexion;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Gonzalo
 */
public class Configuracion implements Serializable{
    
    private final static String NOMBRE = "c.db";
    private boolean billetero;
    private int puertoBilletero;
    private String RutaBingoBot;
    private String idioma;
    private double porcentajeDelMayorPorSalir;
    private int umbralMinimoParaLiberarBolasExtra;
    private boolean utilizarUmbralParaLiberarBolasExtra;

    public boolean isUtilizarUmbralParaLiberarBolasExtra() {
        return utilizarUmbralParaLiberarBolasExtra;
    }

    public void setUtilizarUmbralParaLiberarBolasExtra(boolean utilizarUmbralParaLiberarBolasExtra) {
        this.utilizarUmbralParaLiberarBolasExtra = utilizarUmbralParaLiberarBolasExtra;
    }

    public double getPorcentajeDelMayorPorSalir() {
        return porcentajeDelMayorPorSalir;
    }

    public void setPorcentajeDelMayorPorSalir(double porcentajeDelMayorPorSalir) {
        this.porcentajeDelMayorPorSalir = porcentajeDelMayorPorSalir;
    }

    public int getUmbralMinimoParaLiberarBolasExtra() {
        return umbralMinimoParaLiberarBolasExtra;
    }

    public void setUmbralMinimoParaLiberarBolasExtra(int umbralMinimoParaLiberarBolasExtra) {
        this.umbralMinimoParaLiberarBolasExtra = umbralMinimoParaLiberarBolasExtra;
    }
    
    public boolean isBilletero() {
        return billetero;
    }

    public void setBilletero(boolean billetero) {
        this.billetero = billetero;
    }

    public int getPuertoBilletero() {
        return puertoBilletero;
    }

    public void setPuertoBilletero(int puertoBilletero) {
        this.puertoBilletero = puertoBilletero;
    }

    public String getRutaBingoBot() {
        return RutaBingoBot;
    }

    public void setRutaBingoBot(String RutaBingoBot) {
        this.RutaBingoBot = RutaBingoBot;
    }

    public String getIdioma() {
        return idioma;
    }

    public void setIdioma(String idioma) {
        this.idioma = idioma;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.billetero ? 1 : 0);
        hash = 71 * hash + this.puertoBilletero;
        hash = 71 * hash + Objects.hashCode(this.RutaBingoBot);
        hash = 71 * hash + Objects.hashCode(this.idioma);
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
        final Configuracion other = (Configuracion) obj;
        if (this.billetero != other.billetero) {
            return false;
        }
        if (this.puertoBilletero != other.puertoBilletero) {
            return false;
        }
        if (!Objects.equals(this.RutaBingoBot, other.RutaBingoBot)) {
            return false;
        }
        if (!Objects.equals(this.idioma, other.idioma)) {
            return false;
        }
        return true;
    }
    
    public static boolean existe(){
        return new File(NOMBRE).exists();
    }
    
    public static void crearArchivoDeConfiguracion() throws IOException{
        if (!existe()) {
            try (Writer writer = new FileWriter(NOMBRE)) {
                Gson gson = new GsonBuilder().create();
                
                Configuracion config = new Configuracion();
                
                try {
                    //Cargar los datos
                    List<HashMap<String,Object>> query = Conexion.getInstancia().consultar("SELECT * FROM configuracion");
                    if (query != null && !query.isEmpty()) {
                        config.setUmbralMinimoParaLiberarBolasExtra(
                                query.get(0).get("umbral_minimo_para_liberar_bolas_extra") != null ?
                                Double.valueOf(query.get(0).get("umbral_minimo_para_liberar_bolas_extra").toString()).intValue() : 9);
                        config.setBilletero(false);
                        config.setPuertoBilletero(-1);
                        config.setIdioma("PORTUGUESE");
                        
                        Boolean utilizarUmbral = query.get(0).get("utilizar_umbral_para_liberar_bolas_extra") != null ? Boolean.valueOf(query.get(0).get("utilizar_umbral_para_liberar_bolas_extra").toString()) : true;
                        
                        config.setUtilizarUmbralParaLiberarBolasExtra(utilizarUmbral);
                        Object rutaBingo = query.get(0).get("ruta_bingobot");
                        if (rutaBingo != null && rutaBingo.toString().contains(".swf")) {
                            config.setRutaBingoBot(rutaBingo.toString());
                        }
                        else{
                            config.setRutaBingoBot("bingo.swf");
                        }
                        
                        if (query.get(0).get("porcentaje_del_premio_mayor_por_salir") != null) {
                            Double porcentajeDelMayor = Double.valueOf(query.get(0).get("porcentaje_del_premio_mayor_por_salir").toString());
                            config.setPorcentajeDelMayorPorSalir(porcentajeDelMayor.intValue());
                        }
                        else{
                            config.setPorcentajeDelMayorPorSalir(4);
                        }
                    }
                    gson.toJson(config, writer);
                    
                } catch (SQLException ex) {
                    Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public static Configuracion leer(){
        Gson gson = new Gson();
        Type tipo = new TypeToken<Configuracion>() {}.getType();
        JsonReader reader;
        try {
            reader = new JsonReader(new FileReader(NOMBRE));
            Configuracion result = gson.fromJson(reader, tipo); 
            
            return result;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Configuracion.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
