/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.bv20.core.BV20;
import com.core.bv20.model.SimpleControlador;
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
public class BilleteroTest {
    
    public BilleteroTest() {
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
     public void verificarBilleteroTest() throws InterruptedException {
         BV20 billetero = new BV20(new SimpleControlador());
         billetero.abrirPuerto(0);
         if (billetero.puertoAbierto()) {
             billetero.dataSet();
             Thread.sleep(10000);
         }
     }
}
