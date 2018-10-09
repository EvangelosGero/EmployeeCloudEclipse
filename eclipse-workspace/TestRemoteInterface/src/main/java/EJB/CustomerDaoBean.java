package EJB;

import javax.ejb.LocalBean;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import Entities.Customer;

@Stateful
@LocalBean
public class CustomerDaoBean {

    @PersistenceContext(unitName = "StudentsTest")
    private EntityManager entityManager;
    

    public void saveCustomer(Customer customer) {
    	CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
    	CriteriaQuery<Customer> query = cb.createQuery(Customer.class);
        if (customer.getId() == null) {
            saveNewCustomer(customer);
        } else {
            updateCustomer(customer);
        }
    }

    private void saveNewCustomer(Customer customer) {
        entityManager.persist(customer);
    }

    private void updateCustomer(Customer customer) {
        entityManager.merge(customer);
    }

    public Customer getCustomer(Long customerId) {
        Customer customer;

        customer = entityManager.find(Customer.class, customerId);

        return customer;
    }

    public void deleteCustomer(Customer customer) {
        entityManager.remove(customer);
    }
}
