package Controllers;

import java.io.Serializable;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import EJB.CustomerDaoBean;
import Entities.Customer;

@Named
@RequestScoped
public class CustomerController implements Serializable {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@EJB
    private CustomerDaoBean customerDaoBean;
    
    private Customer customer;
    
    private String firstName;
    private String lastName;
    private String email;
    
    public CustomerController() {
        customer = new Customer();
    }
    
    public String saveCustomer() {
        String returnValue = "customer_saved";
        
        try {
            populateCustomer();
            customerDaoBean.saveCustomer(customer);
        } catch (Exception e) {
            e.printStackTrace();
            returnValue = "error_saving_customer";
        }
        
        return returnValue;
    }
    
    private void populateCustomer() {
        if (customer == null) {
            customer = new Entities.Customer();
        }
        customer.setFirstname(getFirstName());
        customer.setLastname(getLastName());
        customer.setEmail(getEmail());
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
}
