package com.acmetelecom;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Logger;

import org.joda.time.*;

import com.acmetelecom.DaytimePeakPeriod.DayPeriod;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;

/**
 * Contains logic for calculating call costs.
 */
public class CallCostCalculator implements ICallCostCalculator {

	private DaytimePeakPeriod daytimePeakPeriod;
	private TariffLibrary tariffDatabase;
	
	private Logger log = Logger.getLogger(CallCostCalculator.class.getSimpleName());
	
	/**
	 * Constructor.
	 * @param tariffDatabase The tariff database to use when looking up customer's tariffs.
	 * @param daytimePeakPeriod The DaytimePeakPeriod containing information about period timings.
	 * @exception IllegalArgumentException If any of arguments are null.
	 */
	public CallCostCalculator(TariffLibrary tariffDatabase, DaytimePeakPeriod daytimePeakPeriod) {
		AssertionHelper.NotNull(tariffDatabase, "tariffDatabase");
		AssertionHelper.NotNull(daytimePeakPeriod, "daytimePeakPeriod");
		this.tariffDatabase = tariffDatabase;
		this.daytimePeakPeriod = daytimePeakPeriod;
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
        DayPeriod endPeriod = daytimePeakPeriod.getPeriodOfDay(end);
        
        DateTime currentTime = start;
        DayPeriod currentPeriod;
        boolean firstPeriod = true;
        boolean done = false;
        
        // Goes through each period between start and end of call, summing cost progressively.
        while (!done) {
        	int currentDay = currentTime.getDayOfYear();
        	currentPeriod = daytimePeakPeriod.getPeriodOfDay(currentTime);
        	BigDecimal currentPeriodRate = daytimePeakPeriod.getPeriodRate(currentPeriod, tariff);
        	boolean callEndsThisPeriod = currentDay == endDay && currentPeriod == endPeriod;
        	
        	// First period (if call does not end in first period) -> add cost from start time until end of first period.
        	if (firstPeriod && !callEndsThisPeriod) {
        		int time = daytimePeakPeriod.getSecondInDayAtEndOfPeriod(currentPeriod) - start.getSecondOfDay();
        		BigDecimal periodCost = new BigDecimal(time).multiply(currentPeriodRate);
        		cost = cost.add(periodCost);
        		currentTime = currentTime.plusSeconds(time);
        		firstPeriod = false;
        	}
        	// Call ends in this period -> add cost from now until call end time.
        	else if (callEndsThisPeriod) {
        		int time = end.getSecondOfDay() - currentTime.getSecondOfDay();
        		BigDecimal periodCost = new BigDecimal(time).multiply(currentPeriodRate);
        		cost = cost.add(periodCost);
        		done = true;
        	}
        	// TODO: could make this more efficient -> if not at endDay and at PrePeak period then add cost for whole day.
        	// Call continues throughout this period -> add cost of whole period.
        	else {
    			int periodDuration = daytimePeakPeriod.getPeriodDurationSeconds(currentPeriod);
    			BigDecimal periodCost = new BigDecimal(periodDuration).multiply(currentPeriodRate);
    			cost = cost.add(periodCost);
    			currentTime = currentTime.plusSeconds(periodDuration);
        	}
        }
        
        cost = cost.setScale(0, RoundingMode.HALF_UP);
        return cost;
	}	
}