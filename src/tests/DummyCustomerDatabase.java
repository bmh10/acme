package tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.Tariff;

/**
 * A dummy implementation of a customer database for testing purposes.
 *
 */
class DummyCustomerDatabase implements CustomerDatabase {

	private HashMap<Customer, Tariff> customers;
	
	/**
	 * Constructor.
	 * @param customers A HashMap mapping all customers to their tariff type.
	 */
	public DummyCustomerDatabase(HashMap<Customer, Tariff> customers) {
		this.customers = customers;
	}
	
	/**
	 * Returns a list of all customers in the dummy database.
	 * @return The list of all customers.
	 */
	@Override
	public List<Customer> getCustomers() {
		ArrayList<Customer> customerList = new ArrayList<Customer>();
		customerList.addAll(customers.keySet());
		return customerList;
	}
}
