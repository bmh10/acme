package tests;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import com.acmetelecom.Bill;
import com.acmetelecom.BillingSystem;
import com.acmetelecom.CallCostCalculator;
import com.acmetelecom.CallEvent;
import com.acmetelecom.DaytimePeakPeriod;
import com.acmetelecom.HtmlBillGenerator;
import com.acmetelecom.HtmlBillPrinter;
import com.acmetelecom.IBillingSystem;
import com.acmetelecom.LineItem;
import com.acmetelecom.customer.CentralCustomerDatabase;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.CustomerDatabase;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

/**
 * Acceptance tests which ensure that the new criteria for call charging are met.
 * Does not use interface mocking as the idea is to test the system under normal running conditions.
 * @author Ben
 */
public class AcceptanceTests {
	
//	Name: John Smith No: 447722113434 Plan: Business
//	Name: Jane Dixon No: 447799555444 Plan: Leisure
//	Name: Mike Davies No: 447799888888 Plan: Business
//	Name: Anne Jones No: 447777765432 Plan: Standard
//	Name: Sarah Thomas No: 447721232123 Plan: Standard
//	Name: Tina Black No: 447795195195 Plan: Leisure
//	Name: John Watkins No: 447132435245 Plan: Business
//	Name: Giorgia Davidson No: 447132435245 Plan: Standard
//	Name: Jonathan Carr No: 447223432532 Plan: Standard
	
	private final String PhoneNumber = "44000000000";
	
	private HashMap<Customer, Tariff> dummyCustomers;

	IBillingSystem billingSystem;
	
	@Before
	public void Setup() {
		// Create some dummy customers
		dummyCustomers = new HashMap<Customer, Tariff>();
		for (int i = 0; i < 5; i++) {
			dummyCustomers.put(new Customer("DummyCustomer" + i, PhoneNumber + i, "Standard"), Tariff.Standard);
		}
		
		// Dependency injection into BillingSystem.
		CustomerDatabase customerDatabase = new DummyCustomerDatabase(dummyCustomers);
		TariffLibrary tariffDatabase = new DummyTariffDatabase(dummyCustomers);
		
		HtmlBillGenerator billGenerator = new HtmlBillGenerator(new HtmlBillPrinter());
		CallCostCalculator callCostCalculator = new CallCostCalculator(tariffDatabase, new DaytimePeakPeriod());

		this.billingSystem = new BillingSystem(callCostCalculator, billGenerator, customerDatabase);
	}
	
	@Test
	public void CallDuringPeakTimeChargedAtPeakRate() {
		String caller = PhoneNumber + "0";
		String callee = PhoneNumber + "1";
		int callDurationMins = 27;
		Tariff tariff = Tariff.Standard;
		BigDecimal expectedCallCost = new BigDecimal(callDurationMins*60).multiply(tariff.peakRate());
		
		DateTime callStartTime = new DateTime(2013, 11, 4, 7, 54, 30);
		DateTime callEndTime = callStartTime.plusMinutes(callDurationMins);
		//billingSystem.callInitiatedAtTime(caller, callee, callStartTime);
		//billingSystem.callCompletedAtTime(caller, callee, callEndTime);
		
		ArrayList<Bill> bills = billingSystem.createCustomerBills();
		assertTrue(bills.size() == dummyCustomers.size());
		for (Bill bill : bills) {
			if (bill.getCustomer().getPhoneNumber().equals(caller)) {
				List<LineItem> calls = bill.GetCalls();
				String totalBill = bill.GetTotalBill();
				
				// Basic checks.
				assertTrue(calls.size() == 1);
				LineItem call = calls.get(0);
				assertTrue(call.callee() == callee);
				System.out.println(call.cost());
				assertTrue(call.durationMinutes().equals(String.valueOf(callDurationMins)+":00"));
				assertTrue(call.cost().equals(expectedCallCost));
				//assert(totalBill == expectedCallCost);
				
				
				return;
			}
		}
		
		fail("No bill found for call.");
	}
	
	@Test
	public void CallDuringOffPeakTimeChargedAtOffPeakRate() {
		fail("Not yet implemented");
	}
	
	@Test
	public void CallDuringOffPeakOverlappingIntoPeakChargedAppropriately() {
		fail("Not yet implemented");
	}
	
	@Test
	public void CallDuringPeakOverlappingIntoOffPeakChargedAppropriately() {
		fail("Not yet implemented");
	}
	
}