package tests;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.acmetelecom.BillingSystem;
import com.acmetelecom.IBillGenerator;
import com.acmetelecom.ICallCostCalculator;
import com.acmetelecom.customer.CustomerDatabase;

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
	private ICallCostCalculator mockCallCostCalculator;
	private IBillGenerator mockBillGenerator;
	private CustomerDatabase mockCustomerDatabase;
	
	// Instance across which tests are to be applied.
	private BillingSystem billingSystem;
	
	/**
	 * Setup which is run before each unit test.
	 */
	@Before
	public void setup() {
		context = new Mockery();
		mockCallCostCalculator = context.mock(ICallCostCalculator.class);
		mockBillGenerator = context.mock(IBillGenerator.class);
		mockCustomerDatabase = context.mock(CustomerDatabase.class);
		billingSystem = new BillingSystem(mockCallCostCalculator, mockBillGenerator, mockCustomerDatabase);
	}
	
	/**
	 * Tests that passing null parameters in to BillingSystem constructor.
	 */
	@Test
	public void AttemptingToCreateBillingSystemWithNullParametersThrowsInvalidArgumentException() {
		exception.expect(IllegalArgumentException.class);
		new BillingSystem(null, null, null);
	}
	
	@Test
	public void AttemptingToInitiateCallWithNullParametersThrowsInvalidArgumentException() {
		exception.expect(IllegalArgumentException.class);
		billingSystem.callInitiated(null,  null);
	}
	
	@Test
	public void AttemptingToComplateCallWithNullParametersThrowsInvalidArgumentException() {
		exception.expect(IllegalArgumentException.class);
		billingSystem.callCompleted(null, null);
	}
}
