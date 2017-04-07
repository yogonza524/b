/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.core.bv20.model;

import java.io.Serializable;
import java.util.Arrays;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author gonza
 * Github: github.com/yogonza524
 * Web: http://idsoft.com.ar
 * mail: yogonza524@gmail.com
 */
public class Estado implements Serializable{
    
    private boolean depositoHabilitado;
    private final Boolean[] canalesInhibidos;

    /**
     * Constructor por defecto, habilita todos los canales por defecto
     */
    public Estado() {
        this.canalesInhibidos = new Boolean[16];
        for (int i = 0; i < canalesInhibidos.length; i++) {
            this.canalesInhibidos[i] = Boolean.TRUE;
        }
    }
    
    /**
     * Obtiene el canal inhibido dado por i
     * @param i numero entero positivo entre 1 y 16
     * @return TRUE si el canal i esta inhibido, FALSE en otro caso
     */
    public boolean getCanalInhibido(int i){
        if (i > 0 && i < this.canalesInhibidos.length + 1) {
            return canalesInhibidos[i-1];
        }
        return false;
    }
    
    /**
     * Coloca el valor del canal inhibido dado por i
     * @param i numero entero positivo entre 1 y 16
     * @param canal
     */
    public void setCanalInhibido(int i, boolean canal){
        if (i > 0 && i < this.canalesInhibidos.length + 1) {
            this.canalesInhibidos[i - 1] = canal;
        }
    }

    /**
     * Devuelve el estado del deposito
     * @return TRUE si esta habilitado, FALSE en otro caso
     */
    public boolean depositoHabilitado() {
        return depositoHabilitado;
    }

    /**
     *
     * @param depositoHabilitado
     */
    public void setDepositoHabilitado(boolean depositoHabilitado) {
        this.depositoHabilitado = depositoHabilitado;
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido1(){
        return this.canalesInhibidos[0];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido2(){
        return this.canalesInhibidos[1];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido3(){
        return this.canalesInhibidos[2];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido4(){
        return this.canalesInhibidos[3];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido5(){
        return this.canalesInhibidos[4];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido6(){
        return this.canalesInhibidos[5];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido7(){
        return this.canalesInhibidos[6];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido8(){
        return this.canalesInhibidos[7];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido9(){
        return this.canalesInhibidos[8];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido10(){
        return this.canalesInhibidos[9];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido11(){
        return this.canalesInhibidos[10];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido12(){
        return this.canalesInhibidos[11];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido13(){
        return this.canalesInhibidos[12];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido14(){
        return this.canalesInhibidos[13];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido15(){
        return this.canalesInhibidos[14];
    }
    
    /**
     *
     * @return
     */
    public boolean getCanalInhibido16(){
        return this.canalesInhibidos[15];
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (this.depositoHabilitado ? 1 : 0);
        hash = 47 * hash + Arrays.deepHashCode(this.canalesInhibidos);
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
        final Estado other = (Estado) obj;
        if (this.depositoHabilitado != other.depositoHabilitado) {
            return false;
        }
        if (!Arrays.deepEquals(this.canalesInhibidos, other.canalesInhibidos)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Estado{" + "depositoHabilitado=" + depositoHabilitado + ", canalesInhibidos=" + ArrayUtils.toString(canalesInhibidos) + '}';
    }
    
    
}
