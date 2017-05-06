/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.util;



import Controllers.Logic.CreateReport;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 *
 * @author evgero
 */
public class DropIfExists{
    
    private static final FacesContext facesContext = FacesContext.getCurrentInstance();
    
    public static void exec(Connection con, String tableOrView, String nameOfTable){
        ResultSet rs = null;
        Statement stm = null;
        try {            
            DatabaseMetaData databaseMetaData = con.getMetaData();
            if(tableOrView.trim().equalsIgnoreCase("TABLE")){
                rs = databaseMetaData.getTables(null, null, nameOfTable , null);
            }else if (tableOrView.trim().equalsIgnoreCase("VIEW")){
                String[] VIEW_TYPES = {"VIEW"};
                rs = databaseMetaData.getTables(null, null, nameOfTable , VIEW_TYPES);           
            }            
            if (rs.next()) {
                stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_UPDATABLE);
                int update = stm.executeUpdate("DROP "+tableOrView+" "+nameOfTable);                
            }
            
        } catch (SQLException ex) {
            Logger.getLogger(CreateReport.class.getName()).log(Level.SEVERE, null, ex);
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", ex.getMessage()));
        }finally{
            try {
                if(stm != null)stm.close();
                if(rs != null)rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(DropIfExists.class.getName()).log(Level.SEVERE, null, ex);
                facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", ex.getMessage()));
            }
        }
    }
    
}
