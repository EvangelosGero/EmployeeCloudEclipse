/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package timer;

import static employeegui.Alerts.showErrorAlert;
import static employeegui.Alerts.showInformationAlert;
import employeegui.EmployeeGUI;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import static timer.ShowIP.showMyIP;



/**
 * FXML Controller class
 *
 * @author Home
 */
public class TimerController implements Initializable {
    
    private Connection con = EmployeeGUI.con;
    private Statement stm = null;
    private ResultSet rs = null;
    private String hostAddr ="localhost";
        
    @FXML
    private Button startBtn;
    @FXML
    private Button endBtn;
    @FXML
    private TextField passcode;
    @FXML
    private Label timeLabel;
    @FXML
    private Label msg;    
    @FXML
    private Label hostLabel;    
    
    int result;
    String sql;
    /**
     * Initializes the controller class.
     *     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       startBtn.setDisable(true);
       endBtn.setDisable(true);
       msg.setText(null);
       timeLabel.setText(null);
       
       try 
       {                       
        hostLabel.setText(hostAddr);
       } catch (Exception ex) {
            showErrorAlert(ex.getMessage(),"host: "+hostAddr,"Error");             
            startBtn.setDisable(true);
            timeLabel.setText(null);   
          }     
    }      

    @FXML
    private void handleStartBtn(ActionEvent event) throws SQLException {      
         
                        
       try 
        {
         stm = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        
         sql = "INSERT INTO timer (CODE, STARTTIME,PC_IP_IN,PC_NAME_IN) VALUES ('"+passcode.getText()
                 +"', CURRENT_TIMESTAMP,'"+showMyIP(1)+"','"+showMyIP(2)+"')";       
         
         result =stm.executeUpdate(sql);
         showInformationAlert("Η ώρα εισόδου για "+passcode.getText()+" ["+showMyIP(1)+" - "+showMyIP(2)+"] καταγράφηκε επιτυχώς !",null,null);
         passcode.clear();
         startBtn.setDisable(true);
         endBtn.setDisable(true);
        }
         catch (Exception ex) {
          showErrorAlert(ex.getMessage(),null,"Error");
        }finally
       { 
        if (rs != null) rs.close();                
        if (stm != null) stm.close();
        passcode.clear();
       }
    }        
           

    @FXML
    private void handleEndBtn(ActionEvent event) throws SQLException  {
        msg.setText(null);      
               
        try 
        {    
         stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);    
         sql = "SELECT id from timer where code='"+passcode.getText()+"' AND ENDTIME IS NULL";
         rs = stm.executeQuery(sql);
         
         if (rs.next()== false){
             showErrorAlert("Δεν βρέθηκε η εγγραφή για σήμανση εξόδου !",null,"Error");                    
             passcode.clear();
             if (rs != null) rs.close();                
             if (stm != null) stm.close();
             return;
            }                      
        }
        catch (Exception ex) {
            showErrorAlert(ex.getMessage(),null,"Error");
            passcode.clear();
            if (rs != null) rs.close();                
            if (stm != null) stm.close();
            return;
        }
        String ID_To_Update = rs.getString("ID");        
        
        if (rs != null) rs.close();                
        if (stm != null) stm.close();                         
        
        try 
        {               
            stm = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
            String sql = "UPDATE timer SET ENDTIME = CURRENT_TIMESTAMP,"
                 + "INTERVAL_TIME = {fn TIMESTAMPDIFF(SQL_TSI_SECOND, starttime , CURRENT_TIMESTAMP)}*1000,PC_IP_OUT='"
                 +showMyIP(1)+"',PC_NAME_OUT='"+showMyIP(2)+"' "+
                      "WHERE ID="+ID_To_Update;          
                         
            result =stm.executeUpdate(sql);
            showInformationAlert("Η ώρα εξόδου για "+passcode.getText()
                    +" ["+showMyIP(1)+" - "+showMyIP(2)+"] καταγράφηκε επιτυχώς !",null,null);
            passcode.clear();
            startBtn.setDisable(true);
            endBtn.setDisable(true);         
        }
        catch (Exception ex) {
            showErrorAlert(ex.getMessage(),null,"Error");
        }
        finally{
            passcode.clear();
            if (rs != null) rs.close();                
            if (stm != null) stm.close();
        }    
    }           
        
    @FXML
    private void handlePassCode(ActionEvent event) throws SQLException {
        /* Check last employee appearance */
    
        boolean pending_timer_entry_exists;
        
        msg.setText(null);
        startBtn.setDisable(true);
        endBtn.setDisable(true);      
               
        
        try {            
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            sql = "SELECT code,TRIM(LAST_NAME)||' '||TRIM(FIRST_NAME) AS WNAME FROM workers where code='"+passcode.getText()+"'";            
            rs = stm.executeQuery(sql); 
                      
            if (rs.next()== false){
                showErrorAlert("Λανθασμένος κωδικός. Παρακαλώ προσπαθείστε ξανά",null,"Error");                    
                passcode.clear();
                if (rs != null) rs.close();                
                if (stm != null) stm.close();
                return;
            }                      
            msg.setText(rs.getString("WNAME"));                
           
            if (rs != null) rs.close();                
            if (stm != null) stm.close(); 
            
            stm = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);    
            sql = "SELECT * from timer where code='"+passcode.getText()+"' AND ENDTIME IS NULL";
            rs = stm.executeQuery(sql);
            
            pending_timer_entry_exists = rs.next();
            
            if (pending_timer_entry_exists ==true)
                {
                 startBtn.setDisable(true);
                 endBtn.setDisable(false);
                 msg.setText(msg.getText()+" Είσοδος: "+rs.getString("STARTTIME"));  
                }
            else
                {
                 startBtn.setDisable(false);
                 endBtn.setDisable(true);
                }           
            
                   
        }
        catch (Exception ex) {
            showErrorAlert(ex.getMessage(),null,"Error");
            //Logger.getLogger(TimerController.class.getName()).log(Level.SEVERE, null, ex);           
        }  
        finally{  
           if (rs != null) rs.close();                
            if (stm != null) stm.close();
        } 
       }  
    }
           
    
    
