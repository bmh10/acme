package tests;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.acmetelecom.HtmlBillPrinter;

/**
 * Tests behaviour of HtmlBillPrinter in an isolated context.
 */
public class HtmlBillPrinterTests {
	final String dummyCallerNumber = "440000000000";
	final String dummyCalleeNumber = "440000000001";
	final String dummyCustomerName = "DummyName";
	final String dummyTotalBill = "7.60";
	final String dummyTariff = "Standard";
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	// Instance across which tests are to be applied.
	private HtmlBillPrinter htmlBillPrinter;
	
	/**
	 * Setup which is run before each unit test.
	 */
	@Before
	public void setup() {
		htmlBillPrinter = new HtmlBillPrinter();
	}
	
	/**
	 * Tests that attempting to print heading with null parameters throws IllegalArgumentException.
	 */
	@Test
	public void attemptingToPrintHeadingWithNullParametersThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		htmlBillPrinter.printHeading(null, null, null);
	}
	
	/**
	 * Tests that attempting to print item with null parameters throws IllegalArgumentException.
	 */
	@Test
	public void attemptingToPrintItemWithNullParametersThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		htmlBillPrinter.printItem(null, null, null, null);
	}
	
	/**
	 * Tests that attempting to print total with null parameter throws IllegalArgumentException.
	 */
	@Test
	public void attemptingToPrintTotalWithNullParametersThrowsIllegalArgumentException() {
		exception.expect(IllegalArgumentException.class);
		htmlBillPrinter.printTotal(null);
	}
}
