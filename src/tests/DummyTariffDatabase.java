package tests;

import java.util.HashMap;

import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

class DummyTariffDatabase implements TariffLibrary {

	private HashMap<Customer, Tariff> tariffs;
	
	public DummyTariffDatabase(HashMap<Customer, Tariff> tariffs) {
		this.tariffs = tariffs;
	}

	@Override
	public Tariff tarriffFor(Customer customer) {
		return tariffs.get(customer);
	}
}
