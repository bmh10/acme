package com.acmetelecom;

import java.math.BigDecimal;

import com.acmetelecom.customer.Customer;

/**
 * The CallCostCalculator interface which all call cost calculator implementations should implement.
 */
public interface ICallCostCalculator {
	
	/**
	 * Calculates the cost of a the specified call for the specified customer.
	 * @param customer The customer to calculate the call cost for (cost depends on which tariff they are on).
	 * @param call The call to calculate the cost of.
	 */
	BigDecimal calculateCallCost(Customer customer, Call call);
}
