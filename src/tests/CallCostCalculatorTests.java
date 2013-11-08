package tests;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.acmetelecom.Call;
import com.acmetelecom.CallCostCalculator;
import com.acmetelecom.CallEnd;
import com.acmetelecom.CallStart;
import com.acmetelecom.DaytimePeakPeriod;
import com.acmetelecom.DaytimePeakPeriod.DayPeriod;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

/**
 * Tests behaviour of CallCostCalculator in an isolated context.
 */
public class CallCostCalculatorTests {
	
	final String dummyCallerNumber = "440000000000";
	final String dummyCalleeNumber = "440000000001";
	final String dummyCustomerName = "DummyName";
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	private Mockery context;
	private TariffLibrary mockTariffLibrary;
	private ArrayList<Tariff> tariffTypes;
	
	// Don't want to mock this as it is essential part of calculation.
	private DaytimePeakPeriod daytimePeakPeriod;
	
	// Instance across which tests are to be applied.
	private CallCostCalculator callCostCalculator;
	
	/**
	 * Setup which is run before each unit test.
	 */
	@Before
	public void setup() {
		context = new Mockery();
		daytimePeakPeriod = new DaytimePeakPeriod();
		mockTariffLibrary = context.mock(TariffLibrary.class);
		callCostCalculator = new CallCostCalculator(mockTariffLibrary, daytimePeakPeriod);
		tariffTypes = new ArrayList<Tariff>();
		for (Tariff t : Tariff.values()) {
			tariffTypes.add(t);
		}
	}
	
	/**
	 * Tests that passing null parameters in to CallCostCalculator constructor.
	 */
	@Test
	public void AttemptingToCreateCallCostCalculatorWithNullParametersThrowsInvalidArgumentException() {
		exception.expect(IllegalArgumentException.class);
		new CallCostCalculator(null, null);
	}
	
	/**
	 * Tests that passing null parameters in to calculateCallCost function throws an InvalidArgumentException.
	 */
	@Test
	public void AttemptingToCalculateCallCostWithNullParametersThrowsInvalidArgumentException() {
		exception.expect(IllegalArgumentException.class);
		callCostCalculator.calculateCallCost(null, null);
	}
	
	/**
	 * Tests that a call which starts and ends within same peak period is charged correctly
	 * for each tariff type.
	 */
	@Test
	public void CallThatStartsAndEndsInSamePeakPeriodIsChargedCorrectly() {
		for (Tariff tariff : tariffTypes) {
			// Setup.
			int callLengthMins = 102;
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
	}
	
	/**
	 * Tests that call which starts and ends in same off-peak period is charged correctly
	 * for each tariff type.
	 */
	@Test
	public void CallThatStartsAndEndsInSameOffPeakPeriodIsChargedCorrectly() {
		for (Tariff tariff : tariffTypes) {
			// Setup.
			int callLengthMins = 32;
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
	}
	
	/**
	 * Tests that call which starts in off-peak period and ends in next peak period is charged correctly
	 * for each tariff type.
	 */
	@Test
	public void CallThatStartsInOffPeakPeriodAndEndsInPeakPeriodOnSameDayIsChargedCorrectly() {
		for (Tariff tariff : tariffTypes) {
			// Setup.
			int offPeakTime = 12;
			int peakTime = 14;
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
	}
	
	/**
	 * Tests that call which starts in peak period and ends in next off-peak period is charged correctly
	 * for each tariff type.
	 */
	@Test
	public void CallThatStartsInPeakPeriodAndEndsInOffPeakPeriodOnSameDayIsChargedCorrectly() {
		for (Tariff tariff : tariffTypes) {
			// Setup.
			int peakTime = 32;
			int offPeakTime = 23;
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
	}
	
	/**
	 * Tests that a call that starts in pre-peak period and finished in post-peak period the next day
	 * is charged correctly for each tariff type.
	 */
	@Test
	public void CallThatStartsInPrePeakPeriodAndEndsInPostPeakPeriodOfNextDayIsChargedCorrectly() {
		for (Tariff tariff : tariffTypes) {
			// Setup.
			int initialPrePeakTime = 45;
			int finalPostPeakTime = 15;
			DateTime startTime = new DateTime(2013, 11, 5, daytimePeakPeriod.PeakStart - 1, 60 - initialPrePeakTime);
			DateTime endTime = new DateTime(2013, 11, 6, daytimePeakPeriod.PeakEnd, finalPostPeakTime);
			Call call = this.setupCall(startTime, endTime);
			Customer customer = this.setupCustomer(tariff);
			
			// Run.
			BigDecimal result = callCostCalculator.calculateCallCost(customer, call);
			
			// Checks.
			int preDuration = daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.PrePeak);
			int peakDuration = daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.Peak);
			int postDuration = daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.PostPeak);
			
			BigDecimal expectedOffPeakCost = 
					new BigDecimal((initialPrePeakTime + finalPostPeakTime)*60 + postDuration + preDuration).multiply(tariff.offPeakRate());
			BigDecimal expectedPeakCost = new BigDecimal(2*peakDuration).multiply(tariff.peakRate());
			BigDecimal expectedTotalCost = expectedOffPeakCost.add(expectedPeakCost);
			expectedTotalCost = expectedTotalCost.setScale(0, RoundingMode.HALF_UP);
			assertTrue(result.equals(expectedTotalCost));
		}
	}
	
	/**
	 * Tests that a call that lasts for a week is charged correctly for each tariff type.
	 */
	@Test
	public void CallThatLastsForAWeekIsChargedCorrectly() {
		for (Tariff tariff : tariffTypes) {
			// Setup.
			int callTimeDays = 7;
			DateTime startTime = new DateTime(2013, 11, 5, daytimePeakPeriod.PeakStart, 0);
			DateTime endTime = startTime.plusDays(callTimeDays);
			Call call = this.setupCall(startTime, endTime);
			Customer customer = this.setupCustomer(tariff);
			
			// Run.
			BigDecimal result = callCostCalculator.calculateCallCost(customer, call);
			
			// Checks.
			int preDuration = daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.PrePeak);
			int peakDuration = daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.Peak);
			int postDuration = daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.PostPeak);
			
			BigDecimal expectedOffPeakCost = new BigDecimal((preDuration + postDuration)*callTimeDays).multiply(tariff.offPeakRate());
			BigDecimal expectedPeakCost = new BigDecimal(peakDuration*callTimeDays).multiply(tariff.peakRate());
			BigDecimal expectedTotalCost = expectedOffPeakCost.add(expectedPeakCost);
			expectedTotalCost = expectedTotalCost.setScale(0, RoundingMode.HALF_UP);
			assertTrue(result.equals(expectedTotalCost));
		}
	}
	
	/**
	 * Tests that a call which goes into a new year is charged correctly for each tariff type.
	 */
	@Test
	public void CallThatGoesIntoNewYearIsChargedCorrectly() {
		for (Tariff tariff : tariffTypes) {
			// Setup.
			int callTimeDays = 2;
			DateTime startTime = new DateTime(2013, 12, 31, daytimePeakPeriod.PeakStart, 0);
			DateTime endTime = startTime.plusDays(callTimeDays);
			Call call = this.setupCall(startTime, endTime);
			Customer customer = this.setupCustomer(tariff);
			
			// Run.
			BigDecimal result = callCostCalculator.calculateCallCost(customer, call);
			
			// Checks.
			int preDuration = daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.PrePeak);
			int peakDuration = daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.Peak);
			int postDuration = daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.PostPeak);
			
			BigDecimal expectedOffPeakCost = new BigDecimal((preDuration + postDuration)*callTimeDays).multiply(tariff.offPeakRate());
			BigDecimal expectedPeakCost = new BigDecimal(peakDuration*callTimeDays).multiply(tariff.peakRate());
			BigDecimal expectedTotalCost = expectedOffPeakCost.add(expectedPeakCost);
			expectedTotalCost = expectedTotalCost.setScale(0, RoundingMode.HALF_UP);
			assertTrue(result.equals(expectedTotalCost));
		}
	}
	
	/**
	 * Tests that call which lasts less than a minute but crosses from off-peak to peak period is charged correctly
	 * for each tariff type.
	 */
	@Test
	public void CallThatLastsAFewSecondsAndCrossesIntoPeakPeriodIsChargedCorrectly() {
		for (Tariff tariff : tariffTypes) {
			// Setup.
			int offPeakSeconds = 13;
			int peakSeconds = 17;
			DateTime startTime = new DateTime(2013, 11, 5, daytimePeakPeriod.PeakStart - 1, 59, 60 - offPeakSeconds);
			DateTime endTime = startTime.plusSeconds(offPeakSeconds + peakSeconds);
			Call call = this.setupCall(startTime, endTime);
			Customer customer = this.setupCustomer(tariff);
			
			// Run.
			BigDecimal result = callCostCalculator.calculateCallCost(customer, call);
			
			// Checks.
			BigDecimal expectedOffPeakCost = new BigDecimal(offPeakSeconds).multiply(tariff.offPeakRate());
			BigDecimal expectedTotalCost = expectedOffPeakCost.add(new BigDecimal(peakSeconds).multiply(tariff.peakRate()));
			expectedTotalCost = expectedTotalCost.setScale(0, RoundingMode.HALF_UP);
			assertTrue(result.equals(expectedTotalCost));
		}
	}

	// --- Setup Methods --- //
	
	/**
	 * Creates a call between the specified times using the dummy customers.
	 * @param startTime The call start time.
	 * @param endTime The call end time.
	 * @return The call object.
	 */
	private Call setupCall(DateTime startTime, DateTime endTime) {
		CallStart start = new CallStart(dummyCallerNumber, dummyCalleeNumber, startTime);
		CallEnd end = new CallEnd(dummyCallerNumber, dummyCalleeNumber, endTime);
		return new Call(start, end);
	}
	
	/** Sets up dummy customer with specified tariff type and sets tariff library to return specified tariff
	 *  type for this customer.
	 * @param tariffType The tariff type to set for the dummy customer.
	 */
	private Customer setupCustomer(final Tariff tariffType) {
		final Customer customer = new Customer(dummyCustomerName, dummyCallerNumber, tariffType.toString());
		context.checking(new Expectations() {{  
			oneOf (mockTariffLibrary).tarriffFor(customer); will(returnValue(tariffType));
		}});
		
		return customer;
	}
}
