package tests;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.acmetelecom.Call;
import com.acmetelecom.CallCostCalculator;
import com.acmetelecom.CallEnd;
import com.acmetelecom.CallStart;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

/**
 * Tests behaviour of CallCostCalculator in an isolated context.
 * @author BenLaptop
 */
public class CallCostCalculatorTests {

	// Instance across which tests are to be applied.
	CallCostCalculator callCostCalculator;
	
	final String dummyCallerNumber = "440000000000";
	final String dummyCalleeNumber = "440000000001";
	
	Customer dummyCustomer;
	
	TariffLibrary mockTariffLibrary;
	
	Mockery context;
	
	@Before
	public void setup() {
		context = new Mockery();
		mockTariffLibrary = context.mock(TariffLibrary.class);
		dummyCustomer = new Customer("DummyCustomer", dummyCallerNumber, "Standard");
		
		callCostCalculator = new CallCostCalculator(mockTariffLibrary);
	}
	
	@Test
	public void CallThatStartsAndEndsInSamePeakPeriodIsChargedCorrectly() {
		// Setup.
		int callLengthMins = 127;
		DateTime startTime = new DateTime(2013, 11, 5, 8, 0);
		DateTime endTime = startTime.plusMinutes(callLengthMins);
		CallStart start = new CallStart(dummyCallerNumber, dummyCalleeNumber, startTime);
		CallEnd end = new CallEnd(dummyCallerNumber, dummyCalleeNumber, endTime);
		
		context.checking(new Expectations() {{  
			oneOf (mockTariffLibrary).tarriffFor(dummyCustomer); will(returnValue(Tariff.Standard));
		}});
		
		// Run.
		BigDecimal result = callCostCalculator.calculateCallCost(dummyCustomer, new Call(start, end));
		
		// Checks.
		Tariff tariff = Tariff.Standard;
		BigDecimal expectedCost = new BigDecimal(callLengthMins*60).multiply(tariff.peakRate());
		expectedCost = expectedCost.setScale(0, RoundingMode.HALF_UP);
		assertTrue(result.equals(expectedCost));
	}

}
