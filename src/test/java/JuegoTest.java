/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.bingo.entidades.FiguraPago;
import com.bingo.entidades.TablaDePago;
import com.bingo.enumeraciones.Denominacion;
import com.bingo.fabricas.FiguraPagoFactoria;
import com.bingo.rng.RNG;
import com.bingo.util.Matematica;
import com.core.bingosimulador.Juego;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    @Ignore
    public void bolilleroTest() {
        Juego j = new Juego();
        j.setModoSimulacion(true);
        j.generarBolillero();
        j.mostrarBolillero();
        j.setCreditos(100);
        j.setAcumulado(10000);
        j.setDenominacion(Denominacion.DIEZ_CENTAVOS);
        j.setModoTournament(true);
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
        
        j.cobrar();
        
    }
    
    @Test
    @Ignore
    public void randomTest() {
        for (int i = 0; i < 100; i++) {
            System.out.println(RNG.getInstance().pickInt(10)); //de 0 a 9
        }
    }
    
    @Test
    @Ignore
    public void guavaTest() {
        final EventBus eventBus = new EventBus("Ejemplo");
        
        DeadEventsSubscriver d = new DeadEventsSubscriver();
        
        eventBus.register(d);
        eventBus.post(new ProcessingStarted());
    }
    
    public static class DeadEventsSubscriver {
	
        @Subscribe
        public void handleDeadEvent(DeadEvent deadEvent) {
            System.out.println(deadEvent.toString());
        }
    }
    
    public static class InitiateProcessing { }
    public static class ProcessingStarted { }
    public static class ProcessingResults { }
    public static class ProcessingFinished { }
    
    @Test
    @Ignore
    public void saveJsonTest() throws IOException {
        try (Writer writer = new FileWriter("/home/gonza/Descargas/figuras.json")) {
            Gson gson = new GsonBuilder().create();
            List<FiguraPago> figuras = FiguraPagoFactoria.cartones();
            List<TablaDePago> tabla = new ArrayList<>();
            tabla.add(new TablaDePago(figuras));
            gson.toJson(tabla, writer);
        }
    }
    
    @Test
    @Ignore
    public void readJsonFileTest() throws FileNotFoundException {
        Gson gson = new Gson();
        Type tipo = new TypeToken<List<TablaDePago>>() {}.getType();
        JsonReader reader = new JsonReader(new FileReader("/home/gonza/Descargas/figuras.json"));
        List<TablaDePago> data = gson.fromJson(reader, tipo); // contains the whole reviews list
        
        if (data != null && !data.isEmpty()) {
            System.out.println("Tabla de pago");
            data.stream().forEach(figura -> {
                System.out.println(figura.getNumero());
            });
        }
    }
    
    @Test
    @Ignore
    public void crearBonusTest() {
        Map<Integer,Integer> mapa = new HashMap<>();
        mapa.put(2, 4);
        mapa.put(3, 4);
        
        System.out.println(ArrayUtils.toString(Matematica.crearArregloAleatorioConCeros(mapa, 16)));
    }
}
