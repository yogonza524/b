/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
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
public class ProcessTest {
    
    public ProcessTest() {
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
     public void runProcess() throws IOException, InterruptedException {
         String[] command =
        {
            "cmd",
        };
        Process p = Runtime.getRuntime().exec(command);
        new Thread(new SyncPipe(p.getErrorStream(), System.err)).start();
        new Thread(new SyncPipe(p.getInputStream(), System.out)).start();
        PrintWriter stdin = new PrintWriter(p.getOutputStream());
        stdin.println("dir c:\\ /A /Q");
        // write any other commands you want here
        stdin.close();
        int returnCode = p.waitFor();
        System.out.println("Return code = " + returnCode);

     }
     
     class SyncPipe implements Runnable
    {
        public SyncPipe(InputStream istrm, OutputStream ostrm) {
              istrm_ = istrm;
              ostrm_ = ostrm;
          }
          public void run() {
              try
              {
                  final byte[] buffer = new byte[1024];
                  for (int length = 0; (length = istrm_.read(buffer)) != -1; )
                  {
                      ostrm_.write(buffer, 0, length);
                  }
              }
              catch (Exception e)
              {
                  e.printStackTrace();
              }
          }
          private final OutputStream ostrm_;
          private final InputStream istrm_;
    }

}
