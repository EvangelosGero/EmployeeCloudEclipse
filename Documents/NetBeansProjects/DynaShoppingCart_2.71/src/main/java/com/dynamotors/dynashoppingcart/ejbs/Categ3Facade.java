/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.ejbs;

import com.dynamotors.dynashoppingcart.entities.Categ3;
import com.dynamotors.dynashoppingcart.entities.Categ3_;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 *
 * @author evgero
 */
@Stateless
public class Categ3Facade extends AbstractFacade<Categ3> {

    @PersistenceContext(unitName = "dynaShoppingCartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Categ3Facade() {
        super(Categ3.class);
    }
    
    public List<Categ3> findFromParentId(int val){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Categ3> cq = cb.createQuery(Categ3.class);
        Root<Categ3> categ3 = cq.from(Categ3.class);
        cq.where(cb.equal(categ3.get(Categ3_.c3ParentId), val));
        cq.select(categ3);
        TypedQuery<Categ3> q = em.createQuery(cq);
        List<Categ3> resultList = q.getResultList();
        return resultList; 
    }
    
}
