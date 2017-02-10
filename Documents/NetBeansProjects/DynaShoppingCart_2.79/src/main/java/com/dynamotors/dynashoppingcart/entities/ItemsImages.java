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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author evgero
 */
@Entity
@Table(name = "items_images", catalog = "dynashoppingcart", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "ItemsImages.findAll", query = "SELECT i FROM ItemsImages i")})
public class ItemsImages implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id", nullable = false)
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 50)
    @Column(name = "filename", nullable = false, length = 50)
    private String filename;
    @Basic(optional = false)
    @NotNull
    @Column(name = "isbasic", nullable = false)
    private short isbasic;
    @JoinColumn(name = "itemFK_Id", referencedColumnName = "item_id")
    @ManyToOne
    private Items items;

    public ItemsImages() {
    }

    public ItemsImages(Integer id) {
        this.id = id;
    }

    public ItemsImages(Integer id, String filename, short isbasic) {
        this.id = id;
        this.filename = filename;
        this.isbasic = isbasic;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public short getIsbasic() {
        return isbasic;
    }

    public void setIsbasic(short isbasic) {
        this.isbasic = isbasic;
    }

    public Items getItems() {
        return items;
    }

    public void setItems(Items items) {
        this.items = items;
        if (!items.getItemsImagesList().contains(this)) { 
            items.getItemsImagesList().add(this);            
        }
    }

    @Override
    public int hashCode() {
        Integer hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemsImages)) {
            return false;
        }
        ItemsImages other = (ItemsImages) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.dynamotors.dynashoppingcart.entities.ItemsImages[ id=" + id + " ]";
    }
    
}
