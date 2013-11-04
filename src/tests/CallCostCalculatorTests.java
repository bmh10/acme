package tests;

import static org.junit.Assert.*;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.acmetelecom.CallCostCalculator;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;

public class CallCostCalculatorTests {

	
	// Instance across which tests are to be applied.
	CallCostCalculator callCostCalculator;
	
	@Before
	public void setup() {
		HashMap<Customer, Tariff> tariffs = new HashMap<Customer, Tariff>(); 
		callCostCalculator = new CallCostCalculator(new DummyTariffDatabase(tariffs));
	}
	
	@Test
	public void CallThatStartsAndEndsInSamePeakPeriodIsChargedCorrectly() {
		fail("Not yet implemented");
	}

}
