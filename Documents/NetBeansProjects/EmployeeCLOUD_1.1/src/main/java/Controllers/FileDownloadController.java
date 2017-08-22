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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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
    private List<String> salaryExcelFileList;
    private List<String> diakopiExcelFileList;
    private Map<String, List<String>> csl01DirectoriesList;    
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

    public List<String> getSalaryExcelFileList() {
        return salaryExcelFileList;
    }

    public void setSalaryExcelFileList(List<String> salaryExcelFileList) {
        this.salaryExcelFileList = salaryExcelFileList;
    }

    public List<String> getDiakopiExcelFileList() {
        return diakopiExcelFileList;
    }

    public void setDiakopiExcelFileList(List<String> diakopiExcelFileList) {
        this.diakopiExcelFileList = diakopiExcelFileList;
    }

    public Map<String, List<String>> getCsl01DirectoriesList() {
        return csl01DirectoriesList;
    }

    public void setCsl01DirectoriesList(Map<String, List<String>> csl01DirectoriesList) {
        this.csl01DirectoriesList = csl01DirectoriesList;
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
                Collections.sort(apodixisFileList, new Comparator<String>(){                    
           
                @Override
                public int compare(String t1, String t2){
                    return t1.substring(9).
                            compareTo(t2.substring(9));
                }
                });   
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "APD*.pdf")){
                for(Path entry : stream){
                    apdFileList.add(entry.getFileName().toString());
                }
                 Collections.sort(apdFileList, new Comparator<String>(){                    
           
                @Override
                public int compare(String t1, String t2){
                    return t1.substring(4).
                            compareTo(t2.substring(4));
                }
                }); 
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
    
    public void populateExcelFileList(){        
        this.salaryExcelFileList = new ArrayList<>();
        this.diakopiExcelFileList = new ArrayList<>();
        dir = Paths.get("C:", "EmployeeGUI", "EmployeeGUIOutput");
        if (Files.exists(dir)){
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "SALARY*.xls")){
                for(Path entry : stream){
                    salaryExcelFileList.add(entry.getFileName().toString());
                } 
                Collections.sort(salaryExcelFileList, new Comparator<String>(){                    
           
                @Override
                public int compare(String t1, String t2){
                    return t1.substring(14).
                            compareTo(t2.substring(14));
                }
                });   
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "*i.xls")){
                for(Path entry : stream){
                    diakopiExcelFileList.add(entry.getFileName().toString());
                }
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }            
        }else
            JsfUtil.addErrorMessage("EmployeeGUIOutput directory does not exist");
    }
    
    public void populateCSL01FileList(){        
        this.csl01DirectoriesList = new ArrayList<>();
        this.csl01SubDirectoriesList = new ArrayList<>();
        this.csl01DirectoriesFilesList = new ArrayList<>();
        dir = Paths.get("C:", "EmployeeGUI", "EmployeeGUIOutput");
        if (Files.exists(dir)){
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir, "CSL01*")){
                for(Path entry : stream){
                    csl01DirectoriesList.add(entry.getFileName().toString());
                } 
                Collections.sort(csl01DirectoriesList, new Comparator<String>(){                    
           
                @Override
                public int compare(String t1, String t2){
                    return t1.substring(6).
                            compareTo(t2.substring(6));
                }
                });   
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
          /*  try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)){
                for(Path entry : stream){
                    csl01SubDirectoriesList.add(entry.getFileName().toString());
                }
            } catch (IOException ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            } */           
        }else
            JsfUtil.addErrorMessage("EmployeeGUIOutput directory does not exist");
    }
    
}
