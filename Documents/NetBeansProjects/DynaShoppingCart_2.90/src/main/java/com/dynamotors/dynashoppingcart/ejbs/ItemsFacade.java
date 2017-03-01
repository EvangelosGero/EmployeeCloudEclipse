/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.dynamotors.dynashoppingcart.ejbs;

import com.dynamotors.dynashoppingcart.entities.CategMake;
import com.dynamotors.dynashoppingcart.entities.CategModel;
import com.dynamotors.dynashoppingcart.entities.Items;
import com.dynamotors.dynashoppingcart.entities.Items_;
import java.util.List;
import java.util.Map;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.primefaces.model.SortOrder;

/**
 *
 * @author e
 */
@Stateless
public class ItemsFacade extends AbstractFacade<Items> {
    @PersistenceContext(unitName = "dynaShoppingCartPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ItemsFacade() {
        super(Items.class);
    }
    public List<Items> produceSellerList(int id){
      CriteriaBuilder cb = em.getCriteriaBuilder();
      CriteriaQuery<Items> cq = cb.createQuery(Items.class);
      Root<Items> items = cq.from(Items.class);
      cq.where(cb.equal(items.get(Items_.usernmSellerId), id));
      cq.select(items);
      TypedQuery<Items> q = em.createQuery(cq);
      List<Items> result = q.getResultList();
      return result;
   }
   public List<Items> filterWord(String itemCode){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Items> cq = cb.createQuery(Items.class);
        Root<Items> items = cq.from(Items.class);
        cq.where(cb.like(items.get(Items_.itemCode), "%"+itemCode+"%"));
        cq.select(items);
        TypedQuery<Items> q = em.createQuery(cq);
        List<Items> result = q.getResultList();
        return result;       
   }
   public List<Items> getAll(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Items> cq = cb.createQuery(Items.class);
        Root<Items> items = cq.from(Items.class);
        //Join<Items, CategMake> categMake = items.join(Items_.categMakesList);
        cq.select(items);
        
        Path<?> path = getPath(sortField, items);

        if (sortOrder == null){
            //just don't sort
        }else if (sortOrder.equals(SortOrder.ASCENDING)){
            cq.orderBy(cb.asc(path));
        }else if (sortOrder.equals(SortOrder.DESCENDING)){
            cq.orderBy(cb.asc(path));
        }else if (sortOrder.equals(SortOrder.UNSORTED)){
            //just don't sort
        }else{
            //just don't sort
        }

        //filter
        Predicate filterCondition = cb.conjunction();
        if(filters != null)
          for (Map.Entry<String, Object> filter : filters.entrySet()) {
            if (!filter.getValue().equals("")) {
                //try as string using like
                Path<String> pathFilter = getStringPath(filter.getKey(), items);
                if (pathFilter != null)
                    filterCondition = cb.and(filterCondition, cb.like(pathFilter, "%"+filter.getValue()+"%"));                
                //try as non-string using equal
                Path<?> pathFilterNonString = getPath(filter.getKey(), items);
                if(pathFilterNonString != null)
                    filterCondition = cb.and(filterCondition, cb.equal(pathFilterNonString, filter.getValue()));                
                Expression<List<CategMake>> listPath = getListPath(filter.getKey(), items);
                if (listPath != null){
                    //try for CategMake using isMember                         
                    filterCondition = cb.and(filterCondition, cb.isMember((CategMake)filter.getValue(), listPath));                 
                }
                Expression<List<CategModel>> modelList = getModelExpression(filter.getKey(), items);
                if (modelList != null){
                    //try for CategModel using isMember                         
                    filterCondition = cb.and(filterCondition, cb.isMember((CategModel)filter.getValue(), modelList));                 
                }
            }
        }
        cq.where(filterCondition);
        
        //pagination
        TypedQuery<Items> q = em.createQuery(cq);
        if (pageSize >= 0){
            q.setMaxResults(pageSize);
        }
        if (first >= 0){
            q.setFirstResult(first);
        }
        return q.getResultList();
    }
 
    private Path<?> getPath(String field, Root<Items> items) {
        //sort
        Path<?> path = null;
        if (field == null){
            path = items.get(Items_.itemCode);
        }else{
            switch(field){
                case "itemId":
                    path = items.get(Items_.itemId);
                    break;
                case "itemShortDesc":
                    path = items.get(Items_.itemShortDesc);
                    break;
                case "itemCode":
                    path = null;
                    break;
                case "categMake":
                    path = null ;              
                    break;
                case "categ1":
                    path = items.get(Items_.itemCateg1Id) ;              
                    break;
                case "categ2":
                    path = items.get(Items_.itemCateg2Id) ;              
                    break;
                case "categ3":
                    path = items.get(Items_.itemCateg3Id) ;              
                    break;
            }
        }
        return path;
    }
    
    private Expression<List<CategMake>> getListPath(String field, Root<Items> items) {
        //sort
        Expression<List<CategMake>> path = null;
        if (field == null){
            path = null;
        }else{
            switch(field){
                case "itemId":
                    path = null;
                    break;
                case "itemShortDesc":
                    path = null;
                    break;
                case "itemCode":
                    path = null;
                    break;
                case "categMake":
                    path = items.get(Items_.categMakesList);
                    break;
                case "categModel":
                    path = null;
                    break;
                case "categ1":
                    path = null ;              
                    break;
                case "categ2":
                    path = null ;              
                    break;
                case "categ3":
                    path = null ;              
                    break;
            }
        }
        return path;
    }
    
    private Expression<List<CategModel>> getModelExpression(String field, Root<Items> items) {
        //sort
        Expression<List<CategModel>> path = null;
        if (field == null){
            path = null;
        }else{
            switch(field){
                case "itemId":
                    path = null;
                    break;
                case "itemShortDesc":
                    path = null;
                    break;
                case "itemCode":
                    path = null;
                    break;
                case "categMake":
                    path = null;
                    break;
                case "categModel":
                    path = items.get(Items_.categModelsList);
                    break;
                case "categ1":
                    path = null ;              
                    break;
                case "categ2":
                    path = null ;              
                    break;
                case "categ3":
                    path = null ;              
                    break;
            }
        }
        return path;
    }
 
    private Path<String> getStringPath(String field, Root<Items> items) {
        //sort
        Path<String> path = null;
        if (field == null){
            path = items.get(Items_.itemCode);
        }else{
            switch(field){
                case "id":
                   path = null;
                    break;
                case "itemShortDesc":
                    path = items.get(Items_.itemShortDesc);
                    break;
                case "itemCode":
                    path = items.get(Items_.itemCode);
                    break;
                case "categMake":
                    path = null;
                    break;
                case "categModel":
                    path = null;
                    break;
                 case "categ1":
                    path = null ;              
                    break;
                case "categ2":
                    path = null ;              
                    break;
                case "categ3":
                    path = null ;              
                    break;
            }
        }
        return path;
    }
}
