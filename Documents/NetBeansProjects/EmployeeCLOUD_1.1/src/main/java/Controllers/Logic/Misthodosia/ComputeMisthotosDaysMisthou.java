package Controllers.Logic.Misthodosia;


/* Author AppDate.gr Evangelos Gerogiannis*/

import Controllers.util.JsfUtil;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author egdyn_000
 */
public class ComputeMisthotosDaysMisthou {
    private Connection con;
    private Statement stm;
    private ResultSet rs;
    private int id, workDays,  absentDays; 
    private double days;
    
    public Map<Integer, Double> MisthotosDaysMisthou(Connection con, String table) throws SQLException{
       
        this.con = con;
        Map<Integer, Double> misthotosDaysMisthouMap = new HashMap<>(); 
        try {
            String query = "SELECT t1.id, t1.work_days, t1.absent_days - t1.sick_days_less_3 - "
                    + "t1.sick_days_more_3 - t1.vacation_days FROM "+table+" AS t1, workers AS t2 "
                    + "WHERE t1.id = t2.id AND t2.relation = 0";
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stm.executeQuery(query);
            while (rs.next()){
               id = rs.getInt(1);
               workDays = rs.getInt(2);
               absentDays = rs.getInt(3);           
               days = days = workDays == absentDays ? 0 : (workDays-absentDays) >= (workDays/2) ? 25.0 - (absentDays * 1.2) : 
               (workDays - absentDays) * 1.2;
               misthotosDaysMisthouMap.put(id, days);               
            }
           
        } catch (SQLException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }finally{
            if(rs != null) rs.close();
            if(stm != null) stm.close();
        }
        return misthotosDaysMisthouMap;
    }
    
}

