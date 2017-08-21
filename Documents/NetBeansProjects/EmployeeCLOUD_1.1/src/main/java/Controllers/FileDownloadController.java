/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers;

import Controllers.util.JsfUtil;
import java.io.IOException;
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

/**
 *
 * @author evgero
 */

@Named("fileDownloadComponent")
@SessionScoped
public class FileDownloadController implements Serializable{
    
    private List<String> fileList;
    private String selectedFile;
    
    public FileDownloadController(){
        
    } 
    
    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }

    public String getSelectedFile() {
        return selectedFile;
    }

    public void setSelectedFile(String selectedFile) {
        this.selectedFile = selectedFile;
    }
    
    public void populatePDFFileList(){
        this.fileList = new ArrayList<>();
        Path dir = Paths.get("C:", "EmployeeGUI", "EmployeeGUIOutput");
        if (Files.exists(dir)){
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*.pdf")){
                for(Path entry : stream){
                    fileList.add(entry.getFileName().toString());
                }
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }else
            JsfUtil.addErrorMessage("EmployeeGUIOutput directory does not exist");
    }
}
