/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.persistencia;

import com.bingo.entidades.FiguraPago;
import com.bingo.entidades.TablaDePago;
import com.bingo.fabricas.FiguraPagoFactoria;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
public class PersistenciaJson {

    private static PersistenciaJson instancia;
    
    private ConfiguracionPersistencia config;
    
    private PersistenciaJson() throws IOException{
        crearConfiguracion();
    }

    public static PersistenciaJson getInstancia() throws IOException {
        if (instancia == null) {
            instancia = new PersistenciaJson();
        }
        return instancia;
    }
    
    public List<TablaDePago> tablasDepago(String ruta) throws FileNotFoundException, IOException{
        List<TablaDePago> result = null;
        System.out.println("Ruta de la tabla de pagos: " + ruta);
        if (ruta == null) {
            return null;
        }
        
        if (ruta.isEmpty()) {
            return null;
        }
        
        if (!new File(ruta).exists()) {
            ruta = "tablasDePago.json";
            try (Writer writer = new FileWriter(ruta)) {
                
                List<TablaDePago> tabla = new ArrayList<>();
                tabla.add(new TablaDePago(FiguraPagoFactoria.cartones()));
                
                Gson gson = new GsonBuilder().create();
                gson.toJson(tabla, writer);
            }
        }
        
        Gson gson = new Gson();
        Type tipo = new TypeToken<List<TablaDePago>>() {}.getType();
        JsonReader reader = new JsonReader(new FileReader(ruta));
        result = gson.fromJson(reader, tipo); // Lista cargada
        return result;
    }
    
    public List<TablaDePago> tablasDePago() throws FileNotFoundException, IOException{
        return this.tablasDepago(config.getRutaDeLasTablasDePago());
    }
    
    public void persistir(List<TablaDePago> tabla, String rutaCompleta) throws IOException{
        if (rutaCompleta == null || rutaCompleta.isEmpty()) {
            throw new NullPointerException("Ruta invalida, no puede ser vacia");
        }
        if (!rutaCompleta.contains(".json")) {
            throw new InvalidParameterException("No se detectó la extensión .json en su archivo");
        }
        try (Writer writer = new FileWriter(rutaCompleta)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(tabla, writer);
            writer.flush();
            writer.close();
        }
    }
    
    public void persistir(List<TablaDePago> tabla) throws IOException{
        if (this.config != null && this.config.getRutaDeLasTablasDePago() != null && 
                !this.config.getRutaDeLasTablasDePago().isEmpty()) {
            try (Writer writer = new FileWriter(this.config.getRutaDeLasTablasDePago())) {
                Gson gson = new GsonBuilder().create();
                gson.toJson(tabla, writer);
                writer.flush();
                writer.close();
            }
        }
        else{
            throw new NullPointerException("La ruta de las tablas de pago no fue encontrada");
        }
    }
    
    /**
     * Crea la configuracion de acceso a la base de datos JSON
     */
    private void crearConfiguracion() throws IOException{
        if (new File("config.db").exists()) {
            System.out.println("Existe el archivo config.db");
            Gson gson = new Gson();
            JsonReader reader = new JsonReader(new FileReader("config.db"));
            config = gson.fromJson(reader, ConfiguracionPersistencia.class); // Lista cargada 
        }
        else{
            //No existe el archivo, crearlo
            System.out.println("No existe el archivo config.db, creando...");
            try (Writer writer = new FileWriter("config.db")) {
                
                config = new ConfiguracionPersistencia();
                config.setRutaDeLasTablasDePago("tablasDePago.json");
                
                Gson gson = new GsonBuilder().create();
                gson.toJson(config, writer);
                
                writer.flush();
                writer.close();
            }
        }
    }
}