/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package historyMenu;

import employeegui.ShowReport;
import employeegui.VacationReport;
import static historyMenu.ReportEnum.APODIXIS;
import static historyMenu.ReportEnum.APODIXISDP;
import static historyMenu.ReportEnum.APODIXISEA;
import static historyMenu.ReportEnum.APODIXISXMAS;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author evgero
 */
public class OldReports {
    
    private Connection con;
    private ResultSet rs;
    private List<String> list = new ArrayList<>();
    private VBox root;
    public static String selectedReport = null;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("M_yyyy");
                        
    public void oldReportsList(Connection con, ReportEnum type) throws SQLException{
       this.con = con;
        try {
            DatabaseMetaData dm = con.getMetaData();            
            rs = dm.getTables(null, null, null, null);
            if(type == ReportEnum.TIMER){
                Pattern pattern = Pattern.compile("REPORT_..*_....", Pattern.CASE_INSENSITIVE);
                while(rs.next())
                 if (pattern.matcher(rs.getString("TABLE_NAME")).matches())
                     list.add(rs.getString("TABLE_NAME"));
                
                Collections.sort(list, new Comparator<String>(){
           
                @Override
                public int compare(String t1, String t2){
                    return YearMonth.parse(t1.substring(7, t1.length()), formatter).
                            compareTo(YearMonth.parse(t2.substring(7, t2.length()), formatter));
                }
                });                          
            }
            if(type == ReportEnum.VACATION){
                Pattern pattern = Pattern.compile("VACATION_REPORT_....", Pattern.CASE_INSENSITIVE);
                while(rs.next())
                 if (pattern.matcher(rs.getString("TABLE_NAME")).matches())
                     list.add(rs.getString("TABLE_NAME"));
                
                Collections.sort(list, new Comparator<String>(){                    
           
                @Override
                public int compare(String t1, String t2){
                    return t1.substring(t1.length()-4).
                            compareTo(t2.substring(t2.length()-4));
                }
                });                        
            }
            if(type == ReportEnum.MISTHODOTIKH || type == ReportEnum.ARTHRO || type == ReportEnum.IKA
                    || type == ReportEnum.PLIROMI || type == ReportEnum.APODIXIS || type == ReportEnum.APD || type == ReportEnum.CSL01 ){
                Pattern pattern = Pattern.compile("SALARY_REPORT_..*_....", Pattern.CASE_INSENSITIVE);
                while(rs.next())
                 if (pattern.matcher(rs.getString("TABLE_NAME")).matches())
                     list.add(rs.getString("TABLE_NAME"));
                
                Collections.sort(list, new Comparator<String>(){                    
           
                @Override
                public int compare(String t1, String t2){
                    return YearMonth.parse(t1.substring(14, t1.length()), formatter).
                            compareTo(YearMonth.parse(t2.substring(14, t2.length()), formatter));
                }
                });                   
            }
            if(type == ReportEnum.MISTHODOTIKHDP || type == ReportEnum.MISTHODOTIKHEA 
                    || type == ReportEnum.MISTHODOTIKHXMAS || type == ReportEnum.ARTHRODP || type == ReportEnum.ARTHROEA 
                    || type == ReportEnum.ARTHROXMAS || type == ReportEnum.IKADP || type == ReportEnum.IKAEA 
                    || type == ReportEnum.IKAXMAS || type == ReportEnum.PLIROMIDP || type == ReportEnum.PLIROMIEA 
                    || type == ReportEnum.PLIROMIXMAS || type == ReportEnum.APODIXISDP || type == ReportEnum.APODIXISEA 
                    || type == ReportEnum.APODIXISXMAS){
                Pattern pattern = null;
                switch (type){
                    case ARTHRODP:
                    case IKADP:
                    case PLIROMIDP:
                    case MISTHODOTIKHDP:
                    case APODIXISDP:{
                        pattern = Pattern.compile("DORO_PASHA_REPORT_....", Pattern.CASE_INSENSITIVE);
                        break;
                    }
                    case ARTHROEA:
                    case IKAEA:
                    case PLIROMIEA:
                    case MISTHODOTIKHEA:
                    case APODIXISEA:{
                        pattern = Pattern.compile("EPIDOMA_ADEIAS_REPORT_....", Pattern.CASE_INSENSITIVE);
                        break;
                    }
                    case ARTHROXMAS:
                    case IKAXMAS:
                    case PLIROMIXMAS:
                    case MISTHODOTIKHXMAS:
                    case APODIXISXMAS:{
                        pattern = Pattern.compile("DORO_XMAS_REPORT_....", Pattern.CASE_INSENSITIVE);
                        break;
                    }
                }
                while(rs.next())
                 if (pattern.matcher(rs.getString("TABLE_NAME")).matches())
                     list.add(rs.getString("TABLE_NAME"));
                
                Collections.sort(list, new Comparator<String>(){                    
           
                @Override
                public int compare(String t1, String t2){
                    return t1.substring(t1.length()-4).
                            compareTo(t2.substring(t2.length()-4));
                }
                });                   
            }
        } catch (SQLException ex) {
            Logger.getLogger(OldReports.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            if (rs != null)rs.close();
        }
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("ΙΣΤΟΡΙΚΟ");
        stage.getIcons().add(new Image("file:resources/images/employeeList.png"));        
        VBox vBox = new VBox();        
        ObservableList<String> observableList = FXCollections.observableArrayList(list);
        ListView<String> listView = new ListView<>(observableList);
        listView.setTooltip(new Tooltip("Επιλέξτε REPORT"));
        listView.setOnMouseClicked(e->{
           try {
               selectedReport = listView.getSelectionModel().getSelectedItem();
               
               switch (type){                   
                   case TIMER :{                    
                        root = new ShowReport().ProduceReportTable(con, selectedReport);                                                   
                        break;
                    }
                   case VACATION :{                    
                        root = new VacationReport().ProduceVacationTable(con, selectedReport);                                                   
                        break;
                    }
                   case MISTHODOTIKH :{
                        YearMonth yearMonth =  YearMonth.parse(OldReports.selectedReport.substring(14, OldReports.selectedReport.length()), 
                                formatter);        
                        root = yearMonth.getMonthValue() == 4 || yearMonth.getMonthValue() == 7 || yearMonth.getMonthValue() == 12 ?
                        new SalaryReport.ShowMisthodotikiMerger().showMisthodotikiMerger(con, yearMonth.getMonthValue(), yearMonth.getYear(), selectedReport):                                                                      
                        new SalaryReport.ShowMisthodotiki().showMisthodotiki(con, selectedReport , null);
                        break;
                   }
                   case ARTHRO :{
                        YearMonth yearMonth =  YearMonth.parse(OldReports.selectedReport.substring(14, OldReports.selectedReport.length()), 
                                formatter);        
                        root = yearMonth.getMonthValue() == 4 || yearMonth.getMonthValue() == 7 || yearMonth.getMonthValue() == 12 ?
                        new SalaryReport.ShowArthroMerger().showArthroMerger(con, yearMonth.getMonthValue(), yearMonth.getYear(), selectedReport):                                                                      
                        new SalaryReport.ShowArthro().showArthro(con, selectedReport , null);
                        break;
                   }
                   case IKA :{
                        YearMonth yearMonth =  YearMonth.parse(OldReports.selectedReport.substring(14, OldReports.selectedReport.length()), 
                                formatter);        
                        root = yearMonth.getMonthValue() == 4 || yearMonth.getMonthValue() == 7 || yearMonth.getMonthValue() == 12 ?
                        new SalaryReport.ShowIKAMerger().showIKAMerger(con, yearMonth.getMonthValue(), yearMonth.getYear(), selectedReport):                                                                      
                        new SalaryReport.ShowIKA().showIKA(con, selectedReport , null);
                        break;
                   }                   
                   case APODIXIS:
                   case APODIXISEA:
                   case APODIXISDP:
                   case APODIXISXMAS:{
                        new SalaryReport.ApodixisPDF().apodixisPDF(con,  selectedReport); 
                        root = new VBox(100);
                        root.setAlignment(Pos.CENTER);
                        HBox hb = new HBox(100);
                        hb.setAlignment(Pos.CENTER);
                        Label label = new Label("Κατασκευή αρχείου PDF στο C:\\EmployeeGUI\\EmployeeGUIOutput");                        
                        label.setFont(new Font(22));                        
                        hb.getChildren().add(label);
                        root.getChildren().add(hb);
                        break;
                   }
                   case APD:{
                        new SalaryReport.ApdPdf().apdPdf(con,  selectedReport);
                        root = new VBox(100);
                        root.setAlignment(Pos.CENTER);
                        HBox hb = new HBox(100);
                        hb.setAlignment(Pos.CENTER);
                        Label label = new Label("Κατασκευή αρχείου PDF στο C:\\EmployeeGUI\\EmployeeGUIOutput");                        
                        label.setFont(new Font(22));                        
                        hb.getChildren().add(label);
                        root.getChildren().add(hb);
                        break;
                   }
                   case CSL01:{
                        new SalaryReport.CSL01().csl01(con,  selectedReport);
                        root = new VBox(100);
                        root.setAlignment(Pos.CENTER);
                        HBox hb = new HBox(100);
                        hb.setAlignment(Pos.CENTER);
                        Label label = new Label("Κατασκευή αρχείου CSL01 στο C:\\EmployeeGUI\\EmployeeGUIOutput");                        
                        label.setFont(new Font(22));                        
                        hb.getChildren().add(label);
                        root.getChildren().add(hb);
                        break;
                   }
                   case MISTHODOTIKHXMAS :
                   case MISTHODOTIKHEA :
                   case MISTHODOTIKHDP :{
                        root = (new SalaryReport.ShowMisthodotiki()).showMisthodotiki(con,  selectedReport, null);                           
                        break;
                   }
                   case ARTHROXMAS :
                   case ARTHROEA :
                   case ARTHRODP :{
                        root = new SalaryReport.ShowArthro().showArthro(con,  selectedReport, null);                           
                        break;
                   }
                   case IKAXMAS :
                   case IKAEA :
                   case IKADP :{
                        root = new SalaryReport.ShowIKA().showIKA(con,  selectedReport, null);                           
                        break;
                   }
                   case PLIROMI:
                   case PLIROMIXMAS :
                   case PLIROMIEA :
                   case PLIROMIDP :{
                        root = new SalaryReport.ShowPliromi().showPliromi(con,  selectedReport);                           
                        break;
                   }
               }    
               Stage finalStage = new Stage(StageStyle.DECORATED);
               Scene finalScene = new Scene(root, 1200, 700);
               finalStage.setScene(finalScene);
               if(type != APODIXIS && type != APODIXISEA && type != APODIXISDP && type != APODIXISXMAS)finalStage.setFullScreen(true);
               finalStage.show();
           } catch (SQLException ex) {
               Logger.getLogger(OldReports.class.getName()).log(Level.SEVERE, null, ex);
           } catch (IOException ex) {
               Logger.getLogger(OldReports.class.getName()).log(Level.SEVERE, null, ex);
           }
        });
        vBox.getChildren().addAll(listView);
        Scene scene = new Scene(vBox);
        stage.setScene(scene);
        stage.show();
    }
    
}
