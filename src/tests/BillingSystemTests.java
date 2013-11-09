package tests;

import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.acmetelecom.Bill;
import com.acmetelecom.BillingSystem;
import com.acmetelecom.Call;
import com.acmetelecom.CallEnd;
import com.acmetelecom.CallStart;
import com.acmetelecom.Clock;
import com.acmetelecom.IBillGenerator;
import com.acmetelecom.ICallCostCalculator;
import com.acmetelecom.ICallEventManager;
import com.acmetelecom.MoneyFormatter;
import com.acmetelecom.customer.Customer;
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
	private ICallEventManager mockCallEventManager;
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
		mockCallEventManager = context.mock(ICallEventManager.class);
		mockCallCostCalculator = context.mock(ICallCostCalculator.class);
		mockBillGenerator = context.mock(IBillGenerator.class);
		mockCustomerDatabase = context.mock(CustomerDatabase.class);
		billingSystem = 
				new BillingSystem(mockCallEventManager, mockCallCostCalculator, mockBillGenerator, mockCustomerDatabase, new Clock());
	}
	
	/**
	 * Tests that passing null parameters in to BillingSystem constructor throws IllegalArgumentException.
	 */
	@Test
	public void attemptingToCreateBillingSystemWithNullParametersThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		new BillingSystem(null, null, null, null, null);
	}
	
	/**
	 * Tests that attempting to initiate a call with null parameters throws IllegalArgumentException.
	 */
	@Test
	public void attemptingToInitiateCallWithNullParametersThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		billingSystem.callInitiated(null,  null);
	}
	
	/**
	 * Tests initiating a call delegates to the CallEventHandler.
	 */
	@Test
	public void callInitiatedDelegatesToCallEventHandler() {
		context.checking(new Expectations() {{  
			oneOf (mockCallEventManager).handleEvent(with(any(CallStart.class)));
		}});
		
		billingSystem.callInitiated(dummyCallerNumber, dummyCalleeNumber);
		context.assertIsSatisfied();
	}
	
	/**
	 * Tests that attempting to complete a call with null parameters throws IllegalArgumentException.
	 */
	@Test
	public void attemptingToCompleteCallWithNullParametersThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		billingSystem.callCompleted(null, null);
	}
	
	/**
	 * Tests completing a call delegates to the CallEventHandler.
	 */
	@Test
	public void callCompletedDelegatesToCallEventHandler() {
		context.checking(new Expectations() {{  
			oneOf (mockCallEventManager).handleEvent(with(any(CallEnd.class)));
		}});
		
		billingSystem.callCompleted(dummyCallerNumber, dummyCalleeNumber);
		context.assertIsSatisfied();
	}
	
	/**
	 * Tests that creating a customer bill gets the customer list from the customer database and also clears all call logs
	 * stored by the CallEventManager.
	 */
	@Test
	public void creatingCustomerBillsGetsCustomerListFromCustomerDatabaseAndClearsCallLogs() {
		context.checking(new Expectations() {{  
			oneOf (mockCustomerDatabase).getCustomers();
			oneOf (mockCallEventManager).clearCallLogs();
		}});
		
		billingSystem.createCustomerBills();
		context.assertIsSatisfied();
	}
	
	/**
	 * Tests that creating a customer bill gets call information from the CallEventManager and delegates to the CallCostCalculator
	 * and BillGenerator for each customer returned from the customer database.
	 */
	@Test
	public void creatingCustomerBillsGetsCallInformationFromCallEventManagerAndDelegatesToCallCostCalculatorAndBillGeneratorForEachCustomer() {
		final List<Customer> customers = new ArrayList<Customer>();
		customers.add(new Customer(dummyCustomerName+"1", dummyCallerNumber, "Standard" ));
		customers.add(new Customer(dummyCustomerName+"2", dummyCalleeNumber, "Business" ));
		
		final List<Call> customer1Calls = new ArrayList<Call>();
		customer1Calls.add(new Call(
				new CallStart(dummyCallerNumber, dummyCalleeNumber, DateTime.now()),
				new CallEnd(dummyCallerNumber, dummyCalleeNumber, DateTime.now().plusMinutes(15))));
		final List<Call> customer2Calls = new ArrayList<Call>();
		customer2Calls.add(new Call(
				new CallStart(dummyCalleeNumber, dummyCallerNumber, DateTime.now()),
				new CallEnd(dummyCalleeNumber, dummyCallerNumber, DateTime.now().plusMinutes(15))));
		final BigDecimal call1Cost = new BigDecimal(7);
		final BigDecimal call2Cost = new BigDecimal(12);
		
		context.checking(new Expectations() {{  
			oneOf (mockCustomerDatabase).getCustomers(); will(returnValue(customers));
			oneOf (mockCallEventManager).getCallsForCustomer(dummyCallerNumber); will(returnValue(customer1Calls));
			oneOf (mockCallEventManager).getCallsForCustomer(dummyCalleeNumber); will(returnValue(customer2Calls));
			oneOf (mockCallCostCalculator).calculateCallCost(customers.get(0), customer1Calls.get(0)); will(returnValue(call1Cost)); 
			oneOf (mockCallCostCalculator).calculateCallCost(customers.get(1), customer2Calls.get(0)); will(returnValue(call2Cost));
			exactly(2).of (mockBillGenerator).generateBill(with(any(Bill.class)));
			oneOf (mockCallEventManager).clearCallLogs();
		}});
		
		ArrayList<Bill> bills = billingSystem.createCustomerBills();
		
		assertTrue(bills.size() == 2);
		for (Bill b : bills) {
			if (b.getCustomer().getPhoneNumber() == dummyCallerNumber) {
				assertTrue(b.GetTotalBill().equals(MoneyFormatter.penceToPounds(call1Cost)));
			}
			else if (b.getCustomer().getPhoneNumber() == dummyCalleeNumber) {
				assertTrue(b.GetTotalBill().equals(MoneyFormatter.penceToPounds(call2Cost)));
			}
		}
		
		context.assertIsSatisfied();
	}
	
	/**
	 * Tests that creating customer bills for customer's with no stored call information returns a bill with no call items
	 * and zero total charge.
	 */
	@Test
	public void creatingCustomerBillsReturnsBillWithNoItemsIfNoCallInformationForCustomer() {
		final List<Customer> customers = new ArrayList<Customer>();
		customers.add(new Customer(dummyCustomerName, dummyCallerNumber, "Standard" ));
		
		context.checking(new Expectations() {{  
			oneOf (mockCustomerDatabase).getCustomers(); will(returnValue(customers));
			oneOf (mockCallEventManager).getCallsForCustomer(dummyCallerNumber); will(returnValue(null));
			oneOf (mockCallEventManager).clearCallLogs();
		}});
		
		ArrayList<Bill> bills = billingSystem.createCustomerBills();
		
		assertTrue(bills.size() == 1);
		assertTrue(bills.get(0).GetCalls().size() == 0);
		assertTrue(bills.get(0).GetTotalBill().equals(MoneyFormatter.penceToPounds(new BigDecimal(0.0))));
		context.assertIsSatisfied();
	}
}