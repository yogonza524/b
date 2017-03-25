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
public class ConfiguracionPersistencia implements Serializable{

    private String rutaDeLasTablasDePago;
    private boolean utilizarUmbral;
    private int umbralParaLiberarBolasExtra;
    private double factorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor;
    private int limiteMinimoGratis;
    private int limiteMaximoGratis;
    private boolean tournament;
    private double porcentajeParaTournament;
    private int indicePorcentajeTournamentCombo;

    public int getIndicePorcentajeTournamentCombo() {
        return indicePorcentajeTournamentCombo;
    }

    public void setIndicePorcentajeTournamentCombo(int indicePorcentajeTournamentCombo) {
        this.indicePorcentajeTournamentCombo = indicePorcentajeTournamentCombo;
    }

    public boolean isTournament() {
        return tournament;
    }

    public void setTournament(boolean tournament) {
        this.tournament = tournament;
    }

    public double getPorcentajeParaTournament() {
        return porcentajeParaTournament;
    }

    public void setPorcentajeParaTournament(double porcentajeParaTournament) {
        this.porcentajeParaTournament = porcentajeParaTournament;
    }
    
    public String getRutaDeLasTablasDePago() {
        return rutaDeLasTablasDePago;
    }

    public boolean isUtilizarUmbral() {
        return utilizarUmbral;
    }

    public void setUtilizarUmbral(boolean utilizarUmbral) {
        this.utilizarUmbral = utilizarUmbral;
    }

    public int getUmbralParaLiberarBolasExtra() {
        return umbralParaLiberarBolasExtra;
    }

    public void setUmbralParaLiberarBolasExtra(int umbralParaLiberarBolasExtra) {
        this.umbralParaLiberarBolasExtra = umbralParaLiberarBolasExtra;
    }

    public double getFactorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor() {
        return factorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor;
    }

    public void setFactorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor(double factorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor) {
        this.factorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor = factorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor;
    }

    public int getLimiteMinimoGratis() {
        return limiteMinimoGratis;
    }

    public void setLimiteMinimoGratis(int limiteMinimoGratis) {
        this.limiteMinimoGratis = limiteMinimoGratis;
    }

    public int getLimiteMaximoGratis() {
        return limiteMaximoGratis;
    }

    public void setLimiteMaximoGratis(int limiteMaximoGratis) {
        this.limiteMaximoGratis = limiteMaximoGratis;
    }

    public void setRutaDeLasTablasDePago(String rutaDeLasTablasDePago) {
        this.rutaDeLasTablasDePago = rutaDeLasTablasDePago;
    }

    public ConfiguracionPersistencia() {
    }

    public ConfiguracionPersistencia(String rutaDeLasTablasDePago, boolean utilizarUmbral, int umbralParaLiberarBolasExtra, int factorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor, int limiteMinimoGratis, int limiteMaximoGratis) {
        this.rutaDeLasTablasDePago = rutaDeLasTablasDePago;
        this.utilizarUmbral = utilizarUmbral;
        this.umbralParaLiberarBolasExtra = umbralParaLiberarBolasExtra;
        this.factorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor = factorDePorcentajeDeCostoDeBolaExtraSegunElPremioMayor;
        this.limiteMinimoGratis = limiteMinimoGratis;
        this.limiteMaximoGratis = limiteMaximoGratis;
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
