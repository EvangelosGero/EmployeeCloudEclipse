/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ComputeDays;

import employeegui.ErrorStage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Home
 */
public class ComputeEntitledDays {
   
    private Statement stm ;
    private ResultSet rs;
    private PreparedStatement stm1;
    private int entitledDays = 0;
    public void EntitledDays (Connection con) {
        try {
            LocalDate today = LocalDate.now();            
           /* DatabaseMetaData databaseMetaData = con.getMetaData();
            System.out.println(databaseMetaData.getURL());
            File file = null;
            URL url = null;
            URI uri = null;
            try {
              
               url = new URL(databaseMetaData.getURL());
            } catch (MalformedURLException ex) {
                Logger.getLogger(ComputeEntitledDays.class.getName()).log(Level.SEVERE, null, ex);
            }
                         
            try {
                 uri = url.toURI();
            } catch (URISyntaxException ex) {
                Logger.getLogger(ComputeEntitledDays.class.getName()).log(Level.SEVERE, null, ex);
            }
            
           
                file = new File(uri);
            
            
                System.out.println(file.exists()); 
             
            Long lastUptd = file.lastModified();
            FROM TIMESTAMP TO LOCALDATE
            Timestamp ldt = new Timestamp(lastUptd );
            LocalDateTime localDateTime = ldt.toLocalDateTime();
            LocalDate lastUpdated = localDateTime.toLocalDate();
            System.out.println(lastUpdated);*/  
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String query = "SELECT ID, HIRE_DATE, YEARS_BEFORE, ENTITLED_DAYS , apolisi, diakopi FROM WORKERS";
            rs = stm.executeQuery(query); 
            stm1 = con.prepareStatement("UPDATE EMPLOYEE_VACATION SET ENTITLED_DAYS = ? where ID = ?");           
            while (rs.next()) {
                int id = rs.getInt("ID");
                LocalDate apolisiDate;
                if (rs.getInt("apolisi") != 0)apolisiDate = rs.getDate("diakopi").toLocalDate();
                        else apolisiDate = LocalDate.now().plusDays(1);
                if (today.isBefore(apolisiDate)){
                    LocalDate hireLocalDate = rs.getDate("HIRE_DATE").toLocalDate();                 
                    int yearsBefore = rs.getInt("YEARS_BEFORE");
                    
                    
                    if (((hireLocalDate.until(today)).getYears()+yearsBefore) >= 25) {
                        entitledDays = 26;
                    }
                    else if ((((hireLocalDate.until(today)).getYears()+yearsBefore) >= 12) ||
                         ((hireLocalDate.until(today)).getYears() >= 10)){
                        entitledDays = 25;                       
                    }
                    else if ((hireLocalDate.until(today)).getYears() >=2 ){
                        entitledDays = 22;                    
                    }
                
                    else if ((hireLocalDate.until(today).getYears()) >= 1  ) {
                        switch (today.getYear() - hireLocalDate.getYear()){ 
                            case 1 : entitledDays = (int)Math.round((ChronoUnit.DAYS.between(LocalDate.of(today.getYear(), 1, 1), today)*21D)/
                                        (LocalDate.of(today.getYear(), 12, 31).getDayOfYear())*1D);
                                break;
                            case 2 : entitledDays = 21;
                                break;
                        }
                    }                    
                    else { switch (today.getYear() - hireLocalDate.getYear()){                      
                        case 0 : entitledDays = (int)Math.round((ChronoUnit.DAYS.between(hireLocalDate, today)*20D)/
                                    (LocalDate.of(today.getYear(), 12, 31).getDayOfYear())*1D);
                            break;
                        case 1 : entitledDays = (int)Math.round((ChronoUnit.DAYS.between(LocalDate.of(today.getYear(), 1, 1), today)*20D)/
                                    (LocalDate.of(today.getYear(), 12, 31).getDayOfYear())*1D);
                            break;
                        }
                    }
                    rs.updateInt("ENTITLED_DAYS", entitledDays);
                    rs.updateRow();
                    /* UPDATE EMPLOYEE_VACATION ALSO */
                    stm1.setInt(1, entitledDays);
                    stm1.setInt(2,id);
                    Boolean bad = stm1.execute();
                
                }else if (apolisiDate.getYear() < today.getYear()){
                    entitledDays = 0;
                    rs.updateInt("ENTITLED_DAYS", entitledDays);
                    rs.updateRow();
                    /* UPDATE EMPLOYEE_VACATION ALSO */
                    stm1.setInt(1, entitledDays);
                    stm1.setInt(2,id);
                    Boolean bad = stm1.execute();
                }
            }
                                             
         rs.close();
         stm.close();
         stm1.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(ComputeEntitledDays.class.getName()).log(Level.SEVERE, null, ex);
            ErrorStage.showErrorStage(ex.getMessage());
        }
}
    
}
