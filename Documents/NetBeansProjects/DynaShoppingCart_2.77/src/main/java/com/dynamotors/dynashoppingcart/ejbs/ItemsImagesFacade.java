/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.ejbs;

import com.dynamotors.dynashoppingcart.entities.Items;
import com.dynamotors.dynashoppingcart.entities.ItemsImages;
import com.dynamotors.dynashoppingcart.entities.ItemsImages_;
import com.dynamotors.dynashoppingcart.entities.Items_;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

/**
 *
 * @author evgero
 */
@Stateless
public class ItemsImagesFacade extends AbstractFacade<ItemsImages> {

    @PersistenceContext(unitName = "dynaShoppingCartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemsImagesFacade() {
        super(ItemsImages.class);
    }
    public List<ItemsImages> produceSelectedList(int id){        
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<ItemsImages> cq = cb.createQuery(ItemsImages.class);
      Root<ItemsImages> itemsImages = cq.from(ItemsImages.class);
      Join<ItemsImages, Items> items = itemsImages.join(ItemsImages_.items);
      cq.where(cb.equal(items.get(Items_.itemId), id));
      cq.select(itemsImages);
      TypedQuery<ItemsImages> q = em.createQuery(cq);
      List<ItemsImages> result = q.getResultList();      
      return result;
      
   }
}
