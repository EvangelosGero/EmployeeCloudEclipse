/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timer;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 *
 * @author sz
 */
public class ShowIP {
    public static String showMyIP(int opt){
    String result = null;
    InetAddress ipAddress = null;
        try{
            ipAddress=InetAddress.getLocalHost();
        }catch (UnknownHostException exception){
            exception.printStackTrace();
        }
    switch (opt)
    {
        case 1: result = ipAddress.getHostAddress();
                break;
        case 2: result=ipAddress.getHostName();
    }        
    return result;
}
}

