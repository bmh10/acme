package com.acmetelecom;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.joda.time.*;

import com.acmetelecom.customer.CentralTariffDatabase;
import com.acmetelecom.customer.Customer;
import com.acmetelecom.customer.Tariff;
import com.acmetelecom.customer.TariffLibrary;
import com.acmetelecom.DaytimePeakPeriod.DayPeriod;

public class CallCostCalculator implements ICallCostCalculator {

	private DaytimePeakPeriod daytimePeakPeriod;
	
	private TariffLibrary tariffDatabase;
	
	public CallCostCalculator(TariffLibrary tariffDatabase) {
		daytimePeakPeriod = new DaytimePeakPeriod();
		this.tariffDatabase = tariffDatabase;
	}
	
	public BigDecimal calculateCallCost(Customer customer, Call call) {
		
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
            		cost.add(periodCost);
            		done = true;
        		}
        		else {
	        		int time = daytimePeakPeriod.getSecondInDayAtEndOfPeriod(currentPeriod) - start.getSecondOfDay();
	        		BigDecimal periodCost = new BigDecimal(time).multiply(currentPeriodRate);
	        		cost.add(periodCost);
	        		currentTime.plusSeconds(time);
	        		firstPeriod = false;
        		}
        	}
        	// TODO: could make this better - if not at end day then add cost for whole day (if at start of day)
        	else if (currentDay != endDay || (currentDay == endDay && currentPeriod != endPeriod))
        	{
        		// Add cost of whole period.
    			int periodDuration = daytimePeakPeriod.getPeriodDurationSeconds(currentPeriod);
    			BigDecimal periodCost = new BigDecimal(periodDuration).multiply(currentPeriodRate);
    			cost.add(periodCost);
    			currentTime.plusSeconds(periodDuration);
        	}
        	// Call ends in this period.
        	else {
        		int time = end.getSecondOfDay() - currentTime.getSecondOfDay();
        		BigDecimal periodCost = new BigDecimal(time).multiply(currentPeriodRate);
        		cost.add(periodCost);
        		done = true;
        	}
        }
        
        cost = cost.setScale(0, RoundingMode.HALF_UP);
        return cost;
	}	
}