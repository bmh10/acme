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
	 */
	public CallCostCalculator(TariffLibrary tariffDatabase, DaytimePeakPeriod daytimePeakPeriod) {
		this.tariffDatabase = tariffDatabase;
		this.daytimePeakPeriod = daytimePeakPeriod;
	}
	
	/**
	 * Calculates the cost of a the specified call for the specified customer.
	 * @param customer The customer to calculate the call cost for (cost depends on which tariff they are on).
	 * @param call The call to calculate the cost of.
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
        
        while (!done) {
        	int currentDay = currentTime.getDayOfYear();
        	currentPeriod = daytimePeakPeriod.getPeriodOfDay(currentTime);
        	BigDecimal currentPeriodRate = daytimePeakPeriod.getPeriodRate(currentPeriod, tariff);
        	
        	if (firstPeriod) {
        		if (currentDay == endDay && currentPeriod == endPeriod) {
        			int time = end.getSecondOfDay() - start.getSecondOfDay();
            		BigDecimal periodCost = new BigDecimal(time).multiply(currentPeriodRate);
            		cost = cost.add(periodCost);
            		done = true;
        		}
        		else {
	        		int time = daytimePeakPeriod.getSecondInDayAtEndOfPeriod(currentPeriod) - start.getSecondOfDay();
	        		BigDecimal periodCost = new BigDecimal(time).multiply(currentPeriodRate);
	        		cost = cost.add(periodCost);
	        		currentTime = currentTime.plusSeconds(time);
	        		firstPeriod = false;
        		}
        	}
        	// TODO: could make this better - if not at end day then add cost for whole day (if at start of day)
        	else if (currentDay != endDay || (currentDay == endDay && currentPeriod != endPeriod))
        	{
        		// Add cost of whole period.
    			int periodDuration = daytimePeakPeriod.getPeriodDurationSeconds(currentPeriod);
    			BigDecimal periodCost = new BigDecimal(periodDuration).multiply(currentPeriodRate);
    			cost = cost.add(periodCost);
    			currentTime = currentTime.plusSeconds(periodDuration);
        	}
        	// Call ends in this period.
        	else {
        		int time = end.getSecondOfDay() - currentTime.getSecondOfDay();
        		BigDecimal periodCost = new BigDecimal(time).multiply(currentPeriodRate);
        		cost = cost.add(periodCost);
        		done = true;
        	}
        }
        
        cost = cost.setScale(0, RoundingMode.HALF_UP);
        return cost;
	}	
}