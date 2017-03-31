/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.protocol.b1.enumeraciones;

/**
 *
 * @author bingo
 */
public enum Pais {
    
    DESCONOCIDO(0),
    ARGENTINA(1),
    AUSTRALIA(2),
    AUSTRIA(3),
    BELGICA(4),
    BRASIL(5),
    BULGARIA(6),
    CANADA(7),
    COLOMBIA(8),
    CHIPRE(9),
    CHECOESLOVAQUIA(10),
    DINAMARCA(11),
    FINLANDIA(12),
    FRANCIA(13),
    ALEMANIA(14),
    GRAN_BRETANNIA(15),
    GIBRALTAR(16),
    GRECIA(17),
    GUERNSEY(18),
    HUNGRIA(19),
    IRLANDA(20),
    ITALIA(21),
    JERSEY(22),
    LUXEMBURGO(23),
    MALTA(24),
    MEXICO(25),
    MARRUECOS(26),
    NORUEGA(27),
    POLONIA(28),
    PORTUGAL(29),
    RUMANIA(30),
    RUSIA(31),
    ESPANNIA(32),
    SUDAFRICA(33),
    SUECIA(34),
    SUIZA(35),
    TURQUIA(36),
    ESTADOS_UNIDOS(37),
    HOLANDA(38),
    EUROPA(39)
    
    ;
    
    private final int value;
    
    Pais(int value){
        this.value = value;
    }

    public int value(){
        return this.value;
    }
}
