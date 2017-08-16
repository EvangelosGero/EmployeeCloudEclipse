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
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Named;

/**
 *
 * @author evgero
 */

@Named("showVacationReport")
@SessionScoped
public class showVacationReport implements Serializable{
    
    private List<PersonVacation> list = new ArrayList();
    private String tableStr;    
    private Statement stm = null ;
    private ResultSet rs = null ;
    private final FacesContext facesContext = FacesContext.getCurrentInstance();

    public List<PersonVacation> getList() {
        return list;
    }

    public String getTableStr() {
        return tableStr;
    }
    
    public void ProduceReportTable(Connection con, String tbl) throws SQLException {
      try{  
          list.clear();
           stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql = "select * from "+tbl; 
            tableStr = tbl;
            rs = stm.executeQuery(sql);
      
      while (rs.next()){
               list.add(new PersonVacation(rs.getInt("ID"), rs.getString(2), rs.getString(3), 
               rs.getInt(4), rs.getInt(5), rs.getInt(6), rs.getInt(7)));                      
           }
    }
      catch (SQLException err){
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "SQLException", err.getMessage()));
              }  
      finally{
          if (rs != null){rs.close();}
          if (stm != null) {stm.close();}
      }
    
    }
    
    public void createCopy(){
            final ClipboardContent content = new ClipboardContent();
            
            StringBuilder clipboardString = new StringBuilder();
            
            for  (PersonVacation row : list){

               String cell = Integer.toString(row.getId());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = row.getFirstName();
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = row.getLastName();
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getEntitledDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getLastyearDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getConsumedDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');
                    cell = Integer.toString(row.getRemainingDays());
                    clipboardString.append(cell);
                    clipboardString.append('\t');                    
                
                clipboardString.append('\n');
                }
                       
            content.putString(clipboardString.toString());
            Clipboard.getSystemClipboard().setContent(content);
    }
    
}
