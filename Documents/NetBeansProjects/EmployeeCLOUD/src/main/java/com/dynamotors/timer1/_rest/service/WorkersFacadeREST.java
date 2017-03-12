/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.timer1._rest.service;

import com.dynamotors.timer1._rest.Workers;
import com.dynamotors.timer1._rest.Workers_;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author evgero
 */
@Stateless
@Path("com.dynamotors.timer1._rest.workers")
public class WorkersFacadeREST extends AbstractFacade<Workers> {

    @PersistenceContext(unitName = "com.dynamotors_Timer1.5_REST_war_1.0-SNAPSHOTPU")
    private EntityManager em;

    public WorkersFacadeREST() {
        super(Workers.class);
    }

    @POST
    @Override
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void create(Workers entity) {
        super.create(entity);
    }

    @PUT
    @Path("{id}")
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public void edit(@PathParam("id") Integer id, Workers entity) {
        super.edit(entity);
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") Integer id) {
        super.remove(super.find(id));
    }

    @GET
    @Path("{id}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public Workers find(@PathParam("id") Integer id) {
        return super.find(id);
    }

    @GET
    @Override
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Workers> findAll() {
        return super.findAll();
    }

    @GET
    @Path("{from}/{to}")
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public List<Workers> findRange(@PathParam("from") Integer from, @PathParam("to") Integer to) {
        return super.findRange(new int[]{from, to});
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public String countREST() {
        return String.valueOf(super.count());
    }
    
    @GET
    @Path("{passCode}")
    @Produces(MediaType.TEXT_PLAIN)
    public String findPassCode(@PathParam("passCode")String passCode){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<String> cq = cb.createQuery(String.class);
        Root<Workers> workers = cq.from(Workers.class);
        cq.where(cb.equal(workers.get(Workers_.code), passCode));
        cq.select(workers.get(Workers_.firstName));
        TypedQuery<String> q = em.createQuery(cq);
        String result = q.getSingleResult();
        cq.select(workers.get(Workers_.lastName));
        TypedQuery<String> q1 = em.createQuery(cq);
        String result1 = q1.getSingleResult();
        return result+" "+result1;       
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
