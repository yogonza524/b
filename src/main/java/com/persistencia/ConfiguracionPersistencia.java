/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.persistencia;

import java.io.Serializable;
import java.util.Objects;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
class ConfiguracionPersistencia implements Serializable{

    private String rutaDeLasTablasDePago;

    public String getRutaDeLasTablasDePago() {
        return rutaDeLasTablasDePago;
    }

    public void setRutaDeLasTablasDePago(String rutaDeLasTablasDePago) {
        this.rutaDeLasTablasDePago = rutaDeLasTablasDePago;
    }

    public ConfiguracionPersistencia() {
    }

    public ConfiguracionPersistencia(String rutaDeLasTablasDePago) {
        this.rutaDeLasTablasDePago = rutaDeLasTablasDePago;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.rutaDeLasTablasDePago);
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
        final ConfiguracionPersistencia other = (ConfiguracionPersistencia) obj;
        if (!Objects.equals(this.rutaDeLasTablasDePago, other.rutaDeLasTablasDePago)) {
            return false;
        }
        return true;
    }
    
    
}
