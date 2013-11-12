package tests;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.acmetelecom.Bill;
import com.acmetelecom.BillingSystem;
import com.acmetelecom.CallCostCalculator;
import com.acmetelecom.CallEventManager;
import com.acmetelecom.DaytimePeakPeriod;
import com.acmetelecom.FileLogger;
import com.acmetelecom.HtmlBillGenerator;
import com.acmetelecom.HtmlBillPrinter;
import com.acmetelecom.IBillingSystem;
import com.acmetelecom.IPeakPeriod.DayPeriod;
import com.acmetelecom.LineItem;
import com.acmetelecom.MoneyFormatter;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

/**
 * Acceptance tests which ensure that the new criteria for call charging are met.
 * Does not use interface mocking as the idea is to test the system under normal running conditions.
 */
public class AcceptanceTests {
	final int customersInDatabase = 100;
	final String dummyPhoneNumber = "44000000000";
	final String dummyCustomerName = "DummyName";
	
	private HashMap<Customer, Tariff> dummyCustomers;
	
	private DaytimePeakPeriod daytimePeakPeriod;
	private DummyClock clock;
	private CustomerDatabase customerDatabase;
	private TariffLibrary tariffDatabase;
	private Random rand;

	// Entry point to the system.
	IBillingSystem billingSystem;
	
	@Before
	public void Setup() {
		 rand = new Random();
		 
		// Create dummy customers to put in database.
		dummyCustomers = new HashMap<Customer, Tariff>();
		for (int i = 0; i < customersInDatabase; i++) {
			Tariff tariff = getRandomTariff();
			dummyCustomers.put(new Customer(dummyCustomerName + i, dummyPhoneNumber + i, tariff.toString()), tariff);
		}
		
		daytimePeakPeriod = new DaytimePeakPeriod();
		
		// Dependency injection into BillingSystem.
		customerDatabase = new DummyCustomerDatabase(dummyCustomers);
		tariffDatabase = new DummyTariffDatabase(dummyCustomers);

		CallEventManager callEventManager = new CallEventManager();
		CallCostCalculator callCostCalculator = new CallCostCalculator(tariffDatabase, new DaytimePeakPeriod());
		HtmlBillGenerator billGenerator = new HtmlBillGenerator(new HtmlBillPrinter());
		this.clock = new DummyClock();

		this.billingSystem = new BillingSystem(callEventManager, callCostCalculator, billGenerator, customerDatabase, clock);
		FileLogger.setActive(false);
	}
	
	/**
	 * Tests that call during peak time is charged at peak rate using correct tariff.
	 */
	@Test
	public void CallDuringPeakTimeChargedAtPeakRate() {
		Customer c1 = getRandomCustomer();
		Customer c2 = getRandomCustomer();

		int callDurationMins = 27;
		Tariff tariff = tariffDatabase.tarriffFor(c1);
		BigDecimal expectedCallCost = new BigDecimal(callDurationMins*60).multiply(tariff.peakRate());
		expectedCallCost = expectedCallCost.setScale(0, RoundingMode.HALF_UP);
		
		DateTime callStartTime = new DateTime(2013, 11, 4, daytimePeakPeriod.getPeakStart(), 54, 30);
		DateTime callEndTime = callStartTime.plusMinutes(callDurationMins);
		
		simulateSingleCallAndCheckBill(c1, c2, callStartTime, callEndTime, callDurationMins, expectedCallCost);
	}

	/**
	 * Tests that call during off-peak time is charged at off-peak rate using correct tariff.
	 */
	@Test
	public void CallDuringOffPeakTimeChargedAtOffPeakRate() {
		Customer c1 = getRandomCustomer();
		Customer c2 = getRandomCustomer();

		int callDurationMins = 31;
		Tariff tariff = tariffDatabase.tarriffFor(c1);
		BigDecimal expectedCallCost = new BigDecimal(callDurationMins*60).multiply(tariff.offPeakRate());
		expectedCallCost = expectedCallCost.setScale(0, RoundingMode.HALF_UP);
		
		DateTime callStartTime = new DateTime(2013, 11, 4, daytimePeakPeriod.getPeakEnd(), 54, 30);
		DateTime callEndTime = callStartTime.plusMinutes(callDurationMins);
		
		simulateSingleCallAndCheckBill(c1, c2, callStartTime, callEndTime, callDurationMins, expectedCallCost);
	}
	
	/**
	 * Tests that call which crosses over from off-peak to peak period is charged correctly.
	 */
	@Test
	public void CallCrossingOverFromOffPeakToPeakIsChargedAtPeakRate() {
		Customer c1 = getRandomCustomer();
		Customer c2 = getRandomCustomer();

		int offPeakDuration = 6;
		int peakDuration = 12;
		Tariff tariff = tariffDatabase.tarriffFor(c1);
		BigDecimal expectedCallCost = new BigDecimal(offPeakDuration*60).multiply(tariff.offPeakRate())
				.add(new BigDecimal(peakDuration*60).multiply(tariff.peakRate()));
		expectedCallCost = expectedCallCost.setScale(0, RoundingMode.HALF_UP);
		
		DateTime callStartTime = new DateTime(2013, 11, 4, daytimePeakPeriod.getPeakStart()-1, 60 - offPeakDuration);
		DateTime callEndTime = callStartTime.plusMinutes(offPeakDuration + peakDuration);
		
		simulateSingleCallAndCheckBill(c1, c2, callStartTime, callEndTime, offPeakDuration + peakDuration, expectedCallCost);
	}
	
	/**
	 * Tests that call which crosses over from peak to off-peak period is charged correctly.
	 */
	@Test
	public void CallCrossingOverFromPeakToOffPeakIsChargedAtPeakRate() {
		Customer c1 = getRandomCustomer();
		Customer c2 = getRandomCustomer();

		int peakDuration = 11;
		int offPeakDuration = 7;
		Tariff tariff = tariffDatabase.tarriffFor(c1);
		BigDecimal expectedCallCost = new BigDecimal(offPeakDuration*60).multiply(tariff.offPeakRate())
				.add(new BigDecimal(peakDuration*60).multiply(tariff.peakRate()));
		expectedCallCost = expectedCallCost.setScale(0, RoundingMode.HALF_UP);
		
		DateTime callStartTime = new DateTime(2013, 11, 4, daytimePeakPeriod.getPeakEnd() - 1, 60 - peakDuration);
		DateTime callEndTime = callStartTime.plusMinutes(peakDuration + offPeakDuration);
		
		simulateSingleCallAndCheckBill(c1, c2, callStartTime, callEndTime, peakDuration + offPeakDuration, expectedCallCost);
	}
	
	/**
	 * Tests that a long call which continues throughout multiple peak and off-peak periods is charged correctly.
	 */
	@Test
	public void LongCallCoveringMultiplePeakAndOffPeakPeriodsIsChargedCorrectly() {
		Customer c1 = getRandomCustomer();
		Customer c2 = getRandomCustomer();

		int callTimeInDays = 5;
		Tariff tariff = tariffDatabase.tarriffFor(c1);
		DateTime callStartTime = new DateTime(2013, 11, 4, daytimePeakPeriod.getPeakStart(), 0);
		DateTime callEndTime = callStartTime.plusDays(callTimeInDays);
		
		int preDuration = daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.PrePeak);
		int peakDuration = daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.Peak);
		int postDuration = daytimePeakPeriod.getPeriodDurationSeconds(DayPeriod.PostPeak);
		
		BigDecimal expectedCallCost = new BigDecimal((preDuration+postDuration)*callTimeInDays).multiply(tariff.offPeakRate())
				.add(new BigDecimal(peakDuration*callTimeInDays).multiply(tariff.peakRate()));
		expectedCallCost = expectedCallCost.setScale(0, RoundingMode.HALF_UP);
		
		simulateSingleCallAndCheckBill(c1, c2, callStartTime, callEndTime, callTimeInDays*24*60, expectedCallCost);
	}
	
	/**
	 * Tests that all calls are logged when the billing system receives a large number of calls.
	 */
	@Test
	public void TestAllCallsAreLoggedWhenSystemReceivesLargeNumberOfCalls() {
		
		// Run simulation for 300ms.
		long end = System.currentTimeMillis() + 300;
		int callsMade = 0;
		while (System.currentTimeMillis() < end) {
			Customer c1 = getRandomCustomer();
			Customer c2 = getRandomCustomer();
			
			// Call can be up to 2 days.
			int callDuration = rand.nextInt(48*60);
			DateTime callStartTime = getRandomDate();
			DateTime callEndTime = callStartTime.plusMinutes(callDuration);
			
			simulateCall(c1, c2, callStartTime, callEndTime);
			callsMade++;
		}
		
		ArrayList<Bill> bills = billingSystem.createCustomerBills();
		assertTrue(bills.size() == dummyCustomers.size());
		int callsLogged = 0;
		for (Bill bill : bills) {
			callsLogged += bill.getCalls().size();
		}

		assertTrue(callsMade == callsLogged);
	}
	
	/**
	 * Simulates a single call and checks the bill against the expected call cost and duration.
	 * @param c1 The caller.
	 * @param c2 The callee.
	 * @param callStartTime The call start time.
	 * @param callEndTime The call end time.
	 * @param callDurationMins The call duration in minutes.
	 * @param expectedCallCost The expected call cost.
	 */
	private void simulateSingleCallAndCheckBill(
			Customer c1,
			Customer c2,
			DateTime callStartTime,
			DateTime callEndTime,
			int callDurationMins,
			BigDecimal expectedCallCost) {
		
		simulateCall(c1, c2, callStartTime, callEndTime);
		
		ArrayList<Bill> bills = billingSystem.createCustomerBills();
		assertTrue(bills.size() == dummyCustomers.size());
		for (Bill bill : bills) {
			if (bill.getCustomer().getPhoneNumber().equals(c1.getPhoneNumber())) {
				List<LineItem> calls = bill.getCalls();
				String totalBill = bill.getTotalBill();
				
				assertTrue(calls.size() == 1);
				LineItem call = calls.get(0);
				assertTrue(call.callee() == c2.getPhoneNumber());
				assertTrue(call.durationMinutes().equals(String.valueOf(callDurationMins)+":00"));
				assertTrue(call.cost().equals(expectedCallCost));
				assert(totalBill.equals(MoneyFormatter.penceToPounds(expectedCallCost)));
				return;
			}
		}
		
		fail("No bill found for call.");
	}
	
	/**
	 * Simulates a call for specified customers and times.
	 * @param c1 The caller.
	 * @param c2 The callee.
	 * @param callStartTime The call start time.
	 * @param callEndTime The call end time.
	 */
	private void simulateCall(Customer c1, Customer c2, DateTime callStartTime, DateTime callEndTime) {
		clock.setTime(callStartTime);
		billingSystem.callInitiated(c1.getPhoneNumber(), c2.getPhoneNumber());
		clock.setTime(callEndTime);
		billingSystem.callCompleted(c1.getPhoneNumber(), c2.getPhoneNumber());
	}
	
	/**
	 * Gets a random customer from customer database.
	 * @return The Customer object.
	 */
	private Customer getRandomCustomer() {
		int idx = rand.nextInt(dummyCustomers.size());
		return (Customer)dummyCustomers.keySet().toArray()[idx];
	}
	
	/**
	 * Gets a random tariff.
	 * @return The Tariff object.
	 */
	private Tariff getRandomTariff() {
		double rand = Math.random();
		if (rand < 0.33) return Tariff.Standard;
		if (rand < 0.66) return Tariff.Leisure;
		return Tariff.Business;
	}
	
	private DateTime getRandomDate() {
	    return new DateTime(2013, 1, 1, 0, 0).plusSeconds(rand.nextInt(365*24*60*60));
	}
}