/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.ejbs;

import com.dynamotors.dynashoppingcart.entities.Categ2;
import com.dynamotors.dynashoppingcart.entities.Categ2_;
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
public class Categ2Facade extends AbstractFacade<Categ2> {

    @PersistenceContext(unitName = "dynaShoppingCartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Categ2Facade() {
        super(Categ2.class);
    }
    
    public List<Categ2> findFromParentId(int val){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Categ2> cq = cb.createQuery(Categ2.class);
        Root<Categ2> categ2 = cq.from(Categ2.class);
        cq.where(cb.equal(categ2.get(Categ2_.c2ParentId), val));
        cq.select(categ2);
        TypedQuery<Categ2> q = em.createQuery(cq);
        List<Categ2> resultList = q.getResultList();
        return resultList; 
    }
}
