/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.bingo.entidades;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * 
 * @author Gonzalo H. Mendoza
 * email: yogonza524@gmail.com
 * StackOverflow: http://stackoverflow.com/users/5079517/gonza
 */
public class TablaDePago implements Serializable{

    private int numero;
    private List<FiguraPago> figuras;

    public TablaDePago() {
    }

    public TablaDePago(List<FiguraPago> figuras) {
        this.figuras = figuras;
    }

    public List<FiguraPago> getFiguras() {
        return figuras;
    }

    public void setFiguras(List<FiguraPago> figuras) {
        this.figuras = figuras;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    @Override
    public int hashCode() {
        int hash = 7;
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
        final TablaDePago other = (TablaDePago) obj;
        if (!Objects.equals(this.figuras, other.figuras)) {
            return false;
        }
        return true;
    }
    
    
}
