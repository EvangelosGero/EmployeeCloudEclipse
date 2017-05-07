/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Logic;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.Printer;
import javafx.print.PrinterJob;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author Home
 */
@Named("showReport")
@SessionScoped
public class ShowReport implements Serializable {
    
    private List<PersonTimer> list = new ArrayList();     
    private String tableStr;
    private Statement stm = null ;
    private ResultSet rs = null ;
    private final FacesContext facesContext = FacesContext.getCurrentInstance();

    public String getTableStr() {
        return tableStr;
    }

    public List<PersonTimer> getList() {
        return list;
    }
           
    public void ProduceReportTable(Connection con, String tableString) throws SQLException {
      try{  
            this.tableStr = tableString;
            int previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
            int tableYear = LocalDate.now().minusMonths(1).getYear();
            if (tableString == null)tableStr = "REPORT_"+Integer.toString(previousMonth)
            +"_"+Integer.toString(tableYear);
            if (rs != null){rs.close();}
            if (stm != null){stm.close();}
                                 
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql = "select * from "+tableStr;    
            rs = stm.executeQuery(sql);
      
      while (rs.next()){
               list.add(new PersonTimer(rs.getInt("ID"), rs.getString(2), rs.getString(3), 
               rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7), rs.getInt(8), 
               rs.getInt(9), rs.getDouble(10), rs.getInt(11), rs.getInt(12)));                      
           }
    }
      catch (SQLException err){
        System.out.println(err.getMessage());
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", err.getMessage()));
       }  finally{
       if (rs != null){rs.close();}
       if (stm != null){stm.close();}
      }      
    } 
    
    public void createCopy(){
            final ClipboardContent content = new ClipboardContent();
            
            StringBuilder clipboardString = new StringBuilder();
             
              for  (PersonTimer row : list){

               String cell = Integer.toString(row.getId());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = row.getFirstName();
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = row.getLastName();
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getWorkDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getAbsentDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getOccupiedDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getSickDaysLess3());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getSickDaysMore3());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getVacationDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Double.toString(row.getCutHours());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getSuperjobHours());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getSuperHours());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                
                clipboardString.append('\n');
                }
            content.putString(clipboardString.toString());
            Clipboard.getSystemClipboard().setContent(content);
    }
   
        public void printReport(){             
            Printer printer = Printer.getDefaultPrinter();            
            PrinterJob job = PrinterJob.createPrinterJob(printer);
                if (job != null) {
                    Stage dialogStage = new Stage(StageStyle.DECORATED);
                    dialogStage.setTitle("Προσαρμογή Σελίδας για Εκτύπωση");
                    GridPane grid = new GridPane();
                    grid.setAlignment(Pos.CENTER);
                    grid.setHgap(10);
                    grid.setVgap(10);
                    grid.setPadding(new Insets(25, 25, 25, 25));
                    Label scaleX = new Label("Scale X in real number, e.g 0.50 :");
                    grid.add(scaleX, 0, 1);
                    TextField scaleXTextField = new TextField("0.30");
                    grid.add(scaleXTextField, 1, 1);
                    Label scaleY = new Label("Scale Y in real number, e.g. 0.50 :");
                    grid.add(scaleY, 0, 2);
                    TextField scaleYTextField = new TextField("0.30");
                    grid.add(scaleYTextField, 1, 2);
                    Label translateX = new Label("Move X in negative pixels , e.g -300 :");
                    grid.add(translateX, 0, 3);
                    TextField translateXTextField = new TextField("-10");
                    grid.add(translateXTextField, 1, 3);
                    Label translateY = new Label("Move Y in negative pixels, e.g. -70 :");
                    grid.add(translateY, 0, 4);
                    TextField translateYTextField = new TextField("-120");
                    grid.add(translateYTextField, 1, 4);
                    Button btn = new Button("Print");
                    HBox hbBtn = new HBox(10);
                    hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
                    hbBtn.getChildren().add(btn);
                    grid.add(hbBtn, 1, 6);
                    Scene scene = new Scene(grid, 500, 275);
                    dialogStage.setScene(scene);
                    dialogStage.show();                    
                    btn.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle (ActionEvent e) {                    
                    PageLayout pageLayout = printer.createPageLayout(Paper.A4,PageOrientation.LANDSCAPE,
                                Printer.MarginType.HARDWARE_MINIMUM); 
                                }
                     
                        });
                    }
    }      
       
}
