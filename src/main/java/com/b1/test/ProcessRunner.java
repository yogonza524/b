/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.b1.test;

import com.b1.batch.ProcessB1;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

/**
 *
 * @author Gonzalo
 */
public class ProcessRunner {
    public static void main(String[] args) throws IOException, InterruptedException {
     
//        new ProcessB1().run(new String[]{"cmd"}, 
//                "java -jar \"C:\\Documents and Settings\\Gonzalo\\Desktop\\bingoBot\\PantallaPrincipalCorregida\\B1.jar\" -bv20:0", 
//                "Error al iniciar B1",
//                true);
        boolean postgreSQL = new ProcessB1().isRunning("postgresql-9.4", new String[]{"cmd"});
        
        System.out.println(postgreSQL);
    }
}
