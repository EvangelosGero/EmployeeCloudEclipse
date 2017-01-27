/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.ejbs;

import com.dynamotors.dynashoppingcart.entities.ItemsPaymentOptions;
import com.dynamotors.dynashoppingcart.entities.ItemsPaymentOptions_;
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
 * @author e
 */
@Stateless
public class ItemsPaymentOptionsFacade extends AbstractFacade<ItemsPaymentOptions> {
    @PersistenceContext(unitName = "dynaShoppingCartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemsPaymentOptionsFacade() {
        super(ItemsPaymentOptions.class);
    }
    public List<ItemsPaymentOptions> produceOptions(String itemCode){
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery <ItemsPaymentOptions> cq = cb.createQuery(ItemsPaymentOptions.class);
      Root<ItemsPaymentOptions> items = cq.from(ItemsPaymentOptions.class);
      cq.where(cb.equal(items.get(ItemsPaymentOptions_.itemCode), itemCode));
      cq.select(items);
      TypedQuery<ItemsPaymentOptions> q = em.createQuery(cq);
      List<ItemsPaymentOptions> result = q.getResultList();
      return result;
   } 
    
}
