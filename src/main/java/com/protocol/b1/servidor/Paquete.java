/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.protocol.b1.servidor;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.MalformedJsonException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Clase encargada de formatear los mensajes
 * soportados por le porotocolo B1
 * 
 * @author Gonzalo H. Mendoza
 * Web: http://idsoft.com.ar
 * Mail: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza?tab=profile
 */
public class Paquete implements Serializable{
    
    // <editor-fold defaultstate="collapsed" desc="Atributos">
    private int codigo;
    private String estado;
    private Map<String,Object> datos;
    //</editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">
    /**
     * Constructor por defecto
     */
    public Paquete() {}

    /**
     * Constructor avanzado
     * @param codigo numero entero positivo, indica el numero de metodo a disparar
     * @param estado cadena de caracteres, indica el tipo de mensaje
     * @param datos  mapa de cadenas de caracteres, indica los datos del mensaje
     */
    public Paquete(int codigo, String estado, Map<String, Object> datos) {
        this.codigo = codigo;
        this.estado = estado;
        this.datos = datos;
    }

    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Getters y Setters">

    /**
     * Coloca el número de código del paquete
     * @param codigo numero entero positivo, representa el número de método a disparar
     */
    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
    
    /**
     * Obtiene el número de código del paquete
     * @return numero entero positivo, indica el numero de método a disparar
     */
    public int getCodigo(){
        return codigo;
    }

    /**
     * Obtiene el estado del mensaje
     * @return
     */
    public String getEstado() {
        return estado;
    }

    /**
     * Coloca el estado del paquete
     * @param estado cadena de dcarcteres, indica el tipo del paquete
     */
    public void setEstado(String estado) {
        this.estado = estado;
    }

    /**
     * Obtiene el mapa de datos del paquete
     * @return datos del paquete (payload)
     */
    public Map<String, Object> getDatos() {
        return datos;
    }

    /**
     * Coloca el mapa de datos al paquete
     * @param datos mapa de datos (payload)
     */
    public void setDatos(Map<String, Object> datos) {
        this.datos = datos;
    }

    //</editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc="Metodos">
    /**
     * Convierte un paquete B1 a formato JSON estandar
     * @param paquete Entidad de encapsulamiento de información de B1
     * @return Cadena de caracteres en formato JSON estandar
     */
    public static String desdePaquete(Paquete paquete) {
        if (paquete != null) {
            return new Gson().toJson(paquete);
        }
        return "";
    }
    
    /**
     * Convierte una cadena de caracteres en formato JSON a
     * una entidad de información de encapsulamiento B1
     * @param paquete cadena de caracteres, JSON
     * @return Entidad de información B1
     * @throws com.google.gson.stream.MalformedJsonException
     */
    public static Paquete deJSON(String paquete) throws MalformedJsonException{
        if (paquete != null && !paquete.isEmpty()) {
            return new Gson().fromJson(paquete, Paquete.class);
        }
        return null;
    }
    
    
    //</editor-fold>

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + this.codigo;
        hash = 17 * hash + Objects.hashCode(this.estado);
        hash = 17 * hash + Objects.hashCode(this.datos);
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
        final Paquete other = (Paquete) obj;
        if (this.codigo != other.codigo) {
            return false;
        }
        if (!Objects.equals(this.estado, other.estado)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Paquete{" + "codigo=" + codigo + ", estado=" + estado + ", datos=" + datos + '}';
    }
    
    /**
     * Convierte esta instancia a JSON estandar
     * @return cadena de caracteres en formato JSON
     */
    public String aJSON(){
        return new Gson().toJson(this);
//        return new GsonBuilder().setPrettyPrinting().create().toJson(this);
    }
    
    /**
     * Clase encargada de generar los paquetes aplicando el Patron Builder
     */
    public static class PaqueteBuilder{
        private int codigo;
        private String estado;
        private final Map<String,Object> datos;
        
        /**
         * Constructor por defecto, carga los siguientes datos:
         * codigo = 0; estado = "desconocido", datos = {}
         */
        public PaqueteBuilder(){
            this.codigo = 0;
            this.estado = "desconocido";
            this.datos = new HashMap<>();
        }
        
        /**
         * Coloca el código del paquete, el mismo debe seguir el protocolo
         * B1
         * @param codigo numero entero positivo, definido en B1
         * @return PaqueteBuilder, patron builder
         */
        public PaqueteBuilder codigo(int codigo){
            this.codigo = codigo;
            return this;
        }
        
        /**
         * Coloca el estado del mensaje dependiendo los estados definidos en B1
         * @param estado cadena de caracteres, segun B1
         * @return PaqueteBuilder, patron builder
         */
        public PaqueteBuilder estado(String estado){
            this.estado = estado;
            return this;
        }
        
        /**
         * Coloca un dato en el mapa de datos del paquete
         * @param clave cadena de caracteres, unico
         * @param valor objeto que se asocia a la clave en el dato
         * @return PaqueteBuilder, patron builder
         */
        public PaqueteBuilder dato(String clave,Object valor){
            this.datos.put(clave, valor);
            return this;
        }
        
        /**
         * Crea el paquete a partir de las piezas individuales
         * @return Paquete con formato valido segun B1
         */
        public Paquete crear(){
            Paquete p = new Paquete();
            p.setCodigo(codigo);
            p.setEstado(estado);
            p.setDatos(datos);
            
            return p;
        }
    }
}
