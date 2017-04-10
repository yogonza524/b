/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.gson.Gson;
import com.protocol.dao.Conexion;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
public class PostgreSQLTest {
    
    public PostgreSQLTest() {
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
     public void connectionTest() throws SQLException {
         List<HashMap<String,Object>> result = Conexion.getInstancia().consultar("SELECT * FROM idiomas");
         
         if (result != null && !result.isEmpty()) {
             for (int i = 0; i < result.size(); i++) {
                 System.out.println(result.get(i).get("numero") + ": " + result.get(i).get("nombre") );
             }
         }
     }
     
     @Test
    @Ignore
    public void idMachineTest() throws SQLException {
        List<HashMap<String,Object>> result = Conexion.getInstancia().consultar("select numero_maquina from configuracion");
            
        int id = 0;
        if (result != null && !result.isEmpty()) {
            id = (int)result.get(0).get("numero_maquina");
        }
        
         System.out.println(id);
    }
    
    @Test
    @Ignore
    public void anotherTest() throws SQLException {
        List<HashMap<String,Object>> result = Conexion.getInstancia()
                    .consultar("SELECT configuracion.denominacion_actual, denominacion.valor FROM configuracion, denominacion WHERE denominacion.nombre = denominacion_actual");
            
            double valor = 0.0;
            String nombre = "";
            
            if (result != null && !result.isEmpty()) {
                valor = (double) result.get(0).get("valor");
                nombre = (String) result.get(0).get("denominacion_actual");
            }
            
            System.out.println("Nombre: " + nombre);
            System.out.println("Valor: " + valor);
    }
    
}
