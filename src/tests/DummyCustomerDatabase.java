package tests;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.Tariff;

class DummyCustomerDatabase implements CustomerDatabase {

	private HashMap<Customer, Tariff> customers;
	
	public DummyCustomerDatabase(HashMap<Customer, Tariff> customers) {
		this.customers = customers;
	}
	
	@Override
	public List<Customer> getCustomers() {
		ArrayList<Customer> customerList = new ArrayList<Customer>();
		customerList.addAll(customers.keySet());
		return customerList;
	}
}
