/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.bv20.core.BV20;
import com.core.bv20.model.SimpleControlador;
import com.fazecast.jSerialComm.SerialPort;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

/**
 *
 * @author Gonzalo
 */
public class RunnerTest {
    
    public RunnerTest() {
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
     public void buscarBilleteroTest() throws InterruptedException {
         new Thread(() -> {
             BV20 billetero = new BV20(new SimpleControlador());
             int puerto = 0;
             
             int esperar = 0;
             
             //Obtener la cantidad de puertos disponibles
             int puertos = billetero.puertosDisponibles().length;
             
             boolean seguir = true;
             
             while(seguir){
                 for (int i = 0; i < puertos; i++) {
                 
                 //Abrir el puerto
                 if (billetero.abrirPuerto(i)) {
                     
                     billetero.firmware();
                     
                     try {
                         Thread.sleep(3000);
                     } catch (InterruptedException ex) {
                         Logger.getLogger(RunnerTest.class.getName()).log(Level.SEVERE, null, ex);
                     }
                     
                     if (!billetero.getRespuesta().getValues().isEmpty()) {
                         
                         for(Map.Entry<String,Object> entry : billetero.getRespuesta().getValues().entrySet()){
                             System.out.println(entry.getKey() + ": " + entry.getValue());
                         }
                         seguir = false;
                         break;
                     }
                     else{
                        billetero.cerrarPuerto();
                     }
                 }
                 else{
                     billetero.cerrarPuerto();
                 }
             }
             }
             
         }).start();
         
         Thread.currentThread().join();
     }
}
