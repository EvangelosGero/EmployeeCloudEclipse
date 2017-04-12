/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package EJBs;

import Entities.CompanyInformation;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author evgero
 */
@Stateless
public class CompanyInformationFacade extends AbstractFacade<CompanyInformation> {

    @PersistenceContext(unitName = "com.dynamotors_Timer1.5_REST_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CompanyInformationFacade() {
        super(CompanyInformation.class);
    }
    
}
