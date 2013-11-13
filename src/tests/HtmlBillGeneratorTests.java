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
import com.acmetelecom.Call;
import com.acmetelecom.CallEnd;
import com.acmetelecom.CallStart;
import com.acmetelecom.FileLogger;
import com.acmetelecom.HtmlBillGenerator;
import com.acmetelecom.IBillPrinter;
import com.acmetelecom.LineItem;
import com.acmetelecom.customer.Customer;

/**
 * Tests behaviour of HtmlBillGenerator in an isolated context.
 */
public class HtmlBillGeneratorTests {
	final String dummyCallerNumber = "440000000000";
	final String dummyCalleeNumber = "440000000001";
	final String dummyCustomerName = "DummyName";
	final String dummyTotalBill = "7.60";
	final String dummyTariff = "Standard";
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private Mockery context;
	private IBillPrinter mockBillPrinter;
	
	// Instance across which tests are to be applied.
	private HtmlBillGenerator htmlBillGenerator;
	
	/**
	 * Setup which is run before each unit test.
	 */
	@Before
	public void setup() {
		context = new Mockery();
		mockBillPrinter = context.mock(IBillPrinter.class);
		htmlBillGenerator = new HtmlBillGenerator(mockBillPrinter);
		FileLogger.setActive(false);
	}
	
	/**
	 * Tests that passing null parameters in to HtmlBillGenerator constructor throws IllegalArgumentException.
	 */
	@Test
	public void attemptingToCreateHtmlBillGeneratorWithNullParametersThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		new HtmlBillGenerator(null);
	}
	
	/**
	 * Tests that passing null parameters while generating bill throws IllegalArgumentException.
	 */
	@Test
	public void attemptingToGenerateBillWithNullParametersThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		htmlBillGenerator.sendBill(null, null, null);
	}
	
	/**
	 * Tests that generating bill delegates to BillPrinter and calls prints all correct sub-parts of the bill.
	 */
	@Test
	public void generatingBillDelegatesToBillPrinter() {
		final List<LineItem> items = new ArrayList<LineItem>();
		for (int i = 0; i < 7; i++) {
			items.add(new LineItem(
					new Call(
						new CallStart("cr"+i, "ce"+i, DateTime.now()),
						new CallEnd("cr"+i, "ce"+i, DateTime.now().plusMinutes(i))),
					new BigDecimal(i)));
		}
		
		context.checking(new Expectations() {{  
			oneOf (mockBillPrinter).printHeading(dummyCustomerName, dummyCallerNumber, dummyTariff);
			exactly(items.size()).of (mockBillPrinter).printItem(with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)));
			oneOf (mockBillPrinter).printTotal(dummyTotalBill);
		}});
		
		htmlBillGenerator.sendBill(new Customer(dummyCustomerName, dummyCallerNumber, dummyTariff), items, dummyTotalBill);
		context.assertIsSatisfied();
	}
	
	/**
	 * Tests that generating bill delegates to BillPrinter and calls prints all correct sub-parts of the bill.
	 */
	@Test
	public void generatingBillReturnsCorrectBill() {
		final List<LineItem> items = new ArrayList<LineItem>();
			items.add(new LineItem(
					new Call(
						new CallStart(dummyCallerNumber, dummyCalleeNumber, DateTime.now()),
						new CallEnd(dummyCallerNumber, dummyCalleeNumber, DateTime.now().plusMinutes(1))),
					new BigDecimal(1)));
		
		Bill expectedBill = new Bill(
				new Customer(dummyCustomerName, dummyCallerNumber, dummyTariff),
				items,
				dummyTotalBill);
		
		context.checking(new Expectations() {{  
			oneOf (mockBillPrinter).printHeading(dummyCustomerName, dummyCallerNumber, dummyTariff);
			exactly(items.size()).of (mockBillPrinter).printItem(with(any(String.class)), with(any(String.class)), with(any(String.class)), with(any(String.class)));
			oneOf (mockBillPrinter).printTotal(dummyTotalBill);
		}});
		
		Bill returnedBill = htmlBillGenerator.sendBill(new Customer(dummyCustomerName, dummyCallerNumber, dummyTariff), items, dummyTotalBill);
		
		context.assertIsSatisfied();
		Customer returnedCustomer = returnedBill.getCustomer();
		assertTrue(returnedCustomer.getFullName().equals(dummyCustomerName));
		assertTrue(returnedCustomer.getPhoneNumber().equals(dummyCallerNumber));
		assertTrue(returnedCustomer.getPricePlan().equals(dummyTariff));
		assertTrue(expectedBill.getItems().size() == items.size());
		assertTrue(expectedBill.getItems().get(0).callee().equals(dummyCalleeNumber));
		assertTrue(returnedBill.getTotalBill().equals(dummyTotalBill));
	}
}
