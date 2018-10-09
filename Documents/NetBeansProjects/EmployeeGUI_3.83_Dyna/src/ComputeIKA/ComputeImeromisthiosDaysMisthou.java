/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ComputeIKA;

import employeegui.ErrorStage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author egdyn_000
 */
public class ComputeImeromisthiosDaysMisthou {
    private Connection con;
    private Statement stm;
    private ResultSet rs;
    private int id, workDays,  absentDays,  IKADays;
    private double days;
            
    public Map<Integer, Double> imeromisthiosDaysMisthou(Connection con, String table, int previousMonth, 
            int tableYear) throws SQLException{
       
        this.con = con;
        Map<Integer, Double> imeromisthiosDaysMisthouMap = new HashMap<>(); 
        try {
            String query = "SELECT t1.id, t1.work_days, t1.absent_days - t1.sick_days_less_3 - "
                    + "t1.sick_days_more_3 - vacation_days FROM "+table+" AS t1, workers AS t2 "
                    + "WHERE t1.id = t2.id AND t2.relation = 1";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stm.executeQuery(query);
            while (rs.next()){
               id = rs.getInt(1);
               workDays = rs.getInt(2);
               absentDays = rs.getInt(3);               
               IKADays = ComputeIKADays(previousMonth, tableYear);               
               days = workDays == absentDays ? 0: (workDays-absentDays) >= (workDays/2) ? IKADays - (absentDays * 1.2) : 
               (workDays - absentDays) * 1.2;              
               imeromisthiosDaysMisthouMap.put(id, days);
            }
           
        } catch (SQLException ex) {
            Logger.getLogger(ComputeMisthotosDaysMisthou.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }finally{
            if(rs != null) rs.close();
            if(stm != null) stm.close();
        }
        return imeromisthiosDaysMisthouMap;
    }
    public int ComputeIKADays(int previousMonth, int tableYear){
        LocalDate startDate = LocalDate.of(tableYear, previousMonth, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);
        int daysIKA = 0;                                
            LocalDate counterDate = startDate;
            while ((counterDate.isBefore(endDate)) || (counterDate.isEqual(endDate))){
                    if ((counterDate.getDayOfWeek().getValue()!= 7)) 
                        daysIKA++;
                   counterDate = counterDate.plusDays(1);
            }
        return daysIKA;
    }
}
    

