/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SalaryReport;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.VBox;

/**
 *
 * @author egdyn_000
 */
public class ShowArthroMerger {
    
    private VBox vBox;
    private Connection con = null;
    private int previousMonth = LocalDate.now().minusMonths(1).getMonthValue();
    private int tableYear = LocalDate.now().minusMonths(1).getYear();
    private String ReportTableString ;
    private ObservableList<ObservableList> dataRegular = FXCollections.observableArrayList();
    private ObservableList<ObservableList> dataDoro = FXCollections.observableArrayList();    
    
    public VBox showArthroMerger(Connection con, int prevMonth, int tablYear, String tableString) throws SQLException{
         
        this.con = con;
        if(prevMonth != -1)this.previousMonth = prevMonth;
        if(tablYear != -1)this.tableYear = tablYear;
        
        String prefix = previousMonth == 4 ? "DORO_PASHA_REPORT_" : previousMonth == 7 ? "EPIDOMA_ADEIAS_REPORT_" :
                "DORO_XMAS_REPORT_" ;                
        ReportTableString = prefix+Integer.toString(tableYear);
        
        // Run the final report
        
        switch (previousMonth){
            case 5 : new SalaryReport.CreateDoroPashaReport().createDBDoroPashaReport(con, 0);
                    break;
            case 7 : new SalaryReport.CreateEpidomaAdeiasReport().createDBEpidomaAdeiasReport(con, 0); 
                    break;
            case 12 : new SalaryReport.CreateDoroXmasReport().createDBDoroXmasReport(con, 0);
                    break;
        }
        
        
        
        ShowArthro showArthroRegular  = new SalaryReport.ShowArthro();
        showArthroRegular.showArthro(con, tableString, null);
        dataRegular = showArthroRegular.data;
        ShowArthro showArthroDoro  = new SalaryReport.ShowArthro();
        showArthroDoro.showArthro(con, ReportTableString, null);
        dataDoro = showArthroDoro.data;
        dataRegular.addAll(dataDoro);
        vBox = new SalaryReport.ShowArthro().showArthro(con, tableString, dataRegular);
        
        return vBox;
    }
    
}
    

