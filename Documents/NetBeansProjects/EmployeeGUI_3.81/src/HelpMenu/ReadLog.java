/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HelpMenu;

import employeegui.ErrorStage;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author e
 */
public class ReadLog { 
    public static void readLog(){
      try {
          Handler handler = new FileHandler("ApplicationLog.log");
    //  Bindings.   
      } catch (IOException | SecurityException ex) {
          Logger.getLogger(ReadLog.class.getName()).log(Level.SEVERE, null, ex);
          ErrorStage.showErrorStage(ex.getMessage());
      }
    }
}
