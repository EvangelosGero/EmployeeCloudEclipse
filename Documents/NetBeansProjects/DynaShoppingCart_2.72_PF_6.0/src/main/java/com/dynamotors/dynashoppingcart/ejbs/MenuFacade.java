/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.ejbs;

import com.dynamotors.dynashoppingcart.entities.Menu;
import com.dynamotors.dynashoppingcart.entities.Menu_;
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
public class MenuFacade extends AbstractFacade<Menu> {
    @PersistenceContext(unitName = "dynaShoppingCartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public MenuFacade() {
        super(Menu.class);
    }
    
  public List<Menu> findMenuFromRole(String role){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Menu> cq = cb.createQuery(Menu.class);
        Root<Menu> menu = cq.from(Menu.class);
        cq.where(cb.like(menu.get(Menu_.roleuser), role));
        cq.select(menu);
        cq.orderBy(cb.asc(menu.get(Menu_.menuShortName)));
        TypedQuery<Menu> q = em.createQuery(cq);
        List<Menu> resultList = q.getResultList();
        return resultList;
    }   
}
