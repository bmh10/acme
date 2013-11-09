package tests;

import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.acmetelecom.BillingSystem;
import com.acmetelecom.CallEventManager;

public class CallEventManagerTests {
	final String dummyCallerNumber = "440000000000";
	final String dummyCalleeNumber = "440000000001";
	final String dummyCustomerName = "DummyName";
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private Mockery context;
	
	// Instance across which tests are to be applied.
	private CallEventManager callEventManager;
	
	/**
	 * Setup which is run before each unit test.
	 */
	@Before
	public void setup() {
		context = new Mockery();
		callEventManager = new CallEventManager();
	}
	
	@Test
	public void attemptingToCreateBillingSystemWithNullParametersThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		new BillingSystem(null, null, null, null);
	}
}
