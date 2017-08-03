/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Controllers.Logic;

import java.time.LocalDate;
import java.util.Date;

/**
 *
 * @author Home
 */
public class AbsentReportPOJO {
    
    private Integer id;
    private String firstName;
    private String lastName;
    private Date absentDate;
    private String justify;

    public AbsentReportPOJO() {
    }
        
    public AbsentReportPOJO(Integer id, String firstName, String lastName, Date absentDate, String justify) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.absentDate = absentDate;
        this.justify = justify;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getAbsentDate() {
        return absentDate;
    }

    public void setAbsentDate(Date absentDate) {
        this.absentDate = absentDate;
    }

    public String getJustify() {
        return justify;
    }

    public void setJustify(String justify) {
        this.justify = justify;
    }
    
    
}   
            
        


    

