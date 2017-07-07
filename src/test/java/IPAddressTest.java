/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
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
public class IPAddressTest {
    
    public IPAddressTest() {
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
     public void getMyIpTest() throws UnknownHostException, SocketException {
        try {
            InetAddress inet = InetAddress.getLocalHost();
            InetAddress[] ips = InetAddress.getAllByName(inet.getCanonicalHostName());
            if (ips  != null ) {
              for (int i = 0; i < ips.length; i++) {
                System.out.println(ips[i]);
              }
            }
          } catch (UnknownHostException e) {
              e.printStackTrace();
          }
     }
     
     @Test
     @Ignore
     public void getLocalIP() throws SocketException{
         for (
            final Enumeration< NetworkInterface > interfaces =
                NetworkInterface.getNetworkInterfaces();
            interfaces.hasMoreElements( );
        )
        {
            final NetworkInterface cur = interfaces.nextElement( );

            if ( cur.isLoopback() )
            {
                continue;
            }

            System.out.println( "interface " + cur.getName( ) );

            for ( final InterfaceAddress addr : cur.getInterfaceAddresses( ) )
            {
                final InetAddress inet_addr = addr.getAddress( );

                if ( !( inet_addr instanceof Inet4Address ) )
                {
                    continue;
                }

                System.out.println(
                    "  address: " + inet_addr.getHostAddress( ) +
                    "/" + addr.getNetworkPrefixLength( )
                );

                System.out.println(
                    "  broadcast address: " +
                        addr.getBroadcast( ).getHostAddress( )
                );
            }
        }
     }
     
     @Test
     @Ignore
     public void getMyPublicIPInterfaceNetworkTest() throws UnknownHostException{
         InetAddress ip = getLocalHostLANAddress();
         System.out.println(ip.getHostAddress());
     }
     
     private static InetAddress getLocalHostLANAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // Iterate all NICs (network interface cards)...
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // Iterate all IP addresses assigned to each card...
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {

                        if (inetAddr.isSiteLocalAddress()) {
                            // Found non-loopback site-local address. Return it immediately...
                            return inetAddr;
                        }
                        else if (candidateAddress == null) {
                            // Found non-loopback address, but not necessarily site-local.
                            // Store it as a candidate to be returned if site-local address is not subsequently found...
                            candidateAddress = inetAddr;
                            // Note that we don't repeatedly assign non-loopback non-site-local addresses as candidates,
                            // only the first. For subsequent iterations, candidate will be non-null.
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                // We did not find a site-local address, but we found some other non-loopback address.
                // Server might have a non-site-local address assigned to its NIC (or it might be running
                // IPv6 which deprecates the "site-local" concept).
                // Return this non-loopback candidate address...
                return candidateAddress;
            }
            // At this point, we did not find a non-loopback address.
            // Fall back to returning whatever InetAddress.getLocalHost() returns...
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        }
        catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException("Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }
}
