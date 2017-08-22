/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.util.JsfUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author evgero
 */

@Named("fileDownloadController")
@SessionScoped
public class FileDownloadController implements Serializable{
    
    private List<String> apodixisFileList;
    private List<String> apdFileList;
    private List<String> diakopiFileList;
    private String selectedFile;
    private StreamedContent file;
    private Path dir;
    
    public FileDownloadController(){
        
    } 
    
    public List<String> getApodixisFileList() {
        return apodixisFileList;
    }

    public void setApodixisapodixisapodixisFileList(List<String> apodixisFileList) {
        this.apodixisFileList = apodixisFileList;
    }

    public String getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(String selectedFile) {
        this.selectedFile = selectedFile;
    }
    
    public StreamedContent getFile() {
        return this.file;
    }

    public List<String> getApdFileList() {
        return apdFileList;
    }

    public void setApdFileList(List<String> apdFileList) {
        this.apdFileList = apdFileList;
    }

    public List<String> getDiakopiFileList() {
        return diakopiFileList;
    }

    public void setDiakopiFileList(List<String> diakopiFileList) {
        this.diakopiFileList = diakopiFileList;
    }    
        
    public void fileDownloadStream(String fileName) {
        InputStream inputStream = null;
        byte[] byteArray = null;
        try {
            this.setSelectedFile(fileName);
            File initialFile = new File("C:\\EmployeeGUI\\EmployeeGUIOutput\\"+this.selectedFile);
            //if(inputStream.available() != 0)inputStream.close();
            inputStream = new FileInputStream(initialFile);            
            file = new DefaultStreamedContent(inputStream, "application/pdf", this.selectedFile);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
            JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }   finally {
          //  try {
               // inputStream.close();
          //  } catch (IOException ex) {
          //      Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
          //      JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
         //   }
        }
    }

    
    public void populatePDFFileList(){
        this.apodixisFileList = new ArrayList<>();
        this.apdFileList = new ArrayList<>();
        this.diakopiFileList = new ArrayList<>();
        dir = Paths.get("C:", "EmployeeGUI", "EmployeeGUIOutput");
        if (Files.exists(dir)){
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "APODIXIS*.pdf")){
                for(Path entry : stream){
                    apodixisFileList.add(entry.getFileName().toString());
                }                
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "APD*.pdf")){
                for(Path entry : stream){
                    apdFileList.add(entry.getFileName().toString());
                }                
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*_apodixi_*.pdf")){
                for(Path entry : stream){
                    diakopiFileList.add(entry.getFileName().toString());
                }                
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }else
            JsfUtil.addErrorMessage("EmployeeGUIOutput directory does not exist");
    }
}
