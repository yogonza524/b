/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.b1.batch;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Gonzalo
 */
public class ProcessB1 {
    
    public void run(String[] command, String step, String errorMessage, boolean close) throws IOException, InterruptedException{
        Process p = Runtime.getRuntime().exec(command);
        new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
        new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
        PrintWriter stdin = new PrintWriter(p.getOutputStream());

        stdin.println(step);
        if (stdin.checkError()) {
            System.out.println(errorMessage);
            System.exit(0);
        }
        
        // write any other commands you want here
        if (close) {
            stdin.close();
            int returnCode = p.waitFor();
            System.out.println("Return code = " + returnCode);
        }
    }
    
    public boolean isRunning(String processName, String[] command) throws IOException, InterruptedException{
        boolean result = false;
        
        Process p = Runtime.getRuntime().exec(command);
        SyncPipe writer = new SyncPipe(p.getInputStream(), System.out); 
        new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
        new Thread(writer).start();
        PrintWriter stdin = new PrintWriter(p.getOutputStream());

        stdin.println("sc query \"" + processName + "\" | find \"RUNNING\"");
        if (stdin.checkError()) {
            System.out.println("Error: servicio no encontrado");
        }
        
        Thread.sleep(1000);
        
        for(String line : writer.lines){
            if (line.contains("STATE") && line.contains("4") && line.contains("RUNNING")) {
                result = true;
                break;
            }
        }
        
        stdin.close();
        int returnCode = p.waitFor();
        System.out.println("Return code = " + returnCode);
        
        return result;
    }
    
    class SyncPipe implements Runnable
    {   
        public SyncPipe(InputStream istrm, OutputStream ostrm) {
              istrm_ = istrm;
              ostrm_ = ostrm;
              
              lines = new ArrayList<>();
          }
          public void run() {
              try
              {
                  final byte[] buffer = new byte[1024];
                  for (int length = 0; (length = istrm_.read(buffer)) != -1; )
                  {
                      ostrm_.write(buffer, 0, length);
                      lines.add(new String(buffer, StandardCharsets.UTF_8));
                  }
              }
              catch (Exception e)
              {
                  e.printStackTrace();
              }
          }
          
          public List<String> getLines(){return this.lines;}
          
          private final OutputStream ostrm_;
          private final InputStream istrm_;
          private final List<String> lines;
    }
}
