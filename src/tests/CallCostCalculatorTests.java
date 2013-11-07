package tests;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.acmetelecom.Call;
import com.acmetelecom.CallCostCalculator;
import com.acmetelecom.CallEnd;
import com.acmetelecom.CallStart;
import com.acmetelecom.DaytimePeakPeriod;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

/**
 * Tests behaviour of CallCostCalculator in an isolated context.
 * @author BenLaptop
 */
public class CallCostCalculatorTests {
	
	final String dummyCallerNumber = "440000000000";
	final String dummyCalleeNumber = "440000000001";
	final String dummyCustomerName = "DummyName";
	
	int peakPeriodStart;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	Mockery context;
	
	TariffLibrary mockTariffLibrary;
	
	// Don't want to mock this as it is essential part of calculation.
	DaytimePeakPeriod daytimePeakPeriod;
	
	// Instance across which tests are to be applied.
	CallCostCalculator callCostCalculator;
	
	@Before
	public void setup() {
		context = new Mockery();
		daytimePeakPeriod = new DaytimePeakPeriod();
		mockTariffLibrary = context.mock(TariffLibrary.class);
		callCostCalculator = new CallCostCalculator(mockTariffLibrary, daytimePeakPeriod);
	}
	
	@Test
	public void AttemptingToCalculateCallCostWithNullParametersThrowsInvalidArgumentException() {
		exception.expect(IllegalArgumentException.class);
		callCostCalculator.calculateCallCost(null, null);
	}
	
	@Test
	public void CallThatStartsAndEndsInSamePeakPeriodIsChargedCorrectly() {
		// Setup.
		int callLengthMins = 102;
		Tariff tariff = Tariff.Standard;
		DateTime startTime = new DateTime(2013, 11, 5, daytimePeakPeriod.PeakStart + 1, 0);
		DateTime endTime = startTime.plusMinutes(callLengthMins);
		Call call = this.setupCall(startTime, endTime);
		Customer customer = this.setupCustomer(tariff);
		
		// Run.
		BigDecimal result = callCostCalculator.calculateCallCost(customer, call);
		
		// Checks.
		BigDecimal expectedCost = new BigDecimal(callLengthMins*60).multiply(tariff.peakRate());
		expectedCost = expectedCost.setScale(0, RoundingMode.HALF_UP);
		assertTrue(result.equals(expectedCost));
	}
	
	@Test
	public void CallThatStartsAndEndsInSameOffPeakPeriodIsChargedCorrectly() {
		// Setup.
		int callLengthMins = 32;
		Tariff tariff = Tariff.Standard;
		DateTime startTime = new DateTime(2013, 11, 5, daytimePeakPeriod.PeakStart - 2, 54);
		DateTime endTime = startTime.plusMinutes(callLengthMins);
		Call call = this.setupCall(startTime, endTime);
		Customer customer = this.setupCustomer(tariff);
		
		// Run.
		BigDecimal result = callCostCalculator.calculateCallCost(customer, call);
		
		// Checks.
		BigDecimal expectedCost = new BigDecimal(callLengthMins*60).multiply(tariff.offPeakRate());
		expectedCost = expectedCost.setScale(0, RoundingMode.HALF_UP);
		assertTrue(result.equals(expectedCost));
	}
	
	@Test
	public void CallThatStartsInOffPeakPeriodAndEndsInPeakPeriodOnSameDayIsChargedCorrectly() {
		// Setup.
		int offPeakTime = 12;
		int peakTime = 14;
		Tariff tariff = Tariff.Standard;
		DateTime startTime = new DateTime(2013, 11, 5, daytimePeakPeriod.PeakStart - 1, 60 - offPeakTime);
		DateTime endTime = startTime.plusMinutes(offPeakTime + peakTime);
		Call call = this.setupCall(startTime, endTime);
		Customer customer = this.setupCustomer(tariff);
		
		// Run.
		BigDecimal result = callCostCalculator.calculateCallCost(customer, call);
		
		// Checks.
		BigDecimal expectedOffPeakCost = new BigDecimal(offPeakTime*60).multiply(tariff.offPeakRate());
		BigDecimal expectedTotalCost = expectedOffPeakCost.add(new BigDecimal(peakTime*60).multiply(tariff.peakRate()));
		expectedTotalCost = expectedTotalCost.setScale(0, RoundingMode.HALF_UP);
		assertTrue(result.equals(expectedTotalCost));
	}
	
	@Test
	public void CallThatStartsInPeakPeriodAndEndsInOffPeakPeriodOnSameDayIsChargedCorrectly() {
		// Setup.
		int peakTime = 32;
		int offPeakTime = 23;
		Tariff tariff = Tariff.Standard;
		DateTime startTime = new DateTime(2013, 11, 5, daytimePeakPeriod.PeakEnd - 1, 60 - peakTime);
		DateTime endTime = startTime.plusMinutes(offPeakTime + peakTime);
		Call call = this.setupCall(startTime, endTime);
		Customer customer = this.setupCustomer(tariff);
		
		// Run.
		BigDecimal result = callCostCalculator.calculateCallCost(customer, call);
		
		// Checks.
		BigDecimal expectedOffPeakCost = new BigDecimal(offPeakTime*60).multiply(tariff.offPeakRate());
		BigDecimal expectedTotalCost = expectedOffPeakCost.add(new BigDecimal(peakTime*60).multiply(tariff.peakRate()));
		expectedTotalCost = expectedTotalCost.setScale(0, RoundingMode.HALF_UP);
		assertTrue(result.equals(expectedTotalCost));
	}
	
	// TODO: Multiple tests for calls which span multiple days and periods.
	
	// TODO: Test for call that goes over end of year into new year.

	// Sets up call with specified start and end time.
	private Call setupCall(DateTime startTime, DateTime endTime) {
		CallStart start = new CallStart(dummyCallerNumber, dummyCalleeNumber, startTime);
		CallEnd end = new CallEnd(dummyCallerNumber, dummyCalleeNumber, endTime);
		return new Call(start, end);
	}
	
	// Sets up customer with specified tariff type and sets tariff library to return specified tariff
	// type for this customer.
	private Customer setupCustomer(final Tariff tariffType) {
		final Customer customer = new Customer(dummyCustomerName, dummyCallerNumber, tariffType.toString());
		context.checking(new Expectations() {{  
			oneOf (mockTariffLibrary).tarriffFor(customer); will(returnValue(tariffType));
		}});
		
		return customer;
	}
}
