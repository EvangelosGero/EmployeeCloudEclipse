/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HelpMenu;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.swing.SwingUtilities;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.swing.JRViewer;




/**
 *
 * @author egdyn_000
 */
public class HelpText { 
    
    private JRViewer jrViewer;
    private final SwingNode swingNode = new SwingNode();

    
    public void Help (Connection con) throws SQLException{
    try {
        
     //   InputStream is = (InputStream)HelpText.class.getResourceAsStream("Invoice.jrxml");
        InputStream is2 = (InputStream)HelpText.class.getResourceAsStream("Invoice.jasper");
        
          // Compile jrxml file.      
   
    //   JasperDesign design = JRXmlLoader.load(is);
    //   JasperReport jasperReport = JasperCompileManager.compileReport(design);
        
        //Or even better load the compiled file
        
      JasperReport jasperReport = (JasperReport) JRLoader.loadObject(is2);    
      
       // Parameters for report
       Map<String, Object> parameters = new HashMap<String, Object>();
 
       // DataSource
       // This is simple example, no database.
       // then using empty datasource.
      // JRDataSource dataSource = new JREmptyDataSource(); 
        
        Statement statement = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        ResultSet rs = statement.executeQuery("SELECT first_name, last_name from workers WHERE ID= 24");
        JRResultSetDataSource jrDataSource = new JRResultSetDataSource(rs);
       
       JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport,
               parameters, jrDataSource);
      
       jrViewer = new JRViewer(jasperPrint);
       
       // Convert the JComponent to Node i.e. from swing to FX, run in another thread 
       
      SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(jrViewer);
            }
        });
       
    
       // Make sure the output directory exists.
      // File outDir = new File("C:/jasperoutput");
      // outDir.mkdirs();
 
       // Export to PDF.
       JasperExportManager.exportReportToPdfFile(jasperPrint,
               "C:/EmployeeGUI/EmployeeGUIOutput/StyledTextReport.pdf");
        
       System.out.println("Done!");
    } catch (JRException ex) {
        Logger.getLogger(HelpText.class.getName()).log(Level.SEVERE, null, ex);
    }
        VBox vBox = new VBox();
        vBox.setPrefWidth(500);
        vBox.setPadding(new Insets(15, 15, 15, 15));
        TextArea textArea = new TextArea();
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPadding(new Insets(15, 15, 15, 15));       
        textArea.setText("ΒΟΗΘΕΙΑ ΓΙΑ ΤΟ ΠΡΟΓΡΑΜΜΑ StarHR Light \n"+
                "Σημαντικές Σημειώσεις : \n"
                + "Το πρόγραμμα έχει σχεδιαστεί ώστε να τρέχει κάθε μήνα. Δεν είναι απαραίτητο να τρέξει "
                + "στην αρχή ή στο τέλος του μήνα, όμως θα πρέπει κάποια στιγμή μέσα στον μήνα να δημιουργηθούν "
                + "τα  2 report, όπως εξηγείται στη συνέχεια. Εφόσον χαθεί κάποιος μήνας δεν θα μπορείτε να έχετε "
                + "στοιχεία για αυτό το μήνα. Εξάλλου η λογική αυτή βασίζεται στην αναγκαιότητα για έκδοση της μισθοδοσίας "
                + "των ασφαλιστικών εισφορών κλπ. κάθε μήνα. \n"
                + "To report χρόνοληψίας είναι το βασικό report που πρέπει να τρέχει κάθε μήνα , αλλά και μετά από κάθε "
                + "πρόσληψη ή απόλυση εργαζομένου. Ακολούθως τρέχετε το report αδειών για να ενημερωθείτε για την κατάσταση "
                + "των αδειών. Αυτά είναι τα δύο βασικά report. Το πρόγραμμα χρησιμοποιεί την Java DB σα βάση δεδομένων, που "
                + "είναι ιδιαίτερα light και χρησιμοποιούμε σχετικά λίγους πίνακες για να έχουμε μια ικανοποιητική ταχύτητα της "
                + "εφαρμογής. ");
       
        vBox.getChildren().addAll(textArea, swingNode);
        Scene root = new Scene(vBox);
        Stage stage = new Stage (StageStyle.DECORATED);
        stage.setScene(root);
        stage.show();
        
    }
    
}
