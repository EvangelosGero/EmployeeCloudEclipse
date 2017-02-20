/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.ejbs;

import com.dynamotors.dynashoppingcart.entities.OrderItems;
import com.dynamotors.dynashoppingcart.entities.OrderItems_;
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
public class OrderItemsFacade extends AbstractFacade<OrderItems> {

    @PersistenceContext(unitName = "dynaShoppingCartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public OrderItemsFacade() {
        super(OrderItems.class);
    }   
    
    public List<OrderItems> orderItemsPerCustomer(int id){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<OrderItems> cq = cb.createQuery(OrderItems.class);
        Root<OrderItems> orderItems = cq.from(OrderItems.class);
        cq.where(cb.equal(orderItems.get(OrderItems_.userId), id));
        cq.select(orderItems);
        TypedQuery<OrderItems> q = em.createQuery(cq);
        List<OrderItems> result = q.getResultList();
        return result;    
    }    
}
