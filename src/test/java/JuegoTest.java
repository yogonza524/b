/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.bingo.rng.RNG;
import com.core.bingosimulador.Juego;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author gonza
 */
public class JuegoTest {
    
    public JuegoTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
     @Test
     @Ignore
     public void valoresTest() {
         Juego juego = new Juego();
         juego.setModoSimulacion(true);
         
         juego.mostrarConfiguracion();
         
         juego.agregarCreditos(4);
//         juego.habilitar(1);
//         juego.habilitar(2);
         juego.habilitar(3);
         
         juego.aumentarApuestas();
         
         System.out.println("---------------");
         
         juego.mostrarConfiguracion();
         
         juego.disminuirApuestas();
         
         System.out.println("---------------");
         
         juego.mostrarConfiguracion();
         
         juego.disminuirApuestas();
         
         System.out.println("---------------");
         
         juego.mostrarConfiguracion();
     }
     
    @Test
//    @Ignore
    public void bolilleroTest() {
        Juego j = new Juego();
        j.setModoSimulacion(true);
        j.generarBolillero();
        j.mostrarBolillero();
        j.setCreditos(4);
        j.setUmbralParaLiberarBolasExtra(1);
        j.setLimitesBolaExtraGratis(2, 6);
        j.mostrarCreditos();
        j.habilitar(1);
        j.habilitar(2);
        j.habilitar(3);
        j.aumentarApuestas();
        j.mostrarApuestas();
        j.jugar(false);
//        for (int i = 0; i < 10; i++) {
//            j.buscarPremios();
//            j.generarBolillero();
//            j.cicloDeBolasExtra();
//        }
        System.out.println("Cartones habilitados: " + j.habilitados());
        j.mostrarPremiosSegunApostado();
        j.mostrarPremiosObtenidos();
        j.mostrarCartones();
        j.mostrarGanado();
        
        System.out.println("Creditos luego de jugar: " + j.getCreditos());
        System.out.println();
        j.mostrarBolillero();
        System.out.println("Creditos invertidos en bolas extra: " + j.getCreditosInvertidosEnBolasExtra());
        System.out.println("Cantidad de bolas extra compradas: " + j.getCantidadDeBolasExtraSeleccionadas());
        
    }
    
    @Test
    @Ignore
    public void randomTest() {
        for (int i = 0; i < 100; i++) {
            System.out.println(RNG.getInstance().pickInt(10)); //de 0 a 9
        }
    }
}
