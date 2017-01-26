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
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author evgero
 */
@Entity
@Table(name = "categ_make", catalog = "dynashoppingcart", schema = "")
@XmlRootElement
public class CategMake implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "mk_id", nullable = false)
    private Integer mkId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "mk_description", nullable = false, length = 60)
    private String mkDescription;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 60)
    @Column(name = "mk_logo", nullable = false, length = 60)
    private String mkLogo;
    
 //   @ManyToMany(mappedBy="categMakesList")
  //  private List<Items> itemsList;

    public CategMake() {
    }

    public CategMake(Integer mkId) {
        this.mkId = mkId;
    }

    public CategMake(Integer mkId, String mkDescription, String mkLogo) {
        this.mkId = mkId;
        this.mkDescription = mkDescription;
        this.mkLogo = mkLogo;
    }

    public Integer getMkId() {
        return mkId;
    }

    public void setMkId(Integer mkId) {
        this.mkId = mkId;
    }

    public String getMkDescription() {
        return mkDescription;
    }

    public void setMkDescription(String mkDescription) {
        this.mkDescription = mkDescription;
    }

    public String getMkLogo() {
        return mkLogo;
    }

    public void setMkLogo(String mkLogo) {
        this.mkLogo = mkLogo;
    }   

 //   public List<Items> getItemsList() {
 //       return itemsList;
 //   }

  //  public void setItemsList(List<Items> itemsList) {
  //      this.itemsList = itemsList;
  //  }
    
    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (mkId != null ? mkId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CategMake)) {
            return false;
        }
        CategMake other = (CategMake) object;
        if ((this.mkId == null && other.mkId != null) || (this.mkId != null && !this.mkId.equals(other.mkId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.CategMake[ id=" + mkId + " ]";
    }
    
}
