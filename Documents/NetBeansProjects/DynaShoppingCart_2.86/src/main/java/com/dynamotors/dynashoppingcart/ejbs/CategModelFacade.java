/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.ejbs;

import com.dynamotors.dynashoppingcart.entities.CategModel;
import com.dynamotors.dynashoppingcart.entities.CategModel_;
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
public class CategModelFacade extends AbstractFacade<CategModel> {

    @PersistenceContext(unitName = "dynaShoppingCartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CategModelFacade() {
        super(CategModel.class);
    }
    
    public List<CategModel> findFromParentId(int val){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<CategModel> cq = cb.createQuery(CategModel.class);
        Root<CategModel> categModel = cq.from(CategModel.class);
        cq.where(cb.equal(categModel.get(CategModel_.mlParentId), val));
        cq.select(categModel);
        TypedQuery<CategModel> q = em.createQuery(cq);
        List<CategModel> resultList = q.getResultList();
        return resultList; 
    }
    
}
