/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bingo.enumeraciones;

/**
 *
 * @author Usuario
 */
public enum MetodoB1 {
    ID_MAQUINA,
    IDIOMAS_SOPORTADOS,
    IDIOMA_ACTUAL,
    CARTONES_ACTUALES,
    BOLAS,
    CONFIGURACION_JUEGO,
    BOLAS_VISIBLES,
    AUMENTAR_APUESTAS,
    DISMINUIR_APUESTAS,
    COBRAR,
    CAMBIAR_CARTONES,
    HABILITAR_CARTON,
    DESHABILITAR_CARTON,
    DENOMINACION_ACTUAL,
    GENERAR_BOLILLERO,
    COLOCAR_CREDITOS,
    PAGAR,
    GENERAR_BONUS,
    JUGAR,
    COLOCAR_APUESTAS,
    BOLAS_EXTRA_SELECCIONADAS,
    SELECCIONAR_BOLA_EXTRA,
    COSTO_BOLA_EXTRA,
    GANANCIAS_Y_CREDITOS,
    ENVIAR_CREDITOS_ACTUALES,
    BONUS,
    PREMIOS_OBTENIDOS_EN_BONUS,
    INFORMAR_GANANCIAS_EN_BONUS,
    CREDITOS_ACTUALIZADOS,
    ACTUALIZAR_ESTADO_DESDE_LA_VISTA,
    BLOQUEAR_BILLETERO,
    DESBLOQUEAR_BILLETERO,
    ACUMULAR_PARA_EL_JACKPOT,
    OTORGAR_JACKPOT,
    OBTENER_TOTAL_ACUMULADO_EN_JACKPOT,
    REINICIAR_ACUMULADO,
    HABILITAR_JACKPOT,
    NO_IMPLEMENTADO_AUN;
    
    public static String porCodigo(int codigo){
        String response = "NO_IMPLEMENTADO_AUN";
        switch(codigo){
            case 1: response = ID_MAQUINA.name(); break;
            case 3: response = IDIOMAS_SOPORTADOS.name(); break;
            case 4: response = IDIOMA_ACTUAL.name(); break;
            case 6: response = CARTONES_ACTUALES.name(); break;
            case 7: response = BOLAS.name(); break;
            case 10: response = CONFIGURACION_JUEGO.name(); break;
            case 11: response = BOLAS_VISIBLES.name(); break;
            case 14: response = AUMENTAR_APUESTAS.name(); break;
            case 15: response = DISMINUIR_APUESTAS.name(); break;
            case 16: response = COBRAR.name(); break;
            case 17: response = CAMBIAR_CARTONES.name(); break;
            case 18: response = HABILITAR_CARTON.name(); break;
            case 21: response = DESHABILITAR_CARTON.name(); break;
            case 22: response = DENOMINACION_ACTUAL.name(); break;
            case 24: response = GENERAR_BOLILLERO.name(); break;
            case 25: response = COLOCAR_CREDITOS.name(); break;
            case 26: response = PAGAR.name(); break;
            case 27: response = GENERAR_BONUS.name(); break;
            case 50: response = JUGAR.name(); break;
            //case 51: response = this.cargarCreditos(p); break;
            case 52: response = COLOCAR_APUESTAS.name(); break;
            case 60: response = BOLAS_EXTRA_SELECCIONADAS.name(); break;
            case 61: response = SELECCIONAR_BOLA_EXTRA.name(); break;
            case 62: response = COSTO_BOLA_EXTRA.name(); break;
            case 63: response = GANANCIAS_Y_CREDITOS.name(); break;
            case 120: response = ENVIAR_CREDITOS_ACTUALES.name(); break;
            case 121: response = BONUS.name(); break;
            case 122: response = PREMIOS_OBTENIDOS_EN_BONUS.name(); break;
            case 123: response = INFORMAR_GANANCIAS_EN_BONUS.name(); break;
            case 124: response = CREDITOS_ACTUALIZADOS.name(); break;
            case 125: response = ACTUALIZAR_ESTADO_DESDE_LA_VISTA.name(); break;
            case 130: response = BLOQUEAR_BILLETERO.name(); break;
            case 131: response = DESBLOQUEAR_BILLETERO.name(); break;
            case 200: response = ACUMULAR_PARA_EL_JACKPOT.name(); break;
            case 201: response = OTORGAR_JACKPOT.name(); break;
            case 202: response = OBTENER_TOTAL_ACUMULADO_EN_JACKPOT.name(); break;
            case 203: response = REINICIAR_ACUMULADO.name(); break;
            case 210: response = HABILITAR_JACKPOT.name(); break;
            default: response = NO_IMPLEMENTADO_AUN.name();
        }
        return response;
    }
    
}
