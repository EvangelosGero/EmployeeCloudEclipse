/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.ejbs;

import com.dynamotors.dynashoppingcart.entities.Cart;
import com.dynamotors.dynashoppingcart.entities.Cart_;
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
public class CartFacade extends AbstractFacade<Cart> {
    @PersistenceContext(unitName = "dynaShoppingCartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CartFacade() {
        super(Cart.class);
    }
public List<Cart> populateCartWithId(int id1){
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<Cart> cq = cb.createQuery(Cart.class);
    Root<Cart> cart = cq.from(Cart.class);
    cq.where(cb.equal(cart.get(Cart_.usercode), id1));
    cq.select(cart);
    TypedQuery<Cart> q = em.createQuery(cq);
    List<Cart> resultList = q.getResultList();
    return resultList;  
}   
}
