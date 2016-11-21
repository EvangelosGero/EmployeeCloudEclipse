/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HelpMenu;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author evgero
 */
public class GetUpdates {
    public static void getUpdates(){		 
        try {		 
            String fileName = "C:\\EmployeeGUI\\EmployeeGUIOutput\\new.jar"; //The file that will be saved on your computer
            URL link = null;
            try {
                link = new URL("http://12345970-840809898971427973.preview.editmysite.com/uploads/1/2/3/4/12345970/employeegui_3.4.jar"); //The file that you want to download
            } catch (MalformedURLException ex) {
                Logger.getLogger(GetUpdates.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //Code to download
            InputStream in = new BufferedInputStream(link.openStream());
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int n = 0;
            while (-1!=(n=in.read(buf)))
            {
                out.write(buf, 0, n);
            }
            out.close();
            in.close();
            byte[] response = out.toByteArray();
            
            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(response);
            fos.close();
            //End download code
            
            System.out.println("Finished");
            
        } catch (IOException ex) {
            Logger.getLogger(GetUpdates.class.getName()).log(Level.SEVERE, null, ex);
        }

	}
}
