package tests;

import java.util.HashMap;

import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

/**
 * A dummy implementation of the tariff database, for testing purposes.
 */
class DummyTariffDatabase implements TariffLibrary {

	private HashMap<Customer, Tariff> tariffs;
	
	/**
	 * Constructor.
	 * @param tariffs A HashMap mapping all customers to their tariff type.
	 */
	public DummyTariffDatabase(HashMap<Customer, Tariff> tariffs) {
		this.tariffs = tariffs;
	}

	/**
	 * Gets the tariff for a specified customer.
	 * @param customer The customer to get the tariff for.
	 * @return The tariff type of the specified customer.
	 */
	@Override
	public Tariff tarriffFor(Customer customer) {
		return tariffs.get(customer);
	}
}
