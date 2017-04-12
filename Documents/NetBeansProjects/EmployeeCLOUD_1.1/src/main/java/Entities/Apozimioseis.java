/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author evgero
 */
@Entity
@Table(name = "APOZIMIOSEIS", catalog = "", schema = "APP")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Apozimioseis.findAll", query = "SELECT a FROM Apozimioseis a")})
public class Apozimioseis implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "ID", nullable = false)
    private Short id;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "APOZIMIOSI", precision = 52)
    private Double apozimiosi;
    @Column(name = "MI_LIFTHISES_ADEIES", precision = 52)
    private Double miLifthisesAdeies;
    @Column(name = "IMERES_ADEIAS")
    private Short imeresAdeias;
    @Column(name = "IMERES_APOZIMIOSIS")
    private Short imeresApozimiosis;

    public Apozimioseis() {
    }

    public Apozimioseis(Short id) {
        this.id = id;
    }

    public Short getId() {
        return id;
    }

    public void setId(Short id) {
        this.id = id;
    }

    public Double getApozimiosi() {
        return apozimiosi;
    }

    public void setApozimiosi(Double apozimiosi) {
        this.apozimiosi = apozimiosi;
    }

    public Double getMiLifthisesAdeies() {
        return miLifthisesAdeies;
    }

    public void setMiLifthisesAdeies(Double miLifthisesAdeies) {
        this.miLifthisesAdeies = miLifthisesAdeies;
    }

    public Short getImeresAdeias() {
        return imeresAdeias;
    }

    public void setImeresAdeias(Short imeresAdeias) {
        this.imeresAdeias = imeresAdeias;
    }

    public Short getImeresApozimiosis() {
        return imeresApozimiosis;
    }

    public void setImeresApozimiosis(Short imeresApozimiosis) {
        this.imeresApozimiosis = imeresApozimiosis;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Apozimioseis)) {
            return false;
        }
        Apozimioseis other = (Apozimioseis) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Controllers.Apozimioseis[ id=" + id + " ]";
    }
    
}
