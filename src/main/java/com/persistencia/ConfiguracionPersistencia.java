/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.persistencia;

import java.io.Serializable;
import java.util.Map;
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
    private int indiceConfiguracionCostoBolaExtra;
    private boolean utilizarBolasExtraGratis;
    
    private boolean utilizarPremiosFijosBonus;
    private boolean utilizarPremiosVariablesBonus;
    private Map<Integer,Integer> premiosFijosBonus;
    private Map<Integer,Integer> premiosVariablesBonus;
    private int cantidadDePremiosEnBonus;
    
    //Jugador
    private int indiceConfiguracionJugadores;
    private Integer[] creditosMaximosPorPerfil;
    private Double[] probabilidadDeApostarPorPerfil;
    private Double[] probabilidadDeComprarBolasExtra;

    public Map<Integer, Integer> getPremiosFijosBonus() {
        return premiosFijosBonus;
    }

    public boolean isUtilizarBolasExtraGratis() {
        return utilizarBolasExtraGratis;
    }

    public void setUtilizarBolasExtraGratis(boolean utilizarBolasExtraGratis) {
        this.utilizarBolasExtraGratis = utilizarBolasExtraGratis;
    }

    public void setPremiosFijosBonus(Map<Integer, Integer> premiosFijosBonus) {
        this.premiosFijosBonus = premiosFijosBonus;
    }

    public Map<Integer, Integer> getPremiosVariablesBonus() {
        return premiosVariablesBonus;
    }

    public void setPremiosVariablesBonus(Map<Integer, Integer> premiosVariablesBonus) {
        this.premiosVariablesBonus = premiosVariablesBonus;
    }
    
    public int getIndiceConfiguracionCostoBolaExtra() {
        return indiceConfiguracionCostoBolaExtra;
    }

    public void setIndiceConfiguracionCostoBolaExtra(int indiceConfiguracionCostoBolaExtra) {
        this.indiceConfiguracionCostoBolaExtra = indiceConfiguracionCostoBolaExtra;
    }

    public int getIndiceConfiguracionJugadores() {
        return indiceConfiguracionJugadores;
    }

    public void setIndiceConfiguracionJugadores(int indiceConfiguracionJugadores) {
        this.indiceConfiguracionJugadores = indiceConfiguracionJugadores;
    }

    public Integer[] getCreditosMaximosPorPerfil() {
        return creditosMaximosPorPerfil;
    }

    public void setCreditosMaximosPorPerfil(Integer[] creditosMaximosPorPerfil) {
        this.creditosMaximosPorPerfil = creditosMaximosPorPerfil;
    }

    public Double[] getProbabilidadDeApostarPorPerfil() {
        return probabilidadDeApostarPorPerfil;
    }

    public void setProbabilidadDeApostarPorPerfil(Double[] probabilidadDeApostarPorPerfil) {
        this.probabilidadDeApostarPorPerfil = probabilidadDeApostarPorPerfil;
    }

    public Double[] getProbabilidadDeComprarBolasExtra() {
        return probabilidadDeComprarBolasExtra;
    }

    public void setProbabilidadDeComprarBolasExtra(Double[] probabilidadDeComprarBolasExtra) {
        this.probabilidadDeComprarBolasExtra = probabilidadDeComprarBolasExtra;
    }

    public int getCantidadDePremiosEnBonus() {
        return cantidadDePremiosEnBonus;
    }

    public void setCantidadDePremiosEnBonus(int cantidadDePremiosEnBonus) {
        this.cantidadDePremiosEnBonus = cantidadDePremiosEnBonus;
    }

    public boolean isUtilizarPremiosFijosBonus() {
        return utilizarPremiosFijosBonus;
    }

    public void setUtilizarPremiosFijosBonus(boolean utilizarPremiosFijosBonus) {
        this.utilizarPremiosFijosBonus = utilizarPremiosFijosBonus;
    }

    public boolean isUtilizarPremiosVariablesBonus() {
        return utilizarPremiosVariablesBonus;
    }

    public void setUtilizarPremiosVariablesBonus(boolean utilizarPremiosVariablesBonus) {
        this.utilizarPremiosVariablesBonus = utilizarPremiosVariablesBonus;
    }

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
