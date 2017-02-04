/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.ejbs;

import com.dynamotors.dynashoppingcart.entities.ItemsDeliveryAvailablePin;
import com.dynamotors.dynashoppingcart.entities.ItemsDeliveryAvailablePin_;
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
public class ItemsDeliveryAvailablePinFacade extends AbstractFacade<ItemsDeliveryAvailablePin> {
    @PersistenceContext(unitName = "dynaShoppingCartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemsDeliveryAvailablePinFacade() {
        super(ItemsDeliveryAvailablePin.class);
    }
    public List<ItemsDeliveryAvailablePin> producePinList(String itemCode){
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery <ItemsDeliveryAvailablePin> cq = cb.createQuery(ItemsDeliveryAvailablePin.class);
      Root<ItemsDeliveryAvailablePin> items = cq.from(ItemsDeliveryAvailablePin.class);
      cq.where(cb.equal(items.get(ItemsDeliveryAvailablePin_.itemCode), itemCode));
      cq.select(items);
      TypedQuery<ItemsDeliveryAvailablePin> q = em.createQuery(cq);
      List<ItemsDeliveryAvailablePin> result = q.getResultList();
      return result;
   } 
}
