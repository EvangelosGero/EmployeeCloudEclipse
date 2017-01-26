/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.ejbs;

import com.dynamotors.dynashoppingcart.entities.ItemsReviews;
import com.dynamotors.dynashoppingcart.entities.ItemsReviews_;
import com.dynamotors.dynashoppingcart.entities.Orders;
import com.dynamotors.dynashoppingcart.entities.Orders_;
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
public class ItemsReviewsFacade extends AbstractFacade<ItemsReviews> {
    @PersistenceContext(unitName = "dynaShoppingCartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemsReviewsFacade() {
        super(ItemsReviews.class);
    }
    public List<ItemsReviews> itemsReviewsPerCustomer(int id){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ItemsReviews> cq = cb.createQuery(ItemsReviews.class);
        Root<ItemsReviews> itemsReviews = cq.from(ItemsReviews.class);
        Root<Orders> orders = cq.from(Orders.class);
        //Join<ItemsReviews, Schedule> order = itemsReviews.join(Schedule_.itemId);
        //itemsReviews.fetch("scheduleType");
        //cq.where(cb.and(cb.equal(order.get("userId"), id), cb.equal(order.get("scheduleType"), 7)));
        // CANNOT USE JOINS BECAUSE I HAVENT RELATIONSHIPS IN TABLES
        cq.where(cb.and(cb.equal(orders.get(Orders_.orderid), itemsReviews.get(ItemsReviews_.orderId)),
                cb.and(cb.equal(itemsReviews.get(ItemsReviews_.userId), id))
                        , cb.equal(orders.get(Orders_.orderState), (short)7 )));        
        //cq.select(itemsReviews).distinct(true); //WITHOUT DISTINCT IT PRODUCES 4 TIMES THE SAME RESULTS        
        TypedQuery<ItemsReviews> q = em.createQuery(cq);
        List<ItemsReviews> result = q.getResultList();
        return result;    
    }
}
