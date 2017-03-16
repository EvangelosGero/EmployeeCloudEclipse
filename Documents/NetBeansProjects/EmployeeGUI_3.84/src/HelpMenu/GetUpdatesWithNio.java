/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HelpMenu;

import com.sun.glass.ui.Application;
import static employeegui.Alerts.showInformationAlert;
import employeegui.EmployeeGUIController;
import employeegui.ErrorStage;
import java.awt.Desktop;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Alert;


/**
 *
 * @author evgero
 */
public class GetUpdatesWithNio {
    private static final Desktop desktop = Desktop.getDesktop();
    public static void getUpdates(Connection con) throws IOException, SQLException{
        ReadableByteChannel rbc = null;
        ReadableByteChannel rbc1 = null;
        FileOutputStream fos = null;
        ResultSet rs = null;
        Statement stm = null;
        BigDecimal versionDecimal = new BigDecimal(0);
        try {
            URL link2 = null;
            try {
            link2 = new URL("http://www.dynamotors.net/uploads/1/2/3/4/12345970/employeeguiversion.txt");
             } catch (MalformedURLException ex) {
                Logger.getLogger(GetUpdates.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage()); 
            }
            ByteBuffer buffer = ByteBuffer.allocate(200);
            rbc1 = Channels.newChannel(link2.openStream());
            int nread;
            do{
               nread = rbc1.read(buffer);               
            }while (nread != -1 && buffer.hasRemaining());
            buffer.rewind();
            byte[] versionArray = new byte[100];
            buffer.get(versionArray);
            String versionString = new String(versionArray).trim();         
            BigDecimal versionNew = new BigDecimal(versionString);
                // Check version with database and then download new .jar
            try {
                stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
                rs = stm.executeQuery("SELECT version FROM employeeGUI_system ");
                rs.first();                
                versionDecimal = rs.getBigDecimal("version");
                versionDecimal.setScale(2);
            } catch (SQLException ex) {
                Logger.getLogger(GetUpdatesWithNio.class.getName()).log(Level.SEVERE, null, ex);
                ErrorStage.showErrorStage(ex.getMessage()); 
            }
            
            if(!versionNew.equals(versionDecimal)){ 
                
                showInformationAlert("Εναρξη Λήψης νέας version", "Βήμα 1", "Εγκατάσταση");
                
                Path p1 = Paths.get("C:", "EmployeeGUI", "EmployeeGUI.jar");             
                URL link = null;      
                try {
                    link = new URL("http://www.dynamotors.net/uploads/1/2/3/4/12345970/employeegui.jar"); 
                } catch (MalformedURLException ex) {
                    Logger.getLogger(GetUpdates.class.getName()).log(Level.SEVERE, null, ex);
                    ErrorStage.showErrorStage(ex.getMessage()); 
                } 
                if(Files.notExists(p1))Files.createFile(p1);
                rbc = Channels.newChannel(link.openStream());
                fos = new FileOutputStream(p1.toString());
                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);           
                rs.beforeFirst();
                rs.first();
                rs.updateBigDecimal("version", versionNew);
                rs.updateRow();              
                
                showInformationAlert("Το EmployeeGUI θα κλείσει και θα ξανανοίξει η νέα version", "Βήμα2", "Εγκατάσταση");
                
                try {                   
                    desktop.open(p1.toFile());                    
                    Application.GetApplication().terminate();
                    System.exit(0);
                }catch (IOException ex){
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Μήνυμα Σφάλματος");
                    alert.setHeaderText("Σφάλμα ανοίγματος φακέλου");
                    alert.setContentText(ex.getMessage());
                    alert.showAndWait();
                    Logger.getLogger(EmployeeGUIController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }else{
                showInformationAlert("Εχετε την τελευταία version", "ΟΚ", "Εγκατάσταση");                
            }
        } catch (IOException ex) {
            Logger.getLogger(GetUpdates.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage()); 
        }
        finally{
               if(fos != null)fos.close();
               if(rbc != null)rbc.close();
               if(rbc1 != null)rbc1.close();
               rs.close();
               stm.close();
        }
	}
    
}
