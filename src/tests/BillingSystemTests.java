package tests;

import java.util.ArrayList;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.acmetelecom.BillingSystem;
import com.acmetelecom.CallCostCalculator;
import com.acmetelecom.DaytimePeakPeriod;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

/**
 * Tests behaviour of BillingSystem in an isolated context.
 */
public class BillingSystemTests {
	final String dummyCallerNumber = "440000000000";
	final String dummyCalleeNumber = "440000000001";
	final String dummyCustomerName = "DummyName";
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private Mockery context;
	private TariffLibrary mockTariffLibrary;
	
	// Instance across which tests are to be applied.
	private BillingSystem billingSystem;
	
	/**
	 * Setup which is run before each unit test.
	 */
	@Before
	public void setup() {
		context = new Mockery();
		mockTariffLibrary = context.mock(TariffLibrary.class);
		//billingSystem = new BillingSystem();
	}
	
	/**
	 * Tests that passing null parameters in to calculateCallCost function throws an InvalidArgumentException.
	 */
	@Test
	public void AttemptingToCalculateCallCostWithNullParametersThrowsInvalidArgumentException() {
		exception.expect(IllegalArgumentException.class);
		//callCostCalculator.calculateCallCost(null, null);
	}
}
