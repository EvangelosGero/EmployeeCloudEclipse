/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author evgero
 */
@Entity
@Table(name = "categ_model", catalog = "dynashoppingcart", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CategModel.findAll", query = "SELECT c FROM CategModel c")})
public class CategModel implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ml_id", nullable = false)
    private Integer mlId;
    @Size(max = 100)
    @Column(name = "ml_model_name", length = 100)
    private String mlModelName;
    @Size(max = 20)
    @Column(name = "ml_year_range", length = 20)
    private String mlYearRange;
    @Size(max = 100)
    @Column(name = "ml_front_image", length = 100)
    private String mlFrontImage;
    @Size(max = 100)
    @Column(name = "ml_rear_image", length = 100)
    private String mlRearImage;
    @Column(name = "ml_parent_id")
    private Integer mlParentId;   
    

    public CategModel() {
    }

    public CategModel(Integer mlId) {
        this.mlId = mlId;
    }

    public Integer getMlId() {
        return mlId;
    }

    public void setMlId(Integer mlId) {
        this.mlId = mlId;
    }

    public String getMlModelName() {
        return mlModelName;
    }

    public void setMlModelName(String mlModelName) {
        this.mlModelName = mlModelName;
    }

    public String getMlYearRange() {
        return mlYearRange;
    }

    public void setMlYearRange(String mlYearRange) {
        this.mlYearRange = mlYearRange;
    }

    public String getMlFrontImage() {
        return mlFrontImage;
    }

    public void setMlFrontImage(String mlFrontImage) {
        this.mlFrontImage = mlFrontImage;
    }

    public String getMlRearImage() {
        return mlRearImage;
    }

    public void setMlRearImage(String mlRearImage) {
        this.mlRearImage = mlRearImage;
    }

    public Integer getMlParentId() {
        return mlParentId;
    }

    public void setMlParentId(Integer mlParentId) {
        this.mlParentId = mlParentId;
    }
  
    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (mlId != null ? mlId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CategModel)) {
            return false;
        }
        CategModel other = (CategModel) object;
        if ((this.mlId == null && other.mlId != null) || (this.mlId != null && !this.mlId.equals(other.mlId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.CategModel[ mlId=" + mlId + " ]";
    }
    
}
