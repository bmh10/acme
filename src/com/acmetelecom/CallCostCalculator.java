package com.acmetelecom;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.joda.time.*;

import com.acmetelecom.IPeakPeriod.DayPeriod;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

/**
 * Contains logic for calculating call costs according to new regulations.
 */
public class CallCostCalculator implements ICallCostCalculator {

	private IPeakPeriod peakPeriod;
	private TariffLibrary tariffDatabase;
	
	/**
	 * Constructor.
	 * @param tariffDatabase The tariff database to use when looking up customer's tariffs.
	 * @param daytimePeakPeriod The DaytimePeakPeriod containing information about period timings.
	 * @exception IllegalArgumentException If any of arguments are null.
	 */
	public CallCostCalculator(TariffLibrary tariffDatabase, IPeakPeriod peakPeriod) {
		AssertionHelper.NotNull(tariffDatabase, "tariffDatabase");
		AssertionHelper.NotNull(peakPeriod, "peakPeriod");
		this.tariffDatabase = tariffDatabase;
		this.peakPeriod = peakPeriod;
	}
	
	/**
	 * Calculates the cost of a the specified call for the specified customer.
	 * @param customer The customer to calculate the call cost for (cost depends on which tariff they are on).
	 * @param call The call to calculate the cost of.
	 * @exception IllegalArgumentException If any of arguments are null.
	 */
	public BigDecimal calculateCallCost(Customer customer, Call call) {
		AssertionHelper.NotNull(customer, "customer");
		AssertionHelper.NotNull(call, "call");
		
		Tariff tariff = tariffDatabase.tarriffFor(customer);
        BigDecimal cost = new BigDecimal(0.0);
        
        DateTime start = call.startTime();
        DateTime end   = call.endTime();
        
        assert(start.isBefore(end.getMillis()));
        
        // Assumes calls don't last more than a year.
        int endDay = end.getDayOfYear();
        DayPeriod endPeriod = peakPeriod.getPeriodOfDay(end);
        
        DateTime currentTime = start;
        DayPeriod currentPeriod;
        boolean done = false;
        
        // Goes through each period between start and end of call, summing cost progressively.
        while (!done) {
        	int currentDay = currentTime.getDayOfYear();
        	currentPeriod = peakPeriod.getPeriodOfDay(currentTime);
        	BigDecimal currentPeriodRate = peakPeriod.getPeriodRate(currentPeriod, tariff);
        	boolean callEndsThisPeriod = currentDay == endDay && currentPeriod == endPeriod;
        	
        	// Call ends in this period -> add cost from now until call end time.
        	if (callEndsThisPeriod) {
        		int time = end.getSecondOfDay() - currentTime.getSecondOfDay();
        		BigDecimal periodCost = new BigDecimal(time).multiply(currentPeriodRate);
        		cost = cost.add(periodCost);
        		done = true;
        	}
        	// Call continues to end of this period -> add cost from current time until end of period.
        	else {
        		int time = peakPeriod.getSecondInDayAtEndOfPeriod(currentPeriod) - currentTime.getSecondOfDay();
        		BigDecimal periodCost = new BigDecimal(time).multiply(currentPeriodRate);
        		cost = cost.add(periodCost);
        		currentTime = currentTime.plusSeconds(time);
        	}
        }
        
        cost = cost.setScale(0, RoundingMode.HALF_UP);
        return cost;
	}
}