/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJBs;

import Entities.EmplAdmins;
import Entities.EmplAdmins_;
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
 * @author evgero
 */
@Stateless
public class EmplAdminsFacade extends AbstractFacade<EmplAdmins> {

    @PersistenceContext(unitName = "com.dynamotors_Timer1.5_REST_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public EmplAdminsFacade() {
        super(EmplAdmins.class);
    }
    
    public EmplAdmins validateUser(String username, String password) {
        
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<EmplAdmins> cq = cb.createQuery(EmplAdmins.class);
        Root<EmplAdmins> customer = cq.from(EmplAdmins.class);
        cq.where(cb.and(cb.equal(customer.get(EmplAdmins_.username), username),
                (cb.equal(customer.get(EmplAdmins_.password), password))));
        cq.select(customer).distinct(true);
        TypedQuery<EmplAdmins> q = em.createQuery(cq);
        EmplAdmins result = q.getSingleResult();    
        return result;
    }
    
    public boolean changepassword(String useremail, String password, String newPassword){
        boolean confirm = false;
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaUpdate<EmplAdmins> uq = cb.createCriteriaUpdate(EmplAdmins.class);
        Root<EmplAdmins> usernm = uq.from(EmplAdmins.class);
        uq.where(cb.and(cb.equal(usernm.get(EmplAdmins_.username), useremail),
                (cb.equal(usernm.get(EmplAdmins_.password), password))));
        uq.set(usernm.get(EmplAdmins_.password), newPassword);
        Query q = em.createQuery(uq);
        confirm = q.executeUpdate()>0;
        return confirm;
    }
    
    
}
