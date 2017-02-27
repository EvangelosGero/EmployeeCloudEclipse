/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.ejbs;

import com.dynamotors.dynashoppingcart.entities.Usernm;
import com.dynamotors.dynashoppingcart.entities.Usernm_;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;

/**
 *
 * @author e
 */
@Stateless
public class UsernmFacade extends AbstractFacade<Usernm> {
    @PersistenceContext(unitName = "dynaShoppingCartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public UsernmFacade() {
        super(Usernm.class);
    }
    public Usernm validateUser(String username, String password) {
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Usernm> cq = cb.createQuery(Usernm.class);
        Root<Usernm> customer = cq.from(Usernm.class);
        cq.where(cb.and(cb.equal(customer.get(Usernm_.userEmail), username),
                (cb.equal(customer.get(Usernm_.password), password))));
        cq.select(customer).distinct(true);
        TypedQuery<Usernm> q = em.createQuery(cq);
        Usernm result = q.getSingleResult();    
        return result;
    }
    
    public boolean changepassword(String useremail, String password, String newPassword){
        boolean confirm = false;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<Usernm> uq = cb.createCriteriaUpdate(Usernm.class);
        Root<Usernm> usernm = uq.from(Usernm.class);
        uq.where(cb.and(cb.equal(usernm.get(Usernm_.userEmail), useremail),
                (cb.equal(usernm.get(Usernm_.password), password))));
        uq.set(usernm.get(Usernm_.password), newPassword);
        Query q = em.createQuery(uq);
        confirm = q.executeUpdate()>0;
        return confirm;
    }
    
}
