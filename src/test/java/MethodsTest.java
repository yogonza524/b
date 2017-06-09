/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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
public class MethodsTest {
    
    public MethodsTest() {
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
     public void createMethodTest() {
         java.lang.reflect.Method method;
        
         try {
            Ejemplo obj = new Ejemplo();
            Class<?> clazz = obj.getClass();
            Constructor c = obj.getClass().getDeclaredConstructor(clazz);
            method = clazz.getMethod("saludar", String.class);
            method.invoke(c.newInstance(clazz), "Gonza");
         } catch (Exception e) {
             e.printStackTrace();
         }
         
             }
     
     
     public class Ejemplo{
         
         public void saludar(String nombre){
             System.out.println("Hello " + nombre);
         }

        public Ejemplo() {
        }
         
         
     }
}
